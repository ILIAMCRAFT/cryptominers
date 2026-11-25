package com.cryptominers.market;

import com.cryptominers.coin.CoinType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.saveddata.SavedData;

/** Per-world coin prices (in emeralds), saved with the world. */
public class MarketData extends SavedData {
    private static final String NAME = "cryptominers_market";
    private final double[] prices = new double[CoinType.values().length];

    public MarketData() {
        for (CoinType c : CoinType.values()) {
            prices[c.ordinal()] = c.basePrice;
        }
    }

    public static MarketData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(MarketData::load, MarketData::new, NAME);
    }

    private static MarketData load(CompoundTag tag) {
        MarketData data = new MarketData();
        for (CoinType c : CoinType.values()) {
            if (tag.contains(c.id)) data.prices[c.ordinal()] = tag.getDouble(c.id);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        for (CoinType c : CoinType.values()) {
            tag.putDouble(c.id, prices[c.ordinal()]);
        }
        return tag;
    }

    public double getPrice(CoinType c) {
        return prices[c.ordinal()];
    }

    /** Random walk with mean reversion; clamped between 0.25x and 4x the base price. */
    public void fluctuate(RandomSource random) {
        for (CoinType c : CoinType.values()) {
            double p = prices[c.ordinal()];
            p += (c.basePrice - p) * 0.03;
            p *= 1.0 + (random.nextDouble() - 0.5) * 0.16;
            p = Math.max(c.basePrice * 0.25, Math.min(c.basePrice * 4.0, p));
            prices[c.ordinal()] = p;
        }
        setDirty();
    }
}
