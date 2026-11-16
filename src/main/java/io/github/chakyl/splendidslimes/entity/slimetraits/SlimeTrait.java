package io.github.chakyl.splendidslimes.entity.slimetraits;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum SlimeTrait implements StringRepresentable {
    AQUATIC,
    BOISTEROUS,
    DEFIANT,
    DOMINANT,
    RECESSIVE,
    EXPLOSIVE,
    FERAL,
    FLAMING,
    FLOATING,
    GREEDY,
    HANGRY,
    FOODPORTING,
    FRIENDLY,
    DIVERSE,
    HANDY,
    INVERSE,
    LARGOLESS,
    MOODY,
    NUCLEAR,
    POMPOUS,
    PHOTOSYNTHESIZING,
    PUTRID,
    SPIKY,
    SHY,
    WEEPING;

    public static final Codec<SlimeTrait> CODEC = StringRepresentable.fromEnum(SlimeTrait::values);

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}