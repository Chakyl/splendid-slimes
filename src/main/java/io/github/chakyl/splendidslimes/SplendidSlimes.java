package io.github.chakyl.splendidslimes;

import dev.shadowsoffire.placebo.tabs.TabFillingRegistry;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import io.github.chakyl.splendidslimes.item.PlortItem;
import io.github.chakyl.splendidslimes.item.SlimeHeartItem;
import io.github.chakyl.splendidslimes.item.SlimeInventoryItem;
import io.github.chakyl.splendidslimes.item.SlimeSpawnEggItem;
import io.github.chakyl.splendidslimes.registry.ModElements;
import io.github.chakyl.splendidslimes.registry.ModElements.Items;
import net.mcexpanded.fancytabsections.FancyTabSections;
import net.mcexpanded.fancytabsections.Section.SectionColored;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(SplendidSlimes.MODID)
public class SplendidSlimes {
    public static final String MODID = "splendid_slimes";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    private static final ModConfigSpec.Builder CONFIG_BUILDER = new ModConfigSpec.Builder();
    public static final SlimyConfig CONFIG = new SlimyConfig(CONFIG_BUILDER);

    public SplendidSlimes(ModContainer container) {
        IEventBus bus = container.getEventBus();
        container.registerConfig(ModConfig.Type.COMMON, CONFIG_BUILDER.build());
        bus.register(this);
        ModElements.bootstrap(bus);

    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent e) {
        SlimeBreedRegistry.INSTANCE.registerToBus();
        // Items.SLIME_FEEDER
        e.enqueueWork(() -> {
            FancyTabSections.addSection(loc("tab"),
                    new SectionColored(loc("equipment"))
                            .setTitle(Component.translatable("Equipment"))
                            .setTextColor(0xFFFFFF).setTextOutline(0xFF555500)
                            .add(ModElements.Items.SLIME_INCUBATOR.value())
                            .add(ModElements.Items.PLORT_PRESS.value())
                            .add(ModElements.Items.PLORT_RIPPIT.value())
                            .add(ModElements.Items.CORRAL_BLOCK.value())
                            .add(ModElements.Items.CORRAL_PANE.value())
                            .add(ModElements.Items.SLIME_INSPECTOR.value())
                            .add(ModElements.Items.SLIME_CANDY.value())
                            .add(ModElements.Items.ROCKET_POD.value())
                            .add(ModElements.Items.SLIME_SPAWNER.value())


            );
            FancyTabSections.addSection(loc("tab"),
                    new SectionColored(loc("plorts"))
                            .setTitle(Component.translatable("Plorts"))
                            .setTextColor(0xFFFFFF).setTextOutline(0xFF555500)
                            .add((registry) -> SlimeBreedRegistry.INSTANCE.getKeys().stream()
                                    .sorted()
                                    .map(SlimeBreedRegistry.INSTANCE::holder)
                                    .map(holder -> {
                                        ItemStack s = new ItemStack(Items.PLORT);
                                        PlortItem.setStoredPlort(s, holder);
                                        return s;
                                    })
                                    .toList()));
            FancyTabSections.addSection(loc("tab"),
                    new SectionColored(loc("slimes"))
                            .setTitle(Component.translatable("Slimes"))
                            .setTextColor(0xFFFFFF).setTextOutline(0xFF555500)
                            .add((registry) -> SlimeBreedRegistry.INSTANCE.getKeys().stream()
                                    .sorted()
                                    .map(SlimeBreedRegistry.INSTANCE::holder)
                                    .map(holder -> {
                                        ItemStack s = new ItemStack(Items.SLIME_ITEM);
                                        SlimeInventoryItem.setStoredSlime(s, holder);
                                        return s;
                                    })
                                    .toList()));
            FancyTabSections.addSection(loc("tab"),
                    new SectionColored(loc("slime_hearts"))
                            .setTitle(Component.translatable("Slime Hearts"))
                            .setTextColor(0xFFFFFF).setTextOutline(0xFF555500)
                            .add((registry) -> SlimeBreedRegistry.INSTANCE.getKeys().stream()
                                    .sorted()
                                    .map(SlimeBreedRegistry.INSTANCE::holder)
                                    .map(holder -> {
                                        ItemStack s = new ItemStack(Items.SLIME_HEART);
                                        SlimeHeartItem.setStoredPlort(s, holder);
                                        return s;
                                    })
                                    .toList()));
            FancyTabSections.addSection(loc("tab"),
                    new SectionColored(loc("spawn_eggs"))
                            .setTitle(Component.translatable("Spawn Eggs"))
                            .setTextColor(0xFFFFFF).setTextOutline(0xFF555500)
                            .add((registry) -> SlimeBreedRegistry.INSTANCE.getKeys().stream()
                                    .sorted()
                                    .map(SlimeBreedRegistry.INSTANCE::holder)
                                    .map(holder -> {
                                        ItemStack s = new ItemStack(Items.SPLENDID_SLIME_SPAWN_EGG);
                                        SlimeSpawnEggItem.setStoredSlime(s, holder);
                                        return s;
                                    })
                                    .toList())
                            .add(Items.TARR_SPAWN_EGG.value()));
        });
    }

    @SubscribeEvent
    public void caps(RegisterCapabilitiesEvent e) {
        e.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModElements.BlockEntities.PLORT_PRESS, (be, side) -> be.getInventory());
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}