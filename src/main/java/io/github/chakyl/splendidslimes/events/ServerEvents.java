package io.github.chakyl.splendidslimes.events;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.item.SlimeInventoryItem;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

import java.util.ArrayList;
import java.util.List;

//import static io.github.chakyl.splendidslimes.util.SlimeVacUtils.dropLargo;
// TODO: Slime Vac
@EventBusSubscriber(modid = SplendidSlimes.MODID)
public class ServerEvents {
    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Post event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide()) return;
        ItemStack stack = event.getOriginalStack();
        if (stack.isEmpty() || !(stack.getItem() instanceof SlimeInventoryItem)) return;
        DynamicHolder<SlimeBreed> breed = SlimeInventoryItem.getStoredSlime(stack);
        String slimeId = breed.getId().toString();
        List<String> slimepediaData = new ArrayList<>(player.getData(ModElements.Attachments.SLIMEPEDIA_DATA));
        if (!slimepediaData.contains(slimeId)) {
            slimepediaData.add(slimeId);
            player.setData(ModElements.Attachments.SLIMEPEDIA_DATA, slimepediaData);
            player.displayClientMessage(Component.literal("Unlocked " + breed.get().name().getString() + " Slime's entry in the Slimepedia"), true);
        }
    }
//        @SubscribeEvent
//        public static void onUse(PlayerInteractEvent event) {
//            LivingEntity player = event.getEntity();
//            ItemStack eventItem = event.getItemStack();
//            if (player instanceof Player && eventItem.is(SplendidSlimesItemTags.SLIME_VAC_FIREABLE)) {
//                if (!event.isCancelable() || eventItem.getItem() instanceof SlimeVac) return;
//                if (player.getOffhandItem().getItem() instanceof SlimeVac || player.getMainHandItem().getItem() instanceof SlimeVac) {
//                    event.setCanceled(true);
//                }
//            }
//        }

//        @SubscribeEvent
//        public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
//            if (!(event.getEntity() instanceof Player player)) return;
//            if (player.level().isClientSide) return;
//            if (event.getSlot() == EquipmentSlot.MAINHAND) {
//                ItemStack oldStack = event.getFrom();
//                ItemStack newStack = event.getTo();
//
//                if (oldStack.getItem() instanceof SlimeVac) {
//                    if (newStack.getItem() != oldStack.getItem()) {
//                        dropLargo(player.level(), player, oldStack);
//                    }
//                }
//            }
//        }

}
