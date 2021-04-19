package com.obamabob.runite.util;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;

public class DiscordUtil {
    public static final String APP_ID = "793644414584619090";

    public static DiscordRichPresence presence;

    public static boolean connected;

    public static void start() {
        if (connected)
            return;
        connected = true;
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize(APP_ID, handlers, true, "");
        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        setRpcFromSettings();
        (new Thread(DiscordUtil::setRpcFromSettingsNonInt, "Discord-RPC-Callback-Handler")).start();
    }

    public static void end() {
        connected = false;
        rpc.Discord_Shutdown();
    }

    public static String getIP() {
        if (Minecraft.getMinecraft().getCurrentServerData() != null)
            return (Minecraft.getMinecraft().getCurrentServerData()).serverIP;
        if (Minecraft.getMinecraft().isIntegratedServerRunning())
            return "Singleplayer";
        return "Main Menu";
    }

    private static void setRpcFromSettingsNonInt() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                rpc.Discord_RunCallbacks();
                details = getIP();
                state = "Mining Runite";
                presence.details = details;
                presence.state = state;
                rpc.Discord_UpdatePresence(presence);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                Thread.sleep(4000L);
            } catch (InterruptedException e3) {
                e3.printStackTrace();
            }
        }
    }

    private static void setRpcFromSettings() {
        details = getIP();
        state = "Mining Runite";
        presence.startTimestamp = System.currentTimeMillis() / 1000;
        presence.largeImageKey = "wrath";
        presence.largeImageText = "Runite 1.2";
        presence.smallImageKey = "";
        presence.smallImageText = "";
    }

    private static final DiscordRPC rpc = DiscordRPC.INSTANCE;

    private static String details;

    private static String state;

    static {
        presence = new DiscordRichPresence();
        connected = false;
    }
}