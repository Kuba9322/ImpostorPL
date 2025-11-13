package pl.kuce.impostorpl.storage

import pl.kuce.impostorpl.deck.DeckState

/**
 * Persistent store for the canonical deck state (deck + pointer).
 * Implementations must ensure atomicity of writes (transaction or fsync+swap).
 */
interface DeckStore {
    /** Loads the last saved deck state, or null if none exists. */
    fun load(): DeckState?

    /**
     * Saves the given deck state atomically.
     * Callers should prefer small, frequent writes (pointer advance) and batch structural updates.
     */
    fun save(state: DeckState)
}
