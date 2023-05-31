package cn.seiua.skymatrix.mixin.mixins;


import cn.seiua.skymatrix.event.events.BlockRenderEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Block.class)
public abstract class MixinBlock {

    @Inject(at = {@At("HEAD")},
            method = {
                    "shouldDrawSide(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/util/math/BlockPos;)Z"},
            cancellable = true)
    private static void onShouldDrawSide(BlockState state, BlockView world,
                                         BlockPos pos, Direction direction, BlockPos blockPos,
                                         CallbackInfoReturnable<Boolean> cir) {

        new BlockRenderEvent(pos, state).call();
    }


}
