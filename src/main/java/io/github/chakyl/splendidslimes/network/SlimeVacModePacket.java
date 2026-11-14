//package io.github.chakyl.splendidslimes.network;
//
//import io.github.chakyl.splendidslimes.SplendidSlimes;
//import io.github.chakyl.splendidslimes.item.SlimeVac;
//import net.minecraft.core.Holder;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.network.chat.Component;
//import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
//import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
//import net.minecraft.network.protocol.game.ClientboundSoundPacket;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.sounds.SoundEvent;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.world.item.ItemStack;
//import net.minecraftforge.network.NetworkEvent;
//
//import java.util.function.Supplier;
//
//public class SlimeVacModePacket {
//
//    public SlimeVacModePacket() {}
//
//    public SlimeVacModePacket(FriendlyByteBuf buffer) {}
//
//    public void encode(FriendlyByteBuf buffer) {}
//
//    public void handle(Supplier<NetworkEvent.Context> context){
//        ServerPlayer player = context.get().getSender();
//        if (player != null) {
//            ItemStack vacItem = player.getMainHandItem();
//            if (!(vacItem.getItem() instanceof SlimeVac)) {
//                vacItem = player.getOffhandItem();
//            }
//
//            if (!(vacItem.getItem() instanceof SlimeVac)) return;
//
//            SlimeVac.VacMode mode = SlimeVac.getMode(vacItem);
//            switch (mode) {
//                case BOTH -> SlimeVac.setMode(vacItem, SlimeVac.VacMode.SLIME);
//                case SLIME -> SlimeVac.setMode(vacItem, SlimeVac.VacMode.ITEM);
//                case ITEM -> SlimeVac.setMode(vacItem, SlimeVac.VacMode.BOTH);
//            }
//            Float pitch = 0F;
//            switch (mode) {
//                case BOTH -> pitch = 0.7F;
//                case SLIME -> pitch = 0.8F;
//                case ITEM -> pitch = 0.9F;
//            }
//            player.connection.send(new ClientboundSoundPacket(Holder.direct(SoundEvents.NOTE_BLOCK_XYLOPHONE.get()), SoundSource.PLAYERS, player.getX(),player.getY(),player.getZ(), 1.0F, pitch, 1));
//            player.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("info.splendid_slimes.slime_vac.mode_set", SlimeVac.getMode(vacItem))));
//        }
//
//    }
//}
