package io.github.chakyl.splendidslimes.blockentity;

import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots;
import io.github.chakyl.splendidslimes.SlimyConfig;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.block.SlimeIncubatorBlock;
import io.github.chakyl.splendidslimes.recipe.PlortPressingRecipe;
import io.github.chakyl.splendidslimes.registry.ModElements;
import io.github.chakyl.splendidslimes.screen.PlortPressMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public class PlortPressBlockEntity extends BlockEntity implements TickingBlockEntity, SimpleDataSlots.IDataAutoRegister {
    protected final PlortPressItemHandler inventory = new PlortPressItemHandler();
    protected final SimpleDataSlots data = new SimpleDataSlots();

    private int progress = 0;
    private int PRESSING_TIME = SplendidSlimes.CONFIG.PLORT_PRESSING_TIME.getAsInt();

    public PlortPressBlockEntity(BlockPos pos, BlockState state) {
        super(ModElements.BlockEntities.PLORT_PRESS, pos, state);
        this.data.addData(() -> this.progress, v -> this.progress = v);
        this.data.addData(() -> this.PRESSING_TIME, v -> this.PRESSING_TIME = v);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.hasNeighborSignal(pos)) return;
        if (level.getGameTime() % 10 == 0) {
            if (hasRecipe()) {
                if (progress == 0) {
                    BlockState newState = state.setValue(SlimeIncubatorBlock.WORKING, true);
                    level.setBlockAndUpdate(pos, newState);
                }
                this.progress = this.progress + 10;
                setChanged(level, pos, state);
                if (this.progress >= PRESSING_TIME) {
                    craftItem();
                    BlockState newState = state.setValue(SlimeIncubatorBlock.WORKING, false);
                    level.setBlockAndUpdate(pos, newState);
                    this.progress = 0;
                }
            } else {
                this.progress = 0;
            }
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();

        if (this.level != null && !this.level.isClientSide())
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public void drops() {
        SimpleContainer simpleContainer = new SimpleContainer(2);
        simpleContainer.setItem(0, inventory.getStackInSlot(0));
        simpleContainer.setItem(1, inventory.getStackInSlot(1));
        Containers.dropContents(this.level, this.worldPosition, simpleContainer);
    }
//
//    @Nullable
//    @Override
//    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
//        return new PlortPressMenu(pContainerId, pPlayerInventory, this, this.data.asContainerData());
//    }

    private void craftItem() {
        Optional<RecipeHolder<PlortPressingRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return;
        ItemStack result = recipe.get().value().getResultItem(level.registryAccess());
        int resultCount = result.getCount();

        ItemStack outputSlot = this.inventory.getStackInSlot(1);
        this.inventory.extractItemInternal(0, recipe.get().value().getInput().getCount(), false);
        if (outputSlot.getCount() > 0 && canInsertItemIntoOutputSlot(outputSlot, result) && canInsertAmountIntoOutputSlot(outputSlot, resultCount)) {
            outputSlot.setCount(outputSlot.getCount() + resultCount);
        } else {
            this.inventory.setStackInSlot(1, result.copy());
        }
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<PlortPressingRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return false;
        ItemStack input = recipe.get().value().getInput();
        ItemStack output = recipe.get().value().getInput();
        ItemStack result = recipe.get().value().getInput();
        ItemStack inputSlot = this.inventory.getStackInSlot(0);
        ItemStack outputSlot = this.inventory.getStackInSlot(1);
        if ((output.isEmpty() && (slotMatches(outputSlot, result) && (canInsertAmountIntoOutputSlot(outputSlot, result.getCount()) && canInsertItemIntoOutputSlot(outputSlot, result)))) || validateFusion(input, inputSlot, output, outputSlot)) {
            return true;
        }
        return false;
    }

    private boolean slotMatches(ItemStack slot, ItemStack recipe) {
        return ItemStack.isSameItemSameComponents(slot, recipe);
    }

    private boolean validateFusion(ItemStack input, ItemStack inputSlot, ItemStack output, ItemStack outputSlot) {
        return slotMatches(inputSlot, input) && slotMatches(outputSlot, output) && outputSlot.getCount() == output.getCount();
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output, ItemStack result) {
        return output.isEmpty() || ItemStack.isSameItemSameComponents(output, result);
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack output, int addedCount) {
        return output.getCount() + addedCount <= output.getMaxStackSize();
    }

    private Optional<RecipeHolder<PlortPressingRecipe>> getCurrentRecipe() {
        SingleRecipeInput newInventory = new SingleRecipeInput(inventory.getStackInSlot(0));
        return this.level.getRecipeManager().getRecipeFor(PlortPressingRecipe.Type.INSTANCE, newInventory, level);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        super.saveAdditional(tag, regs);
        tag.put("inventory", this.inventory.serializeNBT(regs));
        tag.putInt("progress", this.progress);
        tag.putInt("pressing_time", this.PRESSING_TIME);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        super.loadAdditional(tag, regs);
        this.inventory.deserializeNBT(regs, tag.getCompound("inventory"));
        this.progress = tag.getInt("progress");
        this.PRESSING_TIME = tag.getInt("pressing_time");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void registerSlots(Consumer<DataSlot> consumer) {
        this.data.register(consumer);
    }

    public PlortPressItemHandler getInventory() {
        return this.inventory;
    }

//    @Override
//    public Component getDisplayName() {
//        return Component.translatable("block.splendid_slimes.plort_press");
//    }

    public class PlortPressItemHandler extends InternalItemHandler {
        public PlortPressItemHandler() {
            super(2);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 1) return false; // output slot
            return super.isItemValid(slot, stack);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot == 1) return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 0) return ItemStack.EMPTY; // top slot is input only from automation perspective if needed, or adjust based on your needs
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            PlortPressBlockEntity.this.setChanged();
        }

        public NonNullList<ItemStack> getItems() {
            return this.stacks;
        }
    }
}