package com.obamabob.runite.module.modules.misc;

import com.obamabob.runite.event.EventTarget;
import com.obamabob.runite.event.events.EventMiddleClick;
import com.obamabob.runite.module.Module;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;

public class MiddleClickPearl extends Module {
    public MiddleClickPearl() {super("MiddleClickPearl", Category.MISCELLANEOUS);}

    @EventTarget
    public void onMiddleClick(EventMiddleClick event) {
        int oldSlot = mc.player.inventory.currentItem;
        boolean found = false;
        for(int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem().equals(Items.ENDER_PEARL)) {
                mc.player.inventory.currentItem = i;
                found = true;
                break;
            }
        }
        if (mc.objectMouseOver.typeOfHit != RayTraceResult.Type.ENTITY && found) {
            mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
            mc.player.inventory.currentItem = oldSlot;
        }
    }
}
