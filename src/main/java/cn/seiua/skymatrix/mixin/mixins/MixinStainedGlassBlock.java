package cn.seiua.skymatrix.mixin.mixins;

import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.state.State;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(StainedGlassBlock.class)
public abstract class MixinStainedGlassBlock extends AbstractBlock{
    public MixinStainedGlassBlock(Settings settings) {
        super(settings);
    }
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1.0f, 1f);
    }


}

