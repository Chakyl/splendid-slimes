package io.github.chakyl.splendidslimes.client.renderer;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import io.github.chakyl.splendidslimes.item.SlimeVac;
import io.github.chakyl.splendidslimes.util.SlimeVacUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.Map;

import static io.github.chakyl.splendidslimes.util.SlimeVacUtils.*;

public class SlimeVacRenderer {

    private static Map<RenderType, BufferBuilder> builders = Map.of(
            RenderType.glint(), new BufferBuilder(RenderType.glint().bufferSize()),
            RenderType.glintDirect(), new BufferBuilder(RenderType.glintDirect().bufferSize()),
            RenderType.glintTranslucent(), new BufferBuilder(RenderType.glintTranslucent().bufferSize()),
            RenderType.entityGlint(), new BufferBuilder(RenderType.entityGlint().bufferSize()),
            RenderType.entityGlintDirect(), new BufferBuilder(RenderType.entityGlintDirect().bufferSize())
    );

    public static boolean drawFirstPerson(Player player, MultiBufferSource buffer, PoseStack matrix, int light, float partialTicks) {
        if (!SlimeVac.hasLargo(player)) return false;
        try {
            drawFirstPersonEntity(player, buffer, matrix, light, partialTicks);
        } catch (Exception e) {
        }

        return true;
    }

    private static void drawFirstPersonEntity(Player player, MultiBufferSource buffer, PoseStack matrix, int light, float partialTicks) {
        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
        Entity entity = getVacSlime(player);

        if (entity != null) {
            Vec3 playerpos = getExactPos(player, partialTicks);

            entity.setPos(playerpos.x, playerpos.y, playerpos.z);
            entity.xRotO = 0.0f;
            entity.yRotO = 0.0f;
            entity.setYHeadRot(0.0f);

            float height = entity.getBbHeight();
            float width = entity.getBbWidth();

            matrix.pushPose();
            matrix.mulPose(Axis.YP.rotationDegrees(180));
            matrix.translate(0.0 - 0.75, -height - .01, width + 0.1);

            manager.setRenderShadow(false);

            if (entity instanceof LivingEntity)  ((LivingEntity) entity).hurtTime = 0;

            try {
                manager.render(entity, 0, 0, 0, 0f, 0, matrix, buffer, light);
            } catch (Exception e) {
            }
            manager.setRenderShadow(true);
            matrix.popPose();
        }
    }

    public static void drawThirdPerson(float partialticks, Matrix4f mat) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        int light = 0;
        int perspective = SlimeVacUtils.getPerspective();
        EntityRenderDispatcher manager = mc.getEntityRenderDispatcher();

        PoseStack matrix = new PoseStack();
        matrix.last().pose().mul(mat);

        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();

        BufferSource buffer = MultiBufferSource.immediateWithBuffers(builders, Tesselator.getInstance().getBuilder());

        for (Player player : level.players()) {
            try {
                if (perspective == 0 && player == mc.player) continue;

                light = manager.getPackedLightCoords(player, partialticks);

                Entity entity = SlimeVacUtils.getVacSlime(player);

                if (entity != null) {
                    applyEntityTransformations(player, partialticks, matrix, entity);

                    manager.setRenderShadow(false);

                    if (entity instanceof LivingEntity le) le.hurtTime = 0;

                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                    manager.render(entity, 0, 0, 0, 0f, 0, matrix, buffer, light);
                    matrix.popPose();
                    manager.setRenderShadow(true);
                    matrix.popPose();
                }
            } catch (Exception e) {
            }

        }
        buffer.endLastBatch();

        buffer.endBatch(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS));
        buffer.endBatch(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
        buffer.endBatch(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
        buffer.endBatch(RenderType.entitySmoothCutout(TextureAtlas.LOCATION_BLOCKS));

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();


    }
}
