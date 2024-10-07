package cn.seiua.skymatrix.render;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.ChatOverride;
import cn.seiua.skymatrix.event.events.HudRenderEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import cn.seiua.skymatrix.gui.Theme;
import cn.seiua.skymatrix.gui.ui.UI;
import cn.seiua.skymatrix.utils.MathUtils;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;


public class PointTarget implements RenderTarget {

    /**
     * size 16*16
     */
    private static Identifier ARROW = Identifier.of("skymatrix", "textures/arrow.png");
    private Vector4f screenPos;

    private BlockPos blockPos;

    private Entity entity;

    public PointTarget(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public PointTarget(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void render(MatrixStack matrixStack, float delta) {
        updateScreenPos(matrixStack, blockPos != null ? new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()) : entity.getLeashPos(delta));
    }

    @Override
    public void render2D(MatrixStack matrixStack, float delta) {
        if (screenPos == null) {
            return;
        }
        Window window = SkyMatrix.mc.getWindow();
        Vec2f center = new Vec2f((float) window.getWidth() / 2, (float) window.getHeight() / 2);
        Vec3d vec = new Vec3d((float) (screenPos.x - center.x), (float) (screenPos.y - center.y), 0).normalize();
        if (screenPos.z >= 1) {
            vec = vec.multiply(-1);
        }
        Vec2f intersect = getBoundingIntersectPoint(vec, window);
        matrixStack.push();
        float ms = UI.getS();
        RenderSystem.enableBlend();
        RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_LINEAR);
        double angle = Math.toDegrees(StrictMath.atan2(
                vec.y,
                vec.x))
                + 90f;
        matrixStack.translate((intersect.x - 16) / ms, (intersect.y - 16) / ms, 0);
        matrixStack.multiply(new Quaternionf().rotationXYZ(0, 0, (float) Math.toRadians(angle)));
        HudRenderEvent.instance.getContext().drawTexture(ARROW, 0, 0, 0, 0, 16, 16, 16, 16);
        matrixStack.pop();

    }

    private void updateScreenPos(MatrixStack matrixStack, Vec3d pos) {
        Camera camera = WorldRenderEvent.getInstance().getCamera();
        Vec3d cameraPos = camera.getPos();
        matrixStack.push();
        Vector4f screenPos = new Vector4f((float) (pos.x - cameraPos.x), (float) (pos.y - cameraPos.y), (float) (pos.z - cameraPos.z), 1.0f);
        Matrix4f modelViewProjection = new Matrix4f();
        matrixStack.peek().getPositionMatrix().get(modelViewProjection);
        modelViewProjection.mul(WorldRenderEvent.getInstance().getProjectionMatrix());
        modelViewProjection.mul(WorldRenderEvent.getInstance().getPositionMatrix());
        modelViewProjection.transform(screenPos);
        screenPos.x /= screenPos.w;
        screenPos.y /= screenPos.w;
        screenPos.z /= screenPos.w;
        screenPos.x = (screenPos.x + 1.0f) * SkyMatrix.mc.getWindow().getFramebufferWidth() / 2.0f;
        screenPos.y = (1.0f - screenPos.y) * SkyMatrix.mc.getWindow().getFramebufferHeight() / 2.0f;
        screenPos.z = (1.0f + screenPos.z) / 2.0f;
        matrixStack.peek();
        this.screenPos = screenPos;
        new BlockTarget(blockPos, Theme.getInstance().THEME_UI_SELECTED::geColor).render(matrixStack, 0);
    }

    private Vec2f getBoundingIntersectPoint(Vec3d position, Window window) {
        float ms = UI.getS();
        //将点落在屏幕边框位置设置四条边框的向量
        Vec2f[] screenBounds = new Vec2f[]{
                new Vec2f(0 + 40 * ms, 0 + 40 * ms),
                new Vec2f(window.getWidth() - 40 * ms, 0),
                new Vec2f(window.getWidth() - 40 * ms, window.getHeight() - 50 * ms),
                new Vec2f(0 + 40 * ms, window.getHeight() - 60 * ms)
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
}
