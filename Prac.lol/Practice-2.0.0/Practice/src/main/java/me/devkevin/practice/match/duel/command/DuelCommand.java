package me.devkevin.practice.match.duel.command;

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
 * Copyright 09/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class  DuelCommand extends PracticeCommand {

    private final Practice plugin = Practice.getInstance();

    @Command(name = "duel", inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            player.sendMessage(CC.RED + "/duel <player>");
            return;
        }

        if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
            player.sendMessage(CC.RED + "That player is busy.");
            return;
        }

        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
        Profile targetProfile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        if (profile.getState() != ProfileState.SPAWN && profile.getState() != ProfileState.PARTY) {
            player.sendMessage(CC.RED + "You can't execute that command in your current state.");
            return;
        }

        Player target = this.plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(String.format(PracticeLang.PLAYER_NOT_FOUND, args[0]));
            return;
        }

        if (this.plugin.getTournamentManager().getTournament(target.getUniqueId()) != null) {
            player.sendMessage(CC.RED + "That player is busy.");
            return;
        }
        if (this.plugin.getTournamentManager().isInTournament(target.getUniqueId())) {
            player.sendMessage(CC.RED + "That player is busy.");
            return;
        }

        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

        if ((party != null && this.plugin.getPartyManager().isInParty(target.getUniqueId(), party)) || player.getName().equals(target.getName())) {
            player.sendMessage(CC.RED + "You can't duel yourself.");
            return;
        }

        if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not the leader of the party.");
            return;
        }

        if (targetProfile.isInQueue() || targetProfile.isFighting()) {
            player.sendMessage(CC.RED + "That player is busy.");
            return;
        }

        if (!targetProfile.getOptions().isDuelRequests()) {
            player.sendMessage(CC.RED + "That player is not accepting duel request.");
            return;
        }

        Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
        if (party == null && targetParty != null) {
            player.sendMessage(CC.RED + "That player is already in a party.");
            return;
        }

        if (party != null && targetParty == null) {
            player.sendMessage(CC.RED + "You are already in a party.");
            return;
        }

        profile.setDuelSelecting(target.getUniqueId());
        player.openInventory(plugin.getDuelMenu().getDuelMenu().getCurrentPage());
    }
}
