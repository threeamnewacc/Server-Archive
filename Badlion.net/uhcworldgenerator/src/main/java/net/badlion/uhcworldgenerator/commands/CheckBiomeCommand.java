package net.badlion.uhcworldgenerator.commands;

import net.badlion.uhcworldgenerator.UHCWorldCheckerTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckBiomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        player.sendMessage(player.getLocation().getBlock().getBiome().name());
        player.sendMessage("" + UHCWorldCheckerTask.isValidBiome(player.getLocation().getBlock().getBiome(), player.getLocation().getBlockX(), player.getLocation().getBlockZ()));

        return true;
    }

}
