package us.zonix.hcfactions.factions.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.util.command.CommandArgs;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.CommandSender;
import us.zonix.core.util.Clickable;
import us.zonix.hcfactions.util.command.CommandArgs;

import java.util.*;

/**
 * Copyright 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Alexander Maxwell
 */
public class FactionListCommand extends FactionCommand {
    @Command(name = "f.list", aliases = {"faction.list", "factions.list"}, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        String[] args = command.getArgs();
        CommandSender sender = command.getSender();

        final HashMap<PlayerFaction, Integer> factions = new HashMap<>();
        int page = 1;

        for (PlayerFaction playerFaction : PlayerFaction.getPlayerFactions()) {
            if (playerFaction.getOnlinePlayers().size() > 0) {
                factions.put(playerFaction, playerFaction.getOnlinePlayers().size());
            }
        }

        List<PlayerFaction> sortedList = new ArrayList<>(factions.keySet());
        Collections.sort(sortedList, new Comparator<PlayerFaction>() {
            @Override
            public int compare(PlayerFaction firstFaction, PlayerFaction secondFaction) {
                return factions.get(secondFaction).compareTo(factions.get(firstFaction));
            }
        });

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("list")) {
                if (NumberUtils.isNumber(args[1])) {
                    page = (int) Double.parseDouble(args[1]);
                }
            }
        }

        if (sortedList.isEmpty()) {
            sender.sendMessage(langConfig.getString("ERROR.NO_FACTIONS_TO_LIST"));
            return;
        }

        int listSize = Math.round(sortedList.size() / 10);
        if (listSize == 0) {
            listSize = 1;
        }

        if (page > listSize) {
            page = listSize;
        }

        for (String msg : langConfig.getStringList("FACTION_LIST")) {
            if (msg.contains("%FACTION%")) {
                for (int i = page * 10 - 10; i < page * 10; i++) {
                    if (sortedList.size() > i) {
                        PlayerFaction playerFaction = sortedList.get(i);

                        Clickable clickable = new Clickable(msg.replace("%FACTION%", playerFaction.getName()).replace("%DTR%", playerFaction.getDeathsTillRaidable() + "").replace("%MAX_DTR%", playerFaction.getMaxDeathsTillRaidable() + "").replace("%BALANCE%", playerFaction.getBalance() + "").replace("%ONLINE_COUNT%", playerFaction.getOnlinePlayers().size() + "").replace("%MAX_COUNT%", playerFaction.getAllPlayerUuids().size() + "").replace("%POSITION%", (i + 1) + ""),
                                ChatColor.GRAY + "Click to view faction information",
                                "/f show " + playerFaction.getName());

                        if(sender instanceof Player) {
                            clickable.sendToPlayer((Player) sender);
                        }

                    }
                }
            } else {
                sender.sendMessage(msg.replace("%PAGE%", page + "").replace("%TOTAL_PAGES%", listSize + ""));
            }
        }
    }
}
