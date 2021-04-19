package com.obamabob.runite.event;

import com.obamabob.runite.Runite;
import com.obamabob.runite.command.Command;
import com.obamabob.runite.event.events.EventRender;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.module.ModuleManager;
import com.obamabob.runite.util.EntityUtil;
import com.obamabob.runite.util.RainbowUtil;
import com.obamabob.runite.util.RuniteTessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class EventHandler {
    public EventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        ModuleManager.modules.stream().filter(Module::isToggled).forEach(Module::onTick);
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        Minecraft.getMinecraft().profiler.startSection("eclient");

        Minecraft.getMinecraft().profiler.startSection("setup");
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableDepth();

        GlStateManager.glLineWidth(1f);
        Vec3d renderPos = EntityUtil.getInterpolatedPos(Minecraft.getMinecraft().player, event.getPartialTicks());
        EventRender e = new EventRender(RuniteTessellator.INSTANCE, renderPos);
        e.resetTranslation();
        ModuleManager.modules.stream().filter(Module::isToggled).forEach(module -> {
            module.onWorldRender(e);
        });
        Minecraft.getMinecraft().profiler.startSection("release");
        GlStateManager.glLineWidth(1f);

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        RuniteTessellator.releaseGL();
        Minecraft.getMinecraft().profiler.endSection();

        Minecraft.getMinecraft().profiler.endSection();
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        Runite.Rainbow.updateRainbow();
        Runite.rgb = Runite.Rainbow.rgb;
        RainbowUtil.updateRainbow();
    }

    @SubscribeEvent
    public void onChatSent(ClientChatEvent event) {
        if (event.getMessage().startsWith(Command.prefix)) {
            event.setCanceled(true);
            try {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
                Runite.getInstance().commandManager.callCommand(event.getMessage().substring(1));
            } catch (Exception e) {
                e.printStackTrace();
                Command.sendMessage("An error has occurred, please check your log for more info.");
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            if(Keyboard.getEventKey() == 0 || Keyboard.getEventKey() == Keyboard.KEY_NONE) return;
            ModuleManager.onBind(Keyboard.getEventKey());
        }
    }
}
