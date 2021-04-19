package com.obamabob.runite.module.modules.misc;

import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import com.obamabob.runite.util.Mapping;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;

public class Timer extends Module {
    public Timer() {super("Timer", Category.MISCELLANEOUS);}

    Setting<Double> timer = register(new Setting<>("Speed", this, 2.0, 0.1, 50.0));

    double timerOld = timer.getValue();

    public void onEnable() {setTimer(timer.getValue().floatValue());}

    public void onTick() {
        if (timer.getValue() != timerOld) {
            setTimer(timer.getValue().floatValue());
            timerOld = timer.getValue();
        }
    }

    public void onDisable() {setTimer(1.0f);}

    private void setTimer(final float value) {
        try {
            final Field timer = Minecraft.class.getDeclaredField(Mapping.timer);
            timer.setAccessible(true);
            final Field tickLength = net.minecraft.util.Timer.class.getDeclaredField(Mapping.tickLength);
            tickLength.setAccessible(true);
            tickLength.setFloat(timer.get(mc), 50.0f / value);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}