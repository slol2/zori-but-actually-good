package com.obamabob.runite.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.obamabob.runite.Runite;
import com.obamabob.runite.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class Command {
    public static String prefix;
    public String[] name;
    public String description;

    public Command(String[] name) {
        this.name = name;
        prefix = ",";
        description = "No Description.";
    }

    public void onCommand(String[] args) {}

    public String[] getName() {return name;}

    public static ChatFormatting wh = ChatFormatting.WHITE;
    public static ChatFormatting aq = ChatFormatting.AQUA;
    public static ChatFormatting bo = ChatFormatting.BOLD;
    public static ChatFormatting gr = ChatFormatting.GRAY;

    public static final String ru = "Runite";
    public static final String ar = ">>>";
    public static final String sp = " ";

    public static void sendMessage(String m){
        if (Minecraft.getMinecraft().player != null)
        {
            final ITextComponent itc = new TextComponentString(wh + ru + aq + bo + sp + ar + sp + gr + m);
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(itc, 5936);
        }
    }
    public static void sendRawMessage(String m){
        if (Minecraft.getMinecraft().player != null) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(wh + ru + aq + bo + sp + ar + sp + gr + m));
        }
    }
}
