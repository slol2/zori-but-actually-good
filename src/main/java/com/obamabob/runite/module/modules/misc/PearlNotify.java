package com.obamabob.runite.module.modules.misc;

import com.obamabob.runite.command.Command;
import com.obamabob.runite.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PearlNotify extends Module {

    ConcurrentHashMap<UUID, Integer> uuidMap = new ConcurrentHashMap<>();

    public PearlNotify() {
        super("PearlNotify", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    public void onTick() {
        if (mc.world == null) return;
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderPearl) {
                EntityPlayer closest = null;
                for (EntityPlayer p : mc.world.playerEntities) {
                    if (closest == null || entity.getDistance(p) < entity.getDistance(closest)) {
                        closest = p;
                    }
                }
                if (closest != null && closest.getDistance(entity) < 2 && !uuidMap.containsKey(entity.getUniqueID()) && !closest.getName().equalsIgnoreCase(mc.player.getName())) {
                    uuidMap.put(entity.getUniqueID(), 200);
                    sendChatMessage(closest.getName() + " threw a pearl towards " + getTitle(entity.getHorizontalFacing().getName()) + "!");
                }
            }
        }
        this.uuidMap.forEach((name, timeout) -> {
            if (timeout <= 0) {
                this.uuidMap.remove(name);
            } else {
                this.uuidMap.put(name, timeout - 1);
            }
        });
    }

    public String getTitle(String in) {
        if (in.equalsIgnoreCase("west")) {
            return "east";
        } else if (in.equalsIgnoreCase("east")) {
            return "west";
        } else {
            return in;
        }
    }
}