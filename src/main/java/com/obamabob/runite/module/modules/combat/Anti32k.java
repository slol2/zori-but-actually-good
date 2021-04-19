package com.obamabob.runite.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.obamabob.runite.friend.Friends;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.settings.Setting;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.util.text.TextComponentString;

public class Anti32k extends Module {
    public Anti32k() {super("Anti32k", Category.COMBAT);}

    Setting<Boolean> autoLog = register(new Setting<>("AutoLog", this, false));
    Setting<Integer> distance = register(new Setting<>("Distance", this,6, 0, 16));

    @Override
    public void onTick() {
        if (mc.world == null) return;
        mc.world.loadedEntityList.forEach(player -> {
            if (player instanceof EntityPlayer) {
                if (player == mc.player) return;
                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, ((EntityPlayer) player).inventory.getCurrentItem()) == 32767) {
                    sendChatMessage(ChatFormatting.RED + player.getName() + " is now holding a 32k");
                    if (autoLog.getValue() && player.getDistance(mc.player) <= distance.getValue() && !Friends.isFriend(player.getName())) {
                        mc.player.sendChatMessage(player.getName() + " tried to 32k me! Disconnecting!");
                        mc.player.connection.onDisconnect(new TextComponentString(ChatFormatting.RED + player.getName() + " tried to 32k you."));
                    }
                }
            }
        });
    }
}
