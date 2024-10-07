package cn.seiua.skymatrix.client.module.modules.test;


import baritone.api.BaritoneAPI;
import cn.seiua.skymatrix.client.ChatOverride;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.client.rotation.ClientRotation;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.KeyBind;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.HudRenderEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import cn.seiua.skymatrix.render.PointTarget;
import cn.seiua.skymatrix.utils.MathUtils;
import cn.seiua.skymatrix.utils.ReflectUtils;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Event
@Sign(sign = Signs.FREE)
@SModule(name = "TestRender", category = "test")
public class TestRender implements IToggle {
    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(Arrays.asList(), ReflectUtils.getModuleName(this));
    private List<BlockPos> blocks = new CopyOnWriteArrayList<>();
    @Use
    private ClientRotation rotation;
    private int cd = 0;
    private Entity lastTarget;
    private Vector4f screenPos;
    private PointTarget pointTarget = new PointTarget(new BlockPos(0, 0, 0));

    @EventTarget
    public void onTick(ClientTickEvent event) {


//        }
    }

    @EventTarget
    public void onRender(HudRenderEvent event) {
        pointTarget.render2D(event.getContext().getMatrices(), event.getTickDelta());
    }

    @EventTarget
    public void onRender(WorldRenderEvent event) {
        pointTarget.render(event.getMatrixStack(), event.getTickDelta());

    }

    private Vec2f getBoundingIntersectPoint(Vec3d position, Window window) {
        //将点落在屏幕边框位置设置四条边框的向量
        Vec2f[] screenBounds = new Vec2f[]{
                new Vec2f(0, 0),
                new Vec2f(window.getWidth(), 0),
                new Vec2f(window.getWidth(), window.getHeight()),
                new Vec2f(0, window.getHeight())
        };
        Vec2f center = new Vec2f((float) window.getWidth() / 2, (float) window.getHeight() / 2);
        Vec3d pos = new Vec3d(center.x + (float) position.x, center.y + (float) position.y, 0);
        //计算每两个向量所构成的直线与pos的交点
        Vec2f closest = null;
        for (int i = 0; i < screenBounds.length; i++) {
            Vec2f v = screenBounds[i];
            Vec2f v2 = screenBounds[(i + 1) % screenBounds.length];

            Vec2f intersection = MathUtils.getIntersectionPoint(v, v2, center, new Vec2f((float) center.x, (float) center.y).add(new Vec2f((float) position.x, (float) position.y).multiply(1600)));
            if (intersection != null) {
                if (closest == null || new Vec2f(intersection.x - center.x, intersection.y - center.y).length() < new Vec2f(closest.x - center.x, closest.y - center.y).length()) {
                    closest = intersection;
                }
            }
        }

        Vec2f v = MathUtils.getIntersectionPoint(new Vec2f(closest.x, closest.y), new Vec2f((float) pos.x, (float) pos.y));
        //计算pos到closest的交点
        ChatOverride.getInstance().setOverride(Text.of((int) closest.x + " " + (int) closest.y + "  " + (int) screenPos.x + " " + (int) screenPos.y + " " + (int) screenPos.z));

        return closest;
    }

    @EventTarget
    public void disable() {
        rotation.cancelClientLook();
        rotation.cancelServerLook();
        BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
    }
}