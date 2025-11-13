package pl.kuce.impostorpl.model

/**
 * Immutable word entry with a stable ID.
 * - id: stable identifier (UUID or namespaced hash)
 * - text: user-visible word content
 */
data class Word(
    val id: String,
    val text: String
)