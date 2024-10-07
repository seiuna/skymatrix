package cn.seiua.skymatrix.event.events;

import cn.seiua.skymatrix.event.Event;
import cn.seiua.skymatrix.utils.ColorUtils;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class WorldRenderEvent extends Event {

    private float tickDelta;
    private Camera camera;
    private GameRenderer gameRenderer;
    private RenderTickCounter tickCounter;
    private MatrixStack matrixStack;
    private static WorldRenderEvent INSTANCE = new WorldRenderEvent(null, null, null);
    private Matrix4f positionMatrix;
    private Matrix4f projectionMatrix;

    public static WorldRenderEvent getInstance() {

        return INSTANCE;
    }


    public Matrix4f getPositionMatrix() {
        return positionMatrix;
    }

    public void setPositionMatrix(Matrix4f positionMatrix) {
        this.positionMatrix = positionMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void setProjectionMatrix(Matrix4f projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

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
