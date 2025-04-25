package us.zonix.core.misc.command.staff;

import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class StaffModeCommand extends BaseCommand {

    @Command(name = "staffmode", aliases = {"modmode", "sm", "mm", "staff"}, rank = Rank.TRIAL_MOD, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        this.main.getStaffModeManager().toggleStaffMode(player);
    }

}
