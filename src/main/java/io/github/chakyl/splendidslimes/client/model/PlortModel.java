package io.github.chakyl.splendidslimes.client.model;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.github.chakyl.splendidslimes.SplendidSlimes.loc;
import static io.github.chakyl.splendidslimes.util.SlimeData.getSlimeData;

public class PlortModel implements BakedModel {
    private final BakedModel original;
    private final ItemOverrides overrides;

    @SuppressWarnings("deprecation")
    public PlortModel(BakedModel original, ModelBakery loader) {
        this.original = original;
        this.overrides = new ItemOverrides() {
            @Override
            public BakedModel resolve(BakedModel original, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
                BakedModel specific = PlortModel.this.resolve(original, stack, world, entity, seed);
                return specific == original ? specific : specific.getOverrides().resolve(specific, stack, world, entity, seed);
            }
        };
    }

    public BakedModel resolve(BakedModel original, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        DynamicHolder<SlimeBreed> plort = getSlimeData(stack, "plort");
        if (plort.isBound()) {
            return Minecraft.getInstance().getModelManager().getModel(loc("item/plort/" + plort.getId().getPath()));
        }
        return original;
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.overrides;
    }

    @Override
    @Deprecated
    public List<BakedQuad> getQuads(BlockState pState, Direction pDirection, RandomSource pRandom) {
        return this.original.getQuads(pState, pDirection, pRandom);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.original.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.original.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.original.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this.original.isCustomRenderer();
    }

    @Override
    @Deprecated
    public TextureAtlasSprite getParticleIcon() {
        return this.original.getParticleIcon();
    }

    @Override
    @Deprecated
    public ItemTransforms getTransforms() {
        return this.original.getTransforms();
    }
}