package cn.seiua.skymatrix.event.events;

import cn.seiua.skymatrix.event.Event;

public class AntiCheckEvent extends Event {
    private final String type;

    public AntiCheckEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
