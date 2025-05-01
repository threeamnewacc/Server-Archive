package net.badlion.gberry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DefaultListCommandHandler extends AbstractListCommandHandler {

	@Override
	public void handleListCommand(Player player) {
		player.sendMessage(Gberry.getLineSeparator(ChatColor.RED));
		player.sendMessage(ChatColor.GREEN + "Players Online [" + ChatColor.GOLD
				+ Gberry.plugin.getServer().getOnlinePlayers().size()
				+ "/" + Bukkit.getServer().getMaxPlayers() + ChatColor.GREEN + "]:");
		player.sendMessage(ChatColor.DARK_RED + "[Administrators]: " + this.admins);
		player.sendMessage(ChatColor.DARK_PURPLE + "[Managers]: " + this.managers);
		player.sendMessage(ChatColor.DARK_PURPLE + "[Senior Moderators]: " + this.seniorMods);
		player.sendMessage(ChatColor.DARK_AQUA + "[" + Gberry.serverType.getName() + " Moderators]: " + this.mods);
		player.sendMessage(ChatColor.DARK_AQUA + "[Other Staff]: " + this.otherStaff);
		player.sendMessage(ChatColor.DARK_GREEN + "[Trial Moderators]: " + this.trials);
		player.sendMessage(ChatColor.YELLOW + "[Famous" + ChatColor.BOLD + "+" + ChatColor.RESET + ChatColor.YELLOW + " Players]: " + this.famousPlus);
		player.sendMessage(ChatColor.YELLOW + "[Famous Players]: " + this.famous);
		player.sendMessage(ChatColor.GOLD + "[Lion]: " + this.lions);
		player.sendMessage(ChatColor.GOLD + "[Donator" + ChatColor.BOLD + "+" + ChatColor.RESET + ChatColor.GOLD + "]: " + this.donatorPlus);
		player.sendMessage(ChatColor.GOLD + "[Donators]: " + this.donators);
		player.sendMessage(Gberry.getLineSeparator(ChatColor.RED));
	}

}
