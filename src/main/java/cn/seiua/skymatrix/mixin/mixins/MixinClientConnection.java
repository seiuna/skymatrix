package cn.seiua.skymatrix.mixin.mixins;

import cn.seiua.skymatrix.event.events.ServerPacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Inject(at = @At("HEAD"),
            method = "handlePacket",
            cancellable = true)
    private static  void handlePacket(Packet packet, PacketListener listener, CallbackInfo ci) {
            ServerPacketEvent s=  new ServerPacketEvent(packet);
            s.call();
            if(s.isCancelled())ci.cancel();
    }
}
