package com.cryptominers.miner;

import com.cryptominers.coin.CoinType;
import com.cryptominers.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MinerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int DATA_COUNT = 10;

    /** Energy storage that accepts power from outside but can only be drained internally. */
    private static class MinerEnergy extends EnergyStorage {
        MinerEnergy(int capacity, int maxReceive) {
            super(capacity, maxReceive, 0);
        }

        int use(int amount) {
            if (energy < amount) return 0;
            energy -= amount;
            return amount;
        }

        void set(int value) {
            energy = Math.max(0, Math.min(capacity, value));
        }
    }

    private final MinerTier tier;
    private final MinerEnergy energy;
    private final ContainerData data;
    private final FluidTank tank = new FluidTank(4000, fs -> fs.getFluid() == Fluids.WATER) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };
    private final ItemStackHandler output = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false; // output only
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final LazyOptional<IEnergyStorage> energyCap;
    private final LazyOptional<IFluidHandler> fluidCap = LazyOptional.of(() -> tank);
    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> output);

    private CoinType coin = CoinType.MINECOIN;
    private float temperature = 20f;
    private int progress = 0;

    public MinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MINER.get(), pos, state);
        this.tier = ((MinerBlock) state.getBlock()).getTier();
        this.energy = new MinerEnergy(Math.max(10_000, tier.power * 200), tier.power * 5);
        this.energyCap = LazyOptional.of(() -> energy);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                switch (i) {
                    case 0: return Math.round(temperature * 10f);
                    case 1: return energy.getEnergyStored() & 0xFFFF;
                    case 2: return energy.getEnergyStored() >>> 16;
                    case 3: return energy.getMaxEnergyStored() & 0xFFFF;
                    case 4: return energy.getMaxEnergyStored() >>> 16;
                    case 5: return tank.getFluidAmount();
                    case 6: return coin.difficulty == 0 ? 0 : progress * 1000 / coin.difficulty;
                    case 7: return coin.ordinal();
                    case 8: return tier.power;
                    case 9: return getBlockState().getValue(MinerBlock.RUNNING) ? 1 : 0;
                    default: return 0;
                }
            }

            @Override
            public void set(int i, int value) {
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
            }
        };
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MinerBlockEntity be) {
        MinerTier t = be.tier;
        boolean mining = false;

        ItemStack out = be.output.getStackInSlot(0);
        boolean hasRoom = out.isEmpty() || (out.is(be.coin.item()) && out.getCount() < 64);

        if (hasRoom && be.energy.use(t.power) > 0) {
            mining = true;
            be.temperature += t.heat;
            // Overheating (>= 90 C) halves the hash rate
            int hash = be.temperature >= 90f ? Math.max(1, t.hashRate / 2) : t.hashRate;
            be.progress += hash;
            if (be.progress >= be.coin.difficulty) {
                be.progress -= be.coin.difficulty;
                be.addCoin();
            }
        }

        // Passive air cooling
        be.temperature -= (be.temperature - 20f) * 0.002f;

        // Water cooling
        if (be.temperature > 35f && be.tank.getFluidAmount() > 0) {
            be.tank.drain(1, IFluidHandler.FluidAction.EXECUTE);
            be.temperature -= 0.4f;
        }
        if (be.temperature < 20f) be.temperature = 20f;

        // Meltdown
        if (be.temperature >= 130f) {
            level.removeBlock(pos, false);
            level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 2.5f, Level.ExplosionInteraction.BLOCK);
            return;
        }

        if (mining != state.getValue(MinerBlock.RUNNING)) {
            level.setBlock(pos, state.setValue(MinerBlock.RUNNING, mining), 3);
        }
        if (level.getGameTime() % 20 == 0) be.setChanged();
    }

    private void addCoin() {
        ItemStack out = output.getStackInSlot(0);
        if (out.isEmpty()) {
            output.setStackInSlot(0, new ItemStack(coin.item(), 1));
        } else {
            out.grow(1);
        }
    }

    public void setCoin(CoinType coin) {
        if (this.coin != coin) {
            this.coin = coin;
            this.progress = 0;
            setChanged();
        }
    }

    public ItemStackHandler getOutput() {
        return output;
    }

    public ItemStack takeOutput() {
        ItemStack out = output.getStackInSlot(0).copy();
        output.setStackInSlot(0, ItemStack.EMPTY);
        return out;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new MinerMenu(id, inv, this, data);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) return energyCap.cast();
        if (cap == ForgeCapabilities.FLUID_HANDLER) return fluidCap.cast();
        if (cap == ForgeCapabilities.ITEM_HANDLER) return itemCap.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyCap.invalidate();
        fluidCap.invalidate();
        itemCap.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Energy", energy.getEnergyStored());
        tag.put("Tank", tank.writeToNBT(new CompoundTag()));
        tag.put("Output", output.serializeNBT());
        tag.putString("Coin", coin.id);
        tag.putFloat("Temp", temperature);
        tag.putInt("Progress", progress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energy.set(tag.getInt("Energy"));
        tank.readFromNBT(tag.getCompound("Tank"));
        output.deserializeNBT(tag.getCompound("Output"));
        CoinType c = CoinType.byName(tag.getString("Coin"));
        coin = c == null ? CoinType.MINECOIN : c;
        temperature = tag.contains("Temp") ? tag.getFloat("Temp") : 20f;
        progress = tag.getInt("Progress");
    }
}
