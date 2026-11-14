package io.github.chakyl.splendidslimes.blockentity;

import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.SlimyConfig;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.block.PlortRippitBlock;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.entity.SlimeEntityBase;
import io.github.chakyl.splendidslimes.item.SlimeHeartItem;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static io.github.chakyl.splendidslimes.block.SlimeIncubatorBlock.WORKING;

public class SlimeIncubatorBlockEntity extends BlockEntity implements TickingBlockEntity {
    private int INCUBATION_TIME = SplendidSlimes.CONFIG.INCUBATION_TIME.getAsInt();
    protected int progress = 0;
    protected String slimeType = "";

    protected final IncubatorItemHandler inventory = new IncubatorItemHandler();

    public SlimeIncubatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModElements.BlockEntities.SLIME_INCUBATOR, pos, state);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.hasNeighborSignal(pos)) return;
        if (!slimeType.isEmpty()) {
            if (this.progress >= INCUBATION_TIME) {
                SlimeEntityBase birthSlime = ModElements.Entities.SPLENDID_SLIME.value().create(level);
                if (birthSlime != null) {
                    birthSlime.setSlimeBreed(slimeType);
                    birthSlime.setSize(1, true);
                    birthSlime.setPersistenceRequired();
                    BlockPos facingPos = pos.relative(state.getValue(PlortRippitBlock.FACING));
                    birthSlime.moveTo(facingPos.getX() + 0.25, facingPos.getY(), facingPos.getZ() + 0.25, level.random.nextFloat() * 360.0F, 0.0F);
                    level.addFreshEntity(birthSlime);
                }
                BlockState newState = state.setValue(WORKING, false);
                level.setBlockAndUpdate(pos, newState);
                this.slimeType = "";
                ItemStack stack = this.inventory.getStackInSlot(0);
                if (!stack.isEmpty()) stack.shrink(1);
                this.setChanged();
            } else {
                this.progress++;
            }
        } else {
            ItemStack stack = this.inventory.getStackInSlot(0);
            if (!stack.isEmpty()) setIncubation(stack, level.registryAccess());
            else this.progress = 0;
        }
    }

    public void setIncubation(ItemStack stack, HolderLookup.Provider registries) {
        if (this.getLevel() != null && !this.getLevel().isClientSide()) {
            DynamicHolder<SlimeBreed> slimeDataComponent = stack.get(ModElements.Components.SLIME_BREED);
            BlockState newState = this.getBlockState().setValue(WORKING, true);
            this.getLevel().setBlock(this.getBlockPos(), newState, 2);

            if (slimeDataComponent != null) {
                this.setSlimeType(slimeDataComponent.getId().toString());
            }
        }
    }

    public boolean insertItem(ItemStack itemStack) {
        if (this.inventory.isItemValid(0, itemStack)) {
            ItemStack modifiedStack = itemStack.copy();
            modifiedStack.setCount(1);
            this.inventory.setStackInSlot(0, modifiedStack);
            setIncubation(itemStack, level != null ? level.registryAccess() : null);
            return true;
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        super.saveAdditional(tag, regs);
        tag.put("inventory", this.inventory.serializeNBT(regs));
        tag.putInt("progress", this.progress);
        tag.putString("slimeType", this.slimeType);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        super.loadAdditional(tag, regs);
        if (tag.contains("inventory")) {
            this.inventory.deserializeNBT(regs, tag.getCompound("inventory"));
        }
        this.progress = tag.getInt("progress");
        this.slimeType = tag.getString("slimeType");
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
    public void setChanged() {
        super.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public void setSlimeType(String type) {
        this.slimeType = type;
    }

    public String getSlimeType() {
        return this.slimeType;
    }

    public boolean isIncubating() {
        return !this.slimeType.isEmpty();
    }

    public int getProgress() {
        return this.progress;
    }

    public class IncubatorItemHandler extends InternalItemHandler {

        public IncubatorItemHandler() {
            super(1);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (this.getStackInSlot(0).isEmpty() && stack.getItem() instanceof SlimeHeartItem) {
                return stack.has(ModElements.Components.SLIME_BREED);
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
            SlimeIncubatorBlockEntity.this.setChanged();
        }

        public NonNullList<ItemStack> getItems() {
            return this.stacks;
        }
    }
}