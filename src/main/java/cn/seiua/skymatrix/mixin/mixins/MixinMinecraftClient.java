package cn.seiua.skymatrix.mixin.mixins;

import cn.seiua.skymatrix.client.Client;
import cn.seiua.skymatrix.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {


    @Shadow
    public int attackCooldown;
    @Shadow
    public HitResult crosshairTarget;
    @Shadow
    public ClientPlayerEntity player;
    @Shadow
    public ClientWorld world;
    @Shadow
    public ClientPlayerInteractionManager interactionManager;
    @Shadow
    public ParticleManager particleManager;

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


    /**
     * @author yuuiyi
     * @reason
     */
    @Overwrite
    public final void handleBlockBreaking(boolean breaking) {
        if (!breaking) {
            this.attackCooldown = 0;
        }

        if (Client.instance.isKeepBlockBreaking()) {
            breaking = true;
        }
        if (this.attackCooldown <= 0 && !this.player.isUsingItem()) {
            if (breaking && this.crosshairTarget != null && this.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) this.crosshairTarget;
                BlockPos blockPos = blockHitResult.getBlockPos();
                if (!this.world.getBlockState(blockPos).isAir()) {
                    Direction direction = blockHitResult.getSide();
                    if (this.interactionManager.updateBlockBreakingProgress(blockPos, direction)) {
                        this.particleManager.addBlockBreakingParticles(blockPos, direction);
                        this.player.swingHand(Hand.MAIN_HAND);
                    }
                }

            } else {
                this.interactionManager.cancelBlockBreaking();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "setScreen", cancellable = true)
    private void setScreen(CallbackInfo info) {
        OpenScreenEvent e = new OpenScreenEvent();
        e.call();
        if (e.isCancelled()) info.cancel();
    }


}
