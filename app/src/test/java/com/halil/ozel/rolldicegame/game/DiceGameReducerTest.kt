package com.halil.ozel.rolldicegame.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiceGameReducerTest {
    private val reducer = DiceGameReducer()
    private val catalog = DefaultDiceGameCatalog

    @Test
    fun doubleRollAddsBonusAndUnlocksBadges() {
        val state = reducer.reduce(
            state = reducer.initialState(),
            mutation = DiceGameMutation.RollResolved(DiceRoll(6, 6)),
        )

        assertEquals(20, state.history.first().earnedScore)
        assertTrue(BadgeId.FIRST_ROLL in state.unlockedBadges)
        assertTrue(BadgeId.DOUBLE_STRIKE in state.unlockedBadges)
        assertTrue(BadgeId.PERFECT_TWELVE in state.unlockedBadges)
    }

    @Test
    fun reachingTargetCompletesStageAndPaysRewards() {
        val startingState = reducer.initialState().copy(roundScore = 32)

        val state = reducer.reduce(
            state = startingState,
            mutation = DiceGameMutation.RollResolved(DiceRoll(1, 1)),
        )

        assertEquals(1, state.stageIndex)
        assertEquals(0, state.roundScore)
        assertEquals(catalog.stages[1].maxRolls, state.rollsLeft)
        assertTrue(state.xp >= catalog.stages.first().xpReward)
        assertTrue(state.coins >= catalog.stages.first().coinReward)
    }

    @Test
    fun buyingExtraAttemptIncreasesCurrentAllowance() {
        val state = reducer.initialState().copy(coins = 100)

        val upgraded = reducer.reduce(
            state = state,
            mutation = DiceGameMutation.UpgradeRequested(UpgradeId.EXTRA_ATTEMPT),
        )

        assertTrue(UpgradeId.EXTRA_ATTEMPT in upgraded.ownedUpgrades)
        assertEquals(10, upgraded.coins)
        assertEquals(state.rollsLeft + 1, upgraded.rollsLeft)
    }
}
