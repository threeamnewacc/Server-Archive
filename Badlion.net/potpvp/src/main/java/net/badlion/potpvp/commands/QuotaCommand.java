package net.badlion.potpvp.commands;

import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.RankedLeftManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class QuotaCommand extends GCommandExecutor {

	public QuotaCommand() {
        super(0);
	}

    @Override
    public void onGroupCommand(Command command, String label, final String[] args) {
        if (this.player.hasPermission(PotPvP.getUnlimitedRankedPermission())) {
	        this.player.sendMessage(ChatColor.GREEN + "You have unlimited ranked matches as a donator! :)");
            return;
        }

	    this.player.sendMessage(ChatColor.GREEN + "You have " + RankedLeftManager.getNumberOfRankedMatchesLeft(player)
			    + " matches left today.  Quotas reset at 12:00 AM EST.");
	}

    public void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "/quota");
    }

}
