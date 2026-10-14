package io.github.chakyl.splendidslimes.jade;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.blockentity.SlimeSpawnerBlockEntity;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import static io.github.chakyl.splendidslimes.util.SlimeData.getSlimeData;

public enum SlimeSpawnerInfoComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config
    ) {
        if (accessor.getServerData().contains("slimeType")) {
            DynamicHolder<SlimeBreed> slime = getSlimeData(accessor.getServerData().getString("slimeType"));
            if (slime.isBound()) {
                tooltip.add(Component.translatable("block.splendid_slimes.slime_spawner.breed", slime.get().name()));
            } else {
                tooltip.add(Component.translatable("block.splendid_slimes.slime_spawner.breed_unset"));
            }
        } else {
            tooltip.add(Component.translatable("block.splendid_slimes.slime_spawner.breed_unset"));
        }
        if (accessor.getServerData().contains("slimeCount")) {
            tooltip.add(Component.translatable("block.splendid_slimes.slime_spawner.slime_count", accessor.getServerData().getInt("slimeCount")));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        SlimeSpawnerBlockEntity spawner = (SlimeSpawnerBlockEntity) accessor.getBlockEntity();
        data.putString("slimeType", spawner.getSlimeType());
        data.putInt("slimeCount", spawner.getSlimeCount());
    }

    @Override
    public ResourceLocation getUid() {
        return SlimeInfoPlugin.SLIME_SPAWNER_UID;
    }

}