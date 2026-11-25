package com.cryptominers.miner;

import com.cryptominers.coin.CoinType;
import com.cryptominers.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.SlotItemHandler;

public class MinerMenu extends AbstractContainerMenu {
    private final MinerBlockEntity miner;
    private final ContainerData data;

    /** Client side. */
    public MinerMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, inv.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(MinerBlockEntity.DATA_COUNT));
    }

    /** Server side. */
    public MinerMenu(int id, Inventory inv, BlockEntity be, ContainerData data) {
        super(ModMenus.MINER.get(), id);
        this.miner = (MinerBlockEntity) be;
        this.data = data;
        checkContainerDataCount(data, MinerBlockEntity.DATA_COUNT);

        addSlot(new SlotItemHandler(miner.getOutput(), 0, 148, 78));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 110 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 168));
        }
        addDataSlots(data);
    }

    private int combine(int lo, int hi) {
        return (data.get(hi) << 16) | (data.get(lo) & 0xFFFF);
    }

    public float getTemperature() { return data.get(0) / 10f; }
    public int getEnergy() { return combine(1, 2); }
    public int getMaxEnergy() { return combine(3, 4); }
    public int getWater() { return data.get(5); }
    public float getProgress() { return data.get(6) / 1000f; }
    public int getCoin() { return data.get(7); }
    public int getPower() { return data.get(8); }
    public boolean isRunning() { return data.get(9) == 1; }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id < CoinType.values().length) {
            miner.setCoin(CoinType.values()[id]);
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (index != 0 || !slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (!moveItemStackTo(stack, 1, slots.size(), true)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return miner != null && !miner.isRemoved() && player.distanceToSqr(Vec3.atCenterOf(miner.getBlockPos())) <= 64.0;
    }
}
