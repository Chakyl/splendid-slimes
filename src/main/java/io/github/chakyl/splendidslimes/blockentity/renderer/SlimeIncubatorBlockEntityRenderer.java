package io.github.chakyl.splendidslimes.blockentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.chakyl.splendidslimes.blockentity.SlimeIncubatorBlockEntity;
import io.github.chakyl.splendidslimes.entity.SlimeEntityBase;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class SlimeIncubatorBlockEntityRenderer implements BlockEntityRenderer<SlimeIncubatorBlockEntity> {
    private SlimeEntityBase slimeRender = null;

    public SlimeIncubatorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SlimeIncubatorBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.isIncubating()) {
            if (slimeRender == null) {
                slimeRender = ModElements.Entities.SPLENDID_SLIME.get().create(pBlockEntity.getLevel());
            }

            if (slimeRender != null) {
                slimeRender.setSlimeBreed(pBlockEntity.getSlimeType());
                pPoseStack.pushPose();
                pPoseStack.translate(0.5, 1.1, 0.5);
                pPoseStack.scale(0.5f, 0.5f, 0.5f);
                float time = pBlockEntity.getLevel().getGameTime() + pPartialTick;
                pPoseStack.mulPose(Axis.YP.rotationDegrees(time * 0.5f));

                EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
                dispatcher.render(slimeRender, 0, 0, 0, 0, pPartialTick, pPoseStack, pBuffer, pPackedLight);
                pPoseStack.popPose();
            }

        }
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
