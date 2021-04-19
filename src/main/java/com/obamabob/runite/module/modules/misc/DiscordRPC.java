package com.obamabob.runite.module.modules.misc;

import com.obamabob.runite.module.Module;
import com.obamabob.runite.util.DiscordUtil;

public class DiscordRPC extends Module {
    public DiscordRPC() {super("DiscordRPC", Category.MISCELLANEOUS);}

    public void onEnable() { DiscordUtil.start(); }
    public void onDisable() { DiscordUtil.end(); }
}