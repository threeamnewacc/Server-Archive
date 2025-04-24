package me.devkevin.practice.party.command;

import club.inverted.chatcolor.CC;
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
public class PartyKickCommand extends PracticeCommand {

    @Command(name = "party.kick", aliases = {"p.kick"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        Party party = Practice.getInstance().getPartyManager().getParty(player.getUniqueId());

        if (party == null) {
            player.sendMessage(CC.RED + "You are not in a party.");
        } else if (args.length == 0) {
            player.sendMessage(CC.RED + "Usage: /party kick (name)");
        } else {
            if (party.getLeader() != player.getUniqueId()) {
                player.sendMessage(CC.RED + "You are not the leader of the party!");
                return;
            }

            Player target = Practice.getInstance().getServer().getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(CC.RED + target.getName() + " not found.");
                return;
            }

            Party targetParty = Practice.getInstance().getPartyManager().getParty(player.getUniqueId());

            if (targetParty == null || targetParty.getLeader() != party.getLeader()) {
                player.sendMessage(CC.RED + "That player is not in your party.");
            } else {
                Practice.getInstance().getPartyManager().leaveParty(target);
            }
        }
    }
}
