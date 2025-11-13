package pl.kuce.impostorpl.feature.turn

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.kuce.impostorpl.ui.components.PrimaryButton

@Composable
fun WordRevealScreen(
    playerNumber: Int,
    isImpostor: Boolean,
    word: String,
    onMemorized: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isImpostor) "! KRĘTACZ !" else word,
            color = if (isImpostor) Color.Red else Color.Unspecified,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Nie pokazuj ekranu innym. Zapamiętaj i naciśnij przycisk.",
            style = MaterialTheme.typography.bodyMedium
        )
        PrimaryButton(
            text = "Zapamiętałem słowo",
            onClick = onMemorized
        )
    }
}
