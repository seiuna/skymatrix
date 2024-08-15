package cn.seiua.skymatrix.font;

import cn.seiua.skymatrix.SkyMatrix;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.Optional;

public class FontUtils {


    public static FontRenderer getFontRenderer(String fontname, int fontstyle, int size) {
        Identifier identifier = Identifier.of(SkyMatrix.MODID, "font/" + fontname);
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getResourceManager() != null) {
            Font font = null;
            try {
                Optional optional = mc.getResourceManager().getResource(identifier);
                if (optional.isPresent()) {
                    font = Font.createFont(Font.TRUETYPE_FONT, mc.getResourceManager().getResource(identifier).get().getInputStream());
                }
                font = font.deriveFont(fontstyle, size);
                return new FontRenderer(font);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else {
            throw new NullPointerException("");
        }

    }


}
