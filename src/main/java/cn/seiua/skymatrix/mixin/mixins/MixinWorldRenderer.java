package cn.seiua.skymatrix.mixin.mixins;

import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
    WorldRenderEvent context = WorldRenderEvent.getInstance();
    @Inject(method = "render", at = @At("HEAD"))
    private void beforeRender(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        context.setCamera(camera);
        context.setGameRenderer(gameRenderer);
        context.setTickCounter(tickCounter);
        context.setPositionMatrix(positionMatrix);
        context.setProjectionMatrix(projectionMatrix);
        context.setTickDelta(tickCounter.getTickDelta(true));
    }
    @ModifyExpressionValue(method = "render", at = @At(value = "NEW", target = "net/minecraft/client/util/math/MatrixStack"))
    private MatrixStack setMatrixStack(MatrixStack matrixStack) {
        context.setMatrixStack(matrixStack);
        return matrixStack;
    }
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/debug/DebugRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;DDD)V",
                    ordinal = 0
            )
    )
    private void beforeDebugRender(CallbackInfo ci) {
        context.call();
    }
}
