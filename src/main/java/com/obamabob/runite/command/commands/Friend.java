package com.obamabob.runite.command.commands;

import com.obamabob.runite.command.Command;
import com.obamabob.runite.friend.Friends;

public class Friend extends Command {
    public Friend() {super(new String[]{"friend"});}

    @Override
    public void onCommand(String[] args) {
        if(args.length >3) {
            sendMessage("Usage: friend <add/del> <name>");
            return;
        }
        if (args[1].equalsIgnoreCase("add")) {
            if (Friends.isFriend(args[2])) {
                sendMessage(args[2] + " is already your friend!");
                return;
            }
            Friends.addFriend(args[2]);
            sendMessage("Added " + args[2] + " to your friends list.");
        } else if (args[1].equalsIgnoreCase("del")) {
            if (!Friends.isFriend(args[2])) {
                sendMessage(args[2] + " is already not your friend!");
                return;
            }
            Friends.delFriend(args[2]);
            sendMessage("Removed " + args[2] + " from your friends list.");
        } else sendMessage("Usage: friend <add/del> <name>");
    }
}
