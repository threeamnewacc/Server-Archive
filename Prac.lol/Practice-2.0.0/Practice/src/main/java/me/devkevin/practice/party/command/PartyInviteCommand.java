package me.devkevin.practice.party.command;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.Clickable;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Copyright 30/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class PartyInviteCommand extends PracticeCommand {

    private final Practice plugin = Practice.getInstance();

    @Command(name = "party.invite", aliases = {"party.inv", "p.invite", "p.inv"}, inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.RED + "/party invite <player>");
            return;
        }

        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

        if (party == null) {
            player.sendMessage(CC.RED + "You do not have a party.");
            return;
        }

        if (!this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not the leader of your party.");
            return;
        }

        if (party.isOpen()) {
            player.sendMessage(CC.RED + "The party state is Open. You do not need to invite players.");
            return;
        }

        if (party.getMembers().size() >= party.getLimit()) {
            player.sendMessage(CC.RED + "Party size has reached it's limit");
            return;
        }

        Player target = this.plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(CC.RED + target.getName() + " not found.");
            return;
        }


        Profile targetProfile = this.plugin.getProfileManager().getProfileData(target.getUniqueId());

        if (targetProfile.isBusy()) {
            player.sendMessage(LandCore.getInstance().getProfileManager().getProfile(target.getUniqueId()).getGrant().getRank().getColor() + target.getName() + CC.RED + " is currently busy.");
            return;
        }

        if (target.getUniqueId() == player.getUniqueId()) {
            player.sendMessage(CC.RED + "You can't invite yourself.");
            return;
        }

        if (this.plugin.getPartyManager().getParty(target.getUniqueId()) != null) {
            player.sendMessage(CC.RED + "That player is already in a party.");
            return;
        }

        if (this.plugin.getPartyManager().hasPartyInvite(target.getUniqueId(), player.getUniqueId())) {
            player.sendMessage(CC.RED + "That player has already been invited to your party.");
            return;
        }

        this.plugin.getPartyManager().createPartyInvite(player.getUniqueId(), target.getUniqueId());

        party.broadcast(LandCore.getInstance().getProfileManager().getProfile(target.getUniqueId()).getGrant().getRank().getColor() + target.getName() + " " + CC.YELLOW + "has been invited to the party.");

        Clickable partyInvite = new Clickable(CC.DARK_GREEN + "You have been invited to join " + LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName() + CC.DARK_GREEN + "'s party.",
                CC.GRAY + "Click to accept",
                "/party accept " + player.getName());

        partyInvite.sendToPlayer(target);
    }
}
