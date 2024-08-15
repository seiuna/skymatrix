package cn.seiua.skymatrix.render;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.module.modules.render.LavaEsp;
import cn.seiua.skymatrix.font.FontRenderer;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.utils.RenderUtils;
import cn.seiua.skymatrix.utils.RenderUtilsV2;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class BlockTextTarget extends BlockTarget {

    private String text;

    public BlockTextTarget(BlockPos blockPos, GetColor geColor, String text) {
        super(blockPos, geColor);
        this.text = text;
    }

    @Override
    public void render(MatrixStack matrixStack, float delta) {
        super.render(matrixStack, delta);
        RenderUtilsV2.renderText(matrixStack, ClickGui.fontRenderer28, text, pos.toCenterPos().add(0,0.9f,0), getColor.getColor(),delta);
    }


}
