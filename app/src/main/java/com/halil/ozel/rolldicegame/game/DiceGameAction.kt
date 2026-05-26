package com.halil.ozel.rolldicegame.game

sealed interface DiceGameAction {
    data class RollDice(val roll: DiceRoll) : DiceGameAction
    data class BuyUpgrade(val upgradeId: UpgradeId) : DiceGameAction
    data object Reset : DiceGameAction
}
