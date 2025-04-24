package me.devkevin.practice.party.command;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 12/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class PartyJoinCommand extends PracticeCommand {

    @Command(name = "party.join", aliases = {"p.join"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        Profile profile = Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId());
        Party party = Practice.getInstance().getPartyManager().getParty(player.getUniqueId());

        if (party != null) {
            player.sendMessage(CC.RED + "You are already in a party.");
        } else if (args.length == 0) {
            player.sendMessage(CC.RED + "Usage: /party join (name)");
        } else if (profile.getState() != ProfileState.SPAWN) {
            player.sendMessage(CC.RED + "Cannot execute this command in your current state.");
        } else {
            Player target = Practice.getInstance().getServer().getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(CC.RED + target.getName() + " not found.");
                return;
            }

            Party targetParty = Practice.getInstance().getPartyManager().getParty(target.getUniqueId());

            if (targetParty == null || !targetParty.isOpen() || targetParty.getMembers().size() >= targetParty.getLimit()) {
                player.sendMessage(CC.RED + "You can't join this party.");
            } else {
                Practice.getInstance().getPartyManager().joinParty(targetParty.getLeader(), player);
            }
        }
    }
}
