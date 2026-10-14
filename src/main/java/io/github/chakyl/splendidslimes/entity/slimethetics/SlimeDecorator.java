package io.github.chakyl.splendidslimes.entity.slimethetics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.Optional;

public class SlimeDecorator {
    private Item decorationItem;
    private float scale;
    private float xOffset;
    private float yOffset;
    private float zOffset;

    public SlimeDecorator(Item decorationItem, float scale, float xOffset, float yOffset, float zOffset) {
        this.decorationItem = decorationItem;
        this.scale = scale;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }

    public Item getDecorationItem() {
        return decorationItem;
    }

    public void setDecorationItem(Item decorationItem) {
        this.decorationItem = decorationItem;
    }

    public static SlimeDecorator getDefaultDecorator() {
        return new SlimeDecorator(ModElements.Items.DECORATOR.value(), 1.0f, 0.0f, -1.0f, -0.05f);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getxOffset() {
        return xOffset;
    }

    public void setxOffset(float xOffset) {
        this.xOffset = xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    public void setyOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    public float getzOffset() {
        return zOffset;
    }

    public void setzOffset(float zOffset) {
        this.zOffset = zOffset;
    }

    public static final Codec<SlimeDecorator> CODEC = RecordCodecBuilder.create(inst -> inst.group(BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("item").forGetter(decorator -> Optional.of(decorator.getDecorationItem())),
            Codec.floatRange(-4.0f, 4.0f).fieldOf("scale").orElse(1.0f).forGetter(SlimeDecorator::getScale),
            Codec.floatRange(-4.0f, 4.0f).fieldOf("x_offset").orElse(0.0f).forGetter(SlimeDecorator::getxOffset),
            Codec.floatRange(-4.0f, 4.0f).fieldOf("y_offset").orElse(-1.0f).forGetter(SlimeDecorator::getyOffset),
            Codec.floatRange(-4.0f, 4.0f).fieldOf("z_offset").orElse(-0.05f).forGetter(SlimeDecorator::getzOffset)
    ).apply(inst, (itemOpt, scale, x, y, z) -> new SlimeDecorator(itemOpt.orElseGet(() -> ModElements.Items.DECORATOR.value()), scale, x, y, z)));
}