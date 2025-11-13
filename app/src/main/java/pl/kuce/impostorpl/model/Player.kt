package pl.kuce.impostorpl.model

data class Player(
    val name: String,
    val points: Int = 0,
    val isImpostor: Boolean = false
)