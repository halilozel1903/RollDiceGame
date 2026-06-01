package com.halil.ozel.rolldicegame.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halil.ozel.rolldicegame.game.DiceGameMutation
import com.halil.ozel.rolldicegame.game.DiceGameReducer
import com.halil.ozel.rolldicegame.game.DiceGameState
import com.halil.ozel.rolldicegame.game.DiceRoller
import com.halil.ozel.rolldicegame.game.RandomDiceRoller
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DiceGameViewModel(
    private val reducer: DiceGameReducer = DiceGameReducer(),
    private val diceRoller: DiceRoller = RandomDiceRoller,
) : ViewModel() {
    private val _uiState = MutableStateFlow(reducer.initialState())
    val uiState: StateFlow<DiceGameState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<DiceGameEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<DiceGameEffect> = _effects.asSharedFlow()

    fun accept(intent: DiceGameIntent) {
        when (intent) {
            DiceGameIntent.RollClicked -> dispatch(DiceGameMutation.RollResolved(diceRoller.roll()))
            DiceGameIntent.ResetClicked -> dispatch(DiceGameMutation.NewGameRequested)
            is DiceGameIntent.UpgradeClicked -> dispatch(DiceGameMutation.UpgradeRequested(intent.upgradeId))
        }
    }

    private fun dispatch(mutation: DiceGameMutation) {
        var reducedState: DiceGameState? = null
        _uiState.update { currentState ->
            reducer.reduce(currentState, mutation).also { reducedState = it }
        }
        reducedState?.lastMessage?.let { message ->
            viewModelScope.launch {
                _effects.emit(DiceGameEffect.ToastMessage(message))
            }
        }
    }
}
