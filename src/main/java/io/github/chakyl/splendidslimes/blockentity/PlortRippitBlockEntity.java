package io.github.chakyl.splendidslimes.blockentity;

import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import io.github.chakyl.splendidslimes.block.PlortRippitBlock;
import io.github.chakyl.splendidslimes.item.PlortItem;
import io.github.chakyl.splendidslimes.recipe.PlortRippingRecipe;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static io.github.chakyl.splendidslimes.block.PlortRippitBlock.WORKING;

public class PlortRippitBlockEntity extends BlockEntity implements TickingBlockEntity {
    private int RIPPIT_PROCESSING_TIME = 100;
    protected int progress = 0;
    protected String slimeType = "";
    protected final RippitItemHandler inventory = new RippitItemHandler();

    public PlortRippitBlockEntity(BlockPos pos, BlockState state) {
        super(ModElements.BlockEntities.PLORT_RIPPIT.get(), pos, state);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.hasNeighborSignal(pos)) return;
        if (!this.inventory.getStackInSlot(0).isEmpty() && hasRecipe()) {
            if (this.progress == 0 && !state.getValue(WORKING)) {
                BlockState newState = state.setValue(WORKING, true);
                level.setBlockAndUpdate(pos, newState);
                this.setSlimeType(this.inventory.getStackInSlot(0).getTagElement("plort").get("id").toString().replace("\"", ""));
                level.playSound(null, pos, SoundEvents.FROG_TONGUE, SoundSource.BLOCKS, 1.0F, 0.9F);
                level.playSound(null, pos, SoundEvents.FROG_EAT, SoundSource.BLOCKS, 1.0F, 0.9F);
            } else if (this.progress >= RIPPIT_PROCESSING_TIME) {
                craftItem(pos, state);
                setChanged();
                this.progress = 0;
            } else {
                this.progress++;
            }
        }
    }

    private boolean hasRecipe() {
        Optional<PlortRippingRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return false;
        ItemStack input = recipe.get().getInputItem(getLevel().registryAccess());

        return this.inventory.getStackInSlot(0).is(input.getItem());
    }

    private Optional<PlortRippingRecipe> getCurrentRecipe() {
        SimpleContainer newInventory = new SimpleContainer(this.inventory.getSlots());
        ItemStack newItemStack = this.inventory.getStackInSlot(0).copy();
        newItemStack.setCount(1);
        newInventory.setItem(0, newItemStack);
        return this.level.getRecipeManager().getRecipeFor(PlortRippingRecipe.Type.INSTANCE, newInventory, level);
    }

    private void craftItem(BlockPos pos, BlockState state) {
        Optional<PlortRippingRecipe> recipe = getCurrentRecipe();
        NonNullList<ItemStack> results = recipe.get().getResults(null);
        ItemStack outputItem = Items.AIR.getDefaultInstance();
        List<Integer> weights = recipe.get().getWeights(null);
        int weightTotal = 0;
        int currentWeight = 0;
        for (Integer weight : weights) weightTotal += weight;
        int result = 1;
        if (weightTotal > 1) {
            Random r = new Random();
            result = r.nextInt(weightTotal) + 1;
        }

        for (int i = 0; i < results.size(); i++) {
            currentWeight += weights.get(i);
            if (currentWeight >= result) {
                outputItem = results.get(i);
                break;
            }
        }
        Block.popResourceFromFace(level, pos, state.getValue(PlortRippitBlock.FACING).getOpposite(), outputItem.copy());
        level.playSound(null, pos, SoundEvents.FROG_AMBIENT, SoundSource.BLOCKS, 0.7F, 0.95F + level.getRandom().nextFloat() * 0.1F);
        if (this.inventory.getStackInSlot(0).getCount() == 1) {
            BlockState newState = state.setValue(WORKING, false);
            level.setBlockAndUpdate(pos, newState);
        }
        this.slimeType = "";
        this.inventory.getStackInSlot(0).shrink(1);
    }

    public void setSlimeType(String type) {
        this.slimeType = type;
    }

    public String getSlimeType() {
        return this.slimeType;
    }

    public int getProgress() {
        return this.progress;
    }

    public boolean insertItem(ItemStack itemStack) {
        if (this.inventory.isItemValid(0, itemStack)) {
            ItemStack modifiedStack = itemStack.copy();
            modifiedStack.setCount(1);
            this.inventory.setStackInSlot(0, modifiedStack);
            return true;
        }
        return false;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return LazyOptional.of(() -> this.inventory).cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", this.inventory.serializeNBT());
        tag.putInt("progress", this.progress);
        tag.putString("slimeType", this.slimeType);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.progress = tag.getInt("progress");
        this.slimeType = tag.getString("slimeType");
    }

    public class RippitItemHandler extends InternalItemHandler {

        public RippitItemHandler() {
            super(1);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (this.getStackInSlot(0).isEmpty() && stack.getItem() instanceof PlortItem && stack.hasTag()) {
                CompoundTag plortTag = stack.getTagElement("plort");
                return plortTag != null && plortTag.contains("id");
            }

            return false;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        protected void onContentsChanged(int slot) {
            PlortRippitBlockEntity.this.setChanged();
        }

    }
}