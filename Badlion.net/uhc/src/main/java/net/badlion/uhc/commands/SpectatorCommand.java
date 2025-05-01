package net.badlion.uhc.commands;

import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SpectatorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String s, final String[] args) {
        if (sender instanceof Player) {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("add")) {
                    UUID uuid = BadlionUHC.getInstance().getUUID(args[1]);
                    if (uuid != null) {
                        UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(uuid);
                        if (uhcPlayer.getState() == UHCPlayer.State.DEAD) {
                            UHCPlayerManager.updateUHCPlayerState(uuid, UHCPlayer.State.SPEC);
                        } else if (uhcPlayer.getState() == UHCPlayer.State.PLAYER) {
                            UHCPlayerManager.updateUHCPlayerState(uuid, UHCPlayer.State.SPEC_IN_GAME);
                        } else {
                            sender.sendMessage(ChatColor.RED + "You cannot make that player a spectator or he/she is already one.");
                            return true;
                        }

                        sender.sendMessage(ChatColor.GREEN + "Added " + args[1] + " as a spectator.");
					} else {
                        // Offline lookup
                        BadlionUHC.getInstance().getServer().getScheduler().runTaskAsynchronously(BadlionUHC.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                final UUID uuid = Gberry.getOfflineUUID(args[1]);
                                if (uuid == null) {
                                    sender.sendMessage(ChatColor.RED + "Could not find offline player to mod.");
                                    return;
                                }

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        SpectatorCommand.handleSpectatorAddOfflineCommand(args, uuid);
                                        sender.sendMessage(ChatColor.GREEN + "Added " + args[1] + " as a spectator (offline)");
                                    }
                                }.runTask(BadlionUHC.getInstance());
                            }
                        });
					}
                } else if (args[0].equalsIgnoreCase("remove")) {
                    UUID uuid = BadlionUHC.getInstance().getUUID(args[1]);
                    if (uuid != null) {
                        UHCPlayerManager.removeUHCPlayer(uuid);
                        Player p = Bukkit.getPlayerExact(args[1]);
                        if (p != null) {
                            p.kickPlayer("You are no longer a spectator, please rejoin.");
                        }
                        sender.sendMessage(ChatColor.GREEN + "Removed " + args[1] + " as a spectator.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "No spectator with name " + args[1] + " has been found");
                    }
                } else {
                    this.helpMessage(sender);
                }
            } else {
                this.helpMessage(sender);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
        }
        return true;
    }

    private static boolean handleSpectatorAddOfflineCommand(String[] args, UUID specUUID) {
        UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(specUUID);
        if (uhcPlayer != null && uhcPlayer.getState() == UHCPlayer.State.SPEC) {
            return false;
        }

        if (uhcPlayer == null) {
            UHCPlayerManager.addNewUHCPlayer(specUUID, args[1], UHCPlayer.State.SPEC);
        } else {
            UHCPlayerManager.updateUHCPlayerState(specUUID, UHCPlayer.State.SPEC);
        }

        return true;
    }

    public void helpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "===Spectator Commands===");
        sender.sendMessage(ChatColor.GOLD + "Current number of spectators: " + UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.SPEC).size());
        sender.sendMessage(ChatColor.GOLD + "/spec add - Adds a player to spectator list");
        sender.sendMessage(ChatColor.GOLD + "/spec remove - Removes a player from spectator list");
    }
}
