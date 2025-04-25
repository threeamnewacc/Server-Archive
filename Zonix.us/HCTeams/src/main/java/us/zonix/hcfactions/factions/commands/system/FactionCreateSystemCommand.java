package us.zonix.hcfactions.factions.commands.system;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.factions.Faction;
import us.zonix.hcfactions.factions.commands.FactionCommand;
import us.zonix.hcfactions.factions.type.SystemFaction;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import org.bukkit.command.CommandSender;
import us.zonix.hcfactions.factions.commands.FactionCommand;
import us.zonix.hcfactions.factions.type.SystemFaction;
import us.zonix.hcfactions.util.command.CommandArgs;

/**
 * Copyright 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Alexander Maxwell
 */
public class FactionCreateSystemCommand extends FactionCommand {
    @Command(name = "f.createsystem", aliases = {"faction.createsystem", "factions.createsystem"}, inGameOnly = false, permission = Rank.DEVELOPER)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getPlayer();

        String[] args = command.getArgs();

        if (args.length == 0) {
            sender.sendMessage(langConfig.getString("TOO_FEW_ARGS.CREATE_SYSTEM"));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(command.getArgs()[i]).append(" ");
        }

        String name = sb.toString().trim();

        if (Faction.getByName(name) != null) {
            sender.sendMessage(langConfig.getString("ERROR.NAME_TAKEN"));
            return;
        }

        new SystemFaction(name, null);
        sender.sendMessage(langConfig.getString("SYSTEM_FACTION.CREATED").replace("%NAME%", name));
    }
}
