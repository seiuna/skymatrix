package cn.seiua.skymatrix.mixin.mixins;

import cn.seiua.skymatrix.event.events.ViewClipEvent;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class MixinCamera {



    @ModifyArg(at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/render/Camera.clipToSpace (F)F",
            ordinal = 0
        ),
            method = "update",
            index = 0
        )
    private float update(float f) {
        ViewClipEvent event = new ViewClipEvent(f, "HEAD");
        event.call();

        return event.getDis();
    }

    @Inject(method = "clipToSpace", at = @At("RETURN"), cancellable = true)
    private void clipToSpaceReturn(float f, CallbackInfoReturnable<Float> cir) {
        ViewClipEvent event = new ViewClipEvent(f, "RETURN");
        event.call();
        cir.setReturnValue(event.getDis());
    }

//    @Inject(method = "getYaw", at = @At("RETURN"), cancellable = true)
//    private void getYaw(CallbackInfoReturnable<Float> cir) {
//        HeadRotationEvent event = new HeadRotationEvent(pitch, yaw);
//        event.call();
//        cir.setReturnValue(event.getYaw());
//    }
//
//    @Inject(method = "getPitch", at = @At("RETURN"), cancellable = true)
//    private void getPitch(CallbackInfoReturnable<Float> cir) {
//        HeadRotationEvent event = new HeadRotationEvent(pitch, yaw);
//        event.call();
//        cir.setReturnValue(event.getPitch());
//    }




}
