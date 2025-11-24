package io.github.chakyl.splendidslimes.entity;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class SlimeEntityBase extends Slime {
    public static final EntityDataAccessor<String> BREED = SynchedEntityData.defineId(SlimeEntityBase.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> SECONDARY_BREED = SynchedEntityData.defineId(SlimeEntityBase.class, EntityDataSerializers.STRING);

    public SlimeEntityBase(EntityType<? extends Slime> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 0)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    // Overrides the vanilla method to allow slimes to eat items despite mobgriefing statuts
    @Override
    public void aiStep() {
        super.aiStep();
        this.level().getProfiler().push("looting");
        Vec3i vec3i = this.getPickupReach();

        if (!this.level().isClientSide && this.canPickUpLoot() && this.isAlive() && !this.dead) {
            for (ItemEntity itementity : this.level().getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate((double) vec3i.getX(), (double) vec3i.getY(), (double) vec3i.getZ()))) {
                if (!itementity.isRemoved() && !itementity.getItem().isEmpty() && !itementity.hasPickUpDelay() && this.wantsToPickUp(itementity.getItem())) {
                    this.pickUpItem(itementity);
                }
            }
        }

        this.level().getProfiler().pop();
    }

    public void setSlimeBreed(String data) {
        this.entityData.set(BREED, data);
    }

    public String getSlimeBreed() {
        if (this instanceof Tarr) return SplendidSlimes.MODID + ":tarr";
        return this.entityData.get(BREED);
    }

    public void setSlimeSecondaryBreed(String data) {
        this.entityData.set(SECONDARY_BREED, data);
    }

    public String getSlimeSecondaryBreed() {
        return this.entityData.get(SECONDARY_BREED);
    }

    public boolean isLargo() {
        return !Objects.equals(this.getSlimeSecondaryBreed(), "");
    }

    @Override
    protected @NotNull Component getTypeName() {
        DynamicHolder<SlimeBreed> slime = getSlime();
        DynamicHolder<SlimeBreed> secondarySlime = getSecondarySlime();
        Component slimeName;
        if (!slime.isBound()) {
            slimeName = Component.literal("BROKEN").withStyle(ChatFormatting.OBFUSCATED);
        } else slimeName = slime.get().name();
        if (secondarySlime.isBound()) {
            return Component.translatable("entity.splendid_slimes.largo_splendid_slime", slimeName, secondarySlime.get().name());
        }
        return Component.translatable("entity.splendid_slimes.splendid_slime", slimeName);
    }

    public int getSlimeColor() {
        DynamicHolder<SlimeBreed> slime = getSlime();
        if (!slime.isBound()) return 0xFFFFFF;
        return slime.get().getColor();
    }

    public int getSecondarySlimeColor() {
        DynamicHolder<SlimeBreed> slime = getSecondarySlime();
        if (!slime.isBound()) return -1;
        return slime.get().getColor();
    }

    public DynamicHolder<SlimeBreed> getSlime() {
        String type = getSlimeBreed();
        if (type.isEmpty()) {
            return SlimeBreedRegistry.INSTANCE.emptyHolder();
        }
        return SlimeBreedRegistry.INSTANCE.holder(new ResourceLocation(type));
    }

    public DynamicHolder<SlimeBreed> getSecondarySlime() {
        String type = getSlimeSecondaryBreed();
        if (type.isEmpty()) {
            return SlimeBreedRegistry.INSTANCE.emptyHolder();
        }
        return SlimeBreedRegistry.INSTANCE.holder(new ResourceLocation(type));
    }

    public DynamicHolder<SlimeBreed> getHatSlime() {
        DynamicHolder<SlimeBreed> slime = getSecondarySlime();
        if (!slime.isBound()) {
            slime = getSlime();
        }
        return slime;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BREED, "");
        this.entityData.define(SECONDARY_BREED, "");
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> param) {
        if (BREED.equals(param)) {
            refreshDimensions();
        }
        super.onSyncedDataUpdated(param);
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setSlimeBreed(nbt.getString("Breed"));
        setSlimeSecondaryBreed(nbt.getString("SecondaryBreed"));
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putString("Breed", getSlimeBreed());
        nbt.putString("SecondaryBreed", getSlimeSecondaryBreed());
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData spawn = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (pReason == MobSpawnType.NATURAL) {
            RandomSource randomsource = pLevel.getRandom();
            int i = randomsource.nextInt(2);
            if (i < 2 && randomsource.nextFloat() < 0.5F * pDifficulty.getSpecialMultiplier()) {
                ++i;
            }
            int j = 1 << i;
            this.setSize(j, true);
        } else {
            this.setSize(0, true);
        }
        return spawn;
    }

    @Override
    protected boolean isDealsDamage() {
        return this.isEffectiveAi();
    }

}