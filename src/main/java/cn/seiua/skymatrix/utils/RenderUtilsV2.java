package cn.seiua.skymatrix.utils;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.module.modules.life.GemstoneMarco;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import cn.seiua.skymatrix.font.FontRenderer;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.render.GetColor;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderUtilsV2 {
    public static GetColor color;
    private static final Color DEFAULT_COLOR = new Color(1,1,1,1);
    private static void translate(Camera camera, MatrixStack matrixStack){
        matrixStack.translate(-camera.getPos().getX(),-camera.getPos().getY(),-camera.getPos().getZ());
    }
    private static void setColor(float r,float g,float b,float a){
        if (color != null) {
            Color c = color.getColor();
            RenderSystem.setShaderColor((float) c.getRed() /255, (float) c.getGreen() /255, (float) c.getBlue() /255, (float) c.getAlpha() /255);
            return;
        }
        RenderSystem.setShaderColor(r/255,g/255,b,a/255);
    }
    private static void resetColor(){
        RenderSystem.setShaderColor(1,1,1,1);
    }
    public static void setColorK(GetColor color){
        RenderUtilsV2.color=color;
    }
    public static void resetColorK(GetColor color){
        RenderUtilsV2.color=null;
    }
    public static void renderSolidBox(MatrixStack matrixStack, Box box, Color color){
        matrixStack.push();
        translate(SkyMatrix.mc.gameRenderer.getCamera(),matrixStack);
        if(color==null)color=DEFAULT_COLOR;
        setColor(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS,
                VertexFormats.POSITION);
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.disableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        bufferBuilder
                .vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ);
        bufferBuilder
                .vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ);
        bufferBuilder
                .vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ);
        bufferBuilder
                .vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ);

        bufferBuilder
                .vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ);
        bufferBuilder
                .vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ);
        bufferBuilder
                .vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ);
        bufferBuilder
                .vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ);

        bufferBuilder
                .vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ);
        bufferBuilder
                .vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ);
        bufferBuilder
                .vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ);
        bufferBuilder
                .vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ);

        bufferBuilder
                .vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ);
        bufferBuilder
                .vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ);
        bufferBuilder
                .vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ);
        bufferBuilder
                .vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ);

        bufferBuilder
                .vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ);
        bufferBuilder
                .vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ);
        bufferBuilder
                .vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ);
        bufferBuilder
                .vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ);
        bufferBuilder
                .vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ);
        bufferBuilder
                .vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ);
        bufferBuilder
                .vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ);
        bufferBuilder
                .vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        GlStateManager._disableBlend();
        resetColor();
        matrixStack.pop();
    }
    public static void renderSolidBlock(MatrixStack matrixStack, BlockPos blockPos, Color color){
        renderSolidBox(matrixStack,new Box(blockPos),color);
    }

    public static void renderSolidBox(MatrixStack matrixStack, Vec3d vec3d,Vec3d vec3d1, Color color){
        renderSolidBox(matrixStack,new Box(vec3d.x,vec3d.y,vec3d.z,vec3d1.x,vec3d1.y,vec3d1.z),color);
    }

    public static void renderOutlineBox(MatrixStack matrixStack, Box box, Color color){
        matrixStack.push();
        translate(SkyMatrix.mc.gameRenderer.getCamera(),matrixStack);
        if(color==null)color=DEFAULT_COLOR;
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.lineWidth(2f);
        RenderSystem.disableCull();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder =  tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        setColor(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
        WorldRenderer.drawBox(matrixStack, bufferBuilder, box, (float) color.getRed() /255, (float) color.getGreen() /255, (float) color.getBlue() /255, (float) color.getAlpha() /255);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        resetColor();
        RenderSystem.enableDepthTest();
        matrixStack.pop();
    }
    public static void renderOutlineBlock(MatrixStack matrixStack, BlockPos blockPos, Color color){
        renderOutlineBox(matrixStack,new Box(blockPos),color);
    }
    public static void renderOutlineBox(MatrixStack matrixStack, Vec3d vec3d,Vec3d vec3d1, Color color){
        renderOutlineBox(matrixStack,new Box(vec3d.x,vec3d.y,vec3d.z,vec3d1.x,vec3d1.y,vec3d1.z),color);
    }

    public static void renderLine(MatrixStack matrixStack, Vec3d start, Vec3d end, Color color,float lineWidth){
        matrixStack.push();
        translate(SkyMatrix.mc.gameRenderer.getCamera(),matrixStack);
        if(color==null)color=DEFAULT_COLOR;
        if(RenderUtilsV2.color!=null)color=RenderUtilsV2.color.getColor();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        RenderSystem.lineWidth(lineWidth);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.disableCull();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.LINES);
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        Vector3f b = new Vector3f((float) end.getX(), (float) end.getY(), (float) end.getZ()).sub((float) start.getX(), (float) start.getY(), (float)start.getZ()).normalize().mul(matrixStack.peek().getNormalMatrix());
        Vector3f b1 = new Vector3f((float) start.getX(), (float) start.getY(), (float) start.getZ()).sub((float) end.getX(), (float) end.getY(), (float)end.getZ()).normalize().mul(matrixStack.peek().getNormalMatrix());
        bufferBuilder
                .vertex(matrixStack.peek(), (float) start.x, (float) start.y, (float) start.z)
                .color(color.getRGB())
      .normal(matrixStack.peek(),b1.x,b1.y,b1.z);
        bufferBuilder
                .vertex(matrixStack.peek(), (float) end.x, (float) end.y, (float) end.z)
                .color(color.getRGB())
                .normal(matrixStack.peek(),b.x,b.y,b.z);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        resetColor();
        RenderSystem.enableDepthTest();
        matrixStack.pop();
    }
    public static void renderLine(MatrixStack matrixStack, BlockPos start, BlockPos end, Color color,float lineWidth){
        renderLine(matrixStack,start.toCenterPos(),end.toCenterPos(),color,lineWidth);
    }
    public static final char COLOR_CHAR = '§';
    /**
     *
     * example: xxxxx§xxxxx§xxxxxx§xxxxxxx
     * you must provide the same number of § as the number of Color
     * when case of §, the color will be changed to the next color
     *
     *
     * @param matrixStack
     * @param fontRenderer
     * @param text
     * @param pos
     * @param color
     * @param delta
     */
    public static void renderText(MatrixStack matrixStack,FontRenderer fontRenderer,String text,Vec3d pos,Color color,float delta){
        matrixStack.push();
////
        LivingEntity player = SkyMatrix.mc.player;
////        RenderUtils.translatePos(matrixStack, super.getPos().toCenterPos().add(0, 0.9f, 0));
        float scale = 0.008F;
        double distance = player.getCameraPosVec(delta).distanceTo(pos);
        if (distance > 10)
            scale *= distance / 10;
        Camera camera= SkyMatrix.mc.gameRenderer.getCamera();
        Vec3d vec3d=pos;
        RenderSystem.disableDepthTest();
        matrixStack.peek().getPositionMatrix().translate((float) (vec3d.getX()-camera.getPos().getX()), (float) (vec3d.getY()-camera.getPos().getY()), (float) (vec3d.getZ()-camera.getPos().getZ()))
                .rotate(camera.getRotation())
                .scale(scale, -scale, scale);
        fontRenderer.centeredH();
        fontRenderer.centeredV();
        fontRenderer.setColor(color);
        fontRenderer.drawString(matrixStack, 0,0,0, text);
        RenderSystem.enableDepthTest();
        matrixStack.pop();
    }
    public static void renderText(MatrixStack matrixStack,FontRenderer fontRenderer,String text,BlockPos pos,Color color,float delta){
        renderText(matrixStack,fontRenderer,text,pos.toCenterPos(),color,delta);
    }

}
