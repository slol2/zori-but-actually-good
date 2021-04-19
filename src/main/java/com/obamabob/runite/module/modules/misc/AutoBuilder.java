package com.obamabob.runite.module.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import com.obamabob.runite.util.BlockUtil;
import io.netty.util.internal.MathUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;

public class AutoBuilder extends Module {
    public AutoBuilder() {super("AutoBuilder", Category.MISCELLANEOUS);}

    public BlockPos playerPos;

    int ticks = 0;

    int obsidianSlot= -1;
    int lighterSlot= -1;
    int originalSlot;

    Setting<String> mode = register(new Setting<>("Mode", this, "Portal", new String[]{"Portal", "Swastika", "Highway"}));
    Setting rotate = register(new Setting<>("Rotate",this,true));
    Setting delay = register(new Setting<>("Delay",this,5,1,40));
    ArrayList<BlockPos> blocks = new ArrayList<>();

    @Override
    public void onEnable() {
        playerPos = new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY) + 0.5, Math.floor(mc.player.posZ));
        mc.player.setPosition(Math.floor(mc.player.posX)+.5, Math.floor(mc.player.posY), Math.floor(mc.player.posZ)+.5);
        originalSlot = mc.player.inventory.currentItem;
        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && ((ItemBlock) mc.player.inventory.getStackInSlot(i).getItem()).getBlock() instanceof BlockObsidian) obsidianSlot = i;
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.FLINT_AND_STEEL) lighterSlot = i;
        }
        if (obsidianSlot == -1) disable();
        if (lighterSlot == -1 && mode.getValue().equalsIgnoreCase("Portal")) disable();
        ticks = 0;
        blocks.clear();
        mc.player.inventory.currentItem = obsidianSlot;
        switch(mode.getValue()) {
            case "Portal": {
                switch(mc.player.getAdjustedHorizontalFacing()) {
                    case NORTH: {
                        playerPos = playerPos.north();
                        blocks.add(playerPos);
                        blocks.add(playerPos.east());
                        blocks.add(playerPos.east().east());
                        blocks.add(playerPos.east().east().up());
                        blocks.add(playerPos.east().east().up().up());
                        blocks.add(playerPos.east().east().up().up().up());
                        blocks.add(playerPos.east().east().up().up().up().up());
                        blocks.add(playerPos.east().east().up().up().up().up().west());
                        blocks.add(playerPos.east().east().up().up().up().up().west().west());
                        blocks.add(playerPos.west().up().up().up().up());
                        blocks.add(playerPos.west().up().up().up());
                        blocks.add(playerPos.west().up().up());
                        blocks.add(playerPos.west().up());
                        blocks.add(playerPos.west());
                        break;
                    }
                    case EAST: {
                        playerPos = playerPos.east();
                        blocks.add(playerPos);
                        blocks.add(playerPos.south());
                        blocks.add(playerPos.south().south());
                        blocks.add(playerPos.south().south().up());
                        blocks.add(playerPos.south().south().up().up());
                        blocks.add(playerPos.south().south().up().up().up());
                        blocks.add(playerPos.south().south().up().up().up().up());
                        blocks.add(playerPos.south().south().up().up().up().up().north());
                        blocks.add(playerPos.south().south().up().up().up().up().north().north());
                        blocks.add(playerPos.north().up().up().up().up());
                        blocks.add(playerPos.north().up().up().up());
                        blocks.add(playerPos.north().up().up());
                        blocks.add(playerPos.north().up());
                        blocks.add(playerPos.north());
                        break;
                    }
                    case SOUTH: {
                        playerPos = playerPos.south();
                        blocks.add(playerPos);
                        blocks.add(playerPos.west());
                        blocks.add(playerPos.west().west());
                        blocks.add(playerPos.west().west().up());
                        blocks.add(playerPos.west().west().up().up());
                        blocks.add(playerPos.west().west().up().up().up());
                        blocks.add(playerPos.west().west().up().up().up().up());
                        blocks.add(playerPos.west().west().up().up().up().up().east());
                        blocks.add(playerPos.west().west().up().up().up().up().east().east());
                        blocks.add(playerPos.east().up().up().up().up());
                        blocks.add(playerPos.east().up().up().up());
                        blocks.add(playerPos.east().up().up());
                        blocks.add(playerPos.east().up());
                        blocks.add(playerPos.east());
                        break;
                    }
                    case WEST: {
                        playerPos = playerPos.west().west();
                        blocks.add(playerPos);
                        blocks.add(playerPos.north());
                        blocks.add(playerPos.north().north());
                        blocks.add(playerPos.north().north().up());
                        blocks.add(playerPos.north().north().up().up());
                        blocks.add(playerPos.north().north().up().up().up());
                        blocks.add(playerPos.north().north().up().up().up().up());
                        blocks.add(playerPos.north().north().up().up().up().up().south());
                        blocks.add(playerPos.north().north().up().up().up().up().south().south());
                        blocks.add(playerPos.south().up().up().up().up());
                        blocks.add(playerPos.south().up().up().up());
                        blocks.add(playerPos.south().up().up());
                        blocks.add(playerPos.south().up());
                        blocks.add(playerPos.south());
                        break;
                    }
                }
                break;
            }
            case "Swastika": {
                switch (mc.player.getAdjustedHorizontalFacing()) {
                    case NORTH: {
                        playerPos = playerPos.north();
                        blocks.add(playerPos.west().west());
                        blocks.add(playerPos.west());
                        blocks.add(playerPos);
                        blocks.add(playerPos.up());
                        blocks.add(playerPos.up().up());
                        blocks.add(playerPos.up().up().up());
                        blocks.add(playerPos.up().up().up().up());
                        blocks.add(playerPos.up().up().up().up().east());
                        blocks.add(playerPos.up().up().up().up().east().east());
                        blocks.add(playerPos.east().east());
                        blocks.add(playerPos.east().east().up());
                        blocks.add(playerPos.east().east().up().up());
                        blocks.add(playerPos.east().up().up());
                        blocks.add(playerPos.up().up());
                        blocks.add(playerPos.west().up().up());
                        blocks.add(playerPos.west().west().up().up());
                        blocks.add(playerPos.west().west().up().up().up());
                        blocks.add(playerPos.west().west().up().up().up().up());
                        break;
                    }
                    case EAST: {
                        playerPos = playerPos.east();
                        blocks.add(playerPos.north().north());
                        blocks.add(playerPos.north());
                        blocks.add(playerPos);
                        blocks.add(playerPos.up());
                        blocks.add(playerPos.up().up());
                        blocks.add(playerPos.up().up().up());
                        blocks.add(playerPos.up().up().up().up());
                        blocks.add(playerPos.up().up().up().up().south());
                        blocks.add(playerPos.up().up().up().up().south().south());
                        blocks.add(playerPos.south().south());
                        blocks.add(playerPos.south().south().up());
                        blocks.add(playerPos.south().south().up().up());
                        blocks.add(playerPos.south().up().up());
                        blocks.add(playerPos.up().up());
                        blocks.add(playerPos.north().up().up());
                        blocks.add(playerPos.north().north().up().up());
                        blocks.add(playerPos.north().north().up().up().up());
                        blocks.add(playerPos.north().north().up().up().up().up());
                        break;
                    }
                    case SOUTH: {
                        playerPos = playerPos.south();
                        blocks.add(playerPos.east().east());
                        blocks.add(playerPos.east());
                        blocks.add(playerPos);
                        blocks.add(playerPos.up());
                        blocks.add(playerPos.up().up());
                        blocks.add(playerPos.up().up().up());
                        blocks.add(playerPos.up().up().up().up());
                        blocks.add(playerPos.up().up().up().up().west());
                        blocks.add(playerPos.up().up().up().up().west().west());
                        blocks.add(playerPos.west().west());
                        blocks.add(playerPos.west().west().up());
                        blocks.add(playerPos.west().west().up().up());
                        blocks.add(playerPos.west().up().up());
                        blocks.add(playerPos.up().up());
                        blocks.add(playerPos.east().up().up());
                        blocks.add(playerPos.east().east().up().up());
                        blocks.add(playerPos.east().east().up().up().up());
                        blocks.add(playerPos.east().east().up().up().up().up());
                        break;
                    }
                    case WEST: {
                        playerPos = playerPos.west().west();
                        blocks.add(playerPos.south().south());
                        blocks.add(playerPos.south());
                        blocks.add(playerPos);
                        blocks.add(playerPos.up());
                        blocks.add(playerPos.up().up());
                        blocks.add(playerPos.up().up().up());
                        blocks.add(playerPos.up().up().up().up());
                        blocks.add(playerPos.up().up().up().up().north());
                        blocks.add(playerPos.up().up().up().up().north().north());
                        blocks.add(playerPos.north().north());
                        blocks.add(playerPos.north().north().up());
                        blocks.add(playerPos.north().north().up().up());
                        blocks.add(playerPos.north().up().up());
                        blocks.add(playerPos.up().up());
                        blocks.add(playerPos.south().up().up());
                        blocks.add(playerPos.south().south().up().up());
                        blocks.add(playerPos.south().south().up().up().up());
                        blocks.add(playerPos.south().south().up().up().up().up());
                        break;
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) disable();
        ticks++;
        while (!blocks.isEmpty() && ticks == (int)delay.getValue()) {
            BlockUtil.placeBlockScaffold(blocks.get(0), (boolean)rotate.getValue());
            blocks.remove(0);
            ticks=0;
        }
        if (blocks.isEmpty() && !mode.getValue().equalsIgnoreCase("Portal")) disable();
        else if (blocks.isEmpty() && mode.getValue().equalsIgnoreCase("Portal") && ticks == (int)delay.getValue()) {
            mc.player.inventory.currentItem = lighterSlot;
            BlockUtil.placeBlockScaffold(playerPos.up(), (boolean)rotate.getValue());
            disable();
        }
    }

    @Override
    public void onDisable() {
        mc.player.inventory.currentItem = originalSlot;
    }

    @Override
    public String getHudInfo() {
        return "[" + ChatFormatting.WHITE + blocks.size() + ChatFormatting.RESET + "]";
    }
}
