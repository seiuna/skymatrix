package cn.seiua.skymatrix.utils;

import cn.seiua.skymatrix.client.Run;

import java.util.function.IntSupplier;

public interface TickTimer {
    void update();

    void reset();

    static OneTickTimer build(int tick, Run callback) {
        return new OneTickTimer(tick, callback);
    }

    static OneTickTimer build(IntSupplier intSupplier, Run callback) {
        return new OneTickTimer(intSupplier, callback);
    }

    static OneTickTimer build(IntSupplier intSupplier, int st, Run callback) {
        OneTickTimer tickTimer = build(intSupplier, callback);
        tickTimer.setTick(st);
        return tickTimer;
    }

    static OneTickTimer build(int tick, int st, Run callback) {
        OneTickTimer tickTimer = build(tick, callback);
        tickTimer.setTick(st);
        return tickTimer;
    }

    /**
     * @param tick
     * @param callback
     * @param times    -1 表示无
     * @return
     */
    static CycleTickTimer build(int tick, Run callback, int times) {
        return new CycleTickTimer(tick, callback, times);
    }

}
