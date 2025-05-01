package net.badlion.smellychat.commands;

import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleGlobalCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);

		boolean toggle = !(boolean) chatSettings.getSetting(ChatSettingsManager.Setting.GLOBAL_CHAT);

		chatSettings.setSetting(ChatSettingsManager.Setting.GLOBAL_CHAT, toggle);

		if (toggle) {
			player.sendMessage(ChatColor.YELLOW + "You have enabled " + ChatSettingsManager.Setting.GLOBAL_CHAT.getName() + ".");
		} else {
			player.sendMessage(ChatColor.YELLOW + "You have disabled " + ChatSettingsManager.Setting.GLOBAL_CHAT.getName() + ".");
		}

		return true;
	}

}
