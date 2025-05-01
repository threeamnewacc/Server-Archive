package net.badlion.uhc.commands;

import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.tasks.BorderShrinkTask;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BorderShrinkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender.isOp() || ((Player) sender).getUniqueId().equals(BadlionUHC.getInstance().getHost().getUUID())) {
            if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.STARTED) {
                if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.WORLD_GENERATION) {
                    sender.sendMessage(ChatColor.RED + "You must first generate a world before configuring the border shrink settings");
                    return true;
                }

                if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.RADIUS.name()).getValue() == null) {
                    sender.sendMessage(ChatColor.RED + "Border radius must be set in config first before turning on border shrinking.");
                    return true;
                }

                if (args.length >= 4) {
                    try {
                        int startTime = Integer.valueOf(args[0]);
                        int shrinkInterval = Integer.valueOf(args[1]);
                        int shrinkAmount = Integer.valueOf(args[2]);
                        int minimumRadius = Integer.valueOf(args[3]);
                        int extraShrinkTime = -1;
                        int extraShrinkTime2 = -1;

                        if (args.length >= 5) {
                            extraShrinkTime = Integer.valueOf(args[4]);
                        }

                        if (args.length == 6) {
                            extraShrinkTime2 = Integer.valueOf(args[5]);
                        }

                        // Check for bad numbers
                        if (startTime < 0 || startTime > 270) {
                            sender.sendMessage(ChatColor.RED + "Start time must be between 1 minute and 270 minutes");
                            return true;
                        }
                        if (shrinkInterval < 1 || shrinkInterval > 60) {
                            sender.sendMessage(ChatColor.RED + "Shrink interval must be between 1 minute and 60 minutes");
                            return true;
                        }
                        if (shrinkAmount < 10 || shrinkInterval > 500) {
                            sender.sendMessage(ChatColor.RED + "Shrink amount must be between 10 blocks and 500 blocks");
                            return true;
                        }

                        if (minimumRadius < 25 || minimumRadius > (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.RADIUS.name()).getValue()) {
                            sender.sendMessage(ChatColor.RED + "The minimum border radius must be between 100 blocks and the starting radius ("
                                    + BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.RADIUS.name()).getValue() + " blocks)");
                            return true;
                        }

                        BadlionUHC.getInstance().setBorderShrinkTask(new BorderShrinkTask(startTime, shrinkInterval, shrinkAmount, minimumRadius, extraShrinkTime, extraShrinkTime2));
                        sender.sendMessage(ChatColor.GREEN + "Set border shrinking to start at " + startTime + " and shrink by "
                                + shrinkAmount + " blocks every " + shrinkInterval + " minutes until " + minimumRadius + "x" + minimumRadius);

                    } catch (NumberFormatException e) {
                        this.helpMessage(sender);
                    }
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("on")) {
                        if (BadlionUHC.getInstance().getBorderShrink() != null && BadlionUHC.getInstance().getBorderShrink()) {
                            sender.sendMessage(ChatColor.RED + "Border shrinking is already on");
                        } else {
                            BadlionUHC.getInstance().setBorderShrink(true);
                            Gberry.broadcastMessage(ChatColor.AQUA + "Border shrinking has been turned on");
                        }
                    } else if (args[0].equalsIgnoreCase("off")) {
                        if (BadlionUHC.getInstance().getBorderShrink() != null && BadlionUHC.getInstance().getBorderShrink()) {
                            BadlionUHC.getInstance().setBorderShrink(false);
                            Gberry.broadcastMessage(ChatColor.AQUA + "Border shrinking has been turned off");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Border shrinking is already off");
                        }
                    } else {
                        this.helpMessage(sender);
                    }
                } else {
                    this.helpMessage(sender);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You cannot use that command after the UHC has started");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
        }
        return true;
    }

    public void helpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "===Border Shrink Commands===");
        sender.sendMessage(ChatColor.GOLD + "/bs on:off - Enable/Disable border shrinking");
        sender.sendMessage(ChatColor.GOLD + "/bs [start_time] [shrink_interval] [shrink_amount] [minimum_radius]");
        sender.sendMessage(ChatColor.GOLD + "Example: (All times are in minutes) /bs 10 2 25 100 ");
        sender.sendMessage(ChatColor.GOLD + "Starts shrinking the border 10 minutes in by 25 blocks every 2 minutes until it reaches a radius of 100");
    }

}
