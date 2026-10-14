package io.github.chakyl.splendidslimes.registry;

import com.mojang.serialization.MapCodec;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntityType;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.block.*;
import io.github.chakyl.splendidslimes.block.corral.CorralBlock;
import io.github.chakyl.splendidslimes.block.corral.CorralPane;
import io.github.chakyl.splendidslimes.blockentity.*;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import io.github.chakyl.splendidslimes.entity.SlimeEntityBase;
import io.github.chakyl.splendidslimes.entity.SplendidSlime;
import io.github.chakyl.splendidslimes.entity.Tarr;
import io.github.chakyl.splendidslimes.item.*;
import io.github.chakyl.splendidslimes.item.ItemProjectile.ItemProjectileEntity;
import io.github.chakyl.splendidslimes.recipe.PlortPressingRecipe;
import io.github.chakyl.splendidslimes.recipe.PlortRippingRecipe;
import io.github.chakyl.splendidslimes.screen.PlortPressMenu;
import io.github.chakyl.splendidslimes.util.SlimeLootModifier;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static io.github.chakyl.splendidslimes.SplendidSlimes.loc;

public class ModElements {
    private static final DeferredHelper R = DeferredHelper.create(SplendidSlimes.MODID);

    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SplendidSlimes.MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SplendidSlimes.MODID);

    public static final Supplier<MapCodec<SlimeLootModifier>> SLIME_MODIFIER = LOOT_MODIFIERS.register("slime_modifier", () -> SlimeLootModifier.CODEC);

    public static class Blocks {
        public static final Holder<Block> SLIME_INCUBATOR = R.block("slime_incubator", SlimeIncubatorBlock::new, p -> p.strength(4, 3000).noOcclusion());
        public static final Holder<Block> PLORT_PRESS = R.block("plort_press", PlortPressBlock::new, p -> p.strength(4, 3000).noOcclusion());
        public static final Holder<Block> PLORT_RIPPIT = R.block("plort_rippit", PlortRippitBlock::new, p -> p.strength(4, 3000).noOcclusion());
//        public static final Holder<Block> SLIME_FEEDER = R.block("slime_feeder", SlimeFeederBlock::new, p -> p.strength(4, 3000).noOcclusion());
        public static final Holder<Block> CORRAL_BLOCK = R.block("corral_block", CorralBlock::new, p -> p.strength(4, 3000).noOcclusion().isSuffocating((state, world, pos) -> false).isViewBlocking((state, world, pos) -> false));
        public static final Holder<Block> CORRAL_PANE = R.block("corral_pane", CorralPane::new, p -> p.strength(4, 3000).noOcclusion().isSuffocating((state, world, pos) -> false).isViewBlocking((state, world, pos) -> false));
        public static final Holder<Block> SLIME_SPAWNER = R.block("slime_spawner", SlimeSpawnerBlock::new, p -> p.strength(4, 3000).noOcclusion());

        private static void bootstrap() {
        }
    }

    public static class BlockEntities {
        public static final BlockEntityType<SlimeIncubatorBlockEntity> SLIME_INCUBATOR = R.tickingBlockEntity("slime_incubator", SlimeIncubatorBlockEntity::new, TickingBlockEntityType.TickSide.SERVER, ModElements.Blocks.SLIME_INCUBATOR);
        public static final BlockEntityType<PlortPressBlockEntity> PLORT_PRESS = R.tickingBlockEntity("plort_press", PlortPressBlockEntity::new, TickingBlockEntityType.TickSide.SERVER, Blocks.PLORT_PRESS);
        public static final BlockEntityType<PlortRippitBlockEntity> PLORT_RIPPIT = R.tickingBlockEntity("plort_rippit", PlortRippitBlockEntity::new, TickingBlockEntityType.TickSide.SERVER, Blocks.PLORT_RIPPIT);
//        public static final BlockEntityType<SlimeFeederBlockEntity> SLIME_FEEDER = R.tickingBlockEntity("slime_feeder", SlimeFeederBlockEntity::new, TickingBlockEntityType.TickSide.SERVER, Blocks.SLIME_FEEDER);
        public static final BlockEntityType<SlimeSpawnerBlockEntity> SLIME_SPAWNER = R.tickingBlockEntity("slime_spawner", SlimeSpawnerBlockEntity::new, TickingBlockEntityType.TickSide.SERVER, ModElements.Blocks.SLIME_SPAWNER);

        private static void bootstrap() {
        }
    }

    public static class Items {
        public static final Holder<Item> SLIME_INCUBATOR = R.blockItem("slime_incubator", Blocks.SLIME_INCUBATOR);
        public static final Holder<Item> PLORT_PRESS = R.blockItem("plort_press", Blocks.PLORT_PRESS);
        public static final Holder<Item> PLORT_RIPPIT = R.blockItem("plort_rippit", Blocks.PLORT_RIPPIT);
//        public static final Holder<Item> SLIME_FEEDER = R.blockItem("slime_feeder", Blocks.SLIME_FEEDER);
        public static final Holder<Item> CORRAL_BLOCK = R.blockItem("corral_block", Blocks.CORRAL_BLOCK);
        public static final Holder<Item> CORRAL_PANE = R.blockItem("corral_pane", Blocks.CORRAL_PANE);
        public static final Holder<Item> SLIME_SPAWNER = R.blockItem("slime_spawner", Blocks.SLIME_SPAWNER);

        public static final Holder<Item> PLORT = R.item("plort", PlortItem::new, p -> p.stacksTo(64));
        public static final Holder<Item> DECORATOR = R.item("decorator", DecoratorItem::new, p -> p.stacksTo(64));
        public static final Holder<Item> SLIME_HEART = R.item("slime_heart", SlimeHeartItem::new, p -> p.stacksTo(64));
        public static final Holder<Item> SLIME_ITEM = R.item("slime_item", SlimeInventoryItem::new, p -> p.stacksTo(1));

        public static final Holder<Item> SPLENDID_SLIME_SPAWN_EGG = R.item("spawn_egg_splendid_slime", p -> new SlimeSpawnEggItem(Entities.SPLENDID_SLIME, 0xff7d9d, 0xff7d9d, p));
        public static final Holder<Item> TARR_SPAWN_EGG = R.item("spawn_egg_tarr", p -> new DeferredSpawnEggItem(Entities.TARR, 0x2c221c, 0x921f78, p));

        public static final Holder<Item> TARRTAR = R.item("tarrtar", Item::new, p -> p.stacksTo(64));
        public static final Holder<Item> SLIME_CANDY = R.item("slime_candy", Item::new, p -> p.stacksTo(64).rarity(Rarity.UNCOMMON));
        public static final Holder<Item> ROCKET_POD = R.item("rocket_pod", Item::new, p -> p.stacksTo(64));
        // TODO: Slime Vac
//        public static final Holder<SlimeVac> SLIME_VAC = R.item("slime_vac", SlimeVac::new, p -> p.stacksTo(1));
        public static final Holder<Item> SLIME_INSPECTOR = R.item("slime_inspector", SlimeInspector::new, p -> p.stacksTo(1));

        private static void bootstrap() {
        }
    }

    public static class Entities {
        public static final DeferredHolder<EntityType<?>, EntityType<SlimeEntityBase>> SPLENDID_SLIME = R.entity("splendid_slime", SplendidSlime::new, MobCategory.CREATURE, builder -> builder.sized(1.0f, 1.0f));
        public static final DeferredHolder<EntityType<?>, EntityType<SlimeEntityBase>> TARR = R.entity("tarr", Tarr::new, MobCategory.MONSTER, builder -> builder.sized(1.0f, 1.0f));
        public static final DeferredHolder<EntityType<?>, EntityType<ItemProjectileEntity>> ITEM_PROJECTILE = R.entity("item_projectile", ItemProjectileEntity::new, MobCategory.MISC, builder -> builder.sized(0.25f, 0.25f));

        private static void bootstrap() {
        }
    }

    public static class Menus {
        public static final MenuType<PlortPressMenu> PLORT_PRESS_MENU = R.menuWithPos("roe_recycler", PlortPressMenu::new);

        private static void bootstrap() {
        }
    }

    public static class Particles {

        private static void bootstrap() {
        }
    }

    public static class Recipes {
        public static final Holder<RecipeSerializer<?>> PLORT_PRESSING_SERIALIZER = R.recipeSerializer("plort_pressing", PlortPressingRecipe.Serializer::new);
        public static final Holder<RecipeSerializer<?>> PLORT_RIPPING_SERIALIZER = R.recipeSerializer("plort_ripping", PlortRippingRecipe.Serializer::new);

        private static void bootstrap() {
        }
    }

    public static class Components {
        /**
         * Stored fish pond type, used by {@link PlortItem} and {@link SlimeHeartItem}. The underlying holder may be unbound.
         */
        public static final DataComponentType<DynamicHolder<SlimeBreed>> SLIME_BREED = R.component("slime_breed", b -> b
                .persistent(SlimeBreedRegistry.INSTANCE.holderCodec())
                .networkSynchronized(SlimeBreedRegistry.INSTANCE.holderStreamCodec()));


        private static void bootstrap() {
        }
    }

    public static class Tabs {
        public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, loc("tab"));

        public static final Holder<CreativeModeTab> AB = R.creativeTab("tab", b -> b.title(Component.translatable("itemGroup." + SplendidSlimes.MODID)).icon(() -> Items.SLIME_INCUBATOR.value().getDefaultInstance()));

        private static void bootstrap() {
        }
    }

    public static void bootstrap(IEventBus bus) {
        bus.register(R);
        LOOT_MODIFIERS.register(bus);
        MENUS.register(bus);
        Blocks.bootstrap();
        BlockEntities.bootstrap();
        Items.bootstrap();
        Entities.bootstrap();
        Menus.bootstrap();
        Particles.bootstrap();
        Recipes.bootstrap();
        Components.bootstrap();
        Tabs.bootstrap();
    }
}