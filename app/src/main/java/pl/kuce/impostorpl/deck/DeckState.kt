package pl.kuce.impostorpl.deck

import kotlinx.serialization.Serializable

/**
 * Canonical "deck" representation:
 * - deck: full ordered list of word IDs (used + remaining)
 * - pointer: index of the next word to consume (0..deck.size)
 *
 * Invariants:
 * - 0 <= pointer <= deck.size
 * - deck elements are unique
 * - words at indices [0, pointer) are already used
 * - words at indices [pointer, deck.lastIndex] are remaining
 */
@Serializable
data class DeckState(
    val deck: List<String>,
    val pointer: Int
)
