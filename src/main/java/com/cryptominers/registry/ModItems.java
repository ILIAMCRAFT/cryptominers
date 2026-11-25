package com.cryptominers.registry;

import com.cryptominers.CryptoMiners;
import com.cryptominers.coin.CoinType;
import com.cryptominers.market.TerminalItem;
import com.cryptominers.miner.MinerTier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CryptoMiners.MODID);

    public static final Map<CoinType, RegistryObject<Item>> COINS = new EnumMap<>(CoinType.class);
    public static final Map<MinerTier, RegistryObject<Item>> MINER_ITEMS = new EnumMap<>(MinerTier.class);

    public static final RegistryObject<Item> TERMINAL = ITEMS.register("online_terminal",
            () -> new TerminalItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> COAL_GENERATOR_ITEM = ITEMS.register("coal_generator",
            () -> new BlockItem(ModBlocks.COAL_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<Item> EXCHANGE_ITEM = ITEMS.register("exchange",
            () -> new BlockItem(ModBlocks.EXCHANGE.get(), new Item.Properties()));

    static {
        for (CoinType coin : CoinType.values()) {
            COINS.put(coin, ITEMS.register(coin.id, () -> new Item(new Item.Properties())));
        }
        for (MinerTier tier : MinerTier.values()) {
            MINER_ITEMS.put(tier, ITEMS.register(tier.id,
                    () -> new BlockItem(ModBlocks.MINERS.get(tier).get(), new Item.Properties())));
        }
    }
}
