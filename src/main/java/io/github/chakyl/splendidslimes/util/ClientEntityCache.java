package io.github.chakyl.splendidslimes.util;

import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Caches and retains client entities for rendering.
 * <p>
 * Entities are cleared on level unload.
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = SplendidSlimes.MODID)
public class ClientEntityCache {

    private static final Map<Level, Map<EntityType<?>, LivingEntity>> CACHE = new IdentityHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity> T computeIfAbsent(Level level, CompoundTag displayNbt) {
        var map = CACHE.computeIfAbsent(level, l -> new IdentityHashMap<>());
        EntityType<T> type = (EntityType<T>) ModElements.Entities.SPLENDID_SLIME.value();
        return (T) map.computeIfAbsent(type, k -> {
            T t = type.create(level);
            if (t != null) {
                t.load(displayNbt);
            }
            return t;
        });
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Pre event) {
        CACHE.values().stream().map(Map::values).flatMap(Collection::stream).forEach(e -> e.tickCount++);
    }

    @SubscribeEvent
    public static void unload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            CACHE.remove(event.getLevel());
        }
    }
}