package com.obamabob.runite.command.commands;

import com.obamabob.runite.command.Command;
import com.obamabob.runite.command.CommandManager;

public class Help extends Command {
    public Help() {
        super(new String[]{"help"});
        description = "Gives a list of commands.";
    }

    @Override
    public void onCommand(String[] args) {
        sendRawMessage("Runite 1.2 by obamabob");
        for (Command command : CommandManager.commands) {
            sendRawMessage(command.name[0] + " - " + command.description);
        }
    }
}
