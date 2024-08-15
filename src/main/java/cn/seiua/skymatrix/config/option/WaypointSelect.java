package cn.seiua.skymatrix.config.option;

import cn.seiua.skymatrix.client.HypixelWay;
import cn.seiua.skymatrix.client.exception.WaypointException;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.client.waypoint.Waypoint;
import cn.seiua.skymatrix.client.waypoint.WaypointGroupEntity;
import cn.seiua.skymatrix.gui.UIComponent;
import cn.seiua.skymatrix.gui.ui.UI;
import cn.seiua.skymatrix.gui.ui.UIWaypoint;
import cn.seiua.skymatrix.utils.OptionInfo;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.HashSet;

public class WaypointSelect implements Serializable, UIComponent {

    @JSONField(alternateNames = "value")
    private String value;

    private String filter;

    public WaypointSelect(String value, String filter) {
        this.value = value;
        this.filter = filter;
    }

    public void next() {

        Waypoint waypoint = Waypoint.getInstance();
        String first = null;
        HashSet<String> strings = new HashSet<>(waypoint.getWaypoints().keySet());
        boolean flag = false;
        for (String name : strings) {
            if (name.startsWith(filter)) {
                if (first == null) {
                    first = name;
                }
                if (flag) {
                    value = name;
                    return;
                }
                if (name.equals(value)) {
                    flag = true;
                }

            }
        }
        if (first != null) {
            value = first;
        }

    }

    public void clear() {

    }

    public String getValue() {
        return value;
    }

    public boolean Invalid() {
        HypixelWay hypixelWay = HypixelWay.getInstance();
        return this.value.startsWith(this.filter) && hypixelWay.isIn(Waypoint.getInstance().getByName(this.value).world);
    }

    @Override
    public UI build(String module, String category, String name, Signs sign) {
        OptionInfo<WaypointSelect> optionInfo = new OptionInfo<>(this, category + "." + name, name, module, category, sign);
        UIWaypoint waypoint = new UIWaypoint(optionInfo);
        return waypoint;
    }
    public WaypointGroupEntity waypointGroup() {
        WaypointGroupEntity entity = Waypoint.getInstance().getByName(this.value);
        if (entity == null) {
            throw new WaypointException(Waypoint.getInstance().getLastMessage());
        }
        return entity;
    }
}
