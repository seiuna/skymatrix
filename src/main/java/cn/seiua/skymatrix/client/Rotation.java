package cn.seiua.skymatrix.client;

import net.minecraft.util.math.MathHelper;

public class Rotation {
    public float yaw;
    public float pitch;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static Rotation wrapped(float yaw, float pitch) {
        return new Rotation(MathHelper.wrapDegrees(yaw),
                MathHelper.wrapDegrees(pitch));
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
