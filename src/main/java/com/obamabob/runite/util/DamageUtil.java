package com.obamabob.runite.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;

public class DamageUtil {
    public static int getItemDamage(final ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    public static boolean isArmorLow(EntityPlayer player, int durability) {
        for(ItemStack piece : player.inventory.armorInventory) {
            if(piece == null) {
                return true;
            } else {
                if(getItemDamage(piece) < durability) {
                    return true;
                }
            }
        }
        return false;
    }

    public static float getDamageInPercent(final ItemStack stack) {
        return getItemDamage(stack) / (float)stack.getMaxDamage() * 100.0f;
    }

    public static int getRoundedDamage(final ItemStack stack) {
        return (int)getDamageInPercent(stack);
    }

    public static boolean hasDurability(final ItemStack stack) {
        final Item item = stack.getItem();
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }

    public static int getCooldownByWeapon(EntityPlayer player) {
        Item item = player.getHeldItemMainhand().getItem();
        if(item instanceof ItemSword) {
            return 600;
        }

        if(item instanceof ItemPickaxe) {
            return 850;
        }

        if(item == Items.IRON_AXE) {
            return 1100;
        }

        if(item == Items.STONE_HOE) {
            return 500;
        }

        if(item == Items.IRON_HOE) {
            return 350;
        }

        if(item == Items.WOODEN_AXE || item == Items.STONE_AXE) {
            return 1250;
        }

        if(item instanceof ItemSpade || item == Items.GOLDEN_AXE || item == Items.DIAMOND_AXE || item == Items.WOODEN_HOE || item == Items.GOLDEN_HOE) {
            return 1000;
        }

        return 250;
    }
}
