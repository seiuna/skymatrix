package cn.seiua.skymatrix.event.events;

import cn.seiua.skymatrix.event.Event;

public class ViewClipEvent extends Event {
    private double dis;
    private String type;

    public ViewClipEvent(double dis, String type) {
        this.type = type;
        this.dis = dis;
    }

    public float getDis() {
        return (float) dis;
    }

    public void setDis(double dis) {
        this.dis = dis;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
