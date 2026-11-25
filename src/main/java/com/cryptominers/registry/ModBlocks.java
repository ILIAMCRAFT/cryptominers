package com.cryptominers.registry;

import com.cryptominers.CryptoMiners;
import com.cryptominers.market.ExchangeBlock;
import com.cryptominers.generator.CoalGeneratorBlock;
import com.cryptominers.miner.MinerBlock;
import com.cryptominers.miner.MinerTier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CryptoMiners.MODID);

    public static final Map<MinerTier, RegistryObject<Block>> MINERS = new EnumMap<>(MinerTier.class);

    public static final RegistryObject<Block> COAL_GENERATOR = BLOCKS.register("coal_generator", () -> new CoalGeneratorBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.METAL)));

    public static final RegistryObject<Block> EXCHANGE = BLOCKS.register("exchange", () -> new ExchangeBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).strength(3.0f).requiresCorrectToolForDrops().sound(SoundType.METAL)));

    static {
        for (MinerTier tier : MinerTier.values()) {
            MINERS.put(tier, BLOCKS.register(tier.id, () -> new MinerBlock(tier,
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.METAL)
                            .lightLevel(s -> s.getValue(MinerBlock.RUNNING) ? 5 : 0))));
        }
    }
}
