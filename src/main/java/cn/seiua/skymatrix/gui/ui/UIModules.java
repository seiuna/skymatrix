package cn.seiua.skymatrix.gui.ui;

import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.config.Hide;
import cn.seiua.skymatrix.config.IHide;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.KeyBind;
import cn.seiua.skymatrix.gui.*;
import cn.seiua.skymatrix.utils.CateInfo;
import cn.seiua.skymatrix.utils.ModuleInfo;
import cn.seiua.skymatrix.utils.RenderUtils;
import cn.seiua.skymatrix.utils.UiInfo;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UIModules extends UI {

    private CateInfo cateInfo;
    private List<ModuleInfo> modules;
    private List<UIModule> uiModules;
    private DrawLine drawLine = new DrawLine(250);

    int rs = 0;
    long t = 0;

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (UIModule u : uiModules) {
            u.charTyped(chr, modifiers);
        }
        return true;
    }

    public void addModuleInfo(ModuleInfo moduleInfo) {
        modules.add(moduleInfo);
        UIModule uiModule = new UIModule(moduleInfo);
        uiModules.add(uiModule);
        String category = moduleInfo.getCategory();
        String name = moduleInfo.getName();
        Object o = moduleInfo.getTarget();
        Class c = moduleInfo.getaClass();
        List<UI> temp = new ArrayList<>();
        HashMap<UI, List<HideB>> hideMap = new HashMap<>();
        HashMap<String, IHide> temp1 = new HashMap<>();
        HashMap<String, UI> temp2 = new HashMap<>();
        for (Field field : c.getDeclaredFields()) {
            Value value = (Value) field.getAnnotation(Value.class);
            Sign sign = (Sign) field.getAnnotation(Sign.class);
            if (field.getAnnotation(Ignore.class) != null) continue;
            if (value != null) {
                try {
                    field.setAccessible(true);
                    Object uobj = field.get(o);
                    if (value.name().equals("keyBind")) {
                        moduleInfo.setKeyBind((KeyBind) uobj);
                        continue;
                    }
                    if (uobj instanceof UIComponent) {
                        UIComponent uiComponent = (UIComponent) uobj;
                        if (uobj instanceof IHide) {
                            temp1.put(value.name(), (IHide) uobj);
                        }
                        Hide hide = field.getAnnotation(Hide.class);
                        UI ui = uiComponent.build(name, category, value.name(), sign == null ? null : sign.sign());
                        temp2.put(value.name(), ui);
                        if (ui != null) {
                            if (hide != null) {
                                ArrayList<HideB> t = new ArrayList<>();
                                for (String a : hide.following().split("&")) {
                                    t.add(new HideB(hide.value(), temp1.get(a.trim())));
                                }
                                hideMap.put(ui, t);

                            }
                            temp.add(ui);
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        uiModule.setUis(temp);
        uiModule.setHideMap(hideMap);
    }

    long time = 0;
    private int height;

    public UIModules(CateInfo cateInfo) {
        modules = new ArrayList<>();
        this.cateInfo = cateInfo;
        uiModules = new ArrayList<>();

    }

    @Override
    public void initUI() {
        UiInfo uiInfo = ClickGui.getValue("category." + cateInfo.getFullName() + ".state");
        setX(uiInfo.getX());
        setY(uiInfo.getY());
        for (UI ui : uiModules) {
            ui.initUI();
        }
        Collections.sort(this.uiModules,
                (o1, o2) -> {
                    int a = o1.moduleInfo.getName().toCharArray()[0] - o2.moduleInfo.getName().toCharArray()[0];
//                    System.out.println(a);
                    return a;
                });
    }

    public String getID() {
        return cateInfo.getName();
    }

    public static int flag;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.updateMouse(mouseX, mouseY);
        MatrixStack matrixStack = context.getMatrices();
        if (this.isInBox()) {
            flag++;
        }
        drawLine.reset(getX() - 125);
        setWidth(250);
        setHeight(58);
        setMid(true);
        int sty = (int) (getY() + t + 58);
        if (isOpen()) {

            for (UIModule uiModule : uiModules) {
                uiModule.update(getX(), sty);
                uiModule.setZ(getZ());
                uiModule.render(context, mouseX, mouseY, delta);
                sty += uiModule.getHeight();
            }
            height = sty;

        }

        RenderUtils.cent();
        RenderUtils.setColor(Theme.getInstance().BOARD.geColor());
        RenderUtils.drawRound2D(new Box(getX(), getY(), 0, getX() + 250, getY() + 58, 0), matrixStack, 10);
        RenderUtils.resetCent();
        if (isOpen()) {
            RenderUtils.drawRound2D(new Box(getX() - getWidth() / 2, getY() + getHeight() / 4, 0, getX() + 250 / 2, getY() + 58 / 2, 0), matrixStack, 0);

        }
        ClickGui.fontRenderer24.centeredH();
        ClickGui.fontRenderer24.resetCenteredV();
        ClickGui.fontRenderer24.setColor(Theme.getInstance().THEME.geColor());
        ClickGui.fontRenderer24.setDrawSize(32);
        ClickGui.fontRenderer24.drawString(matrixStack, drawLine.get(25), getY(), upperFirst(cateInfo.getName()));
        ClickGui.fontRenderer24.resetCenteredH();
        ClickGui.fontRenderer24.resetCenteredV();

    }

    public boolean isOpen() {
        UiInfo uiInfo = ClickGui.getValue("category" + "." + cateInfo.getFullName() + ".state");
        return uiInfo.isValue();
    }

    private void setOpen(boolean b) {
        ClickGui.getValue("category" + "." + cateInfo.getFullName() + ".state").setValue(b);
    }

    @Override
    public int getMaskHeight() {
        int fh = height - getY() - getHeight();
        if (fh < 1000) {
            return fh;
        } else {
            return 1000;
        }

    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        boolean flag = isInBoxA(mouseX, mouseY);
        for (UIModule uiModule : uiModules) {
            if (flag) {
                uiModule.mouseMoved(mouseX, mouseY);
            } else {
                uiModule.setMouse(-1, -1);
            }

        }


        super.mouseMoved(mouseX, mouseY);
    }

    public void update(int x, int y) {
        super.update(x, y);

    }

    @Override
    public void updateUI() {
        if (rs == 0) return;
        int f = (int) t;
        int tv = (Math.abs(rs) - 1);
        rs = tv * (Math.abs(rs) / rs);
        if (tv >= 20) {
            if (rs > 0) {
                f--;
                f--;
            } else {
                f++;
                f++;
            }
        }
        int fh = (height - getY() - f - getHeight());
        if (f > 0) {
            f = 0;
        } else {
            if (fh >= 1000) {
                if ((fh + f) < 1000) {
                    f = fh - 1000;
                    f *= -1;
                }
            } else {
                f++;
                f++;
            }
        }
        t = f;
        super.updateUI();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (isInBoxA(mouseX, mouseY)) {
            if (amount > 0) {
                rs = -100;
            } else {
                rs = 100;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public boolean isInBoxA(double mx, double my) {

        double ty = getY() + (double) getHeight() / 2;
        double ty1 = ty + getMaskHeight();


        double tx = getX() - (double) getWidth() / 2;
        double tx1 = getX() + (double) getWidth() / 2;

        return (my <= ty1 && my >= ty) && (mx <= tx1 && mx >= tx);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        boolean flag = (isInBox() && button == 0);
        if (isInBox()) {
            if (button == 1) {
                setOpen(!isOpen());
            }
        }
        if (isInBoxA(mouseX, mouseY)) {

            for (UIModule uiModule : uiModules) {
                boolean b = uiModule.mouseClicked(mouseX, mouseY, button);
                if (b == false) {
                    flag = b;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
        return flag;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        ClickGui.getValue("category." + cateInfo.getFullName() + ".state").setX(getX());
        ClickGui.getValue("category." + cateInfo.getFullName() + ".state").setY(getY());
        for (UIModule uiModule : uiModules) {
            uiModule.mouseReleased(mouseX, mouseY, button);
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {

        for (UIModule uiModule : uiModules) {
            uiModule.keyReleased(keyCode, scanCode, modifiers);
        }


        return true;
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        for (UIModule uiModule : uiModules) {
            uiModule.keyPressed(keyCode, scanCode, modifiers);
        }
    }


}
