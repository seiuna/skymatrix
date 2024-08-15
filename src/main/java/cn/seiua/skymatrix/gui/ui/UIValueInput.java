package cn.seiua.skymatrix.gui.ui;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.config.option.ValueInput;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.gui.DrawLine;
import cn.seiua.skymatrix.gui.Theme;
import cn.seiua.skymatrix.utils.OptionInfo;
import cn.seiua.skymatrix.utils.RenderUtils;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Objects;

public class UIValueInput extends UI {
    private DrawLine drawLine = new DrawLine(250);
    private OptionInfo<ValueInput> optionInfo;
    private float o = 0;

    public UIValueInput(OptionInfo<ValueInput> optionInfo) {
        this.optionInfo = optionInfo;

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
        setHeight(52);
        setMid(true);
        super.update(x, y + getHeight() / 2);
    }

    public static int flag;

    public boolean charTyped(char chr, int modifiers) {
        if (ClickGui.instance.getFocus() == this) {



                ValueInput valueInput = this.optionInfo.getTarget();
                valueInput.setValue(valueInput.getValue() + chr);


        }
        return
                false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.updateMouse(mouseX, mouseY);
        if (ClickGui.instance.getFocus() == this && isInBox() && !isInButton(mouseX, mouseY)) {
            flag++;
        }
        MatrixStack matrixStack = context.getMatrices();
        drawLine.reset(getX() - 125);

        RenderUtils.cent();
        RenderUtils.setColor(getBoardColoar());
        RenderUtils.drawRound2D(new Box(getX(), getY(), 0, getX() + 250, getY() + getHeight(), 0), matrixStack, 0);
        RenderUtils.setColor(Theme.getInstance().THEME_UI_SELECTED.geColor());
        RenderUtils.drawRound2D(new Box(getX(), getY(), 0, getX() + 200, getY() + 42, 0), matrixStack, 7);
        RenderUtils.setColor(getBoardColoar());
        RenderUtils.drawRound2D(new Box(getX(), getY(), 0, getX() + 196, getY() + 38, 0), matrixStack, 7);
        RenderUtils.setColor(optionInfo.getSign().color);
        RenderUtils.drawRound2D(new Box(getX() + 124, getY(), 0, getX() + 127, getY() + getHeight(), 0), matrixStack, 0);
        RenderUtils.resetCent();

        ClickGui.iconfontRenderer26.centeredH();
        ClickGui.iconfontRenderer26.centeredV();
        ClickGui.iconfontRenderer26.setColor(Theme.getInstance().THEME_UI_SELECTED.geColor());

        String icon = optionInfo.getTarget().getType();
        ClickGui.iconfontRenderer26.drawString(matrixStack, getX() - 77, getY(), icon);


        ClickGui.fontRenderer16.centeredH();
        ClickGui.fontRenderer16.setColor(Theme.getInstance().THEME.geColor());
        String v = optionInfo.getTarget().pre + optionInfo.getTarget().getValue();
        boolean flag = false;
        if (optionInfo.getTarget().getValue() == null || Objects.equals(optionInfo.getTarget().getValue(), "")) {
            flag = true;
            ClickGui.fontRenderer16.setColor(Theme.getInstance().THEME.geColor().darker());
            v = optionInfo.getTarget().pre + optionInfo.getTarget().getSuggestion();
        }

        int w = ClickGui.fontRenderer16.getStringWidth(v);
        ClickGui.fontRenderer16.drawString(matrixStack, drawLine.get(65), getY(), v);
        if (flag) {
            w = ClickGui.fontRenderer16.getStringWidth(optionInfo.getTarget().pre);
        }
        drawLine.append(w);


        if (isInButton(mouseX, mouseY)) {
            ClickGui.iconfontRenderer22.centeredH();
            ClickGui.iconfontRenderer22.centeredV();
            ClickGui.iconfontRenderer22.setColor(Theme.getInstance().THEME_UI_SELECTED.geColor());
            ClickGui.iconfontRenderer22.drawString(matrixStack, getX() + 77, getY(), "\uE912");
            ClickGui.iconfontRenderer22.resetCenteredH();
            ClickGui.iconfontRenderer22.resetCenteredV();
        } else {
            ClickGui.iconfontRenderer20.centeredH();
            ClickGui.iconfontRenderer20.centeredV();
            ClickGui.iconfontRenderer20.setColor(Theme.getInstance().THEME.geColor());
            ClickGui.iconfontRenderer20.drawString(matrixStack, getX() + 77, getY(), "\uE912");
            ClickGui.iconfontRenderer20.resetCenteredH();
            ClickGui.iconfontRenderer20.resetCenteredV();
        }

        if (ClickGui.instance.getFocus() == this) {
            if (System.currentTimeMillis() % 1800 > 900) {
                ClickGui.fontRenderer16.drawString(matrixStack, drawLine.get(0), getY(), "|");
            }
        }

        ClickGui.fontRenderer16.resetCenteredH();
        ClickGui.fontRenderer16.resetCenteredV();


        drawLine(matrixStack);


    }

    @Override
    public void initUI() {

    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean isInButton(double mouseX, double mouseY) {
        double x = getX() + 77;
        double y = getY();
        double r = Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2));
        if (r <= 18) {
            return true;
        }
        return false;
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
                if (ClickGui.instance.getFocus() == this) {
                    ClickGui.instance.setFocus(null);
                } else {
                    ClickGui.instance.setFocus(this);
                }

            }

        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        ValueInput valueInput = this.optionInfo.getTarget();
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            ClickGui.instance.setFocus(null);

        }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (valueInput.getValue().length() != 0) {
                valueInput.setValue(valueInput.getValue().substring(0, valueInput.getValue().length() - 1));
            }
        }
//
//        String key = GLFW.glfwGetKeyName(keyCode, scanCode);
//        if (GLFW.GLFW_KEY_SPACE == keyCode) {
//            key = " ";
//        }
//
//        if (key != null) {
////            System.out.println(key);
//
//            valueInput.setValue(valueInput.getValue() + key);
//
//        }
//

        super.keyPressed(keyCode, scanCode, modifiers);
    }
}
