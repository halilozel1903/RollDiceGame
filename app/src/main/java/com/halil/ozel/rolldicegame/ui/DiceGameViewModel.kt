package com.halil.ozel.rolldicegame.ui

import androidx.lifecycle.ViewModel
import com.halil.ozel.rolldicegame.game.DiceGameAction
import com.halil.ozel.rolldicegame.game.DiceGameReducer
import com.halil.ozel.rolldicegame.game.DiceGameState
import com.halil.ozel.rolldicegame.game.DiceRoller
import com.halil.ozel.rolldicegame.game.RandomDiceRoller
import com.halil.ozel.rolldicegame.game.UpgradeId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DiceGameViewModel(
    private val reducer: DiceGameReducer = DiceGameReducer(),
    private val diceRoller: DiceRoller = RandomDiceRoller,
) : ViewModel() {
    private val _state = MutableStateFlow(reducer.initialState())
    val state: StateFlow<DiceGameState> = _state.asStateFlow()

    fun roll() {
        dispatch(DiceGameAction.RollDice(diceRoller.roll()))
    }

    fun buyUpgrade(upgradeId: UpgradeId) {
        dispatch(DiceGameAction.BuyUpgrade(upgradeId))
    }

    fun reset() {
        dispatch(DiceGameAction.Reset)
    }

    private fun dispatch(action: DiceGameAction) {
        _state.update { currentState -> reducer.reduce(currentState, action) }
    }
}
