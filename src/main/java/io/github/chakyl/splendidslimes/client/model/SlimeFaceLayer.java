package io.github.chakyl.splendidslimes.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.chakyl.splendidslimes.entity.SlimeEntityBase;
import io.github.chakyl.splendidslimes.entity.Tarr;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

import static io.github.chakyl.splendidslimes.SplendidSlimes.loc;

public class SlimeFaceLayer<T extends Entity> extends RenderLayer<T, SlimeEntityModel<T>> {
    private static Map<String, ResourceLocation> cache = new HashMap<>();
    private final EntityModel<T> faceModel;

    public SlimeFaceLayer(RenderLayerParent<T, SlimeEntityModel<T>> renderer, ModelPart faceModelRoot) {
        super(renderer);
        this.faceModel = new SlimeFaceModel<>(faceModelRoot);
    }


    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getParentModel().copyPropertiesTo(this.faceModel);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(getTextureLocation((SlimeEntityBase) entity)));
        this.faceModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
    }

    public ResourceLocation getTextureLocation(SlimeEntityBase slimeEntityBase) {
        String path = slimeEntityBase.getSlimeBreed().replace(":", ":textures/entity/slime/") + "/" + slimeEntityBase.getSlimeFace() + ".png";
        if (!cache.containsKey(path)) {
            cache.put(path, ResourceLocation.parse(path));
        }
        return cache.get(path);
    }
}
