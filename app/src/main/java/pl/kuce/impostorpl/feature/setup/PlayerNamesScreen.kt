package pl.kuce.impostorpl.feature.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.kuce.impostorpl.ui.components.PrimaryButton

@Composable
fun PlayerNamesScreen(
    playersCount: Int,
    names: List<String>,
    onNameChange: (Int, String) -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Imiona graczy:", style = MaterialTheme.typography.titleMedium)

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            names.take(playersCount).forEachIndexed { index, name ->
                androidx.compose.material3.TextField(
                    value = name,
                    onValueChange = { onNameChange(index, it) },
                    label = { Text("Gracz ${index + 1}") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }

        PrimaryButton(text = "Rozpocznij", onClick = onStart)
    }
}
