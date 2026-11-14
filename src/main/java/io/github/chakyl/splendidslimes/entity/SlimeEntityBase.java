package io.github.chakyl.splendidslimes.entity;

import com.google.common.annotations.VisibleForTesting;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.*;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;

public class SlimeEntityBase extends Mob implements NeutralMob {
    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 127;
    public static final int MAX_NATURAL_SIZE = 4;
    public float targetSquish;
    public float squish;
    public float oSquish;
    private boolean wasOnGround;

    private static final EntityDataAccessor<Integer> ID_SIZE;
    public static final EntityDataAccessor<String> BREED = SynchedEntityData.defineId(SlimeEntityBase.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> SECONDARY_BREED = SynchedEntityData.defineId(SlimeEntityBase.class, EntityDataSerializers.STRING);

    public SlimeEntityBase(EntityType<? extends SlimeEntityBase> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
        this.moveControl = new SlimeEntityBase.SlimeMoveControl(this);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SlimeEntityBase.SlimeFloatGoal(this));
        this.goalSelector.addGoal(2, new SlimeEntityBase.SlimeAttackGoal(this));
        this.goalSelector.addGoal(3, new SlimeEntityBase.SlimeRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new SlimeEntityBase.SlimeKeepOnJumpingGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, (target) -> Math.abs(((LivingEntity) target).getY() - this.getY()) <= (double) 4.0F));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
    }

    public void tick() {
        this.squish += (this.targetSquish - this.squish) * 0.5F;
        this.oSquish = this.squish;
        super.tick();
        if (this.onGround() && !this.wasOnGround) {
            float f = this.getDimensions(this.getPose()).width();
            float f1 = f / 2.0F;
            if (!this.spawnCustomParticles()) {
                for (int i = 0; (float) i < f * 4; ++i) {
                    float f2 = this.random.nextFloat() * ((float) Math.PI * 2F);
                    float f3 = this.random.nextFloat() * 0.5F + 0.5F;
                    float f4 = Mth.sin(f2) * f1 * f3;
                    float f5 = Mth.cos(f2) * f1 * f3;
                    this.level().addParticle(this.getParticleType(), this.getX() + (double) f4, this.getY(), this.getZ() + (double) f5, (double) 0.0F, (double) 0.0F, (double) 0.0F);
                }
            }

            this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.targetSquish = -0.5F;
        } else if (!this.onGround() && this.wasOnGround) {
            this.targetSquish = 1.0F;
        }

        this.wasOnGround = this.onGround();
        this.decreaseSquish();
    }

    public SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
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

    public DynamicHolder<SlimeBreed> getSlime() {
        String type = getSlimeBreed();
        if (type.isEmpty()) {
            return SlimeBreedRegistry.INSTANCE.emptyHolder();
        }
        return SlimeBreedRegistry.INSTANCE.holder(ResourceLocation.parse(type));
    }

    public String getSlimeFace() {
       return "angry";
    }

    public DynamicHolder<SlimeBreed> getSecondarySlime() {
        String type = getSlimeSecondaryBreed();
        if (type.isEmpty()) {
            return SlimeBreedRegistry.INSTANCE.emptyHolder();
        }
        return SlimeBreedRegistry.INSTANCE.holder(ResourceLocation.parse(type));
    }

    public DynamicHolder<SlimeBreed> getDecoratorSlime() {
        DynamicHolder<SlimeBreed> slime = getSecondarySlime();
        if (!slime.isBound()) {
            slime = getSlime();
        }
        return slime;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_SIZE, 1);
        builder.define(BREED, "");
        builder.define(SECONDARY_BREED, "");
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> param) {
        if (ID_SIZE.equals(param)) {
            this.refreshDimensions();
            this.setYRot(this.yHeadRot);
            this.yBodyRot = this.yHeadRot;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.doWaterSplashEffect();
            }
        }

        if (BREED.equals(param)) {
            refreshDimensions();
        }
        super.onSyncedDataUpdated(param);
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag nbt) {
        this.setSize(nbt.getInt("Size") + 1, false);
        super.readAdditionalSaveData(nbt);
        this.wasOnGround = nbt.getBoolean("wasOnGround");
        setSlimeBreed(nbt.getString("Breed"));
        setSlimeSecondaryBreed(nbt.getString("SecondaryBreed"));
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("Size", this.getSize() - 1);
        nbt.putBoolean("wasOnGround", this.wasOnGround);
        nbt.putString("Breed", getSlimeBreed());
        nbt.putString("SecondaryBreed", getSlimeSecondaryBreed());
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType
            spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData spawn = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        if (spawnType == MobSpawnType.NATURAL) {
            RandomSource randomsource = level.getRandom();
            int i = randomsource.nextInt(3);
            if (i < 2 && randomsource.nextFloat() < 0.5F * difficulty.getSpecialMultiplier()) {
                ++i;
            }

            int j = 1 << i;
            this.setSize(j, true);
        } else {

            this.setSize(0, true);
        }
        return spawn;
    }

    protected boolean isDealsDamage() {
        return this.isEffectiveAi();
    }

    public boolean isTiny() {
        return this.getSize() <= 1;
    }

    protected ParticleOptions getParticleType() {
        return ParticleTypes.ITEM_SLIME;
    }

    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @VisibleForTesting
    public void setSize(int size, boolean resetHealth) {
        int i = Mth.clamp(size, 1, 127);
        this.entityData.set(ID_SIZE, i);
        this.reapplyPosition();
        this.refreshDimensions();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double) (i * i));
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double) (0.2F + 0.1F * (float) i));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double) i);
        if (resetHealth) {
            this.setHealth(this.getMaxHealth());
        }

        this.xpReward = i;
    }

    public int getSize() {
        return (Integer) this.entityData.get(ID_SIZE);
    }

    protected void decreaseSquish() {
        this.targetSquish *= 0.6F;
    }

    protected int getJumpDelay() {
        return this.random.nextInt(20) + 10;
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public EntityType<? extends SlimeEntityBase> getType() {
        return (EntityType<? extends SlimeEntityBase>) super.getType();
    }


    public void remove(Entity.RemovalReason reason) {
        int i = this.getSize();
        if (!this.level().isClientSide && i > 1 && this.isDeadOrDying()) {
            Component component = this.getCustomName();
            boolean flag = this.isNoAi();
            float f = this.getDimensions(this.getPose()).width();
            float f1 = f / 2.0F;
            int j = i / 2;
            int k = 2 + this.random.nextInt(3);
            ArrayList<Mob> children = new ArrayList();

            for (int l = 0; l < k; ++l) {
                float f2 = ((float) (l % 2) - 0.5F) * f1;
                float f3 = ((float) (l / 2) - 0.5F) * f1;
                SlimeEntityBase slime = (SlimeEntityBase) this.getType().create(this.level());
                if (slime != null) {
                    if (this.isPersistenceRequired()) {
                        slime.setPersistenceRequired();
                    }

                    slime.setCustomName(component);
                    slime.setNoAi(flag);
                    slime.setInvulnerable(this.isInvulnerable());
                    slime.setSize(j, true);
                    slime.moveTo(this.getX() + (double) f2, this.getY() + (double) 0.5F, this.getZ() + (double) f3, this.random.nextFloat() * 360.0F, 0.0F);
                    children.add(slime);
                }
            }

            if (!EventHooks.onMobSplit(this, children).isCanceled()) {
                Level var10001 = this.level();
                Objects.requireNonNull(var10001);
                children.forEach(var10001::addFreshEntity);
            }
        }

        super.remove(reason);
    }

    public void push(Entity entity) {
        super.push(entity);
        if (entity instanceof IronGolem && this.isDealsDamage()) {
            this.dealDamage((LivingEntity) entity);
        }

    }

    public void playerTouch(Player entity) {
        if (this.isDealsDamage()) {
            this.dealDamage(entity);
        }

    }

    protected void dealDamage(LivingEntity livingEntity) {
        if (this.isAlive() && this.isWithinMeleeAttackRange(livingEntity) && this.hasLineOfSight(livingEntity)) {
            DamageSource damagesource = this.damageSources().mobAttack(this);
            if (livingEntity.hurt(damagesource, this.getAttackDamage())) {
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                Level var4 = this.level();
                if (var4 instanceof ServerLevel) {
                    ServerLevel serverlevel = (ServerLevel) var4;
                    EnchantmentHelper.doPostAttackEffects(serverlevel, livingEntity, damagesource);
                }
            }
        }

    }

    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
        return new Vec3((double) 0.0F, (double) dimensions.height() - (double) 0.015625F * (double) this.getSize() * (double) partialTick, (double) 0.0F);
    }

    protected float getAttackDamage() {
        return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return this.isTiny() ? SoundEvents.SLIME_HURT_SMALL : SoundEvents.SLIME_HURT;
    }

    protected SoundEvent getDeathSound() {
        return this.isTiny() ? SoundEvents.SLIME_DEATH_SMALL : SoundEvents.SLIME_DEATH;
    }

    protected SoundEvent getSquishSound() {
        return this.isTiny() ? SoundEvents.SLIME_SQUISH_SMALL : SoundEvents.SLIME_SQUISH;
    }

    public static boolean checkSlimeSpawnRules(EntityType<Slime> slime, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (MobSpawnType.isSpawner(spawnType)) {
            return checkMobSpawnRules(slime, level, spawnType, pos, random);
        } else {
            if (level.getDifficulty() != Difficulty.PEACEFUL) {
                if (spawnType == MobSpawnType.SPAWNER) {
                    return checkMobSpawnRules(slime, level, spawnType, pos, random);
                }

                if (level.getBiome(pos).is(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS) && pos.getY() > 50 && pos.getY() < 70 && random.nextFloat() < 0.5F && random.nextFloat() < level.getMoonBrightness() && level.getMaxLocalRawBrightness(pos) <= random.nextInt(8)) {
                    return checkMobSpawnRules(slime, level, spawnType, pos, random);
                }

                if (!(level instanceof WorldGenLevel)) {
                    return false;
                }

                ChunkPos chunkpos = new ChunkPos(pos);
                boolean flag = WorldgenRandom.seedSlimeChunk(chunkpos.x, chunkpos.z, ((WorldGenLevel) level).getSeed(), 987234911L).nextInt(10) == 0;
                if (random.nextInt(10) == 0 && flag && pos.getY() < 40) {
                    return checkMobSpawnRules(slime, level, spawnType, pos, random);
                }
            }

            return false;
        }
    }

    protected float getSoundVolume() {
        return 0.4F * (float) this.getSize();
    }

    public int getMaxHeadXRot() {
        return 0;
    }

    protected boolean doPlayJumpSound() {
        return this.getSize() > 0;
    }

    public void jumpFromGround() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, (double) this.getJumpPower(), vec3.z);
        this.hasImpulse = true;
        CommonHooks.onLivingJump(this);
    }

    float getSoundPitch() {
        float f = this.isTiny() ? 1.4F : 0.8F;
        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
    }

    protected SoundEvent getJumpSound() {
        return this.isTiny() ? SoundEvents.SLIME_JUMP_SMALL : SoundEvents.SLIME_JUMP;
    }

    public EntityDimensions getDefaultDimensions(Pose pose) {
        return super.getDefaultDimensions(pose).scale((float) this.getSize());
    }

    protected boolean spawnCustomParticles() {
        return false;
    }

    static {
        ID_SIZE = SynchedEntityData.defineId(SlimeEntityBase.class, EntityDataSerializers.INT);
    }

    // TODO: Implement anger?
    @Override
    public int getRemainingPersistentAngerTime() {
        return 0;
    }

    @Override
    public void setRemainingPersistentAngerTime(int i) {

    }

    @Override
    public @Nullable UUID getPersistentAngerTarget() {
        return null;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {
    }

    @Override
    public void startPersistentAngerTimer() {
    }

    static class SlimeAttackGoal extends Goal {
        private final SlimeEntityBase slime;
        private int growTiredTimer;

        public SlimeAttackGoal(SlimeEntityBase slime) {
            this.slime = slime;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity == null) {
                return false;
            } else {
                return !this.slime.canAttack(livingentity) ? false : this.slime.getMoveControl() instanceof SlimeEntityBase.SlimeMoveControl;
            }
        }

        public void start() {
            this.growTiredTimer = reducedTickDelay(300);
            super.start();
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity == null) {
                return false;
            } else {
                return !this.slime.canAttack(livingentity) ? false : --this.growTiredTimer > 0;
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity != null) {
                this.slime.lookAt(livingentity, 10.0F, 10.0F);
            }

            MoveControl var3 = this.slime.getMoveControl();
            if (var3 instanceof SlimeEntityBase.SlimeMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(this.slime.getYRot(), this.slime.isDealsDamage());
            }

        }
    }

    static class SlimeFloatGoal extends Goal {
        private final SlimeEntityBase slime;

        public SlimeFloatGoal(SlimeEntityBase slime) {
            this.slime = slime;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
            slime.getNavigation().setCanFloat(true);
        }

        public boolean canUse() {
            return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof SlimeEntityBase.SlimeMoveControl;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.slime.getRandom().nextFloat() < 0.8F) {
                this.slime.getJumpControl().jump();
            }

            MoveControl var2 = this.slime.getMoveControl();
            if (var2 instanceof SlimeEntityBase.SlimeMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setWantedMovement(1.2);
            }

        }
    }

    static class SlimeKeepOnJumpingGoal extends Goal {
        private final SlimeEntityBase slime;

        public SlimeKeepOnJumpingGoal(SlimeEntityBase slime) {
            this.slime = slime;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        public boolean canUse() {
            return !this.slime.isPassenger();
        }

        public void tick() {
            MoveControl var2 = this.slime.getMoveControl();
            if (var2 instanceof SlimeEntityBase.SlimeMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setWantedMovement((double) 1.0F);
            }

        }
    }

    static class SlimeMoveControl extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private final SlimeEntityBase slime;
        private boolean isAggressive;

        public SlimeMoveControl(SlimeEntityBase slime) {
            super(slime);
            this.slime = slime;
            this.yRot = 180.0F * slime.getYRot() / (float) Math.PI;
        }

        public void setDirection(float yRot, boolean aggressive) {
            this.yRot = yRot;
            this.isAggressive = aggressive;
        }

        public void setWantedMovement(double speed) {
            this.speedModifier = speed;
            this.operation = Operation.MOVE_TO;
        }

        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = Operation.WAIT;
                if (this.mob.onGround()) {
                    this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.slime.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }

                        this.slime.getJumpControl().jump();
                        if (this.slime.doPlayJumpSound()) {
                            this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
                        }
                    } else {
                        this.slime.xxa = 0.0F;
                        this.slime.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }
            }

        }
    }

    static class SlimeRandomDirectionGoal extends Goal {
        private final SlimeEntityBase slime;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public SlimeRandomDirectionGoal(SlimeEntityBase slime) {
            this.slime = slime;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            return this.slime.getTarget() == null && (this.slime.onGround() || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION)) && this.slime.getMoveControl() instanceof SlimeEntityBase.SlimeMoveControl;
        }

        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = this.adjustedTickDelay(40 + this.slime.getRandom().nextInt(60));
                this.chosenDegrees = (float) this.slime.getRandom().nextInt(360);
            }

            MoveControl var2 = this.slime.getMoveControl();
            if (var2 instanceof SlimeEntityBase.SlimeMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(this.chosenDegrees, false);
            }

        }
    }
}