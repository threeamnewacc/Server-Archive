package us.zonix.hcfactions.profile.fight.command;

import org.bukkit.entity.Player;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

public class KillStreakCommand extends PluginCommand {
    @Command(name = "killstreak", aliases = "ks")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        player.sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getStringList("KILL_STREAK.HELP_MENU").toArray(new String[FactionsPlugin.getInstance().getLanguageConfig().getStringList("KILL_STREAK.HELP_MENU").size()]));
    }
}
