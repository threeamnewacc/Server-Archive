package us.zonix.core.social.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class SocialSpyCommand extends BaseCommand {

    @Command(name = "socialspy", aliases = {"spy", "sspy"}, rank = Rank.MANAGER, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile == null) {
            return;
        }

        profile.getOptions().setSocialSpy(!profile.getOptions().isSocialSpy());

        player.sendMessage(ChatColor.GRAY + "You have toggled social spy: " + (profile.getOptions().isSocialSpy() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
    }

}
