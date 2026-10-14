package io.github.chakyl.splendidslimes.events;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.blockentity.renderer.SlimeIncubatorBlockEntityRenderer;
import io.github.chakyl.splendidslimes.client.Keybindings;
import io.github.chakyl.splendidslimes.client.model.DecoratorModel;
import io.github.chakyl.splendidslimes.client.model.PlortModel;
import io.github.chakyl.splendidslimes.client.model.SlimeEntityModel;
import io.github.chakyl.splendidslimes.client.model.SlimeFaceModel;
import io.github.chakyl.splendidslimes.client.renderer.SlimeEntityRenderer;
import io.github.chakyl.splendidslimes.client.renderer.SlimeItemModelRenderer;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.item.ItemProjectile.ItemProjectileRenderer;
import io.github.chakyl.splendidslimes.registry.ModElements;
import io.github.chakyl.splendidslimes.screen.PlortPressScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import java.util.Set;

import static io.github.chakyl.splendidslimes.SplendidSlimes.loc;
import static io.github.chakyl.splendidslimes.util.SlimeData.getSlimeData;

@EventBusSubscriber(value = Dist.CLIENT, modid = SplendidSlimes.MODID)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModElements.Entities.SPLENDID_SLIME.value(), SlimeEntityRenderer::new);
        event.registerEntityRenderer(ModElements.Entities.TARR.value(), SlimeEntityRenderer::new);
        event.registerEntityRenderer(ModElements.Entities.ITEM_PROJECTILE.value(), ItemProjectileRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EntityRenderers.register(ModElements.Entities.SPLENDID_SLIME.value(), SlimeEntityRenderer::new);
            EntityRenderers.register(ModElements.Entities.TARR.value(), SlimeEntityRenderer::new);
            EntityRenderers.register(ModElements.Entities.ITEM_PROJECTILE.value(), ItemProjectileRenderer::new);
        });
    }

    @SubscribeEvent
    public static void screens(RegisterMenuScreensEvent e) {
        e.register(ModElements.Menus.PLORT_PRESS_MENU, PlortPressScreen::new);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModElements.BlockEntities.SLIME_INCUBATOR, SlimeIncubatorBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(Keybindings.INSTANCE.slimeVacModeKey);
    }

    @SubscribeEvent
    public static void mrl(ModelEvent.RegisterAdditional e) {
        e.register(ModelResourceLocation.standalone(loc("item/slime_item")));
        e.register(ModelResourceLocation.standalone(loc("item/decorator")));
        e.register(ModelResourceLocation.standalone(loc("item/slime_heart")));
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SlimeEntityRenderer.SPLENDID_SLIME_BASE, SlimeEntityModel::createInnerBodyLayer);
        event.registerLayerDefinition(SlimeEntityRenderer.SLIME_FACE_LAYER, SlimeFaceModel::createInnerBodyLayer);
    }

    @SubscribeEvent
    public static void addPlortModel(ModelEvent.RegisterAdditional e) {
        Set<ResourceLocation> locs = Minecraft.getInstance().getResourceManager().listResources("models", loc -> SplendidSlimes.MODID.equals(loc.getNamespace()) && loc.getPath().contains("/plort/") && loc.getPath().endsWith(".json"))
                .keySet();
        for (ResourceLocation s : locs) {
            String path = s.getPath().substring("models/".length(), s.getPath().length() - ".json".length());
            e.register(ModelResourceLocation.standalone(loc(path)));
        }
    }

    @SubscribeEvent
    public static void replacePlortModel(ModelEvent.ModifyBakingResult e) {
        ModelResourceLocation key = new ModelResourceLocation(loc("plort"), "inventory");
        BakedModel oldModel = e.getModels().get(key);
        if (oldModel != null) {
            e.getModels().put(key, new PlortModel(oldModel, e.getModelBakery()));
        }
    }

    @SubscribeEvent
    public static void addDecoratorModel(ModelEvent.RegisterAdditional e) {
        Set<ResourceLocation> locs = Minecraft.getInstance().getResourceManager().listResources("models", loc -> SplendidSlimes.MODID.equals(loc.getNamespace()) && loc.getPath().contains("/decorator/") && loc.getPath().endsWith(".json"))
                .keySet();
        for (ResourceLocation s : locs) {
            String path = s.getPath().substring("models/".length(), s.getPath().length() - ".json".length());
            e.register(ModelResourceLocation.standalone(loc(path)));
        }
    }

    @SubscribeEvent
    public static void replaceDecoratorModel(ModelEvent.ModifyBakingResult e) {
        ModelResourceLocation key = new ModelResourceLocation(loc("decorator"), "inventory");
        BakedModel oldModel = e.getModels().get(key);
        if (oldModel != null) {
            e.getModels().put(key, new DecoratorModel(oldModel, e.getModelBakery()));
        }
    }

    @SubscribeEvent
    public static void colors(RegisterColorHandlersEvent.Item e) {
        e.register((stack, tint) -> {
            DynamicHolder<SlimeBreed> slime = getSlimeData(stack);
            int color = 0xFFFFFF;
            if (slime.isBound()) {
                color = slime.get().color().getValue();
            }
            return  0xFF000000 | color;
        }, ModElements.Items.SLIME_HEART.value());

    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
                               private final SlimeItemModelRenderer dmisr = new SlimeItemModelRenderer();

                               @Override
                               public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                                   return this.dmisr;
                               }
                           },
                ModElements.Items.SLIME_ITEM.value());
    }
}