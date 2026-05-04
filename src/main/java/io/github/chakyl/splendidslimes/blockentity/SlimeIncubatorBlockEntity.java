package io.github.chakyl.splendidslimes.blockentity;

import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import io.github.chakyl.splendidslimes.SlimyConfig;
import io.github.chakyl.splendidslimes.block.PlortRippitBlock;
import io.github.chakyl.splendidslimes.entity.SlimeEntityBase;
import io.github.chakyl.splendidslimes.item.SlimeHeartItem;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

import static io.github.chakyl.splendidslimes.block.SlimeIncubatorBlock.WORKING;

public class SlimeIncubatorBlockEntity extends BlockEntity implements TickingBlockEntity {
    private int INCUBATION_TIME = SlimyConfig.incubationTime;
    protected int progress = 0;
    protected String slimeType = "";

    protected final IncubatorItemHandler inventory = new IncubatorItemHandler();

    public SlimeIncubatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModElements.BlockEntities.SLIME_INCUBATOR.get(), pos, state);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.hasNeighborSignal(pos)) return;
        if (!slimeType.isEmpty()) {
            if (this.progress >= INCUBATION_TIME) {
                SlimeEntityBase birthSlime = ModElements.Entities.SPLENDID_SLIME.get().create(level);
                birthSlime.setSlimeBreed(slimeType);
                birthSlime.setSize(1, true);
                birthSlime.setPersistenceRequired();
                BlockPos facingPos = pos.relative(state.getValue(PlortRippitBlock.FACING));
                birthSlime.moveTo(facingPos.getX() + 0.25, facingPos.getY(), facingPos.getZ() + 0.25, level.random.nextFloat() * 360.0F, 0.0F);
                level.addFreshEntity(birthSlime);
                BlockState newState = state.setValue(WORKING, false);
                level.setBlockAndUpdate(pos, newState);
                this.slimeType = "";
                this.setChanged();
            } else {
                this.progress++;
            }
        } else {
            ItemStack stack = this.inventory.getStackInSlot(0);
            if (!stack.isEmpty()) setIncubation(stack);
            else this.progress = 0;
        }
    }

    public void setIncubation(ItemStack stack) {
        if (this.getLevel() != null && !this.getLevel().isClientSide()) {
            CompoundTag heartTag = stack.getTagElement("slime");
            BlockState newState = this.getBlockState().setValue(WORKING, true);
            this.getLevel().setBlock(this.getBlockPos(), newState, 2);
            this.setSlimeType(heartTag.get("id").toString().replace("\"", ""));
        }
    }

    public boolean insertItem(ItemStack itemStack) {
        if (this.inventory.isItemValid(0, itemStack)) {
            ItemStack modifiedStack = itemStack.copy();
            modifiedStack.setCount(1);
            this.inventory.setStackInSlot(0, modifiedStack);
            setIncubation(itemStack);
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
        tag.putInt("progress", this.progress);
        tag.putString("slimeType", this.slimeType);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.progress = tag.getInt("progress");
        this.slimeType = tag.getString("slimeType");
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

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }

    public class IncubatorItemHandler extends InternalItemHandler {

        public IncubatorItemHandler() {
            super(1);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (this.getStackInSlot(0).isEmpty() && stack.getItem() instanceof SlimeHeartItem && stack.hasTag()) {
                CompoundTag heartTag = stack.getTagElement("slime");
                return heartTag != null && heartTag.contains("id");
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

    }
}