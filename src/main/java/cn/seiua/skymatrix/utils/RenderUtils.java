package cn.seiua.skymatrix.utils;

import cn.seiua.skymatrix.SkyMatrix;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;

import java.awt.*;

public class RenderUtils {

    public static boolean outline = false;
    private static Color color;
    private static boolean cent;

    public static void drawSolidBox(Box bb, MatrixStack matrixStack) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS,
                VertexFormats.POSITION);
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        GlStateManager._enableBlend();
        GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        setupColor();
        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ);
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.minZ);
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.maxZ);
        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.minY, (float) bb.maxZ);

        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.maxY, (float) bb.minZ);
        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.maxY, (float) bb.maxZ);
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ);
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.maxY, (float) bb.minZ);

        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ);
        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.maxY, (float) bb.minZ);
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.maxY, (float) bb.minZ);
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.minZ);

        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.minZ);
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.maxY, (float) bb.minZ);
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ);
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.maxZ);

        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.minY, (float) bb.maxZ);
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.maxZ);
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ);
        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.maxY, (float) bb.maxZ);

        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ);
        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.minY, (float) bb.maxZ);
        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.maxY, (float) bb.maxZ);
        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.maxY, (float) bb.minZ);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        GlStateManager._disableBlend();
        resetColor();
    }

    public static void setColor(Color col) {
        color = col;
    }

    public static void setupColor() {
        float rr = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;
        RenderSystem.setShaderColor(rr, g, b, a);
    }

    public static void cent() {
        cent = true;
    }

    public static void resetCent() {
        cent = false;
    }

    public static void resetColor() {

        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public static Box getCenterBox(Box bb) {

        double wdx = (bb.maxX - bb.minX) / 2;
        double wdy = (bb.maxY - bb.minY) / 2;
        double wdz = (bb.maxZ - bb.minZ) / 2;


        return new Box(bb.minX - wdx, bb.minY - wdy, bb.minZ - wdz, bb.minX + wdx, bb.minY + wdy, bb.minZ + wdz);

    }

    public static void clearMask() {
        RenderSystem.depthMask(true);
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, false);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawMask(MatrixStack matrixStack, Box box) {
        RenderSystem.enableDepthTest();
        RenderSystem.colorMask(false, false, false, true);
        RenderUtils.setColor(Color.red);
        RenderUtils.resetCent();
        RenderUtils.drawRound2D(box, matrixStack, 10);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(false);
        RenderSystem.depthFunc(GL11.GL_GREATER);
    }

    public static void drawRound2D(Box bb, MatrixStack matrixStack, int r) {
        if (cent) bb = getCenterBox(bb);
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS,
                VertexFormats.POSITION);
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        GL11C.glEnable(GL11C.GL_LINE_SMOOTH);
        setupColor();
        GlStateManager._enableBlend();
        GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        bufferBuilder.vertex(matrix, (float) bb.minX, (float) bb.minY + r, (float) bb.minZ);
        bufferBuilder.vertex(matrix, (float) bb.minX, (float) bb.maxY - r, (float) bb.minZ);
        bufferBuilder.vertex(matrix, (float) bb.maxX, (float) bb.maxY - r, (float) bb.minZ);
        bufferBuilder.vertex(matrix, (float) bb.maxX, (float) bb.minY + r, (float) bb.minZ);
        bufferBuilder.vertex(matrix, (float) bb.minX + r, (float) bb.minY, (float) bb.minZ);
        bufferBuilder.vertex(matrix, (float) bb.minX + r, (float) bb.maxY, (float) bb.minZ);
        bufferBuilder.vertex(matrix, (float) bb.maxX - r, (float) bb.maxY, (float) bb.minZ);
        bufferBuilder.vertex(matrix, (float) bb.maxX - r, (float) bb.minY, (float) bb.minZ);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        GlStateManager._disableBlend();
        if (r > 0) {
            bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLES,
                    VertexFormats.POSITION);
            int spx = (int) (bb.maxX - r);
            int spy = (int) (bb.minY + r);
            float lx = 0;
            float ly = 0;
            boolean nmsl = true;
            for (int i = 0; i <= 360; i += 3) {
                if (i == 90) {
                    spx = (int) (bb.minX + r);
                    spy = (int) (bb.minY + r);
                    nmsl = false;
                }
                if (i == 180) {
                    spx = (int) (bb.minX + r);
                    spy = (int) (bb.maxY - r);
                    nmsl = true;
                }
                if (i == 270) {
                    spx = (int) (bb.maxX - r);
                    spy = (int) (bb.maxY - r);
                    nmsl = false;
                }
                ly = r * (float) Math.sin(Math.toRadians(i));
                lx = r * (float) Math.cos(Math.toRadians(i));
                bufferBuilder.vertex(matrix, (float) spx, (float) spy, (float) bb.minZ);
                bufferBuilder.vertex(matrix, (float) spx + lx, (float) spy - ly, (float) bb.minZ);
                bufferBuilder.vertex(matrix, nmsl ? (float) spx + ly : (float) spx - ly, nmsl ? (float) spy - lx : (float) spy + lx, (float) bb.minZ);

            }
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        }
        GlStateManager._disableBlend();
        resetColor();
    }

    private static void line(BufferBuilder bufferBuilder, int r, Box bb, Matrix4f matrix, int s, int e, int sx, int sy) {
        float lx = r * (float) Math.sin(Math.toRadians(s));
        float ly = r * (float) Math.cos(Math.toRadians(s));
        for (int i = s; i <= e; i++) {
            bufferBuilder
                    .vertex(matrix, sx + lx, sy + ly, (float) bb.minZ);

            lx = r * (float) Math.sin(Math.toRadians(i));
            ly = r * (float) Math.cos(Math.toRadians(i));
            bufferBuilder
                    .vertex(matrix, sx + lx, sy + ly, (float) bb.minZ);

        }
    }

