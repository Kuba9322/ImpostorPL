package pl.kuce.impostorpl.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import pl.kuce.impostorpl.R

// Single-family setup: everything uses Exo 2
private val Exo2Family = FontFamily(
    Font(R.font.exo2regular, weight = FontWeight.Normal),
    Font(R.font.exo3semibold, weight = FontWeight.SemiBold),
    // If you add exo2_bold.ttf later, also include:
    // Font(R.font.exo2_bold, weight = FontWeight.Bold),
)

// Keep the old names so the rest of the app doesn't need changes
val DisplayFontFamily = Exo2Family
val BodyFontFamily = Exo2Family
