package com.halil.ozel.rolldicegame.game

import kotlin.math.max
import kotlin.random.Random

data class DiceRoll(
    val first: Int,
    val second: Int,
) {
    init {
        require(first in 1..6) { "First dice must be between 1 and 6." }
        require(second in 1..6) { "Second dice must be between 1 and 6." }
    }

    val sum: Int = first + second
    val isDouble: Boolean = first == second
}

data class DiceStage(
    val number: Int,
    val title: String,
    val targetScore: Int,
    val maxRolls: Int,
    val xpReward: Int,
    val coinReward: Int,
)

enum class UpgradeId {
    EXTRA_ATTEMPT,
    DOUBLE_BOOST,
    XP_TRAINING,
}

data class DiceUpgrade(
    val id: UpgradeId,
    val title: String,
    val description: String,
    val cost: Int,
)

enum class BadgeId {
    FIRST_ROLL,
    DOUBLE_STRIKE,
    PERFECT_TWELVE,
    COMBO_MASTER,
    COIN_KEEPER,
    STAGE_FIVE,
    FINAL_TABLE,
}

data class Badge(
    val id: BadgeId,
    val title: String,
    val description: String,
    val xpReward: Int,
    val coinReward: Int,
)

data class RollRecord(
    val roll: DiceRoll,
    val earnedScore: Int,
    val label: String,
)

data class DiceGameState(
    val stageIndex: Int = 0,
    val roundScore: Int = 0,
    val rollsLeft: Int = DiceGameEngine.stages.first().maxRolls,
    val xp: Int = 0,
    val coins: Int = 0,
    val combo: Int = 0,
    val bestCombo: Int = 0,
    val currentRoll: DiceRoll = DiceRoll(5, 2),
    val lastMessage: String = "İlk hedef hazır. Masaya çık ve puanı topla.",
    val ownedUpgrades: Set<UpgradeId> = emptySet(),
    val unlockedBadges: Set<BadgeId> = emptySet(),
    val history: List<RollRecord> = emptyList(),
) {
    val stage: DiceStage = DiceGameEngine.stages[stageIndex]
    val playerLevel: Int = (xp / DiceGameEngine.xpPerPlayerLevel) + 1
    val xpInLevel: Int = xp % DiceGameEngine.xpPerPlayerLevel
    val progress: Float = (roundScore.toFloat() / stage.targetScore).coerceIn(0f, 1f)
}

object DiceGameEngine {
    const val xpPerPlayerLevel = 120

    val stages = listOf(
        DiceStage(1, "Başlangıç Masası", targetScore = 34, maxRolls = 6, xpReward = 40, coinReward = 30),
        DiceStage(2, "Çift Zar Avı", targetScore = 46, maxRolls = 6, xpReward = 55, coinReward = 40),
        DiceStage(3, "Risk Koridoru", targetScore = 58, maxRolls = 7, xpReward = 70, coinReward = 55),
        DiceStage(4, "Kritik Seri", targetScore = 72, maxRolls = 7, xpReward = 90, coinReward = 70),
        DiceStage(5, "Şampiyon Masası", targetScore = 90, maxRolls = 8, xpReward = 120, coinReward = 95),
        DiceStage(6, "Efsane Turu", targetScore = 112, maxRolls = 8, xpReward = 155, coinReward = 125),
    )

    val upgrades = listOf(
        DiceUpgrade(
            id = UpgradeId.EXTRA_ATTEMPT,
            title = "Ek Deneme",
            description = "Her etapta 1 ekstra zar hakkı.",
            cost = 90,
        ),
        DiceUpgrade(
            id = UpgradeId.DOUBLE_BOOST,
            title = "Çift Zar Bonus",
            description = "Çift geldiğinde +4 ekstra skor.",
            cost = 130,
        ),
        DiceUpgrade(
            id = UpgradeId.XP_TRAINING,
            title = "Usta Antrenmanı",
            description = "Etap ödüllerinden +%20 XP.",
            cost = 170,
        ),
    )

    val badges = listOf(
        Badge(BadgeId.FIRST_ROLL, "İlk Atış", "İlk zarı attın.", xpReward = 10, coinReward = 10),
        Badge(BadgeId.DOUBLE_STRIKE, "Çifte Güç", "Çift zar yakaladın.", xpReward = 20, coinReward = 15),
        Badge(BadgeId.PERFECT_TWELVE, "On İki", "6-6 attın.", xpReward = 35, coinReward = 25),
        Badge(BadgeId.COMBO_MASTER, "Seri Ustası", "3 komboya ulaştın.", xpReward = 45, coinReward = 35),
        Badge(BadgeId.COIN_KEEPER, "Kasa Dolu", "250 coin biriktirdin.", xpReward = 30, coinReward = 0),
        Badge(BadgeId.STAGE_FIVE, "Beşinci Masa", "5. etaba çıktın.", xpReward = 60, coinReward = 45),
        Badge(BadgeId.FINAL_TABLE, "Final Masası", "Efsane turuna ulaştın.", xpReward = 90, coinReward = 70),
    )

    fun newGame(): DiceGameState = DiceGameState()

    fun randomRoll(): DiceRoll = DiceRoll(
        first = Random.nextInt(1, 7),
        second = Random.nextInt(1, 7),
    )

