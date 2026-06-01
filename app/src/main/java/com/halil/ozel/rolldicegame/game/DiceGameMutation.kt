package com.halil.ozel.rolldicegame.game

sealed interface DiceGameMutation {
    data class RollResolved(val roll: DiceRoll) : DiceGameMutation
    data class UpgradeRequested(val upgradeId: UpgradeId) : DiceGameMutation
    data object NewGameRequested : DiceGameMutation
}
