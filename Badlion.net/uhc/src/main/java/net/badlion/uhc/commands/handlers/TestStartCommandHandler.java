package net.badlion.uhc.commands.handlers;

import net.badlion.common.Configurator;
import net.badlion.uhc.BadlionUHC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestStartCommandHandler {

    public static void handleTestStartCommand(CommandSender sender, String[] args) {
        if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.PRE_START) {
            sender.sendMessage(ChatColor.RED + "Order to start a UHC is Generate world, Config, Generate Spawns and then start.");
            return;
        }

        if (BadlionUHC.getInstance().getBorderShrink() != null && BadlionUHC.getInstance().getBorderShrink()) {
            if (BadlionUHC.getInstance().getBorderShrinkTask() == null) {
                sender.sendMessage(ChatColor.RED + "Please set the border shrink settings before starting with \"/bs\"!");
                return;
            }
        } else if (BadlionUHC.getInstance().getBorderShrink() == null) {
            sender.sendMessage(ChatColor.RED + "Set border shrinking before trying to start a game.");
            return;
        }

        if (!BadlionUHC.getInstance().getConfigurator().checkIfAllOptionsSet()) {
            sender.sendMessage(ChatColor.YELLOW + "The following options are not set currently and are required.");
            for (Configurator.Option option : BadlionUHC.getInstance().getConfigurator().unconfiguredOptions()) {
                sender.sendMessage(option.toString());
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW + "All settings are configured, the UHC can start when you are ready :)");
        }
    }

}
