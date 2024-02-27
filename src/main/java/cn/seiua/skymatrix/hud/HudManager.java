package cn.seiua.skymatrix.hud;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.ConfigManager;
import cn.seiua.skymatrix.client.Notice;
import cn.seiua.skymatrix.client.NoticeType;
import cn.seiua.skymatrix.client.Notification;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.Init;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.KeyBind;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.HudRenderEvent;
import cn.seiua.skymatrix.gui.Theme;
import cn.seiua.skymatrix.gui.ui.UI;
import cn.seiua.skymatrix.utils.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Event
@Sign(sign = Signs.PRO)
@SModule(name = "hud", category = "client")
public class HudManager extends Screen {

    private static HudManager instance;
    @Use
    private List<Object> objects;
    private List<ClientHud> huds;

    public HudManager() {
        super(Text.of("HudManager"));
    }

    @Value(name = "shift")
    public KeyBind keyBind = new KeyBind("toggle", List.of(GLFW.GLFW_KEY_LEFT_SHIFT, KeyBind.getMouseKey(GLFW.GLFW_MOUSE_BUTTON_1)), this::toggle);

    private void toggle() {
        if (this.focus != null) {
            this.focus.enable = !this.focus.enable;
        }
    }
    @Init
    public void init() {
        instance = this;
        huds = new ArrayList<>();
        for (Object o : objects) {
            for (Field f : o.getClass().getDeclaredFields()) {
                if (f.getType() == ClientHud.class && f.getAnnotation(Value.class) != null) {
                    try {
                        f.setAccessible(true);
                        huds.add((ClientHud) f.get(o));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }


    boolean tv1;
    boolean tv2;

    int tv5;
    int tv6;
    int tv7;

    public boolean isInBox(ClientHud hud, int mouseX, int mouseY) {
        float x = hud.getX();
        float y = hud.getY();
        float h = hud.getTarget().getHudHeight() + y;
        float w = hud.getTarget().getHudWidth() + x;
        if ((mouseX >= x && mouseX <= w && mouseY >= y && mouseY <= h)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private ClientHud focus;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float ms = UI.getS();
        mouseY = (int) (mouseY * ms);
        mouseX = (int) (mouseX * ms);
        int height = (int) (this.height * ms);
        int width = (int) (this.width * ms);
        MatrixStack matrixStack = context.getMatrices();
        matrixStack.push();
        matrixStack.scale(1.0f / ms, 1.0f / ms, 1.0f / ms);
        boolean flag = false;
        boolean flag1 = !drag;
        for (ClientHud hud : huds) {
            if (hud != null) {

                if (!flag && isInBox(hud, mouseX, mouseY) && flag1) {
                    flag = true;
                    focus = hud;

                }
                hud.getTarget().draw(matrixStack, hud.x, hud.y);
            }
        }
        if (this.focus != null) {
            flag = isInBox(this.focus, mouseX, mouseY);
        }

        if (!flag) {
            if (flag1) {
                GLFW.glfwSetCursor(SkyMatrix.mc.getWindow().getHandle(), 0);
                this.focus = null;
            }

        } else {
            float x = focus.x;
            float y = focus.y;
            float h = focus.getTarget().getHudHeight();
            float w = focus.getTarget().getHudWidth();
            RenderUtils.resetCent();
            RenderUtils.setColor(Theme.getInstance().THEME_UI_SELECTED.geColor());
            RenderUtils.drawOutlineBox(new Box(x, y, 0, x + w, y + h, 0), matrixStack);
            long id = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_ALL_CURSOR);
            GLFW.glfwSetCursor(SkyMatrix.mc.getWindow().getHandle(), id);
        }
        matrixStack.scale(ms, ms, ms);
        matrixStack.pop();

        if (drag && this.focus != null) {

            this.focus.setX((mouseX - px));
            this.focus.setY((mouseY - py));
        } else {
            drag = false;
        }
        super.render(context, mouseX, mouseY, delta);
    }

    boolean drag;
    float px;
    float py;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float ms = UI.getS();
        mouseY = mouseY * ms;
        mouseX = mouseX * ms;
        if (focus != null) {
            if (button == 0) {
                drag = true;
                px = (float) (mouseX - this.focus.getX());
                py = (float) (mouseY - this.focus.getY());
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Use
    public ConfigManager configManager;
    @Use
    public Notification notification;

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        float ms = UI.getS();
        mouseY = mouseY * ms;
        mouseX = mouseX * ms;
        drag = false;
        return true;
    }

    @Override
    public void close() {

        configManager.writeToProfile();
        configManager.saveProfiles();
        notification.push(new Notice("Config", "profile: " + configManager.getCurrent().getName() + " saved successfully", NoticeType.INFO));

        super.close();
    }

    @EventTarget
    public void onRender(HudRenderEvent event) {
        if (SkyMatrix.mc.currentScreen == this) return;
        if (SkyMatrix.mc.options.playerListKey.isPressed()) return;
        for (ClientHud hud : huds) {
            if (hud == null) continue;
            if (!hud.enable) continue;
            float ms = UI.getS();

            MatrixStack matrixStack = event.getContext().getMatrices();
            matrixStack.push();
            matrixStack.scale(1.0f / ms, 1.0f / ms, 1.0f / ms);
            hud.getTarget().draw(matrixStack, hud.x, hud.y);

            matrixStack.scale(ms, ms, ms);
            matrixStack.pop();
        }
    }


}
