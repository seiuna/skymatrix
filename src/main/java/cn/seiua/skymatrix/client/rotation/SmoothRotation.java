package cn.seiua.skymatrix.client.rotation;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.Run;
import cn.seiua.skymatrix.client.component.Component;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.Use;

@Component
@Event(register = true)
@Deprecated
public class SmoothRotation {
    public float yawDifference;
    public boolean running = false;
    private float pitchDifference;
    private int ticks = -1;
    private int tickCounter = 0;

    public RotatioTask task;
    public RotatioTask task1;

    public boolean isClient() {
        if (task == null || task.isClient()) {
            return true;
        }
        return false;
    }

    @Use
    public RotationFaker rotationFaker;



    public void smoothLook(Rotation rotation, float ticks, Run callback, boolean client, boolean disPitch, boolean disYaw) {
        if (this.ticks == 0) {
            look(rotation);
            if (callback != null)
                callback.run();
            this.running = false;
            return;
        }

        this.task = new RotatioTask(callback, client, disPitch, disYaw);

        pitchDifference = wrapAngleTo180(rotation.getPitch() - rotationFaker.getServerPitch());
        yawDifference = wrapAngleTo180(rotation.getYaw() - rotationFaker.getServerYaw());

        this.ticks = (int) (ticks * 20);
        tickCounter = 0;
    }

    public void smoothLook(Rotation rotation, float ticks, Run callback, boolean client) {
        smoothLook(rotation, ticks, callback, client, false, false);
    }

    public void look(Rotation rotation) {

    }

    public void reset() {
        this.tickCounter = 0;
        ticks = 0;
        task = null;
        running = false;
    }

    public void onTick() {
        if (!enable) return;
        if (SkyMatrix.mc.player == null) return;
        if (task == null) return;
        if (tickCounter < ticks) {
            running = true;
            if (task.client) {
                rotationFaker.faceVectorClient(new Rotation(rotationFaker.getServerYaw() + yawDifference / ticks, rotationFaker.getServerPitch() + pitchDifference / ticks));
            } else
                rotationFaker.faceVectorPacket(new Rotation(rotationFaker.getServerYaw() + yawDifference / ticks, rotationFaker.getServerPitch() + pitchDifference / ticks));
            tickCounter++;
        } else if (task.getCallback() != null) {
            running = false;
            task.getCallback().run();
            if (!this.task.client) {
//                 if (!task.isDisPitch()) {
                rotationFaker.setServerPitch(SkyMatrix.mc.player.getPitch());
//                 }
//                 if (!task.isDisYaw()) {
                rotationFaker.setServerYaw(SkyMatrix.mc.player.getYaw());
                //                }
            }
            task = null;

        } else {
            if (!this.task.client) {

                rotationFaker.setServerPitch(SkyMatrix.mc.player.getPitch());
                rotationFaker.setServerYaw(SkyMatrix.mc.player.getYaw());
            }
            task = null;
            running = false;
        }
    }

    private double wrapAngleTo180(double angle) {
        return angle - Math.floor(angle / 360 + 0.5) * 360;
    }

    private float wrapAngleTo180(float angle) {
        return (float) (angle - Math.floor(angle / 360 + 0.5) * 360);
    }

    private boolean enable = true;

    public boolean isEnable() {
        return enable;
    }

    public void enable() {
        enable = true;
    }

    public void disable() {
        enable = false;
    }

    class RotatioTask {
        private Run callback = null;
        private boolean client;
        private boolean disPitch;
        private boolean disYaw;

        public RotatioTask(Run callback, boolean client, boolean disPitch, boolean disYaw) {
            this.callback = callback;
            this.client = client;
            this.disPitch = disPitch;
            this.disYaw = disYaw;
        }

        public boolean isDisPitch() {
            return disPitch;
        }

        public void setDisPitch(boolean disPitch) {
            this.disPitch = disPitch;
        }

        public boolean isDisYaw() {
            return disYaw;
        }

        public void setDisYaw(boolean disYaw) {
            this.disYaw = disYaw;
        }

        public Run getCallback() {
            return callback;
        }

        public void setCallback(Run callback) {
            this.callback = callback;
        }

        public boolean isClient() {
            return client;
        }

        public void setClient(boolean client) {
            this.client = client;
        }
    }
}
