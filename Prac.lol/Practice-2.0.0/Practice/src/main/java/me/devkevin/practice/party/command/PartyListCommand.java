package me.devkevin.practice.party.command;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.practice.Practice;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Copyright 12/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class PartyListCommand extends PracticeCommand {

    @Command(name = "party.list", aliases = {"p.list"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Party party = Practice.getInstance().getPartyManager().getParty(player.getUniqueId());

        if (party == null) {
            player.sendMessage(CC.RED + "You are not in a party.");
        } else {
            StringBuilder builder = new StringBuilder(CC.GOLD + "Your party" + CC.GRAY + " (" + CC.RESET + party.getMembers().size() + CC.GRAY + "):\n");

            List<UUID> members = new ArrayList<>(party.getMembers());

            members.remove(party.getLeader());

            builder.append(CC.GREEN).append("Leader: ").append(LandCore.getInstance().getProfileManager().getProfile(party.getLeader()).getGrant().getRank().getColor() + Practice.getInstance().getServer().getPlayer(party.getLeader()).getName()).append("\n");

            members.stream().map(Practice.getInstance().getServer()::getPlayer).filter(Objects::nonNull).forEach(member -> builder.append(LandCore.getInstance().getProfileManager().getProfile(member.getUniqueId()).getGrant().getRank().getColor() + member.getName()).append("\n"));

            player.sendMessage(builder.toString());
        }
    }
}
