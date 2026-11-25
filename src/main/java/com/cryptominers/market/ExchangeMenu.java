package com.cryptominers.market;

import com.cryptominers.coin.CoinType;
import com.cryptominers.registry.ModBlocks;
import com.cryptominers.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

/**
 * Slot-less menu for the exchange GUI. Prices are synced through ContainerData (price * 100),
 * trades are sent as menu button ids:  id = (coinIndex * 2 + (buy ? 1 : 0)) * 4 + amountIndex.
 */
public class ExchangeMenu extends AbstractContainerMenu {
    private static final int[] AMOUNTS = {1, 8, 64, 0}; // 0 = all (sell) / max (buy)

    private final boolean online;
    private final BlockPos pos;
    private final ContainerData data;

    /** Client side. */
    public ExchangeMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, buf.readBoolean(), buf.readBlockPos(), new SimpleContainerData(4));
    }

    public ExchangeMenu(int id, Inventory inv, boolean online, BlockPos pos, ContainerData data) {
        super(ModMenus.EXCHANGE.get(), id);
        this.online = online;
        this.pos = pos;
        this.data = data;
        addDataSlots(data);
    }

    public static void open(ServerPlayer sp, boolean online, BlockPos pos) {
        ContainerData data = new ContainerData() {
            @Override
            public int get(int i) {
                if (i == 3) return online ? (int) Math.round(Exchange.ONLINE_FEE * 1000) : 0;
                return (int) Math.round(MarketData.get(sp.server).getPrice(CoinType.values()[i]) * 100);
            }

            @Override
            public void set(int i, int value) {
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
        NetworkHooks.openScreen(sp, new SimpleMenuProvider((id, inv, p) -> new ExchangeMenu(id, inv, online, pos, data),
                Component.translatable(online ? "item.cryptominers.online_terminal" : "block.cryptominers.exchange")),
                buf -> {
                    buf.writeBoolean(online);
                    buf.writeBlockPos(pos);
                });
    }

    public boolean isOnline() { return online; }
    public double getPrice(CoinType c) { return data.get(c.ordinal()) / 100.0; }
    public double getFee() { return data.get(3) / 1000.0; }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!(player instanceof ServerPlayer sp) || !stillValid(player)) return false;
        int coinIdx = id / 8;
        if (id < 0 || coinIdx >= CoinType.values().length) return false;
        boolean buy = ((id / 4) & 1) == 1;
        int amount = AMOUNTS[id % 4];
        CoinType type = CoinType.values()[coinIdx];
        double fee = online ? Exchange.ONLINE_FEE : 0.0;

        if (buy) {
            if (amount == 0) amount = Exchange.maxAffordable(sp, type, fee);
            Exchange.buy(sp, type, amount, fee);
        } else {
            if (amount == 0) amount = Exchange.count(sp, type.item());
            Exchange.sell(sp, type, amount, fee);
        }
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (online) return Exchange.hasTerminal(player);
        return player.level().getBlockState(pos).is(ModBlocks.EXCHANGE.get())
                && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }
}
