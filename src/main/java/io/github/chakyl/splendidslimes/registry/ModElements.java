package io.github.chakyl.splendidslimes.registry;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntityType;
import dev.shadowsoffire.placebo.menu.MenuUtil;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.block.*;
import io.github.chakyl.splendidslimes.block.corral.CorralBlock;
import io.github.chakyl.splendidslimes.block.corral.CorralPane;
import io.github.chakyl.splendidslimes.blockentity.*;
import io.github.chakyl.splendidslimes.entity.SlimeEntityBase;
import io.github.chakyl.splendidslimes.entity.SplendidSlime;
import io.github.chakyl.splendidslimes.entity.Tarr;
import io.github.chakyl.splendidslimes.item.*;
import io.github.chakyl.splendidslimes.item.ItemProjectile.ItemProjectileEntity;
import io.github.chakyl.splendidslimes.recipe.PlortPressingRecipe;
import io.github.chakyl.splendidslimes.recipe.PlortRippingRecipe;
import io.github.chakyl.splendidslimes.screen.PlortPressMenu;
import io.github.chakyl.splendidslimes.util.SlimeLootModifier;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModElements {
    private static final DeferredHelper R = DeferredHelper.create(SplendidSlimes.MODID);

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SplendidSlimes.MODID);
    public static final RegistryObject<Codec<SlimeLootModifier>> SLIME_MODIFIER = LOOT_MODIFIERS.register("slime_modifier", () -> SlimeLootModifier.CODEC);

    private static final BlockBehaviour.StatePredicate ALWAYS_FALSE = (state, world, pos) -> false;

    static RegistryObject<EntityType<SlimeEntityBase>> slimeEntity = R.entity("splendid_slime", () -> EntityType.Builder.<SlimeEntityBase>of(SplendidSlime::new, MobCategory.CREATURE).sized(1.3f, 2.0f).build("splendid_slime"));
    static RegistryObject<EntityType<SlimeEntityBase>> tarrEntity = R.entity("tarr", () -> EntityType.Builder.<SlimeEntityBase>of(Tarr::new, MobCategory.MONSTER).sized(1.3f, 2.0f).build("tarr"));

    static BlockBehaviour.Properties defaultBehavior = BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.IRON_BLOCK);

    public static class Blocks {
        public static final RegistryObject<Block> SLIME_INCUBATOR = R.block("slime_incubator", () -> new SlimeIncubatorBlock(defaultBehavior.strength(4, 3000).noOcclusion()));
        public static final RegistryObject<Block> PLORT_PRESS = R.block("plort_press", () -> new PlortPressBlock(defaultBehavior.strength(4, 3000).noOcclusion()));
        public static final RegistryObject<Block> PLORT_RIPPIT = R.block("plort_rippit", () -> new PlortRippitBlock(defaultBehavior.strength(4, 3000).noOcclusion()));
        public static final RegistryObject<Block> SLIME_FEEDER = R.block("slime_feeder", () -> new SlimeFeederBlock(defaultBehavior.strength(4, 3000).noOcclusion()));
        public static final RegistryObject<CorralBlock> CORRAL_BLOCK = R.block("corral_block", () -> new CorralBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.GLASS).strength(4, 3000).noOcclusion().isSuffocating(ALWAYS_FALSE).isViewBlocking(ALWAYS_FALSE)));
        public static final RegistryObject<CorralPane> CORRAL_PANE = R.block("corral_pane", () -> new CorralPane(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.GLASS_PANE).strength(4, 3000).noOcclusion().isSuffocating(ALWAYS_FALSE).isViewBlocking(ALWAYS_FALSE)));
        public static final RegistryObject<Block> SLIME_SPAWNER = R.block("slime_spawner", () -> new SlimeSpawnerBlock(defaultBehavior.strength(4, 3000).noOcclusion()));

        private static void bootstrap() {
        }
    }

    public static class BlockEntities {
        public static final RegistryObject<BlockEntityType<SlimeIncubatorBlockEntity>> SLIME_INCUBATOR = R.blockEntity("slime_incubator",
                () -> new TickingBlockEntityType<>(SlimeIncubatorBlockEntity::new, ImmutableSet.of(ModElements.Blocks.SLIME_INCUBATOR.get()), false, true));
        public static final RegistryObject<BlockEntityType<PlortPressBlockEntity>> PLORT_PRESS = R.blockEntity("plort_press",
                () -> new TickingBlockEntityType<>(PlortPressBlockEntity::new, ImmutableSet.of(Blocks.PLORT_PRESS.get()), false, true));
        public static final RegistryObject<BlockEntityType<PlortRippitBlockEntity>> PLORT_RIPPIT = R.blockEntity("plort_rippit",
                () -> new TickingBlockEntityType<>(PlortRippitBlockEntity::new, ImmutableSet.of(Blocks.PLORT_RIPPIT.get()), false, true));
        public static final RegistryObject<BlockEntityType<SlimeFeederBlockEntity>> SLIME_FEEDER = R.blockEntity("slime_feeder",
                () -> new TickingBlockEntityType<>(SlimeFeederBlockEntity::new, ImmutableSet.of(Blocks.SLIME_FEEDER.get()), false, true));
        public static final RegistryObject<BlockEntityType<SlimeSpawnerBlockEntity>> SLIME_SPAWNER = R.blockEntity("slime_spawner",
                () -> new TickingBlockEntityType<>(SlimeSpawnerBlockEntity::new, ImmutableSet.of(ModElements.Blocks.SLIME_SPAWNER.get()), false, true));

        private static void bootstrap() {
        }
    }

    public static class Items {
        public static final RegistryObject<BlockItem> SLIME_INCUBATOR = R.item("slime_incubator", () -> new BlockItem(Blocks.SLIME_INCUBATOR.get(), new Item.Properties()));
        public static final RegistryObject<BlockItem> PLORT_PRESS = R.item("plort_press", () -> new BlockItem(Blocks.PLORT_PRESS.get(), new Item.Properties()));
        public static final RegistryObject<BlockItem> PLORT_RIPPIT = R.item("plort_rippit", () -> new BlockItem(Blocks.PLORT_RIPPIT.get(), new Item.Properties()));
        public static final RegistryObject<BlockItem> SLIME_FEEDER = R.item("slime_feeder", () -> new BlockItem(Blocks.SLIME_FEEDER.get(), new Item.Properties()));
        public static final RegistryObject<BlockItem> CORRAL_BLOCK = R.item("corral_block", () -> new BlockItem(Blocks.CORRAL_BLOCK.get(), new Item.Properties()));
        public static final RegistryObject<BlockItem> CORRAL_PANE = R.item("corral_pane", () -> new BlockItem(Blocks.CORRAL_PANE.get(), new Item.Properties()));
        public static final RegistryObject<BlockItem> SLIME_SPAWNER = R.item("slime_spawner", () -> new BlockItem(Blocks.SLIME_SPAWNER.get(), new Item.Properties()));
        public static final RegistryObject<PlortItem> PLORT = R.item("plort", () -> new PlortItem(new Item.Properties().stacksTo(64)));
        public static final RegistryObject<HatItem> HAT = R.item("hat", () -> new HatItem(new Item.Properties().stacksTo(64)));
        public static final RegistryObject<SlimeHeartItem> SLIME_HEART = R.item("slime_heart", () -> new SlimeHeartItem(new Item.Properties().stacksTo(64)));
        public static final RegistryObject<SlimeInventoryItem> SLIME_ITEM = R.item("slime_item", () -> new SlimeInventoryItem(new Item.Properties().stacksTo(1)));
        public static final RegistryObject<SlimeSpawnEggItem> SPLENDID_SLIME_SPAWN_EGG = R.item("spawn_egg_splendid_slime", () -> new SlimeSpawnEggItem(slimeEntity, 0xff7d9d, 0xff7d9d, new Item.Properties()));
        public static final RegistryObject<ForgeSpawnEggItem> TARR_SPAWN_EGG = R.item("spawn_egg_tarr", () -> new ForgeSpawnEggItem(tarrEntity, 0x2c221c, 0x921f78, new Item.Properties()));
        public static final RegistryObject<Item> TARRTAR = R.item("tarrtar", () -> new Item(new Item.Properties().stacksTo(64)));
        public static final RegistryObject<Item> SLIME_CANDY = R.item("slime_candy", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> ROCKET_POD = R.item("rocket_pod", () -> new Item(new Item.Properties().stacksTo(64)));
        public static final RegistryObject<SlimeVac> SLIME_VAC = R.item("slime_vac", () -> new SlimeVac(new Item.Properties().stacksTo(1)));
        public static final RegistryObject<SlimeInspector> SLIME_INSPECTOR = R.item("slime_inspector", () -> new SlimeInspector(new Item.Properties().stacksTo(1)));

        private static void bootstrap() {
        }
    }

    public static class Entities {
        public static final RegistryObject<EntityType<SlimeEntityBase>> SPLENDID_SLIME = slimeEntity;
        public static final RegistryObject<EntityType<SlimeEntityBase>> TARR = tarrEntity;
        public static final RegistryObject<EntityType<ItemProjectileEntity>> ITEM_PROJECTILE = R.entity("item_projectile", () -> EntityType.Builder.<ItemProjectileEntity>of(ItemProjectileEntity::new, MobCategory.MISC).build("item_projectile"));

        private static void bootstrap() {
        }
    }

    public static class Menus {
        public static final RegistryObject<MenuType<PlortPressMenu>> PLORT_PRESS_MENU = R.menu("plort_press_menu", () -> MenuUtil.bufType(PlortPressMenu::new));

        private static void bootstrap() {
        }
    }

    public static class Particles {
        public static final RegistryObject<SimpleParticleType> VAC_SUCK = R.particle("vac_suck", () -> new SimpleParticleType(false));
        private static void bootstrap() {

        }
    }

    public static class Recipes {
        public static final RegistryObject<RecipeSerializer<PlortPressingRecipe>> PLORT_PRESSING_SERIALIZER = R.recipeSerializer("plort_pressing", PlortPressingRecipe.Serializer::new);
        public static final RegistryObject<RecipeSerializer<PlortRippingRecipe>> PLORT_RIPPING_SERIALIZER = R.recipeSerializer("plort_ripping", PlortRippingRecipe.Serializer::new);

        private static void bootstrap() {

        }
    }

    public static class Tabs {
        public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(SplendidSlimes.MODID, "tab"));

        public static final RegistryObject<CreativeModeTab> AB = R.tab("tab",
                () -> CreativeModeTab.builder().title(Component.translatable("itemGroup." + SplendidSlimes.MODID)).icon(() -> Items.SLIME_INCUBATOR.get().getDefaultInstance()).build());

        private static void bootstrap() {
        }
    }

    public static void bootstrap() {
        Blocks.bootstrap();
        BlockEntities.bootstrap();
        Items.bootstrap();
        Entities.bootstrap();
        Menus.bootstrap();
        Particles.bootstrap();
        Recipes.bootstrap();
        Tabs.bootstrap();
    }
}