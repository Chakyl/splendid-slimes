package io.github.chakyl.splendidslimes;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SlimyConfig {
    //Slimes
    public  final ModConfigSpec.IntValue SLIME_HUNGER_AMOUNT;
    public  final ModConfigSpec.IntValue SLIME_MAX_HAPPINESS;
    public  final ModConfigSpec.IntValue SLIME_HAPPY_THRESHOLD;
    public  final ModConfigSpec.IntValue SLIME_UNHAPPY_THRESHOLD;
    public  final ModConfigSpec.IntValue SLIME_FURIOUS_THRESHOLD;
    public  final ModConfigSpec.IntValue SLIME_EFFECT_COOLDOWN;
    public  final ModConfigSpec.BooleanValue SLIME_OWNER_OFFLINE_CHECK;

    // Tarrs
    public  final ModConfigSpec.BooleanValue ENABLE_TARRS;
    public  final ModConfigSpec.BooleanValue ENABLE_FORGIVING_TARRS;

    // Machine
    public  final ModConfigSpec.IntValue INCUBATION_TIME;
    public  final ModConfigSpec.IntValue PLORT_PRESSING_TIME;


    public SlimyConfig(final ModConfigSpec.Builder builder) {
        builder.comment("꒷꒦꒷꒦꒷꒦꒷꒦꒷꒦꒷꒦ Splendid Slimes Config! ꒦꒷꒦꒷꒦꒷꒦꒷꒦꒷꒦꒷");

        builder.push("slimes");

        SLIME_HUNGER_AMOUNT = builder
                .comment("How much a full slime's hunger will equate to")
                .defineInRange("Slime Hunger Amount", 24000, 20, Integer.MAX_VALUE);

        SLIME_MAX_HAPPINESS = builder
                .comment("Maximum happiness value for a Splendid Slime.")
                .defineInRange("Slime Max Happiness", 1000, 3, Integer.MAX_VALUE);

        SLIME_HAPPY_THRESHOLD = builder
                .comment("Minimum amount of happiness a Splendid Slime can have before being considered 'Happy'.")
                .defineInRange("Slime Happy Threshold", 600, 0, Integer.MAX_VALUE);

        SLIME_UNHAPPY_THRESHOLD = builder
                .comment("Maximum amount of happiness a Splendid Slime can have before being considered 'Unhappy'.")
                .defineInRange("Slime Unhappy Threshold", 400, 0, Integer.MAX_VALUE);

        SLIME_FURIOUS_THRESHOLD = builder
                .comment("Maximum amount of happiness a Splendid Slime can have before being considered 'Furious'.")
                .defineInRange("Slime Furious Threshold", 200, 0, Integer.MAX_VALUE);

        SLIME_EFFECT_COOLDOWN = builder
                .comment("How many ticks it takes for a Splendid Slime to try and perform Negative/Positive effects/commands. Setting this value too low may cause performance issues.")
                .defineInRange("Slime Effect Cooldown", 800, 20, Integer.MAX_VALUE);

        SLIME_OWNER_OFFLINE_CHECK = builder
                .comment("When true, Slimes will not get hungry or perform effects when their owner is offline. This can help prevent disasters on Multiplayer servers and has no effect in single player.")
                .define("Slime Owner Offline Check", true);

        builder.pop();

        builder.push("tarrs");

        ENABLE_TARRS = builder
                .comment("If true, Largo Slimes will turn into Tarrs after eating a 3rd Plort instead of just dying.")
                .define("Enable Tarrs", true);

        ENABLE_FORGIVING_TARRS = builder
                .comment("If true, Tarrs will be persistent and turn back into previous slime when killed")
                .define("Enable Forgiving Tarrs", false);

        builder.pop();

        builder.push("machines");

        INCUBATION_TIME = builder
                .comment("Time it takes for Splendid Slimes to incubate in Slime Incubator")
                .defineInRange("Slime Incubation Time", 6000, 1, Integer.MAX_VALUE);

        PLORT_PRESSING_TIME = builder
                .comment("Time it takes to craft items in a Plort Press")
                .defineInRange("Plort Pressing Time", 1200, 20, Integer.MAX_VALUE);

        builder.pop();

    }

}