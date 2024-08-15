package cn.seiua.skymatrix.font;

import cn.seiua.skymatrix.utils.ColorUtils;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontRenderer {

    public static final int LEFT_TOP = 0;
    public static final int LEFT_DOWN = 1;
    public static final int RIGHT_TOP = 2;
    public static final int RIGHT_DOWN = 3;
    /**
     * 文字的起始位置 0为左上角 1为左下角 2为右上角 3为右下角 如过设置了centeredV centeredH次参数将会失效
     */
    public int side;

    /**
     * 当前字体
     */
    private Font font;
    /**
     * 是否绘制阴影
     */
    private boolean shadow;
    /**
     * 是否绘制描边
     */
    private boolean outline;
    /**
     * 是否绘制下划线
     */
    private boolean underline;
    /**
     * 当前的颜色
     */
    private Color color = Color.white;
    /**
     * 之前的颜色 当你 setColor(Color) 后back将会是上一次设置的颜色
     */
    private Color back = Color.white;
    /**
     * 绘制文字的大小 单位px
     */
    private float drawSize;
    /**
     * 比率
     */
    private float rate;
    /**
     * 文字的高度基于 drawSize 自动计算
     */
    private int height;
    /**
     * 文字是否水平居中
     */
    private boolean centeredV;
    /**
     * 文字是否垂直居中
     */
    private boolean centeredH;
    /**
     * 字符缓存
     */
    private Map<String, CharInfo> charsCache;
    private int outlineSize = 1;
    private Color outlineColor;
    private Color shadowColor;


    public void setOutlineSize(int outlineSize) {
        this.outlineSize = outlineSize;
    }

    private int getOutlineSize() {
        return this.outlineSize;
    }

    public void resetOutlineSize() {
        this.outlineSize = 1;
    }

    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
    }

    public void resetOutlineColor() {
        this.outlineColor = null;
    }

    public FontRenderer(Font font) {
        this.charsCache = new HashMap<>();
        this.font = font;
        String init = "1234567890qwertyuiopasdfghjklzxcvbnm";
        for (char c : init.toCharArray()) {
            CharInfo charInfo = getCharImg(c);
            this.height = charInfo.getHeight();
        }

    }

    private Color getOutlineColor(Color color) {
        if (outlineColor != null) {
            return outlineColor;
        }
        return ColorUtils.darkenColor(color, 0.5);
    }

    private CharInfo getCharImg(char c) {
        if (this.charsCache.containsKey(c + "")) {
            return this.charsCache.get(c + "");
        }
        BufferedImage bufferedImage = ClientImageGenerator.generateFontImages(c, font);
        byte[] bytes = toByteArray(bufferedImage);
        this.charsCache.put(c + "", new CharInfo(bytes, bufferedImage.getHeight(), bufferedImage.getWidth()));
        return charsCache.get(c + "");
    }

    /**
     * 设置字体颜色
     *
     * @param color 颜色
     */
    public void setColor(Color color) {
        this.back = this.color;
        this.color = color;
    }

    /**
     * 设置字体绘制的大小单位px
     *
     * @param size 大小单位px
     */
    public void setDrawSize(float size) {
        this.drawSize = size;
        this.rate = height * 1.0f / size;
    }

    /**
     * 设置回之前设置的颜色
     */
    public void setBack() {
        Color t = back;
        this.back = color;
        this.color = t;
    }

    /**
     * 使接下来绘制的文字水平居中
     */
    public void centeredV() {
        this.centeredV = true;
    }

    /**
     * 使接下来绘制的文字垂直
     */
    public void centeredH() {
        this.centeredH = true;
    }

    /**
     * 重置文字水平居中
     */
    public void resetCenteredV() {
        this.centeredV = false;
    }

    /**
     * 重置文字垂直居中
     */
    public void resetCenteredH() {
        this.centeredH = false;
    }

    public void drawStringWithShadow(MatrixStack matrixStack, int x, int y, String str) {
        this.shadow = true;
        this.drawString(matrixStack, x, y, str);
        this.shadow = false;
    }

    public void drawStringWithOutline(MatrixStack matrixStack, int x, int y, String str) {
        this.outline = true;
        this.drawString(matrixStack, x, y, str);
        this.outline = false;
    }

    public void drawStringWithUnderLine(MatrixStack matrixStack, int x, int y, String str) {
        this.underline = true;
        this.drawString(matrixStack, x, y, str);
        this.underline = false;
    }

    public void drawString(MatrixStack matrixStack, float x, float y, String str) {
        drawString(matrixStack, x, y, 0, str);
    }

    public void drawString(MatrixStack matrixStack, float x, float y, float z, String str) {
        setSide(x, y, str);
        x = this.x;
        y = this.y;
        String text = str;

        if (centeredV) x -= getStringWidth(str) / 2;
        if (this.colors != null) {
            if (index <= this.colors.size() - 1) {
                this.setColor(this.colors.get(index));
                index++;
            }
        }
        for (char c : text.toCharArray()) {
            if (c == '§') {
                if (this.colors != null) {
                    if (index <= this.colors.size() - 1) {
                        this.setColor(this.colors.get(index));
                        index++;
                    }
                }
                continue;
            }
            CharInfo charInfo = getCharImg(c);
            if (outline) {
                setColor(getOutlineColor(this.color));
                drawChar(matrixStack, x + this.getOutlineSize(), y, z, charInfo);
                drawChar(matrixStack, x - this.getOutlineSize(), y, z, charInfo);
                drawChar(matrixStack, x, y - this.getOutlineSize(), z, charInfo);
                drawChar(matrixStack, x, y + this.getOutlineSize(), z, charInfo);
                setBack();
            }
            if (shadow) {
                setColor(ColorUtils.darkenColor(color, 0.5f));
                drawChar(matrixStack, x + 1, y + 1, z, charInfo);
                setBack();
            }
            drawChar(matrixStack, x, y, z, charInfo);
            x += charInfo.getWidth();
        }
        this.colors = null;
        this.index = 0;
    }

    private void drawChar(MatrixStack matrixStack, float x, float y, float z, CharInfo charInfo) {
        float tx = x;
        float ty = y;
        if (centeredH) y -= getStringHeight() / 2;
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, charInfo.getGlid());
        RenderSystem.enableCull();
        RenderSystem.setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, GlConst.GL_LINEAR);
        RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_LINEAR);
        drawTexture(charInfo.getIdentifier(), x, y, z, 0, 0, (charInfo.getWidth()), (charInfo.getHeight()), (charInfo.getWidth()), (charInfo.getHeight()), matrixStack);
        GlStateManager._disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        if (underline) {
//            DrawableHelper.fill(matrixStack, x, y + getStringHeight() / 2, z, (int) (x + charInfo.getWidth()), (int) y + getStringHeight() / 2 + 1, this.color.getRGB());
        }

    }

    private void drawTexture(Identifier texture, float x, float y, float z, float u, float v, float width, float height, float textureWidth, float textureHeight, MatrixStack matrixStack) {
        this.drawTexture(texture, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight, matrixStack);
    }

    private void drawTexture(Identifier texture, float x1, float x2, float y1, float y2, float z, float regionWidth, float regionHeight, float u, float v, float textureWidth, float textureHeight, MatrixStack matrixStack) {
        this.drawTexturedQuad(texture, x1, x2, y1, y2, z, (u + 0.0F) / (float) textureWidth, (u + (float) regionWidth) / (float) textureWidth, (v + 0.0F) / (float) textureHeight, (v + (float) regionHeight) / (float) textureHeight, matrixStack);
    }

    private void drawTexturedQuad(Identifier texture, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2, MatrixStack matrixStack) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, (float) x1, (float) y1, (float) z).texture(u1, v1);
        bufferBuilder.vertex(matrix4f, (float) x1, (float) y2, (float) z).texture(u1, v2);
        bufferBuilder.vertex(matrix4f, (float) x2, (float) y2, (float) z).texture(u2, v2);
        bufferBuilder.vertex(matrix4f, (float) x2, (float) y1, (float) z).texture(u2, v1);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    private float x;
    private float y;

    public void setSide(float x, float y, String str) {
        this.x = x;
        this.y = y;
        if (centeredH || centeredV) return;
        if (side == RIGHT_DOWN) {
            this.y -= getStringHeight();
        }
        if (side == RIGHT_TOP) {
            //none
        }
        if (side == LEFT_DOWN) {
            this.x -= getStringWidth(str);
            this.y -= getStringHeight();
        }
        if (side == LEFT_TOP) {
            this.x -= getStringWidth(str);
        }

    }

    /**
     * 以当前设置的绘制大小，获取字符串的宽度
     *
     * @param str str
     * @return Width
     */
    public int getStringWidth(@NotNull String str) {
        str=str.replace("§","");
        int retv = 0;
        for (char c : str.toCharArray()) {
            if (c == '§') continue;
            retv += getCharImg(c).getWidth();
        }
        return retv;
    }

    /**
     * 以当前设置的绘制大小，获取字符的高度
     *
     * @return height
     */
    public int getStringHeight() {
        return (int) (height);
    }

    private byte[] toByteArray(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void drawStringWithRainBowColor(MatrixStack matrixStack, float x, float y, String str) {
    }

    private ArrayList<Color> colors;
    private int index;

    public void setColors(Color... color) {
        index = 0;
        colors = new ArrayList<>(List.of(color));
    }
}
