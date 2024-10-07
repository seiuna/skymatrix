package cn.seiua.skymatrix.event.events;

import cn.seiua.skymatrix.event.Event;
import net.minecraft.client.gui.DrawContext;

public class HudRenderEvent extends Event {

    private DrawContext drawContext;

    private float tickDelta;

    public static HudRenderEvent instance = new HudRenderEvent(null, 0);

    public HudRenderEvent(DrawContext drawContext, float tickDelta) {
        this.drawContext = drawContext;
        this.tickDelta = tickDelta;
    }

    public DrawContext getContext() {
        return drawContext;
    }

    public void setContext(DrawContext drawContext) {
        this.drawContext = drawContext;
    }

    public float getTickDelta() {
        return tickDelta;
    }

    public void setTickDelta(float tickDelta) {
        this.tickDelta = tickDelta;
    }
}
