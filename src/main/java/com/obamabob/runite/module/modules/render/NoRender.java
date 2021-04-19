package com.obamabob.runite.module.modules.render;

import com.obamabob.runite.event.EventTarget;
import com.obamabob.runite.event.events.EventPacket;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoRender extends Module {

    private final Setting<Boolean> mob = register(new Setting<>("Mob", this, false));
    private final Setting<Boolean> gentity = register(new Setting<>("GEntity", this, false));
    public final Setting<Boolean> armor = register(new Setting<>("Armor", this, false));
    public final Setting<Boolean> armorTrans = register(new Setting<>("Armor Transparency", this, false));
    public final Setting<Integer> alpha = register(new Setting<>("Transparency", this, 255, 0, 255));
    private final Setting<Boolean> object = register(new Setting<>("Object", this, false));
    private final Setting<Boolean> xp = register(new Setting<>("XP", this, false));
    private final Setting<Boolean> paint = register(new Setting<>("Paintings", this, false));
    private final Setting<Boolean> fire = register(new Setting<>("Fire", this, true));

    public NoRender() {
        super("NoRender", Category.RENDER);
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @EventTarget
    public void onPacket(EventPacket.Receive event) {
        Packet packet = event.getPacket();
        if ((packet instanceof SPacketSpawnMob && mob.getValue()) ||
                (packet instanceof SPacketSpawnGlobalEntity && gentity.getValue()) ||
                (packet instanceof SPacketSpawnObject && object.getValue()) ||
                (packet instanceof SPacketSpawnExperienceOrb && xp.getValue()) ||
                (packet instanceof SPacketSpawnPainting && paint.getValue()))
            event.setCancelled(true);
    }

    @SubscribeEvent
    public void onBlockOverlay(RenderBlockOverlayEvent event) {
        if (fire.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) event.setCanceled(true);
    }
}
