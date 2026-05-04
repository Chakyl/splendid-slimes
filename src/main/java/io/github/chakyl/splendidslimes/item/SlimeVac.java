package io.github.chakyl.splendidslimes.item;

import io.github.chakyl.splendidslimes.entity.SlimeEntityBase;
import io.github.chakyl.splendidslimes.entity.SplendidSlime;
import io.github.chakyl.splendidslimes.item.ItemProjectile.ItemProjectileEntity;
import io.github.chakyl.splendidslimes.registry.ModElements;
import io.github.chakyl.splendidslimes.tag.SplendidSlimesItemTags;
import io.github.chakyl.splendidslimes.util.SlimeVacUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SlimeVac extends Item {
    public static final String NBT_MODE = "Mode";
    private static final int RANGE = 10;
    private static final double ANGLE = Math.cos(Math.PI / 4F); //Pre-calc cosine for speed

    public SlimeVac(Properties pProperties) {
        super(pProperties);
    }

    public static VacMode getMode(ItemStack stack) {
        if (stack.hasTag()) {
            String stringMode = stack.getOrCreateTag().getString(NBT_MODE);
            if (stringMode.isEmpty()) return VacMode.BOTH;
            return VacMode.valueOf(stack.getOrCreateTag().getString(NBT_MODE));
        }
        return VacMode.BOTH;
    }

    public static void setMode(ItemStack stack, VacMode mode) {
        stack.getOrCreateTag().putString(NBT_MODE, mode.name());
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        super.onCraftedBy(pStack, pLevel, pPlayer);
        initNbt(pStack);
    }

    private ItemStack initNbt(ItemStack stack) {
        stack.getOrCreateTag().putString(NBT_MODE, VacMode.BOTH.name());
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> list, TooltipFlag pFlag) {
        list.add(Component.translatable("info.splendid_slimes.slime_vac").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("info.splendid_slimes.slime_vac.mode", getMode(pStack).name()));
    }

    private void suckParticles(Level level, Player player) {
        if (!level.isClientSide) {
            Vec3 startPos = player.getEyePosition().add(0f, -0.2f, -0.5f);
            Vec3 lookDirection = player.getViewVector(1.0F);

            Vec3 endPos = startPos.add(lookDirection.x * RANGE, lookDirection.y * RANGE, lookDirection.z * RANGE);

            HitResult rayTraceResult = level.clip(new ClipContext(startPos, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

            if (rayTraceResult instanceof BlockHitResult blockHit) {
                endPos = blockHit.getLocation();
            }

            for (int i = 0; i < RANGE * 2; i++) {
                double t = (double) i / RANGE * 2;
                double x = startPos.x + (endPos.x - startPos.x) * t;
                double y = startPos.y + (endPos.y - startPos.y) * t;
                double z = startPos.z + (endPos.z - startPos.z) * t;
                ((ServerLevel) level).sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0, 0, 0, 0.01);
            }
        }
    }

    public static boolean playerCanPickupSlime(Player player) {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!stack.is(ModElements.Items.SLIME_VAC.get())) stack = player.getItemInHand(InteractionHand.OFF_HAND);
        return getMode(stack) != VacMode.ITEM;
    }

    public static void removeLargoTag(Player player) {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!stack.is(ModElements.Items.SLIME_VAC.get())) stack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (!stack.is(ModElements.Items.SLIME_VAC.get())) return;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("largo")) {
            tag.remove("largo");
        }
    }

    public static boolean hasLargo(Player player) {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!stack.is(ModElements.Items.SLIME_VAC.get())) stack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (!stack.is(ModElements.Items.SLIME_VAC.get())) return false;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("largo")) {
            return !tag.getCompound("largo").isEmpty();
        }
        return false;
    }

    // References: Crossroads Vacuum, Create Potato Cannon
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack handStack = player.getItemInHand(hand);

        boolean hasLargo = hasLargo(player);
        if (player.isCrouching()) {
            if (hasLargo) return InteractionResultHolder.pass(handStack);
//            suckParticles(level, player);
            Class entityClass = SplendidSlime.class;
            if (getMode(handStack) == VacMode.ITEM) entityClass = ItemEntity.class;
            ArrayList<Entity> entities = (ArrayList<Entity>) level.getEntitiesOfClass(entityClass, new AABB(player.getX(), player.getY(), player.getZ(), player.getX(), player.getY(), player.getZ()).inflate(RANGE), EntitySelector.ENTITY_STILL_ALIVE);

            if (getMode(handStack) == VacMode.BOTH) {
                entities.addAll(level.getEntitiesOfClass(ItemEntity.class, new AABB(player.getX(), player.getY(), player.getZ(), player.getX(), player.getY(), player.getZ()).inflate(RANGE), EntitySelector.ENTITY_STILL_ALIVE));
            }
            Vec3 look = player.getLookAngle().scale(RANGE);
            Vec3 playPos = player.position();
            entities.removeIf((Entity e) -> {
                Vec3 ePos = e.position().subtract(playPos);
                return ePos.length() >= RANGE || ePos.dot(look) / (ePos.length() * look.length()) <= ANGLE;
            });

            for (Entity entity : entities) {
                Vec3 motVec = player.position().subtract(entity.position()).scale(0.25D);
                entity.push(motVec.x, motVec.y + 0.1, motVec.z);
            }

            return InteractionResultHolder.pass(handStack);
        } else {
            if (level.isClientSide) {
                return InteractionResultHolder.pass(handStack);
            }
            Vec3 lookVec = player.getLookAngle();
            Vec3 motion = lookVec.normalize()
                    .scale(2)
                    .scale(1.5f);
            InteractionHand inverseHand = InteractionHand.OFF_HAND;
            if (hand == InteractionHand.OFF_HAND) {
                inverseHand = InteractionHand.MAIN_HAND;
            }

            ItemStack itemStackToLaunch = findFireableItem(player);

            boolean slimeFired = false;
            if (hasLargo || itemStackToLaunch != ItemStack.EMPTY && itemStackToLaunch.is(SplendidSlimesItemTags.SLIME_VAC_FIREABLE)) {
                Entity projectile;
                Item itemToLaunch = itemStackToLaunch.getItem();
                Vec3 barrelPos = getShootLocVec(player, hand == InteractionHand.MAIN_HAND,
                        new Vec3(.45f, -0.5f, 1.0f));
                if (hasLargo) {
                    projectile = SlimeVacUtils.getVacSlime(player);
                    projectile.setDeltaMovement(0, 0, 0);
                    slimeFired = true;
                } else if (itemToLaunch == ModElements.Items.SLIME_ITEM.get()) {
                    projectile = SlimeInventoryItem.getSlimeFromItem(itemStackToLaunch.getTag().getCompound("entity"), itemStackToLaunch.getTag().getCompound("slime"), level);
                    projectile.setDeltaMovement(0, 0, 0);
                    slimeFired = true;
                } else if (itemToLaunch == Items.ARROW.asItem()) {
                    projectile = EntityType.ARROW.create(level);
                    ((Arrow) projectile).setOwner(player);
                } else if (itemToLaunch == Items.TNT.asItem()) {
                    projectile = EntityType.TNT.create(level);
                } else {
                    projectile = new ItemProjectileEntity(ModElements.Entities.ITEM_PROJECTILE.get(), level);
                    ((ItemProjectileEntity) projectile).setItem(itemStackToLaunch.copy());
                    barrelPos = getShootLocVec(player, hand == InteractionHand.MAIN_HAND,
                            new Vec3(.65f, -1.0f, 2.0f));
                }
                if (projectile == null) return InteractionResultHolder.pass(handStack);
                Vec3 splitMotion = motion;

                projectile.setPos(barrelPos.x, barrelPos.y, barrelPos.z);
                projectile.setDeltaMovement(splitMotion);
                if (projectile instanceof SlimeEntityBase) {
                    BlockPos targetPos = projectile.getOnPos();
                    while (!level.getBlockState(targetPos).isAir() && targetPos.getY() < level.getMaxBuildHeight()) {
                        targetPos = targetPos.above();
                    }
                    projectile.moveTo(targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D, 0.0F, 0.0F);

                }
                level.addFreshEntity(projectile);
                projectile.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 0.9F);
                if (slimeFired) {
                    if (hasLargo) {
                        removeLargoTag(player);
                    } else itemStackToLaunch.shrink(1);
                }
                else if (!player.isCreative()) itemStackToLaunch.shrink(1);
                player.getCooldowns().addCooldown(ModElements.Items.SLIME_VAC.get(), 4);

                return InteractionResultHolder.pass(handStack);
            }
            return InteractionResultHolder.pass(handStack);
        }
    }

    public ItemStack findFireableItem(Player player) {
        ItemStack fireable = ItemStack.EMPTY;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (stack.isEmpty()) continue;
            if (stack.is(ModElements.Items.SLIME_ITEM.get())) {
                return stack;
            }
            if (fireable.isEmpty() && stack.is(SplendidSlimesItemTags.SLIME_VAC_FIREABLE)) fireable = stack;
        }
        return fireable;
    }
    public static Vec3 getShootLocVec(Player player, boolean mainHand, Vec3 rightHandForward) {
        Vec3 start = player.position()
                .add(0, player.getEyeHeight(), 0);
        float yaw = (float) ((player.getYRot()) / -180 * Math.PI);
        float pitch = (float) ((player.getXRot()) / -180 * Math.PI);
        int flip = mainHand == (player.getMainArm() == HumanoidArm.RIGHT) ? -1 : 1;
        Vec3 barrelPosNoTransform = new Vec3(flip * rightHandForward.x, rightHandForward.y, rightHandForward.z);
        Vec3 barrelPos = start.add(barrelPosNoTransform.xRot(pitch)
                .yRot(yaw));
        return barrelPos;
    }

    public enum VacMode {
        ITEM,
        SLIME,
        BOTH;

        VacMode() {
        }
    }

}