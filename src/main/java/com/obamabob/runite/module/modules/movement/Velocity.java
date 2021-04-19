package com.obamabob.runite.module.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.obamabob.runite.event.EventTarget;
import com.obamabob.runite.event.events.EventEntity;
import com.obamabob.runite.event.events.EventPacket;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

public class Velocity extends Module {
    private final Setting<Float> horizontal = register(new Setting<>("Horizontal", this, 0f, 0f, 100f));
    private final Setting<Float> vertical = register(new Setting<>("Vertical", this, 0f, 0f, 100f));

    public Velocity() {
        super("Velocity", Category.MOVEMENT);
    }
    @EventTarget
    public void onPacket(EventPacket.Receive event) {
        if (mc.player == null) return;
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity velocity = (SPacketEntityVelocity) event.getPacket();
            if (velocity.getEntityID() == mc.player.entityId) {
                if (horizontal.getValue() == 0 && vertical.getValue() == 0) event.setCancelled(true);
                velocity.motionX *= horizontal.getValue();
                velocity.motionY *= vertical.getValue();
                velocity.motionZ *= horizontal.getValue();
            }
        } else if (event.getPacket() instanceof SPacketExplosion) {
            if (horizontal.getValue() == 0 && vertical.getValue() == 0) event.setCancelled(true);
            SPacketExplosion velocity = (SPacketExplosion) event.getPacket();
            velocity.motionX *= horizontal.getValue();
            velocity.motionY *= vertical.getValue();
            velocity.motionZ *= horizontal.getValue();
        }
    }

    @EventTarget
    public void onEntityCollision(EventEntity.EntityCollision event) {
        if (event.getEntity() == mc.player) {
            if (horizontal.getValue() == 0 && vertical.getValue() == 0) {
                event.setCancelled(true);
                return;
            }
            event.setX(-event.getX() * horizontal.getValue());
            event.setY(0);
            event.setZ(-event.getZ() * horizontal.getValue());
        }
    }

    @Override
    public String getHudInfo() {
        return "[" + ChatFormatting.WHITE + "H:" + horizontal.getValue().intValue() + "V:" + vertical.getValue().intValue() + ChatFormatting.RESET + "]";
    }
}
