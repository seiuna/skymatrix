package cn.seiua.skymatrix.utils;

import cn.seiua.skymatrix.client.Run;

import java.util.function.IntSupplier;

public class OneTickTimer implements TickTimer {

    private int tick;

    IntSupplier targetTick;
    private Run callBack;

    OneTickTimer(int tick, Run callBack) {
        this.tick = tick;
        this.targetTick = () -> tick;
        this.callBack = callBack;
    }

    OneTickTimer(IntSupplier intSupplier, Run callBack) {
        this.tick = tick;
        this.targetTick = intSupplier;
        this.callBack = callBack;
    }

    @Override
    public void reset() {
        tick = targetTick.getAsInt();
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public void update() {
        if (tick == 0) {
            if (callBack != null)
                callBack.run();
        }
        tick--;

    }
}
