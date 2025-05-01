package net.badlion.gfactions.commands.events;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BloodBowlCommand  implements CommandExecutor {

	private GFactions plugin;
	
	public BloodBowlCommand(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (this.plugin.getBloodBowlManager().isRunning()) {
				player.sendMessage(ChatColor.GREEN + "There is a BloodBowl portal located at " + this.plugin.getBloodBowlManager().getLowerBoundEntry1().getBlockX() + ", "
										   + this.plugin.getBloodBowlManager().getLowerBoundEntry1().getBlockY() + ", "
										   + this.plugin.getBloodBowlManager().getLowerBoundEntry1().getBlockZ());
				player.sendMessage(ChatColor.GREEN + "There is a BloodBowl portal located at " + this.plugin.getBloodBowlManager().getLowerBoundEntry2().getBlockX() + ", "
										   + this.plugin.getBloodBowlManager().getLowerBoundEntry2().getBlockY() + ", "
										   + this.plugin.getBloodBowlManager().getLowerBoundEntry2().getBlockZ());
				player.sendMessage(ChatColor.GREEN + "There is a BloodBowl portal located at " + this.plugin.getBloodBowlManager().getLowerBoundEntry3().getBlockX() + ", "
										   + this.plugin.getBloodBowlManager().getLowerBoundEntry3().getBlockY() + ", "
										   + this.plugin.getBloodBowlManager().getLowerBoundEntry3().getBlockZ());
				player.sendMessage(ChatColor.GREEN + "There is a BloodBowl portal located at " + this.plugin.getBloodBowlManager().getLowerBoundEntry4().getBlockX() + ", "
										   + this.plugin.getBloodBowlManager().getLowerBoundEntry4().getBlockY() + ", "
										   + this.plugin.getBloodBowlManager().getLowerBoundEntry4().getBlockZ());
			} else {
				player.sendMessage(ChatColor.RED + "No BloodBowl at the moment.");
			}
		}
		return true;
	}

}
