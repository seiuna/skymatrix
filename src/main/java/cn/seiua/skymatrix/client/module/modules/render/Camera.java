package cn.seiua.skymatrix.client.module.modules.render;

import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.config.Hide;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.KeyBind;
import cn.seiua.skymatrix.config.option.MultipleChoice;
import cn.seiua.skymatrix.config.option.ToggleSwitch;
import cn.seiua.skymatrix.config.option.ValueSlider;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ViewClipEvent;
import cn.seiua.skymatrix.gui.Icons;
import cn.seiua.skymatrix.utils.ReflectUtils;

import java.util.Arrays;
import java.util.Map;

@Event
@Sign(sign = Signs.FREE)
@SModule(name = "camera", category = "render")
public class Camera {
    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(Arrays.asList(), ReflectUtils.getModuleName(this));
    @Value(name = "selected")
    @Sign(sign = Signs.FREE)
    public MultipleChoice selected = new MultipleChoice(Map.of("noclip", false), Icons.MODE);
    @Value(name = "modify clip")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch modify = new ToggleSwitch(false);

    @Value(name = "clip distance")
    @Hide(following = "modify clip")
    @Sign(sign = Signs.BETA)
    public ValueSlider distance = new ValueSlider(3d, 3d, 100d, 0.1d);
    private double tempDis;

    @EventTarget
    public void viewClipEvent(ViewClipEvent event) {

        if (modify.isValue() && event.getType().equals("HEAD")) {
            tempDis = distance.getValue().doubleValue();
            event.setDis(tempDis);
        } else if (!event.getType().equals("RETURN")) {
            tempDis = event.getDis();
        }
        if (this.selected.getValue().get("noclip") && event.getType().equals("RETURN")) {
            event.setDis(tempDis);
        }

    }


}
