package cn.seiua.skymatrix.gui.ui;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.config.option.DoubleValueSlider;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.gui.DrawLine;
import cn.seiua.skymatrix.gui.Theme;
import cn.seiua.skymatrix.utils.MathUtils;
import cn.seiua.skymatrix.utils.OptionInfo;
import cn.seiua.skymatrix.utils.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class UIDoubleSlider extends UI {
    public static final double TR = 8;
    private DrawLine drawLine = new DrawLine(250);
    private OptionInfo<DoubleValueSlider> optionInfo;
    private double rate;
    private float o = 0;
    private double min;
    private double max;
    private double interval;
    private double w;
    private boolean hd;
    private boolean hd2;

    public UIDoubleSlider(OptionInfo<DoubleValueSlider> optionInfo) {
        this.optionInfo = optionInfo;
        min = this.optionInfo.getTarget().getMin().doubleValue();
        max = this.optionInfo.getTarget().getMax().doubleValue();
        interval = this.optionInfo.getTarget().getInterval().doubleValue();
        w = max - min;

        super.setInBoxLeft(this::toggle);
    }

    private Color getBoardColoar() {
        if (isInBox()) {

            o += 0.05;
//            if (tempdis) {
//                o = 0;
//            }
        } else {
            o -= 0.05;
//            tempdis = false;
        }
        if (o > 1) o = 1;
        if (o < 0) o = 0;
        Color color = Theme.getInstance().SUBBOARD.geColor();
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        Color ac = Color.getHSBColor(hsb[0], hsb[1], Math.max(hsb[2], 0.35f * o));
        return ac;
    }

    public void update(int x, int y) {
        setWidth(250);
        setHeight(50);
        super.update(x, y + getHeight() / 2);
    }

    public void toggle() {
//        this.optionInfo.getTarget().setValue(!this.optionInfo.getTarget().isValue());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.updateMouse(mouseX, mouseY);
        MatrixStack matrixStack = context.getMatrices();
        drawLine.reset(getX() - 125);
        setMid(true);
        double t1 = getTarget();
        double t2 = getTarget2();
        double d1 = getTarget() + getX();
        double d2 = getTarget2() + getX();
        double dd = Math.abs(t1 - t2);
        double minv = Math.min(optionInfo.getTarget().getValue().doubleValue(), optionInfo.getTarget().getValua().doubleValue());
        double maxv = Math.max(optionInfo.getTarget().getValue().doubleValue(), optionInfo.getTarget().getValua().doubleValue());
        double fv = getX() - 100 + (((minv - this.min) / w) * 200);
        RenderUtils.cent();
        RenderUtils.setColor(getBoardColoar());
        RenderUtils.drawRound2D(new Box(getX(), getY(), 0, getX() + 250, getY() + getHeight(), 0), matrixStack, 0);
        RenderUtils.setColor(optionInfo.getSign().color);
        RenderUtils.drawRound2D(new Box(getX() + 124, getY(), 0, getX() + 127, getY() + getHeight(), 0), matrixStack, 0);
        RenderUtils.resetCent();
        RenderUtils.cent();
        RenderUtils.setColor(Theme.getInstance().THEME.geColor());
        RenderUtils.drawRound2D(new Box(getX(), getY() + 10, 0, getX() + 200, getY() + 12, 0), matrixStack, 1);
        RenderUtils.setColor(Theme.getInstance().THEME_UI_SELECTED.geColor());
        RenderUtils.resetCent();
        RenderUtils.drawRound2D(new Box(fv, getY() + 9, 0, fv + dd, getY() + 11, 0), matrixStack, 1);
        drawLine(matrixStack);
        ClickGui.fontRenderer16.centeredH();
        ClickGui.fontRenderer16.setColor(Theme.getInstance().THEME.geColor());
        ClickGui.fontRenderer16.drawString(matrixStack, drawLine.get(25), getY() - 10, upperFirst(optionInfo.getName()));
        drawLine.append(ClickGui.fontRenderer16.getStringWidth(upperFirst(optionInfo.getName())));
        ClickGui.fontRenderer16.centeredV();
        String v = optionInfo.getTarget().toValueString(minv) + "-" + optionInfo.getTarget().toValueString(maxv);
        int w = ClickGui.fontRenderer16.getStringWidth(v);
        ClickGui.fontRenderer16.drawString(matrixStack, getX() + 96 - (float) w / 2, getY() - 10, v);
        ClickGui.fontRenderer16.resetCenteredH();
        ClickGui.fontRenderer16.resetCenteredV();
        double uw = 200;
        // true v1 max false v1 min
        boolean v1max = (Math.max(this.optionInfo.getTarget().getValue().doubleValue(), this.optionInfo.getTarget().getValua().doubleValue()) == this.optionInfo.getTarget().getValue().doubleValue());
        ClickGui.iconfontRenderer28.centeredH();
        ClickGui.iconfontRenderer28.centeredV();
        ClickGui.iconfontRenderer28.setColor(getBoardColoar());
        ClickGui.iconfontRenderer28.centeredV();
        ClickGui.iconfontRenderer28.drawString(matrixStack, (int) (getX() - uw / 2 + t1), getY() + 8, v1max ? "\uE900" : "\uE901");
        ClickGui.iconfontRenderer28.resetCenteredH();
        ClickGui.iconfontRenderer28.resetCenteredV();
        ClickGui.iconfontRenderer28.centeredH();
        ClickGui.iconfontRenderer28.centeredV();
        ClickGui.iconfontRenderer28.setColor(getBoardColoar());
        ClickGui.iconfontRenderer28.centeredV();
        ClickGui.iconfontRenderer28.drawString(matrixStack, (int) (getX() - uw / 2 + t2), getY() + 8, v1max ? "\uE901" : "\uE900");
        ClickGui.iconfontRenderer28.resetCenteredH();
        ClickGui.iconfontRenderer28.resetCenteredV();
        if (isInButton(mouseX, mouseY) || hd) {
            ClickGui.iconfontRenderer22.centeredH();
            ClickGui.iconfontRenderer22.centeredV();
            ClickGui.iconfontRenderer22.setColor(Theme.getInstance().THEME_UI_SELECTED.geColor());
            ClickGui.iconfontRenderer22.centeredV();
            ClickGui.iconfontRenderer22.drawString(matrixStack, (int) (getX() - uw / 2 + t1), getY() + 9, v1max ? "\uE900" : "\uE901");
            ClickGui.iconfontRenderer22.resetCenteredH();
            ClickGui.iconfontRenderer22.resetCenteredV();
        } else {
            ClickGui.iconfontRenderer18.centeredH();
            ClickGui.iconfontRenderer18.centeredV();
            ClickGui.iconfontRenderer18.setColor(Theme.getInstance().THEME_UI_SELECTED.geColor());
            ClickGui.iconfontRenderer18.centeredV();
            ClickGui.iconfontRenderer18.drawString(matrixStack, (int) (getX() - uw / 2 + t1), getY() + 9, v1max ? "\uE900" : "\uE901");
            ClickGui.iconfontRenderer18.resetCenteredH();
            ClickGui.iconfontRenderer18.resetCenteredV();
        }
        if (isInButton2(mouseX, mouseY) || hd2) {
            ClickGui.iconfontRenderer22.centeredH();
            ClickGui.iconfontRenderer22.centeredV();
            ClickGui.iconfontRenderer22.setColor(Theme.getInstance().THEME_UI_SELECTED.geColor());
            ClickGui.iconfontRenderer22.centeredV();
            ClickGui.iconfontRenderer22.drawString(matrixStack, (int) (getX() - uw / 2 + t2), getY() + 9, v1max ? "\uE901" : "\uE900");
            ClickGui.iconfontRenderer22.resetCenteredH();
            ClickGui.iconfontRenderer22.resetCenteredV();
        } else {
            ClickGui.iconfontRenderer18.centeredH();
            ClickGui.iconfontRenderer18.centeredV();
            ClickGui.iconfontRenderer18.setColor(Theme.getInstance().THEME_UI_SELECTED.geColor());
            ClickGui.iconfontRenderer18.centeredV();
            ClickGui.iconfontRenderer18.drawString(matrixStack, (int) (getX() - uw / 2 + t2), getY() + 9, v1max ? "\uE901" : "\uE900");
            ClickGui.iconfontRenderer18.resetCenteredH();
            ClickGui.iconfontRenderer18.resetCenteredV();
        }
    }

    public double getTarget() {
        rate = (this.optionInfo.getTarget().getValue().doubleValue() - this.min) / w;
        double target = rate * 200;
        return target;
    }

    public double getTarget2() {
        rate = (this.optionInfo.getTarget().getValua().doubleValue() - this.min) / w;
        double target = rate * 200;
        return target;
    }

    @Override
    public void initUI() {

    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean isInButton(double mouseX, double mouseY) {
        double x = (getX() - 100 + getTarget());
        double y = getY() + 9;
        double r = Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2));
        if (r <= TR) {
            return true;
        }
        return false;
    }

    public boolean isInButton2(double mouseX, double mouseY) {
        double x = (getX() - 100 + getTarget2());
        double y = getY() + 9;
        double r = Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2));
        if (r <= TR) {
            return true;
        }
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (hd) {
            double max = (getX() + 100);
            double min = (getX() - 100);
            double value = mouseX;
            if (value > max) value = max;
            if (value < min) value = min;
            double v = ((value - min) / 200) * w + this.min;
            Number tv = MathUtils.findClosest(this.min, this.max, this.interval, v);
            this.optionInfo.getTarget().setValue(tv);
            return;
        }
        if (hd2) {
            double max = (getX() + 100);
            double min = (getX() - 100);
            double value = mouseX;
            if (value > max) value = max;
            if (value < min) value = min;
            double v = ((value - min) / 200) * w + this.min;
            Number tv = MathUtils.findClosest(this.min, this.max, this.interval, v);
            this.optionInfo.getTarget().setValua(tv);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isInBox()) {
            if (InputUtil.isKeyPressed(SkyMatrix.mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) && button == 1) {
                this.optionInfo.openDoc();
                return super.mouseClicked(mouseX, mouseY, button);
            }
        }
        if (button == 0) {
            if (isInButton(mouseX, mouseY)) {
                hd = true;
            }
            if (isInButton2(mouseX, mouseY)) {
                hd2 = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            hd = false;
            hd2 = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

}
