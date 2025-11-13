package pl.kuce.impostorpl.deck

/**
 * DeckManager defines high-level operations over the canonical deck.
 * This is an interface; provide a production implementation later.
 *
 * Responsibilities:
 * - expose the next word ID (peek)
 * - consume the next word ID (advance pointer)
 * - append newly added word IDs (append-shuffle strategy)
 * - remove word IDs that are no longer valid (e.g., deleted words)
 * - rebuild the deck from scratch when needed
 */
interface DeckManager {
    /** Returns next word ID without advancing the pointer, or null if exhausted. */
    fun peekNextWordId(): String?

    /** Advances the pointer and returns consumed word ID, or null if exhausted. */
    fun consumeNextWordId(): String?

    /** Appends new word IDs to the remaining tail (shuffle order decided by implementation). */
    fun appendNewWordIds(newIds: List<String>)

    /** Removes given IDs from the deck (both used and remaining). */
    fun removeWordIds(idsToRemove: Set<String>)

    /** Rebuilds the deck from provided IDs (e.g., after full reset). Pointer is typically set to 0. */
    fun resetWith(ids: List<String>)

    /** Current deck snapshot (for diagnostics/telemetry). */
    fun currentState(): DeckState
}
