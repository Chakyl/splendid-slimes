package io.github.chakyl.splendidslimes.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.chakyl.splendidslimes.client.model.SlimeEntityModel;
import io.github.chakyl.splendidslimes.client.model.SlimeFaceLayer;
import io.github.chakyl.splendidslimes.client.model.SlimeHandyLayer;
import io.github.chakyl.splendidslimes.client.model.SlimeDecoratorLayer;
import io.github.chakyl.splendidslimes.entity.SlimeEntityBase;
import io.github.chakyl.splendidslimes.entity.Tarr;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;

import static io.github.chakyl.splendidslimes.SplendidSlimes.loc;

public class SlimeEntityRenderer extends MobRenderer<SlimeEntityBase, SlimeEntityModel<SlimeEntityBase>> {
    public static final ModelLayerLocation SPLENDID_SLIME_BASE = new ModelLayerLocation(loc("main"), "main");
    public static final ModelLayerLocation SLIME_FACE_LAYER = new ModelLayerLocation(loc("slime_face"), "main");
    private static Map<String, ResourceLocation> cache = new HashMap<>();

    public SlimeEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new SlimeEntityModel<>(context.bakeLayer(SPLENDID_SLIME_BASE)), 0.25F);
        addLayer(new SlimeFaceLayer<>(this, context.bakeLayer(SLIME_FACE_LAYER)));
        addLayer(new SlimeDecoratorLayer<>(this));
        addLayer(new SlimeHandyLayer<>(this, context.getItemInHandRenderer()));
    }

    public void render(SlimeEntityBase slimeEntityBase, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        this.shadowRadius = 0.25F * (float) slimeEntityBase.getSize();
        super.render(slimeEntityBase, entityYaw, partialTicks, poseStack, multiBufferSource, packedLight);
    }

    @Override
    protected RenderType getRenderType(SlimeEntityBase slimeEntityBase, boolean bodyVisible, boolean translucent, boolean glowing) {
        ResourceLocation resourcelocation = this.getTextureLocation(slimeEntityBase);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (bodyVisible) {
            return RenderType.entityTranslucent(resourcelocation);
        } else {
            return glowing ? RenderType.outline(resourcelocation) : null;
        }
    }

    protected void scale(SlimeEntityBase slimeEntityBase, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.999F, 0.999F, 0.999F);
        poseStack.translate(0.0F, 0.001F, 0.0F);
        float slimeSize = (float) slimeEntityBase.getSize();
        float lerpy = Mth.lerp(partialTickTime, slimeEntityBase.oSquish, slimeEntityBase.squish) / (slimeSize * 0.5F + 1.0F);
        float cutter = 1.0F / (lerpy + 1.0F);
        poseStack.scale(cutter * slimeSize, 1.0F / cutter * slimeSize, cutter * slimeSize);
    }

    @Override
    public ResourceLocation getTextureLocation(SlimeEntityBase slimeEntityBase) {
        String path = slimeEntityBase.getSlimeBreed().replace(":", ":textures/entity/slime/") + "/base.png";
        if (slimeEntityBase instanceof Tarr) path = "splendid_slimes:textures/entity/slime/tarr.png";
        if (!cache.containsKey(path)) {
            cache.put(path, ResourceLocation.parse(path));
        }
        return cache.get(path);
    }
}

