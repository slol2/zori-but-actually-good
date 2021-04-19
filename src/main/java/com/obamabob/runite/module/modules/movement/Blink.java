package com.obamabob.runite.module.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.obamabob.runite.event.EventTarget;
import com.obamabob.runite.event.events.EventPacket;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Blink extends Module {

    public Blink() {
        super("Blink", Category.MOVEMENT);
    }

    Setting<Boolean> ghostPlayer = register(new Setting<>("Ghost Player", this, true));

    EntityOtherPlayerMP entity;
    private final Queue<Packet> packets = new ConcurrentLinkedQueue();

    public void onEnable() {
        if (ghostPlayer.getValue() && mc.player != null) {
            entity = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
            entity.copyLocationAndAnglesFrom(mc.player);
            entity.inventory.copyInventory(mc.player.inventory);
            entity.rotationYaw = mc.player.rotationYaw;
            entity.rotationYawHead = mc.player.rotationYawHead;
            mc.world.addEntityToWorld(667, entity);
        }
    }

    public void onUpdate() {
        if (!ghostPlayer.getValue() && entity != null) {
            mc.world.removeEntity(entity);
        }
    }

    public void onDisable() {
        if (entity != null) {
            mc.world.removeEntity(entity);
        }

        if (packets.size() > 0 && mc.player != null) {
            for (Packet packet : packets) {
                mc.player.connection.sendPacket(packet);
            }
            packets.clear();
        }
    }

    @EventTarget
    public void onSend(EventPacket.Send event) {
        Packet packet = event.getPacket();
        if (packet instanceof CPacketChatMessage || packet instanceof CPacketConfirmTeleport || packet instanceof CPacketKeepAlive || packet instanceof CPacketTabComplete || packet instanceof CPacketClientStatus) {
            return;
        }
        if (mc.player == null || mc.player.isDead) {
            packets.add(packet);
            event.setCancelled(true);
        }
    }

    public String getHudInfo() {
        String t = "[" + ChatFormatting.WHITE + packets.size() + ChatFormatting.RESET + "]";

        return t;
    }
}
