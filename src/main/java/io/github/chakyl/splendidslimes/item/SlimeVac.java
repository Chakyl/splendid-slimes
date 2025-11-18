package io.github.chakyl.splendidslimes.item;

import io.github.chakyl.splendidslimes.entity.SplendidSlime;
import io.github.chakyl.splendidslimes.item.ItemProjectile.ItemProjectileEntity;
import io.github.chakyl.splendidslimes.registry.ModElements;
import io.github.chakyl.splendidslimes.tag.SplendidSlimesItemTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SlimeVac extends Item {
    public static final String NBT_MODE = "Mode";
    private static final int RANGE = 10;
    private static final double ANGLE = Math.cos(Math.PI / 4F);//Pre-calc cosine for speed

    public SlimeVac(Properties pProperties) {
        super(pProperties);
    }

    public static VacMode getMode(ItemStack stack) {
        if (stack.hasTag()) {
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

    // References: Crossroads Vacuum, Create Potato Cannon
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack handStack = player.getItemInHand(hand);
        if (player.isCrouching()) {
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
                entity.push(motVec.x, motVec.y + 0.2, motVec.z);
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

            ItemStack itemStackToLaunch = player.getItemInHand(inverseHand);

            boolean slimeFired = false;
            if (itemStackToLaunch != ItemStack.EMPTY && itemStackToLaunch.is(SplendidSlimesItemTags.SLIME_VAC_FIREABLE)) {
                Entity projectile;
                Item itemToLaunch = itemStackToLaunch.getItem();
                Vec3 barrelPos = getShootLocVec(player, hand == InteractionHand.MAIN_HAND,
                        new Vec3(.45f, -0.5f, 1.0f));
                if (itemToLaunch == ModElements.Items.SLIME_ITEM.get()) {
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
                level.addFreshEntity(projectile);
                projectile.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 0.9F);
                if (slimeFired) player.setItemInHand(inverseHand, ItemStack.EMPTY);
                else if (!player.isCreative()) player.getItemInHand(inverseHand).shrink(1);
                player.getCooldowns().addCooldown(ModElements.Items.SLIME_VAC.get(), 4);

                return InteractionResultHolder.pass(handStack);
            }
            return InteractionResultHolder.pass(handStack);
        }
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