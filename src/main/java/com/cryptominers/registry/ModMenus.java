package com.cryptominers.registry;

import com.cryptominers.CryptoMiners;
import com.cryptominers.market.ExchangeMenu;
import com.cryptominers.miner.MinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CryptoMiners.MODID);

    public static final RegistryObject<MenuType<MinerMenu>> MINER =
            MENUS.register("miner", () -> IForgeMenuType.create(MinerMenu::new));

    public static final RegistryObject<MenuType<ExchangeMenu>> EXCHANGE =
            MENUS.register("exchange", () -> IForgeMenuType.create(ExchangeMenu::new));
}
