package com.halil.ozel.rolldicegame.ui

import com.halil.ozel.rolldicegame.game.UpgradeId

sealed interface DiceGameIntent {
    data object RollClicked : DiceGameIntent
    data class UpgradeClicked(val upgradeId: UpgradeId) : DiceGameIntent
    data object ResetClicked : DiceGameIntent
}

sealed interface DiceGameEffect {
    data class ToastMessage(val message: String) : DiceGameEffect
}
