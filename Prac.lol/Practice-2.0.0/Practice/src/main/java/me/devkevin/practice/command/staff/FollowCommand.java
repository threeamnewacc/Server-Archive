package me.devkevin.practice.command.staff;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Copyright 12/02/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class FollowCommand extends PracticeCommand {

    @Override @Command(name = "follow", permission = Rank.TRIAL_MOD, usage = "&cUsage: /follow <player>")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.translate(command.getCommand().getUsage()));
            return;
        }

        Profile profile = getPlugin().getProfileManager().getProfileData(player.getUniqueId());

        if (!profile.isInStaffMode()) {
            player.sendMessage(CC.translate("&cYou must be on staff mode. do /staff."));
            return;
        }

        if (args.length < 1) {
            if (profile.isFollowing() && profile.getFollowingId() != null) {
                if (profile.getFollowingId() != null) {
                    if (profile.getFollowingId() == null) {
                        player.sendMessage(CC.translate("&cYou are currently following nobody."));
                        return;
                    }

                    profile.setFollowing(false);
                    player.sendMessage(CC.translate("&cStopped following " + LandCore.getInstance().getProfileManager().getProfile(Bukkit.getPlayer(profile.getFollowingId()).getUniqueId()).getGrant().getRank().getColor() + Bukkit.getPlayer(profile.getFollowingId()).getName() + "&c!"));
                    profile.setFollowingId(null);
                    profile.setState(ProfileState.STAFF);
                }
            } else {
                player.sendMessage(CC.translate("&cYou are currently following nobody."));
            }
            return;
        }

        Player target = getPlugin().getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(String.format(PracticeLang.PLAYER_NOT_FOUND, args[0]));
            return;
        }

        if (target == player) {
            player.sendMessage(CC.translate("&cYou cannot follow yourself!"));
            return;
        }

        if (profile.getFollowingId() != null && profile.getFollowingId().equals(target.getUniqueId())) {
            player.sendMessage(CC.translate("&cYou are already following this player."));
            return;
        }

        profile.setFollowing(true);
        profile.setFollowingId(target.getUniqueId());
        player.sendMessage(CC.translate("&aYou started following " + LandCore.getInstance().getProfileManager().getProfile(target.getUniqueId()).getGrant().getRank().getColor() + target.getName() + "&a."));
    }
}
