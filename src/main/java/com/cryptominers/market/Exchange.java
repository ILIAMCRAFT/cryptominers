package com.cryptominers.market;

import com.cryptominers.coin.CoinType;
import com.cryptominers.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Locale;

public final class Exchange {
    public static final double ONLINE_FEE = 0.05;

    private Exchange() {}

    public static void sell(ServerPlayer p, CoinType type, int amount, double fee) {
        if (amount <= 0 || count(p, type.item()) < amount) {
            msg(p, "\u00a7cYou do not have enough " + type.displayName + ".");
            return;
        }
        double price = MarketData.get(p.server).getPrice(type);
        int payout = (int) Math.floor(price * amount * (1.0 - fee));
        if (payout < 1) {
            msg(p, "\u00a7cThat amount is worth less than 1 emerald. Sell more.");
            return;
        }
        remove(p, type.item(), amount);
        give(p, Items.EMERALD, payout);
        msg(p, String.format(Locale.ROOT, "\u00a7aSold %d %s for %d emeralds.", amount, type.displayName, payout));
    }

    public static void buy(ServerPlayer p, CoinType type, int amount, double fee) {
        if (amount <= 0) {
            msg(p, "\u00a7cYou cannot afford any " + type.displayName + ".");
            return;
        }
        double price = MarketData.get(p.server).getPrice(type);
        int cost = (int) Math.ceil(price * amount * (1.0 + fee));
        if (count(p, Items.EMERALD) < cost) {
            msg(p, "\u00a7cYou need " + cost + " emeralds.");
            return;
        }
        remove(p, Items.EMERALD, cost);
        give(p, type.item(), amount);
        msg(p, String.format(Locale.ROOT, "\u00a7aBought %d %s for %d emeralds.", amount, type.displayName, cost));
    }

    /** Most coins the player can afford (capped at 256). */
    public static int maxAffordable(ServerPlayer p, CoinType type, double fee) {
        double unit = MarketData.get(p.server).getPrice(type) * (1.0 + fee);
        return Math.min(256, (int) Math.floor(count(p, Items.EMERALD) / unit));
    }

    private static void msg(ServerPlayer p, String text) {
        p.sendSystemMessage(Component.literal(text));
    }

    public static boolean hasTerminal(Player p) {
        return count(p, ModItems.TERMINAL.get()) > 0;
    }

    public static int count(Player p, Item item) {
        int n = 0;
        Inventory inv = p.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.is(item)) n += s.getCount();
        }
        return n;
    }

    public static void remove(Player p, Item item, int amount) {
        Inventory inv = p.getInventory();
        for (int i = 0; i < inv.getContainerSize() && amount > 0; i++) {
            ItemStack s = inv.getItem(i);
            if (s.is(item)) {
                int take = Math.min(amount, s.getCount());
                s.shrink(take);
                amount -= take;
            }
        }
    }

    private static void give(ServerPlayer p, Item item, int amount) {
        while (amount > 0) {
            int n = Math.min(64, amount);
            ItemHandlerHelper.giveItemToPlayer(p, new ItemStack(item, n));
            amount -= n;
        }
    }
}
