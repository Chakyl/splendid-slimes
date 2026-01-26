package io.github.chakyl.splendidslimes.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.client.WrappedRTBuffer;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.entity.SlimeEntityBase;
import io.github.chakyl.splendidslimes.item.SlimeInventoryItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE, value = Dist.CLIENT, modid = SplendidSlimes.MODID)
public class SlimeItemModelRenderer extends BlockEntityWithoutLevelRenderer {

    public SlimeItemModelRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    private static final MultiBufferSource.BufferSource GHOST_ENTITY_BUF = MultiBufferSource.immediate(new BufferBuilder(256));
    private static final ResourceLocation DATA_MODEL_BASE = new ResourceLocation(SplendidSlimes.MODID, "item/slime_item");

    @Override
    @SuppressWarnings("deprecation")
    public void renderByItem(ItemStack stack, ItemDisplayContext type, PoseStack matrix, MultiBufferSource buf, int light, int overlay) {
        ItemRenderer irenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel base = irenderer.getItemModelShaper().getModelManager().getModel(DATA_MODEL_BASE);
        matrix.pushPose();
        if (type == ItemDisplayContext.FIXED) {
            matrix.translate(1, 1, 0);
            float scale = 0.5F;
            matrix.scale(scale, scale, scale);
            matrix.translate(-1.5F, -0.5F, 0.5F);
            matrix.mulPose(Axis.XP.rotationDegrees(90));
            matrix.mulPose(Axis.XP.rotationDegrees(90));
            matrix.translate(0, 0, -1);
        }
        else if (type != ItemDisplayContext.GUI) {
            matrix.translate(1, 1, 0);
            float scale = 0.5F;
            matrix.scale(scale, scale, scale);
            matrix.translate(-1.5F, -0.5F, 0.5F);
            matrix.mulPose(Axis.XP.rotationDegrees(90));
        }
        else {
            matrix.translate(0, -.5F, -.5F);
            matrix.mulPose(Axis.XN.rotationDegrees(75));
            matrix.mulPose(Axis.ZP.rotationDegrees(45));
            float scale = 0.9F;
            matrix.scale(scale, scale, scale);
            matrix.translate(0.775, 0, -0.0825);
        }
        irenderer.renderModelLists(base, stack, light, overlay, matrix, ItemRenderer.getFoilBufferDirect(GHOST_ENTITY_BUF, ItemBlockRenderTypes.getRenderType(stack, true), true, false));
        GHOST_ENTITY_BUF.endBatch();
        matrix.popPose();
        DynamicHolder<SlimeBreed> model = SlimeInventoryItem.getStoredSlime(stack);
        if (model.isBound()) {
            LivingEntity ent = io.github.chakyl.splendidslimes.util.ClientEntityCache.computeIfAbsent(Minecraft.getInstance().level, new CompoundTag());
            if (Minecraft.getInstance().player != null) ent.tickCount = Minecraft.getInstance().player.tickCount;
            if (ent != null) {
                this.renderEntityInInventory(matrix, type, ent, model.get());
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void renderEntityInInventory(PoseStack matrix, ItemDisplayContext type, LivingEntity pLivingEntity, SlimeBreed breed) {
        matrix.pushPose();
        matrix.translate(0.5, 0.25, 0.5);
        if (type == ItemDisplayContext.FIXED) {
            matrix.translate(0, -0.5, 0);
            float scale = 1F;
            matrix.scale(scale, scale, scale);
            matrix.translate(0, 1.45, 0);
            matrix.mulPose(Axis.XN.rotationDegrees(90));
            matrix.mulPose(Axis.YN.rotationDegrees(180));
        }
        else if (type == ItemDisplayContext.GUI) {
            matrix.translate(0.05, -0.25, 0);
            float scale = 1F;
            matrix.scale(scale, scale, scale);
            matrix.translate(0, 0.2, 0);
        }
        else {
            float scale = 1F;
            matrix.translate(type == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || type == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ?  -0.3 : 0.3, 0, 0);
            matrix.scale(scale, scale, scale);
            matrix.translate(0, 0.12 + 0.05 * Math.sin((pLivingEntity.tickCount + Minecraft.getInstance().getDeltaFrameTime()) / 32), 0);
        }

        float rotation = -30;
        if (type == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || type == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) rotation = 30;
        if (type == ItemDisplayContext.FIXED) rotation = 180;
        matrix.mulPose(Axis.YP.rotationDegrees(rotation));
        pLivingEntity.setYRot(0);
        ((SlimeEntityBase) pLivingEntity).setSlimeBreed("splendid_slimes:" + breed.breed());
        pLivingEntity.yBodyRot = pLivingEntity.getYRot();
        pLivingEntity.yHeadRot = pLivingEntity.getYRot();
        pLivingEntity.yHeadRotO = pLivingEntity.getYRot();
        EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        entityrenderermanager.setRenderShadow(false);
        MultiBufferSource.BufferSource rtBuffer = GHOST_ENTITY_BUF;
        RenderSystem.runAsFancy(() -> {
            entityrenderermanager.render(pLivingEntity, 0, 0, 0, 0.0F, Minecraft.getInstance().getDeltaFrameTime(), matrix, new WrappedRTBuffer(rtBuffer), 15728880);
        });
        rtBuffer.endBatch();
        entityrenderermanager.setRenderShadow(true);
        matrix.popPose();
    }

}