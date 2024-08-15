package cn.seiua.skymatrix.render;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.utils.MathUtils;
import cn.seiua.skymatrix.utils.RenderUtils;
import cn.seiua.skymatrix.utils.RenderUtilsV2;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class BlockTarget implements RenderTarget {

    protected BlockPos pos;
    protected GetColor getColor;

    public BlockTarget(BlockPos blockPos, GetColor geColor) {
        this.pos = blockPos;
        this.getColor = geColor;
    }

    @Override
    public void render(MatrixStack matrixStack, float delta) {
        RenderUtilsV2.renderOutlineBlock(matrixStack,pos,getColor.getColor());
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public int hashCode() {
        return pos.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.equals(pos);
    }
}
