package me.devkevin.practice.command.requeue;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.queue.QueueType;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright 26/11/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class PlayAgainCommand extends PracticeCommand {
    @Override
    @Command(name = "requeue", aliases = {"playagain"}, inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        Profile profile = getPlugin().getProfileManager().getProfileData(player.getUniqueId());
        if (!getPlugin().getMatchManager().hasPlayAgainRequest(player.getUniqueId())) {
            player.sendMessage(CC.RED + "The time for requeuing has expired.");
            return;
        }

        Kit kit = getPlugin().getMatchManager().getPlayAgainRequestKit(profile.getUuid());
        if (kit == null) {
            player.sendMessage(CC.RED + "Kit not found.");
            return;
        }

        if (profile.isInSpawn()) {
            getPlugin().getQueue().addPlayer(player, profile, kit.getName(), QueueType.UN_RANKED);
        } else if (profile.isFighting() || profile.isSpectating()) {
            Match match = getPlugin().getMatchManager().getMatch(profile.getUuid());

            if (match != null) {
                match.getTeams().forEach(team -> team.killPlayer(profile.getUuid()));
                match.getSpectators().remove(profile.getUuid());
            }

            getPlugin().getProfileManager().sendToSpawn(player);
            getPlugin().getQueue().addPlayer(player, profile, kit.getName(), QueueType.UN_RANKED);
        }

        if (player.getItemInHand().isSimilar(getPlugin().getHotbarItem().getPlayAgain())) {
            player.setItemInHand(new ItemStack(Material.AIR));
        }

        getPlugin().getMatchManager().removePlayAgainRequest(profile.getUuid());
    }
}
