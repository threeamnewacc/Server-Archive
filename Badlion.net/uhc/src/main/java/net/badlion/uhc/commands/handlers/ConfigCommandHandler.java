package net.badlion.uhc.commands.handlers;

import net.badlion.common.Configurator;
import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.tasks.BorderShrinkTask;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfigCommandHandler {

    public static void sendConfigsToPlayer(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "============UHC Settings==========");
        for (String gamemode : GameModeHandler.GAME_MODES) {
            sender.sendMessage(ChatColor.GOLD + "Game Mode: " + gamemode.replace("_", " ") + " - " + GameModeHandler.gamemodes.get(gamemode).getAuthor());
        }
        sender.sendMessage(ChatColor.GOLD + "World Size - " + (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.RADIUS.name()).getValue() != null ?
                                                                  (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.RADIUS.name()).getValue() * 2 + " x " + (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.RADIUS.name()).getValue() * 2 : "Not Set"));

        for (String line : BadlionUHC.getInstance().getConfigurator().getOptionValues()) {
            // We do this above
            if (line.startsWith(ChatColor.GOLD + "World Radius")) {
                continue;
            }

            sender.sendMessage(line);
        }

        if (BadlionUHC.getInstance().getBorderShrink() != null && BadlionUHC.getInstance().getBorderShrink() && BadlionUHC.getInstance().getBorderShrinkTask() != null) {
            BorderShrinkTask ezTask = BadlionUHC.getInstance().getBorderShrinkTask();
            sender.sendMessage(ChatColor.GOLD + "- Border shrinking will start at " + (ezTask.startTime + ezTask.shrinkInterval) + " minutes and shrink by "
                                  + ezTask.shrinkAmount + " blocks every " + ezTask.shrinkInterval + " minutes until " + ezTask.minimumRadius + "x" + ezTask.minimumRadius);
        }
        sender.sendMessage(ChatColor.AQUA + "Note: If IPvP is disabled, you will be unable to IPvP until PvP is enabled");
    }

    public static void handleConfigCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            ConfigCommandHandler.usage(sender);
            return;
        }

        // Still just list options out
        if (args[0].equalsIgnoreCase("list")) {
            ConfigCommandHandler.sendConfigsToPlayer(sender);
        } else if (args.length == 2) {
            if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED) {
                sender.sendMessage(ChatColor.RED + "You cannot modify config options once the UHC has started");
                return;
            }

            // Try to update the config option value
            Object result;
            try {
	            result = BadlionUHC.getInstance().getConfigurator().updateOption(args[0].toLowerCase(), args[1]);
            } catch (Configurator.NoSuchKeyExistsException e) {
                sender.sendMessage(ChatColor.RED + "Invalid config option.");
                return;
            }

            // Update current scoreboards
            if (args[0].equalsIgnoreCase(BadlionUHC.CONFIG_OPTIONS.SCOREBOARDHEALTHSCALE.name())) {
                for (UHCPlayer uhcp : UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER)) {
                    Player p1 = BadlionUHC.getInstance().getServer().getPlayer(uhcp.getUUID());
                    if (p1 == null) {
                        continue;
                    }

                    UHCPlayerManager.updateHealthScores(p1);
                }
            }

            if (result == null) {
                Gberry.broadcastMessage(ChatColor.AQUA + BadlionUHC.getInstance().getConfigurator().getOption(args[0].toLowerCase()).getNiceName() + " has been set to " + args[1]);
            } else {
                sender.sendMessage(ChatColor.RED + result.toString());
                sender.sendMessage(Gberry.getLineSeparator(ChatColor.YELLOW));
                ConfigCommandHandler.usage(sender);
            }
        } else {
            ConfigCommandHandler.usage(sender);
        }
    }

    public static void usage(CommandSender sender) {
        sender.sendMessage("Usage:");
        sender.sendMessage("/uhc config list - Lists all flags and their values");
        sender.sendMessage("/uhc config <flag> <value> - Sets the specified flag to the given value");
        sender.sendMessage("Example:");
        sender.sendMessage("/uhc config nether false");
    }

}