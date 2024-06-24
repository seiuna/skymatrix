package cn.seiua.skymatrix.event.events;

public class ServerRotationEvent extends AntiCheckEvent {
    private final String type;

    public ServerRotationEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
