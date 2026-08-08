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
            .named(loc(MODID))
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
        // 1.21.1 TODO: FTS
//        e.enqueueWork(() -> {
//            FancyTabSections.addSection(loc("tab"),
//                    new SectionColored(loc("equipment"))
//                            .setTitle(Component.translatable("Ranching Equipment"))
//                            .setTextColor(0xFFFFFF).setTextOutline(0xFF555500)
//                            .add(ModElements.Items.SLIME_INCUBATOR.get())
//                            .add(ModElements.Items.PLORT_PRESS.get())
//                            .add(ModElements.Items.PLORT_RIPPIT.get())
//                            .add(ModElements.Items.SLIME_FEEDER.get())
//                            .add(ModElements.Items.CORRAL_BLOCK.get())
//                            .add(ModElements.Items.CORRAL_PANE.get())
//                            .add(ModElements.Items.SLIME_SPAWNER.get())
//                            .add(ModElements.Items.SLIME_VAC.get())
//                            .add(ModElements.Items.ROCKET_POD.get())
//                            .add(ModElements.Items.SLIME_INSPECTOR.get())
//                            .add(ModElements.Items.SLIME_CANDY.get())
//            );
//
//            FancyTabSections.addSection(loc("tab"),
//                    new SectionColored(loc("slimes"))
//                            .setTitle(Component.translatable("Slimes"))
//                            .setTextColor(0xFFFFFF).setTextOutline(0xFF555500)
//                            .add((registry) -> SlimeBreedRegistry.INSTANCE.getKeys().stream()
//                                    .sorted()
//                                    .map(SlimeBreedRegistry.INSTANCE::holder)
//                                    .map(holder -> {
//                                        ItemStack s = new ItemStack(Items.SLIME_ITEM.get());
//                                        SlimeInventoryItem.setStoredSlime(s, holder.get());
//                                        return s;
//                                    })
//                                    .toList()));
//            FancyTabSections.addSection(loc("tab"),
//                    new SectionColored(loc("plorts"))
//                            .setTitle(Component.translatable("Plorts"))
//                            .setTextColor(0xFFFFFF).setTextOutline(0xFF555500)
//                            .add((registry) -> SlimeBreedRegistry.INSTANCE.getKeys().stream()
//                                    .sorted()
//                                    .map(SlimeBreedRegistry.INSTANCE::holder)
//                                    .map(holder -> {
//                                        ItemStack s = new ItemStack(Items.PLORT.get());
//                                        PlortItem.setStoredPlort(s, holder.get());
//                                        return s;
//                                    })
//                                    .toList()));
//            FancyTabSections.addSection(loc("tab"),
//                    new SectionColored(loc("hearts"))
//                            .setTitle(Component.translatable("Hearts"))
//                            .setTextColor(0xFFFFFF).setTextOutline(0xFF555500)
//                            .add((registry) -> SlimeBreedRegistry.INSTANCE.getKeys().stream()
//                                    .sorted()
//                                    .map(SlimeBreedRegistry.INSTANCE::holder)
//                                    .map(holder -> {
//                                        ItemStack s = new ItemStack(Items.SLIME_HEART.get());
//                                        SlimeHeartItem.setStoredPlort(s, holder.get());
//                                        return s;
//                                    })
//                                    .toList()));
//            FancyTabSections.addSection(loc("tab"),
//                    new SectionColored(loc("spawn_eggs"))
//                            .setTitle(Component.translatable("Spawn Eggs"))
//                            .setTextColor(0xFFFFFF).setTextOutline(0xFF555500)
//                            .add((registry) -> SlimeBreedRegistry.INSTANCE.getKeys().stream()
//                                    .sorted()
//                                    .map(SlimeBreedRegistry.INSTANCE::holder)
//                                    .map(holder -> {
//                                        ItemStack s = new ItemStack(Items.SPLENDID_SLIME_SPAWN_EGG.get());
//                                        SlimeSpawnEggItem.setStoredSlime(s, holder.getId());
//                                        return s;
//                                    })
//                                    .toList())
//                            .add(Items.TARR_SPAWN_EGG.get()));
//        });
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}