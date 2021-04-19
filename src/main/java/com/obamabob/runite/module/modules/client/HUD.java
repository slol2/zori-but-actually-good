package com.obamabob.runite.module.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.obamabob.runite.Runite;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.module.ModuleManager;
import com.obamabob.runite.settings.Setting;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Calendar;

public class HUD extends Module {
    public HUD() {super("HUD", Category.CLIENT);MinecraftForge.EVENT_BUS.register(this);}

    Setting<Boolean> watermark = register(new Setting<>("Watermark", this, true));
    Setting<Boolean> greeter = register(new Setting<>("Greeter",this, true));
    Setting<Boolean> arrayList = register(new Setting<>("ArrayList",this, true));
    Setting<Boolean> coords = register(new Setting<>("Coordinates",this, true));
    Setting<Integer> r = register(new Setting<>("Red", this, 255, 0, 255));
    Setting<Integer> g = register(new Setting<>("Green", this, 0, 0, 255));
    Setting<Integer> b = register(new Setting<>("Blue", this, 0, 0, 255));
    Setting<Boolean> rainbow = register(new Setting<>("Rainbow", this, true));

    public void onEnable(){disable();}

    public int rgb;
    public int y;

    public int updateRainbow(int IN) {
        if (!rainbow.getValue()) return IN;
        float hue2 = Color.RGBtoHSB(new Color(IN).getRed(), new Color(IN).getGreen(), new Color(IN).getBlue(), null)[0];
        hue2 += 0.02;
        if (hue2 > 1) hue2 -= 1;
        return Color.HSBtoRGB(hue2, 1, 1);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;
        rgb = (rainbow.getValue() ? Runite.rgb : new Color(r.getValue(), g.getValue(), b.getValue()).getRGB());
        y=2;
        if (watermark.getValue()) {
            mc.fontRenderer.drawStringWithShadow("Runite 1.2", 2, 2, rgb);
            y += 10;
        }
        if (greeter.getValue()) {
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if (hour >= 0 && hour < 12) mc.fontRenderer.drawStringWithShadow("Good morning " + mc.player.getName() + " :^)", (new ScaledResolution(mc).getScaledWidth()/2)-(mc.fontRenderer.getStringWidth("Good morning " + mc.player.getName() + ":^)")/2),2, rgb);
            else if (hour >= 12 && hour < 16) mc.fontRenderer.drawStringWithShadow("Good afternoon " + mc.player.getName() + " :^)", (new ScaledResolution(mc).getScaledWidth()/2)-(mc.fontRenderer.getStringWidth("Good afternoon " + mc.player.getName() + ":^)")/2),2, rgb);
            else if (hour >= 16 && hour < 24) mc.fontRenderer.drawStringWithShadow("Good evening " + mc.player.getName() + " :^)", (new ScaledResolution(mc).getScaledWidth()/2)-(mc.fontRenderer.getStringWidth("Good evening " + mc.player.getName() + ":^)")/2),2, rgb);
        }
        if (arrayList.getValue()) {
            ModuleManager.modules.stream().filter(Module::isToggled).filter(Module::isDrawn).forEach(module -> {
                rgb = updateRainbow(rgb);
                mc.fontRenderer.drawStringWithShadow(module.getName() + " " + module.getHudInfo(), 2, y, rgb);
                y+=10;
            });
        }
        if (coords.getValue()) {
            rgb = updateRainbow(rgb);
            if (mc.player == null) return;
            DecimalFormat format = new DecimalFormat("0.#");
            if (mc.player.dimension==-1) mc.fontRenderer.drawStringWithShadow(format.format(mc.player.posX) + ChatFormatting.WHITE + ", " + ChatFormatting.RESET + format.format(mc.player.posY) + ChatFormatting.WHITE + ", " + ChatFormatting.RESET + format.format(mc.player.posZ) + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + format.format(mc.player.posX/8) + ChatFormatting.WHITE + ", " + ChatFormatting.RESET + format.format(mc.player.posY) + ChatFormatting.WHITE + ", " + ChatFormatting.RESET + format.format(mc.player.posZ/8) + ChatFormatting.WHITE + "]", 2, new ScaledResolution(mc).getScaledHeight()-mc.fontRenderer.FONT_HEIGHT-2, rgb);
            else if (mc.player.dimension==0) mc.fontRenderer.drawStringWithShadow(format.format(mc.player.posX) + ChatFormatting.WHITE + ", " + ChatFormatting.RESET + format.format(mc.player.posY) + ChatFormatting.WHITE + ", " + ChatFormatting.RESET + format.format(mc.player.posZ) + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + format.format(mc.player.posX*8) + ChatFormatting.WHITE + ", " + ChatFormatting.RESET + format.format(mc.player.posY) + ChatFormatting.WHITE + ", " + ChatFormatting.RESET + format.format(mc.player.posZ*8) + ChatFormatting.WHITE + "]", 2, new ScaledResolution(mc).getScaledHeight()-mc.fontRenderer.FONT_HEIGHT-2, rgb);
            else mc.fontRenderer.drawStringWithShadow(format.format(mc.player.posX) + ChatFormatting.WHITE + ", " + ChatFormatting.RESET + format.format(mc.player.posY) + ChatFormatting.WHITE + ", " + ChatFormatting.RESET + format.format(mc.player.posZ), 2, new ScaledResolution(mc).getScaledHeight()-mc.fontRenderer.FONT_HEIGHT-2, rgb);
        }
    }
}
