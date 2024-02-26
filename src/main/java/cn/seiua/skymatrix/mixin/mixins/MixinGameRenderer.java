package cn.seiua.skymatrix.mixin.mixins;

import cn.seiua.skymatrix.event.events.ClientSettingEvent;
import cn.seiua.skymatrix.event.events.UpdateTargetedEntityEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0),
            method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V")
    private void onRenderWorld(float partialTicks, long finishTimeNano,
                               MatrixStack matrixStack, CallbackInfo ci) {

        WorldRenderEvent event = new WorldRenderEvent(matrixStack, partialTicks);
        event.call();
    }


    @Inject(
            at = @At(value = "INVOKE",
                    target = "net/minecraft/client/MinecraftClient.getProfiler ()Lnet/minecraft/util/profiler/Profiler;",
                    opcode = Opcodes.INVOKEVIRTUAL,
                    ordinal = 0),
            method = "updateTargetedEntity")
    public void updateTargetedEntity(float tickDelta, CallbackInfo ci) {
        new UpdateTargetedEntityEvent.Pre().call();
    }

    @Inject(
            at = @At(value = "INVOKE",
                    target = "net/minecraft/entity/Entity.getRotationVec (F)Lnet/minecraft/util/math/Vec3d;",
                    opcode = Opcodes.INVOKEVIRTUAL,
                    ordinal = 0),
            method = "updateTargetedEntity")
    public void updateTargetedEntityPost(float tickDelta, CallbackInfo ci) {
        new UpdateTargetedEntityEvent.Post().call();
    }

    @Inject(
            at = @At(value = "TAIL"),
            method = "updateTargetedEntity")
    public void updateTargetedEntityOver(float tickDelta, CallbackInfo ci) {
        new UpdateTargetedEntityEvent.Over().call();
    }

    @Inject(
            at = @At(value = "INVOKE",
                    target = "net/minecraft/client/MinecraftClient.openGameMenu (Z)V",
                    opcode = Opcodes.INVOKEVIRTUAL,
                    ordinal = 0),
            method = "render", cancellable = true)
    public void openGameMenu(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        ClientSettingEvent e = new ClientSettingEvent("lostFocus");
        e.call();
        if (e.isCancelled()) ci.cancel();
    }

}
