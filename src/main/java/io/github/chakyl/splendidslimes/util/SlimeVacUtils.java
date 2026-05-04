package io.github.chakyl.splendidslimes.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.chakyl.splendidslimes.item.SlimeInventoryItem;
import io.github.chakyl.splendidslimes.item.SlimeVac;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class SlimeVacUtils {

    public static Entity getVacSlime(Player player) {
        if (!SlimeVac.hasLargo(player)) return null;
        CompoundTag slime = player.getItemInHand(InteractionHand.MAIN_HAND).getTag().getCompound("largo");
        if (slime.isEmpty()) return null;
        Entity entity = SlimeInventoryItem.getSlimeFromItem(slime.getCompound("entity"), slime.getCompound("slime"), player.level());

        return entity;
    }

    @SuppressWarnings("resource")
    public static int getPerspective() {
        boolean isThirdPerson = !Minecraft.getInstance().options.getCameraType().isFirstPerson(); // isThirdPerson
        boolean isThirdPersonReverse = Minecraft.getInstance().options.getCameraType().isMirrored();

        if (!isThirdPerson && !isThirdPersonReverse)
            return 0;
        if (isThirdPerson && !isThirdPersonReverse)
            return 1;
        return 2;
    }

    public static void applyEntityTransformations(Player player, float partialticks, PoseStack matrix, Entity entity) {
        int perspective = getPerspective();
        Pose pose = player.getPose();

        applyGeneralTransformations(player, partialticks, matrix);

        if (perspective == 2) matrix.translate(0, -1.6, 0.65);
        else matrix.translate(0, -1.6, -0.65);
        matrix.scale(1.666f, 1.666f, 1.666f);

        float height = entity.getBbHeight();
        float width = entity.getBbWidth();
        float multiplier = height * width;
        entity.yo = 0.0f;
        entity.yRotO = 0.0f;
        entity.setYHeadRot(0.0f);
        entity.xo = 0.0f;
        entity.xRotO = 0.0f;

        if (perspective == 2)
            matrix.mulPose(Axis.YP.rotationDegrees(180));

        matrix.scale((10 - multiplier) * 0.1f, (10 - multiplier) * 0.1f, (10 - multiplier) * 0.1f);
        matrix.translate(0.0, height / 2 + -(height / 2) + 1, width - 0.1 < 0.7 ? width - 0.1 + (0.7 - (width - 0.1)) : width - 0.1);

        if (pose == Pose.SWIMMING || pose == Pose.FALL_FLYING) {
            matrix.mulPose(Axis.XN.rotationDegrees(90));
            matrix.translate(0, -0.2 * height, 0);

            if (pose == Pose.FALL_FLYING)
                matrix.translate(0, 0, 0.2);
        }

    }

    public static Vec3 getExactPos(Entity entity, float partialticks) {
        return new Vec3(entity.xOld + (entity.getX() - entity.xOld) * partialticks, entity.yOld + (entity.getY() - entity.yOld) * partialticks, entity.zOld + (entity.getZ() - entity.zOld) * partialticks);
    }

    public static float getExactBodyRotationDegrees(LivingEntity entity, float partialticks) {
        if (entity.getVehicle() != null && entity.getVehicle() instanceof LivingEntity vehicle)
            if (vehicle instanceof Player player)
                return -(player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * partialticks);
            else
                return -(entity.yHeadRotO + (entity.yHeadRot - entity.yHeadRotO) * partialticks);
        else
            return -(entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialticks);
    }

    public static Quaternionf getExactBodyRotation(LivingEntity entity, float partialticks) {
        return Axis.YP.rotationDegrees(getExactBodyRotationDegrees(entity, partialticks));
    }


    public static void applyGeneralTransformations(Player player, float partialticks, PoseStack matrix) {
        int perspective = getPerspective();
        Quaternionf playerrot = getExactBodyRotation(player, partialticks);
        Vec3 playerpos = getExactPos(player, partialticks);
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 offset = playerpos.subtract(cameraPos);
        Pose pose = player.getPose();

        matrix.pushPose();
        matrix.translate(offset.x, offset.y, offset.z);

        if (perspective == 2)
            playerrot.mul(Axis.YP.rotationDegrees(180));
        matrix.mulPose(playerrot);

        matrix.pushPose();
        matrix.scale(0.6f, 0.6f, 0.6f);

        if (perspective == 2)
            matrix.translate(0, 0, -1.35);

        if (isSneaking(player)) {
            matrix.translate(0, -0.4, 0);
        }

        if (pose == Pose.SWIMMING) {
            float f = player.getSwimAmount(partialticks);
            float f3 = player.isInWater() ? -90.0F - player.xRotO : -90.0F;
            float f4 = Mth.lerp(f, 0.0F, f3);
            if (perspective == 2) {
                matrix.translate(0, 0, 1.35);
                matrix.mulPose(Axis.XP.rotationDegrees(f4));
            } else
                matrix.mulPose(Axis.XN.rotationDegrees(f4));

            matrix.translate(0, -1.5, -1.848);
            if (perspective == 2)
                matrix.translate(0, 0, 2.38);
        }

        if (pose == Pose.FALL_FLYING) {
            float f1 = player.getFallFlyingTicks() + partialticks;
            float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
            if (!player.isAutoSpinAttack()) {
                if (perspective == 2)
                    matrix.translate(0, 0, 1.35);

                if (perspective == 2)
                    matrix.mulPose(Axis.XP.rotationDegrees(f2 * (-90.0F - player.xRotO)));
                else
                    matrix.mulPose(Axis.XN.rotationDegrees(f2 * (-90.0F - player.xRotO)));
            }

            Vec3 viewVector = player.getViewVector(partialticks);
            Vec3 deltaMovement = player.getDeltaMovement();
            double d0 = deltaMovement.horizontalDistanceSqr();
            double d1 = deltaMovement.horizontalDistanceSqr();
            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (deltaMovement.x * viewVector.x + deltaMovement.z * viewVector.z) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = deltaMovement.x * viewVector.z - deltaMovement.z * viewVector.x;

                matrix.mulPose(Axis.YP.rotation((float) (Math.signum(d3) * Math.acos(d2))));
            }

            if (perspective != 2)
                matrix.translate(0, 0, -1.35);
            matrix.translate(0, -0.2, 0);
        }

        matrix.translate(0, 1.6, 0.65);
    }

    public static boolean isSneaking(Player player) {
        if (player.getAbilities().flying) return false;
        return player.isShiftKeyDown() || player.isCrouching();
    }
}
