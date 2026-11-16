package io.github.chakyl.splendidslimes.item;

import io.github.chakyl.splendidslimes.network.ClientBoundOpenSlimepediaScreenPacket;
import io.github.chakyl.splendidslimes.network.PacketHandler;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class SlimepediaItem extends Item {
    public SlimepediaItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PacketHandler.sendToPlayer(new ClientBoundOpenSlimepediaScreenPacket(player.getData(ModElements.Attachments.SLIMEPEDIA_DATA)), serverPlayer);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

}