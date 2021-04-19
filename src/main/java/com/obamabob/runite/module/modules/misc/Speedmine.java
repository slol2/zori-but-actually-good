package com.obamabob.runite.module.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.obamabob.runite.event.EventTarget;
import com.obamabob.runite.event.events.EventBlock;
import com.obamabob.runite.event.events.EventPacket;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import com.obamabob.runite.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class Speedmine extends Module {
    public Speedmine() {super("Speedmine", Category.MISCELLANEOUS);}

    Setting<String> mode = register(new Setting<>("Mode", this, "Normal", new String[]{"Normal", "Packet", "Damage", "Instant"}));
    Setting<Double> damage = register(new Setting<>("Damage Ammount", this, 0.7, 0.0, 1.0));
    Setting<Boolean> reset = register(new Setting<>("Reset", this, true));
    Setting<Boolean> noBreakAnim = register(new Setting<>("No Break Anim", this, false));
    Setting<Boolean> noDelay = register(new Setting<>("No Delay", this, false));
    Setting<Boolean> noSwing = register(new Setting<>("No Swing", this, false));
    Setting<Boolean> allow = register(new Setting<>("MultiTask", this, false));
    Setting<Boolean> doubleBreak = register(new Setting<>("Double Break", this, false));

    private final com.obamabob.runite.util.Timer timer = new Timer();

    private IBlockState currentBlockState = null;

    private BlockPos currentPos = null;
    private BlockPos lastPos = null;

    private EnumFacing lastFacing = null;

    private boolean isMining = false;



    @Override
    public void onTick() {
        if (currentPos != null &&
                (!mc.world.getBlockState(currentPos).equals(currentBlockState) || mc.world.getBlockState(currentPos).getBlock() == Blocks.AIR)) {
            currentPos = null;
            currentBlockState = null;
        }

        if (noDelay.getValue()) {
            mc.playerController.blockHitDelay = 0;
        }

        if (isMining && lastFacing != null && lastFacing != null && noBreakAnim.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, lastPos, lastFacing));
        }

        if (reset.getValue() && mc.gameSettings.keyBindUseItem.isKeyDown() && !allow.getValue()) {
            mc.playerController.isHittingBlock = false;
        }
    }

    @EventTarget
    public void onSend(EventPacket.Send event) {
        if (noSwing.getValue() && event.getPacket() instanceof CPacketAnimation) {
            event.setCancelled(true);
        }

        if (noBreakAnim.getValue() && event.getPacket() instanceof CPacketPlayerDigging) {
            final CPacketPlayerDigging p = (CPacketPlayerDigging) event.getPacket();
            try {
                for (final Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(p.getPosition()))) {
                    if (entity instanceof EntityEnderCrystal) {
                        showAnim();
                        return;
                    }
                }
            } catch (Exception ignored){}

            if (p.getAction().equals(CPacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
                showAnim(true, p.getPosition(), p.getFacing());
            }

            if (p.getAction().equals(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)) {
                showAnim();
            }
        }
    }

    @EventTarget
    public void blockListener(EventBlock event) {
        if (event.getStage() == 3 && reset.getValue() && mc.playerController.curBlockDamageMP > 0.1f) {
            mc.playerController.isHittingBlock = true;
        }
        if (event.getStage() == 4 && !mode.getValue().equalsIgnoreCase("Normal")) {
            if (canBreak(event.pos)) {
                if (reset.getValue()) {
                    mc.playerController.isHittingBlock = false;
                }

                if (mode.getValue().equalsIgnoreCase("Packet")) {
                    if (currentPos == null) {
                        currentPos = event.pos;
                        currentBlockState = mc.world.getBlockState(currentPos);
                        timer.reset();
                    }
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                    event.setCancelled(true);
                }

                if (mode.getValue().equalsIgnoreCase("Damage")) {
                    if (mc.playerController.curBlockDamageMP >= damage.getValue()) {
                        mc.playerController.curBlockDamageMP = 1.0f;
                    }
                }

                if (mode.getValue().equalsIgnoreCase("Instant")) {
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                    mc.playerController.onPlayerDestroyBlock(event.pos);
                    mc.world.setBlockToAir(event.pos);
                }

            }

            if (doubleBreak.getValue()) {

                final BlockPos above = event.pos.up();
                if (canBreak(above) && mc.player.getDistance(above.getX(), above.getY(), above.getZ()) <= 5.0) {
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, above, event.facing));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, above, event.facing));
                    mc.playerController.onPlayerDestroyBlock(above);
                    mc.world.setBlockToAir(above);
                }
            }

        }
    }

    public void showAnim(final boolean isMining, final BlockPos lastPos, final EnumFacing lastFacing) {
        this.isMining = isMining;
        this.lastPos = lastPos;
        this.lastFacing = lastFacing;
    }

    public void showAnim() {
        this.showAnim(false, null, null);
    }

    @Override
    public String getHudInfo() {
        return "[" + ChatFormatting.WHITE + mode.getValue() + ChatFormatting.RESET + "]";
    }

    public static boolean canBreak(final BlockPos pos) {
        final IBlockState blockState = Minecraft.getMinecraft().world.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, Minecraft.getMinecraft().world, pos) != -1.0f;
    }
}
