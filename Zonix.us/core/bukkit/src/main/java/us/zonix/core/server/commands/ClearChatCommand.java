package us.zonix.core.server.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.CorePlugin;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class ClearChatCommand extends BaseCommand {

    @Command(name = "clearchat", aliases = {"cc"}, requiresPlayer = true, rank = Rank.TRIAL_MOD)
    public void onCommand(CommandArgs command) {

        Player player = command.getPlayer();

        if (Profile.getByUuid(player.getUniqueId()).getRank() == Rank.MEDIA_OWNER) {
            player.sendMessage(ChatColor.RED + "You don't have enough permissions.");
            return;
        }

        this.main.getServer().getScheduler().runTaskAsynchronously(this.main, () -> {
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < 100; i++) {
                builder.append("§8 §8 §1 §3 §3 §7 §8 §r \n");
            }

            String message = builder.toString();

            for (Player online : CorePlugin.getInstance().getServer().getOnlinePlayers()) {
                online.sendMessage(message);
                online.sendMessage(ChatColor.RED + "Chat has been cleared by " + player.getName() + ".");
            }
        });
    }

}
