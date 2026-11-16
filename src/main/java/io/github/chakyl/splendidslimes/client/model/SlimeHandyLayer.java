package io.github.chakyl.splendidslimes.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.chakyl.splendidslimes.entity.SlimeEntityBase;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SlimeHandyLayer<T extends Entity> extends RenderLayer<T, SlimeEntityModel<T>> {
    private final ItemInHandRenderer itemInHandRenderer;

    public SlimeHandyLayer(RenderLayerParent<T, SlimeEntityModel<T>> renderer, ItemInHandRenderer pItemInHandRenderer) {
        super(renderer);
        this.itemInHandRenderer = pItemInHandRenderer;
    }

    protected void scale(SlimeEntityBase slimeEntityBase, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.999F, 0.999F, 0.999F);
        poseStack.translate(0.0F, 0.001F, 0.0F);
        float slimeSize = (float) slimeEntityBase.getSize();
        slimeSize += 0.25F;
        float lerpy = Mth.lerp(partialTickTime, slimeEntityBase.oSquish, slimeEntityBase.squish) / (slimeSize * 0.5F + 1.0F);
        float cutter = 1.0F / (lerpy + 1.0F);
        poseStack.translate(0, 0, 0);
        poseStack.scale(cutter * slimeSize, 1.0F / cutter * slimeSize, cutter * slimeSize);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

        if (pLivingEntity.isAlive() && !pLivingEntity.isInvisible()) {
            ItemStack weapon = pLivingEntity.getWeaponItem();
            if (weapon == null || weapon.isEmpty()) {
                return;
            }
            if (!weapon.isEmpty()) {
                pPoseStack.pushPose();
                pPoseStack.translate(0.25, 1F, -0.25f);
                pPoseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                this.itemInHandRenderer.renderItem((LivingEntity) pLivingEntity, weapon, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, pPoseStack, pBuffer, pPackedLight);
                pPoseStack.popPose();
            }

        }
    }

}
