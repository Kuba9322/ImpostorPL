package pl.kuce.impostorpl.orchestrator

import kotlin.random.Random
import pl.kuce.impostorpl.deck.DeckState
import pl.kuce.impostorpl.deck.DefaultDeckManager
import pl.kuce.impostorpl.deck.DeckManager
import pl.kuce.impostorpl.model.Word
import pl.kuce.impostorpl.storage.DeckStore
import pl.kuce.impostorpl.storage.WordStore

/**
 * Default orchestrator that wires:
 * - WordStore (ID -> text),
 * - DeckStore (persistent deck+pointer),
 * - DeckManager (in-memory permutation + pointer).
 *
 * Guarantees:
 * - No repeats until the deck is exhausted (pre-shuffled deck + pointer).
 * - New words are appended to the remaining tail (append-shuffle).
 * - Removed words are pruned from the deck; pointer is adjusted.
 *
 * Persistence:
 * - Deck state is saved after each structural change and after each consume.
 * - Word map is updated via WordStore.
 *
 * Notes:
 * - This class initializes its own DeckManager instance based on DeckStore state.
 * - If no deck is present yet, it builds a fresh permutation from all known words.
 */
class DefaultNextWordProvider(
    private val wordStore: WordStore,
    private val deckStore: DeckStore,
    private val random: Random = Random.Default
) : NextWordProvider {

    private var deckManager: DeckManager

    init {
        // Try to restore existing deck; otherwise build a new one from all known words.
        val saved: DeckState? = deckStore.load()
        deckManager = if (saved != null) {
            DefaultDeckManager(initialState = saved, random = random)
        } else {
            val allIds = wordStore.getAllWords().keys.toList()
            val mgr = DefaultDeckManager(initialState = DeckState(emptyList(), 0), random = random)
            mgr.resetWith(allIds)
            deckStore.save(mgr.currentState())
            mgr
        }

// 1) Remove IDs no longer present in WordStore (prune)
        val wordIds = wordStore.getAllWords().keys.toSet()
        val currentDeck = deckManager.currentState().deck
        val toRemove = currentDeck.asSequence().filter { it !in wordIds }.toSet()
        if (toRemove.isNotEmpty()) {
            deckManager.removeWordIds(toRemove)
            deckStore.save(deckManager.currentState())
        }

// 2) Append-shuffle newly discovered IDs (additions in raw/words)
        val present = deckManager.currentState().deck.toHashSet()
        val newIds = wordIds.asSequence().filter { it !in present }.toList()
        if (newIds.isNotEmpty()) {
            deckManager.appendNewWordIds(newIds) // shuffles only the new tail
            deckStore.save(deckManager.currentState())
        }

    }

    override fun consumeNextWord(): Word? {
        // Peek to handle the exhausted case without advancing.
        val nextId = deckManager.peekNextWordId() ?: return null

        // Advance pointer and persist the new state atomically (callers should ensure transactional save in DeckStore).
        val consumedId = deckManager.consumeNextWordId() ?: return null
        deckStore.save(deckManager.currentState())

        // Resolve text from WordStore (ID is stable; text may have been updated).
        val word = wordStore.getAllWords()[consumedId]
        return word
    }

    override fun peekNextWord(): Word? {
        val id = deckManager.peekNextWordId() ?: return null
        return wordStore.getAllWords()[id]
    }

    override fun onNewWordsAvailable(newWords: List<Word>) {
        if (newWords.isEmpty()) return

        // 1) Upsert into WordStore (IDs are stable; new IDs get added, existing IDs update text)
        wordStore.upsert(newWords)

        // 2) Append-shuffle only truly new IDs (not present anywhere in the current deck)
        val present = deckManager.currentState().deck.toHashSet()
        val candidates = newWords.asSequence()
            .map { it.id }
            .filter { it.isNotEmpty() }
            .filter { it !in present }
            .toList()

        if (candidates.isNotEmpty()) {
            // Shuffle only the new tail and append
            // (Shuffling is delegated to DeckManager implementation.)
            deckManager.appendNewWordIds(candidates)
            deckStore.save(deckManager.currentState())
        }
    }

    override fun onWordsRemoved(idsToRemove: Set<String>) {
        if (idsToRemove.isEmpty()) return

        // 1) Remove from WordStore (historical usage can remain implicit by pointer region)
        wordStore.removeById(idsToRemove)

        // 2) Prune IDs from deck (both used and remaining); pointer auto-adjusts inside DeckManager
        deckManager.removeWordIds(idsToRemove)
        deckStore.save(deckManager.currentState())
    }
}
