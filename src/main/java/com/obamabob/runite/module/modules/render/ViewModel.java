package com.obamabob.runite.module.modules.render;

import com.obamabob.runite.event.EventTarget;
import com.obamabob.runite.event.events.EventTransformSideFirstPerson;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ViewModel extends Module {
    public ViewModel() {super("ViewModel", Category.RENDER);}

    Setting<String> mode = register(new Setting<>("Mode", this, "ESP", new String[]{"Value", "FOV", "Both"}));
    public Setting<Boolean> cancelEating = register(new Setting<>("Cancel Eating", this, false));
    Setting<Double> xLeft = register(new Setting<>("Left X", this, 0.0, -2.0, 2.0));
    Setting<Double> yLeft = register(new Setting<>("Left Y", this, 0.0, -2.0, 2.0));
    Setting<Double> zLeft = register(new Setting<>("Left Z", this, 0.0, -2.0, 2.0));
    Setting<Double> xRight = register(new Setting<>("Right X", this, 0.0, -2.0, 2.0));
    Setting<Double> yRight = register(new Setting<>("Right Y", this, 0.0, -2.0, 2.0));
    Setting<Double> zRight = register(new Setting<>("Right Z", this, 0.0, -2.0, 2.0));
    Setting<Integer> fov = register(new Setting<>("FOV", this, 130, 70, 200));

    @EventTarget
    public void listener(EventTransformSideFirstPerson event) {
        if (mode.getValue().equalsIgnoreCase("value") || mode.getValue().equalsIgnoreCase("both")) {
            if (event.getEnumHandSide() == EnumHandSide.RIGHT) GlStateManager.translate(xRight.getValue(), yRight.getValue(), zRight.getValue());
            else if (event.getEnumHandSide() == EnumHandSide.LEFT) GlStateManager.translate(xLeft.getValue(), yLeft.getValue(), zLeft.getValue());
        }
    }

    @SubscribeEvent
    public void onFov(EntityViewRenderEvent.FOVModifier event) {
        if (mode.getValue().equalsIgnoreCase("FOV") || mode.getValue().equalsIgnoreCase("Both")) {
            event.setFOV((float) fov.getValue());
        }
    }

    public void onEnable() {MinecraftForge.EVENT_BUS.register(this);}
    public void onDisable() {MinecraftForge.EVENT_BUS.unregister(this);}
}