package io.github.chakyl.splendidslimes.item;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.tabs.ITabFiller;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import io.github.chakyl.splendidslimes.entity.SplendidSlime;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class SlimeSpawnEggItem extends DeferredSpawnEggItem implements ITabFiller {
    public SlimeSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Item.Properties props) {
        super(type, backgroundColor, highlightColor, props);
    }

    @Override
    public void fillItemCategory(CreativeModeTab creativeModeTab, BuildCreativeModeTabContentsEvent event) {
        SlimeBreedRegistry.INSTANCE.getKeys().stream().sorted().map(SlimeBreedRegistry.INSTANCE::holder).forEach(holder -> {
            ItemStack s = new ItemStack(this);
            setStoredSlime(s, holder);
            event.accept(s);
        });

    }

    @Override
    public Component getName(ItemStack pStack) {
        DynamicHolder<SlimeBreed> slime = getStoredSlime(pStack);
        MutableComponent slimeName;
        if (!slime.isBound()) {
            slimeName = Component.literal("BROKEN").withStyle(ChatFormatting.OBFUSCATED);
        } else slimeName = slime.get().name();
        return Component.translatable(this.getDescriptionId(pStack), slimeName);
    }


    public static DynamicHolder<SlimeBreed> setStoredSlime(ItemStack stack) {
        return stack.getOrDefault(ModElements.Components.SLIME_BREED, SlimeBreedRegistry.INSTANCE.emptyHolder());
    }

    public static void setStoredSlime(ItemStack stack, SlimeBreed slimeBreed) {
        setStoredSlime(stack, SlimeBreedRegistry.INSTANCE.holder(slimeBreed));
    }

    public static void setStoredSlime(ItemStack stack, DynamicHolder<SlimeBreed> slimeBreed) {
        stack.set(ModElements.Components.SLIME_BREED, slimeBreed);
    }

    public static DynamicHolder<SlimeBreed> getStoredSlime(ItemStack stack) {
        return stack.getOrDefault(ModElements.Components.SLIME_BREED, SlimeBreedRegistry.INSTANCE.emptyHolder());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack itemstack = context.getItemInHand();
            BlockPos blockpos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockstate = level.getBlockState(blockpos);
            BlockPos blockpos1;
            if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
                blockpos1 = blockpos;
            } else {
                blockpos1 = blockpos.relative(direction);
            }
            EntityType<?> entitytype = this.getType(itemstack);
            Entity slime = entitytype.spawn((ServerLevel) level, itemstack, context.getPlayer(), blockpos1, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);
            if (slime != null) {
                DynamicHolder<SlimeBreed> slimeBreed = getStoredSlime(itemstack);
                if (slimeBreed.isBound() && slime instanceof SplendidSlime slimeEntity) {
                     slimeEntity.setSlimeBreed(slimeBreed.getId().toString());
                }
                itemstack.shrink(1);
                level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
            }

            return InteractionResult.CONSUME;
        }
    }

}
