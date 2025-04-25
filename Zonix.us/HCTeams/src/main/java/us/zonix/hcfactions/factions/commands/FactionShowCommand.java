package us.zonix.hcfactions.factions.commands;

import us.zonix.hcfactions.factions.type.SystemFaction;
import us.zonix.hcfactions.factions.Faction;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.LocationSerialization;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.util.player.SimpleOfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.factions.type.SystemFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.util.player.SimpleOfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Copyright 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Alexander Maxwell
 */
public class FactionShowCommand extends FactionCommand {
    @Command(name = "f.show", aliases = {"faction.show", "factions.show", "f.i", "faction.i", "factions.i", "f.info", "faction.info", "factions.info", "f.who", "faction.who", "factions.who"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (command.getArgs().length == 0) {
            PlayerFaction playerFaction = profile.getFaction();

            if (playerFaction == null) {
                player.sendMessage(langConfig.getString("ERROR.NOT_IN_FACTION"));
                return;
            }

            sendFactionInformation(player, playerFaction);
            return;
        }


        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < command.getArgs().length; i++) {
            sb.append(command.getArgs()[i]).append(" ");
        }
        String name = sb.toString().trim().replace(" ", "");

        Set<Faction> matchedFactions = Faction.getAllByString(name);

        if (matchedFactions.isEmpty()) {
            player.sendMessage(langConfig.getString("ERROR.NO_FACTIONS_FOUND").replace("%NAME%", name));
            return;
        }

        for (Faction faction : matchedFactions) {
            sendFactionInformation(player, faction);
        }
    }

