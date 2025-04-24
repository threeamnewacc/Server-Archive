package me.devkevin.practice.party.command;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.util.Clickable;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright 12/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class PartyOpenCommand extends PracticeCommand {

    @Command(name = "party.open", aliases = {"p.open", "party.lock", "p.lock"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        Party party = Practice.getInstance().getPartyManager().getParty(player.getUniqueId());

        if (party == null) {
            player.sendMessage(CC.RED + "You are not in a party.");
        } else {
            if (party.getLeader() != player.getUniqueId()) {
                player.sendMessage(CC.RED + "You are not the leader of the party!");
                return;
            }

            party.setOpen(!party.isOpen());

            if (party.isOpen()) {
                if (player.hasPermission("practice.donors.*")) {
                    party.setBroadcastTask(new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Player online : Bukkit.getOnlinePlayers()) {
                                Party partyPlayer = Practice.getInstance().getPartyManager().getParty(online.getUniqueId());
                                Player leader = Bukkit.getPlayer(party.getLeader());

                                if (partyPlayer == null) {
                                    Clickable clickable = new Clickable(LandCore.getInstance().getProfileManager().getProfile(leader.getUniqueId()).getGrant().getRank().getColor() + leader.getName() +
                                            CC.RESET + " is hosting a public party" + CC.GOLD + "[Click to join]",
                                            CC.GRAY + "Click to join",
                                    "/party join " + leader.getName());

                                    clickable.sendToPlayer(online);
                                }
                            }
                        }
                    }.runTaskTimerAsynchronously(Practice.getInstance(), 20L, 20L * 60L));
                }
            } else {
                if (party.getBroadcastTask() != null){
                    party.getBroadcastTask().cancel();
                }
            }

            party.broadcast(CC.YELLOW + "Your party is now " + (party.isOpen() ? CC.GREEN + CC.BOLD + "OPEN" : CC.RED + CC.BOLD + "LOCKED"));
        }
    }
}
