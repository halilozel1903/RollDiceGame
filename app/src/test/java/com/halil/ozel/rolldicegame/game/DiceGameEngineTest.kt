package com.halil.ozel.rolldicegame.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiceGameEngineTest {
    @Test
    fun doubleRollAddsBonusAndUnlocksBadges() {
        val state = DiceGameEngine.roll(
            state = DiceGameEngine.newGame(),
            nextRoll = { DiceRoll(6, 6) },
        )

        assertEquals(20, state.history.first().earnedScore)
        assertTrue(BadgeId.FIRST_ROLL in state.unlockedBadges)
        assertTrue(BadgeId.DOUBLE_STRIKE in state.unlockedBadges)
        assertTrue(BadgeId.PERFECT_TWELVE in state.unlockedBadges)
    }

    @Test
    fun reachingTargetCompletesStageAndPaysRewards() {
        val startingState = DiceGameEngine.newGame().copy(roundScore = 32)

        val state = DiceGameEngine.roll(
            state = startingState,
            nextRoll = { DiceRoll(1, 1) },
        )

        assertEquals(1, state.stageIndex)
        assertEquals(0, state.roundScore)
        assertEquals(DiceGameEngine.stages[1].maxRolls, state.rollsLeft)
        assertTrue(state.xp >= DiceGameEngine.stages.first().xpReward)
        assertTrue(state.coins >= DiceGameEngine.stages.first().coinReward)
    }

    @Test
    fun buyingExtraAttemptIncreasesCurrentAllowance() {
        val state = DiceGameEngine.newGame().copy(coins = 100)

        val upgraded = DiceGameEngine.buyUpgrade(state, UpgradeId.EXTRA_ATTEMPT)

        assertTrue(UpgradeId.EXTRA_ATTEMPT in upgraded.ownedUpgrades)
        assertEquals(10, upgraded.coins)
        assertEquals(state.rollsLeft + 1, upgraded.rollsLeft)
    }
}
