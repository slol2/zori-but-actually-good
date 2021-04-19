package com.obamabob.runite.command.commands;

import com.obamabob.runite.command.Command;
import com.obamabob.runite.module.ModuleManager;

public class Toggle extends Command {
    public Toggle() {super(new String[]{"toggle", "t"}); description = "Toggles a specified module.";}

    boolean found;

    @Override
    public void onCommand(String[] args) {
        found = false;
        ModuleManager.modules.forEach(module -> {
            if (args[1].equalsIgnoreCase(module.getName())) {
                found = true;
                module.toggle();
            }
        });
        if (!found) sendMessage("The specified module cannot be found!");
    }
}
