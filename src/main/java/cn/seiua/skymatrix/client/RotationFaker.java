package cn.seiua.skymatrix.client;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.component.Component;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.*;
import cn.seiua.skymatrix.utils.RotationUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

@Component
@Event(register = true)
public final class RotationFaker {
    private boolean fakeRotation;
    private float serverYaw;
    private float serverPitch;

    private float preServerYaw;
    private float preServerPitch;
    public float realYaw;
    public float realPitch;

    @Use
    public SmoothRotation smoothRotation;

    public static RotationFaker instance;

    public void setServerYaw(float serverYaw) {

        this.serverYaw = serverYaw;
    }

    public void setServerPitch(float serverPitch) {

        if (serverPitch > 90) {
//            System.out.println(serverPitch + " set");
        }
        this.serverPitch = serverPitch;
    }

    public RotationFaker() {
        instance = this;
    }

    @EventTarget
    public void onPreMotion(PreMotionEvent e) {

        if (!this.smoothRotation.running)
            return;

        ClientPlayerEntity player = SkyMatrix.mc.player;
        realYaw = player.getYaw();
        realPitch = player.getPitch();
        if (getServerPitch() > 90) {
            setServerPitch(89);
        }
        if (getServerPitch() < -90) {
            setServerPitch(-89);
        }

        player.setYaw(getServerYaw());
        player.setPitch(getServerPitch());
    }

    @EventTarget
    public void onTick(ClientTickEvent e) {
        if (!this.smoothRotation.running) {
            setServerYaw(SkyMatrix.mc.player.getYaw());
            setServerPitch(SkyMatrix.mc.player.getPitch());
        }
    }

    private float preServerYaw1;
    private float preServerPitch1;

    @EventTarget
    public void pre(UpdateTargetedEntityEvent.Pre e) {
        if (!this.smoothRotation.isClient()) {
            ClientPlayerEntity player = SkyMatrix.mc.player;
            assert SkyMatrix.mc.player != null;
            preServerYaw1 = player.getYaw();
            preServerPitch1 = player.getPitch();
            player.setPitch(getServerPitch());
            player.setYaw(getServerYaw());
        }


    }

    @EventTarget
    public void post(UpdateTargetedEntityEvent.Post e) {

        if (!this.smoothRotation.isClient()) {
            assert SkyMatrix.mc.player != null;
            SkyMatrix.mc.player.setPitch(preServerPitch1);
            SkyMatrix.mc.player.setYaw(preServerYaw1);
        }

    }

    private float py;
    private float pp;

    @EventTarget
    public void onItem(InteractItemMEvent e) {
        if (smoothRotation.task == null || smoothRotation.task.isClient()) return;
        if (e.getType().equals("HEAD")) {
            py = SkyMatrix.mc.player.getYaw();
            pp = SkyMatrix.mc.player.getPitch();
            SkyMatrix.mc.player.setYaw(getServerYaw());
            SkyMatrix.mc.player.setPitch(getServerPitch());
        } else {
            SkyMatrix.mc.player.setYaw(py);
            SkyMatrix.mc.player.setPitch(pp);
        }
//        Vec2f vec2f = new Vec2f((float) Math.cos(Math.toRadians(SkyMatrix.mc.player.getYaw())), (float) Math.sin(Math.toRadians(SkyMatrix.mc.player.getYaw())));
//        Vec2f vec2f1 = new Vec2f((float) Math.cos(Math.toRadians(this.getServerYaw())), (float) Math.sin(Math.toRadians(this.getServerYaw())));
//        if (MathUtils.calculateAngle(vec2f1, vec2f) >= 90) {
//            SkyMatrix.mc.options.sprintKey.setPressed(false);
//        }

    }

    @EventTarget
    public void onRotation(LivingEntityRenderEvent e) {
        if (!this.smoothRotation.running) return;
        if (smoothRotation.task.isClient()) return;
        if (e.getEntity() == SkyMatrix.mc.player) {
            AbstractClientPlayerEntity entity = (AbstractClientPlayerEntity) e.getEntity();
            if (e.getType().equals("HEAD")) {

                entity.headYaw = getServerYaw();
                prePitch = entity.getPitch();
                entity.prevHeadYaw = preServerYaw;
                entity.setPitch(getServerPitch());
                entity.prevPitch = getServerPitch();
//                e.getEntity().prevBodyYaw = getServerYaw();
//                e.getEntity().bodyYaw = getServerYaw();


            } else {
                entity.setPitch(prePitch);
//                entity.prevPitch = realPitch;
//                entity.headYaw = realYaw;
//                entity.prevHeadYaw = realYaw;
            }
        }
    }

    private float preYaw;
    private float prePitch;

    @EventTarget
    public void onPostMotion(PostMotionEvent e) {
        if (!this.smoothRotation.running)
            return;

        ClientPlayerEntity player = SkyMatrix.mc.player;
        player.setYaw(realYaw);
        player.setPitch(realPitch);
        fakeRotation = false;
    }

    public void faceVectorPacket(Vec3d vec) {
        Rotation needed = RotationUtils.getNeededRotations(vec);
        faceVectorPacket(needed);
    }

    public void faceVectorClient(Vec3d vec) {
        Rotation needed = RotationUtils.getNeededRotations(vec);
        faceVectorClient(needed);
    }

    public void faceVectorPacket(Rotation rotation) {
        Rotation needed = rotation;

        if (!smoothRotation.task.isDisPitch()) {
            preServerPitch = serverPitch;
            serverPitch = needed.getPitch();
        }
        if (!smoothRotation.task.isDisYaw()) {
            preServerYaw = serverYaw;
            serverYaw = needed.getYaw();
        }
        fakeRotation = true;


    }

    public void faceVectorClient(Rotation rotation) {
        Rotation needed = rotation;

        ClientPlayerEntity player = SkyMatrix.mc.player;
        this.serverPitch = needed.getPitch();


        float yaw = RotationUtils.limitAngleChange(player.getYaw(), needed.getYaw());
        player.setYaw(
                yaw);
        this.serverYaw = yaw;
        player.setPitch(needed.getPitch());
    }

    public void faceVectorClientIgnorePitch(Vec3d vec) {
        Rotation needed = RotationUtils.getNeededRotations(vec);

        ClientPlayerEntity player = SkyMatrix.mc.player;
        SkyMatrix.mc.player.setYaw(
                RotationUtils.limitAngleChange(player.getYaw(), needed.getYaw()));
        SkyMatrix.mc.player.setPitch(0);
    }

    public synchronized float getServerYaw() {
        return ((smoothRotation.task == null || smoothRotation.task.isClient()) ? SkyMatrix.mc.player.getYaw() : serverYaw);
    }

    public synchronized float getServerPitch() {
        return (smoothRotation.task == null || smoothRotation.task.isClient()) ? SkyMatrix.mc.player.getPitch() : serverPitch;
    }

    public Vec3d getServerRotationVec3d() {
        return Vec3d.fromPolar(getServerPitch(), getServerYaw());
    }

    private boolean keepServer;

    public void release() {
        keepServer = false;
    }

    public void keepServer() {
    }
}
