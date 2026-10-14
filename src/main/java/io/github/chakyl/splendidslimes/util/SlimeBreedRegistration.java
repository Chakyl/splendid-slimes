//package io.github.chakyl.splendidslimes.util;
//
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import net.minecraft.core.particles.SimpleParticleType;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.effect.MobEffect;
//import net.minecraft.world.effect.MobEffectInstance;
//import net.minecraftforge.registries.ForgeRegistries;
// TODO: Don't need this on 1.21.1?
//public class SlimeBreedRegistration {
//    public static JsonObject getEffectJson(Object effect, boolean isInfinite) {
//        JsonObject jsonEffect = new JsonObject();
//        if (effect.getClass() == MobEffectInstance.class) {
//            jsonEffect.addProperty("effect", BuiltInRegistries.MOB_EFFECT.getKey(((MobEffectInstance) effect).getEffect()).toString());
//            if (!isInfinite) jsonEffect.addProperty("duration", ((MobEffectInstance) effect).getDuration());
//            jsonEffect.addProperty("amplifier", ((MobEffectInstance) effect).getAmplifier());
//        }
//        return jsonEffect;
//    }
//
//    public static MobEffectInstance getEffectFromJson(JsonElement jsonElement, boolean isInfinite) {
//        MobEffect postiveEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("minecraft:slowness"));
//        int duration = isInfinite ? -1 : 40;
//        int amplifier = 0;
//        if (jsonElement.getAsJsonObject().has("effect"))
//            postiveEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(jsonElement.getAsJsonObject().get("effect").getAsString()));
//        if (!isInfinite && jsonElement.getAsJsonObject().has("duration"))
//            duration = jsonElement.getAsJsonObject().get("duration").getAsInt();
//        if (jsonElement.getAsJsonObject().has("amplifier"))
//            amplifier = jsonElement.getAsJsonObject().get("amplifier").getAsInt();
//        return new MobEffectInstance(postiveEffect, duration, amplifier, isInfinite, true);
//    }
//
//    public static String getParticleTypeJson(SimpleParticleType particleType) {
//        return ForgeRegistries.PARTICLE_TYPES.getKey(particleType).toString();
//    }
//
//    public static SimpleParticleType getParticleTypeFromJson(JsonElement jsonElement) {
//        return (SimpleParticleType) ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(jsonElement.getAsString()));
//    }
//}
