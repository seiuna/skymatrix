package cn.seiua.skymatrix.client;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.component.*;
import cn.seiua.skymatrix.client.config.Setting;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.HandleKeyInputBeforeEvent;
import com.google.common.collect.EvictingQueue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

@Component
@Event(register = true)
@Category(name = "client")
public final class Client {

    private static final Logger logger = LogManager.getLogger();
    public static Client instance;
    @Use
    private ConfigManager configManager;
    @Use
    private EventManager eventManager;
    @Use
    private ConnectManager connectManager;
    @Use
    private Setting setting;
    public int stage;
    public static File root = new File(MinecraftClient.getInstance().runDirectory, "skymartix");

    private static final Queue<Text> priorityQueue = EvictingQueue.create(40);

    @Init(level = 999)
    public void start() {
        root.mkdirs();
        instance = this;
    }

    private boolean keepBlockBreaking;

    public boolean isKeepBlockBreaking() {
        return keepBlockBreaking;
    }

    public void setKeepBlockBreaking(boolean keepBlockBreaking) {
        this.keepBlockBreaking = keepBlockBreaking;
        if (!keepBlockBreaking) {

        }
    }

    @EventTarget
    private void onHandleKeyInputBeforeEvent(HandleKeyInputBeforeEvent event) {
        if (this.keepBlockBreaking && SkyMatrix.mc.currentScreen != null) {
            SkyMatrix.mc.attackCooldown = 0;
            SkyMatrix.mc.handleBlockBreaking(true);
        }

    }
    private boolean flag;

    @EventTarget
    public void ClientTickEvent(ClientTickEvent e) {

        if (!Objects.equals(setting.title.getValue(), "")) {
            SkyMatrix.mc.getWindow().setTitle(setting.title.getValue());
        }

        if (MinecraftClient.getInstance().world != null) {
            updataGuiScreen();
        }

        Text text = priorityQueue.poll();
        if (text != null) {

        }
    }

    @Use
    public List<Screen> guiScreens;

    private Screen targetGui;

    public void openGui(Class gui) {
        for (Screen guiScreen : guiScreens) {
            if (guiScreen.getClass() == gui) {
                logger.debug("display guiscreen: " + guiScreen);
                targetGui = guiScreen;
                return;
            }
        }
    }

    private void updataGuiScreen() {
        if (targetGui != null) {
            MinecraftClient.getInstance().setScreen(targetGui);
            targetGui = null;
        }
    }

    public static void sendMessage(Text message) {
        assert SkyMatrix.mc.player != null;
        SkyMatrix.mc.player.sendMessage(Text.of("§8[§9S§9k§9y§9M§9a§9t§9r§9i§9x§8]").copy().append(message));

    }

    public static void sendDebugMessage(Text message) {
        if (!Setting.getInstance().debug.isValue()) return;

        SkyMatrix.mc.player.sendMessage(Text.of("§3[§bDebug§3]§7: §r").copy().append(message));

    }

    public static void sendSimpleMessage(Text message) {
        assert SkyMatrix.mc.player != null;

        SkyMatrix.mc.player.sendMessage(Text.of("§c[").copy().append(message).append(Text.of("§c]")));
    }

}
