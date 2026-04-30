package io.github.chakyl.splendidslimes.events;

import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.entity.SlimeEntityBase;
import io.github.chakyl.splendidslimes.item.SlimeVac;
import io.github.chakyl.splendidslimes.registry.ModElements;
import io.github.chakyl.splendidslimes.tag.SplendidSlimesItemTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerForgeEvents {
    @Mod.EventBusSubscriber(modid = SplendidSlimes.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onUse(PlayerInteractEvent event) {
            LivingEntity player = event.getEntity();
            ItemStack eventItem = event.getItemStack();
            if (player instanceof Player && eventItem.is(SplendidSlimesItemTags.SLIME_VAC_FIREABLE)) {
                if (!event.isCancelable() || eventItem.getItem() instanceof SlimeVac) return;
                if (player.getOffhandItem().getItem() instanceof SlimeVac || player.getMainHandItem().getItem() instanceof SlimeVac) {
                    event.setCanceled(true);
                }
            }
        }

//        private static final Map<UUID, SlimeEntityBase> TRACKED_LARGOS = new HashMap<>();
//
//        @SubscribeEvent
//        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
//            if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
//
//            ServerPlayer player = (ServerPlayer) event.player;
//            ItemStack stack = player.getMainHandItem();
//            UUID uuid = player.getUUID();
//
//            if (stack.hasTag() && stack.getTag().getBoolean("RenderChicken")) {
//                SlimeEntityBase largo = TRACKED_LARGOS.get(uuid);
//
//                if (largo == null || !largo.isAlive()) {
//                    largo = ModElements.Entities.SPLENDID_SLIME.get().create(player.level());
//                    if (largo != null) {
//                        largo.setNoAi(true);
//                        largo.setInvulnerable(true);
//                        largo.setPersistenceRequired();
//                        player.level().addFreshEntity(largo);
//                        TRACKED_LARGOS.put(uuid, largo);
//                    }
//                }
//
//                if (largo != null) {
//                    Vec3 look = player.getLookAngle();
//                    double x = player.getX() + look.x * 2.0;
//                    double y = player.getEyeY() + look.y * 2.0 - (largo.getBbHeight() / 2.0);
//                    double z = player.getZ() + look.z * 2.0;
//
//                    largo.moveTo(x, y, z, player.getYRot() + 180f, player.getXRot());
//                }
//            } else {
//                SlimeEntityBase largo = TRACKED_LARGOS.remove(uuid);
//                if (largo != null) {
//                    largo.discard();
//                }
//            }
//        }
    }
}
