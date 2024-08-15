package cn.seiua.skymatrix.client.waypoint;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.Run;
import cn.seiua.skymatrix.client.RunR;
import net.minecraft.entity.ai.pathing.NavigationType;

import java.util.ArrayList;

public class WPProcess extends Thread {

    private boolean circle;
    private WaypointGroupEntity groupEntity;
    private int index = 0;

    public WaypointGroupEntity getGroupEntity() {
        return groupEntity;
    }

    private int count = -1;
    private boolean over = false;

    private Run run1;
    private RunR<Boolean> next;

    public WPProcess(boolean circle, WaypointGroupEntity groupEntity) {
        this.circle = circle;
        this.groupEntity = groupEntity;
        if (circle) {
            count = 0;
        }
    }

    public boolean isPause() {
        return pause;
    }

    private boolean pause;

    public void setNextCircle(Run run) {

        this.run1 = run;
    }

    public void setNext(RunR<Boolean> run) {
        this.next = run;
    }

    public void pause() {

        pause = true;
    }

    public void resume1() {
        pause = false;
    }

    public void startP() {
        if (SkyMatrix.mc.world == null) return;
        reset();

        pause = false;

    }

    private void reset() {
        over = false;


    }

    public void stopP() {

        index = -1;
        over = true;
        BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
    }

    public float getCDegree() {
        return (float) (getCurrentIndex() - 1) / getTargetIndex();
    }

    public int getCurrentIndex() {
        return index;
    }

    public int getTargetIndex() {
        return groupEntity.getWaypoints().size();
    }

    public int getCount() {
        return count;
    }

    public boolean isOver() {
        return over;
    }

    boolean flag = true;
    private boolean pf;

    private boolean r() {
        if (over || SkyMatrix.mc.world == null) {
            return false;
        }
        if (this.pause) {
            if (BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().isActive() && !pf) {
                BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
                pf = true;
            }

            return true;
        }
        if (!BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().isActive() && flag) {
            ArrayList<WaypointEntity> wps = this.groupEntity.getWaypoints();
            if (pause) return true;
            if (wps.size() - 1 < index) {
                index = 0;
                if (!circle) {

                    return true;
                } else {
                    count++;
                    if (run1 != null) run1.run();
                }
                return true;

            }

            if (next != null) {
                if (index > 0) {
                    if (!next.run(this.groupEntity.getWaypoints().get((index - 1) % this.groupEntity.getWaypoints().size()))) {

                        return true;
                    }
                }

            }

            WaypointEntity wp = wps.get(index);
            BaritoneAPI.getSettings().allowSprint.value = true;
            BaritoneAPI.getSettings().avoidance.value = false;
            BaritoneAPI.getSettings().allowWalkOnBottomSlab.value = true;
            BaritoneAPI.getSettings().antiCheatCompatibility.value = true;
            BaritoneAPI.getSettings().allowParkour.value = true;
            BaritoneAPI.getSettings().allowBreak.value = false;
            BaritoneAPI.getSettings().allowParkourAscend.value = true;
            BaritoneAPI.getSettings().renderGoal.value = false;
            BaritoneAPI.getSettings().renderPath.value = false;
            BaritoneAPI.getSettings().freeLook.value = true;
            BaritoneAPI.getSettings().assumeSafeWalk.value = false;
            BaritoneAPI.getSettings().allowPlace.value = false;
            int t = wp.getY();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (!SkyMatrix.mc.world.getBlockState(wp.toBlockPos()).canPathfindThrough( NavigationType.AIR)) {
                t++;
            }
            pf = false;
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(wp.getX(), t, wp.getZ()));
            index++;

            flag = false;

        } else {

            flag = true;
        }
        return true;
    }

    @Override
    public void run() {
        while (true) {

            if (!this.r()) {
                BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
                return;
            }
        }

    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
