package us.zonix.core.misc.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.profile.Profile;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

import java.util.HashMap;
import java.util.UUID;

public class RequestCommand extends BaseCommand {

    private static HashMap<UUID, Long> cooldown = new HashMap<>();

    @Command(name = "request", aliases = {"suggest", "bug"}, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /request [message]");
            return;
        }

        if (cooldown.containsKey(player.getUniqueId()) && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
            player.sendMessage(ChatColor.RED + "Please wait, before sending a request again.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (String arg : args) {
            sb.append(arg).append(" ");
        }

        String reason = sb.toString().trim();

        new BukkitRunnable() {
            public void run() {
                main.getRedisManager().writeRequest(main.getServerId(), Profile.getRankColor(player.getUniqueId()) + player.getName(), reason);
                cooldown.put(player.getUniqueId(), System.currentTimeMillis() + 30 * 1000L);
                player.sendMessage(ChatColor.GREEN + "Your request has been submitted.");
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }

}
