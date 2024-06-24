package cn.seiua.skymatrix.event.events;

import cn.seiua.skymatrix.event.Event;
import net.minecraft.text.Text;

public class GameMessageEvent extends Event {
    Text raw;
    String text;

    public GameMessageEvent(Text raw) {
        this.raw = raw;
        this.text = raw.getString();
    }

    public Text getRaw() {
        return raw;
    }

    public void setRaw(Text raw) {
        this.raw = raw;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
