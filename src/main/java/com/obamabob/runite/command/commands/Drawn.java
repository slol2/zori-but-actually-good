package com.obamabob.runite.command.commands;

import com.obamabob.runite.command.Command;
import com.obamabob.runite.module.ModuleManager;
import org.lwjgl.input.Keyboard;

public class Drawn extends Command {
    public Drawn() {super(new String[]{"drawn"});}

    @Override
    public void onCommand(String[] args) {
        if (args.length > 3) sendMessage("Usage: drawn <module>");
        else if (ModuleManager.getModuleByName(args[1]) != null) {
            ModuleManager.getModuleByName(args[1]).setDrawn(!ModuleManager.getModuleByName(args[1]).isDrawn());
        } else {
            sendMessage("The module you specified could not be found!");
        }
    }
}
