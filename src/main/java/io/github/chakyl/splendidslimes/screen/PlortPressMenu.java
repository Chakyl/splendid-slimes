package io.github.chakyl.splendidslimes.screen;

import dev.shadowsoffire.placebo.menu.BlockEntityMenu;
import dev.shadowsoffire.placebo.menu.FilteredSlot;
import io.github.chakyl.splendidslimes.blockentity.PlortPressBlockEntity;
import io.github.chakyl.splendidslimes.item.PlortItem;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class PlortPressMenu  extends BlockEntityMenu<PlortPressBlockEntity> {
    public PlortPressMenu(int id, Inventory pInv, BlockPos pos) {
        super(ModElements.Menus.PLORT_PRESS_MENU, id, pInv, pos);
        PlortPressBlockEntity.PlortPressItemHandler inv = this.tile.getInventory();
        this.addSlot(new FilteredSlot(inv, 0, 26, 34, s -> s.is(ModElements.Items.PLORT)));
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlot(new FilteredSlot(inv, 1 + y * 3 + x, 98 + x * 18, 15 + y * 18, s -> false));
            }
        }
        this.addPlayerSlots(pInv, 8, 84);
        this.mover.registerRule((stack, slot) -> slot == 0, 9, this.slots.size());
        this.mover.registerRule((stack, slot) -> stack.getItem() instanceof PlortItem, 0, 1);
        this.mover.registerRule((stack, slot) -> slot < 9, 9, this.slots.size());
        this.registerInvShuffleRules();
    }


    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.level().getBlockState(this.pos).is(ModElements.Blocks.PLORT_PRESS);

    }

}
