package io.github.chakyl.splendidslimes.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import dev.shadowsoffire.placebo.reload.DynamicRegistry;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static io.github.chakyl.splendidslimes.SplendidSlimes.loc;

public class SlimeBreedRegistry extends DynamicRegistry<SlimeBreed> {

    public static final SlimeBreedRegistry INSTANCE = new SlimeBreedRegistry();

    private Map<String, SlimeBreed> slimesByBreed = new HashMap<>();

    public SlimeBreedRegistry() {
        super(SplendidSlimes.LOGGER, "slimes", true, false);
    }

    @Override
    protected void registerBuiltinCodecs() {
        this.registerDefaultCodec(loc("slime_breeds"), SlimeBreed.CODEC);
    }

    @Override
    protected void beginReload() {
        super.beginReload();
        this.slimesByBreed = new HashMap<>();
    }

    @Override
    protected void onReload() {
        super.onReload();
        this.slimesByBreed = ImmutableMap.copyOf(this.slimesByBreed);
    }

    @Override
    protected void validateItem(ResourceLocation key, SlimeBreed slimeBreed) {
        slimeBreed.validate(key);
        if (this.slimesByBreed.containsKey(slimeBreed.breed())) {
            String msg = "Attempted to register two slimes (%s and %s) for Entity Type %s!";
            throw new UnsupportedOperationException(String.format(msg, key, this.getKey(this.slimesByBreed.get(slimeBreed.breed())), slimeBreed.breed()));
        }
        this.slimesByBreed.put(slimeBreed.breed(), slimeBreed);
    }

    @Nullable
    public SlimeBreed getForEntity(EntityType<?> type) {
        return this.slimesByBreed.get(type);
    }

    @Override
    public Map<ResourceLocation, JsonElement> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return super.prepare(pResourceManager, pProfiler);
    }

}