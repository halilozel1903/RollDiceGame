# Roll Dice Quest

<p align="center">
  <img src="dice.jpg" width="280" alt="Roll Dice Quest dice artwork" />
</p>

<p align="center">
  <img alt="Platform" src="https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white" />
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.4.20-7F52FF?logo=kotlin&logoColor=white" />
  <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.09.00-4285F4?logo=jetpackcompose&logoColor=white" />
  <img alt="API" src="https://img.shields.io/badge/API-24%2B-brightgreen" />
  <img alt="License" src="https://img.shields.io/badge/license-MIT-blue" />
</p>

A Kotlin + Jetpack Compose Android game that turns a classic two-dice roll into a staged quest with scoring, XP, coins, upgrades, badges, and roll history.

## Highlights

- Six tables with rising score targets and limited rolls
- Combo scoring from doubles, streaks, lucky sevens, and upgrades
- One extra roll token each table
- Persistent upgrades bought with coins
- Eight badges with XP and coin rewards
- Bottom navigation for Play, Powers, Badges, and History
- Reducer-based game logic with unit tests

## Gameplay

Roll two dice and chase the current table’s target score before your rolls run out.

Each roll adds the dice total. Doubles award a bonus; consecutive doubles grow the combo. A total of 7 is a lucky seven and adds extra score. Clearing a table pays XP and coins and unlocks the next one. Running out of rolls restarts the table with consolation XP. Each table also grants one extra-roll token.

Coins buy permanent upgrades. Badges unlock automatically from achievements.

### Tables

| Table | Target | Rolls | XP | Coins |
| --- | ---: | ---: | ---: | ---: |
| Opening Table | 34 | 6 | 40 | 30 |
| Doubles Hunt | 46 | 6 | 55 | 40 |
| Risk Corridor | 58 | 7 | 70 | 55 |
| Critical Streak | 72 | 7 | 90 | 70 |
| Champion Table | 90 | 8 | 120 | 95 |
| Legend Tour | 112 | 8 | 155 | 125 |

### Upgrades

| Upgrade | Effect | Cost |
| --- | --- | ---: |
| Extra Attempt | +1 roll on every table | 90 |
| Doubles Boost | +4 score on doubles | 130 |
| Master Training | +20% XP from table rewards | 170 |
| Lucky Seven | +8 extra score when the dice total 7 | 110 |

### Badges

| Badge | Unlock |
| --- | --- |
| First Roll | Roll for the first time |
| Double Strike | Land a double |
| Twelve | Roll 6-6 |
| Lucky 7 | Roll a total of 7 |
| Combo Master | Reach a 3x combo |
| Full Vault | Hold 250 coins |
| Fifth Table | Reach table 5 |
| Final Table | Reach Legend Tour |

## Architecture

The UI is Compose. Game rules live in a reducer so scoring, stages, upgrades, and badges stay testable and independent of the screen.

```text
MainActivity  →  DiceGameViewModel  →  DiceGameReducer
     UI              intents / state         rules
                     one-shot effects
```

| File | Role |
| --- | --- |
| `MainActivity.kt` | Screens, navigation, theme, and Compose UI |
| `DiceGameViewModel.kt` | Accepts intents, holds state, emits effects |
| `DiceGameContract.kt` | UI intent and effect contract |
| `DiceGameReducer.kt` | Scoring, stage flow, upgrades, and badges |
| `DiceGameModels.kt` | State and domain models |
| `DiceGameCatalog.kt` | Stage, upgrade, and badge definitions |
| `DiceRoller.kt` | Dice rolling abstraction |

## Tech stack

| Layer | Version |
| --- | --- |
| Language | Kotlin 2.4.20 |
| UI | Jetpack Compose BOM 2026.09.00, Material 3 |
| Architecture | AndroidX Lifecycle, Coroutines / Flow |
| Build | Gradle 9.7.1, Android Gradle Plugin 9.4.0 |
| SDK | compile/target 37, min 24 |
| Tests | JUnit 4 |

## Getting started

Open the project in Android Studio, then run the `app` configuration.

```bash
./gradlew :app:assembleDebug
./gradlew test
```

Requires JDK 17.

## Screenshots

<p>
  <img src="Screenshot_1539290025.png" width="200" alt="Play screen" />
  <img src="Screenshot_1539290034.png" width="200" alt="Game progress" />
  <img src="Screenshot_1539290044.png" width="200" alt="Dice board" />
</p>

## License

MIT. See [LICENSE](LICENSE).