    private void sendFactionInformation(Player player, Faction faction) { //This method is legit aids, need to fix this when I have time...
        List<String> toSend = new ArrayList<>();
        if (faction instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction) faction;

            final String ROOT = "FACTION_SHOW.PLAYER_FACTION.";
            final String ROOT_SETTINGS = ROOT + "SETTINGS.";
            ChatColor offlineColor = ChatColor.valueOf(langConfig.getString(ROOT_SETTINGS + "OFFLINE_COLOR").toUpperCase());
            ChatColor onlineColor = ChatColor.valueOf(langConfig.getString(ROOT_SETTINGS + "ONLINE_COLOR").toUpperCase());
            ChatColor raidableColor =  ChatColor.valueOf(langConfig.getString(ROOT_SETTINGS + "DTR_COLOR.RAIDABLE").toUpperCase());
            ChatColor notRaidableColor =  ChatColor.valueOf(langConfig.getString(ROOT_SETTINGS + "DTR_COLOR.NOT_RAIDABLE").toUpperCase());
            String killFormat = langConfig.getString(ROOT_SETTINGS + "SHOW_KILLS.FORMAT");
            String splitNamesFormat = langConfig.getString(ROOT_SETTINGS + "SPLIT_NAMES.FORMAT");
            boolean splitNamesEnabled = langConfig.getBoolean(ROOT_SETTINGS + "SPLIT_NAMES.ENABLED");
            boolean killFormatEnabled = langConfig.getBoolean(ROOT_SETTINGS + "SHOW_KILLS.ENABLED");


            for (String string : langConfig.getStringList(ROOT + "MESSAGE")) {
                string = string.replace("%FACTION%", faction.getName());
                string = string.replace("%ONLINE_COUNT%", playerFaction.getOnlinePlayers().size() + "");
                string = string.replace("%MAX_COUNT%", playerFaction.getAllPlayerUuids().size() + "");

                if (string.contains("%HOME%")) {
                    if (playerFaction.getHome() == null) {
                        string = string.replace("%HOME%", langConfig.getString(ROOT_SETTINGS + "HOME_PLACEHOLDER"));
                    } else {
                        Location homeLocation = LocationSerialization.deserializeLocation(playerFaction.getHome());
                        string = string.replace("%HOME%", homeLocation.getBlockX() + ", " + homeLocation.getBlockZ());
                    }
                }

                if (string.contains("%LEADER%")) {

                    String leaderString;
                    UUID leader = playerFaction.getLeader();
                    SimpleOfflinePlayer leaderPlayer = SimpleOfflinePlayer.getByUuid(leader);

                    if (leaderPlayer == null) {
                        continue;
                    }
                    if (Bukkit.getPlayer(leader) == null) {
                        leaderString = offlineColor + leaderPlayer.getName();
                    } else {
                        leaderString = onlineColor + leaderPlayer.getName();
                    }

                    if (killFormatEnabled) {
                        leaderString = leaderString + killFormat.replace("%KILLS%", leaderPlayer.getKills() + "");
                    }

                    string = string.replace("%LEADER%", leaderString);
                }

                if (string.contains("%OFFICERS%")) {
                    String officerString = "";

                    if (playerFaction.getOfficers().isEmpty()) {
                        continue;
                    }

                    for (UUID uuid : playerFaction.getOfficers()) {
                        SimpleOfflinePlayer officer = SimpleOfflinePlayer.getByUuid(uuid);

                        if (officer == null) {
                            continue;
                        }

                        if (Bukkit.getPlayer(uuid) == null) {
                            officerString = officerString + offlineColor + officer.getName();
                        } else {
                            officerString = officerString + onlineColor + officer.getName();
                        }

                        if (killFormatEnabled) {
                            officerString = officerString + killFormat.replace("%KILLS%", officer.getKills() + "");
                        }

                        if (splitNamesEnabled) {
                            officerString = officerString + splitNamesFormat;
                        }
                    }

                    string = string.replace("%OFFICERS%", officerString);
                }

                if (string.contains("%MEMBERS%")) {
                    String memberString = "";

                    if (playerFaction.getMembers().isEmpty()) {
                        continue;
                    }

                    for (UUID uuid : playerFaction.getMembers()) {
                        SimpleOfflinePlayer member = SimpleOfflinePlayer.getByUuid(uuid);

                        if (member == null) {
                            continue;
                        }

                        if (Bukkit.getPlayer(uuid) == null) {
                            memberString = memberString + offlineColor + member.getName();
                        } else {
                            memberString = memberString + onlineColor + member.getName();
                        }

                        if (killFormatEnabled) {
                            memberString = memberString + killFormat.replace("%KILLS%", member.getKills() + "");
                        }

                        if (splitNamesEnabled) {
                            memberString = memberString + splitNamesFormat;
                        }
                    }

                    string = string.replace("%MEMBERS%", memberString);
                }

                if (string.contains("%ALLIES%")) {

                    if (playerFaction.getAllies().isEmpty()) {
                        continue;
                    }

                    ChatColor allyColor = ChatColor.valueOf(mainConfig.getString("TAB_LIST.ALLY_COLOR"));
                    String allies = "";
                    for (PlayerFaction ally : playerFaction.getAllies()) {
                        allies = allies + allyColor + ally.getName();

                        if (splitNamesEnabled) {
                            allies = allies + splitNamesFormat;
                        }
                    }

                    string = string.replace("%ALLIES%", allies);
                }

                if (string.contains("%DTR%")) {
                    if (playerFaction.isRaidable()) {
                        string = string.replace("%DTR%", raidableColor + "" + playerFaction.getDeathsTillRaidable());
                    } else {
                        string = string.replace("%DTR%", notRaidableColor + "" + playerFaction.getDeathsTillRaidable());
                    }
                }

                if (string.contains("%DTR_SYMBOL%")) {
                    if (playerFaction.getDeathsTillRaidable().equals(playerFaction.getMaxDeathsTillRaidable())) {
                        string = string.replace("%DTR_SYMBOL%", langConfig.getString(ROOT_SETTINGS + "DTR_SYMBOL.FULL"));
                    } else {
                        if (playerFaction.isFrozen()) {
                            string = string.replace("%DTR_SYMBOL%", langConfig.getString(ROOT_SETTINGS + "DTR_SYMBOL.FROZEN"));
                        } else {
                            string = string.replace("%DTR_SYMBOL%", langConfig.getString(ROOT_SETTINGS + "DTR_SYMBOL.REGENERATING"));
                        }
                    }
                }

                string = string.replace("%BALANCE%", playerFaction.getBalance() + "");
                string = string.replace("%MAX_DTR%", playerFaction.getMaxDeathsTillRaidable() + "");

                if (string.contains("%ANNOUNCEMENT%")) {
                    if (playerFaction.getAnnouncement() == null || !playerFaction.getOnlinePlayers().contains(player)) {
                        continue;
                    }
                    string = string.replace("%ANNOUNCEMENT%", playerFaction.getAnnouncement());
                }

                if (string.contains("%REGEN_TIME%")) {

                    if (!(playerFaction.isFrozen())) {
                        continue;
                    }

                    string = string.replace("%REGEN_TIME%", playerFaction.getFormattedFreezeDuration());
                }

                if (splitNamesEnabled && string.contains(splitNamesFormat)) {
                    string = string.substring(0, string.lastIndexOf(splitNamesFormat));
                }

                toSend.add(string);
            }
        } else {
            SystemFaction systemFaction = (SystemFaction) faction;
            final String ROOT = "FACTION_SHOW.SYSTEM_FACTION.";
            final String ROOT_SETTINGS = ROOT + "SETTINGS.";
            for (String string : langConfig.getStringList(ROOT + "MESSAGE")) {
                string = string.replace("%FACTION%", faction.getName());
                string = string.replace("%COLOR%", systemFaction.getColor() + "");

                if (string.contains("%HOME%")) {
                    if (faction.getHome() == null) {
                        string = string.replace("%HOME%", langConfig.getString(ROOT_SETTINGS + "HOME_PLACEHOLDER"));
                    } else {
                        Location homeLocation = LocationSerialization.deserializeLocation(faction.getHome());
                        string = string.replace("%HOME%", homeLocation.getBlockX() + ", " + homeLocation.getBlockZ());
                    }
                }

                toSend.add(string);
            }
        }

        for (String message : toSend) {
            player.sendMessage(message);
        }
    }
}
