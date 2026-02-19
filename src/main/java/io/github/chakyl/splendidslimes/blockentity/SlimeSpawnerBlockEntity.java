package io.github.chakyl.splendidslimes.blockentity;

import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import io.github.chakyl.splendidslimes.block.SlimeSpawnerBlock;
import io.github.chakyl.splendidslimes.entity.SplendidSlime;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SlimeSpawnerBlockEntity extends BlockEntity implements TickingBlockEntity {
    private int ACTIVATION_RANGE = 14;
    private int MAX_NEARBY_ENTITIES = 6;
    protected String slimeType = "";
    private int slimeCount = 3;
    private int cooldown = 0;
    private int dispensedSlimes = 0;

    public SlimeSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModElements.BlockEntities.SLIME_SPAWNER.get(), pos, state);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (!slimeType.isEmpty()) {
            BlockPos facingPos = pos.relative(state.getValue(DispenserBlock.FACING));
            if (level.getGameTime() % 4 == 0 && dispensedSlimes == 0 && !level.getBlockState(facingPos).isSolid() && isNearPlayer(level, pos)) {
                int day = (int) (Math.floor((double) level.dayTime() / 24000) + 1);
                if (day != cooldown) {
                    int nearbySlimes = level.getEntitiesOfClass(SplendidSlime.class, (new AABB(pos.getX(), pos.getY(), pos.getZ(), (pos.getX() + 1), (pos.getY() + 1), (pos.getZ() + 1))).inflate(6)).size();
                    if (nearbySlimes < this.MAX_NEARBY_ENTITIES) {
                        BlockState newState = state.setValue(SlimeSpawnerBlock.OPEN, true);
                        this.cooldown = day;
                        level.setBlockAndUpdate(pos, newState);
                        setChanged();
                    }
                }
            }
            if (level.getGameTime() % 4 == 0 && state.getValue(SlimeSpawnerBlock.OPEN)) {
                Direction direction = state.getValue(SlimeSpawnerBlock.FACING);
                double d0 = facingPos.getX() + 0.5;
                double d1 = facingPos.getY();
                double d2 = facingPos.getZ() + +0.5;
                RandomSource randomsource = level.random;
                double d3 = randomsource.triangle(direction.getStepX(), 0.11485000000000001D);
                double d4 = randomsource.triangle(direction.getStepY(), 0.11485000000000001D);
                double d5 = randomsource.triangle(direction.getStepZ(), 0.11485000000000001D);
                SplendidSlime spawnedSlime = (SplendidSlime) ModElements.Entities.SPLENDID_SLIME.get().create(level);
                spawnedSlime.setSlimeBreed(slimeType);
                spawnedSlime.setSize(2, true);
                spawnedSlime.setEatingCooldown(SplendidSlime.SLIME_STARVING_COOLDOWN / 2);
                spawnedSlime.push(d3, d4, d5);
                spawnedSlime.moveTo(d0, d1, d2, level.random.nextFloat() * 360.0F, 0.0F);
                level.addFreshEntity(spawnedSlime);
                this.dispensedSlimes++;
                spawnedSlime.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 1.2F);
            }
        }
        if (this.dispensedSlimes == slimeCount) {
            this.dispensedSlimes = 0;
            BlockState newState = state.setValue(SlimeSpawnerBlock.OPEN, false);
            level.setBlockAndUpdate(pos, newState);
            setChanged();
        }
    }

    private boolean isNearPlayer(Level pLevel, BlockPos pPos) {
        return pLevel.hasNearbyAlivePlayer((double) pPos.getX() + 0.5D, (double) pPos.getY() + 0.5D, (double) pPos.getZ() + 0.5D, (double) this.ACTIVATION_RANGE);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("cooldown", this.cooldown);
        tag.putString("slimeType", this.slimeType);
        tag.putInt("slimeCount", this.slimeCount);
        tag.putInt("dispensedSlimes", this.dispensedSlimes);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.cooldown = tag.getInt("cooldown");
        this.slimeType = tag.getString("slimeType");
        this.slimeCount = tag.getInt("slimeCount");
        this.dispensedSlimes = tag.getInt("dispensedSlimes");
    }

    public void setSlimeType(String type) {
        this.slimeType = type;
    }

    public String getSlimeType() {
        return this.slimeType;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public int setSlimeCount(int count) {
        return this.slimeCount = count;
    }

    public int getSlimeCount() {
        return this.slimeCount;
    }
}