package cn.seiua.skymatrix.client;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.component.Component;
import cn.seiua.skymatrix.client.component.Event;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Text;

@Component
@Event(register = true)
public class ChatOverride {
    private static ChatOverride instance;
    private Text override;

    public ChatOverride() {
        instance = this;
    }

    public static ChatOverride getInstance() {
        return instance;
    }

    public void setOverride(Text text) {
        Text statt = Text.of("§9§lSkymatrix §r§8§k>> §b§r");
        override = statt.copyContentOnly().append(text);
    }

    public void setOverride(String text) {
        setOverride(Text.of(text));
    }

    public void clearOverride() {
        override = null;
    }

    public void onRenderPre() {
        if (override == null) return;
        SkyMatrix.mc.inGameHud.getChatHud().visibleMessages.addFirst(new ChatHudLine.Visible(200000, override.asOrderedText(), MessageIndicator.system(), false));
    }

    public void onRenderPost() {
        if (override == null) return;
        SkyMatrix.mc.inGameHud.getChatHud().visibleMessages.removeFirst();

    }
}
