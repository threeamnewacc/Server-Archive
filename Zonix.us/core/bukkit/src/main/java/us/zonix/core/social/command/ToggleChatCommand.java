package us.zonix.core.social.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class ToggleChatCommand extends BaseCommand {

    @Command(name = "togglechat", aliases = {"tgc", "toggleglobalchat"}, rank = Rank.DEFAULT, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile == null) {
            return;
        }

        profile.setChatEnabled(!profile.isChatEnabled());

        player.sendMessage(ChatColor.GRAY + "You have toggled global chat: " + (profile.isChatEnabled() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
    }

}