package com.obamabob.runite.module.modules.render;

import com.obamabob.runite.Runite;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class SkyColour extends Module {
    public SkyColour() {
        super("SkyColour", Category.RENDER);
    }

    Setting<Integer> red = register(new Setting<>("Red", this, 255, 0 , 255));
    Setting<Integer> green = register(new Setting<>("Green", this, 0, 0 , 255));
    Setting<Integer> blue = register(new Setting<>("Blue", this, 0, 0 , 255));
    Setting<Boolean> rainbow = register(new Setting<>("Rainbow", this, true));

    @SubscribeEvent
    public void fogDolour(final EntityViewRenderEvent.FogColors event) {
        event.setRed((rainbow.getValue() ? new Color(Runite.rgb).getRed() : red.getValue()) / 255f);
        event.setGreen((rainbow.getValue() ? new Color(Runite.rgb).getGreen() :  green.getValue()) / 255f);
        event.setBlue((rainbow.getValue() ? new Color(Runite.rgb).getBlue() :  blue.getValue()) / 255f);
    }

    @SubscribeEvent
    public void fogDensity(final EntityViewRenderEvent.FogDensity event) {
        event.setDensity(0.0f);
        event.setCanceled(true);
    }

    @Override
    public void onEnable() {MinecraftForge.EVENT_BUS.register(this);}

    @Override
    public void onDisable() {MinecraftForge.EVENT_BUS.unregister(this);}
}