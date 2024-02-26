package cn.seiua.skymatrix.mixin.mixins;

import cn.seiua.skymatrix.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {


    @Inject(at = @At("HEAD"), method = "run")
    public void run(CallbackInfo ci) {
        System.setProperty("java.awt.headless", "false");
    }

    @Inject(at = @At("HEAD"), method = "close")
    public void close(CallbackInfo ci) {
        new GameExitEvent().call();
    }

    @Inject(at = @At("HEAD"), method = "joinWorld")
    public void joinWorld(ClientWorld world, CallbackInfo ci) {
        new WorldChangeEvent(world).call();
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void onEndTick(CallbackInfo info) {
        new ClientTickEvent().call();
    }

    @Inject(
            at = @At(value = "FIELD",
                    target = "net/minecraft/client/MinecraftClient.overlay : Lnet/minecraft/client/gui/screen/Overlay;",
                    opcode = Opcodes.GETFIELD,
                    shift = At.Shift.AFTER,
                    ordinal = 0),
            method = "tick")
    private void onTick(CallbackInfo ci) {
        new HandleKeyInputBeforeEvent().call();
    }


    @Inject(at = @At("HEAD"), method = "setScreen", cancellable = true)
    private void setScreen(CallbackInfo info) {
        OpenScreenEvent e = new OpenScreenEvent();
        e.call();
        if (e.isCancelled()) info.cancel();
    }


}
