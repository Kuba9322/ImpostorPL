package pl.kuce.impostorpl.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

data class AppPalette(
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val tertiary: Color = Color(0xFF00C2FF),
    val onTertiary: Color = Color(0xFF001318),
    val error: Color = Color(0xFFBA1A1A),
    val onError: Color = Color.White
) {
    fun toColorScheme(dark: Boolean): ColorScheme {
        return if (dark) {
            darkColorScheme(
                primary = primary,
                onPrimary = onPrimary,
                secondary = secondary,
                onSecondary = onSecondary,
                background = background,
                onBackground = onBackground,
                surface = surface,
                onSurface = onSurface,
                tertiary = tertiary,
                onTertiary = onTertiary,
                error = error,
                onError = onError
            )
        } else {
            lightColorScheme(
                primary = primary,
                onPrimary = onPrimary,
                secondary = secondary,
                onSecondary = onSecondary,
                background = background,
                onBackground = onBackground,
                surface = surface,
                onSurface = onSurface,
                tertiary = tertiary,
                onTertiary = onTertiary,
                error = error,
                onError = onError
            )
        }
    }
}

object Palettes {
    // Futuristic / gaming (ciemne tło + neon)
    val Neon = AppPalette(
        primary = Color(0xFF7C4DFF),
        onPrimary = Color.White,
        secondary = Color(0xFF5E35B1),
        onSecondary = Color.White,
        background = Color(0xFF0E0E12),
        onBackground = Color(0xFFEAEAF0),
        surface = Color(0xFF15151B),
        onSurface = Color(0xFFEAEAF0),
        tertiary = Color(0xFF00E5FF),
        onTertiary = Color(0xFF001217)
    )

    // Chłodny niebieski
    val Blue = AppPalette(
        primary = Color(0xFF2962FF),
        onPrimary = Color.White,
        secondary = Color(0xFF455A64),
        onSecondary = Color.White,
        background = Color(0xFFF3F6FB),
        onBackground = Color(0xFF101317),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF101317),
        tertiary = Color(0xFF00B0FF),
        onTertiary = Color(0xFF001018)
    )

    // Retro/Arcade
    val Retro = AppPalette(
        primary = Color(0xFFFF3D00),
        onPrimary = Color.White,
        secondary = Color(0xFFFFC400),
        onSecondary = Color(0xFF1B1300),
        background = Color(0xFF121212),
        onBackground = Color(0xFFFFF3E0),
        surface = Color(0xFF1A1A1A),
        onSurface = Color(0xFFFFF3E0),
        tertiary = Color(0xFF00E676),
        onTertiary = Color(0xFF00150D)
    )

    val BlueDark = AppPalette(
        primary = Color(0xFF2962FF),
        onPrimary = Color.White,
        secondary = Color(0xFF455A64),
        onSecondary = Color.White,
        background = Color(0xFF0E0E12),   // DARK background
        onBackground = Color(0xFFECEFF1),
        surface = Color(0xFF15151B),      // DARK surface
        onSurface = Color(0xFFECEFF1),
        tertiary = Color(0xFF00B0FF),
        onTertiary = Color(0xFF001018)
    )

    val ViolDark = AppPalette(
        primary = Color(0xFF6B36CC),
        onPrimary = Color.White,
        secondary = Color(0x70673AB7),
        onSecondary = Color.White,
        background = Color(0xFF0E0E12),   // DARK background
        onBackground = Color(0xFFECEFF1),
        surface = Color(0xFF15151B),      // DARK surface
        onSurface = Color(0xFFECEFF1),
        tertiary = Color(0xFFE2FF3B),
        onTertiary = Color(0xFF001018)
    )
}
