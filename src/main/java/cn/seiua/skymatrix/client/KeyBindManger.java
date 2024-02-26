package cn.seiua.skymatrix.client;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.component.Component;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.Init;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.config.option.KeyBind;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.KeyboardEvent;
import cn.seiua.skymatrix.event.events.MouseEvent;
import cn.seiua.skymatrix.message.Message;
import cn.seiua.skymatrix.message.MessageBuilder;
import com.google.common.collect.EvictingQueue;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Event(register = true)
public class KeyBindManger /*implements Runnable*/ {

    @Use
    private ConfigManager configManager;
    private static final Logger logger = LoggerFactory.getLogger("KeyBindManger");
    private List<KeyBind> keyBinds;

    private Message message = MessageBuilder.build("keybinding");

    public static final int MOUSE = 10000;

    private final EvictingQueue<Integer> evictingQueue = EvictingQueue.create(3);
    public static final HashMap<Integer, String> KEY_MAP = new HashMap<Integer, String>() {{
        put(256, "ESCAPE");
        put(257, "ENTER");
        put(258, "TAB");
        put(259, "BACKSPACE");
        put(260, "INSERT");
        put(261, "DELETE");
        put(262, "RIGHT");
        put(263, "LEFT");
        put(264, "DOWN");
        put(265, "UP");
        put(266, "PAGE UP");
        put(267, "PAGE DOWN");
        put(268, "HOME");
        put(269, "END");
        put(280, "CAPS LOCK");
        put(281, "SCROLL LOCK");
        put(282, "NUM LOCK");
        put(283, "PRINT SCREEN");
        put(284, "PAUSE");
        put(290, "F1");
        put(291, "F2");
        put(292, "F3");
        put(293, "F4");
        put(294, "F5");
        put(295, "F6");
        put(296, "F7");
        put(297, "F8");
        put(298, "F9");
        put(299, "F10");
        put(300, "F11");
        put(301, "F12");
        put(302, "F13");
        put(303, "F14");
        put(304, "F15");
        put(305, "F16");
        put(306, "F17");
        put(307, "F18");
        put(308, "F19");
        put(309, "F20");
        put(310, "F21");
        put(311, "F22");
        put(312, "F23");
        put(313, "F24");
        put(314, "F25");
        put(320, "KP 0");
        put(321, "KP 1");
        put(322, "KP 2");
        put(323, "KP 3");
        put(324, "KP 4");
        put(325, "KP 5");
        put(326, "KP 6");
        put(327, "KP 7");
        put(328, "KP 8");
        put(329, "KP 9");
        put(330, "KP DECIMAL");
        put(331, "KP DIVIDE");
        put(332, "KP MULTIPLY");
        put(333, "KP SUBTRACT");
        put(334, "KP ADD");
        put(335, "KP ENTER");
        put(336, "KP EQUAL");
        put(340, "LEFT SHIFT");
        put(341, "LEFT CONTROL");
        put(342, "LEFT ALT");
        put(343, "LEFT SUPER");
        put(344, "RIGHT SHIFT");
        put(345, "RIGHT CONTROL");
        put(346, "RIGHT ALT");
        put(347, "RIGHT SUPER");
        put(348, "MENU");
    }};
    // key KeyBind
    // key key KeyBind
    // key KeyBind

    private int key0 = -1;
    private int key1 = -1;
    private int key2 = -1;

    public static String getKeyName(int key) {
        if (key >= MOUSE) {
            return "M" + (key - MOUSE);
        }
        String keyName = GLFW.glfwGetKeyName(key, GLFW.glfwGetKeyScancode(key));
        if (keyName == null) {
            keyName = KEY_MAP.get(key);
        }
        return keyName;
    }

    @Init(level = 999999999)
    public void handle() {
        configManager.addReloadCallbacks(this::update);
        update();
    }

    public void update() {
        keyBinds = configManager.getConfigsByClass(KeyBind.class);
    }

    @EventTarget
    public void keyboard(KeyboardEvent e) {

        if (e.getAction() == 1) {
            evictingQueue.add(e.getKeyCode());
            matchKey();
        }
        if (e.getAction() == 0) {
            evictingQueue.remove(e.getKeyCode());
        }

    }

    @EventTarget
    public void mouse(MouseEvent e) {
        if (e.getAction() == 1) {
            evictingQueue.add(e.getButton() + MOUSE);
            matchKey();
        }
        if (e.getAction() == 0) {
            evictingQueue.remove(e.getButton() + MOUSE);
        }

    }

    private void printKeyNames() {

    }

    public boolean isSubset(List<Integer> givenList, List<Integer> parentList) {
        if (givenList.size() == 0 || parentList.size() == 0) return false;
        if (givenList.size() > parentList.size()) {
            return false;
        }
        int startIndex = parentList.indexOf(givenList.get(0));

        if (startIndex == -1) {
            return false;
        } else if (givenList.size() == 1) {
            return true;
        }
        if (startIndex >= givenList.size() - 1) return false;
        int j = 0;
        for (int i = startIndex; i < parentList.size(); i++) {
            if (!Objects.equals(parentList.get(i), givenList.get(j))) {

                return false;
            }
            j++;
            if (j == givenList.size()) break;
        }

        return true;
    }

    public void matchKey() {
        if (SkyMatrix.mc.currentScreen != null) return;
        List<Integer> keys = this.evictingQueue.stream().toList();
        List<KeyBind> keyBinds = new CopyOnWriteArrayList<>(this.keyBinds.stream().toList());
//        message.sendDebugMessage(Text.of(keys.toString()));
        boolean flag = false;
        for (KeyBind k : keyBinds) {
            if (k.getKeys().size() == 0) continue;
//            message.sendDebugMessage(Text.of(k.getKeys().toString() + " 5"));
            if (isSubset(k.getKeys(), keys)) {
                flag = true;
                k.getRun().run();
            }
        }

        if (flag) {
            this.evictingQueue.clear();
        }


    }


}
