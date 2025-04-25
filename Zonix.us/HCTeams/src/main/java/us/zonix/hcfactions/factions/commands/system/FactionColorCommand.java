package us.zonix.hcfactions.factions.commands.system;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.factions.commands.FactionCommand;
import us.zonix.hcfactions.factions.type.SystemFaction;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import us.zonix.hcfactions.factions.commands.FactionCommand;
import us.zonix.hcfactions.factions.type.SystemFaction;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

/**
 * Copyright 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Alexander Maxwell
 */
public class FactionColorCommand extends FactionCommand {
    @Command(name = "f.color", aliases = {"faction.color", "factions.color", "f.setcolor", "faction.setcolor", "factions.setcolor"}, inGameOnly = false, permission = Rank.DEVELOPER)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getPlayer();

        String[] args = command.getArgs();

        if (args.length != 2) {
            sender.sendMessage(langConfig.getString("TOO_FEW_ARGS.SET_COLOR"));
            return;
        }

        String name = args[0];

        SystemFaction systemFaction = SystemFaction.getByName(name);

        if (systemFaction == null) {
            sender.sendMessage(langConfig.getString("ERROR.SYSTEM_FACTION_NOT_FOUND").replace("%NAME%", name));
            return;
        }

        ChatColor color;
        try {
            color = ChatColor.valueOf(args[1].toUpperCase());
        } catch (Exception exception) {
            sender.sendMessage(langConfig.getString("ERROR.INVALID_COLOR"));
            return;
        }

        systemFaction.setColor(color);
        sender.sendMessage(langConfig.getString("SYSTEM_FACTION.SET_COLOR").replace("%COLOR%", color + "").replace("%COLOR_NAME%", color.name()).replace("%FACTION%", systemFaction.getName()));
    }
}
