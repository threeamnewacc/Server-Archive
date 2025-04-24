package me.devkevin.practice.party.command;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 12/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class PartyLimitCommand extends PracticeCommand {

    @Command(name = "party.setlimit", aliases = {"p.setlimit"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        Party party = Practice.getInstance().getPartyManager().getParty(player.getUniqueId());

        if (party == null) {
            player.sendMessage(CC.RED + "You are not in a party.");
        } else if (args.length == 0) {
            player.sendMessage(CC.RED + "Usage: /party setlimit (amount)");
        } else {

            if (party.getLeader() != player.getUniqueId()) {
                player.sendMessage(CC.RED + "You are not the leader of the party!");
                return;
            }

            try {
                int limit = Integer.parseInt(args[0]);

                CoreProfile playerData = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                if (!player.hasPermission("practice.donors.*")) {
                    if (limit < 2 || limit > 25) {
                        player.sendMessage("");
                        player.sendMessage(CC.RED + "You cannot set your limit party with " + playerData.getGrant().getRank().getColor() + playerData.getGrant().getRank().getName() + CC.RED + " rank.");
                        player.sendMessage(CC.GRAY + "Purchase rank at https://udrop.buycraft.net/ to set the limit to 25.");
                        player.sendMessage("");
                    } else {
                        party.setLimit(limit);
                        player.sendMessage(CC.GREEN + "You have set the party player limit to " + CC.YELLOW + limit + CC.GREEN + " players.");
                    }
                }
                else if (!player.hasPermission("practice.donors.gold")) {
                    if (limit < 2 || limit > 40) {
                        player.sendMessage("");
                        player.sendMessage(CC.RED + "You cannot set your limit party with " + playerData.getGrant().getRank().getColor() + playerData.getGrant().getRank().getName() + CC.RED + " rank.");
                        player.sendMessage(CC.GRAY + "Purchase rank at https://udrop.buycraft.net/ to set the limit to 40.");
                        player.sendMessage("");
                    } else {
                        party.setLimit(limit);
                        player.sendMessage(CC.GREEN + "You have set the party player limit to " + CC.YELLOW + limit + CC.GREEN + " players.");
                    }
                }
                else if (!player.hasPermission("practice.donors.emerald")) {
                    if (limit < 2 || limit > 60) {
                        player.sendMessage("");
                        player.sendMessage(CC.RED + "You cannot set your limit party with " + playerData.getGrant().getRank().getColor() + playerData.getGrant().getRank().getName() + CC.RED + " rank.");
                        player.sendMessage(CC.GRAY + "Purchase rank at https://udrop.buycraft.net/ to set the limit to 60.");
                        player.sendMessage("");
                    } else {
                        party.setLimit(limit);
                        player.sendMessage(CC.GREEN + "You have set the party player limit to " + CC.YELLOW + limit + CC.GREEN + " players.");
                    }
                }
                else if (!player.hasPermission("practice.donors.diamond")) {
                    if (limit < 2 || limit > 80) {
                        player.sendMessage("");
                        player.sendMessage(CC.RED + "You cannot set your limit party with " + playerData.getGrant().getRank().getColor() + playerData.getGrant().getRank().getName() + CC.RED + " rank.");
                        player.sendMessage(CC.GRAY + "Purchase rank at https://udrop.buycraft.net/ to set the limit to 80.");
                        player.sendMessage("");
                    } else {
                        party.setLimit(limit);
                        player.sendMessage(CC.GREEN + "You have set the party player limit to " + CC.YELLOW + limit + CC.GREEN + " players.");
                    }
                }
                else if (!player.hasPermission("practice.donors.udrop")) {
                    if (limit < 2 || limit > 100) {
                        player.sendMessage("");
                        player.sendMessage(CC.RED + "You cannot set your limit party with " + playerData.getGrant().getRank().getColor() + playerData.getGrant().getRank().getName() + CC.RED + " rank.");
                        player.sendMessage(CC.GRAY + "Purchase rank at https://udrop.buycraft.net/ to set the limit to 100.");
                        player.sendMessage("");
                    } else {
                        party.setLimit(limit);
                        player.sendMessage(CC.GREEN + "You have set the party player limit to " + CC.YELLOW + limit + CC.GREEN + " players.");
                    }
                }

            }  catch (NumberFormatException e) {
                player.sendMessage(CC.RED + "That is not a number.");
            }
        }
    }
}
