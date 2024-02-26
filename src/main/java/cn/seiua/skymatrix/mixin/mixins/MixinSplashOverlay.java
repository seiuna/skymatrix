package cn.seiua.skymatrix.mixin.mixins;

import net.minecraft.client.gui.screen.SplashOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;
import java.util.function.IntSupplier;

@Mixin(SplashOverlay.class)
public abstract class MixinSplashOverlay {

    @Final
    @Shadow
    private static IntSupplier BRAND_ARGB = () -> new Color(255, 255, 255, 255).getRGB();

}
