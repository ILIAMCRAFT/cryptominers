package com.cryptominers.generator;

import com.cryptominers.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CoalGeneratorBlockEntity extends BlockEntity {
    private static final int CAPACITY = 100_000;
    private static final int GENERATION_PER_TICK = 80;
    private final EnergyStorage energy = new EnergyStorage(CAPACITY, 80, 80, 0);
    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);
    private int burnTime;
    private int fuelTime;

    public CoalGeneratorBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.COAL_GENERATOR.get(), pos, state); }

    public void addFuel(Player player, net.minecraft.world.InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int duration = fuelDuration(stack);
        if (duration <= 0 || (burnTime <= 0 && energy.getEnergyStored() >= CAPACITY)) return;
        if (burnTime <= 0) fuelTime = duration;
        else fuelTime += duration;
        if (!player.getAbilities().instabuild) stack.shrink(1);
        setChanged();
    }

    private static int fuelDuration(ItemStack stack) {
        if (stack.is(Items.COAL) || stack.is(Items.CHARCOAL)) return 1600;
        if (stack.is(Items.COAL_BLOCK)) return 16000;
        return 0;
    }

    public static void serverTick(Level level, BlockPos pos, CoalGeneratorBlockEntity generator) {
        if (generator.burnTime > 0) {
            generator.burnTime--;
            if (generator.energy.getEnergyStored() < CAPACITY) generator.energy.receiveEnergy(GENERATION_PER_TICK, false);
        } else if (generator.fuelTime > 0) {
            generator.burnTime = generator.fuelTime;
            generator.fuelTime = 0;
        }
        if (generator.energy.getEnergyStored() > 0) {
            for (Direction direction : Direction.values()) {
                BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
                if (neighbor == null) continue;
                neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(target -> {
                    int amount = Math.min(80, generator.energy.getEnergyStored());
                    int accepted = target.receiveEnergy(amount, false);
                    if (accepted > 0) generator.energy.extractEnergy(accepted, false);
                });
                if (generator.energy.getEnergyStored() <= 0) break;
            }
        }
        generator.setChanged();
    }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Energy", energy.getEnergyStored());
        tag.putInt("BurnTime", burnTime);
        tag.putInt("FuelTime", fuelTime);
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag);
        energy.receiveEnergy(tag.getInt("Energy"), false);
        burnTime = tag.getInt("BurnTime");
        fuelTime = tag.getInt("FuelTime");
    }
    @Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) return energyCap.cast();
        return super.getCapability(cap, side);
    }
    @Override public void invalidateCaps() { super.invalidateCaps(); energyCap.invalidate(); }
}
