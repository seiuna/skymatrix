package cn.seiua.skymatrix.config.option;

import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.gui.UIComponent;
import cn.seiua.skymatrix.gui.ui.UI;
import cn.seiua.skymatrix.gui.ui.UIDoubleSlider;
import cn.seiua.skymatrix.utils.OptionInfo;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Objects;
import java.util.Random;

public class DoubleValueSlider implements UIComponent {

    private static Random random = new Random();
    @JSONField(alternateNames = "value", serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
    private Number value;
    @JSONField(alternateNames = "valua", serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
    private Number valua;
    private transient Number min;
    private transient Number max;
    private transient Number interval;

    public DoubleValueSlider(Number value, Number valua, Number min, Number max, Number interval) {
        this.value = value;
        this.min = min;
        this.max = max;
        this.interval = interval;
        this.valua = valua;
    }

    public String toValueString(Number v) {
        return String.format("%.1f", v);
    }

    public double getRandomValue() {
        double v = Math.min(value.doubleValue(), value.doubleValue()) + (random.nextInt() % ((Math.max(value.doubleValue(), valua.doubleValue()) - Math.min(value.doubleValue(), valua.doubleValue()))));
        if (Objects.equals(value, valua)) v = valua.doubleValue();
        if (Double.isNaN(v)) {
            return this.value.doubleValue();
        }
        return Math.min(v, this.maxValue().doubleValue());
    }

    public Number getValua() {
        return valua;
    }

    public void setValua(Number valua) {
        this.valua = valua;
    }

    public Number getValue() {
        return value;
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


    public Number maxValue() {


        return Math.max(this.valua.doubleValue(), (double) this.value.doubleValue());
    }

    public Number minValue() {
        return Math.min((double) this.valua.doubleValue(), (double) this.value.doubleValue());
    }

    @Override
    public UI build(String module, String category, String name, Signs sign) {

        OptionInfo<DoubleValueSlider> optionInfo = new OptionInfo<>(this, category + "." + name, name, module, category, sign);
        UIDoubleSlider slider = new UIDoubleSlider(optionInfo);
        return slider;
    }

}