//    public static Vec3d getClientLookVec() {
//        ClientPlayerEntity player = SkyMatrix.mc.player;
//        float f = 0.017453292F;
//        float pi = (float) Math.PI;
//
//        float f1 = MathHelper.cos(-player.getYaw() * f - pi);
//        float f2 = MathHelper.sin(-player.getYaw() * f - pi);
//        float f3 = -MathHelper.cos(-player.getPitch() * f);
//        float f4 = MathHelper.sin(-player.getPitch() * f);
//
//        return new Vec3d(f2 * f3, f4, f1 * f3);
//    }

    public static void drawOutlineBox(Box bb, MatrixStack matrixStack) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES,
                VertexFormats.POSITION);
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        setupColor();
        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ)
        ;
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.minZ)
        ;

        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.minZ)
        ;
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.maxZ)
        ;

        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.maxZ)
        ;
        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.minY, (float) bb.maxZ)
        ;

        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.minY, (float) bb.maxZ)
        ;
        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ)
        ;

        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ)
        ;
        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.maxY, (float) bb.minZ)
        ;

        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.minZ)
        ;
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.maxY, (float) bb.minZ)
        ;

        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.maxZ)
        ;
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ)
        ;

        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.minY, (float) bb.maxZ)
        ;;
        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.maxY, (float) bb.maxZ)
        ;

        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.maxY, (float) bb.minZ)
        ;
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.maxY, (float) bb.minZ)
        ;

        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.maxY, (float) bb.minZ)
        ;
        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ)
        ;

        bufferBuilder
                .vertex(matrix, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ)
        ;
        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.maxY, (float) bb.maxZ)
                ;

        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.maxY, (float) bb.maxZ)
               ;
        bufferBuilder
                .vertex(matrix, (float) bb.minX, (float) bb.maxY, (float) bb.minZ);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        resetColor();
    }

    public static void drawRound2DOutline(Box box, MatrixStack matrices, int i) {
        outline = true;
        drawRound2D(box, matrices, i);
        outline = false;
    }

    public static void drawColorRound2D(Box bb, MatrixStack matrixStack, int i) {
        if (cent) bb = getCenterBox(bb);

        int t = (int) (bb.maxX - bb.minX);

        Color color1 = Color.red;
        float th = 0;

        for (int j = 0; j < t; j++) {
            th = j * 1.0f / t;
            color1 = ColorUtils.setHue(color1, th);
            setColor(color1);
            Matrix4f matrix = matrixStack.peek().getPositionMatrix();
            Tessellator tessellator = RenderSystem.renderThreadTesselator();
            BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS,
                    VertexFormats.POSITION);
            RenderSystem.setShader(GameRenderer::getPositionProgram);
            setupColor();
            GlStateManager._enableBlend();
            GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            bufferBuilder.vertex(matrix, (float) bb.minX + j, (float) bb.minY, (float) bb.minZ);
            bufferBuilder.vertex(matrix, (float) bb.minX + j, (float) bb.maxY, (float) bb.minZ);
            bufferBuilder.vertex(matrix, (float) bb.minX + j + 1, (float) bb.maxY, (float) bb.minZ);
            bufferBuilder.vertex(matrix, (float) bb.minX + j + 1, (float) bb.minY, (float) bb.minZ);
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        }

        GlStateManager._disableBlend();
        resetColor();


    }

    public static int regionX1;
    public static int regionZ1;
    private static MatrixStack matrixStacka;

    public static void translateView(MatrixStack matrixStack) {
        if (matrixStacka == matrixStack) return;

        matrixStacka = matrixStack;
        if(SkyMatrix.mc.getEntityRenderDispatcher().camera == null) return;
        Vec3d camPos = SkyMatrix.mc.getEntityRenderDispatcher().camera.getPos();
        BlockPos blockPos = SkyMatrix.mc.getEntityRenderDispatcher().camera.getBlockPos();
        int regionX = (blockPos.getX() >> 9) * 512;
        int regionZ = (blockPos.getZ() >> 9) * 512;
        matrixStack.translate(regionX - camPos.x, -camPos.y,
                regionZ - camPos.z);

        BlockPos camPos1 = SkyMatrix.mc.getEntityRenderDispatcher().camera.getBlockPos();
        regionX1 = (camPos1.getX() >> 9) * 512;
        regionZ1 = (camPos1.getZ() >> 9) * 512;
    }

    public static void drawLine(MatrixStack matrixStack, Vec3i vec3d1, Vec3i vec3d) {

        drawLine(matrixStack, new Vec3d(vec3d1.getX(), vec3d1.getY(), vec3d1.getZ()), new Vec3d(vec3d.getX(), vec3d.getY(), vec3d.getZ()));
    }

    public static void drawLine(MatrixStack matrixStack, BlockPos blockPos, BlockPos blockPos1) {

        drawLine(matrixStack, blockPos.toCenterPos(), blockPos1.toCenterPos());
    }

    public static void drawLine(MatrixStack matrixStack, Vec3d vec3d1, Vec3d vec3d) {

        setupColor();
        RenderUtils.translatePos(matrixStack, vec3d);
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES,
                VertexFormats.POSITION);
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderUtils.setupColor();
        bufferBuilder
                .vertex(matrix, 0, 0, 0)
               ;
        bufferBuilder
                .vertex(matrix, (float) (vec3d1.getX() - vec3d.getX()), (float) (vec3d1.getY() - vec3d.getY()), (float) (vec3d1.getZ() - vec3d.getZ()))
               ;
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        resetColor();

    }

    public static void translateEntity(MatrixStack matrixStack, LivingEntity entity) {
        translatePos(matrixStack, entity.getPos());
    }

    public static void translatePos(MatrixStack matrixStack, Vec3d vec3d) {
        matrixStack.translate(
                vec3d.getX() - regionX1,
                vec3d.getY(),
                vec3d.getZ() - regionZ1);
    }

    public static void translatePos(MatrixStack matrixStack, BlockPos pos) {
        translatePos(matrixStack, new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
    }


}
