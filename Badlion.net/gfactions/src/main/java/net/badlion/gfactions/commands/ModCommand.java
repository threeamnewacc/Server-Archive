package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ModCommand implements CommandExecutor {

	private GFactions plugin;

	public ModCommand(GFactions plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
		if (strings.length != 1) {
			return false;
		}

		if (s.equalsIgnoreCase("mod")) { // Mod
			this.plugin.getServer().dispatchCommand(sender, "user " + strings[0] + " addperm bm.mute");
			this.plugin.getServer().dispatchCommand(sender, "user " + strings[0] + " addperm bm.unmute");
			this.plugin.getServer().dispatchCommand(sender, "user " + strings[0] + " addperm GFactions.mod");
			this.plugin.getServer().dispatchCommand(sender, "user " + strings[0] + " addperm badlion.staff");
			this.plugin.getServer().dispatchCommand(sender, "user " + strings[0] + " meta prefix &2[ChatMod]");
			sender.sendMessage(ChatColor.YELLOW + "Successfully modded " + strings[0]);
		} else { // Demod
			this.plugin.getServer().dispatchCommand(sender, "user " + strings[0] + " rmperm bm.mute");
			this.plugin.getServer().dispatchCommand(sender, "user " + strings[0] + " rmperm bm.unmute");
			this.plugin.getServer().dispatchCommand(sender, "user " + strings[0] + " rmperm GFactions.mod");
			this.plugin.getServer().dispatchCommand(sender, "user " + strings[0] + " rmperm badlion.staff");
			this.plugin.getServer().dispatchCommand(sender, "user " + strings[0] + " meta prefix");
			sender.sendMessage(ChatColor.YELLOW + "Successfully demodded " + strings[0]);
		}

		return true;
	}

}
