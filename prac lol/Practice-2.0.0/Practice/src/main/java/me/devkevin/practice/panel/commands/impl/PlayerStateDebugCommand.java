package me.devkevin.practice.panel.commands.impl;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Copyright 15/02/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class PlayerStateDebugCommand extends PracticeCommand {

    @Override @Command(name = "playerstatedebug", aliases = {"pdebug", "playerstate"}, permission = Rank.ADMIN, usage = "&cUsage: /pdebug <player>")
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

                player.sendMessage(CC.CHAT_BAR);
                player.sendMessage(CC.translate("&6&lPlayer State Debug"));
                player.sendMessage(CC.CHAT_BAR);
                player.sendMessage(CC.translate(" &6&l&fName: &f" + target.getName()));
                player.sendMessage(CC.translate(" &6&l&fState: &f" + profile.getState()));
                player.sendMessage(CC.translate(" &6&l&fWorld: &f" + target.getWorld().getName()));
                player.sendMessage(CC.translate(" &6&l&fisFollowing: &f" + profile.isFollowing()));
                player.sendMessage(CC.translate(" &6&l&fisSpectating: &f" + profile.isSpectating()));
                player.sendMessage(CC.translate(" &6&l&fLocation:"));
                player.sendMessage(CC.translate("  &3 &fX &f" + Math.round(target.getLocation().getX())));
                player.sendMessage(CC.translate("  &3 &fY &f" + Math.round(target.getLocation().getY())));
                player.sendMessage(CC.translate("  &3 &fZ &f" + Math.round(target.getLocation().getZ())));
                player.sendMessage(CC.translate(" "));
                if (!profile.getCachedPlayer().isEmpty()) {
                    player.sendMessage(CC.translate(" &6&l&fCached Player: &f" + profile.getCachedPlayer().get(target).getName()));
                } else {
                    player.sendMessage(CC.translate(" &6&l&fCached Player: &f" + profile.getCachedPlayer().get(target)));
                }
                player.sendMessage(CC.translate(" &6&l&fCachePlayerMap values: &f" + profile.getCachedPlayer().values()));
                player.sendMessage(CC.CHAT_BAR);
            } else {
                player.sendMessage(CC.translate("&cPlayer not found."));
            }
        }
    }
}
