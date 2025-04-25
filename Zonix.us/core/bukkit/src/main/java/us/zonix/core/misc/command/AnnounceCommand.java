package us.zonix.core.misc.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.server.ServerType;
import us.zonix.core.util.CC;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class AnnounceCommand extends BaseCommand {

    @Command(name = "announce", requiresPlayer = true, rank = Rank.PLATINUM)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        String serverId = CorePlugin.getInstance().getServerId();

        if (main.getServerType() != ServerType.SURVIVAL_GAMES) {
            player.sendMessage(CC.RED + "You cannot announce this type of game.");
            return;
        }

        Long last = CorePlugin.getInstance().getLastAnnounce();

        if (last == null || System.currentTimeMillis() >= last + 30000) {
            new BukkitRunnable() {
                public void run() {
                    String name = Profile.getRankColor(player.getUniqueId()) + player.getName();
                    main.getRedisManager().writeAnnounce(name, "Survival Games", serverId);
                    CorePlugin.getInstance().setLastAnnounce(System.currentTimeMillis());
                }
            }.runTaskAsynchronously(CorePlugin.getInstance());
        }
        else {
            player.sendMessage(ChatColor.RED + "You must wait before this server can be announced again.");
        }
    }

}
