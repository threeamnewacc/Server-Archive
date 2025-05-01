package net.badlion.uhc.commands;

import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
			if (uhcPlayer.getState().ordinal() >= UHCPlayer.State.SPEC.ordinal() && (player.hasPermission("badlion.famous")
                    || player.hasPermission("badlion.uhctrial") || player.hasPermission("badlion.donatorplus")
                    || player.hasPermission("badlion.lion"))) {
                if (player.getGameMode() == GameMode.CREATIVE) {
					if (args.length == 1) {
						Player target = Bukkit.getPlayerExact(args[0]);

						if (player == target) {
							player.sendMessage(ChatColor.RED + "You cannot teleport to yourself.");
							return true;
						}

						boolean disguisedName = target != null && target.isDisguised() && target.getDisguisedName().equalsIgnoreCase(args[0]);

						// Let hosts /tele to real names even when player is disguised
						if (target == null || (!player.hasPermission("badlion.uhchost") && target.isDisguised() && !disguisedName)) {
							player.sendMessage(ChatColor.RED + "Player not found.");
							return true;
						}

                        UHCPlayer uhcPlayerTo = UHCPlayerManager.getUHCPlayer(target.getUniqueId());
                        if (uhcPlayer.getState().ordinal() < UHCPlayer.State.MOD.ordinal() && uhcPlayerTo.getState() != UHCPlayer.State.PLAYER) {
                            player.sendMessage(ChatColor.RED + "You can only teleport to players who are alive.");
                            return true;
                        }

						boolean isInNether = target.getWorld().getEnvironment() == World.Environment.NETHER;

                        // Lion can only tele to within 500x500, donator+ within 100x100
                        if (!player.hasPermission("badlion.famous") && !player.hasPermission("badlion.uhctrial")) {
                            if (player.hasPermission("badlion.lion")) {
                                if (isInNether || target.getLocation().getX() < -500 || target.getLocation().getX() > 500 || target.getLocation().getZ() < -500 || target.getLocation().getZ() > 500) {
                                    player.sendMessage(ChatColor.RED + "This player is outside of the 500x500 border. Cannot teleport to them.");
                                    return true;
                                }
                            } else {
                                if (isInNether || target.getLocation().getX() < -100 || target.getLocation().getX() > 100 || target.getLocation().getZ() < -100 || target.getLocation().getZ() > 100) {
                                    player.sendMessage(ChatColor.RED + "This player is outside of the 100x100 border. Cannot teleport to them.");
                                    return true;
                                }
                            }
                        }

						// Check if this player is underground
						if (!player.hasPermission("badlion.uhctrial")) {
							Block block = target.getLocation().getBlock();
							int y = target.getLocation().getWorld().getHighestBlockYAt(block.getX(), block.getZ());
							// 250 check because player.getLocation().getBlock() returns bedrock at that level
							if (target.getLocation().getY() < 250 && y - block.getY() > 10) {
								player.sendMessage(ChatColor.RED + "Cannot follow players underground as a spectator.");
								return true;
							}
						}

						player.teleport(target);
					} else if (args.length == 2 && player.hasPermission("badlion.uhctrial")) {
                        Player toSend = Bukkit.getPlayerExact(args[0]);
                        Player teleTo = Bukkit.getPlayerExact(args[1]);

                        if (teleTo == null || toSend == null) {
                            player.sendMessage(ChatColor.RED + "One of the players was not found.");
                            return true;
                        }

                        toSend.teleport(teleTo.getLocation());
                    } else if (args.length == 3) {
                        int x;
                        int y;
                        int z;

                        try {
                            x = Integer.parseInt(args[0]);
                            y = Integer.parseInt(args[1]);
                            z = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.RED + "Invalid format. /tele [x] [y] [z]");
                            return true;
                        }

                        // Lion can only tele to within 500x500, donator+ within 100x100
                        if (!player.hasPermission("badlion.famous") && !player.hasPermission("badlion.uhctrial") && !player.hasPermission("badlion.uhchost")) {
                            if (player.hasPermission("badlion.lion") || player.hasPermission("badlion.lionplus")) {
                                if (x < -500 || x > 500 || z < -500 || z > 500) {
                                    player.sendMessage(ChatColor.RED + "You cannot teleport out of the 500x500 border.");
                                    return true;
                                }
                            } else {
                                if (x < -100 || x > 100 || z < -100 || z > 100) {
                                    player.sendMessage(ChatColor.RED + "You cannot teleport out of the 500x500 border.");
                                    return true;
                                }
                            }
                        }

                        player.teleport(new Location(player.getLocation().getWorld(), x, y, z));
                    } else {
						return false;
					}
				} else {
					player.sendMessage(ChatColor.RED + "You cannot use this command while not in creative");
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
			}
		}
		return true;
	}

}
