package cn.seiua.skymatrix.gui;

import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.Init;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.ColorHolder;

import java.awt.*;

@Event
@Sign(sign = Signs.PRO)
@SModule(name = "theme", category = "client")
public class Theme {
    public static int start = 40;
    private static Theme theme;


    @Value(name = "THEME")
    @Sign(sign = Signs.BETA)
    public ColorHolder THEME = new ColorHolder(new Color(207, 222, 238));
    @Value(name = "THEME_UI")
    @Sign(sign = Signs.BETA)
    public ColorHolder THEME_UI_SELECTED = new ColorHolder(new Color(140, 247, 255));
    @Value(name = "THEME_UI_ERROR")
    @Sign(sign = Signs.BETA)
    public ColorHolder THEME_UI_ERROR = new ColorHolder(new Color(253, 99, 99));
    @Value(name = "UNSELECTED")
    @Sign(sign = Signs.BETA)
    public ColorHolder UNSELECTED = new ColorHolder(new Color(173, 185, 197));
    @Value(name = "SUBBOARD")
    @Sign(sign = Signs.BETA)
    public ColorHolder SUBBOARD = new ColorHolder(new Color(29, 41, 54));
    @Value(name = "LINE")
    @Sign(sign = Signs.BETA)
    public ColorHolder LINE = new ColorHolder(new Color(42, 58, 76));
    @Value(name = "SUBLINE")
    @Sign(sign = Signs.BETA)
    public ColorHolder SUBLINE = new ColorHolder(new Color(36, 48, 61));
    @Value(name = "HOVERANDSELECTED")
    @Sign(sign = Signs.BETA)
    public ColorHolder HOVERANDSELECTED = new ColorHolder(new Color(42, 58, 77));
    @Value(name = "BOARD")
    @Sign(sign = Signs.BETA)
    public ColorHolder BOARD = new ColorHolder(new Color(34, 47, 62));

    public static Theme getInstance() {

        return theme;
    }

    @Init
    public void init() {
        theme = this;
    }

}
