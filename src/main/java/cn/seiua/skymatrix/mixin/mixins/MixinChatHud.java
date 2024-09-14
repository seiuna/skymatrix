package cn.seiua.skymatrix.mixin.mixins;

import cn.seiua.skymatrix.client.ChatOverride;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class MixinChatHud {


    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        ChatOverride.getInstance().onRenderPre();
    }

    @Inject(method = "render", at = @At("RETURN"), cancellable = true)
    private void renderRt(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        ChatOverride.getInstance().onRenderPost();
    }

}
