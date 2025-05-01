package net.badlion.gberry.commands;

import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;

public class HackerCommand implements CommandExecutor {

	public Gberry gberry;

	public HackerCommand(Gberry gberry) {
		this.gberry = gberry;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			if (args.length < 1) {
				return true;
			}

			/*if (sender.isOp() && args[0].equalsIgnoreCase("timeout")) {
				SpigotConfig.badlionWatchDogTimeOutPeriod = Integer.parseInt(args[1]);
				sender.sendMessage("done");
				return true;
			} else if (sender.isOp() && args[0].equalsIgnoreCase("print")) {
				SpigotConfig.badlionWatchDogPrintTimePeriod = Integer.parseInt(args[1]);
				sender.sendMessage("done");
				return true;
			} else */if (sender.isOp() && args[0].equalsIgnoreCase("extreme")) {
				SimplePluginManager.extremeTesting = !SimplePluginManager.extremeTesting;
				sender.sendMessage("done");
				return true;
			} else if (sender.isOp() && args[0].equalsIgnoreCase("extremelvl")) {
				SimplePluginManager.extremeTestingThreshold = Integer.parseInt(args[1]);
				sender.sendMessage("done");
				return true;
			} else if (sender.isOp() && args[0].equalsIgnoreCase("lag")) {
				try {
					Thread.sleep(Long.parseLong(args[1]));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sender.sendMessage("done");
				return true;
			}

			Player hacker = Bukkit.getPlayerExact(args[0]);
			if (hacker != null) {
				hacker.setDebugGCheat(!hacker.isDebugGCheat());
				sender.sendMessage(ChatColor.YELLOW + "Hack detection is now " + hacker.isDebugGCheat() + " on " + hacker.getName());
			}
		}
		return true;
	}

}
