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
 * Copyright 31/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class PartyCreateCommand extends PracticeCommand {

    private final Practice plugin = Practice.getInstance();

    @Command(name = "party.create", aliases = {"p.create"}, inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

        if (party != null) {
            player.sendMessage(CC.RED + "You are already in a party.");
            return;
        }

        if (profile.getState() != ProfileState.SPAWN) {
            player.sendMessage(CC.RED + "You need to be in the spawn to execute that command.");
            return;
        }

        this.plugin.getPartyManager().createParty(player);
    }
}
