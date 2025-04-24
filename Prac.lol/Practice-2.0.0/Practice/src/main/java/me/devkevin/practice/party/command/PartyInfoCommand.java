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
public class PartyInfoCommand extends PracticeCommand {

    @Command(name = "party.info", aliases = {"p.info"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Party party = Practice.getInstance().getPartyManager().getParty(player.getUniqueId());

        if (party == null) {
            player.sendMessage(CC.RED + "You are not in a party.");
        } else {
            List<UUID> members = new ArrayList<>(party.getMembers());
            members.remove(party.getLeader());

            StringBuilder builder = new StringBuilder(CC.GOLD + "Members" + CC.GRAY + " (" + CC.RESET + (party.getMembers().size() + CC.GRAY + "): "));
            members.stream().map(Practice.getInstance().getServer()::getPlayer).filter(Objects::nonNull).forEach(member -> builder.append(LandCore.getInstance().getProfileManager().getProfile(member.getUniqueId()).getGrant().getRank().getColor() + member.getName()).append(","));


            String[] information = new String[] {
                    CC.DARK_GRAY + CC.STRIKETHROUGH + "----------------------------------------------------",
                    CC.RED + "Party Information:",
                    CC.GOLD + "Leader: " + LandCore.getInstance().getProfileManager().getProfile(party.getLeader()).getGrant().getRank().getColor() + Practice.getInstance().getServer().getPlayer(party.getLeader()).getName(),
                    CC.GOLD + builder.toString(),
                    CC.GOLD + "Party State: " + CC.GRAY + (party.isOpen() ?
                            CC.GREEN + "Open" :
                            CC.RED + "Locked"),
                    CC.DARK_GRAY + CC.STRIKETHROUGH + "----------------------------------------------------"
            };

            player.sendMessage(information);
        }
    }
}
