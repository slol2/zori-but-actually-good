package com.obamabob.runite.module.modules.misc;

import com.obamabob.runite.module.Module;
import net.minecraft.util.text.TextComponentString;

public class EzLog extends Module {
    public EzLog() {super("EzLog", Category.MISCELLANEOUS);}

    public void onEnable() {
        if (mc.player != null)mc.player.connection.onDisconnect(new TextComponentString("EZZZZZ LOG STAY MAD NN"));
        disable();
    }
}
