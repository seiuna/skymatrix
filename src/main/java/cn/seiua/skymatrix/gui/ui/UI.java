package cn.seiua.skymatrix.gui.ui;


import cn.seiua.skymatrix.client.Run;
import cn.seiua.skymatrix.client.config.Setting;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.gui.Theme;
import cn.seiua.skymatrix.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

public abstract class UI {
    private int x;
    private int y;
    private int z;

    public boolean charTyped(char chr, int modifiers) {
        return true;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    private String hideValue;

    public String getHideValue() {
        return hideValue;
    }

    public void setHideValue(String hideValue) {
        this.hideValue = hideValue;
    }

    private int width;
    private int height;
    protected double mouseX;
    protected double mouseY;
    private boolean mid;

    protected void updateMouse(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    private Run inBoxLeft;
    private Run inBoxRight;
    private boolean drag;
    private int px;
    private int py;

    public static float getS() {
        int ms = Math.round(MinecraftClient.getInstance().getWindow().getWidth() * 1.0f / MinecraftClient.getInstance().getWindow().getScaledWidth());
        return ms * Setting.getInstance().scale.getValue().floatValue();
    }

    public static void addRenderDetail(DrawDetial drawDetial) {
        ClickGui.drawDetial = drawDetial;
    }

    public static String upperFirst(String b) {
        char[] c = b.toCharArray();
        boolean flag = true;
        for (int i = 0; i < c.length; i++) {
            if (flag) {
                c[i] = Character.toUpperCase(c[i]);
                flag = false;
            }
            if (c[i] == ' ') {
                flag = true;
            }
        }
        return new String(c);
    }

    public void setInBoxLeft(Run inBoxLeft) {
        this.inBoxLeft = inBoxLeft;
    }

    public void updateUI() {

    }

    public void setInBoxRight(Run inBoxRight) {
        this.inBoxRight = inBoxRight;
    }

    public boolean isMid() {
        return mid;
    }

    public void setMid(boolean mid) {
        this.mid = mid;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int get(int v) {
        return v / MinecraftClient.getInstance().options.getGuiScale().getValue();
    }

    public abstract void render(DrawContext matrixStack, int mouseX, int mouseY, float delta);

    public void handleMouseInput(int mouseX, int mouseY) {

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void update(int x, int y) {
        setX(x);
        setY(y);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public abstract void initUI();

    public abstract boolean keyReleased(int keyCode, int scanCode, int modifiers);

    public void keyPressed(int keyCode, int scanCode, int modifiers) {

    }

    public boolean isInBox() {
        int tx = x;
        int ty = y;
        if (mid) {
            tx = x - width / 2;
            ty = y - height / 2;
        }

        if ((mouseX >= tx && mouseX <= tx + width && mouseY >= ty && mouseY <= ty + height)) {
            return true;
        }


        return false;
    }

    public void mouseMoved(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        if (drag) {
            setX((int) (mouseX - px));
            setY((int) (mouseY - py));
        }

    }

    public void drawLine(MatrixStack matrixStack) {
        RenderUtils.cent();
        RenderUtils.setColor(Theme.getInstance().SUBLINE.geColor());
        RenderUtils.drawRound2D(new Box(getX(), getY() - getHeight() / 2, 0, getX() + 249, getY() - getHeight() / 2 + 1, 0), matrixStack, 0);
    }

    public int getMaskHeight() {
        return -1;
    }

    public int getMaskWidth() {
        return -1;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {


        if (isInBox()) {
            if (button == 0) {
                if (inBoxLeft != null) {
                    inBoxLeft.run();
                    return true;
                }
            }
            if (button == 1) {
                if (inBoxRight != null)
                    inBoxRight.run();
                return true;
            }


            px = (int) (mouseX - getX());
            py = (int) (mouseY - getY());
            drag = true;
            return false;
        }
        return true;
    }


    public boolean mouseReleased(double mouseX, double mouseY, int button) {

        drag = false;
        return true;
    }


    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return true;
    }


    public void setMouse(double i, double i1) {

        this.mouseX = i;
        this.mouseY = i1;
    }
}
