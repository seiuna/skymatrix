package cn.seiua.skymatrix.mixin.mixins;


import cn.seiua.skymatrix.event.events.BlockRenderEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.occlusion.BlockOcclusionCache")
public class MixinBlockOcclusionCache {
    @Inject(at = @At("HEAD"), method = "shouldDrawSide", cancellable = true, remap = false)
    private void shouldDrawSide(BlockState state, BlockView reader, BlockPos pos, Direction face,
                                CallbackInfoReturnable<Boolean> ci) {
        //NMSL
        new BlockRenderEvent(pos, state).call();
    }
}


