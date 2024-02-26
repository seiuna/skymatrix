package cn.seiua.skymatrix.config.option;

import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.gui.UIComponent;
import cn.seiua.skymatrix.gui.ui.UI;
import cn.seiua.skymatrix.gui.ui.UISlider;
import cn.seiua.skymatrix.utils.OptionInfo;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;


public class ValueSlider implements Serializable, UIComponent {
    @JSONField(alternateNames = "value")
    private Number value;
    private transient Number min;
    private transient Number max;
    private transient Number interval;

    public ValueSlider(Number value, Number min, Number max, Number interval) {
        this.value = value;
        this.min = min;
        this.max = max;
        this.interval = interval;
    }

    public Number getValue() {
        return value;
    }

    public int getIntValue() {
        return value.intValue();
    }

    public void setValue(Number value) {

        this.value = value;
    }

    public Number getMin() {
        return min;
    }

    public void setMin(Number min) {
        this.min = min;
    }

    public Number getMax() {
        return max;
    }

    public void setMax(Number max) {
        this.max = max;
    }

    public Number getInterval() {
        return interval;
    }

    public void setInterval(Number interval) {
        this.interval = interval;
    }

    public String toValueString() {
        return value.toString();
    }


    @Override
    public UI build(String module, String category, String name, Signs sign) {

        OptionInfo<ValueSlider> optionInfo = new OptionInfo<>(this, category + "." + name, name, module, category, sign);
        UISlider slider = new UISlider(optionInfo);
        return slider;
    }


}
