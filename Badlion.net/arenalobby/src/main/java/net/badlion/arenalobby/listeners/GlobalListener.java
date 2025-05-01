package net.badlion.arenalobby.listeners;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.GroupStateMachine;
import net.badlion.arenalobby.commands.MCPCommand;
import net.badlion.arenalobby.helpers.DuelHelper;
import net.badlion.arenalobby.managers.DuelRequestManager;
import net.badlion.arenalobby.managers.SidebarManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.HelpCommandEvent;
import net.badlion.gberry.events.ServerRebootMessageEvent;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class GlobalListener extends BukkitUtil.Listener {

	private static boolean allowInvMovement;

	public GlobalListener() {
		GlobalListener.allowInvMovement = ArenaLobby.getInstance().getConfig().getBoolean("allow-inv", false);
	}

	// No one should take damage on arena lobby servers
	@EventHandler
	public void onPlayerHurt(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerHurt(EntityDamageByEntityEvent event) {
		// Stop damage from players and other entities
		if (event.getEntity() instanceof Player || event.getDamager() instanceof Player) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onHungerLoss(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			player.setFoodLevel(20);
			player.setSaturation(10);
		}
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onInvMovement(InventoryClickEvent event) {
		if (GlobalListener.allowInvMovement) {
			for (HumanEntity entity : event.getViewers()) {
				if (entity.getGameMode() == GameMode.CREATIVE && entity.hasPermission("badlion.builder")) {
					event.setCancelled(false);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (event.getPlayer().isOp()) {
			event.getPlayer().setOp(false);
		}
		final Player player = event.getPlayer();

		SidebarManager.addSidebarItems(player);
		//event.getPlayer().setPlayerListName("§§§" + event.getPlayer().getName());

		ArenaLobby.getInstance().addUUIDToUsername(event.getPlayer().getUniqueId(), event.getPlayer().getName());

		if (event.getPlayer().hasPermission("badlion.kittrial")) {
			ArenaLobby.getInstance().addMuteBanPerms(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void playerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		SidebarManager.removeSidebar(player);
		MCPCommand.cooldowns.remove(player.getUniqueId());
		DuelHelper.DuelCreator duelCreator = DuelRequestManager.getDuelCreator(player.getUniqueId());
		if (duelCreator != null) {
			if (duelCreator.isAccepted()) {
				return;
			}
			// Send messages
			//duelCreator.getSender().sendMessage(ChatColor.RED + player.getName() + " has logged out, duel request cancelled.");
			if (duelCreator.getSenderId().equals(player.getUniqueId())) {
				DuelHelper.handleDuelDeny(false, player.getUniqueId(), duelCreator.getReceiverId());
				duelCreator.sendMessageToReceiverIfOnline(ChatColor.RED + player.getName() + " has logged out, duel request cancelled.");
			} else {
				DuelHelper.handleDuelDeny(false, player.getUniqueId(), duelCreator.getSenderId());
				duelCreator.sendMessageToSenderIfOnline(ChatColor.RED + player.getName() + " has logged out, duel request cancelled.");
			}
		}

		try {
			// Try to remove any extra memory stuff we have of them
			ArenaLobby.getInstance().getQueuedPlayerSignUpdates().remove(player);
			ArenaLobby.getInstance().getBlockedPlayerSignUpdates().remove(player);
			ArenaLobby.getInstance().getBlockedBlockChangeLocations().remove(player);

			// Crash?
			event.getPlayer().closeInventory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void playerDropItemEvent(PlayerDropItemEvent event) {
		if (!event.getPlayer().isDead()) {
			Group group = ArenaLobby.getInstance().getPlayerGroup(event.getPlayer());
			if (GroupStateMachine.lobbyState.contains(group)) {
				event.setCancelled(true);
			} else if (GroupStateMachine.kitCreationState.contains(group)
					&& !GroupStateMachine.kitCreationState.getKitCreator(event.getPlayer()).getKitRuleSet().usesCustomChests()) {
				event.setCancelled(true);
			} else if (GroupStateMachine.getInstance().getCurrentState(group) == GroupStateMachine.matchMakingState) {
				event.setCancelled(true);
			}
		} else {
			// Fix Race Condition
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			event.getDrops().clear();
			event.setDroppedExp(0);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClickEvent(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Group group = ArenaLobby.getInstance().getPlayerGroup(player);
		if (GroupStateMachine.lobbyState.contains(group)) {
			event.setCancelled(true);
		} else if (GroupStateMachine.getInstance().getCurrentState(group) == GroupStateMachine.matchMakingState) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.FIRST)
	public void onPlayerInteractWhileDead(PlayerInteractEvent event) {
		if (event.getPlayer().isDead()) {
			// Fix Race Condition
			event.setCancelled(true);
		} else {
			// By default, the event is cancelled by default...weird ass fucking shit...
			// allow it so we can check this condition properly
			event.setUseInteractedBlock(Event.Result.ALLOW);
		}
	}

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerSuffocation(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerFallIntoVoid(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.VOID) {
			final Player player = (Player) event.getEntity();
			Group group = ArenaLobby.getInstance().getPlayerGroup(player);

			// They are not in matchmaking state and fell off edge
			if (!GroupStateMachine.matchMakingState.contains(group)) {
				if (GroupStateMachine.kitCreationState.contains(group)) {
					// TODO: Temporary fix for https://hub.spigotmc.org/jira/browse/SPIGOT-1921
					BukkitUtil.runTaskNextTick(new Runnable() {
						@Override
						public void run() {
							player.teleport(ArenaLobby.getInstance().getKitCreationLocation());
						}
					});
				} else {
					BukkitUtil.runTaskNextTick(new Runnable() {
						@Override
						public void run() {
							player.teleport(ArenaLobby.getInstance().getSpawnLocation());
						}
					});
				}

				event.setCancelled(true);
			} else if (GroupStateMachine.matchMakingState.contains(group)
					&& GroupStateMachine.getInstance().getCurrentState(group) == GroupStateMachine.matchMakingState) {
				BukkitUtil.runTaskNextTick(new Runnable() {
					@Override
					public void run() {
						player.teleport(ArenaLobby.getInstance().getSpawnLocation());
					}
				});

				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.FIRST)
	public void onPlayerUseCompass(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getItem().getType() == Material.COMPASS && event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void priorityLoginListener(PlayerLoginEvent event) {
		if (event.getResult() != PlayerLoginEvent.Result.KICK_WHITELIST && event.getPlayer().hasPermission("badlion.donator") || event.getPlayer().hasPermission("badlion.staff")) {
			event.setResult(PlayerLoginEvent.Result.ALLOWED);
		}
	}

	@EventHandler(priority = EventPriority.FIRST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		// Do this so we can check in other places for it
		event.setRespawnLocation(ArenaLobby.getInstance().getDefaultRespawnLocation());
	}

	@EventHandler
	public void onPlayerFishEvent(PlayerFishEvent event) {
		if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
			event.setExpToDrop(0);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		String message = event.getMessage().toLowerCase();
		if (message.equalsIgnoreCase("/sp") || message.toLowerCase().startsWith("/sp ")) {
			String[] strings = message.split("/sp");
			if (strings.length > 1) {
				event.getPlayer().performCommand("spectate" + strings[1]);
			} else {
				event.getPlayer().performCommand("spectate");
			}

			event.setCancelled(true);
		} else if (message.toLowerCase().startsWith("/whitelist remove ")) {
			if (event.getPlayer().hasPermission("bukkit.command.whitelist.remove")) {
				String[] args = message.split(" ");
				if (args.length == 3) {
					Player kick = ArenaLobby.getInstance().getServer().getPlayerExact(args[2]);
					if (kick != null && Gberry.isPlayerOnline(kick)) {
						kick.kickPlayer(ChatColor.GOLD + "Thank you for playing in our tournament! You have been unwhitelisted from the server.");
					}
				}
			}
		} else if (message.toLowerCase().startsWith("/stop") && event.getPlayer().isOp()) {
			ArenaLobby.restarting = true;
			for (Player pl : ArenaLobby.getInstance().getServer().getOnlinePlayers()) {
				pl.kickPlayer("Server is rebooting.");
			}
		}
	}

    /*@EventHandler(priority=EventPriority.LAST)
    public void onNonDonatorPlusJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission("badlion.donatorplus")) {
            event.getPlayer().kickPlayer("Season 11 beta is restricted to Donator+/Lion only");
        } else {
	        event.getPlayer().sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + "Welcome to Season 11 Beta! Please report all bugs on the forums. Elo on here will be reset when S11 launches.");
	        event.getPlayer().sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + "If you teleport into an arena and take fall damage, please write down the coords and make a bug report thread!");
        }
    }*/

	@EventHandler
	public void onHelpCommandEvent(HelpCommandEvent event) {
		Player player = event.getPlayer();
		Group group = ArenaLobby.getInstance().getPlayerGroup(player);
		State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);

		player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
		if (currentState == GroupStateMachine.loginState || currentState == GroupStateMachine.lobbyState) {
			//DateTime now = DateTime.now(DateTimeZone.UTC);
			//DateTime reset = DateTime.now(DateTimeZone.UTC).plusDays(1).withHourOfDay(4);

			//int hoursUntilRankedMatchesReset = now.hourOfDay().getDifference(reset.toInstant());

			player.sendFormattedMessage("{0}Welcome to Badlion ArenaPvP", ChatColor.AQUA);
			player.sendFormattedMessage("{0}Use the inventory items in your hotbar by right clicking them to explore the server", ChatColor.GREEN);
			player.sendMessage("");
			player.sendFormattedMessage("{0}Other Commands:", ChatColor.AQUA);
			player.sendFormattedMessage("{0} Send a message to your clan", ChatColor.GREEN + "/cc [message] -");
			player.sendFormattedMessage("{0} Duel another player on the server", ChatColor.GREEN + "/duel [name] -");
			player.sendFormattedMessage("{0} Party commands", ChatColor.GREEN + "/party -");
			player.sendFormattedMessage("{0} Teleport to a player to spectate them", ChatColor.GREEN + "/spec [name] -");
			player.sendFormattedMessage("{0} Follow a player around to their matches", ChatColor.GREEN + "/follow [name] -");
			player.sendFormattedMessage("{0} Gift a player your ranked matches for the day, 20 ranked matches if you are Donator+ or 40 ranked matches if Lion", ChatColor.GREEN + "/giftmatches [name] -");
		} else if (currentState == GroupStateMachine.kitCreationState) {
			player.sendFormattedMessage("{0}You are now in the Kit Creation area. Here you can modify your kits for gameplay.", ChatColor.AQUA);
		} else if (currentState == GroupStateMachine.matchMakingState) {
			player.sendFormattedMessage("{0}You are currently waiting for a match. Sometimes matches might take a little while to find.", ChatColor.AQUA);
		} else {
			player.sendFormattedMessage("{0}Welcome to Badlion ArenaPvP", ChatColor.AQUA);
			player.sendFormattedMessage("{0}Ask around for further help if you need it!", ChatColor.GREEN);
		}
		player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
	}

	@EventHandler
	public void onServerRebootTimeLeftEvent(ServerRebootMessageEvent event) {
		// Disable ranked matches at 5 minutes
		if (event.getMinutesLeft() == 5) {
			// Cancel the message so we can send our own saying ranked is disabled
			event.setCancelled(true);

			ArenaLobby.getInstance().setAllowRankedMatches(false);
			Gberry.broadcastMessage(ChatColor.RED + "Server rebooting in 5 minutes. Ranked matches are now disabled");
		} else if (event.getMinutesLeft() == 0) {

		}
	}

	@EventHandler
	public void onPlayerStopServer(ServerCommandEvent event) {
		if (event.getCommand().toLowerCase().startsWith("/stop") || event.getCommand().toLowerCase().startsWith("stop")) {
			ArenaLobby.restarting = true;
			for (Player pl : ArenaLobby.getInstance().getServer().getOnlinePlayers()) {
				pl.kickPlayer("Server is rebooting.");
			}
		}
	}

}
