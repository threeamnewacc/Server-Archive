package us.zonix.core.server.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class SilenceChatCommand extends BaseCommand {

    @Command(name = "silence", aliases = {"mutechat", "silencechat"}, requiresPlayer = true, rank = Rank.TRIAL_MOD)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (Profile.getByUuid(player.getUniqueId()).getRank() == Rank.MEDIA_OWNER) {
            player.sendMessage(ChatColor.RED + "You don't have enough permissions.");
            return;
        }

        this.main.getRedisManager().setChatSilenced(!this.main.getRedisManager().isChatSilenced());

        Bukkit.broadcastMessage(ChatColor.RED + (this.main.getRedisManager().isChatSilenced() ? "Chat has been silenced by " + player.getName() + "." : "Chat has been unsilenced by " + player.getName() + "."));
    }

}
