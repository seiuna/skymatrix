package cn.seiua.skymatrix.utils;


import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.Rotation;
import cn.seiua.skymatrix.client.RotationFaker;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


public enum RotationUtils {
    ;

    public static Vec3d getEyesPos() {
        ClientPlayerEntity player = SkyMatrix.mc.player;

        return new Vec3d(player.getX(),
                player.getY() + player.getEyeHeight(player.getPose()),
                player.getZ());
    }

    public static Vec3d getClientLookVec() {
        float f = 0.017453292F;
        float pi = (float) Math.PI;

        float f1 = MathHelper.cos(-RotationFaker.instance.getServerYaw() * f - pi);
        float f2 = MathHelper.sin(-RotationFaker.instance.getServerYaw() * f - pi);
        float f3 = -MathHelper.cos(-RotationFaker.instance.getServerPitch() * f);
        float f4 = MathHelper.sin(-RotationFaker.instance.getServerPitch() * f);

        return new Vec3d(f2 * f3, f4, f1 * f3);
    }

    public static Rotation toRotation(Vec3d vec3d) {
        float yaw = (float) Math.toDegrees(Math.atan2(vec3d.x, vec3d.z)) * -1;
        float pitch = (float) Math.toDegrees(Math.atan2(vec3d.y, Math.sqrt(Math.pow(vec3d.x, 2) + Math.pow(vec3d.z, 2)))) * -1;
        return new Rotation(yaw, pitch);
    }

    public static Rotation toRotation(Entity entity) {
        return toRotation(entity.getEyePos().subtract(0, 0.2, 0).subtract(SkyMatrix.mc.player.getEyePos()));
    }

    public static Rotation toRotation(BlockPos blockPos) {
        return toRotation(blockPos.toCenterPos().subtract(SkyMatrix.mc.player.getEyePos()));
    }
    public static Rotation getNeededRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return Rotation.wrapped(yaw, pitch);
    }

    public static double getAngleToLookVec(Vec3d vec) {
        Rotation needed = getNeededRotations(vec);

        float currentYaw = MathHelper.wrapDegrees(RotationFaker.instance.getServerYaw());
        float currentPitch = MathHelper.wrapDegrees(RotationFaker.instance.getServerPitch());

        float diffYaw = MathHelper.wrapDegrees(currentYaw - needed.yaw);
        float diffPitch = MathHelper.wrapDegrees(currentPitch - needed.pitch);

        return Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
    }


    public static boolean isFacingBox(Box box, double range) {
        Vec3d start = getEyesPos();
        Vec3d end = start.add(getClientLookVec().multiply(range));
        return box.raycast(start, end).isPresent();
    }

    public static float getHorizontalAngleToLookVec(Vec3d vec) {
        Rotation needed = getNeededRotations(vec);
        return MathHelper.wrapDegrees(RotationFaker.instance.getServerYaw())
                - needed.yaw;
    }

    public static Rotation slowlyTurnTowards(Rotation end, float maxChange) {
        Entity player = SkyMatrix.mc.player;
        float startYaw = player.prevYaw;
        float startPitch = player.prevPitch;
        float endYaw = end.getYaw();
        float endPitch = end.getPitch();

        float yawChange = Math.abs(MathHelper.wrapDegrees(endYaw - startYaw));
        float pitchChange =
                Math.abs(MathHelper.wrapDegrees(endPitch - startPitch));

        float maxChangeYaw =
                Math.min(maxChange, maxChange * yawChange / pitchChange);
        float maxChangePitch =
                Math.min(maxChange, maxChange * pitchChange / yawChange);

        float nextYaw = limitAngleChange(startYaw, endYaw, maxChangeYaw);
        float nextPitch =
                limitAngleChange(startPitch, endPitch, maxChangePitch);

        return new Rotation(nextYaw, nextPitch);
    }


    public static float limitAngleChange(float current, float intended,
                                         float maxChange) {
        float currentWrapped = MathHelper.wrapDegrees(current);
        float intendedWrapped = MathHelper.wrapDegrees(intended);

        float change = MathHelper.wrapDegrees(intendedWrapped - currentWrapped);
        change = MathHelper.clamp(change, -maxChange, maxChange);

        return current + change;
    }


    public static float limitAngleChange(float current, float intended) {
        float currentWrapped = MathHelper.wrapDegrees(current);
        float intendedWrapped = MathHelper.wrapDegrees(intended);

        float change = MathHelper.wrapDegrees(intendedWrapped - currentWrapped);

        return current + change;
    }


}