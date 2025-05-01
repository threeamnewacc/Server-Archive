package net.badlion.potpvp.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.HelpCommandEvent;
import net.badlion.gberry.events.ServerRebootMessageEvent;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.commands.PartyCommand;
import net.badlion.potpvp.events.War;
import net.badlion.potpvp.ffaworlds.SGFFAWorld;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.managers.EnderPearlManager;
import net.badlion.potpvp.managers.FFAManager;
import net.badlion.potpvp.matchmaking.Match;
import net.badlion.potpvp.matchmaking.RedRoverMatch;
import net.badlion.potpvp.states.matchmaking.GameState;
import net.badlion.potpvp.tdm.TDMGame;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.statemachine.State;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemsEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerItemsDroppedFromDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
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
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GlobalListener extends BukkitUtil.Listener {

    private static boolean allowInvMovement;

    public GlobalListener() {
        GlobalListener.allowInvMovement = PotPvP.getInstance().getConfig().getBoolean("allow-inv", false);
    }

    @EventHandler(priority=EventPriority.LAST)
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

        //event.getPlayer().setPlayerListName("§§§" + event.getPlayer().getName());

        PotPvP.getInstance().addUUIDToUsername(event.getPlayer().getUniqueId(), event.getPlayer().getName());

        if (event.getPlayer().hasPermission("badlion.kittrial")) {
            PotPvP.getInstance().addMuteBanPerms(event.getPlayer());
        }
    }

	@EventHandler(priority=EventPriority.LOW)
	public void playerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();

        try {
	        // Try to remove any extra memory stuff we have of them
	        PotPvP.getInstance().getQueuedPlayerSignUpdates().remove(player);
	        PotPvP.getInstance().getBlockedPlayerSignUpdates().remove(player);
	        PotPvP.getInstance().getBlockedBlockChangeLocations().remove(player);

	        // Crash?
	        event.getPlayer().closeInventory();
        } catch (Exception e) {
	        e.printStackTrace();
        }
	}

	@EventHandler(ignoreCancelled=true)
	public void playerDropItemEvent(PlayerDropItemEvent event) {
        if (!event.getPlayer().isDead()) {
            Group group = PotPvP.getInstance().getPlayerGroup(event.getPlayer());
            if (GroupStateMachine.lobbyState.contains(group) || GroupStateMachine.spectatorState.contains(group)
		            || GroupStateMachine.partyState.contains(group) || GroupStateMachine.partyRequestState.contains(group)) {
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

	@EventHandler
	public void onPlayerCraftItemEvent(CraftItemEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = ((Player) event.getWhoClicked());
			Group group = PotPvP.getInstance().getPlayerGroup(player);

			// Cancel if they're not in an SG FFA
			if (!(GameState.getGroupGame(group) instanceof SGFFAWorld)) {
				event.setCancelled(true);
				event.setResult(Event.Result.DENY);

				player.sendMessage(ChatColor.RED + "Crafting items is not allowed!");
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onInventoryClickEvent(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Group group = PotPvP.getInstance().getPlayerGroup(player);
		if (GroupStateMachine.lobbyState.contains(group) || GroupStateMachine.spectatorState.contains(group)
				|| GroupStateMachine.partyState.contains(group) || GroupStateMachine.partyRequestState.contains(group)) {
			event.setCancelled(true);
		} else if (GroupStateMachine.getInstance().getCurrentState(group) == GroupStateMachine.matchMakingState) {
			event.setCancelled(true);
		} else if (GroupStateMachine.regularMatchState.contains(group)) {
			Match match = GroupStateMachine.regularMatchState.getMatchFromGroup(group);
			// RedRover check
			if (match instanceof RedRoverMatch && ((RedRoverMatch) match).isSelectingFighter(player)) {
				event.setCancelled(true);
			}
		} else if (!GroupStateMachine.regularMatchState.contains(group)) {
			ItemStack item = event.getCurrentItem();

			if (item == null || item.getType().equals(Material.AIR)) return;

			if (event.getView().getTopInventory().getName().equals(ChatColor.BOLD + ChatColor.AQUA.toString() + "Opponent's Inventory")) {
				event.setCancelled(true);

				if (SmellyInventory.isCloseInventoryItem(item)) {
					BukkitUtil.closeInventory(player);
				}
			}
		}
	}

    @EventHandler(priority=EventPriority.FIRST)
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
	public void onSpectatorExtinguishFireEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);
		if (GroupStateMachine.spectatorState.contains(group)) {
			if (event.getClickedBlock() != null && event.getClickedBlock().getRelative(BlockFace.UP).getType().equals(Material.FIRE)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onSpectatorHorseInventoryClickEvent(InventoryClickEvent event) {
		Player player = ((Player) event.getWhoClicked());

		if (player.isOp()) {
			return;
		}

		if (player.getGameMode() == GameMode.CREATIVE && !player.isOp()) {
			event.setCancelled(true);
		} else if (event.getClickedInventory() instanceof HorseInventory) {
			event.setCancelled(true);
		}
	}

    @EventHandler(priority=EventPriority.LOWEST)
    public void onEPThrow(ProjectileLaunchEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EnderPearl) {
            EnderPearl ep = (EnderPearl) entity;
            Player player = (Player) ep.getShooter();
            EnderPearlManager.put(player, ep);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
	        Arrow arrow = (Arrow) event.getEntity();
	        if (arrow.getShooter() instanceof Player) {
		        Player player = (Player) arrow.getShooter();

		        // Player could have logged off
		        if (Gberry.isPlayerOnline(player)) {
			        Group group = PotPvP.getInstance().getPlayerGroup(player);
			        Game game = GameState.getGroupGame(group);
			        if (game != null) {
				        // Don't remove arrows in TDMs
				        if (game instanceof TDMGame) {
					        // NOTE: I don't think we need to do this, the arrows despawn after a few seconds
					        //GroupStateMachine.tdmState.addArrow((TDMGame) game, arrow);
				        } else {
					        event.getEntity().remove();
				        }
			        }
		        }
	        }
        } else if (event.getEntity() instanceof EnderPearl) {
            EnderPearl pearl = (EnderPearl) event.getEntity();
            if (pearl.getShooter() instanceof Player) {
                Player player = (Player) pearl.getShooter();
                EnderPearlManager.remove(player);
            }
        }
    }

    @EventHandler
    public void onPlayerSuffocation(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LAST)
    public void onPlayerFallIntoVoid(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            final Player player = (Player) event.getEntity();
            Group group = PotPvP.getInstance().getPlayerGroup(player);

            // They are not in matchmaking state and fell off edge
            if (!GroupStateMachine.matchMakingState.contains(group)) {
                if (GroupStateMachine.kitCreationState.contains(group)) {
	                // TODO: Temporary fix for https://hub.spigotmc.org/jira/browse/SPIGOT-1921
	                if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
		                BukkitUtil.runTaskNextTick(new Runnable() {
			                @Override
			                public void run() {
				                player.teleport(PotPvP.getInstance().getKitCreationLocation());
			                }
		                });
	                } else {
		                player.teleport(PotPvP.getInstance().getKitCreationLocation());
	                }
                } else {
	                if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
		                BukkitUtil.runTaskNextTick(new Runnable() {
			                @Override
			                public void run() {
				                player.teleport(PotPvP.getInstance().getSpawnLocation());
			                }
		                });
	                } else {
		                player.teleport(PotPvP.getInstance().getSpawnLocation());
	                }
                }

                event.setCancelled(true);
            } else if (GroupStateMachine.matchMakingState.contains(group)
                    && GroupStateMachine.getInstance().getCurrentState(group) == GroupStateMachine.matchMakingState) {
	            if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
		            BukkitUtil.runTaskNextTick(new Runnable() {
			            @Override
			            public void run() {
				            player.teleport(PotPvP.getInstance().getSpawnLocation());
			            }
		            });
	            } else {
		            player.teleport(PotPvP.getInstance().getSpawnLocation());
	            }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.FIRST)
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

    @EventHandler(priority=EventPriority.FIRST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // Do this so we can check in other places for it
        event.setRespawnLocation(PotPvP.getInstance().getDefaultRespawnLocation());
    }

    @EventHandler(priority = EventPriority.FIRST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Group group = PotPvP.getInstance().getPlayerGroup(player);

        event.setDeathMessage(null);

        // Drop items if party match
        if (!GroupStateMachine.regularMatchState.contains(group) || !group.isParty()) {
            if (!GroupStateMachine.uhcMeetupState.contains(group)) {
                event.getDrops().clear();
            }
        }
    }

    @EventHandler
    public void onPlayerItemsDroppedFromDeath(PlayerItemsDroppedFromDeathEvent event) {
        Player player = event.getPlayer();
        Group group = PotPvP.getInstance().getPlayerGroup(player);
        Game game = GameState.getGroupGame(group);

	    // UHC Meetup
	    for (net.badlion.potpvp.events.Event event2 : net.badlion.potpvp.events.Event.getEvents().values()) {
		    if (event2.getEventType() == net.badlion.potpvp.events.Event.EventType.UHC_MEETUP) {
			    if (event2.getParticipants() != null && event2.getParticipants().contains(player)) {
				    // Remove items if match is already over (last player died)
				    if (event2.isOver()) {
					    for (Item item : event.getItemsDroppedOnDeath()) {
						    item.remove();
					    }
					    return;
				    }

				    event2.getArena().getDroppedItems().addAll(event.getItemsDroppedOnDeath());
				    return;
			    }
		    }
	    }

	    // Party Match/War
	    if ((group.isParty() && GroupStateMachine.regularMatchState.contains(group)) || game instanceof War) {
            // Remove items if match is already over (last player died)
            if (game.isOver()) {
                for (Item item : event.getItemsDroppedOnDeath()) {
                    item.remove();
                }
                return;
            }

	        game.getArena().getDroppedItems().addAll(event.getItemsDroppedOnDeath());
        } else {
	        // Always clear items (safety measure)
	        for (Item item : event.getItemsDroppedOnDeath()) {
		        item.remove();
	        }
        }
    }

    @EventHandler
    public void onBlockDropItemsEvent(BlockDropItemsEvent event) {
        Arena arena = ArenaManager.getBrokenBlockArena(event.getBlock());
        if (arena != null) {
            arena.getDroppedItems().addAll(event.getItems());
        }
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
				    Player kick = PotPvP.getInstance().getServer().getPlayerExact(args[2]);
				    if (kick != null && Gberry.isPlayerOnline(kick)) {
					    kick.kickPlayer(ChatColor.GOLD + "Thank you for playing in our tournament! You have been unwhitelisted from the server.");
				    }
			    }
		    }
	    } else if (message.toLowerCase().startsWith("/stop") && event.getPlayer().isOp()) {
            PotPvP.restarting = true;
            for (Player pl : PotPvP.getInstance().getServer().getOnlinePlayers()) {
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
		Group group = PotPvP.getInstance().getPlayerGroup(player);
		State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);

        player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
		if (currentState == GroupStateMachine.loginState || currentState == GroupStateMachine.lobbyState) {
            player.sendMessage(ChatColor.AQUA + "Welcome to Badlion ArenaPvP");
            player.sendMessage(ChatColor.GREEN + "Use the inventory items in your hotbar by right clicking them to explore the server");
            player.sendMessage("");
            player.sendMessage(ChatColor.AQUA + "Other Commands:");
            player.sendMessage(ChatColor.GREEN + "/duel <name> - Duel another player on the server");
		} else if (currentState == GroupStateMachine.duelRequestState) {
			player.sendMessage(ChatColor.GREEN + "You currently have a duel request. Either accept or deny the duel request");
		} else if (currentState == GroupStateMachine.partyState) {
			player.sendMessage(ChatColor.AQUA + "You are currently in a party. Use the items in your hotbar to queue up for a ladder");
            player.sendMessage(ChatColor.AQUA + "or you may use /duel <name> to duel another party");
            player.sendMessage("");
            player.sendMessage(ChatColor.AQUA + "Party Commands:");
            ((PartyCommand) PotPvP.getInstance().getCommand("party").getExecutor()).usage(player);
		} else if (currentState == GroupStateMachine.partyRequestState) {
			player.sendMessage(ChatColor.GREEN + "You currently have a party invite request. Either accept or deny the party invite request");
		} else if (currentState == GroupStateMachine.kitCreationState) {
			player.sendMessage(ChatColor.AQUA + "You are now in the Kit Creation area. Here you can modify your kits for gameplay");
		} else if (currentState == GroupStateMachine.spectatorState) {
			player.sendMessage(ChatColor.AQUA + "Sectator Commands:");
            player.sendMessage(ChatColor.GREEN + "/sp <name> - Teleport to a player");
            player.sendMessage(ChatColor.GREEN + "/follow <name> - Follow a player through multiple matches");
            player.sendMessage(ChatColor.GREEN + "/unfollow - Stop following the player you were following");
		} else if (currentState == GroupStateMachine.followState) {
            player.sendMessage(ChatColor.AQUA + "You are currently following a player. You will continue to teleport to this player as they");
            player.sendMessage(ChatColor.AQUA + "go inbetween matches on the server. Use /unfollow to stop following them");
		} else if (currentState == GroupStateMachine.ffaState) {
			player.sendMessage(ChatColor.AQUA + "You are in a Free For All World. Jump down and fight to the death with other players");
		} else if (currentState == GroupStateMachine.matchMakingState) {
			player.sendMessage(ChatColor.AQUA + "You are currently waiting for a match. Sometimes matches might take a little while to find");
		} else if (currentState == GroupStateMachine.regularMatchState || currentState == GroupStateMachine.rankedMatchState) {
			player.sendMessage(ChatColor.AQUA + "You are currently in a match.");
            player.sendMessage(ChatColor.GREEN + "If you wish to leave use /spawn at any time");
		} else if (currentState == GroupStateMachine.kothState) {
            player.sendMessage(ChatColor.AQUA + "You are playing in a King Of the Hill (KOTH)");
            player.sendMessage(ChatColor.GREEN + "Stand in the hill to earn points.");
            player.sendMessage(ChatColor.GREEN + "First team to 120 points wins.");
            player.sendMessage(ChatColor.GREEN + "There is a 10 minute time limit.");
            player.sendMessage(ChatColor.GREEN + "You will respawn when you die.");
		} else if (currentState == GroupStateMachine.lmsState) {
            player.sendMessage(ChatColor.AQUA + "You are playing in a Last Man Standing (LMS)");
            player.sendMessage(ChatColor.GREEN + "Last person alive wins.");
            player.sendMessage(ChatColor.GREEN + "Your kit will refresh everytime you kill someone");
		} else if (currentState == GroupStateMachine.slaughterState) {
			player.sendMessage(ChatColor.AQUA + "You are playing in a Slaughter Match");
            player.sendMessage(ChatColor.GREEN + "First player to 25 kills or whoever has the most kills at 10 minutes wins");
		} else if (currentState == GroupStateMachine.uhcMeetupState) {
			player.sendMessage(ChatColor.AQUA + "You are in a UHC Meetup.");
            player.sendMessage(ChatColor.GREEN + "This game mode replicates the 100x100 meetup in a UHC match");
            player.sendMessage(ChatColor.GREEN + "Be the last player alive to win!");
		} else if (currentState == GroupStateMachine.warState) {
			player.sendMessage(ChatColor.AQUA + "You are in a War Match");
            player.sendMessage(ChatColor.GREEN + "Team up with your allies and defeat the enemy colored team!");
            player.sendMessage(ChatColor.GREEN + "Last team standing wins");
		} else if (currentState == GroupStateMachine.tdmState) {
			player.sendMessage(ChatColor.AQUA + "You are now in a Team Death Match (TDM) game");
            player.sendMessage(ChatColor.GREEN + "TDM Matches last 15 minutes in length");
            player.sendMessage(ChatColor.GREEN + "Whichever team has more points at the end wins");
            player.sendMessage(ChatColor.GREEN + "Earn points by killing the enemy players");
		} else {
            player.sendMessage(ChatColor.AQUA + "Welcome to Badlion ArenaPvP");
            player.sendMessage(ChatColor.GREEN + "Ask around for further help if you need it!");
        }
        player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
	}

	@EventHandler
	public void onServerRebootTimeLeftEvent(ServerRebootMessageEvent event) {
		// Disable ranked matches at 5 minutes
		if (event.getMinutesLeft() == 5) {
			// Cancel the message so we can send our own saying ranked is disabled
			event.setCancelled(true);

			PotPvP.getInstance().setAllowRankedMatches(false);
			Gberry.broadcastMessage(ChatColor.RED + "Server rebooting in 5 minutes. Ranked matches are now disabled");
		} else if (event.getMinutesLeft() == 0) {
			FFAManager.cancelQuit = true;

			FFAManager.flushAllFFAStats();
		}
	}

    @EventHandler
    public void onPlayerStopServer(ServerCommandEvent event) {
        if (event.getCommand().toLowerCase().startsWith("/stop") || event.getCommand().toLowerCase().startsWith("stop")) {
            PotPvP.restarting = true;
            for (Player pl : PotPvP.getInstance().getServer().getOnlinePlayers()) {
                pl.kickPlayer("Server is rebooting.");
            }
        }
    }

}
