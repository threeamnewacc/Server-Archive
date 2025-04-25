package us.zonix.core.social.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.profile.Profile;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class MessageCommand extends BaseCommand {

    @Command(name = "message", aliases = {"msg", "m", "whisper", "w", "tell"}, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        String[] args = command.getArgs();

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /message <player> <message>");
            return;
        }

        if (player.getName().toLowerCase().equalsIgnoreCase(args[0].toLowerCase())) {
            player.sendMessage(ChatColor.RED + "You cannot message yourself.");
            return;
        }

        if (!profile.getOptions().isReceivePrivateMessages()) {
            player.sendMessage(ChatColor.RED + "You cannot send messages if you cannot receive them.");
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(ChatColor.RED + "That player is not online.");
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

        String message = StringUtils.join(args, ' ', 1, args.length);

        main.getSocialHelper().sendMessage(player, profile, target, targetProfile, message);

    }

}
