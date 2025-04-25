package us.zonix.core.social.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.profile.Profile;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class ReplyCommand extends BaseCommand {

    @Command(name = "reply", aliases = {"r"}, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        String[] args = command.getArgs();

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /reply <message>");
            return;
        }

        if (profile == null) {
            return;
        }

        if (profile.getLastMessaged() == null) {
            player.sendMessage(ChatColor.RED + "You don't have any message to reply back.");
            return;
        }

        if (!profile.getOptions().isReceivePrivateMessages()) {
            player.sendMessage(ChatColor.RED + "You cannot send messages if you cannot receive them.");
        }

        Player target = Bukkit.getPlayer(profile.getLastMessaged());

        if (target == null) {
            player.sendMessage(ChatColor.RED + "That player is no longer online.");
            return;
        }

        if (profile.getIgnored().contains(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can't send a message to an ignored player.");
            return;
        }

        Profile targetProfile = Profile.getByUuid(target.getUniqueId());

        if (targetProfile == null) {
            return;
        }

        if (!targetProfile.getOptions().isReceivePrivateMessages()) {
            player.sendMessage(ChatColor.RED + "That player is not receiving private messages.");
            return;
        }

        if (targetProfile.getIgnored().contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "That player has ignored you.");
            return;
        }

        String message = StringUtils.join(args, ' ', 0, args.length);

        main.getSocialHelper().sendMessage(player, profile, target, targetProfile, message);
    }

}
