package cn.seiua.skymatrix.mixin.mixins;

import cn.seiua.skymatrix.client.module.modules.render.FairyEsp;
import cn.seiua.skymatrix.event.events.ServerPacketEvent;
import net.fabricmc.fabric.mixin.client.message.ClientPlayNetworkHandlerMixin;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

}
