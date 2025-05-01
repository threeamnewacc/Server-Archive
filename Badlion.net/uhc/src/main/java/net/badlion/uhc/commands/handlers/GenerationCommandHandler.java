package net.badlion.uhc.commands.handlers;

import net.badlion.uhc.BadlionUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GenerationCommandHandler {

    public static void handleGenerateWorldCommand(CommandSender sender, String[] args) {
        /*if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.WORLD_GENERATION) {
            p.sendMessage(ChatColor.YELLOW + "The UHC world has already been generated!");
            return;
        }*/

        if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.RADIUS.name()).getValue() != null) {
            GenerationCommandHandler.setupWorld();
        }

        sender.sendMessage(ChatColor.GREEN + "Starting world generation.");
    }

    public static void setupWorld() {
        //String shape = (String) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.SHAPE.name()).getValue();
        Integer radius = (Integer) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.RADIUS.name()).getValue();

        // Add 2 to radius so players can't abuse the teleport glitch
        BadlionUHC.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb shape square");
        BadlionUHC.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb uhcworld set " + (radius) + " 0 0");
        BadlionUHC.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb uhcworld_nether set " + (radius / 8) + " 0 0");
        BadlionUHC.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb uhcworld fill 1000");
        BadlionUHC.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb fill confirm");
        BadlionUHC.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb uhcworld_nether fill 1000");
        BadlionUHC.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb fill confirm");
    }

}
