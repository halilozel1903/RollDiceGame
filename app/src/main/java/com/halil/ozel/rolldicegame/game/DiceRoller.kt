package com.halil.ozel.rolldicegame.game

import kotlin.random.Random

fun interface DiceRoller {
    fun roll(): DiceRoll
}

object RandomDiceRoller : DiceRoller {
    override fun roll(): DiceRoll = DiceRoll(
        first = Random.nextInt(1, 7),
        second = Random.nextInt(1, 7),
    )
}
