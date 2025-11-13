package pl.kuce.impostorpl.storage

import pl.kuce.impostorpl.deck.DeckState

/**
 * Simple in-memory DeckStore.
 * - Stores a single DeckState instance in memory.
 * - Not persisted across app restarts.
 * - Methods are synchronized to ensure atomic visibility.
 */
class InMemoryDeckStore(
    private var state: DeckState? = null
) : DeckStore {

    @Synchronized
    override fun load(): DeckState? {
        return state
    }

    @Synchronized
    override fun save(state: DeckState) {
        // Store a defensive copy to avoid accidental external mutations.
        this.state = DeckState(
            deck = state.deck.toList(),
            pointer = state.pointer
        )
    }
}
