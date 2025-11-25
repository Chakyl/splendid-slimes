package io.github.chakyl.splendidslimes;

import dev.shadowsoffire.placebo.network.MessageHelper;
import dev.shadowsoffire.placebo.tabs.TabFillingRegistry;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import io.github.chakyl.splendidslimes.registry.ModElements;
import io.github.chakyl.splendidslimes.registry.ModElements.Items;
import io.github.chakyl.splendidslimes.registry.ModElements.Tabs;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(SplendidSlimes.MODID)
public class SplendidSlimes {
    public static final String MODID = "splendid_slimes";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, MODID))
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .networkProtocolVersion(() -> "1.0.0")
            .simpleChannel();

    public SplendidSlimes() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);
        SlimyConfig.load();
        ModElements.bootstrap();
        MessageHelper.registerMessage(CHANNEL, 0, new SlimyConfig.ConfigMessage.Provider());
        ModElements.LOOT_MODIFIERS.register(modEventBus);
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            TabFillingRegistry.register(Tabs.TAB_KEY, Items.SLIME_INCUBATOR, Items.PLORT_PRESS, Items.PLORT_RIPPIT, Items.SLIME_FEEDER, Items.CORRAL_BLOCK, Items.CORRAL_PANE, Items.SLIME_SPAWNER, Items.SLIME_VAC, Items.ROCKET_POD, Items.SLIME_INSPECTOR, Items.SLIME_CANDY, Items.PLORT, Items.SLIME_HEART, Items.SLIME_ITEM, Items.SPLENDID_SLIME_SPAWN_EGG, Items.TARR_SPAWN_EGG);
        });
        SlimeBreedRegistry.INSTANCE.registerToBus();
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }
}