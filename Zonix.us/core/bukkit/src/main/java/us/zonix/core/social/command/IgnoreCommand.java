package us.zonix.core.social.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.profile.Profile;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class IgnoreCommand extends BaseCommand {

    @Command(name = "ignore", requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        String[] args = command.getArgs();

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /ignore <player>");
            return;
        }

        if (player.getName().toLowerCase().equalsIgnoreCase(args[0].toLowerCase())) {
            player.sendMessage(ChatColor.RED + "You cannot ignore yourself.");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(ChatColor.RED + "That player is not online.");
            return;
        }

        if (profile.getIgnored().contains(target.getUniqueId())) {
            profile.getIgnored().remove(target.getUniqueId());
            player.sendMessage(ChatColor.RED + "You have unignored " + target.getName());
            return;
        }

        profile.getIgnored().add(target.getUniqueId());

        player.sendMessage(ChatColor.RED + "You have ignored " + target.getName());

    }

}
