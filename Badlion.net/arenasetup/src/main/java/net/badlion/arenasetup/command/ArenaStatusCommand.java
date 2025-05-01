package net.badlion.arenasetup.command;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenasetup.ArenaSetup;
import net.badlion.arenasetup.SetupSession;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class ArenaStatusCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			SetupSession setupSession = ArenaSetup.getInstance().getSetupSessionMap().get(player.getUniqueId());
			if (setupSession == null) {
				player.sendMessage(ChatColor.RED + "ERROR: You are not setting up an arena!");
				return false;
			}
			player.sendMessage("Arena Setup Status");
			player.sendMessage(ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + "--------");
			player.sendMessage(ChatColor.GOLD + "Name: " + ChatColor.GREEN + setupSession.getArenaName());
			player.sendMessage(ChatColor.GOLD + "Warp1: " + (setupSession.getWarp1() != null ? ChatColor.GREEN + "SET" : ChatColor.RED + "Not Set"));
			player.sendMessage(ChatColor.GOLD + "Warp2: " + (setupSession.getWarp2() != null ? ChatColor.GREEN + "SET" : ChatColor.RED + "Not Set"));

			if (setupSession.getSelection() == null) {
				player.sendMessage("Arena Selection: Not set");
				return false;
			}
			int air = 0;
			int blocks = 0;
			for (Block block : setupSession.getSelection().getAllBlocks()) {
				if (block.getType().equals(Material.AIR)) {
					air++;
				} else {
					blocks++;
				}
			}
			player.sendMessage(ChatColor.GOLD + "Arena Selection: " + ChatColor.GREEN + air + " air blocks, and " + blocks + " solid blocks.");

			if (setupSession.getTypes().isEmpty()) {
				player.sendMessage(ChatColor.GOLD + "Arena Types: " + ChatColor.RED + "Not Set");
			} else {
				String types = setupSession.getTypes().stream().map(KitRuleSet::getName).collect(Collectors.joining(", "));
				player.sendMessage(ChatColor.GOLD + "Arena Types: " + ChatColor.GREEN + types);
			}
			player.sendMessage(ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + "--------");
			player.sendMessage(ChatColor.GOLD + "Arena Valid: " + (setupSession.isValid() ? ChatColor.GREEN + "YES" : ChatColor.RED + "NO"));
		}
		return false;
	}
}
