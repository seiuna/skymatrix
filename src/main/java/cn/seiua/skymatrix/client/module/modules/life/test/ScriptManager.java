package cn.seiua.skymatrix.client.module.modules.life.test;

import cn.seiua.skymatrix.client.component.Component;
import cn.seiua.skymatrix.client.component.Config;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;

import java.util.LinkedList;

@Event
@Component
@Config(name = "ScriptManager")
public class ScriptManager {
    private static ScriptManager instance;

    ScriptCallBack callBack;
    Script root;
    LinkedList<Script> scripts;

    private boolean finish;
    private boolean pause;

    public ScriptManager() {
        instance = this;
    }
    public static void next() {
    }
    public static boolean isRunning() {
        return instance.root != null && !instance.finish && !instance.pause;
    }
    public static Script setupScript(Script script, ScriptCallBack callBack) {
        instance.root = script;
        instance.scripts = new LinkedList<>();
        instance.callBack = callBack;
        instance.scripts.add(script);
        instance.pause = false;
        instance.finish = false;
        return script;
    }
    public static void pause() {
        instance.pause = true;
    }
    public static void resume() {
        instance.pause = false;
    }
    public static void finish() {
        instance.finish = true;
    }
    public static void clear() {
        instance.root = null;
        instance.scripts = null;
        instance.callBack = null;
        instance.finish = false;
        instance.pause = false;
    }

    @EventTarget
    public void onTick(ClientTickEvent e) {
    }
    @EventTarget
    public void onRender(WorldRenderEvent e) {
    }
}
