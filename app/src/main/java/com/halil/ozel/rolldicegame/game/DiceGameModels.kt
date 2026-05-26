package com.halil.ozel.rolldicegame.game

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
    val rollsLeft: Int,
    val xp: Int = 0,
    val coins: Int = 0,
    val combo: Int = 0,
    val bestCombo: Int = 0,
    val currentRoll: DiceRoll = DiceRoll(5, 2),
    val lastMessage: String = "İlk hedef hazır. Masaya çık ve puanı topla.",
    val ownedUpgrades: Set<UpgradeId> = emptySet(),
    val unlockedBadges: Set<BadgeId> = emptySet(),
    val history: List<RollRecord> = emptyList(),
)

fun DiceGameState.currentStage(catalog: DiceGameCatalog): DiceStage = catalog.stages[stageIndex]

fun DiceGameState.playerLevel(catalog: DiceGameCatalog): Int = (xp / catalog.xpPerPlayerLevel) + 1

fun DiceGameState.xpInCurrentLevel(catalog: DiceGameCatalog): Int = xp % catalog.xpPerPlayerLevel

fun DiceGameState.stageProgress(catalog: DiceGameCatalog): Float {
    val stage = currentStage(catalog)
    return (roundScore.toFloat() / stage.targetScore).coerceIn(0f, 1f)
}
