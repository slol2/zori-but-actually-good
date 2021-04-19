package com.obamabob.runite.command;

import com.obamabob.runite.command.commands.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CommandManager {
    public static ArrayList<Command> commands;

    public CommandManager() {
        commands = new ArrayList<>();
        commands.add(new Help());
        commands.add(new Toggle());
        commands.add(new Bind());
        commands.add(new Friend());
        commands.add(new Drawn());
    }

    static boolean found;

    public static void callCommand(String args) {
        found = false;
        String[] argsArray = args.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        commands.forEach(command -> {
            for(String name : command.name) {
                if (argsArray[0].equalsIgnoreCase(name)) {
                    found = true;
                    command.onCommand(argsArray);
                }
            }
        });
        if (!found) {Command.sendMessage("Command not found! Type " + Command.prefix + "help for a list of commands.");}
    }
}
