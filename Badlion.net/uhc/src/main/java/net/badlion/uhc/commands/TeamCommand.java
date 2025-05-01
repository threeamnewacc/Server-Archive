package net.badlion.uhc.commands;

import net.badlion.disguise.DisguisedPlayer;
import net.badlion.disguise.managers.DisguiseManager;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.commands.handlers.GameModeHandler;
import net.badlion.uhc.commands.handlers.teams.*;
import net.badlion.uhc.events.TeamListCommandEvent;
import net.badlion.uhc.listeners.gamemodes.BackpacksGameMode;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.managers.UHCTeamManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
	        Player player = (Player) sender;

            if (BadlionUHC.getInstance().getGameType().equals(UHCTeam.GameType.TEAM)) {
                UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());

                // Allow hosts/mods to use /team list [name]
                if (!sender.isOp() && uhcPlayer.getState().ordinal() >= UHCPlayer.State.MOD.ordinal() && args.length > 0 && !args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage(ChatColor.RED + "You are not allowed to use this command.");
                    return true;
                }

	            if (BadlionUHC.getInstance().getState().ordinal() >= BadlionUHC.BadlionUHCState.COUNTDOWN.ordinal()) {
		            if ((args.length > 0 && !sender.isOp() && !args[0].equalsIgnoreCase("list"))
				            && (!GameModeHandler.GAME_MODES.contains("BACKPACKS") || (GameModeHandler.GAME_MODES.contains("BACKPACKS")
						            && (!args[0].equalsIgnoreCase("inventory") && !args[0].equalsIgnoreCase("i"))))) {
			            sender.sendMessage(ChatColor.RED + "Cannot do any team management after the game has started or entered countdown.");
			            return true;
		            }
	            }

                if (args.length >= 1) {
	                if ((args[0].equalsIgnoreCase("inventory") || args[0].equalsIgnoreCase("i")) && GameModeHandler.GAME_MODES.contains("BACKPACKS")) {
		                if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED) {
			                player.openInventory(BackpacksGameMode.teamInventories.get(uhcPlayer.getTeam().getTeamNumber()));
		                } else {
			                player.sendMessage(ChatColor.RED + "The game hasn't started yet!");
			                return true;
		                }
	                } else if (args[0].equalsIgnoreCase("leave")) {
                        LeaveCommandHandler.handleLeaveCommand((Player) sender, args);
                    } else if (args[0].equalsIgnoreCase("invite")) {
                        InviteCommandHandler.handleInviteCommand((Player) sender, args);
                    } else if (args[0].equalsIgnoreCase("kick")) {
                        KickCommandHandler.handleKickCommand((Player) sender, args);
                    } else if (args[0].equalsIgnoreCase("accept")) {
                        AcceptCommandHandler.handleAcceptCommand((Player) sender, args);
                    } else if (args[0].equalsIgnoreCase("deny")) {
                        DenyCommandHandler.handleDenyCommand((Player) sender, args);
                    } else if (args[0].equalsIgnoreCase("solo")) {
                        if (BadlionUHC.getInstance().isMiniUHC()) {
                            sender.sendMessage(ChatColor.RED + "/team solo is disabled in MiniUHC");
                            return true;
                        }

                        // Possible bug with glitched teams
                        if (uhcPlayer.getTeamRequest() != null) {
                            sender.sendMessage(ChatColor.RED + "Cannot use /team solo with a pending team invite");
                            return true;
                        }

                        if (uhcPlayer.getTeam().getSize() > 1) {
                            sender.sendMessage(ChatColor.RED + "Cannot use /team solo with a team");
                            return true;
                        }

                        uhcPlayer.setSolo(!uhcPlayer.isSolo());
                        if (uhcPlayer.isSolo()) {
                            sender.sendMessage(ChatColor.GREEN + "You will no longer be paired with a team mate randomly.");
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "You will now be paired with a team mate randomly if you do not have one.");
                        }
                    } else if (args[0].equalsIgnoreCase("list")) {
                        TeamListCommandEvent event = new TeamListCommandEvent((Player) sender);
                        BadlionUHC.getInstance().getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled()) {
                            if (args.length == 2) {
                                UUID uuid = BadlionUHC.getInstance().getUUID(args[1]);
                                if (uuid == null) {
                                    sender.sendMessage(ChatColor.RED + "Player not found.");
                                    return true;
                                }

                                UHCPlayer uhcp = UHCPlayerManager.getUHCPlayer(uuid);
                                if (uhcp == null) {
                                    sender.sendMessage(ChatColor.RED + "Player not found.");
                                    return true;
                                }

                                this.teamList(sender, uhcp.getTeam());
                            } else {
                                this.teamList(sender, uhcPlayer.getTeam());
                            }
                        }
                    } else if (args[0].equals("force") && sender.isOp()) {
                        if (args.length != 3) {
                            sender.sendMessage(ChatColor.RED + "Invalid usage of /team force");
                        } else {
                            Player pf = BadlionUHC.getInstance().getServer().getPlayer(args[1]);
                            Player pt = BadlionUHC.getInstance().getServer().getPlayer(args[2]);

                            if (pf == null || pt == null) {
                                sender.sendMessage(ChatColor.RED + "One of the players is not online.");
                                return true;
                            }

                            UHCPlayer from = UHCPlayerManager.getUHCPlayer(pf.getUniqueId());
                            UHCPlayer to = UHCPlayerManager.getUHCPlayer(pt.getUniqueId());

                            if (from.getTeam().getSize() == 1) {
                                UHCTeamManager.removeUHCTeam(from.getTeam());
                            }

                            from.setTeamRequest(null);
                            from.getTeam().removePlayer(pf.getUniqueId());
                            from.setTeam(to.getTeam());
                            to.getTeam().addPlayer(pf.getUniqueId());
                            sender.sendMessage(ChatColor.GREEN + "Forced " + args[1] + " to team " + args[2]);
                        }
                    } else {
                        this.teamHelpMenu(sender);
                    }
                } else {
                    this.teamHelpMenu(sender);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Custom teams are currently disabled. Ask the host to enable them.");
            }
        } else {
            sender.sendMessage("You can only use this command ingame");
        }
        return true;
    }

    private void teamHelpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "Custom teams are enabled and you can use the following commands:");
        sender.sendMessage(ChatColor.YELLOW + "/team invite [name] - Invite a member to your team.");
        sender.sendMessage(ChatColor.YELLOW + "/team accept - Accept an invite from a team leader.");
        sender.sendMessage(ChatColor.YELLOW + "/team deny - Deny an invite from a team leader.");
        sender.sendMessage(ChatColor.YELLOW + "/team kick [name] - (Leader Only) Kick a member from your team.");
        sender.sendMessage(ChatColor.YELLOW + "/team leave - Leave a team.");

        if (!BadlionUHC.getInstance().isMiniUHC()) {
            sender.sendMessage(ChatColor.YELLOW + "/team solo - Toggle whether you want a random ally or not");
        }

        sender.sendMessage(ChatColor.YELLOW + "/team list - List your teammates");
        sender.sendMessage(ChatColor.YELLOW + "/team list <player> - List another player's teammates");
    }

	private void teamList(CommandSender sender, UHCTeam team) {
		StringBuilder sb = new StringBuilder();
		sb.append(team.getPrefix());
		sb.append(ChatColor.YELLOW);
		sb.append(" Players: ");

		for (UUID uuid2 : team.getUuids()) {
			Player pl = BadlionUHC.getInstance().getServer().getPlayer(uuid2);
			if (pl != null) {
				UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(uuid2);

				// Green for alive, red for dead
				if (uhcPlayer.getState() == UHCPlayer.State.PLAYER || uhcPlayer.getState() == UHCPlayer.State.SPEC_IN_GAME) {
					sb.append(ChatColor.GREEN);
					sb.append(pl.getDisguisedName());
					sb.append(ChatColor.GOLD);
					sb.append(HealthCommand.getHeartsLeftString(ChatColor.GOLD, pl.getHealth()));
				} else {
					sb.append(ChatColor.RED);
					sb.append(pl.getDisguisedName());
				}
			} else {
				UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(uuid2);

				// Green for alive, red for dead
				if (uhcPlayer.getState() == UHCPlayer.State.PLAYER || uhcPlayer.getState() == UHCPlayer.State.SPEC_IN_GAME) {
					sb.append(ChatColor.GREEN);
				} else {
					sb.append(ChatColor.RED);
				}

				// Have to do disguised checks because this player is offline
				DisguisedPlayer disguisedPlayer = DisguiseManager.getDisguisePlayer(uuid2);
				if (disguisedPlayer != null) {
					sb.append(disguisedPlayer.getDisguisedName());
				} else {
					sb.append(BadlionUHC.getInstance().getUsername(uuid2));
				}
			}

			sb.append(ChatColor.YELLOW);
			sb.append(", ");
		}

		sender.sendMessage(sb.substring(0, sb.length() - 2));
	}

}
