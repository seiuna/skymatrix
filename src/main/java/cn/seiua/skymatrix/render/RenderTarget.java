package cn.seiua.skymatrix.render;

import net.minecraft.client.util.math.MatrixStack;

public interface RenderTarget {

    void render(MatrixStack matrixStack, float delta);

    default void render2D(MatrixStack matrixStack, float delta) {

    }

}
