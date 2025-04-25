package us.zonix.hcfactions.profile.options.command;

import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import org.bukkit.entity.Player;

public class ProfileOptionsCommand extends PluginCommand {
    @Command(name = "options", aliases = "settings")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        player.openInventory(profile.getOptions().getInventory());
    }
}
