package cn.seiua.skymatrix.client.module.modules;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.rotation.RotationFaker;
import cn.seiua.skymatrix.client.waypoint.WaypointEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public interface ModuleTools {


    static void updataTarget() {
        float y = SkyMatrix.mc.player.getYaw();
        float p = SkyMatrix.mc.player.getPitch();
        SkyMatrix.mc.player.setYaw(RotationFaker.instance.getServerYaw());
        SkyMatrix.mc.player.setPitch(RotationFaker.instance.getServerPitch());
        SkyMatrix.mc.crosshairTarget = SkyMatrix.mc.player.raycast(5, 1, false);
        SkyMatrix.mc.player.setYaw(y);
        SkyMatrix.mc.player.setPitch(p);
    }

    static void execute(String cmd) {
        SkyMatrix.mc.getNetworkHandler().sendCommand(cmd);
    }

    static BlockPos getTarget() {
        BlockPos bp = null;
        if (SkyMatrix.mc.crosshairTarget.getType().equals(HitResult.Type.BLOCK)) {
            bp = ((BlockHitResult) SkyMatrix.mc.crosshairTarget).getBlockPos();
        }
        return bp;
    }

    static void pathing(WaypointEntity entity) {
        BaritoneAPI.getSettings().allowSprint.value = true;
        BaritoneAPI.getSettings().considerPotionEffects.value = true;
        BaritoneAPI.getSettings().avoidance.value = false;
        BaritoneAPI.getSettings().allowWalkOnBottomSlab.value = true;
        BaritoneAPI.getSettings().antiCheatCompatibility.value = true;
        BaritoneAPI.getSettings().allowParkour.value = true;
        BaritoneAPI.getSettings().allowBreak.value = false;
        BaritoneAPI.getSettings().allowParkourAscend.value = true;
        BaritoneAPI.getSettings().renderGoal.value = false;
        BaritoneAPI.getSettings().allowPlace.value = false;
        BaritoneAPI.getSettings().renderPath.value = false;
        BaritoneAPI.getSettings().assumeSafeWalk.value = false;
        BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(entity.getX(), entity.getY(), entity.getZ()));

    }

}
