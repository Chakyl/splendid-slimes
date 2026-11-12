package io.github.chakyl.splendidslimes.item;

import io.github.chakyl.splendidslimes.network.ClientBoundOpenSlimepediaScreenPacket;
import io.github.chakyl.splendidslimes.network.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class SlimepediaItem extends Item {
    public SlimepediaItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PacketHandler.sendToPlayer(new ClientBoundOpenSlimepediaScreenPacket(List.of()), serverPlayer);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

}