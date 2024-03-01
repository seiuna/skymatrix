package cn.seiua.skymatrix.mixin.mixins;

import cn.seiua.skymatrix.event.events.ClientPacketEvent;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonNetworkHandler.class)
public abstract class MixinClientCommonNetworkHandler {

    @Inject(at = @At("HEAD"), method = "sendPacket(Lnet/minecraft/network/packet/Packet;)V", cancellable = true)
    public void onPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        ClientPacketEvent packetEvent = new ClientPacketEvent(packet);
        packetEvent.call();
        if (packetEvent.isCancelled()) callbackInfo.cancel();
    }

}
