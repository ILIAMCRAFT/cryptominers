package com.cryptominers.registry;

import com.cryptominers.CryptoMiners;
import com.cryptominers.coin.CoinType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CryptoMiners.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cryptominers"))
            .icon(() -> new ItemStack(CoinType.DRAGONCOIN.item()))
            .displayItems((params, out) -> ModItems.ITEMS.getEntries().forEach(e -> out.accept(e.get())))
            .build());
}
