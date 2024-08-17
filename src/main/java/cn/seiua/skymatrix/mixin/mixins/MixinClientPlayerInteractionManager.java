package cn.seiua.skymatrix.mixin.mixins;

import cn.seiua.skymatrix.event.events.InteractItemEvent;
import cn.seiua.skymatrix.event.events.InteractItemMEvent;
import cn.seiua.skymatrix.event.events.ReachEvent;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Shadow
    private GameMode gameMode;

    @Inject(at = @At("HEAD"), method = "interactItem")
    public void onInteractItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        InteractItemEvent event = new InteractItemEvent(player.getStackInHand(hand), hand);
        event.call();
        InteractItemMEvent event1 = new InteractItemMEvent(player.getStackInHand(hand), hand, "HEAD");
        event1.call();

        if (event.isCancelled()) cir.cancel();
    }

    @Inject(at = @At("RETURN"), method = "interactItem")
    public void onInteractItemPost(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        InteractItemMEvent event = new InteractItemMEvent(player.getStackInHand(hand), hand, "RETURN");
        event.call();

        if (event.isCancelled()) cir.cancel();
    }



}
