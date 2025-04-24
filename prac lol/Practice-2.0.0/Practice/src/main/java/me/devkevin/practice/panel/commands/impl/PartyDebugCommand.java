package me.devkevin.practice.panel.commands.impl;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 15/02/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class PartyDebugCommand extends PracticeCommand {

    @Override @Command(name = "partydebug", permission = Rank.ADMIN, usage = "&cUsage: /partydebug <player>")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.translate(command.getCommand().getUsage()));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (args.length == 1) {
            if (target != null) {
                Profile profile = getPlugin().getProfileManager().getProfileData(target.getUniqueId());

                if (!profile.isInParty()) {
                    player.sendMessage(CC.translate("&c" + target.getName() + " is not in a Party."));
                    return;
                }

                Party party = getPlugin().getPartyManager().getParty(target.getUniqueId());

                final List<String> partyMembers = new ArrayList<>();
                party.getMembers().forEach(mem -> partyMembers.add(Bukkit.getPlayer(mem).getName()));

                player.sendMessage(CC.CHAT_BAR);
                player.sendMessage(CC.translate("&6&lParty Debug"));
                player.sendMessage(CC.CHAT_BAR);
                player.sendMessage(CC.translate(" &e&l&fLeader: &f" + Bukkit.getPlayer(party.getLeader()).getName()));
                player.sendMessage(CC.translate(" &e&l&fState: " + (party.isOpen() ? "&apublic" : "&cprivate")));
                player.sendMessage(CC.translate(" &e&l&fSize: &f" + party.getMembers().size() + "&7/&f" + party.getLimit()));
                player.sendMessage(CC.translate(" &e&l&fMembers: &f" + partyMembers));

                player.sendMessage(CC.CHAT_BAR);
            } else {
                player.sendMessage(CC.translate("&cPlayer not found."));
            }
        }
    }
}
