package cn.seiua.skymatrix.mixin.mixins;

import cn.seiua.skymatrix.event.events.ClientSettingEvent;
import cn.seiua.skymatrix.event.events.ReachEvent;
import cn.seiua.skymatrix.event.events.UpdateTargetedEntityEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(
            at = @At(value = "INVOKE",
                    target = "net/minecraft/client/render/GameRenderer.updateCrosshairTarget (F)V",
                    opcode = Opcodes.INVOKEVIRTUAL,
                    ordinal = 0),
            method = "renderWorld")
    private void onRenderWorld(RenderTickCounter tickCounter, CallbackInfo ci) {
//        WorldRenderEvent event = new WorldRenderEvent(new MatrixStack(), tickCounter.getTickDelta(true),null);
//        event.call();
    }
    /**
     * @author seiuna
     * @reason reach
     */
    @ModifyArg(
            index = 1,
            at = @At(value = "INVOKE",
                    target = "java/lang/Math.max (DD)D",
                    opcode = Opcodes.INVOKESTATIC,
                    ordinal = 0

                   ),
            method = "findCrosshairTarget")
    public double findCrosshairTarget$mathMax(double b) {
        ReachEvent event = new ReachEvent(4.5f);
        event.call();
        return event.getReach();
    }

    @Inject(
            at = @At(value = "INVOKE",
                    target = "net/minecraft/client/MinecraftClient.getProfiler ()Lnet/minecraft/util/profiler/Profiler;",
                    opcode = Opcodes.INVOKEVIRTUAL,
                    ordinal = 0),
            method = "updateCrosshairTarget")
    public void updateTargetedEntity(float tickDelta, CallbackInfo ci) {
        new UpdateTargetedEntityEvent.Pre().call();
    }

    @Inject(
            at = @At(value = "INVOKE",
                    target = "net/minecraft/client/render/GameRenderer.findCrosshairTarget (Lnet/minecraft/entity/Entity;DDF)Lnet/minecraft/util/hit/HitResult;",
                    opcode = Opcodes.INVOKEVIRTUAL,
                    shift = At.Shift.AFTER,
                    ordinal = 0),
            method = "updateCrosshairTarget")
    public void updateTargetedEntityPost(float tickDelta, CallbackInfo ci) {
        new UpdateTargetedEntityEvent.Post().call();
    }

    @Inject(
            at = @At(value = "TAIL"),
            method = "updateCrosshairTarget")
    public void updateTargetedEntityOver(float tickDelta, CallbackInfo ci) {
        new UpdateTargetedEntityEvent.Over().call();
    }

    @Inject(
            at = @At(value = "INVOKE",
                    target = "net/minecraft/client/MinecraftClient.openGameMenu (Z)V",
                    opcode = Opcodes.INVOKEVIRTUAL,
                    ordinal = 0),
            method = "render", cancellable = true)
    public void openGameMenu(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        ClientSettingEvent e = new ClientSettingEvent("lostFocus");
        e.call();
        if (e.isCancelled()) ci.cancel();
    }

}
