package cn.seiua.skymatrix.gui.ui;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.config.Setting;
import cn.seiua.skymatrix.config.option.SkyblockItemSelect;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.gui.DrawLine;
import cn.seiua.skymatrix.gui.Theme;
import cn.seiua.skymatrix.utils.ColorUtils;
import cn.seiua.skymatrix.utils.OptionInfo;
import cn.seiua.skymatrix.utils.RenderUtils;
import cn.seiua.skymatrix.utils.SkyBlockUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class UISkyblockItemSelect extends UI {
    private DrawLine drawLine = new DrawLine(250);
    private OptionInfo<SkyblockItemSelect> optionInfo;
    private float o = 0;
    private Map<String, String> itemMap;
    private int index;

    public UISkyblockItemSelect(OptionInfo<SkyblockItemSelect> optionInfo) {
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

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.updateMouse(mouseX, mouseY);
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
        ClickGui.iconfontRenderer18.centeredH();
        ClickGui.iconfontRenderer18.centeredV();


        Color color = Theme.getInstance().THEME_UI_SELECTED.geColor();
        String v = "NONE";
        if (this.optionInfo.getTarget().getUuid() != "none") {

            if (this.itemMap.get(this.optionInfo.getTarget().getUuid()) != null) {
                v = this.itemMap.get(this.optionInfo.getTarget().getUuid());
                v = upperFirst(v.replace("_", " ").toLowerCase());
                while (ClickGui.fontRenderer16.getStringWidth(v) > 154) {
                    v = v.substring(0, v.length() - 2);
                }
            } else {
                v = "NOFOUND";
            }
        }
        if (this.optionInfo.getTarget().isAutoSelect()) {
            color = ColorUtils.setHue(Color.red, (float) (System.currentTimeMillis() % 2000) / 2000);

        }
        ClickGui.iconfontRenderer18.setColor(color);

        String icon = "\uE935";
        ClickGui.iconfontRenderer18.drawString(matrixStack, getX() - 77, getY(), icon);


        ClickGui.fontRenderer16.centeredH();
        ClickGui.fontRenderer16.setColor(Theme.getInstance().THEME.geColor());

        int w = ClickGui.fontRenderer16.getStringWidth(v);
        ClickGui.fontRenderer16.setColor(color);
        ClickGui.fontRenderer16.drawString(matrixStack, drawLine.get(65), getY() - 2, 0, v);
        drawLine.append(w);

//        if (isInButton(mouseX, mouseY)) {
//            ClickGui.iconfontRenderer22.centeredH();
//            ClickGui.iconfontRenderer22.centeredV();
//            ClickGui.iconfontRenderer22.setColor(Theme.getInstance().THEME_UI_SELECTED.geColor());
//            ClickGui.iconfontRenderer22.drawString(matrixStack, getX() + 77, getY(), "\uE912");
//            ClickGui.iconfontRenderer22.resetCenteredH();
//            ClickGui.iconfontRenderer22.resetCenteredV();
//        } else {
//            ClickGui.iconfontRenderer20.centeredH();
//            ClickGui.iconfontRenderer20.centeredV();
//            ClickGui.iconfontRenderer20.setColor(Theme.getInstance().THEME.geColor());
//            ClickGui.iconfontRenderer20.drawString(matrixStack, getX() + 77, getY(), "\uE912");
//            ClickGui.iconfontRenderer20.resetCenteredH();
//            ClickGui.iconfontRenderer20.resetCenteredV();
//        }

//        if (ClickGui.instance.getFocus() == this) {
//            if (System.currentTimeMillis() % 1800 > 900) {
//                ClickGui.fontRenderer16.drawString(matrixStack, drawLine.get(0), getY(), "|");
//            }
//        }

        ClickGui.fontRenderer16.resetCenteredH();
        ClickGui.fontRenderer16.resetCenteredV();




    }

    @Override
    public void initUI() {
        int i = 0;
        itemMap = new LinkedHashMap<>();
        for (ItemStack itemStack : SkyMatrix.mc.player.getInventory().main) {
            if (i == 9) {
                break;
            }
            String uuid = SkyBlockUtils.getItemUuid(itemStack);
            String id = Setting.getInstance().full_name.isValue() ? itemStack.getName().getString() : SkyBlockUtils.getItemId(itemStack);

            if (uuid != "none") {

                if (this.optionInfo.getTarget().getFilter().filter(itemStack)) {
                    itemMap.put(uuid, id);
                    if (this.optionInfo.getTarget().getUuid() != "none") {
                        index = i;
                    }
                }

            }
            i++;
        }

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

    public String getSelected() {
        return null;
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
            if (isInBox()) {
                if (Screen.hasShiftDown()) {
                    if (this.optionInfo.getTarget().getSelector() == null) return true;
                    this.optionInfo.getTarget().setAutoSelect(!this.optionInfo.getTarget().isAutoSelect());
                    return true;
                }


                if (this.itemMap.size() == 0) return true;
                index = ((++index) % this.itemMap.size());
                this.optionInfo.getTarget().setUuid(this.itemMap.keySet().stream().toList().get(index));

            }

        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {


        super.keyPressed(keyCode, scanCode, modifiers);
    }
}
