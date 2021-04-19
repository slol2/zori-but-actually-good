package com.obamabob.runite.module.modules.combat;

import com.obamabob.runite.module.Module;
import com.obamabob.runite.module.ModuleManager;
import com.obamabob.runite.settings.Setting;
import com.obamabob.runite.util.BlockUtil;
import com.obamabob.runite.util.Mapping;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Field;

public class Burrow extends Module {
    public Burrow() {super("Burrow", Category.COMBAT);}

    Setting<Boolean> timer = register(new Setting<Boolean>("Timer", this, false));
    Setting<Boolean> toggleSurround = register(new Setting<Boolean>("Toggle Surround After", this, false));
    Setting<Boolean> toggleRStep = register(new Setting<Boolean>("Toggle Reverse Step", this, false));

    BlockPos playerPos;

    @Override
    public void onEnable() {
        if (timer.getValue()) setTimer(50);
        if(toggleRStep.getValue()) ModuleManager.getModuleByName("ReverseStep").disable();
        this.playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        if (mc.world.getBlockState(this.playerPos).getBlock().equals(Blocks.OBSIDIAN)) {
            this.disable();
            return;
        }
        mc.player.jump();
    }

    public void onDisable() {if (toggleSurround.getValue()) ModuleManager.getModuleByName("Surround").enable(); setTimer(1.0f);}

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (mc.player.posY > this.playerPos.getY() + 1.04) {
            mc.playerController.processRightClickBlock(mc.player, mc.world, playerPos, EnumFacing.DOWN, new Vec3d(playerPos), EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.jump();
            this.disable();
        }
    }

    private void setTimer(final float value) {
        try {
            final Field timer = Minecraft.class.getDeclaredField(Mapping.timer);
            timer.setAccessible(true);
            final Field tickLength = net.minecraft.util.Timer.class.getDeclaredField(Mapping.tickLength);
            tickLength.setAccessible(true);
            tickLength.setFloat(timer.get(mc), 50.0f / value);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
