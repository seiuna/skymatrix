package cn.seiua.skymatrix.client.config;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.Init;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.ToggleSwitch;
import cn.seiua.skymatrix.config.option.ValueInput;
import cn.seiua.skymatrix.config.option.ValueSlider;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientSettingEvent;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.gui.Icons;

@Event(register = true)
@Sign(sign = Signs.PRO)
@SModule(name = "setting", category = "client")
public class Setting {
    @Value(name = "title")
    @Sign(sign = Signs.BETA)
    public ValueInput title = new ValueInput("Genshin Impact", Icons.LOCATION);
    @Value(name = "debug")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch debug = new ToggleSwitch(false);
    @Value(name = "lost focus")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch focus = new ToggleSwitch(true);
    @Value(name = "show full name")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch full_name = new ToggleSwitch(true);
    @Value(name = "gui scale")
    @Sign(sign = Signs.BETA)
    public ValueSlider scale = new ValueSlider(1, 0.2, 2, 0.1);

    @Value(name = "simple style")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch simple = new ToggleSwitch(true);

    private static Setting instance;


    @Init
    public void init() {
        instance = this;
    }

    @EventTarget
    public void onSetting(ClientSettingEvent e) {
        switch (e.getType()) {
            case "lostFocus" -> {
                if (this.focus.isValue()) {

                }
            }
        }
    }

    @EventTarget
    public void onTick(ClientTickEvent e) {
        if (this.focus.isValue()) {
            SkyMatrix.mc.options.pauseOnLostFocus = false;
        }

    }

    public static Setting getInstance() {
        return instance;
    }

}
