package cn.seiua.skymatrix.event.events;

import net.minecraft.entity.player.PlayerEntity;

public class LookLockEvent extends AntiCheckEvent {

    private final long time;
    private final PlayerEntity e;
    private final boolean throwBlock;
    private final boolean free;

    public LookLockEvent(PlayerEntity e, long time, boolean throwBlock, boolean free) {
        this.throwBlock = throwBlock;
        this.e = e;
        this.time = time;
        this.free = free;
    }

    public boolean isFree() {
        return free;
    }

    public long getTime() {
        return time;
    }

    public PlayerEntity getPlayer() {
        return e;
    }

    public boolean isThrowBlock() {
        return throwBlock;
    }
}
