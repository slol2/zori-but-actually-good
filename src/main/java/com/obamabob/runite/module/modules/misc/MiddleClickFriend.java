package com.obamabob.runite.module.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.obamabob.runite.event.EventTarget;
import com.obamabob.runite.event.events.EventMiddleClick;
import com.obamabob.runite.friend.Friends;
import com.obamabob.runite.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;

public class MiddleClickFriend extends Module {
    public MiddleClickFriend() {super("MiddleClickFriend", Category.MISCELLANEOUS);}

    @EventTarget
    public void onMiddleClick(EventMiddleClick event) {
        final RayTraceResult ray = mc.objectMouseOver;
        if (ray.typeOfHit == RayTraceResult.Type.ENTITY);
        final Entity entity = ray.entityHit;
        if (entity instanceof EntityPlayer) {
            String name = ((EntityPlayer) entity).getDisplayNameString();
            if (Friends.isFriend(name)) {
                Friends.delFriend(name);
                sendChatMessage(ChatFormatting.RED + "Removed " + ChatFormatting.GRAY + name + " from your friends list");
            } else {
                Friends.addFriend(name);
                sendChatMessage(ChatFormatting.GREEN + "Added " + ChatFormatting.GRAY + name + " to your friends list");
            }
        }
    }
}