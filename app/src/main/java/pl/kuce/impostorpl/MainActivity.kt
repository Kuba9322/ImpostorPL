package pl.kuce.impostorpl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import pl.kuce.impostorpl.feature.setup.BottomBar
import pl.kuce.impostorpl.feature.setup.GameSettingsScreen
import pl.kuce.impostorpl.feature.setup.PlayerNamesScreen
import pl.kuce.impostorpl.feature.turn.PassPhoneScreen
import pl.kuce.impostorpl.loader.ResourceWordLoader
import pl.kuce.impostorpl.orchestrator.DefaultNextWordProvider
import pl.kuce.impostorpl.orchestrator.NextWordProvider
import pl.kuce.impostorpl.sound.SoundPlayer
import pl.kuce.impostorpl.storage.InMemoryWordStore
import pl.kuce.impostorpl.storage.PersistentDeckStore
import pl.kuce.impostorpl.ui.components.ChoiceButton
import pl.kuce.impostorpl.ui.splash.FullscreenSplash
import pl.kuce.impostorpl.ui.theme.AppBackground
import pl.kuce.impostorpl.ui.theme.ImpostorPLTheme

enum class Stage { SETTINGS, NAMES, PASS, REVEAL, PRE_FINISHED, FINISHED }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        SoundPlayer.init(
            applicationContext,
            R.raw.keyboard_click,
            R.raw.start_sound
        )

        enableEdgeToEdge()
        setContent {
            ImpostorPLTheme {
                var initDone by rememberSaveable { mutableStateOf(false) }
                var timeDone by rememberSaveable { mutableStateOf(false) }
                var isSoundOn by rememberSaveable { mutableStateOf(true) }
                var showExitDialog by rememberSaveable { mutableStateOf(false) }

                LaunchedEffect(isSoundOn) {
                    SoundPlayer.enabled = isSoundOn
                }

                val ready = initDone && timeDone

                val context = LocalContext.current
                var nextWordProvider by remember { mutableStateOf<NextWordProvider?>(null) }

                LaunchedEffect(Unit) {
                    val initialWords = ResourceWordLoader.load(context, R.raw.words)
                    val wordStore = InMemoryWordStore(initial = initialWords)
                    val deckStore = PersistentDeckStore(context)
                    nextWordProvider = DefaultNextWordProvider(wordStore, deckStore)
                    initDone = true
                }

                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2500)
                    timeDone = true
                }

                if (!ready) {
                    FullscreenSplash(
                        bgResId = R.drawable.splash_full,
                        darken = 0f
                    )
                    return@ImpostorPLTheme
                }
                // ---- APP CONTENT AFTER INIT ----

                var stage by rememberSaveable { mutableStateOf(Stage.SETTINGS) }

                var playersCount by rememberSaveable { mutableIntStateOf(4) }
                var impostorWinPoints by rememberSaveable { mutableIntStateOf(2) }   // 1..5
                var playersWinPoints by rememberSaveable { mutableIntStateOf(1) }    // 0..4
                var impostorsCount by rememberSaveable { mutableIntStateOf(1) }      // 1..2

                var currentPlayer by rememberSaveable { mutableIntStateOf(0) }
                var roundWord by rememberSaveable { mutableStateOf("") }
                var roundId by rememberSaveable { mutableIntStateOf(0) }

                val players = remember { mutableStateListOf<pl.kuce.impostorpl.model.Player>() }

                LaunchedEffect(playersCount) {
                    while (players.size < playersCount) {
                        players += pl.kuce.impostorpl.model.Player(name = "Gracz ${players.size + 1}")
                    }
                    while (players.size > playersCount) {
                        players.removeAt(players.lastIndex)
                    }
                }

                fun startRound() {
                    // next unique word from deck
                    val w = nextWordProvider?.consumeNextWord()
                    roundWord = w?.text ?: "NO_WORDS_AVAILABLE"

                    // reset and assign impostors
                    for (i in players.indices) {
                        val p = players[i]
                        if (p.isImpostor) players[i] = p.copy(isImpostor = false)
                    }
                    if (players.isNotEmpty()) {
                        val pool = players.indices.shuffled()
                        val chosen = pool.take(impostorsCount.coerceAtMost(players.size))
                        for (idx in chosen) {
                            players[idx] = players[idx].copy(isImpostor = true)
                        }
                    }

                    currentPlayer = 0
                    stage = Stage.PASS
                    roundId++
                }

                AppBackground(
                    bgResId = R.drawable.my_bg,
                    darken = 0.75f
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            when (stage) {
                                Stage.SETTINGS -> {
                                    GameSettingsScreen(
                                        players = playersCount,
                                        onPlayersChange = { playersCount = it.coerceIn(3, 9) },
                                        impostorWinPoints = impostorWinPoints,
                                        onImpostorWinPointsChange = {
                                            impostorWinPoints = it.coerceIn(1, 5)
                                        },
                                        playersWinPoints = playersWinPoints,
                                        onPlayersWinPointsChange = {
                                            playersWinPoints = it.coerceIn(0, 4)
                                        },
                                        impostorsCount = impostorsCount,
                                        onImpostorsCountChange = {
                                            impostorsCount = it.coerceIn(1, 2)
                                        },
                                        onNext = { stage = Stage.NAMES }
                                    )
                                }

                                Stage.NAMES -> {
                                    PlayerNamesScreen(
                                        playersCount = playersCount,
                                        names = players.map { it.name },
                                        onNameChange = { index, newName ->
                                            players[index] = players[index].copy(name = newName)
                                        },
                                        onStart = { startRound() }
                                    )
                                }

                                Stage.PASS -> {
                                    PassPhoneScreen(
                                        playerLabel = players[currentPlayer].name,
                                        onPlayerReady = { stage = Stage.REVEAL }
                                    )
                                }

                                Stage.REVEAL -> {
                                    val isImpostor = players[currentPlayer].isImpostor
                                    pl.kuce.impostorpl.feature.turn.WordRevealScreen(
                                        playerNumber = currentPlayer + 1,
                                        isImpostor = isImpostor,
                                        word = roundWord,
                                        onMemorized = {
                                            if (currentPlayer + 1 < playersCount) {
                                                currentPlayer += 1
                                                stage = Stage.PASS
                                            } else {
                                                stage = Stage.PRE_FINISHED
                                            }
                                        }
                                    )
                                }

                                Stage.PRE_FINISHED -> {
                                    PreFinishedScreen(
                                        onEndRoundClick = { stage = Stage.FINISHED }
                                    )
                                }

                                Stage.FINISHED -> {
                                    FinishedScreen(
                                        players = players,
                                        roundId = roundId,
                                        impostorWinPoints = impostorWinPoints,
                                        playersWinPoints = playersWinPoints,
                                        onImpostorWin = {
                                            for (i in players.indices) if (players[i].isImpostor) {
                                                val p = players[i]
                                                players[i] =
                                                    p.copy(points = p.points + impostorWinPoints)
                                            }
                                        },
                                        onPlayersWin = {
                                            for (i in players.indices) if (!players[i].isImpostor) {
                                                val p = players[i]
                                                players[i] =
                                                    p.copy(points = p.points + playersWinPoints)
                                            }
                                        },
                                        onNewRound = { startRound() },
                                        onEndGame = {
                                            for (i in players.indices) {
                                                val p = players[i]
                                                players[i] = p.copy(points = 0, isImpostor = false)
                                            }
                                            currentPlayer = 0
                                            roundId = 0
                                            stage = Stage.SETTINGS
                                        }
                                    )
                                }
                            }
                        }

                        BottomBar(
                            soundOn = isSoundOn,
                            onToggleSound = { isSoundOn = !isSoundOn },
                            onExitClick = { showExitDialog = true }
                        )
                    }

                    if (showExitDialog) {
                        ExitConfirmationDialog(
                            onConfirmExit = {
                                showExitDialog = false
                            },
                            onDismiss = { showExitDialog = false }
                        )
                    }
                }
            }
        }
    }


    @Composable
    private fun PreFinishedScreen(
        onEndRoundClick: () -> Unit
    ) {
        LaunchedEffect(Unit) {
            SoundPlayer.play(R.raw.start_sound)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            pl.kuce.impostorpl.ui.components.PrimaryButton(
                text = "KONIEC RUNDY",
                onClick = onEndRoundClick
            )
        }
    }

    @Composable
    private fun FinishedScreen(
        players: List<pl.kuce.impostorpl.model.Player>,
        roundId: Int,
        impostorWinPoints: Int,
        playersWinPoints: Int,
        onImpostorWin: () -> Unit,
        onPlayersWin: () -> Unit,
        onNewRound: () -> Unit,
        onEndGame: () -> Unit
    ) {
        var winner by rememberSaveable(roundId) { mutableStateOf<String?>(null) }
        var confirmed by rememberSaveable(roundId) { mutableStateOf(false) }

        val impostorIndex = players.indexOfFirst { it.isImpostor }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Koniec rundy",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )

            ChoiceButton(
                text = "Impostor wygrał (+$impostorWinPoints)",
                selected = winner == "impostor",
                onClick = { if (!confirmed) winner = "impostor" },
                enabled = !confirmed
            )

            ChoiceButton(
                text = "Gracze wygrali (+$playersWinPoints dla reszty)",
                selected = winner == "players",
                onClick = { if (!confirmed) winner = "players" },
                enabled = !confirmed
            )

            pl.kuce.impostorpl.ui.components.PrimaryButton(
                text = "Potwierdź",
                onClick = {
                    if (!confirmed && winner != null) {
                        if (winner == "impostor") onImpostorWin() else onPlayersWin()
                        confirmed = true
                    }
                },
                enabled = (winner != null && !confirmed)
            )

            Spacer(Modifier.height(8.dp))

            Text("Wyniki", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)

            ResultsTable(
                rows = players
                    .mapIndexed { i, p -> Triple(i, p.name, p.points) }
                    .sortedByDescending { it.third },
                highlightIndex = impostorIndex
            )

            Spacer(Modifier.height(12.dp))

            pl.kuce.impostorpl.ui.components.PrimaryButton(
                text = "Nowa runda",
                onClick = onNewRound
            )

            pl.kuce.impostorpl.ui.components.PrimaryButton(
                text = "Zakończ rozgrywkę",
                onClick = onEndGame
            )
        }
    }


    @Composable
    private fun ResultsTable(
        rows: List<Triple<Int, String, Int>>,
        highlightIndex: Int
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Gracz",
                    style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Punkty",
                    style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(0.4f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }

            androidx.compose.material3.HorizontalDivider()

            // Body
            rows.forEach { (index, name, pts) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val suffix = if (index == highlightIndex) " (Impostor)" else ""
                    Text(
                        text = name + suffix,
                        modifier = Modifier.weight(1f),
                        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = pts.toString(),
                        modifier = Modifier.weight(0.4f),
                        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }
            }
        }
    }
    @Composable
    private fun ExitConfirmationDialog(
        onConfirmExit: () -> Unit,
        onDismiss: () -> Unit
    ) {
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmExit()
                        (context as? android.app.Activity)?.finish()
                    }
                ) {
                    Text("Tak")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Nie")
                }
            },
            title = {
                Text("Zakończyć grę?")
            },
            text = {
                Text("Czy na pewno chcesz opuścić aplikację?")
            }
        )
    }
}

