package com.obamabob.runite.util;

import java.awt.*;

public class RainbowUtil {

    private static int rgb;
    public static int a;
    public static int r;
    public static int g;
    public static int b;
    static float hue = 0.01f;

    public static void updateRainbow() {
        rgb = Color.HSBtoRGB(hue, 1, 1);
        a = (rgb >>> 24) & 0xFF;
        r = (rgb >>> 16) & 0xFF;
        g = (rgb >>> 8) & 0xFF;
        b = rgb & 0xFF;
        hue += 5 / 1000f;
        if (hue > 1) hue -= 1;
    }
}