package cc.patrone.practice.command;

import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import org.bukkit.ChatColor;

public class TimeCommands {

    @Command(name = "day", inGameOnly = true)
    public void day(CommandArgs args) {
        args.getPlayer().setPlayerTime(1000L, false);
        args.getSender().sendMessage(ChatColor.GREEN + "The time has been set to day.");
        return;
    }

    @Command(name = "night", inGameOnly = true)
    public void night(CommandArgs args) {
        args.getPlayer().setPlayerTime(20000L, false);
        args.getSender().sendMessage(ChatColor.GREEN + "The time has been set to night.");
        return;
    }
}
