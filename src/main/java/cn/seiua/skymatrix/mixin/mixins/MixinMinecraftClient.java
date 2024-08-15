package cn.seiua.skymatrix.mixin.mixins;

import cn.seiua.skymatrix.client.Client;
import cn.seiua.skymatrix.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.HitResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
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
    @Shadow private GameOptions options;
    @Shadow private WorldRenderer worldRenderer;
    @Shadow private Screen currentScreen;
    @Shadow private GameRenderer gameRenderer;
    @Shadow public  InGameHud inGameHud;
    @Shadow private int itemUseCooldown;
    @Shadow private  ToastManager toastManager;
    @Shadow private  TutorialManager tutorialManager;
    @Shadow private TutorialToast socialInteractionsToast;

    @Shadow     private Overlay overlay;
    @Shadow  private Mouse mouse;

    @Inject(at = @At("HEAD"), method = "close")
    public void close(CallbackInfo ci) {
        new GameExitEvent().call();
    }

    @Inject(at = @At("HEAD"), method = "joinWorld")
    public void joinWorld(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason, CallbackInfo ci) {
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
                    shift = At.Shift.BEFORE,
                    ordinal = 0),
            method = "tick")
    private void onTickHandleInputBefore(CallbackInfo ci) {
        new HandleKeyInputBeforeEvent().call();
    }
    @Inject(
            at = @At(value = "INVOKE",
                    target = "net/minecraft/client/MinecraftClient.handleInputEvents ()V",
                    opcode = Opcodes.INVOKEVIRTUAL,
                    shift = At.Shift.AFTER,
                    ordinal = 0),
            method = "tick")
    private void onTickHandleInputAfter(CallbackInfo ci) {
        new HandleKeyInputAfterEvent().call();
    }

    @Inject(
            cancellable = true,
            at = @At(value = "INVOKE",
                    target = "net/minecraft/client/MinecraftClient.handleBlockBreaking (Z)V",
                    opcode = Opcodes.INVOKEVIRTUAL,
                    shift = At.Shift.AFTER,
                    ordinal = 0),
            method = "handleInputEvents")
    private void HandleInput(CallbackInfo ci) {
       if(Client.HandleInputBlockBreaking()){
           ci.cancel();
       }
    }
    @Inject(
            cancellable = true,
            at = @At("HEAD"),
            method = "handleBlockBreaking")
    private void HandleBlockBreaking(CallbackInfo ci) {
        if(Client.doBlackList("")) {
            ci.cancel();
        }
    }
    @ModifyArg(method = "handleInputEvents", at = @At(value = "INVOKE", target = "net/minecraft/client/MinecraftClient.handleBlockBreaking (Z)V"), index = 0)
    private boolean injected(boolean breaking) {
        return Client.HandleInputBlockBreaking() || breaking;
    }

    /**
     * @author yuuiyi
     * @reason
     */
//    @Overwrite
//    public final void handleBlockBreaking(boolean breaking) {
//        if (!breaking) {
//            this.attackCooldown = 0;
//        }
//
//        if (Client.instance.isKeepBlockBreaking()) {
//            breaking = true;
//        }
//        if (this.attackCooldown <= 0 && !this.player.isUsingItem()) {
//            if (breaking && this.crosshairTarget != null && this.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
//                BlockHitResult blockHitResult = (BlockHitResult) this.crosshairTarget;
//                BlockPos blockPos = blockHitResult.getBlockPos();
//                if (!this.world.getBlockState(blockPos).isAir()) {
//                    Direction direction = blockHitResult.getSide();
//                    if (this.interactionManager.updateBlockBreakingProgress(blockPos, direction)) {
//                        this.particleManager.addBlockBreakingParticles(blockPos, direction);
//                        this.player.swingHand(Hand.MAIN_HAND);
//                    }
//                }
//
//            } else {
//                this.interactionManager.cancelBlockBreaking();
//            }
//        }
//    }

    @Inject(at = @At("HEAD"), method = "setScreen", cancellable = true)
    private void setScreenI(CallbackInfo info) {
        OpenScreenEvent e = new OpenScreenEvent();
        e.call();
        if (e.isCancelled()) info.cancel();
    }


}
