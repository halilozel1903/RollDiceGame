package com.halil.ozel.rolldicegame.game

import kotlin.math.max

class DiceGameReducer(
    private val catalog: DiceGameCatalog = DefaultDiceGameCatalog,
) {
    fun initialState(): DiceGameState = DiceGameState(
        rollsLeft = catalog.stages.first().maxRolls,
    )

    fun reduce(
        state: DiceGameState,
        mutation: DiceGameMutation,
    ): DiceGameState = when (mutation) {
        is DiceGameMutation.RollResolved -> roll(state, mutation.roll)
        is DiceGameMutation.UpgradeRequested -> buyUpgrade(state, mutation.upgradeId)
        DiceGameMutation.ExtraRollRequested -> grantExtraRoll(state)
        DiceGameMutation.NewGameRequested -> initialState()
    }

    private fun roll(
        state: DiceGameState,
        roll: DiceRoll,
    ): DiceGameState {
        val scoring = scoreRoll(state, roll)
        val roundScore = state.roundScore + scoring.earnedScore
        val rollsLeft = max(0, state.rollsLeft - 1)
        val history = (listOf(scoring.record) + state.history).take(MaxHistoryItems)

        val afterRoll = state.copy(
            roundScore = roundScore,
            rollsLeft = rollsLeft,
            combo = scoring.combo,
            bestCombo = max(state.bestCombo, scoring.combo),
            currentRoll = roll,
            lastMessage = scoring.record.label,
            history = history,
            luckySevenCount = state.luckySevenCount + if (roll.isLuckySeven) 1 else 0,
        )

        val stage = state.currentStage(catalog)
        val resolved = when {
            roundScore >= stage.targetScore -> completeStage(afterRoll, stage)
            rollsLeft == 0 -> missStage(afterRoll, stage)
            else -> afterRoll
        }

        return unlockBadges(resolved)
    }

    private fun buyUpgrade(
        state: DiceGameState,
        upgradeId: UpgradeId,
    ): DiceGameState {
        val upgrade = catalog.upgrades.first { it.id == upgradeId }
        if (upgrade.id in state.ownedUpgrades) {
            return state.copy(lastMessage = "${upgrade.title} is already active.")
        }
        if (state.coins < upgrade.cost) {
            return state.copy(lastMessage = "Need ${upgrade.cost - state.coins} more coins for ${upgrade.title}.")
        }

        val upgraded = state.copy(
            coins = state.coins - upgrade.cost,
            ownedUpgrades = state.ownedUpgrades + upgrade.id,
            lastMessage = "${upgrade.title} unlocked.",
        )

        return if (upgrade.id == UpgradeId.EXTRA_ATTEMPT) {
            upgraded.copy(rollsLeft = upgraded.rollsLeft + 1)
        } else {
            upgraded
        }
    }

    private fun scoreRoll(
        state: DiceGameState,
        roll: DiceRoll,
    ): RollScoring {
        val combo = if (roll.isDouble) state.combo + 1 else 0
        val doubleBonus = if (roll.isDouble) DoubleRollBonus else 0
        val upgradeBonus = if (roll.isDouble && UpgradeId.DOUBLE_BOOST in state.ownedUpgrades) DoubleBoostBonus else 0
        val luckySevenBonus = if (roll.isLuckySeven) LuckySevenBonus else 0
        val luckySevenUpgradeBonus =
            if (roll.isLuckySeven && UpgradeId.LUCKY_SEVEN in state.ownedUpgrades) LuckySevenUpgradeBonus else 0
        val comboBonus = if (combo >= ComboBonusThreshold) combo * ComboScoreMultiplier else 0
        val earnedScore = roll.sum + doubleBonus + upgradeBonus + luckySevenBonus + luckySevenUpgradeBonus + comboBonus

        return RollScoring(
            combo = combo,
            earnedScore = earnedScore,
            record = RollRecord(
                roll = roll,
                earnedScore = earnedScore,
                label = buildRollLabel(roll, earnedScore, combo, upgradeBonus, luckySevenBonus + luckySevenUpgradeBonus),
            ),
        )
    }

    private fun completeStage(
        state: DiceGameState,
        completedStage: DiceStage,
    ): DiceGameState {
        val xpMultiplier = if (UpgradeId.XP_TRAINING in state.ownedUpgrades) XpTrainingMultiplier else 1.0
        val xpReward = (completedStage.xpReward * xpMultiplier).toInt()
        val nextIndex = (state.stageIndex + 1).coerceAtMost(catalog.stages.lastIndex)
        val nextStage = catalog.stages[nextIndex]
        val rollAllowance = nextStage.maxRolls + extraAttemptCount(state)
        val message = if (state.stageIndex == catalog.stages.lastIndex) {
            "Legend Tour cleared: +$xpReward XP, +${completedStage.coinReward} coins."
        } else {
            "${completedStage.title} cleared: +$xpReward XP, +${completedStage.coinReward} coins."
        }

        return state.copy(
            stageIndex = nextIndex,
            roundScore = 0,
            rollsLeft = rollAllowance,
            xp = state.xp + xpReward,
            coins = state.coins + completedStage.coinReward,
            combo = 0,
            lastMessage = message,
            extraRollsLeft = ExtraRollsPerStage,
        )
    }

    private fun missStage(
        state: DiceGameState,
        stage: DiceStage,
    ): DiceGameState {
        val consolationXp = max(MinConsolationXp, stage.xpReward / ConsolationXpDivider)

        return state.copy(
            roundScore = 0,
            rollsLeft = stage.maxRolls + extraAttemptCount(state),
            xp = state.xp + consolationXp,
            combo = 0,
            lastMessage = "Target missed. You earned +$consolationXp XP. ${stage.title} restarted.",
            extraRollsLeft = ExtraRollsPerStage,
        )
    }

    private fun unlockBadges(state: DiceGameState): DiceGameState {
        val newlyUnlocked = catalog.badges.filter { badge ->
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
            lastMessage = "Badge unlocked: $titles. +$xpReward XP, +$coinReward coins.",
        )
    }

    private fun hasEarnedBadge(
        id: BadgeId,
        state: DiceGameState,
    ): Boolean = when (id) {
        BadgeId.FIRST_ROLL -> state.history.isNotEmpty()
        BadgeId.DOUBLE_STRIKE -> state.currentRoll.isDouble
        BadgeId.PERFECT_TWELVE -> state.currentRoll.first == 6 && state.currentRoll.second == 6
        BadgeId.LUCKY_SEVEN -> state.currentRoll.isLuckySeven || state.luckySevenCount > 0
        BadgeId.COMBO_MASTER -> state.bestCombo >= 3
        BadgeId.COIN_KEEPER -> state.coins >= 250
        BadgeId.STAGE_FIVE -> state.currentStage(catalog).number >= 5
        BadgeId.FINAL_TABLE -> state.currentStage(catalog).number == catalog.stages.last().number
    }

    private fun extraAttemptCount(state: DiceGameState): Int =
        if (UpgradeId.EXTRA_ATTEMPT in state.ownedUpgrades) 1 else 0

    private fun grantExtraRoll(state: DiceGameState): DiceGameState {
        if (state.extraRollsLeft <= 0) {
            return state.copy(lastMessage = "No extra rolls left on this table.")
        }

        return state.copy(
            extraRollsLeft = state.extraRollsLeft - 1,
            rollsLeft = state.rollsLeft + 1,
            lastMessage = "Extra roll used. Rolls left: ${state.rollsLeft + 1}.",
        )
    }

    private fun buildRollLabel(
        roll: DiceRoll,
        earnedScore: Int,
        combo: Int,
        upgradeBonus: Int,
        luckyBonus: Int,
    ): String {
        val parts = mutableListOf("${roll.first} + ${roll.second} = ${roll.sum}", "+$earnedScore score")
        if (roll.isDouble) parts += "doubles bonus"
        if (roll.isLuckySeven) parts += "lucky 7"
        if (combo >= ComboBonusThreshold) parts += "combo x$combo"
        if (upgradeBonus > 0) parts += "upgrade +$upgradeBonus"
        if (luckyBonus > 0) parts += "seven +$luckyBonus"
        return parts.joinToString(" · ")
    }

    private data class RollScoring(
        val combo: Int,
        val earnedScore: Int,
        val record: RollRecord,
    )

    private companion object {
        const val MaxHistoryItems = 6
        const val DoubleRollBonus = 8
        const val DoubleBoostBonus = 4
        const val ComboBonusThreshold = 2
        const val ComboScoreMultiplier = 3
        const val MinConsolationXp = 8
        const val ConsolationXpDivider = 5
        const val XpTrainingMultiplier = 1.2
        const val LuckySevenBonus = 5
        const val LuckySevenUpgradeBonus = 8
        const val ExtraRollsPerStage = 1
    }
}
