package com.cryptominers.market;

import com.cryptominers.CryptoMiners;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = CryptoMiners.MODID)
public class MarketEvents {
    private static final int INTERVAL_TICKS = 1200; // 1 minute
    private static int ticks = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (++ticks < INTERVAL_TICKS) return;
        ticks = 0;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            MarketData.get(server).fluctuate(server.overworld().getRandom());
        }
    }
}
