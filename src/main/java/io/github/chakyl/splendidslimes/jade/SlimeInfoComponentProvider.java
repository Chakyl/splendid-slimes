package io.github.chakyl.splendidslimes.jade;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.entity.SlimeEntityBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import static io.github.chakyl.splendidslimes.util.SlimeData.getSlimeData;

public enum SlimeInfoComponentProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            EntityAccessor entityAccessor,
            IPluginConfig config
    ) {
        boolean isLargo = entityAccessor.getServerData().contains("SecondaryBreed") && !entityAccessor.getServerData().getString("SecondaryBreed").isEmpty();
        if (entityAccessor.getServerData().contains("Breed")) {
            DynamicHolder<SlimeBreed> slime = getSlimeData(entityAccessor.getServerData().getString("Breed"));
            if (slime.isBound()) {
                if (!isLargo) {
                    tooltip.add(Component.translatable("entity.splendid_slimes.diet", slime.get().diet()));
                } else {
                    tooltip.add(Component.translatable("entity.splendid_slimes.largo_diet", slime.get().diet(), getSlimeData(entityAccessor.getServerData().getString("SecondaryBreed")).get().diet()));
                }
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag data, EntityAccessor accessor) {
        SlimeEntityBase slime = (SlimeEntityBase) accessor.getEntity();
        data.putString("Breed", slime.getEntityData().get(SlimeEntityBase.BREED));
        data.putString("SecondaryBreed", slime.getEntityData().get(SlimeEntityBase.SECONDARY_BREED));
    }
    @Override
    public ResourceLocation getUid() {
        return SlimeInfoPlugin.UID;
    }

}