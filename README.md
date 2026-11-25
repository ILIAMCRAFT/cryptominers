# Crypto Miners (Forge 1.20.1)

## Build
Requires JDK 17.

1. The Gradle wrapper JAR could not be bundled. Generate it once:
   `gradle wrapper --gradle-version 8.1.1`  (or just open the folder in IntelliJ IDEA, which imports Gradle automatically).
2. `./gradlew build`      -> jar in `build/libs/`
3. `./gradlew runClient`  -> test in game

## Gameplay (all GUI based)
* **Miners** (Antminer S9/S19/S21, Whatsminer M30/M50/M60) need Forge Energy (FE) from any power mod.
  Right-click to open the miner GUI: live bars for energy, temperature, water and mining progress,
  buttons to pick the coin (MineCoin / RedCoin / DragonCoin) and an output slot (hoppers can extract too).
* **Water cooling**: right-click with a water bucket, or pipe water in from any fluid mod (4000 mB tank).
  Uses 1 mB/tick while hot (> 35 C).
* Above 90 C a miner runs at half speed; at 130 C it explodes. S9 is safe on air cooling, bigger models need water.
* **Exchange block** (in person, no fee): right-click to open the trading GUI.
* **Online Terminal** (remote, 5% fee): right-click the item anywhere to open the same trading GUI.
  It must stay in your inventory while the GUI is open.
* Trading GUI: live prices, per coin Sell 1 / 8 / 64 / All and Buy 1 / 8 / 64 / Max (paid in Emeralds).
  Prices fluctuate every minute and are saved per world.

Tuning values live in `MinerTier.java` and `CoinType.java`.

## Coal Generator (added)
- Place the Coal Generator next to one or more Crypto Miners.
- Right-click it while holding Coal, Charcoal, or a Coal Block to add fuel.
- Coal and charcoal burn for 1,600 ticks; a coal block burns for 16,000 ticks.
- Generates up to 80 Forge Energy (FE) per tick and stores up to 100,000 FE.
- Automatically sends energy to adjacent blocks that expose the Forge Energy capability.
- The generator uses the vanilla furnace texture as a temporary visual; a custom model/texture can be added later.
## Features

### ⛏️ Mining System
- Cryptocurrency mining machines.
- Automated mining operations.
- Energy-powered mining devices.
- Mining performance based on available power.

### ⚡ Power System
- Forge Energy (FE) integration.
- Coal Generator.
- Coal-based electricity generation.
- Charcoal fuel support.
- Block of Coal fuel support.
- Energy storage.
- Automatic energy transfer to adjacent machines.

### 🔧 Machines & Technology
- Dedicated mining equipment.
- Modular machine system.
- Expandable technology system.

### 🎮 Minecraft Integration
- Minecraft Java Edition 1.20.1.
- Minecraft Forge 1.20.1.
- In-game machine interaction.

### 🚧 Future Features
- Solar Generator.
- Advanced Power Generator.
- Additional mining machines.
- Improved energy management.
- New technology upgrades.

---

## Technical Information

| Property | Value |
|---|---|
| Minecraft Version | 1.20.1 |
| Mod Loader | Forge |
| Energy API | Forge Energy |
| Language | Java |
| Project Type | Minecraft Mod |

## Compatibility

CryptoMiners is designed for Minecraft Java Edition 1.20.1 running Forge.

Other Minecraft versions and mod loaders are not currently supported.