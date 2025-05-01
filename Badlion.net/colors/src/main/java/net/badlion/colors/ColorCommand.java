package net.badlion.colors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ColorCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		final Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("WHITE")) {
                String name = player.getName();
                player.sendFormattedMessage("{0}Name color changed.", ChatColor.GREEN);
                player.setDisplayName(ChatColor.WHITE + name);
                ColorName.getInstance().commitColorChange(player, "WHITE");
            } else if (args[0].equalsIgnoreCase("BLUE")) {
                String name = player.getName();
                player.sendFormattedMessage("{0}Name color changed.", ChatColor.GREEN);
                player.setDisplayName(ChatColor.BLUE + name);
                ColorName.getInstance().commitColorChange(player, "BLUE");
            } else if (args[0].equalsIgnoreCase("AQUA")) {
                String name = player.getName();
                player.sendFormattedMessage("{0}Name color changed.", ChatColor.GREEN);
                player.setDisplayName(ChatColor.AQUA + name);
                ColorName.getInstance().commitColorChange(player, "AQUA");
            } else if (args[0].equalsIgnoreCase("GOLD")) {
                String name = player.getName();
                player.sendFormattedMessage("{0}Name color changed.", ChatColor.GREEN);
                player.setDisplayName(ChatColor.GOLD + name);
                ColorName.getInstance().commitColorChange(player, "GOLD");
            } else if (args[0].equalsIgnoreCase("GRAY")) {
                String name = player.getName();
                player.sendFormattedMessage("{0}Name color changed.", ChatColor.GREEN);
                player.setDisplayName(ChatColor.GRAY + name);
                ColorName.getInstance().commitColorChange(player, "GRAY");
            } else if (args[0].equalsIgnoreCase("GREEN")) {
                String name = player.getName();
                player.sendFormattedMessage("{0}Name color changed.", ChatColor.GREEN);
                player.setDisplayName(ChatColor.GREEN + name);
                ColorName.getInstance().commitColorChange(player, "GREEN");
            } else if (args[0].equalsIgnoreCase("RED")) {
                String name = player.getName();
                player.sendFormattedMessage("{0}Name color changed.", ChatColor.GREEN);
                player.setDisplayName(ChatColor.RED + name);
                ColorName.getInstance().commitColorChange(player, "RED");
            }else if (args[0].equalsIgnoreCase("YELLOW")) {
                String name = player.getName();
                player.sendFormattedMessage("{0}Name color changed.", ChatColor.GREEN);
                player.setDisplayName(ChatColor.YELLOW + name);
                ColorName.getInstance().commitColorChange(player, "YELLOW");
            } else if (args[0].equalsIgnoreCase("LIGHT_PURPLE")) {
                String name = player.getName();
                player.sendFormattedMessage("{0}Name color changed.", ChatColor.GREEN);
                player.setDisplayName(ChatColor.LIGHT_PURPLE + name);
                ColorName.getInstance().commitColorChange(player, "LIGHT_PURPLE");
            } else if (args[0].equalsIgnoreCase("DARK_AQUA")) {
                String name = player.getName();
                player.sendFormattedMessage("{0}Name color changed.", ChatColor.GREEN);
                player.setDisplayName(ChatColor.DARK_AQUA + name);
                ColorName.getInstance().commitColorChange(player, "DARK_AQUA");
            } else if (args[0].equalsIgnoreCase("DARK_GRAY")) {
                String name = player.getName();
                player.sendFormattedMessage("{0}Name color changed.", ChatColor.GREEN);
                player.setDisplayName(ChatColor.DARK_GRAY + name);
                ColorName.getInstance().commitColorChange(player, "DARK_GRAY");
            } else if (args[0].equalsIgnoreCase("DARK_GREEN")) {
                String name = player.getName();
                player.sendFormattedMessage("{0}Name color changed.", ChatColor.GREEN);
                player.setDisplayName(ChatColor.DARK_GREEN + name);
                ColorName.getInstance().commitColorChange(player, "DARK_GREEN");
            } else if (args[0].equalsIgnoreCase("DARK_PURPLE")) {
                String name = player.getName();
                player.sendFormattedMessage("{0}Name color changed.", ChatColor.GREEN);
                player.setDisplayName(ChatColor.DARK_PURPLE + name);
                ColorName.getInstance().commitColorChange(player, "DARK_PURPLE");
            }

	        player.sendFormattedMessage("{0} color not found.", args[0]);
        } else {
            player.sendFormattedMessage("{0} Change to color.", ChatColor.GREEN + "/color <COLOR> <-");
            player.sendFormattedMessage("{0}Colors: {1}",
                    ChatColor.WHITE,
                    ChatColor.BLUE + "Blue, " + ChatColor.AQUA + "Aqua, " +
                    ChatColor.GOLD + "Gold, " + ChatColor.GRAY + "Gray, " +
                    ChatColor.GREEN + "Green, " + ChatColor.RED + "Red, " +
                    ChatColor.YELLOW + "Yellow, " + ChatColor.WHITE + "White, " +
                    ChatColor.LIGHT_PURPLE + "Light_Purple, " + ChatColor.DARK_AQUA + "Dark_aqua, " +
                    ChatColor.DARK_GRAY + "Dark_gray, " + ChatColor.DARK_GREEN + "Dark_green, " +
                    ChatColor.DARK_PURPLE + "Dark_purple.");
        }

        return true;
	}

}
