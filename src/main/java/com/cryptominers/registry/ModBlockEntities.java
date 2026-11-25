package com.cryptominers.registry;

import com.cryptominers.CryptoMiners;
import com.cryptominers.miner.MinerBlockEntity;
import com.cryptominers.generator.CoalGeneratorBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CryptoMiners.MODID);

    public static final RegistryObject<BlockEntityType<CoalGeneratorBlockEntity>> COAL_GENERATOR = BLOCK_ENTITIES.register("coal_generator",
            () -> BlockEntityType.Builder.of(CoalGeneratorBlockEntity::new, ModBlocks.COAL_GENERATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<MinerBlockEntity>> MINER = BLOCK_ENTITIES.register("miner",
            () -> BlockEntityType.Builder.of(MinerBlockEntity::new,
                    ModBlocks.MINERS.values().stream().map(RegistryObject::get).toArray(Block[]::new)).build(null));
}
