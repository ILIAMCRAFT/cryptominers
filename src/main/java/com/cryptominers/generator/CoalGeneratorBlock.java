package com.cryptominers.generator;

import com.cryptominers.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import javax.annotation.Nullable;

public class CoalGeneratorBlock extends Block implements EntityBlock {
    public CoalGeneratorBlock(Properties properties) { super(properties); }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new CoalGeneratorBlockEntity(pos, state); }
    @Nullable @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != ModBlockEntities.COAL_GENERATOR.get()) return null;
        return (lvl, pos, st, be) -> CoalGeneratorBlockEntity.serverTick(lvl, pos, (CoalGeneratorBlockEntity) be);
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.COAL) || held.is(Items.CHARCOAL) || held.is(Items.COAL_BLOCK)) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof CoalGeneratorBlockEntity generator) generator.addFuel(player, hand);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}
