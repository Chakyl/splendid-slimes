package io.github.chakyl.splendidslimes.item;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.tabs.ITabFiller;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.data.SlimeBreedRegistry;
import io.github.chakyl.splendidslimes.entity.SplendidSlime;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

import static io.github.chakyl.splendidslimes.util.SlimeData.getSlimeData;

public class SlimeInventoryItem extends Item implements ITabFiller {
    public static final String SLIME = "slime";
    public static final String ID = "id";
    public static final String DATA = "data";

    public SlimeInventoryItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> list, TooltipFlag pFlag) {
        DynamicHolder<SlimeBreed> slime = getSlimeData(pStack, SLIME);
        Component slimeDiet;
        List<String> traits = new ArrayList<>();
        if (!slime.isBound()) {
            slimeDiet = Component.translatable("diet.splendid_slimes.default_diet");
        } else {
            slimeDiet = slime.get().diet();
            traits = slime.get().traits();
        }
        if (!traits.isEmpty()) {
            if (Screen.hasShiftDown()) {
                for (String trait : traits) {
                    list.add(Component.translatable("trait.splendid_slimes." + trait + ".name").withStyle(ChatFormatting.GRAY));
                    list.add(Component.translatable("trait.splendid_slimes." + trait + ".info").withStyle(ChatFormatting.DARK_GRAY));
                }
            } else {
                list.add(Component.translatable("entity.splendid_slimes.diet", slimeDiet).withStyle(ChatFormatting.GRAY));
                list.add(Component.translatable("item.splendid_slimes.slime_item.tooltip_prompt"));
            }
        } else {
            list.add(Component.translatable("entity.splendid_slimes.diet", slimeDiet).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, CreativeModeTab.Output output) {
        SlimeBreedRegistry.INSTANCE.getKeys().stream().sorted().forEach(key -> {
            ItemStack s = new ItemStack(this);
            setStoredSlime(s, key);
            output.accept(s);
        });
    }

    @Override
    public Component getName(ItemStack pStack) {
        DynamicHolder<SlimeBreed> slime = getSlimeData(pStack, SLIME);
        Component slimeName;
        if (!slime.isBound()) {
            slimeName = Component.translatable("item.splendid_slimes.default_slime_item");
        } else slimeName = slime.get().name();
        return Component.translatable(this.getDescriptionId(pStack), slimeName);
    }

    public static void setStoredSlime(ItemStack stack, SlimeBreed slimeBreed) {
        setStoredSlime(stack, SlimeBreedRegistry.INSTANCE.getKey(slimeBreed));
    }

    public static void setStoredSlime(ItemStack stack, ResourceLocation plort) {
        stack.removeTagKey(SLIME);
        stack.getOrCreateTagElement(SLIME).putString(ID, plort.toString());
    }

    public static SplendidSlime getSlimeFromItem(CompoundTag data, CompoundTag slimeData, Level level) {
        if (!slimeData.isEmpty() || !data.isEmpty()) {
            SplendidSlime slimeEntity = new SplendidSlime(ModElements.Entities.SPLENDID_SLIME.get(), level);
            if (!data.isEmpty()) {
                slimeEntity.load(data);
            } else {
                slimeEntity.setSlimeBreed(slimeData.getString("id"));
            }
            return slimeEntity;
        }
        return null;
    }

    public static int getData(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(SLIME);
        return stack.isEmpty() || tag == null ? 0 : tag.getInt(DATA);
    }

    public static void setData(ItemStack stack, int data) {
        stack.getOrCreateTagElement(SLIME).putInt(DATA, data);
    }

}