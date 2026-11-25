package com.cryptominers.miner;

import com.cryptominers.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class MinerBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty RUNNING = BooleanProperty.create("running");

    private final MinerTier tier;

    public MinerBlock(MinerTier tier, Properties props) {
        super(props);
        this.tier = tier;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(RUNNING, false));
    }

    public MinerTier getTier() {
        return tier;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, RUNNING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MinerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != ModBlockEntities.MINER.get()) return null;
        return (lvl, pos, st, be) -> MinerBlockEntity.serverTick(lvl, pos, st, (MinerBlockEntity) be);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof MinerBlockEntity miner)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;

        // Water bucket / fluid containers fill the cooling tank
        if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, hit.getDirection())) {
            return InteractionResult.CONSUME;
        }
        NetworkHooks.openScreen((ServerPlayer) player, miner, pos);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock()) && !level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof MinerBlockEntity miner) {
                ItemStack out = miner.takeOutput();
                if (!out.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), out);
                }
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }
}
