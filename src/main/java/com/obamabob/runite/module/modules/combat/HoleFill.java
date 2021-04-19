package com.obamabob.runite.module.modules.combat;

import com.obamabob.runite.module.Module;
import com.obamabob.runite.module.ModuleManager;
import com.obamabob.runite.settings.Setting;
import com.obamabob.runite.util.BlockUtil;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HoleFill  extends Module {
    public HoleFill() {super("HoleFill", Category.COMBAT);}

    Setting<String> mode = register(new Setting<>("Type", this, "Obby", new String[]{"Obby", "Echest", "Both", "Web"}));
    Setting<Integer> placeDelay = register(new Setting<>("Delay", this,3,0,10));
    Setting<Double> horizontalRange = register(new Setting<>("H Range", this, 4.0,0.0,10.0));
    Setting<Double> verticalRange = register(new Setting<>("V Range", this, 2.0, 0.0, 5.0));
    Setting<Boolean> rotate = register(new Setting<>("Rotate", this, true));
    Setting<Boolean> autoSwitch = register(new Setting<>("Switch", this, true));

    private boolean isSneaking = false;
    private int delayTicks = 0;
    private int oldHandEnable = -1;

    public void onEnable() {
        if (autoSwitch.getValue() && mc.player != null) {
            oldHandEnable = mc.player.inventory.currentItem;
        }
    }

    public void onDisable() {
        if (autoSwitch.getValue() && mc.player != null) {
            mc.player.inventory.currentItem = oldHandEnable;
        }
        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }
    }

    public void onTick() {
        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }

        List<BlockPos> holePos = new ArrayList<>();
        holePos.addAll(findHoles());

        if (holePos != null) {

            if (autoSwitch.getValue()) {
                int oldHand = mc.player.inventory.currentItem;
                int newHand = findRightBlock(oldHand);

                if (newHand != -1) {
                    mc.player.inventory.currentItem = findRightBlock(oldHand);
                }
                else {
                    return;
                }
            }

            BlockPos placePos = holePos.stream().sorted(Comparator.comparing(blockPos -> blockPos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ))).findFirst().orElse(null);

            if (placePos == null) {
                return;
            }

            for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(placePos))) {
                if (entity instanceof EntityPlayer) {
                    return;
                }
            }

            if (delayTicks >= placeDelay.getValue() && isHoldingRightBlock(mc.player.inventory.currentItem, mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem()) && placeBlock(placePos)) {
                delayTicks = 0;
            }
            delayTicks++;
        }
    }

    private List<BlockPos> findHoles() {
        NonNullList<BlockPos> holes = NonNullList.create();

        //from old HoleFill module, really good way to do this
        Iterable<BlockPos> worldPosBlockPos = BlockPos.getAllInBox(mc.player.getPosition().add(-horizontalRange.getValue(), -verticalRange.getValue(), -horizontalRange.getValue()), mc.player.getPosition().add(horizontalRange.getValue(), verticalRange.getValue(), horizontalRange.getValue()));

        for (BlockPos blockPos : worldPosBlockPos) {
            if (isSurrounded(blockPos)) {
                holes.add(blockPos);
            }
        }

        return holes;
    }

    private boolean isSurrounded(BlockPos blockPos) {
        if (mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR
                && mc.world.getBlockState(blockPos.east()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(blockPos.west()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(blockPos.north()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(blockPos.south()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(blockPos.down()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(blockPos.up()).getBlock() == Blocks.AIR
                && mc.world.getBlockState(blockPos.up(2)).getBlock() == Blocks.AIR) {
            return true;
        }

        return false;
    }

    private int findRightBlock(int oldHand) {
        int newHand = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);

            if (itemStack == ItemStack.EMPTY || !(itemStack.getItem() instanceof ItemBlock)) {
                continue;
            }

            Block block = ((ItemBlock) itemStack.getItem()).getBlock();
            if ((mode.getValue().equalsIgnoreCase("Obby") || mode.getValue().equalsIgnoreCase("Both")) && block instanceof BlockObsidian) {
                newHand = i;
                break;
            }
            else if ((mode.getValue().equalsIgnoreCase("Echest") || mode.getValue().equalsIgnoreCase("Both")) && block instanceof BlockEnderChest) {
                newHand = i;
                break;
            }
            else if (mode.getValue().equalsIgnoreCase("Web") && block instanceof BlockWeb) {
                newHand = i;
                break;
            }
        }

        if (newHand == -1) {
            newHand = oldHand;
        }

        return newHand;
    }

    private Boolean isHoldingRightBlock(int hand, Item item) {
        if (hand == -1) {
            return false;
        }

        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock) item).getBlock();

            if (mode.getValue().equalsIgnoreCase("Obby") && block instanceof BlockObsidian) {
                return true;
            }
            else if (mode.getValue().equalsIgnoreCase("Echest") && block instanceof BlockEnderChest) {
                return true;
            }
            else if (mode.getValue().equalsIgnoreCase("Both") && (block instanceof BlockObsidian || block instanceof BlockEnderChest)) {
                return true;
            }
            else if (mode.getValue().equalsIgnoreCase("Web") && block instanceof BlockWeb) {
                return true;
            }
        }

        return false;
    }

    /** Mostly ported from Surround, best way to do it */
    private Boolean placeBlock(BlockPos blockPos) {
        if (blockPos == null) {
            return false;
        }

        Block block = mc.world.getBlockState(blockPos).getBlock();

        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }

        EnumFacing side = BlockUtil.getPlaceableSide(blockPos);

        if (side == null) {
            return false;
        }

        BlockPos neighbour = blockPos.offset(side);
        EnumFacing opposite = side.getOpposite();

        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }

        boolean stoppedAC = false;

        if (ModuleManager.isModuleEnabled("AutoCrystal")) {
            AutoCrystal.stopAC = true;
            stoppedAC = true;
        }

        if (rotate.getValue()) {
            BlockUtil.faceVectorPacketInstant(hitVec);
        }

        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;

        if (stoppedAC) {
            AutoCrystal.stopAC = false;
            stoppedAC = false;
        }

        return true;
    }
}
