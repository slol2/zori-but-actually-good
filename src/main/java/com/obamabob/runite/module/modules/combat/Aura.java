package com.obamabob.runite.module.modules.combat;

import com.obamabob.runite.event.EventTarget;
import com.obamabob.runite.event.events.EventPacket;
import com.obamabob.runite.friend.Friends;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import com.obamabob.runite.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;

public class Aura extends Module {
    public Aura() {
        super("Aura", Category.COMBAT);
    }

    final Setting<Boolean> players = register(new Setting<>("Players", this, true));
    final Setting<Boolean> animals = register(new Setting<>("Animals", this, false));
    final Setting<Boolean> mobs = register(new Setting<>("Mobs", this, false));
    final Setting<Double> range = register(new Setting<>("Range", this, 5.5d, 1d, 10d));
    final Setting<Boolean> walls = register(new Setting<>("Walls", this, false));
    final Setting<Boolean> rotate = register(new Setting<>("Rotate", this, true));
    final Setting<Boolean> sharpness = register(new Setting<>("32k Switch", this, false));

    boolean isSpoofingAngles = false;
    double yaw;
    double pitch;

    @Override
    public void onTick() {
        if (mc.player == null) {
            disable();
            return;
        }
        if (mc.player.isDead) {
            return;
        }
        boolean shield = mc.player.getHeldItemOffhand().getItem().equals(Items.SHIELD) && mc.player.getActiveHand() == EnumHand.OFF_HAND;
        boolean gap = mc.player.getHeldItemOffhand().getItem().equals(Items.GOLDEN_APPLE) && mc.player.getActiveHand() == EnumHand.OFF_HAND;
        if (mc.player.isHandActive() && !shield && !gap) {
            return;
        }

        for (Entity target : Minecraft.getMinecraft().world.loadedEntityList) {
            if (!EntityUtil.isLiving(target)) {
                continue;
            }
            if (target == mc.player) {
                continue;
            }
            if (mc.player.getDistance(target) > range.getValue()) {
                continue;
            }
            if (((EntityLivingBase) target).getHealth() <= 0) {
                continue;
            }
            if (!walls.getValue() && (!mc.player.canEntityBeSeen(target) && !canEntityFeetBeSeen(target))) {
                continue; // If walls is on & you can't see the feet or head of the target, skip. 2 raytraces needed
            }
            if (players.getValue() && target instanceof EntityPlayer && !Friends.isFriend(target.getName())) {
                attack(target);
                return;
            } else {
                if (EntityUtil.isPassive(target) ? animals.getValue() : (EntityUtil.isMobAggressive(target) && mobs.getValue())) {
                    attack(target);
                    return;
                }
            }
        }
        resetRotation();
    }

    private boolean checkSharpness(ItemStack stack) {

        if (stack.getTagCompound() == null) {
            return false;
        }

        NBTTagList enchants = (NBTTagList) stack.getTagCompound().getTag("ench");

        if (enchants == null) {
            return false;
        }


        for (int i = 0; i < enchants.tagCount(); i++) {
            NBTTagCompound enchant = ((NBTTagList) enchants).getCompoundTagAt(i);
            if (enchant.getInteger("id") == 16) {
                int lvl = enchant.getInteger("lvl");
                if (lvl >= 16) {
                    return true;
                }
                break;
            }
        }

        return false;

    }

    private void attack(Entity e) {

        if (sharpness.getValue()) {

            if (!checkSharpness(mc.player.getHeldItemMainhand())) {

                int newSlot = -1;

                for (int i = 0; i < 9; i++) {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    if (stack == ItemStack.EMPTY) {
                        continue;
                    }
                    if (checkSharpness(stack)) {
                        newSlot = i;
                        break;
                    }
                }

                if (newSlot != -1) {
                    mc.player.inventory.currentItem = newSlot;
                }

            }

        }

        if (rotate.getValue()) {
            lookAtPacket(e.posX, e.posY, e.posZ, mc.player);
        }

        mc.playerController.attackEntity(mc.player, e);
        mc.player.swingArm(EnumHand.MAIN_HAND);

    }

    private boolean canEntityFeetBeSeen(Entity entityIn) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posX + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ), false, true, false) == null;
    }

    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = EntityUtil.calculateLookAt(px, py, pz, me);
        setYawAndPitch((float) v[0], (float) v[1]);
    }

    private void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
    }

    private void resetRotation() {
        if (isSpoofingAngles) {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            isSpoofingAngles = false;
        }
    }

    @Override
    public void onDisable() {
        resetRotation();
    }

    @EventTarget
    public void onSend(EventPacket.Send event) {
        Packet packet = event.getPacket();
        if (packet instanceof CPacketPlayer && rotate.getValue()) {
            if (isSpoofingAngles) {
                ((CPacketPlayer) packet).yaw = (float) yaw;
                ((CPacketPlayer) packet).pitch = (float) pitch;
            }
        }
    }
}