package us.zonix.core.misc.command.staff;

import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.BungeeUtil;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class VanishCommand extends BaseCommand {

    @Command(name = "vanish", aliases = {"unvanish", "v", "hide"}, rank = Rank.TRIAL_MOD, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if(this.main.getStaffModeManager().getVanishedPlayers().contains(player.getUniqueId())) {
            this.main.getStaffModeManager().unvanishPlayer(player);
        } else {
            this.main.getStaffModeManager().vanishPlayer(player);
        }
    }
}
