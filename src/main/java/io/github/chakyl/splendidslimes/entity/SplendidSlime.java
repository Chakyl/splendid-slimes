package io.github.chakyl.splendidslimes.entity;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.SlimyConfig;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.item.SlimeInspector;
import io.github.chakyl.splendidslimes.item.SlimeVac;
import io.github.chakyl.splendidslimes.registry.ModElements;
import io.github.chakyl.splendidslimes.util.SlimeComfortUtils;
import io.github.chakyl.splendidslimes.util.SlimeData;
import io.github.chakyl.splendidslimes.util.SlimeUtils;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

import static io.github.chakyl.splendidslimes.util.SlimeData.plortIsFromLargoless;
import static io.github.chakyl.splendidslimes.util.SlimeUtils.*;


public class SplendidSlime extends SlimeEntityBase {
    public static final EntityDataAccessor<Integer> HAPPINESS = SynchedEntityData.defineId(SplendidSlime.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> EATING_COOLDOWN = SynchedEntityData.defineId(SplendidSlime.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LAST_ATE = SynchedEntityData.defineId(SplendidSlime.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<String> TARGET_ENTITY = SynchedEntityData.defineId(SplendidSlime.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> PARTICLE_ANIMATION_TICK = SynchedEntityData.defineId(SplendidSlime.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> TAMED = SynchedEntityData.defineId(SplendidSlime.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(SplendidSlime.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final String SLIME = "slime";
    public static final String ID = "id";
    public static final String DATA = "data";
    public static final int SLIME_EFFECT_COOLDOWN = SlimyConfig.slimeEffectCooldown;
    public static final int SLIME_STARVING_COOLDOWN = SlimyConfig.slimeStarvingTime;
    public static final int SLIME_HUNGRY_THRESHOLD = SLIME_STARVING_COOLDOWN / 2;
    public static final int MAX_HAPPINESS = SlimyConfig.slimeMaxHappiness;
    public static final int FURIOUS_THRESHOLD = SlimyConfig.slimeFuriousThreshold;
    public static final int UNHAPPY_THRESHOLD = SlimyConfig.slimeUnhappyThreshold;
    public static final int HAPPY_THRESHOLD = SlimyConfig.slimeHappyThreshold;
    public ItemEntity itemTarget = null;
    boolean tarred;
    private final EntityType<SlimeEntityBase> entityType;
    private final int effectRadius = 6;
    private final int particleAnimationTime = 2;

    public SplendidSlime(EntityType<SlimeEntityBase> entityType, Level level) {
        super(entityType, level);
        this.entityType = entityType;
        this.tarred = false;
        this.moveControl = new SlimeMoveControl(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SlimeFloatGoal(this));
        this.goalSelector.addGoal(2, new SlimeTargetItemGoal(this, 60));
        this.goalSelector.addGoal(2, new SlimeAttackEntities(this, true, 60, null));
        this.goalSelector.addGoal(2, new SlimeAttackGoal(this));
        this.goalSelector.addGoal(3, new SlimeRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new SlimeKeepOnJumpingGoal(this));
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            int happiness = this.getHappiness();
            int eatCooldown = this.getEatingCooldown();
            if (eatCooldown > 0 && this.tickCount % 20 == 0 && this.isOwnerOnline()) {
                setEatingCooldown(eatCooldown - 20);
            }
            if (this.tickCount == 2) {
                DynamicHolder<SlimeBreed> slime = this.getSlime();
                DynamicHolder<SlimeBreed> secondarySlime = this.getSecondarySlime();
                if (slime.isBound()) applyEffects(this, this, slime.get().innateEffects(), false);
                if (secondarySlime.isBound()) applyEffects(this, this, secondarySlime.get().innateEffects(), false);
            }

            if (this.tickCount % SLIME_EFFECT_COOLDOWN == 0 && this.isOwnerOnline()) {
                DynamicHolder<SlimeBreed> slime = getSlime();
                if (slime.isBound()) {
                    if (happiness <= UNHAPPY_THRESHOLD || (this.hasTrait("inverse") && happiness >= HAPPY_THRESHOLD)) {
                        double chance = 1 - (((happiness + 1.0) / UNHAPPY_THRESHOLD));
                        if (this.random.nextFloat() <= chance) {
                            List<LivingEntity> nearbyEntities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(effectRadius));
                            for (LivingEntity entity : nearbyEntities) {
                                applyNegativeEffects(this, entity, true);
                            }
                            executeSlimeCommands(this, true, false);
                        }
                    } else if (happiness >= HAPPY_THRESHOLD || this.hasTrait("inverse")) {
                        double chance = 1 - (((happiness + 1.0) / HAPPY_THRESHOLD));
                        if (this.random.nextFloat() <= chance) {
                            List<LivingEntity> nearbyEntities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(effectRadius));
                            for (LivingEntity entity : nearbyEntities) {
                                applyPositiveEffects(this, entity, true);
                            }
                            executeSlimeCommands(this, false, false);
                        }
                    }

                    if (!notHungry()) {
                        handleHungryTraits(this);
                    }
                }
            }
            if (this.tickCount % (SLIME_EFFECT_COOLDOWN * 4) == 0 && this.hasTrait("weeping")) {
                if (!SlimeUtils.cry(this, this.level())) {
                    addHappiness(-50);
                }
            }
            if (this.tickCount % 600 == 0 && this.isOwnerOnline()) {
                if (happiness <= FURIOUS_THRESHOLD && this.hasTrait("nuclear")) {
                    this.level().explode(this, this.getX(), this.getY(), this.getZ(), this.hasTrait("flaming") ? 12f : 16f, this.hasTrait("flaming"), Level.ExplosionInteraction.MOB);
                    this.kill();
                }

                if (this.hasTrait("floating") && this.random.nextFloat() <= 0.65) {
                    this.addEffect(new MobEffectInstance(MobEffects.LEVITATION, this.random.nextIntBetweenInclusive(10, 300), 0, true, false));
                }
                if (happiness > MAX_HAPPINESS) setHappiness(MAX_HAPPINESS - 1);
                else if (happiness > 0) {
                    if (SlimeComfortUtils.aquaticTraitCheck(this)) {
                        addHappiness(-10);
                    }
                    if (this.getEatingCooldown() == 0) addHappiness(-5);
                } else setHappiness(0);
            }
            int particleAnimationTick = this.getParticleAnimationTick();
            int animationTime = effectRadius * particleAnimationTime;
            if (!this.isDeadOrDying() && particleAnimationTick > -1 && particleAnimationTick < animationTime) {
                this.level().broadcastEntityEvent(this, (byte) 5);
                if (particleAnimationTick + 1 >= animationTime) this.setParticleAnimationTick(-1);
                else this.setParticleAnimationTick(particleAnimationTick + 1);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == 5) {
            SimpleParticleType particle = this.getSlime().get().emitEffectParticle();
            int ringPoints = 32;
            int tickCount = getParticleAnimationTick();
            for (int i = 0; i < ringPoints; i++) {
                double radius = ((double) 1 / particleAnimationTime) * (double) tickCount;
                double angle = 2 * Math.PI * i / ringPoints;
                double x = this.getRandomX(0.8) + radius * Math.cos(angle);
                double y = this.getRandomY() + 0.5;
                double z = this.getRandomZ(0.8) + radius * Math.sin(angle);
                this.level().addParticle(particle, true, x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
        if (event == 4) {
            for (int i = 0; i < 32; i++) {
                double d0 = this.random.nextGaussian() * 0.2D;
                double d1 = this.random.nextGaussian() * 0.2D;
                double d2 = this.random.nextGaussian() * 0.2D;
                this.level().addParticle(ParticleTypes.TOTEM_OF_UNDYING, true, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
            }
        } else {
            super.handleEntityEvent(event);
        }
    }

    public InteractionResult pickupSlime(Player player) {
        this.revive();
        if (level().isClientSide) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 0.9F);
            return InteractionResult.SUCCESS;
        }

        CompoundTag slimeItemTags = new CompoundTag();
        CompoundTag slimeTags = new CompoundTag();
        this.save(slimeTags);
        slimeItemTags.put("entity", slimeTags);
        CompoundTag id = new CompoundTag();
        id.putString("id", this.getSlimeBreed());
        this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 0.9F);
        slimeItemTags.put("slime", id);

        ItemStack slimeItem = new ItemStack(ModElements.Items.SLIME_ITEM.get());
        slimeItem.setTag(slimeItemTags);
        this.discard();

        if (!player.getInventory().add(slimeItem)) {
            ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY() + 0.5, this.getZ(), slimeItem);
            itemEntity.setPickUpDelay(0);
            itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0, 1, 0));
            this.level().addFreshEntity(itemEntity);
        }

        return InteractionResult.CONSUME;
    }

    public EntityType<SlimeEntityBase> getEntityType() {
        return entityType;
    }

    public boolean canPickUpLoot() {
        return true;
    }

    private boolean notHungry() {
        return getEatingCooldown() > SLIME_HUNGRY_THRESHOLD;
    }

    public boolean checkFoods(ItemStack pStack, List<Object> foods) {
        Item pickUpItem = pStack.getItem();
        for (Object food : foods) {
            if (food.getClass() == ItemStack.class && ((ItemStack) food).getItem() == pickUpItem) return true;
            if (food.getClass() == TagKey.class && pStack.is((TagKey<Item>) food)) return true;
        }
        return false;
    }

    public boolean isPrimaryFood(ItemStack food) {
        SlimeBreed slime = this.getSlime().get();
        return this.checkFoods(food, slime.foods());
    }

    public List<Object> getSlimeFoods(SlimeBreed slime) {
        if (!this.isLargo() || slime.traits().contains("dominant")) return slime.foods();
        DynamicHolder<SlimeBreed> secondaryBreed = getSecondarySlime();
        if (secondaryBreed.isBound()) {
            if (secondaryBreed.get().traits().contains("recessive")) return slime.foods();
            else if (slime.traits().contains("recessive") || secondaryBreed.get().traits().contains("dominant"))
                return secondaryBreed.get().foods();

            List<Object> combined = new ArrayList<>(slime.foods());
            combined.addAll(secondaryBreed.get().foods());
            return combined;
        }
        return slime.foods();
    }

    public boolean wantsToPickUp(ItemStack pStack) {
        Item pickUpItem = pStack.getItem();
        if (notHungry() && pickUpItem != ModElements.Items.PLORT.get()) return false;
        if (pickUpItem == ModElements.Items.SLIME_CANDY.get()) return true;
        if (!getSlime().isBound()) return false;
        if (pickUpItem instanceof SpawnEggItem) return false;
        SlimeBreed slime = getSlime().get();
        if (pickUpItem == ModElements.Items.PLORT.get() && pStack.hasTag() && !this.hasTrait("largoless")) {
            CompoundTag plortTag = pStack.getTagElement("plort");
            if (plortTag != null && plortTag.contains("id")) {
                String id = plortTag.get("id").toString();
                if (SlimeData.plortIsFromLargoless(id)) return false;
                return !id.contains(this.getSlimeBreed()) && !(!this.getSlimeSecondaryBreed().isEmpty() && id.contains(this.getSlimeSecondaryBreed()));
            }
        }
        return checkFoods(pStack, getSlimeFoods(slime));
    }

    public boolean isOwnerOnline() {
        if (!this.getTamed()) return true;
        return (this.level().getPlayerByUUID(this.getOwnerUUID()) != null);
    }

    public ItemStack getSlimePlort() {
        return getSlimePlort(false);
    }

    public ItemStack getSlimePlort(Boolean secondary) {
        ItemStack plort = new ItemStack(ModElements.Items.PLORT.get());
        if (secondary) {
            plort.getOrCreateTagElement("plort").putString(ID, getSlimeSecondaryBreed());
        } else {
            plort.getOrCreateTagElement("plort").putString(ID, getSlimeBreed());
        }
        return plort;
    }

    public List<EntityType<? extends LivingEntity>> getEdibleMobs() {
        DynamicHolder<SlimeBreed> slime = getSlime();
        if (!slime.isBound()) return null;
        DynamicHolder<SlimeBreed> secondarySlime = getSecondarySlime();
        if (secondarySlime.isBound()) {
            List<EntityType<? extends LivingEntity>> edibleMobs = new ArrayList<>();
            edibleMobs.addAll(slime.get().entities());
            edibleMobs.addAll(secondarySlime.get().entities());
            return edibleMobs;
        }
        return slime.get().entities();
    }

    public List<EntityType<? extends LivingEntity>> getHostileToMobs() {
        DynamicHolder<SlimeBreed> slime = getSlime();
        if (!slime.isBound()) return null;
        DynamicHolder<SlimeBreed> secondarySlime = getSecondarySlime();
        if (secondarySlime.isBound()) {
            List<EntityType<? extends LivingEntity>> hostileTo = new ArrayList<>();
            hostileTo.addAll(slime.get().hostileToEntities());
            hostileTo.addAll(secondarySlime.get().hostileToEntities());
            return hostileTo;
        }
        return slime.get().hostileToEntities();
    }

    public Boolean hasSameBreed(LivingEntity livingEntity) {
        if (livingEntity.getType() == this.getType()) {
            SynchedEntityData entityData = livingEntity.getEntityData();
            if (entityData.get(BREED).equals(this.getSlimeBreed())) return true;
            return entityData.get(SECONDARY_BREED).equals(this.getSlimeBreed());
        }
        return false;
    }

    public boolean isFavoriteFood(Item item) {
        boolean favorite;
        DynamicHolder<SlimeBreed> slime = getSlime();
        if (!slime.isBound()) return false;
        favorite = item == slime.get().favoriteFood().getItem();
        DynamicHolder<SlimeBreed> secondarySlime = getSecondarySlime();
        if (!favorite && secondarySlime.isBound()) favorite = item == secondarySlime.get().favoriteFood().getItem();
        return favorite;
    }

    public boolean getFavoriteEntity(EntityType<? extends LivingEntity> entityType) {
        boolean favorite;
        DynamicHolder<SlimeBreed> slime = getSlime();
        if (!slime.isBound()) return false;
        favorite = entityType == slime.get().favoriteEntity();
        DynamicHolder<SlimeBreed> secondarySlime = getSecondarySlime();
        if (!favorite && secondarySlime.isBound()) favorite = entityType == secondarySlime.get().favoriteEntity();
        return favorite;
    }

    public void setTarred() {
        this.tarred = true;
    }

    public boolean getTarred() {
        return this.tarred;
    }

    public void setItemTarget(ItemEntity entity) {
        this.itemTarget = entity;
    }

    public ItemEntity getItemTarget() {
        return this.itemTarget;
    }

    public int getHappiness() {
        return this.entityData.get(HAPPINESS);
    }

    public void addHappiness(int data) {
        if (this.hasTrait("moody")) this.setHappiness(this.getHappiness() + (data * 2));
        else this.setHappiness(this.getHappiness() + data);
    }

    public void setHappiness(int data) {
        this.entityData.set(HAPPINESS, data);
    }

    public String getTargetEntity() {
        return this.entityData.get(TARGET_ENTITY);
    }

    public void setTargetEntity(String data) {
        this.entityData.set(TARGET_ENTITY, data);
    }

    public int getEatingCooldown() {
        return this.entityData.get(EATING_COOLDOWN);
    }

    public void setEatingCooldown(int data) {
        this.entityData.set(EATING_COOLDOWN, data);
    }

    public int getParticleAnimationTick() {
        return this.entityData.get(PARTICLE_ANIMATION_TICK);
    }

    public int getLastAte() {
        return this.entityData.get(LAST_ATE);
    }

    public void setLastAte(int data) {
        this.entityData.set(LAST_ATE, data);
    }

    public void setParticleAnimationTick(int data) {
        this.entityData.set(PARTICLE_ANIMATION_TICK, data);
    }

    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse((UUID) null);
    }

    public void setOwnerUUID(@Nullable UUID pUuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(pUuid));
    }

    public boolean getTamed() {
        return this.entityData.get(TAMED);
    }

    public void setTamed(boolean tamed) {
        this.entityData.set(TAMED, tamed);
    }

    public boolean isInvulnerableTo(DamageSource pSource) {
        if (pSource.is(DamageTypes.EXPLOSION) || pSource.is(DamageTypes.PLAYER_EXPLOSION)) return true;
        if ((pSource.is(DamageTypes.ON_FIRE) || pSource.is(DamageTypes.IN_FIRE)) && (hasTrait("flaming") || hasTrait("aquatic")))
            return true;
        return super.isInvulnerableTo(pSource);
    }

    public boolean hasTrait(String trait) {
        DynamicHolder<SlimeBreed> slime = this.getSlime();
        DynamicHolder<SlimeBreed> secondarySlime = this.getSecondarySlime();
        if (slime.isBound()) {
            List<String> traits = slime.get().traits();
            if (traits.contains(trait)) {
                return true;
            }
            if (secondarySlime.isBound()) {
                List<String> secondarySlimeTraits = secondarySlime.get().traits();
                return secondarySlimeTraits.contains(trait);
            }
        }
        return false;
    }

    public void push(Entity pEntity) {
        super.push(pEntity);
        if (this.hasTrait("flaming")) {
            if (!(pEntity instanceof SplendidSlime && ((SplendidSlime) pEntity).hasTrait("flaming")))
                pEntity.setSecondsOnFire(3);
        }
        if (notHungry()) return;
        List<EntityType<? extends LivingEntity>> edibleMobs = getEdibleMobs();
        if (edibleMobs == null) return;
        for (EntityType mobType : edibleMobs) {
            if (pEntity.getType() == mobType) {
                this.dealDamage((LivingEntity) pEntity);
                applyNegativeEffects(this, (LivingEntity) pEntity, false);
            }
        }
    }

    @Override
    public boolean killedEntity(ServerLevel pLevel, LivingEntity pEntity) {
        if (notHungry()) return true;
        List<EntityType<? extends LivingEntity>> edibleMobs = getEdibleMobs();
        if (edibleMobs == null) return true;
        for (EntityType mobType : edibleMobs) {
            if (pEntity.getType() == mobType) {
                handleFeed(getFavoriteEntity((EntityType<? extends LivingEntity>) pEntity.getType()), null);
            }
        }
        return true;
    }

    public void playerTouch(Player pEntity) {
        if (pEntity.isHolding(ModElements.Items.SLIME_VAC.get()) && pEntity.isCrouching()) {
            if (SlimeVac.playerCanPickupSlime(pEntity) && this.getSlimeSecondaryBreed().isEmpty()) {
                this.pickupSlime(pEntity);
            }
        }
        if (this.isDealsDamage() && (this.getHappiness() <= FURIOUS_THRESHOLD || this.hasTrait("spiky") || this.hasTrait("feral"))) {
            applyNegativeEffects(this, pEntity, false);
            super.playerTouch(pEntity);
        }
        if (this.isDealsDamage() && this.hasTrait("flaming")) {
            pEntity.setSecondsOnFire(3);
        }
    }

    private void handleSlimeCandy() {
        int happiness = this.getHappiness();
        int newHappiness;
        if (happiness < FURIOUS_THRESHOLD) newHappiness = UNHAPPY_THRESHOLD;
        else if (happiness < UNHAPPY_THRESHOLD) newHappiness = HAPPY_THRESHOLD;
        else newHappiness = MAX_HAPPINESS;
        this.setHappiness(newHappiness);
    }

    public void handleFeed(boolean isFavorite, ItemStack food) {
        int happiness = getHappiness();
        int happinessIncrease = isFavorite ? 50 : 25;
        boolean displayAngerParticles = false;
        if (food != null && food.is(ModElements.Items.SLIME_CANDY.get())) {
            handleSlimeCandy();
            for (int i = 0; i < 8; i++) {
                double d0 = this.random.nextGaussian() * 0.04D;
                double d1 = this.random.nextGaussian() * 0.04D;
                double d2 = this.random.nextGaussian() * 0.04D;
                this.getServer().getLevel(this.level().dimension()).sendParticles(ParticleTypes.NOTE, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 1, d0, d1, d2, 0.2);
            }
        }
        if (happiness > FURIOUS_THRESHOLD) {
            ItemStack dropOne = getSlimePlort();
            int size = this.getSize();
            if (size >= 2) {
                if (isFavorite) dropOne.setCount(2);
                this.spawnAtLocation(dropOne);
                if (!this.getSlimeSecondaryBreed().isEmpty()) {
                    ItemStack dropTwo = getSlimePlort(true);
                    if (isFavorite) dropTwo.setCount(2);
                    this.spawnAtLocation(dropTwo);
                    if (size == 2) {
                        this.moveTo(this.getOnPos().above().getCenter());
                        this.setJumping(false);
                        this.setSize(size + 1, true);
                    }
                }
            } else {
                this.moveTo(this.getOnPos().above().getCenter());
                this.setJumping(false);
                this.setSize(size + 1, true);
            }
        }
        if (!this.getTamed()) {
            Player closestPlayer = null;
            List<Player> nearbyPlayers = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(effectRadius * 3));
            if (!nearbyPlayers.isEmpty()) {
                for (Player player : nearbyPlayers) {
                    if (closestPlayer == null) {
                        closestPlayer = player;
                    } else if (player.distanceTo(this) < closestPlayer.distanceTo(this)) {
                        closestPlayer = player;
                    }
                }
                this.setOwnerUUID(closestPlayer.getUUID());
                this.setTamed(true);
                this.setPersistenceRequired();
            }
        }
        if (isFavorite) {
            for (int i = 0; i < 4; i++) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.getServer().getLevel(this.level().dimension()).sendParticles(ParticleTypes.NOTE, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 1, d0, d1, d2, 0.2);
            }
        }
        int nearbyFriends = SlimeComfortUtils.getNearbyFriends(this).size();
        if (nearbyFriends >= 3) happinessIncrease += 15;
        if (nearbyFriends > 5) happinessIncrease += 15;
        if (SlimeComfortUtils.slimeIsSuffocated(this) || nearbyFriends > 8) {
            displayAngerParticles = true;
            happinessIncrease -= 120;

        }
        if (SlimeComfortUtils.diverseTraitCheck(this)) {
            displayAngerParticles = true;
            addHappiness(-40);
        }
        if (SlimeComfortUtils.photosynthesizingTraitCheck(this)) {
            displayAngerParticles = true;
            addHappiness(-40);
        }
        if (this.hasTrait("picky") && this.isLargo()) {
            if (food != null) {
                boolean isPrimary = this.isPrimaryFood(food);
                if (this.getLastAte() == 0 && isPrimary || this.getLastAte() == 1 && !isPrimary) {
                    displayAngerParticles = true;
                    happinessIncrease = -60;
                } else {
                    if (this.getLastAte() == 1) this.setLastAte(0);
                    else this.setLastAte(1);
                }
            }
        }
        if (displayAngerParticles) {
            for (int i = 0; i < 4; i++) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.getServer().getLevel(this.level().dimension()).sendParticles(ParticleTypes.ANGRY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 1, d0, d1, d2, 0.2);
            }
        }
        this.heal(2);
        this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 0.9F);
        addHappiness(happinessIncrease);
        setEatingCooldown(SLIME_STARVING_COOLDOWN);
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        ItemStack spawnEggStack = ModElements.Items.SPLENDID_SLIME_SPAWN_EGG.get().getDefaultInstance();
        spawnEggStack.getOrCreateTagElement("EntityTag").putString("Breed", this.getSlimeBreed());
        return spawnEggStack;
    }

    @Override
    protected ParticleOptions getParticleType() {
        ItemStack particleItem = getSlimePlort();
        DynamicHolder<SlimeBreed> slime = getSlime();
        if (slime.isBound() && slime.get().particle().getItem() != Items.AIR) {
            particleItem = slime.get().particle();
        }

        return new ItemParticleOption(ParticleTypes.ITEM, particleItem);

    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        ItemStack item = itemEntity.getItem();
        boolean atePlort = false;
        if (wantsToPickUp(item)) {
            if (this.getHappiness() > FURIOUS_THRESHOLD && item.getItem() == ModElements.Items.PLORT.get() && item.hasTag()) {
                CompoundTag plortTag = item.getTagElement("plort");
                if (plortTag != null && plortTag.contains("id")) {
                    atePlort = true;
                    if (this.getSlimeSecondaryBreed().isEmpty()) {
                        if (this.getSize() < 4) {
                            this.moveTo(this.getOnPos().above().getCenter());
                            this.setJumping(false);
                            this.setSize(this.getSize() + 1, false);
                        }
                        ;
                        this.playSound(SoundEvents.AMETHYST_BLOCK_STEP, 1.0F, 0.9F);
                        this.setSlimeSecondaryBreed(plortTag.get("id").toString().replace("\"", ""));
                        DynamicHolder<SlimeBreed> secondaryBreed = this.getSecondarySlime();
                        if (secondaryBreed.isBound())
                            applyEffects(this, this, secondaryBreed.get().innateEffects(), false);
                    } else {
                        causeChaos();
                    }
                }
            }
            if (!atePlort) handleFeed(this.isFavoriteFood(item.getItem()), item);
            item.setCount(item.getCount() - 1);
            itemEntity.setItem(item);
        }
    }

    public void causeChaos() {
        this.setTarred();
        this.kill();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.hasTrait("defiant")) {
            if (pSource.is(DamageTypes.GENERIC_KILL)) return super.hurt(pSource, pAmount);
            if (pAmount <= 1) return super.hurt(pSource, pAmount);
            double chance = 1 - (this.getHealth() / pAmount);
            if (this.random.nextFloat() < chance) {
                this.playSound(SoundEvents.TOTEM_USE, 0.4F, 1.1F);
                this.heal(pAmount);
                this.level().broadcastEntityEvent(this, (byte) 4);
                return false;
            }
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getMainHandItem();
        if (!itemstack.isEmpty()) {
            if (itemstack.getItem() instanceof SlimeInspector slimeInspector) {
                slimeInspector.runInspection(pPlayer, this);
                return InteractionResult.SUCCESS;
            } else if (this.hasTrait("handy")){
                ItemStack itemstack1 = this.equipItemIfPossible(itemstack.copy());
                if (!itemstack1.isEmpty()) {
                    itemstack.shrink(itemstack1.getCount());
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void remove(RemovalReason pReason) {
        int size = this.getSize();

        if (!this.level().isClientSide && this.isDeadOrDying()) {
            if (!SlimyConfig.enableTarrs || !this.getTarred()) {
                if (size > 1) {
                    Component component = this.getCustomName();
                    boolean flag = this.isNoAi();
                    SplendidSlime slime = (SplendidSlime) this.getType().create(this.level());
                    if (slime != null) {
                        if (this.isPersistenceRequired()) {
                            slime.setPersistenceRequired();
                        }
                        String secondaryBreed = this.getSlimeSecondaryBreed();
                        slime.setSlimeBreed(this.getSlimeBreed());
                        if (!secondaryBreed.isEmpty()) slime.setSlimeSecondaryBreed(secondaryBreed);
                        slime.setCustomName(component);
                        slime.setNoAi(flag);
                        slime.setInvulnerable(this.isInvulnerable());
                        slime.setSize(size - 1, true);
                        slime.setOwnerUUID(this.getOwnerUUID());
                        slime.setTamed(this.getTamed());
                        slime.setHappiness(this.getHappiness() - 100);
                        slime.setEatingCooldown(this.getEatingCooldown());
                        slime.moveTo(this.getX(), this.getY() + (double) 0.5F, this.getZ(), this.random.nextFloat() * 360.0F, 0.0F);
                        this.level().addFreshEntity(slime);
                    }
                }
            } else {
                Component component = this.getCustomName();
                int victimSize = this.getSize();
                if (victimSize > 0) victimSize--;
                SlimeEntityBase tarr = ModElements.Entities.TARR.get().create(this.level());
                tarr.setCustomName(component);
                tarr.setInvulnerable(this.isInvulnerable());
                tarr.setSize(victimSize + 1, true);
                tarr.moveTo(this.getX(), this.getY() + (double) 0.5F, this.getZ(), this.random.nextFloat() * 360.0F, 0.0F);
                this.level().addFreshEntity(tarr);
            }
        }
        this.setRemoved(pReason);
        this.invalidateCaps();
        this.brain.clearMemories();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAPPINESS, 500);
        this.entityData.define(LAST_ATE, 0);
        this.entityData.define(EATING_COOLDOWN, 0);
        this.entityData.define(TARGET_ENTITY, "");
        this.entityData.define(PARTICLE_ANIMATION_TICK, -1);
        this.entityData.define(TAMED, false);
        this.entityData.define(OWNER_UUID, Optional.empty());
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag nbt) {
        String secondaryBreed = nbt.getString("SecondaryBreed");
        if (this.hasTrait("largoless")) setSlimeSecondaryBreed("");
        if (!secondaryBreed.isEmpty() && plortIsFromLargoless(secondaryBreed)) setSlimeSecondaryBreed("");
        setEatingCooldown(nbt.getInt("EatingCooldown"));
        setLastAte(nbt.getInt("LastAte"));
        setHappiness(nbt.getInt("Happiness"));
        setTargetEntity(nbt.getString("TargetEntity"));
        setParticleAnimationTick(nbt.getInt("ParticleAnimationTick"));
        UUID uuid;
        if (nbt.hasUUID("Owner")) {
            uuid = nbt.getUUID("Owner");
        } else {
            String s = nbt.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }
        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
                this.setTamed(true);
            } catch (Throwable throwable) {
                this.setTamed(false);
            }
        }
        super.readAdditionalSaveData(nbt);
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("EatingCooldown", this.getEatingCooldown());
        nbt.putInt("LastAte", this.getLastAte());
        nbt.putInt("Happiness", this.getHappiness());
        nbt.putString("TargetEntity", this.getTargetEntity());
        nbt.putInt("ParticleAnimationTick", this.getParticleAnimationTick());
        if (this.getOwnerUUID() != null) {
            nbt.putUUID("Owner", this.getOwnerUUID());
        }
        nbt.putBoolean("Tamed", this.getTamed());
    }

    static class SlimeTargetItemGoal extends Goal {
        private final SplendidSlime slime;
        private ItemEntity targetItem;
        protected final int randomInterval;

        public SlimeTargetItemGoal(SplendidSlime slime, int pRandomInterval) {
            this.slime = slime;
            this.randomInterval = reducedTickDelay(pRandomInterval);
        }

        protected void findTarget() {
            List<ItemEntity> nearbyItems = this.slime.level().getEntitiesOfClass(ItemEntity.class, this.slime.getBoundingBox().inflate(10));
            if (nearbyItems.isEmpty()) return;
            ItemEntity target = null;
            for (ItemEntity potentialTarget : nearbyItems) {
                if (target == null || this.slime.distanceToSqr(potentialTarget) < this.slime.distanceToSqr(target)) {
                    if (slime.wantsToPickUp(potentialTarget.getItem())) target = potentialTarget;
                }
            }
            this.targetItem = target;
            this.slime.setItemTarget(target);
        }


        @Override
        public void tick() {
            if (this.targetItem != null) {
                this.slime.lookAt(targetItem, 10.0F, 10.0F);
                MoveControl movecontrol = this.slime.getMoveControl();
                if (movecontrol instanceof SlimeMoveControl slime$slimemovecontrol) {
                    slime$slimemovecontrol.setDirection(this.slime.getYRot(), true);
                }
            }

        }

        @Override
        public boolean canContinueToUse() {
            if (!targetItem.isAlive()) targetItem = null;
            if (this.slime.notHungry()) {
                targetItem = null;
                return false;
            }
            return targetItem != null && targetItem.isAlive();
        }

        @Override
        public boolean canUse() {
            if (this.slime.notHungry()) return false;
            if (this.randomInterval > 0 && this.slime.getRandom().nextInt(this.randomInterval) != 0) {
                return false;
            } else {
                this.findTarget();
                return this.targetItem != null;
            }
        }

        @Override
        public void start() {
            if (this.slime.hasTrait("foodporting")) {
                this.slime.setPos(targetItem.getX(), targetItem.getY(), targetItem.getZ());
                this.slime.playSound(SoundEvents.SHULKER_TELEPORT, 1.0F, 1.0F);
            }
            super.start();
        }

        @Override
        public void stop() {
            this.slime.setItemTarget(null);
            this.targetItem = null;
        }

    }

    public class SlimeAttackEntities<T extends LivingEntity> extends TargetGoal {
        protected LivingEntity target;
        protected TargetingConditions targetConditions;
        protected final int randomInterval;

        public SlimeAttackEntities(Mob pMob, boolean pMustSee, int pRandomInterval, @Nullable Predicate<LivingEntity> pTargetPredicate) {
            super(pMob, pMustSee);
            this.randomInterval = reducedTickDelay(pRandomInterval);
            this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(pTargetPredicate);
        }

        protected void findTarget() {
            List<EntityType<? extends LivingEntity>> edibleMobs = getEdibleMobs();
            List<EntityType<? extends LivingEntity>> hostileToMobs = getHostileToMobs();
            if (edibleMobs == null && hostileToMobs == null) return;
            List<EntityType<? extends LivingEntity>> finalHostileToMobs = hostileToMobs;
            List<LivingEntity> nearbyEntities = this.mob.level().getEntitiesOfClass(LivingEntity.class, this.mob.getBoundingBox().inflate(10), e -> (!notHungry() && edibleMobs.contains(e.getType())) || finalHostileToMobs.contains(e.getType()) || (hasTrait("feral") && e.getType() == EntityType.PLAYER) || (hasTrait("friendly") && e.getType() == EntityType.PLAYER) || (((SplendidSlime) this.mob).getHappiness() < FURIOUS_THRESHOLD && e.getType() == EntityType.PLAYER));
            if (!nearbyEntities.isEmpty()) {
                LivingEntity targetEntity = nearbyEntities.get(0);
                for (LivingEntity potentialTarget : nearbyEntities) {
                    if (this.mob.distanceToSqr(potentialTarget) < this.mob.distanceToSqr(targetEntity)) {
                        targetEntity = potentialTarget;
                    }
                }
                if (hostileToMobs.contains(targetEntity.getType())) setTargetEntity(targetEntity.getUUID().toString());
                this.target = targetEntity;
            }
        }

        public boolean canUse() {
            if (notHungry() && getHostileToMobs().isEmpty()) return false;
            if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
                return false;
            } else {
                this.findTarget();
                return this.target != null;
            }
        }

        public void tick() {
            // Run attack commands every 30 seconds
            if (this.mob.tickCount % 600 == 0) {
                if (!getTargetEntity().equals("")) {
                    executeSlimeCommands((SplendidSlime) this.mob, false, true);
                }
            }
        }

        public void start() {
            if (((SplendidSlime) this.mob).hasTrait("foodporting")) {
                this.mob.setPos(target.getX(), target.getY(), target.getZ());
                this.mob.playSound(SoundEvents.SHULKER_TELEPORT, 1.0F, 1.0F);
            }
            this.mob.setTarget(this.target);
            super.start();
        }

        public void stop() {
            this.mob.setTarget((LivingEntity) null);
            setTargetEntity("");
            this.targetMob = null;
        }
    }

    static class SlimeAttackGoal extends Goal {
        private final SplendidSlime slime;
        private int growTiredTimer;

        public SlimeAttackGoal(SplendidSlime pSlime) {
            this.slime = pSlime;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity == null) {
                return false;
            } else {
                return !this.slime.canAttack(livingentity) ? false : this.slime.getMoveControl() instanceof SlimeMoveControl;
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
            } else if (!this.slime.canAttack(livingentity)) {
                return false;
            } else {
                return --this.growTiredTimer > 0;
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

            MoveControl movecontrol = this.slime.getMoveControl();
            if (movecontrol instanceof SlimeMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(this.slime.getYRot(), true);
            }

        }
    }

    static class SlimeFloatGoal extends Goal {
        private final SplendidSlime slime;

        public SlimeFloatGoal(SplendidSlime pSlime) {
            this.slime = pSlime;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
            pSlime.getNavigation().setCanFloat(true);
        }

        public boolean canUse() {
            return this.slime.getNavigation().canFloat() && (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof SlimeMoveControl;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.slime.getNavigation().canFloat()) {
                if (this.slime.getRandom().nextFloat() < 0.8F) {
                    this.slime.getJumpControl().jump();
                }

                MoveControl movecontrol = this.slime.getMoveControl();
                if (movecontrol instanceof SlimeMoveControl slime$slimemovecontrol) {
                    slime$slimemovecontrol.setWantedMovement(1.2D);
                }
                if (slime.tickCount % 40 == 0 && slime.hasTrait("aquatic")) {
                    this.slime.getNavigation().setCanFloat(false);
                }
            }
        }
    }

    static class SlimeKeepOnJumpingGoal extends Goal {
        private final SplendidSlime slime;

        public SlimeKeepOnJumpingGoal(SplendidSlime pSlime) {
            this.slime = pSlime;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        public boolean canUse() {
            return !this.slime.isPassenger();
        }

        public void tick() {
            MoveControl movecontrol = this.slime.getMoveControl();
            if (movecontrol instanceof SlimeMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setWantedMovement(1.0D);
            }

        }
    }

    static class SlimeMoveControl extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private final SplendidSlime slime;
        private boolean isAggressive;

        public SlimeMoveControl(SplendidSlime pSlime) {
            super(pSlime);
            this.slime = pSlime;
            this.yRot = 180.0F * pSlime.getYRot() / (float) Math.PI;
        }

        public void setDirection(float pYRot, boolean pAggressive) {
            this.yRot = pYRot;
            this.isAggressive = pAggressive;
        }

        public void setWantedMovement(double pSpeed) {
            this.speedModifier = pSpeed;
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
                        this.jumpDelay = this.slime.getRandom().nextInt(20) + 10;
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }

                        this.slime.getJumpControl().jump();
//                        if (this.slime.doPlayJumpSound()) {
//                            this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
//                        }
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
        private final SplendidSlime slime;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public SlimeRandomDirectionGoal(SplendidSlime pSlime) {
            this.slime = pSlime;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            return this.slime.getItemTarget() == null && this.slime.getTarget() == null && (this.slime.onGround() || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION)) && this.slime.getMoveControl() instanceof SlimeMoveControl;
        }

        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = this.adjustedTickDelay(40 + this.slime.getRandom().nextInt(60));
                this.chosenDegrees = (float) this.slime.getRandom().nextInt(360);
            }

            MoveControl movecontrol = this.slime.getMoveControl();
            if (movecontrol instanceof SlimeMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(this.chosenDegrees, false);
            }

        }
    }
}
