package com.cryptominers.market;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Online (remote) exchange with a 5% fee. Right-click anywhere to open it. */
public class TerminalItem extends Item {
    public TerminalItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            ExchangeMenu.open(sp, true, BlockPos.ZERO);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
