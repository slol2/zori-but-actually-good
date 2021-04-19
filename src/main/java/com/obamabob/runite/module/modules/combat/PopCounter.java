package com.obamabob.runite.module.modules.combat;

import com.obamabob.runite.Runite;
import com.obamabob.runite.clickgui.Panel;
import com.obamabob.runite.event.EventTarget;
import com.obamabob.runite.event.events.EventPacket;
import com.obamabob.runite.event.events.EventTotemPop;
import com.obamabob.runite.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

import java.util.concurrent.ConcurrentHashMap;

public class PopCounter extends Module {

    public ConcurrentHashMap<EntityPlayer, Integer> popMap = new ConcurrentHashMap<>();

    public PopCounter() {
        super("PopCounter", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player.getHealth() == 0.0f  && popMap.containsKey(player)) {
                sendChatMessage(player.getName() + " has died after popping " + popMap.get(player) + (popMap.get(player) == 1 ? " totem!" : " totems!"));
                popMap.remove(player);
            }
        }
    }

    @EventTarget
    public void onPop(EventTotemPop event) {
        int pops = popMap.getOrDefault(event.getPlayer(), 0) + 1;
        sendChatMessage(event.getPlayer().getName() + " has popped " + pops + (pops == 1 ? " totem!" : " totems!"));
        popMap.put(event.getPlayer(), pops);
    }

    @EventTarget
    public void onPacket(EventPacket.Receive event) {
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                EntityPlayer entity = (EntityPlayer) packet.getEntity(mc.world);
                EventTotemPop eventTotemPop = new EventTotemPop(entity);
                eventTotemPop.call();
            }
        }
    }
}