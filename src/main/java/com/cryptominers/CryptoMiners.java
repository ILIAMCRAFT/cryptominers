package com.cryptominers;

import com.cryptominers.registry.ModBlockEntities;
import com.cryptominers.registry.ModBlocks;
import com.cryptominers.registry.ModCreativeTabs;
import com.cryptominers.registry.ModItems;
import com.cryptominers.registry.ModMenus;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CryptoMiners.MODID)
public class CryptoMiners {
    public static final String MODID = "cryptominers";

    public CryptoMiners() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(bus);
        ModItems.ITEMS.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModMenus.MENUS.register(bus);
        ModCreativeTabs.TABS.register(bus);
    }
}
