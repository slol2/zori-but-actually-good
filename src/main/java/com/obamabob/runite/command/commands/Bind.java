package com.obamabob.runite.command.commands;

import com.obamabob.runite.command.Command;
import com.obamabob.runite.module.Module;
import com.obamabob.runite.module.ModuleManager;
import org.lwjgl.input.Keyboard;

public class Bind extends Command {
    public Bind() {super(new String[]{"bind"});}

    @Override
    public void onCommand(String[] args) {
        if (args.length < 2) {
            sendMessage("Please specify which module you want bound");
            return;
        }
        if (args.length < 3) {
            sendMessage("Please specify the key you would like to bind");
            return;
        }
        Module m = ModuleManager.getModuleByName(args[1]);
        if (m == null) {
            sendMessage("Module not found.");
            return;
        }
        m.setBind(Keyboard.getKeyIndex(args[2].toUpperCase()));
        sendMessage(m.getName() + " bound to " + args[2].toUpperCase());
    }
}
