package net.badlion.uhcworldgenerator.commands;

import net.badlion.uhcworldgenerator.UHCWorldCheckerTask;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UHCWorldCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        World world = null;
        if (args.length == 1) {
            world = Bukkit.getServer().getWorld(args[0]);
            if (world == null) {
                WorldCreator wc = new WorldCreator(args[0]);
                world = Bukkit.getServer().createWorld(wc);
                Bukkit.getServer().getWorlds().add(world);
            }
        } else if (UHCWorldCheckerTask.isGenerating) {
            world = UHCWorldCheckerTask.world;
        }

        if (world != null) {
            Player player = (Player) sender;
            player.setGameMode(GameMode.CREATIVE);
            player.teleport(world.getHighestBlockAt(0, 0).getLocation().add(0, 5, 0));
        } else {
            sender.sendMessage(ChatColor.RED + "World does not exist.");
        }

        return true;
    }

}
