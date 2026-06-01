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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.halil.ozel.rolldicegame.game.Badge as GameBadge
import com.halil.ozel.rolldicegame.game.BadgeId
import com.halil.ozel.rolldicegame.game.DefaultDiceGameCatalog
import com.halil.ozel.rolldicegame.game.DiceGameCatalog
import com.halil.ozel.rolldicegame.game.DiceGameState
import com.halil.ozel.rolldicegame.game.DiceUpgrade
import com.halil.ozel.rolldicegame.game.RollRecord
import com.halil.ozel.rolldicegame.game.UpgradeId
import com.halil.ozel.rolldicegame.game.currentStage
import com.halil.ozel.rolldicegame.game.playerLevel
import com.halil.ozel.rolldicegame.game.stageProgress
import com.halil.ozel.rolldicegame.game.xpInCurrentLevel
import com.halil.ozel.rolldicegame.ui.DiceGameEffect
import com.halil.ozel.rolldicegame.ui.DiceGameIntent
import com.halil.ozel.rolldicegame.ui.DiceGameViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RollDiceTheme {
                val viewModel: DiceGameViewModel = viewModel()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(viewModel) {
                    viewModel.effects.collectLatest { effect ->
                        when (effect) {
                            is DiceGameEffect.ToastMessage -> snackbarHostState.showSnackbar(effect.message)
                        }
                    }
                }

                DiceQuestApp(
                    state = state,
                    catalog = DefaultDiceGameCatalog,
                    snackbarHostState = snackbarHostState,
                    onIntent = viewModel::accept,
                )
            }
        }
    }
}

private enum class DiceScreen(
    val title: String,
    @param:DrawableRes val icon: Int,
) {
    Play("Oyun", R.drawable.ic_nav_play),
    Upgrades("Güçler", R.drawable.ic_nav_upgrades),
    Badges("Rozetler", R.drawable.ic_nav_badges),
    History("Geçmiş", R.drawable.ic_nav_history),
}

@Composable
private fun RollDiceTheme(content: @Composable () -> Unit) {
    val colors = lightColorScheme(
        primary = Color(0xFF2459A6),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFDCEAFF),
        onPrimaryContainer = Color(0xFF0F2D56),
        secondary = Color(0xFFD97706),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFFE8C2),
        onSecondaryContainer = Color(0xFF4B2B00),
        tertiary = Color(0xFF0F766E),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFD8F3EF),
        onTertiaryContainer = Color(0xFF083D39),
        background = Color(0xFFF5F7FB),
        surface = Color.White,
        surfaceVariant = Color(0xFFE8EDF5),
        onSurface = Color(0xFF172033),
        onSurfaceVariant = Color(0xFF5C6678),
        outline = Color(0xFFCAD3E1),
    )

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        content = content,
    )
}

@Composable
private fun DiceQuestApp(
    state: DiceGameState,
    catalog: DiceGameCatalog,
    snackbarHostState: SnackbarHostState,
    onIntent: (DiceGameIntent) -> Unit,
) {
    var selectedScreen by rememberSaveable { mutableStateOf(DiceScreen.Play) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            DiceNavigationBar(
                selectedScreen = selectedScreen,
                onSelected = { selectedScreen = it },
            )
        },
    ) { scaffoldPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(scaffoldPadding)
                .statusBarsPadding(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Header(
                    state = state,
                    catalog = catalog,
                    onReset = { onIntent(DiceGameIntent.ResetClicked) },
                )
            }

            when (selectedScreen) {
                DiceScreen.Play -> {
                    item {
                        DiceBoard(
                            state = state,
                            onRoll = { onIntent(DiceGameIntent.RollClicked) },
                        )
                    }
                    item {
                        StageProgress(state = state, catalog = catalog)
                    }
                    item {
                        FeatureOverview(
                            state = state,
                            catalog = catalog,
                            onScreenSelected = { selectedScreen = it },
                        )
                    }
                }

                DiceScreen.Upgrades -> {
                    item {
                        SectionTitle(title = "Yükseltmeler", subtitle = "Coinleri kalıcı oyun avantajlarına çevir.")
                    }
                    item {
                        UpgradePanel(
                            state = state,
                            upgrades = catalog.upgrades,
                            onBuyUpgrade = { onIntent(DiceGameIntent.UpgradeClicked(it)) },
                        )
                    }
                }

                DiceScreen.Badges -> {
                    item {
                        SectionTitle(title = "Rozet Koleksiyonu", subtitle = "Her başarı kendi görseli ve ödülüyle izlenir.")
                    }
                    item {
                        BadgePanel(
                            badges = catalog.badges,
                            unlocked = state.unlockedBadges,
                        )
                    }
                }

                DiceScreen.History -> {
                    item {
                        SectionTitle(title = "Oyun Geçmişi", subtitle = "Son atışları ve seri performansını takip et.")
                    }
                    item {
                        HistoryPanel(state = state)
                    }
                }
            }
        }
    }
}

