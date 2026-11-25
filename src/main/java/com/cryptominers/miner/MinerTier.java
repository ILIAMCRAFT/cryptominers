package com.cryptominers.miner;

public enum MinerTier {
    // id, display name, FE per tick, heat per tick, hash (progress per tick)
    ANTMINER_S9("antminer_s9", "Antminer S9", 40, 0.10f, 4),
    ANTMINER_S19("antminer_s19", "Antminer S19", 120, 0.18f, 10),
    ANTMINER_S21("antminer_s21", "Antminer S21", 250, 0.28f, 20),
    WHATSMINER_M30("whatsminer_m30", "Whatsminer M30", 100, 0.16f, 9),
    WHATSMINER_M50("whatsminer_m50", "Whatsminer M50", 180, 0.22f, 15),
    WHATSMINER_M60("whatsminer_m60", "Whatsminer M60", 300, 0.32f, 24);

    public final String id;
    public final String displayName;
    public final int power;
    public final float heat;
    public final int hashRate;

    MinerTier(String id, String displayName, int power, float heat, int hashRate) {
        this.id = id;
        this.displayName = displayName;
        this.power = power;
        this.heat = heat;
        this.hashRate = hashRate;
    }
}
