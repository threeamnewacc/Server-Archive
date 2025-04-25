package us.zonix.hcfactions.misc.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.command.CommandArgs;

public class CobbleCommand extends PluginCommand {

    @Command(name = "cobble", inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        Profile profile = Profile.getByPlayer(player);

        if (profile == null) {
            return;
        }

        profile.setCobble(!profile.isCobble());

        player.sendMessage(ChatColor.YELLOW + "You have toggled cobble " + (profile.isCobble() ? "on" : "off") + ".");

    }


}
