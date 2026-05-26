package com.halil.ozel.rolldicegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.halil.ozel.rolldicegame.game.Badge as GameBadge
import com.halil.ozel.rolldicegame.game.DiceGameEngine
import com.halil.ozel.rolldicegame.game.DiceGameState
import com.halil.ozel.rolldicegame.game.DiceUpgrade
import com.halil.ozel.rolldicegame.game.UpgradeId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RollDiceTheme {
                val viewModel: DiceGameViewModel = viewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()

                DiceQuestScreen(
                    state = state,
                    onRoll = viewModel::roll,
                    onReset = viewModel::reset,
                    onBuyUpgrade = viewModel::buyUpgrade,
                )
            }
        }
    }
}

class DiceGameViewModel : ViewModel() {
    private val _state = MutableStateFlow(DiceGameEngine.newGame())
    val state: StateFlow<DiceGameState> = _state.asStateFlow()

    fun roll() {
        _state.update { DiceGameEngine.roll(it) }
    }

    fun buyUpgrade(upgradeId: UpgradeId) {
        _state.update { DiceGameEngine.buyUpgrade(it, upgradeId) }
    }

    fun reset() {
        _state.value = DiceGameEngine.newGame()
    }
}

@Composable
private fun RollDiceTheme(content: @Composable () -> Unit) {
    val colors = darkColorScheme(
        primary = Color(0xFF4ADE80),
        onPrimary = Color(0xFF07230F),
        secondary = Color(0xFFFACC15),
        onSecondary = Color(0xFF2E2400),
        tertiary = Color(0xFFFB7185),
        background = Color(0xFF101411),
        surface = Color(0xFF181C18),
        surfaceVariant = Color(0xFF242A24),
        onSurface = Color(0xFFF3F6EF),
        onSurfaceVariant = Color(0xFFC8D0C3),
    )

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        content = content,
    )
}

@Composable
private fun DiceQuestScreen(
    state: DiceGameState,
    onRoll: () -> Unit,
    onReset: () -> Unit,
    onBuyUpgrade: (UpgradeId) -> Unit,
) {
    val background = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF101411),
            Color(0xFF151B14),
            Color(0xFF1B1712),
            Color(0xFF111316),
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Header(state = state, onReset = onReset)
            }
            item {
                DiceBoard(state = state, onRoll = onRoll)
            }
            item {
                StageProgress(state = state)
            }
            item {
                UpgradePanel(
                    state = state,
                    upgrades = DiceGameEngine.upgrades,
                    onBuyUpgrade = onBuyUpgrade,
                )
            }
            item {
                BadgePanel(
                    badges = DiceGameEngine.badges,
                    unlocked = state.unlockedBadges,
                )
            }
            item {
                HistoryPanel(state = state)
            }
        }
    }
}

@Composable
private fun Header(
    state: DiceGameState,
    onReset: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Roll Dice Quest",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = state.stage.title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedButton(onClick = onReset) {
                Text("Sıfırla")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatPill(label = "Etap", value = "${state.stage.number}/${DiceGameEngine.stages.size}", modifier = Modifier.weight(1f))
            StatPill(label = "Oyuncu", value = "Lv ${state.playerLevel}", modifier = Modifier.weight(1f))
            StatPill(label = "Coin", value = state.coins.toString(), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(62.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.78f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun DiceBoard(
    state: DiceGameState,
    onRoll: () -> Unit,
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DiceFace(
                    face = state.currentRoll.first,
                    modifier = Modifier.weight(1f),
                )
                DiceFace(
                    face = state.currentRoll.second,
                    modifier = Modifier.weight(1f),
                )
            }

            Text(
                text = state.lastMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatPill(label = "Kalan", value = state.rollsLeft.toString(), modifier = Modifier.weight(1f))
                StatPill(label = "Kombo", value = "x${state.combo}", modifier = Modifier.weight(1f))
                StatPill(label = "Toplam", value = state.roundScore.toString(), modifier = Modifier.weight(1f))
            }

            Button(
                onClick = onRoll,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(
                    text = "Zar At",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun DiceFace(
    face: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFEFF5EB),
        tonalElevation = 6.dp,
    ) {
        AnimatedContent(
            targetState = face,
            label = "dice-face",
        ) { targetFace ->
            Image(
                painter = painterResource(id = diceDrawable(targetFace)),
                contentDescription = "Zar $targetFace",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun StageProgress(state: DiceGameState) {
    val animatedProgress by animateFloatAsState(
        targetValue = state.progress,
        label = "stage-progress",
    )

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Hedef ${state.stage.targetScore}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${state.roundScore}/${state.stage.targetScore}",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                )
            }
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text(
                text = "XP ${state.xpInLevel}/${DiceGameEngine.xpPerPlayerLevel}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun UpgradePanel(
    state: DiceGameState,
    upgrades: List<DiceUpgrade>,
    onBuyUpgrade: (UpgradeId) -> Unit,
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Ödül Yükseltmeleri",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            upgrades.forEach { upgrade ->
                UpgradeRow(
                    upgrade = upgrade,
                    isOwned = upgrade.id in state.ownedUpgrades,
                    canBuy = state.coins >= upgrade.cost,
                    onBuy = { onBuyUpgrade(upgrade.id) },
                )
            }
        }
    }
}

@Composable
private fun UpgradeRow(
    upgrade: DiceUpgrade,
    isOwned: Boolean,
    canBuy: Boolean,
    onBuy: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = upgrade.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = upgrade.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Button(
                onClick = onBuy,
                enabled = !isOwned && canBuy,
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(if (isOwned) "Aktif" else "${upgrade.cost}")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BadgePanel(
    badges: List<GameBadge>,
    unlocked: Set<com.halil.ozel.rolldicegame.game.BadgeId>,
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Rozetler",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                badges.forEach { badge ->
                    val isUnlocked = badge.id in unlocked
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isUnlocked) {
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.28f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                        },
                    ) {
                        Column(
                            modifier = Modifier
                                .width(150.dp)
                                .padding(10.dp),
                        ) {
                            Text(
                                text = badge.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) {
                                    MaterialTheme.colorScheme.tertiary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                            Text(
                                text = if (isUnlocked) "Açık" else "Kilitli",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryPanel(state: DiceGameState) {
    if (state.history.isEmpty()) return

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Son Atışlar",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            state.history.forEach { record ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${record.roll.first}-${record.roll.second}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "+${record.earnedScore}",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@DrawableRes
private fun diceDrawable(face: Int): Int = when (face) {
    1 -> R.drawable.dice_1
    2 -> R.drawable.dice_2
    3 -> R.drawable.dice_3
    4 -> R.drawable.dice_4
    5 -> R.drawable.dice_5
    6 -> R.drawable.dice_6
    else -> R.drawable.dice_1
}
