package io.github.chakyl.splendidslimes.jade;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.SlimyConfig;
import io.github.chakyl.splendidslimes.blockentity.SlimeIncubatorBlockEntity;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import static io.github.chakyl.splendidslimes.util.SlimeData.getSlimeData;

public enum SlimeIncubatorInfoComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
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
                tooltip.add(Component.translatable("block.splendid_slimes.slime_incubator.breed_unset"));
            }
        } else {
            tooltip.add(Component.translatable("block.splendid_slimes.slime_incubator.breed_unset"));
        }
        if (accessor.getServerData().contains("progress") && accessor.getServerData().getInt("progress") > 0) {
            int totalSeconds = SlimyConfig.incubationTime / 20;
            int currentSeconds = accessor.getServerData().getInt("progress") / 20;
            int remainingSeconds = totalSeconds - currentSeconds;

            if (remainingSeconds > 0) {
                int minutes = remainingSeconds / 60;
                remainingSeconds %= 60;

                String formattedTime = String.format("%d:%02d", minutes, remainingSeconds);
                tooltip.add(Component.translatable("block.splendid_slimes.slime_incubator.progress", formattedTime));
            } else {
                tooltip.add(Component.translatable("block.splendid_slimes.slime_incubator.progress", "0:00 Seconds"));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        SlimeIncubatorBlockEntity incubatorBlockEntity = (SlimeIncubatorBlockEntity) accessor.getBlockEntity();
        data.putString("slimeType", incubatorBlockEntity.getSlimeType());
        data.putInt("progress", incubatorBlockEntity.getProgress());
    }

    @Override
    public ResourceLocation getUid() {
        return SlimeInfoPlugin.UID;
    }
}