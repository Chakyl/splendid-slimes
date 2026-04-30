package io.github.chakyl.splendidslimes.blockentity;

import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import io.github.chakyl.splendidslimes.SlimyConfig;
import io.github.chakyl.splendidslimes.block.PlortRippitBlock;
import io.github.chakyl.splendidslimes.block.SlimeIncubatorBlock;
import io.github.chakyl.splendidslimes.entity.SlimeEntityBase;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class SlimeIncubatorBlockEntity extends BlockEntity implements TickingBlockEntity {
    private int INCUBATION_TIME = SlimyConfig.incubationTime;
    protected int progress = 0;
    protected String slimeType = "";

    public SlimeIncubatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModElements.BlockEntities.SLIME_INCUBATOR.get(), pos, state);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (!slimeType.isEmpty()) {
            if (this.progress >= INCUBATION_TIME) {
                SlimeEntityBase birthSlime = ModElements.Entities.SPLENDID_SLIME.get().create(level);
                birthSlime.setSlimeBreed(slimeType);
                birthSlime.setSize(1, true);
                birthSlime.setPersistenceRequired();
                BlockPos facingPos = pos.relative(state.getValue(PlortRippitBlock.FACING));
                birthSlime.moveTo(facingPos.getX() + 0.25, facingPos.getY(), facingPos.getZ() + 0.25, level.random.nextFloat() * 360.0F, 0.0F);
                level.addFreshEntity(birthSlime);
                BlockState newState = state.setValue(SlimeIncubatorBlock.WORKING, false);
                level.setBlockAndUpdate(pos, newState);
                this.slimeType = "";
                this.setChanged();
            }
            else {
                this.progress++;
            }
        }
        else this.progress = 0;
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

}