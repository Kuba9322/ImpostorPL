package pl.kuce.impostorpl.orchestrator

import pl.kuce.impostorpl.model.Word

/**
 * High-level use case boundary for the UI:
 * - provides the next word (text), guaranteeing no repeats until the deck is exhausted
 * - encapsulates deck operations + word lookup
 *
 * Implementation will coordinate WordStore + DeckManager + DeckStore.
 */
interface NextWordProvider {
    /**
     * Returns the next word (user-visible), advancing the pointer.
     * Returns null if the deck is exhausted.
     */
    fun consumeNextWord(): Word?

    /**
     * Returns the next word without advancing (for display previews, if needed).
     * Returns null if the deck is exhausted.
     */
    fun peekNextWord(): Word?

    /**
     * To be called when new words are available:
     * - Detect new IDs vs existing
     * - Append-shuffle only the new IDs into the remaining tail
     * - Persist the updated deck
     */
    fun onNewWordsAvailable(newWords: List<Word>)

    /**
     * To be called when some words are removed:
     * - Remove their IDs from the remaining tail
     * - Persist the updated deck
     * (Historical usage remains intact)
     */
    fun onWordsRemoved(idsToRemove: Set<String>)
}
