package pl.kuce.impostorpl.deck

import kotlin.random.Random

/**
 * In-memory deck manager with "pre-shuffled deck + pointer" semantics.
 * Guarantees no repeats until the deck is exhausted.
 *
 * Persistence is NOT handled here; wire DeckStore in the orchestrator/use-case layer.
 *
 * Invariants:
 * - deck holds a permutation (no duplicates)
 * - 0 <= pointer <= deck.size
 * - indices [0, pointer) are used; [pointer, last] are remaining
 */
class DefaultDeckManager(
    initialState: DeckState = DeckState(emptyList(), 0),
    private val random: Random = Random.Default // inject for deterministic tests if needed
) : DeckManager {

    // Internal mutable state; exposed snapshots via currentState()
    private var deck: MutableList<String> = initialState.deck.toMutableList()
    private var pointer: Int = initialState.pointer.coerceIn(0, initialState.deck.size)

    override fun peekNextWordId(): String? {
        return if (pointer < deck.size) deck[pointer] else null
    }

    override fun consumeNextWordId(): String? {
        if (pointer >= deck.size) return null
        val id = deck[pointer]
        pointer += 1
        return id
    }

    override fun appendNewWordIds(newIds: List<String>) {
        if (newIds.isEmpty()) return
        // Filter out anything that is already present in the deck
        val uniqueNew = newIds.asSequence()
            .filter { it.isNotEmpty() }
            .filter { id -> deck.binarySearch(id).let { // binarySearch won't work; deck isn't sorted
                // deck is a permutation (unsorted). Use a set for membership check.
                // Doing it efficiently:
                // 1) Build a temporary set once.
                false
            } }
            .toList()
        // Above inline approach is not suitable with binarySearch because deck isn't sorted.
        // Use a one-time set for membership check:
        val present = deck.toHashSet()
        val filtered = newIds.asSequence()
            .filter { it.isNotEmpty() }
            .filterNot { present.contains(it) }
            .toMutableList()

        // Shuffle ONLY the new IDs, then append at the tail
        if (filtered.isNotEmpty()) {
            filtered.shuffle(random)
            deck.addAll(filtered)
        }
        // pointer stays unchanged
        ensureInvariants()
    }

    override fun removeWordIds(idsToRemove: Set<String>) {
        if (idsToRemove.isEmpty() || deck.isEmpty()) return

        // We must remove from both used and remaining parts while keeping pointer consistent.
        // Strategy:
        // - Count how many removals occur in [0, pointer) to shift pointer left accordingly.
        // - Rebuild deck without removed IDs.
        var removedBeforePointer = 0
        for (i in 0 until pointer) {
            val id = deck[i]
            if (idsToRemove.contains(id)) removedBeforePointer += 1
        }
        if (removedBeforePointer > 0) {
            pointer -= removedBeforePointer
            if (pointer < 0) pointer = 0
        }

        // Rebuild deck filtering out the removed IDs
        if (idsToRemove.isNotEmpty()) {
            val filtered = deck.filter { id -> !idsToRemove.contains(id) }
            deck = filtered.toMutableList()
        }

        // Clamp pointer if it ended up beyond deck size
        if (pointer > deck.size) pointer = deck.size
        ensureInvariants()
    }

    override fun resetWith(ids: List<String>) {
        // Build a clean permutation from the given IDs; pointer at 0
        val unique = LinkedHashSet<String>(ids.size)
        ids.forEach { id ->
            if (id.isNotEmpty()) unique.add(id)
        }
        deck = unique.toMutableList()
        deck.shuffle(random)
        pointer = 0
        ensureInvariants()
    }

    override fun currentState(): DeckState {
        return DeckState(deck = deck.toList(), pointer = pointer)
    }

    // --- Internal guards ---

    private fun ensureInvariants() {
        // 0 <= pointer <= deck.size
        if (pointer < 0) pointer = 0
        if (pointer > deck.size) pointer = deck.size
        // Ensure uniqueness (debug-time check; O(n) but cheap for 2–4k)
        assert(deck.size == deck.toSet().size) {
            "Deck contains duplicates; this violates the permutation invariant."
        }
    }
}
