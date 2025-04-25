package us.zonix.core.social.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class ToggleMessagesCommand extends BaseCommand {

    @Command(name = "tpm", aliases = {"tdm", "togglemessages", "togglepm"}, rank = Rank.DEFAULT, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile == null) {
            return;
        }

        profile.getOptions().setReceivePrivateMessages(!profile.getOptions().isReceivePrivateMessages());

        player.sendMessage(ChatColor.GRAY + "You have toggled private messages: " + (profile.getOptions().isReceivePrivateMessages() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
    }

}