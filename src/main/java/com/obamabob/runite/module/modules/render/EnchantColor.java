package com.obamabob.runite.module.modules.render;

import com.obamabob.runite.Runite;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class EnchantColor extends Module {

    private final Setting<String> mode = register(new Setting<>("Mode", this, "Color", new String[]{"Color", "Rainbow"}));
    private final Setting<Integer> red = register(new Setting<>("Red", this, 255, 0, 255));
    private final Setting<Integer> green = register(new Setting<>("Green", this, 255, 0, 255));
    private final Setting<Integer> blue = register(new Setting<>("Blue", this, 255, 0, 255));

    public  EnchantColor() {
        super("EnchantColor", Category.RENDER);
    }

    public static Color getColor(long offset, float fade){
        if (Runite.getInstance().moduleManager.getModuleT(EnchantColor.class).mode.getValue().equalsIgnoreCase("Color")) {
            return new Color(Runite.getInstance().moduleManager.getModuleT(EnchantColor.class).red.getValue(), Runite.getInstance().moduleManager.getModuleT(EnchantColor.class).green.getValue(), Runite.getInstance().moduleManager.getModuleT(EnchantColor.class).blue.getValue());
        }
        float hue = (float) (System.nanoTime() + offset) / 1.0E10F % 1.0F;
        long color = Long.parseLong(Integer.toHexString(Integer.valueOf(Color.HSBtoRGB(hue, 1.0F, 1.0F)).intValue()), 16);
        Color c = new Color((int) color);
        return new Color(c.getRed()/255.0F*fade, c.getGreen()/255.0F*fade, c.getBlue()/255.0F*fade, c.getAlpha()/255.0F);
    }
}