    fun roll(
        state: DiceGameState,
        nextRoll: () -> DiceRoll = ::randomRoll,
    ): DiceGameState {
        val roll = nextRoll()
        val combo = if (roll.isDouble) state.combo + 1 else 0
        val doubleBonus = if (roll.isDouble) 8 else 0
        val upgradeBonus = if (roll.isDouble && UpgradeId.DOUBLE_BOOST in state.ownedUpgrades) 4 else 0
        val comboBonus = if (combo >= 2) combo * 3 else 0
        val earnedScore = roll.sum + doubleBonus + upgradeBonus + comboBonus
        val roundScore = state.roundScore + earnedScore
        val rollsLeft = max(0, state.rollsLeft - 1)
        val label = buildRollLabel(roll, earnedScore, combo, upgradeBonus)
        val history = (listOf(RollRecord(roll, earnedScore, label)) + state.history).take(6)

        val afterRoll = state.copy(
            roundScore = roundScore,
            rollsLeft = rollsLeft,
            combo = combo,
            bestCombo = max(state.bestCombo, combo),
            currentRoll = roll,
            lastMessage = label,
            history = history,
        )

        val resolved = when {
            roundScore >= state.stage.targetScore -> completeStage(afterRoll)
            rollsLeft == 0 -> missStage(afterRoll)
            else -> afterRoll
        }

        return unlockBadges(resolved)
    }

    fun buyUpgrade(state: DiceGameState, upgradeId: UpgradeId): DiceGameState {
        val upgrade = upgrades.first { it.id == upgradeId }
        if (upgrade.id in state.ownedUpgrades) {
            return state.copy(lastMessage = "${upgrade.title} zaten aktif.")
        }
        if (state.coins < upgrade.cost) {
            return state.copy(lastMessage = "${upgrade.title} için ${upgrade.cost - state.coins} coin daha lazım.")
        }

        val adjusted = state.copy(
            coins = state.coins - upgrade.cost,
            ownedUpgrades = state.ownedUpgrades + upgrade.id,
            lastMessage = "${upgrade.title} açıldı.",
        )

        return if (upgrade.id == UpgradeId.EXTRA_ATTEMPT) {
            adjusted.copy(rollsLeft = adjusted.rollsLeft + 1)
        } else {
            adjusted
        }
    }

    private fun completeStage(state: DiceGameState): DiceGameState {
        val xpMultiplier = if (UpgradeId.XP_TRAINING in state.ownedUpgrades) 1.2 else 1.0
        val stage = state.stage
        val xpReward = (stage.xpReward * xpMultiplier).toInt()
        val nextIndex = (state.stageIndex + 1).coerceAtMost(stages.lastIndex)
        val nextStage = stages[nextIndex]
        val loopsAtFinalStage = state.stageIndex == stages.lastIndex
        val rollAllowance = nextStage.maxRolls + if (UpgradeId.EXTRA_ATTEMPT in state.ownedUpgrades) 1 else 0
        val message = if (loopsAtFinalStage) {
            "Efsane turu temizlendi: +$xpReward XP, +${stage.coinReward} coin."
        } else {
            "${stage.title} tamamlandı: +$xpReward XP, +${stage.coinReward} coin."
        }

        return state.copy(
            stageIndex = nextIndex,
            roundScore = 0,
            rollsLeft = rollAllowance,
            xp = state.xp + xpReward,
            coins = state.coins + stage.coinReward,
            combo = 0,
            lastMessage = message,
        )
    }

    private fun missStage(state: DiceGameState): DiceGameState {
        val allowance = state.stage.maxRolls + if (UpgradeId.EXTRA_ATTEMPT in state.ownedUpgrades) 1 else 0
        val consolationXp = max(8, state.stage.xpReward / 5)

        return state.copy(
            roundScore = 0,
            rollsLeft = allowance,
            xp = state.xp + consolationXp,
            combo = 0,
            lastMessage = "Hedef kaçtı. +$consolationXp XP aldın, ${state.stage.title} yeniden başladı.",
        )
    }

    private fun unlockBadges(state: DiceGameState): DiceGameState {
        val newlyUnlocked = badges.filter { badge ->
            badge.id !in state.unlockedBadges && hasEarnedBadge(badge.id, state)
        }

        if (newlyUnlocked.isEmpty()) return state

        val xpReward = newlyUnlocked.sumOf { it.xpReward }
        val coinReward = newlyUnlocked.sumOf { it.coinReward }
        val titles = newlyUnlocked.joinToString { it.title }

        return state.copy(
            xp = state.xp + xpReward,
            coins = state.coins + coinReward,
            unlockedBadges = state.unlockedBadges + newlyUnlocked.map { it.id },
            lastMessage = "Rozet açıldı: $titles. +$xpReward XP, +$coinReward coin.",
        )
    }

    private fun hasEarnedBadge(id: BadgeId, state: DiceGameState): Boolean = when (id) {
        BadgeId.FIRST_ROLL -> state.history.isNotEmpty()
        BadgeId.DOUBLE_STRIKE -> state.currentRoll.isDouble
        BadgeId.PERFECT_TWELVE -> state.currentRoll.first == 6 && state.currentRoll.second == 6
        BadgeId.COMBO_MASTER -> state.bestCombo >= 3
        BadgeId.COIN_KEEPER -> state.coins >= 250
        BadgeId.STAGE_FIVE -> state.stage.number >= 5
        BadgeId.FINAL_TABLE -> state.stage.number == stages.last().number
    }

    private fun buildRollLabel(
        roll: DiceRoll,
        earnedScore: Int,
        combo: Int,
        upgradeBonus: Int,
    ): String {
        val parts = mutableListOf("${roll.first} + ${roll.second} = ${roll.sum}", "+$earnedScore skor")
        if (roll.isDouble) parts += "çift bonus"
        if (combo >= 2) parts += "kombo x$combo"
        if (upgradeBonus > 0) parts += "yükseltme +$upgradeBonus"
        return parts.joinToString(" · ")
    }
}
