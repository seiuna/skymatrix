package cn.seiua.skymatrix.client.rotation;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.Run;
import cn.seiua.skymatrix.client.component.Component;
import cn.seiua.skymatrix.client.component.Event;

@Component
@Event(register = true)
@Deprecated
public class RotationFuck {
    private static RotationFuck instance;
    public RotatioTask task;
    public boolean running = false;
    private int ticks = -1;

    public RotationFuck() {
        instance = this;
    }

    public static Rotation getServersideLook() {
        if (instance.task != null) {
            return new Rotation((float) instance.task.getYaw(), (float) instance.task.getPitch());
        }
        return new Rotation(SkyMatrix.mc.player.getPitch(), SkyMatrix.mc.player.getYaw());
    }

    public void smoothLook(Rotation rotation, int ticks, Run callback) {
        if (this.ticks == 0) {
            look(rotation);
            callback.run();
            return;
        }
        this.task = new RotatioTask(callback, false, 0, 0, ticks);
        this.task.pitchDifference = wrapAngleTo180(rotation.getPitch() - SkyMatrix.mc.player.getPitch());
        this.task.yawDifference = wrapAngleTo180(rotation.getYaw() - SkyMatrix.mc.player.getYaw());
        this.ticks = ticks * 20;

    }

    public void look(Rotation rotation) {
        SkyMatrix.mc.player.setPitch(rotation.getPitch());
        SkyMatrix.mc.player.setYaw(rotation.getYaw());
    }

    public void onTick() {
        if (SkyMatrix.mc.player == null) return;
        if (this.task != null && this.task.tickCounter < task.ticks) {
            running = true;
            SkyMatrix.mc.player.setPitch((float) (SkyMatrix.mc.player.getPitch() + task.pitchDifference / task.ticks));
            SkyMatrix.mc.player.setYaw((float) (SkyMatrix.mc.player.getYaw() + task.yawDifference / task.ticks));
            this.task.tickCounter++;
        } else {
            running = false;
        }

    }

    private double wrapAngleTo180(double angle) {
        return angle - Math.floor(angle / 360 + 0.5) * 360;
    }

    private float wrapAngleTo180(float angle) {
        return (float) (angle - Math.floor(angle / 360 + 0.5) * 360);
    }

    static class RotatioTask {
        private Run callback = null;
        private boolean client;
        private double pitchDifference;
        private double yawDifference;
        private double yaw;
        private double pitch;
        private int ticks;
        private int tickCounter = 0;
        ;

        public RotatioTask(Run callback, boolean client, double pitchDifference, double yawDifference, int ticks) {
            this.callback = callback;
            this.client = client;
            this.pitchDifference = pitchDifference;
            this.yawDifference = yawDifference;
            this.ticks = ticks;
        }

        public int getTicks() {
            return ticks;
        }

        public void setTicks(int ticks) {
            this.ticks = ticks;
        }

        public double getPitchDifference() {
            return pitchDifference;
        }

        public void setPitchDifference(double pitchDifference) {
            this.pitchDifference = pitchDifference;
        }

        public double getYawDifference() {
            return yawDifference;
        }

        public void setYawDifference(double yawDifference) {
            this.yawDifference = yawDifference;
        }

        public double getYaw() {
            return yaw;
        }

        public void setYaw(double yaw) {
            this.yaw = yaw;
        }

        public double getPitch() {
            return pitch;
        }

        public void setPitch(double pitch) {
            this.pitch = pitch;
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
