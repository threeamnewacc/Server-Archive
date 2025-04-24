package me.devkevin.landcore.commands.impl;

import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.utils.PlayerList;
import org.bukkit.command.CommandSender;

public class ListCommand extends BaseCommand {
    public ListCommand() {
        super("list");
        setAliases("online", "players", "who");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        PlayerList onlinePlayerList = PlayerList.newList().sortedByRank();

        sender.sendMessage(" ");
        sender.sendMessage(PlayerList.ORDERED_RANKS);
        sender.sendMessage(" ");
        sender.sendMessage("(" + onlinePlayerList.size() + ") " + onlinePlayerList.asColoredNames());
        sender.sendMessage(" ");
    }
}
