package com.obamabob.runite.util;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.Display;

public class TitleUtil{
    int ticks = 0;
    int bruh = 0;
    int breakTimer = 0;
    String bruh1 = "Runite 1.2";
    boolean qwerty = false;
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        ++ticks;
        if (ticks % 10 == 0)
        {
            Display.setTitle((bruh1.substring(0, bruh1.length()-bruh)));
            if ((bruh == bruh1.length() && breakTimer != 2) || (bruh == 0 && breakTimer != 4)) {
                breakTimer++;
                return;
            } else breakTimer = 0;
            if (bruh == bruh1.length()) qwerty = true;
            if (qwerty) --bruh;
            else ++bruh;
            if (bruh == 0) qwerty = false;
        }
    }
}