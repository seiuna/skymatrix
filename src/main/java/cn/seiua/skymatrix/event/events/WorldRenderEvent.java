package cn.seiua.skymatrix.event.events;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.event.Event;
import cn.seiua.skymatrix.utils.ColorUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class WorldRenderEvent extends Event {

    private float tickDelta;
    private Camera camera;
    private GameRenderer gameRenderer;
    private RenderTickCounter tickCounter;
    private MatrixStack matrixStack;

    public WorldRenderEvent(RenderTickCounter tickCounter, Camera camera, GameRenderer gameRenderer) {

    }

    public float getTickDelta() {
        return tickDelta;
    }

    public void setTickDelta(float tickDelta) {
        this.tickDelta = tickDelta;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public GameRenderer getGameRenderer() {
        return gameRenderer;
    }

    public void setGameRenderer(GameRenderer gameRenderer) {
        this.gameRenderer = gameRenderer;
    }

    public RenderTickCounter getTickCounter() {
        return tickCounter;
    }

    public void setTickCounter(RenderTickCounter tickCounter) {
        this.tickCounter = tickCounter;
    }

    public void setMatrixStack(MatrixStack matrixStack) {
        this.matrixStack = matrixStack;
    }

    public MatrixStack getMatrixStack() {
        ColorUtils.stepWorld = 0;
        return matrixStack;
    }
}
