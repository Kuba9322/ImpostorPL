package pl.kuce.impostorpl.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp)
)

@Composable
fun ImpostorPLTheme(
    palette: AppPalette = Palettes.ViolDark,             // choose palette here
    darkTheme: Boolean = true,      // force true for dark bg
    content: @Composable () -> Unit
) {
    val scheme = palette.toColorScheme(darkTheme)

    MaterialTheme(
        colorScheme = scheme,
        typography = AppTypography,
        shapes = AppShapes
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}
