package io.github.chakyl.splendidslimes.events;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.client.Keybindings;
import io.github.chakyl.splendidslimes.client.model.HatModel;
import io.github.chakyl.splendidslimes.client.model.PlortModel;
import io.github.chakyl.splendidslimes.client.model.SlimeEntityModel;
import io.github.chakyl.splendidslimes.client.renderer.SlimeEntityRenderer;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.item.ItemProjectile.ItemProjectileRenderer;
import io.github.chakyl.splendidslimes.registry.ModElements;
import io.github.chakyl.splendidslimes.screen.PlortPressScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Set;

import static io.github.chakyl.splendidslimes.util.SlimeData.getSlimeData;

@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT, modid = SplendidSlimes.MODID)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModElements.Entities.SPLENDID_SLIME.get(), SlimeEntityRenderer::new);
        event.registerEntityRenderer(ModElements.Entities.TARR.get(), SlimeEntityRenderer::new);
        event.registerEntityRenderer(ModElements.Entities.ITEM_PROJECTILE.get(), ItemProjectileRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EntityRenderers.register(ModElements.Entities.SPLENDID_SLIME.get(), SlimeEntityRenderer::new);
            EntityRenderers.register(ModElements.Entities.TARR.get(), SlimeEntityRenderer::new);
            EntityRenderers.register(ModElements.Entities.ITEM_PROJECTILE.get(), ItemProjectileRenderer::new);
            MenuScreens.register(ModElements.Menus.PLORT_PRESS_MENU.get(), PlortPressScreen::new);
        });

    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(Keybindings.INSTANCE.slimeVacModeKey);
    }

    @SubscribeEvent
    public static void mrl(ModelEvent.RegisterAdditional e) {
        e.register(new ResourceLocation(SplendidSlimes.MODID, "item/slime_item"));
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SlimeEntityRenderer.SPLENDID_SLIME_BASE, SlimeEntityModel::createInnerBodyLayer);
    }

    @SubscribeEvent
    public static void addPlortModel(ModelEvent.RegisterAdditional e) {
        Set<ResourceLocation> locs = Minecraft.getInstance().getResourceManager().listResources("models", loc -> SplendidSlimes.MODID.equals(loc.getNamespace()) && loc.getPath().contains("/plort/") && loc.getPath().endsWith(".json"))
                .keySet();
        for (ResourceLocation s : locs) {
            String path = s.getPath().substring("models/".length(), s.getPath().length() - ".json".length());
            e.register(new ResourceLocation(SplendidSlimes.MODID, path));
        }
    }

    @SubscribeEvent
    public static void replacePlortModel(ModelEvent.ModifyBakingResult e) {
        ModelResourceLocation key = new ModelResourceLocation(SplendidSlimes.loc("plort"), "inventory");
        BakedModel oldModel = e.getModels().get(key);
        if (oldModel != null) {
            e.getModels().put(key, new PlortModel(oldModel, e.getModelBakery()));
        }
    }

    @SubscribeEvent
    public static void addHatModel(ModelEvent.RegisterAdditional e) {
        Set<ResourceLocation> locs = Minecraft.getInstance().getResourceManager().listResources("models", loc -> SplendidSlimes.MODID.equals(loc.getNamespace()) && loc.getPath().contains("/hat/") && loc.getPath().endsWith(".json"))
                .keySet();
        for (ResourceLocation s : locs) {
            String path = s.getPath().substring("models/".length(), s.getPath().length() - ".json".length());
            e.register(new ResourceLocation(SplendidSlimes.MODID, path));
        }
    }

    @SubscribeEvent
    public static void replaceHatModel(ModelEvent.ModifyBakingResult e) {
        ModelResourceLocation key = new ModelResourceLocation(SplendidSlimes.loc("hat"), "inventory");
        BakedModel oldModel = e.getModels().get(key);
        if (oldModel != null) {
            e.getModels().put(key, new HatModel(oldModel, e.getModelBakery()));
        }
    }

    @SubscribeEvent
    public static void colors(RegisterColorHandlersEvent.Item e) {
        e.register((stack, tint) -> {
            DynamicHolder<SlimeBreed> slime = getSlimeData(stack, "slime");
            int color = 0xFFFFFF;
            if (slime.isBound()) {
                color = slime.get().getColor();
            }
            return color;
        }, ModElements.Items.SLIME_HEART.get());

    }

}