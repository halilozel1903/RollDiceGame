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
        DiceStage(1, "Başlangıç Masası", targetScore = 34, maxRolls = 6, xpReward = 40, coinReward = 30),
        DiceStage(2, "Çift Zar Avı", targetScore = 46, maxRolls = 6, xpReward = 55, coinReward = 40),
        DiceStage(3, "Risk Koridoru", targetScore = 58, maxRolls = 7, xpReward = 70, coinReward = 55),
        DiceStage(4, "Kritik Seri", targetScore = 72, maxRolls = 7, xpReward = 90, coinReward = 70),
        DiceStage(5, "Şampiyon Masası", targetScore = 90, maxRolls = 8, xpReward = 120, coinReward = 95),
        DiceStage(6, "Efsane Turu", targetScore = 112, maxRolls = 8, xpReward = 155, coinReward = 125),
    )

    override val upgrades: List<DiceUpgrade> = listOf(
        DiceUpgrade(
            id = UpgradeId.EXTRA_ATTEMPT,
            title = "Ek Deneme",
            description = "Her etapta 1 ekstra zar hakkı.",
            cost = 90,
        ),
        DiceUpgrade(
            id = UpgradeId.DOUBLE_BOOST,
            title = "Çift Zar Bonus",
            description = "Çift geldiğinde +4 ekstra skor.",
            cost = 130,
        ),
        DiceUpgrade(
            id = UpgradeId.XP_TRAINING,
            title = "Usta Antrenmanı",
            description = "Etap ödüllerinden +%20 XP.",
            cost = 170,
        ),
    )

    override val badges: List<Badge> = listOf(
        Badge(BadgeId.FIRST_ROLL, "İlk Atış", "İlk zarı attın.", xpReward = 10, coinReward = 10),
        Badge(BadgeId.DOUBLE_STRIKE, "Çifte Güç", "Çift zar yakaladın.", xpReward = 20, coinReward = 15),
        Badge(BadgeId.PERFECT_TWELVE, "On İki", "6-6 attın.", xpReward = 35, coinReward = 25),
        Badge(BadgeId.COMBO_MASTER, "Seri Ustası", "3 komboya ulaştın.", xpReward = 45, coinReward = 35),
        Badge(BadgeId.COIN_KEEPER, "Kasa Dolu", "250 coin biriktirdin.", xpReward = 30, coinReward = 0),
        Badge(BadgeId.STAGE_FIVE, "Beşinci Masa", "5. etaba çıktın.", xpReward = 60, coinReward = 45),
        Badge(BadgeId.FINAL_TABLE, "Final Masası", "Efsane turuna ulaştın.", xpReward = 90, coinReward = 70),
    )
}
