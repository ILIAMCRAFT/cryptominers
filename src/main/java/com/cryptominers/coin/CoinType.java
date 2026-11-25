package com.cryptominers.coin;

import com.cryptominers.registry.ModItems;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public enum CoinType {
    // id, display name, difficulty (progress points needed per coin), base price in emeralds
    MINECOIN("minecoin", "MineCoin", 2400, 1.0),
    REDCOIN("redcoin", "RedCoin", 6000, 4.0),
    DRAGONCOIN("dragoncoin", "DragonCoin", 14400, 16.0);

    public final String id;
    public final String displayName;
    public final int difficulty;
    public final double basePrice;

    CoinType(String id, String displayName, int difficulty, double basePrice) {
        this.id = id;
        this.displayName = displayName;
        this.difficulty = difficulty;
        this.basePrice = basePrice;
    }

    public Item item() {
        return ModItems.COINS.get(this).get();
    }

    @Nullable
    public static CoinType byName(String name) {
        for (CoinType c : values()) {
            if (c.id.equalsIgnoreCase(name)) return c;
        }
        return null;
    }

    @Nullable
    public static CoinType fromItem(Item item) {
        for (CoinType c : values()) {
            if (c.item() == item) return c;
        }
        return null;
    }

    public static List<String> names() {
        return Arrays.stream(values()).map(c -> c.id).toList();
    }
}
