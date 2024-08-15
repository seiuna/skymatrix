package cn.seiua.skymatrix.render;

import java.awt.*;

public class CustomColor implements GetColor{

    private Color color;
    public CustomColor(int r, int g, int b, int a) {
        color=new Color(r,g,b,a);
    }
    public CustomColor(Color color) {
        color=color;
    }

    @Override
    public Color getColor() {
        return color;
    }
}
