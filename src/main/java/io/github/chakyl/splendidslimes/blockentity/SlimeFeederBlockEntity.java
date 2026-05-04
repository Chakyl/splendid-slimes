package io.github.chakyl.splendidslimes.blockentity;

import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.entity.SplendidSlime;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

import static io.github.chakyl.splendidslimes.block.SlimeFeederBlock.HAS_FOOD;

public class SlimeFeederBlockEntity extends RandomizableContainerBlockEntity implements TickingBlockEntity {
    private NonNullList<ItemStack> contents = NonNullList.withSize(9, ItemStack.EMPTY);

    public SlimeFeederBlockEntity(BlockPos pos, BlockState state) {
        super(ModElements.BlockEntities.SLIME_FEEDER.get(), pos, state);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.hasNeighborSignal(pos)) return;
        if (level.getGameTime() % 80 == 0) {
            Boolean hasItems = false;
            for (ItemStack slotItem : contents) {
                if (!slotItem.isEmpty()) {
                    hasItems = true;
                    break;
                }
            }
            if (state.getValue(HAS_FOOD) != hasItems) {
                BlockState newState = state.setValue(HAS_FOOD, hasItems);
                level.setBlockAndUpdate(pos, newState);
            }
            if (hasItems) {
                List<SplendidSlime> nearbySlimes = level.getEntitiesOfClass(SplendidSlime.class, new AABB(this.worldPosition).inflate(6));
                for (SplendidSlime slime : nearbySlimes) {
                    boolean handlePicky = slime.hasTrait("picky") && slime.isLargo();
                    for (ItemStack slotItem : contents) {
                        if (slime.wantsToPickUp(slotItem)) {
                            boolean isPrimary = slime.isPrimaryFood(slotItem);
                            if (!handlePicky || !(slime.getPickyLastAte() == 0 && isPrimary || slime.getPickyLastAte() == 1 && !isPrimary)) {
                                slime.handlePickup(slotItem, null);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (!trySaveLootTable(compound)) {
            ContainerHelper.saveAllItems(compound, contents);
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block." + SplendidSlimes.MODID + ".slime_feeder");
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        contents = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (!tryLoadLootTable(compound)) {
            ContainerHelper.loadAllItems(compound, contents);
        }
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return contents;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        contents = itemsIn;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new ChestMenu(MenuType.GENERIC_9x1, id, player, this, 1);
    }

}