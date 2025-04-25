package us.zonix.core.misc.command.staff;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class StaffChatCommand extends BaseCommand {

    @Command(name = "staffchat", aliases = "sc", rank = Rank.TRIAL_MOD, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            if (this.main.getRedisManager().getStaffChat().contains(player.getUniqueId())) {
                this.main.getRedisManager().getStaffChat().remove(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "You have been removed from the staff chat.");
            }
            else {
                this.main.getRedisManager().getStaffChat().add(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "You have been added to the staff chat.");
            }

            return;
        }

        StringBuilder sb = new StringBuilder();

        for (String arg : args) {
            sb.append(arg).append(" ");
        }

        String message = sb.toString().trim();

        if (message.length() == 0) {
            player.sendMessage(ChatColor.RED + "Please type something.");
            return;
        }

        new BukkitRunnable() {
            public void run() {
                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile != null) {
                    main.getRedisManager().writeStaffChat(player.getName(), profile.getRank(), message);
                }
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }

}
