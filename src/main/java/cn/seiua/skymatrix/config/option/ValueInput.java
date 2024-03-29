package cn.seiua.skymatrix.config.option;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.gui.Icons;
import cn.seiua.skymatrix.gui.UIComponent;
import cn.seiua.skymatrix.gui.ui.UI;
import cn.seiua.skymatrix.gui.ui.UIValueInput;
import cn.seiua.skymatrix.utils.OptionInfo;
import com.alibaba.fastjson.annotation.JSONField;

public class ValueInput implements UIComponent {

    @JSONField(alternateNames = "value")
    private String value;

    public static String COMMAND = "Cmd";
    public static String BLOCK = "Block";
    public static String ITEM = "Item";
    public transient String pre = "";
    private transient String type;
    private transient String suggestion;

    public void execute() {
        SkyMatrix.mc.getNetworkHandler().sendCommand(value);
    }

    public ValueInput(String value, String type) {
        if (type == Icons.CMD) {
            pre = "/";
        }
        this.value = value;
        this.type = type;
    }

    public ValueInput(String value, String type, String suggestion) {
        this(value, type);
        this.suggestion = suggestion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    @Override
    public UI build(String module, String category, String name, Signs sign) {
        OptionInfo<ValueInput> optionInfo = new OptionInfo<>(this, category + "." + name, name, module, category, sign);
        UIValueInput valueInput = new UIValueInput(optionInfo);
        return valueInput;
    }
}
