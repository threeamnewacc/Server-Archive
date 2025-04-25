package us.zonix.core.misc.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.profile.Profile;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

import java.util.HashMap;
import java.util.UUID;

public class ReportCommand extends BaseCommand {

    private static HashMap<UUID, Long> cooldown = new HashMap<>();

    @Command(name = "report", requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /report [player] [reason]");
            return;
        }

        if (cooldown.containsKey(player.getUniqueId()) && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
            player.sendMessage(ChatColor.RED + "Please wait, before sending a report again.");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(ChatColor.RED + "That player is not online.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        String reason = sb.toString().trim();

        new BukkitRunnable() {
            public void run() {
                main.getRedisManager().writeReport(main.getServerId(), Profile.getRankColor(player.getUniqueId()) + player.getName(), Profile.getRankColor(target.getUniqueId()) + target.getName(), reason);
                cooldown.put(player.getUniqueId(), System.currentTimeMillis() + 30 * 1000L);
                player.sendMessage(ChatColor.GREEN + "Your report has been submitted.");
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }

}