@Composable
private fun DiceNavigationBar(
    selectedScreen: DiceScreen,
    onSelected: (DiceScreen) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
    ) {
        DiceScreen.entries.forEach { screen ->
            NavigationBarItem(
                selected = selectedScreen == screen,
                onClick = { onSelected(screen) },
                icon = {
                    Image(
                        painter = painterResource(id = screen.icon),
                        contentDescription = screen.title,
                        modifier = Modifier.size(24.dp),
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        maxLines = 1,
                    )
                },
            )
        }
    }
}

@Composable
private fun Header(
    state: DiceGameState,
    catalog: DiceGameCatalog,
    onReset: () -> Unit,
) {
    val stage = state.currentStage(catalog)

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Roll Dice Quest",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stage.title,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onReset,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                ) {
                    Text("Sıfırla")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatPill(label = "Etap", value = "${stage.number}/${catalog.stages.size}", modifier = Modifier.weight(1f))
                StatPill(label = "Oyuncu", value = "Lv ${state.playerLevel(catalog)}", modifier = Modifier.weight(1f))
                StatPill(label = "Coin", value = state.coins.toString(), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    containerColor: Color? = null,
) {
    Surface(
        modifier = modifier.height(62.dp),
        shape = RoundedCornerShape(8.dp),
        color = containerColor ?: MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
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

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Text(
                    text = state.lastMessage,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatPill(
                    label = "Kalan",
                    value = state.rollsLeft.toString(),
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                StatPill(
                    label = "Kombo",
                    value = "x${state.combo}",
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                StatPill(
                    label = "Toplam",
                    value = state.roundScore.toString(),
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                )
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
                    .fillMaxSize()
                    .padding(18.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun StageProgress(
    state: DiceGameState,
    catalog: DiceGameCatalog,
) {
    val stage = state.currentStage(catalog)
    val animatedProgress by animateFloatAsState(
        targetValue = state.stageProgress(catalog),
        label = "stage-progress",
    )

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
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
                    text = "Hedef ${stage.targetScore}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${state.roundScore}/${stage.targetScore}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text(
                text = "XP ${state.xpInCurrentLevel(catalog)}/${catalog.xpPerPlayerLevel}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun FeatureOverview(
    state: DiceGameState,
    catalog: DiceGameCatalog,
    onScreenSelected: (DiceScreen) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Macera Alanları",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FeatureCard(
                title = "Güçler",
                value = "${state.ownedUpgrades.size}/${catalog.upgrades.size}",
                image = R.drawable.ic_upgrade_double,
                modifier = Modifier.weight(1f),
                onClick = { onScreenSelected(DiceScreen.Upgrades) },
            )
            FeatureCard(
                title = "Rozetler",
                value = "${state.unlockedBadges.size}/${catalog.badges.size}",
                image = R.drawable.ic_badge_final,
                modifier = Modifier.weight(1f),
                onClick = { onScreenSelected(DiceScreen.Badges) },
            )
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    value: String,
    @DrawableRes image: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(118.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        contentPadding = PaddingValues(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start,
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = title,
                modifier = Modifier.size(42.dp),
            )
            Column {
                Text(text = title, fontWeight = FontWeight.Bold)
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                )
            }
        }
    }
}

@Composable
private fun UpgradePanel(
    state: DiceGameState,
    upgrades: List<DiceUpgrade>,
    onBuyUpgrade: (UpgradeId) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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

@Composable
private fun UpgradeRow(
    upgrade: DiceUpgrade,
    isOwned: Boolean,
    canBuy: Boolean,
    onBuy: () -> Unit,
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(62.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            ) {
                Image(
                    painter = painterResource(id = upgradeDrawable(upgrade.id)),
                    contentDescription = upgrade.title,
                    modifier = Modifier.padding(12.dp),
                )
            }
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    disabledContainerColor = MaterialTheme.colorScheme.outline,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
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
    unlocked: Set<BadgeId>,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        badges.forEach { badge ->
            BadgeCard(
                badge = badge,
                isUnlocked = badge.id in unlocked,
            )
        }
    }
}

@Composable
private fun BadgeCard(
    badge: GameBadge,
    isUnlocked: Boolean,
) {
    val containerColor = if (isUnlocked) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val titleColor = if (isUnlocked) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = Modifier
            .width(156.dp)
            .height(186.dp),
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Image(
                painter = painterResource(id = badgeDrawable(badge.id)),
                contentDescription = badge.title,
                modifier = Modifier
                    .size(54.dp)
                    .alpha(if (isUnlocked) 1f else 0.42f),
            )
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = badge.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Black,
                    color = titleColor,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = if (isUnlocked) "Açık" else "Kilitli",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "+${badge.xpReward} XP  +${badge.coinReward} coin",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun HistoryPanel(state: DiceGameState) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatPill(
                    label = "En iyi seri",
                    value = "x${state.bestCombo}",
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                StatPill(
                    label = "Son zar",
                    value = "${state.currentRoll.first}-${state.currentRoll.second}",
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

            if (state.history.isEmpty()) {
                EmptyHistory()
            } else {
                state.history.forEach { record ->
                    HistoryRow(record = record)
                }
            }
        }
    }
}

@Composable
private fun EmptyHistory() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_nav_history),
            contentDescription = "Geçmiş",
            modifier = Modifier.size(48.dp),
        )
        Text(
            text = "Henüz atış yok.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun HistoryRow(record: RollRecord) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_nav_play),
                contentDescription = "Atış",
                modifier = Modifier.size(34.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${record.roll.first}-${record.roll.second}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = record.label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = "+${record.earnedScore}",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

@DrawableRes
private fun badgeDrawable(id: BadgeId): Int = when (id) {
    BadgeId.FIRST_ROLL -> R.drawable.ic_badge_first_roll
    BadgeId.DOUBLE_STRIKE -> R.drawable.ic_badge_double
    BadgeId.PERFECT_TWELVE -> R.drawable.ic_badge_twelve
    BadgeId.COMBO_MASTER -> R.drawable.ic_badge_combo
    BadgeId.COIN_KEEPER -> R.drawable.ic_badge_coin
    BadgeId.STAGE_FIVE -> R.drawable.ic_badge_stage
    BadgeId.FINAL_TABLE -> R.drawable.ic_badge_final
}

@DrawableRes
private fun upgradeDrawable(id: UpgradeId): Int = when (id) {
    UpgradeId.EXTRA_ATTEMPT -> R.drawable.ic_upgrade_attempt
    UpgradeId.DOUBLE_BOOST -> R.drawable.ic_upgrade_double
    UpgradeId.XP_TRAINING -> R.drawable.ic_upgrade_xp
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
