package me.devkevin.practice.party.command;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 30/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class PartyAcceptCommand extends PracticeCommand {

    private final Practice plugin = Practice.getInstance();

    @Command(name = "party.accept", aliases = {"p.accept"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.RED + "/party accept <name>");
            return;
        }

        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

        if (party != null) {
            player.sendMessage(CC.RED + "You're already in a party.");
            return;
        }

        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        if (profile.getState() != ProfileState.SPAWN) {
            player.sendMessage(CC.RED + "You can't do that in your current state");
            return;
        }

        Player target = this.plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(String.format(PracticeLang.PLAYER_NOT_FOUND, args[0]));
            return;
        }

        Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());

        if (targetParty == null) {
            player.sendMessage(CC.RED + "That player is not in a party.");
            return;
        }

        if (targetParty.getMembers().size() >= targetParty.getLimit()) {
            player.sendMessage(CC.RED + "That party is full and cannot hold anymore players.");
            return;
        }

        if (!this.plugin.getPartyManager().hasPartyInvite(player.getUniqueId(), targetParty.getLeader())) {
            player.sendMessage(CC.RED + "You have not been invited to that party.");
            return;
        }

        this.plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
    }
}
