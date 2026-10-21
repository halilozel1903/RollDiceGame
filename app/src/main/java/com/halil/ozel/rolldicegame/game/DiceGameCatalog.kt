package com.halil.ozel.rolldicegame.game

interface DiceGameCatalog {
    val xpPerPlayerLevel: Int
    val stages: List<DiceStage>
    val upgrades: List<DiceUpgrade>
    val badges: List<Badge>
}

object DefaultDiceGameCatalog : DiceGameCatalog {
    override val xpPerPlayerLevel: Int = 120

    override val stages: List<DiceStage> = listOf(
        DiceStage(1, "Opening Table", targetScore = 34, maxRolls = 6, xpReward = 40, coinReward = 30),
        DiceStage(2, "Doubles Hunt", targetScore = 46, maxRolls = 6, xpReward = 55, coinReward = 40),
        DiceStage(3, "Risk Corridor", targetScore = 58, maxRolls = 7, xpReward = 70, coinReward = 55),
        DiceStage(4, "Critical Streak", targetScore = 72, maxRolls = 7, xpReward = 90, coinReward = 70),
        DiceStage(5, "Champion Table", targetScore = 90, maxRolls = 8, xpReward = 120, coinReward = 95),
        DiceStage(6, "Legend Tour", targetScore = 112, maxRolls = 8, xpReward = 155, coinReward = 125),
    )

    override val upgrades: List<DiceUpgrade> = listOf(
        DiceUpgrade(
            id = UpgradeId.EXTRA_ATTEMPT,
            title = "Extra Attempt",
            description = "One extra roll on every table.",
            cost = 90,
        ),
        DiceUpgrade(
            id = UpgradeId.DOUBLE_BOOST,
            title = "Doubles Boost",
            description = "Doubles award +4 extra score.",
            cost = 130,
        ),
        DiceUpgrade(
            id = UpgradeId.XP_TRAINING,
            title = "Master Training",
            description = "+20% XP from table rewards.",
            cost = 170,
        ),
        DiceUpgrade(
            id = UpgradeId.LUCKY_SEVEN,
            title = "Lucky Seven",
            description = "+8 extra score when the dice total 7.",
            cost = 110,
        ),
    )

    override val badges: List<Badge> = listOf(
        Badge(BadgeId.FIRST_ROLL, "First Roll", "You rolled for the first time.", xpReward = 10, coinReward = 10),
        Badge(BadgeId.DOUBLE_STRIKE, "Double Strike", "You landed a double.", xpReward = 20, coinReward = 15),
        Badge(BadgeId.PERFECT_TWELVE, "Twelve", "You rolled 6-6.", xpReward = 35, coinReward = 25),
        Badge(BadgeId.LUCKY_SEVEN, "Lucky 7", "Your dice totaled 7.", xpReward = 25, coinReward = 20),
        Badge(BadgeId.COMBO_MASTER, "Combo Master", "You reached a 3x combo.", xpReward = 45, coinReward = 35),
        Badge(BadgeId.COIN_KEEPER, "Full Vault", "You held 250 coins.", xpReward = 30, coinReward = 0),
        Badge(BadgeId.STAGE_FIVE, "Fifth Table", "You reached table 5.", xpReward = 60, coinReward = 45),
        Badge(BadgeId.FINAL_TABLE, "Final Table", "You reached Legend Tour.", xpReward = 90, coinReward = 70),
    )
}
