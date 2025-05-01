package net.badlion.uhc.commands;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.events.ObjectivesCommandEvent;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ObjectivesCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You cannot use this command.");
			return true;
		}

		Player player = (Player) sender;

		// Hosts/mods can't use this
		UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
		if (!sender.isOp() && uhcPlayer.getState().ordinal() >= UHCPlayer.State.MOD.ordinal()) {
			sender.sendMessage(ChatColor.RED + "You are not allowed to use this command.");
			return true;
		}

		ObjectivesCommandEvent event = new ObjectivesCommandEvent(player);
		BadlionUHC.getInstance().getServer().getPluginManager().callEvent(event);

		if (!event.isSentMessages()) {
			player.sendMessage(ChatColor.RED + "No objectives are set for this UHC.");
		}

		return true;
	}
}
