package pl.kuce.impostorpl.feature.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.kuce.impostorpl.ui.components.PrimaryButton
import androidx.compose.ui.Alignment as UiAlignment

private const val MIN_PLAYERS = 3
private const val MAX_PLAYERS = 9
private const val MIN_IMPOSTOR_WIN = 1
private const val MAX_IMPOSTOR_WIN = 5
private const val MIN_PLAYERS_WIN = 0
private const val MAX_PLAYERS_WIN = 4
private const val MIN_IMPOSTORS = 1
private const val MAX_IMPOSTORS = 2

@Composable
fun GameSettingsScreen(
    players: Int,
    onPlayersChange: (Int) -> Unit,
    impostorWinPoints: Int,
    onImpostorWinPointsChange: (Int) -> Unit,
    playersWinPoints: Int,
    onPlayersWinPointsChange: (Int) -> Unit,
    impostorsCount: Int,
    onImpostorsCountChange: (Int) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding() // safe area under notch/camera
            .verticalScroll(scroll)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ustawienia gry",
            style = MaterialTheme.typography.headlineMedium
        )

        SettingStepper(
            title = "Liczba graczy",
            value = players,
            min = MIN_PLAYERS,
            max = MAX_PLAYERS,
            onDec = { onPlayersChange((players - 1).coerceAtLeast(MIN_PLAYERS)) },
            onInc = { onPlayersChange((players + 1).coerceAtMost(MAX_PLAYERS)) },
            minMaxLabel = "Min: $MIN_PLAYERS   Max: $MAX_PLAYERS",
            decContentDesc = "Zmniejsz liczbę graczy",
            incContentDesc = "Zwiększ liczbę graczy",
            fullWidth = true
        )

        SettingStepper(
            title = "Punkty dla impostora (wygrana)",
            value = impostorWinPoints,
            min = MIN_IMPOSTOR_WIN,
            max = MAX_IMPOSTOR_WIN,
            onDec = { onImpostorWinPointsChange((impostorWinPoints - 1).coerceAtLeast(MIN_IMPOSTOR_WIN)) },
            onInc = { onImpostorWinPointsChange((impostorWinPoints + 1).coerceAtMost(MAX_IMPOSTOR_WIN)) },
            minMaxLabel = "Min: $MIN_IMPOSTOR_WIN   Max: $MAX_IMPOSTOR_WIN",
            decContentDesc = "Zmniejsz punkty impostora",
            incContentDesc = "Zwiększ punkty impostora",
            fullWidth = true
        )

        SettingStepper(
            title = "Punkty dla graczy (wygrana)",
            value = playersWinPoints,
            min = MIN_PLAYERS_WIN,
            max = MAX_PLAYERS_WIN,
            onDec = { onPlayersWinPointsChange((playersWinPoints - 1).coerceAtLeast(MIN_PLAYERS_WIN)) },
            onInc = { onPlayersWinPointsChange((playersWinPoints + 1).coerceAtMost(MAX_PLAYERS_WIN)) },
            minMaxLabel = "Min: $MIN_PLAYERS_WIN   Max: $MAX_PLAYERS_WIN",
            decContentDesc = "Zmniejsz punkty graczy",
            incContentDesc = "Zwiększ punkty graczy",
            fullWidth = true
        )

        SettingStepper(
            title = "Liczba impostorów",
            value = impostorsCount,
            min = MIN_IMPOSTORS,
            max = MAX_IMPOSTORS,
            onDec = { onImpostorsCountChange((impostorsCount - 1).coerceAtLeast(MIN_IMPOSTORS)) },
            onInc = { onImpostorsCountChange((impostorsCount + 1).coerceAtMost(MAX_IMPOSTORS)) },
            minMaxLabel = "Min: $MIN_IMPOSTORS   Max: $MAX_IMPOSTORS",
            decContentDesc = "Zmniejsz liczbę impostorów",
            incContentDesc = "Zwiększ liczbę impostorów",
            fullWidth = true
        )

        PrimaryButton(
            text = "Dalej",
            onClick = onNext
        )
    }
}

@Composable
private fun SettingStepper(
    title: String,
    value: Int,
    min: Int,
    max: Int,
    onDec: () -> Unit,
    onInc: () -> Unit,
    minMaxLabel: String,
    decContentDesc: String,
    incContentDesc: String,
    fullWidth: Boolean = false
) {
    val canDec = value > min
    val canInc = value < max

    Column(
        modifier = if (fullWidth) Modifier.fillMaxWidth() else Modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onDec,
                enabled = canDec,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp) // taller buttons
                    .semantics { contentDescription = decContentDesc }
            ) {
                Text(text = "–", fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }

            Box(
                modifier = Modifier
                    .weight(1f)         // center column gets equal space as buttons
                    .height(64.dp),
                contentAlignment = UiAlignment.Center
            ) {
                Text(
                    text = "$value",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = onInc,
                enabled = canInc,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp) // taller buttons
                    .semantics { contentDescription = incContentDesc }
            ) {
                Text(text = "+", fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }

        Text(
            text = minMaxLabel,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
