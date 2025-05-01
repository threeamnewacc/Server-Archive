package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

public class SpawnListener implements Listener {
	
	private GFactions plugin;
	private LinkedList<String> joinMessages = new LinkedList<>();

	private Set<UUID> neverPlayedBefore = new HashSet<>();
	
	public SpawnListener(GFactions plugin) {
		this.plugin = plugin;

		this.joinMessages.add("§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=");
		this.joinMessages.add(ChatColor.AQUA + "Welcome to " + ChatColor.YELLOW + ChatColor.BOLD + "Badlion Factions" + ChatColor.RESET + ChatColor.AQUA + "!");
		this.joinMessages.add(ChatColor.AQUA + "Please read the following for more information and don't be afraid to ask for help!");
		this.joinMessages.add("§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=");

		this.joinMessages.add("§3=§b=§3=§b=§3=§b= " + ChatColor.YELLOW + ChatColor.BOLD + "Information" + ChatColor.RESET + " §3=§b=§3=§b=§3=§b=");
		this.joinMessages.add(ChatColor.DARK_AQUA + "Use /register [email]" + ChatColor.AQUA + " to register on the Badlion Network and get a free kit, free extra home, and Villager rank.");
		this.joinMessages.add(ChatColor.DARK_AQUA + "Use /pvptime" + ChatColor.AQUA + " to see pvp protection time remaining.");
		this.joinMessages.add(ChatColor.DARK_AQUA + "Use /pvp on" + ChatColor.AQUA + " to turn off pvp protection.");
		this.joinMessages.add(ChatColor.DARK_AQUA + "Use /wild" + ChatColor.AQUA + " to teleport into the wilderness (2 hr cooldown).");
		this.joinMessages.add(ChatColor.DARK_AQUA + "Use /kit" + ChatColor.AQUA + " to see available kits.");
		this.joinMessages.add(ChatColor.DARK_AQUA + "Use /warps" + ChatColor.AQUA + " to see available warps.");
		this.joinMessages.add(ChatColor.DARK_AQUA + "Use /vote" + ChatColor.AQUA + " to see voting rewards.");
		this.joinMessages.add(ChatColor.DARK_AQUA + "Use /events" + ChatColor.AQUA + " to see if any events are running at the moment.");
		this.joinMessages.add(ChatColor.DARK_AQUA + "This is a TNT Raiding & PvP Server." + ChatColor.AQUA + " Have fun!");
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if (player != null) {
			// EOTW
			if (!player.hasPlayedBefore() || this.neverPlayedBefore.contains(player.getUniqueId())) {
				this.neverPlayedBefore.add(player.getUniqueId());

				player.kickPlayer(ChatColor.RED + "You cannot join during the EOTW if you have never played before!");
				return;
			}

			if (!player.hasPlayedBefore()) {
				// Tp to spawn
				this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new BukkitRunnable() {
					
					@Override
					public void run() {
						player.teleport(plugin.getSpawnLocation());
						//Gberry.broadcastMessage(ChatColor.GOLD + "Welcome " + ChatColor.GREEN + player.getDisplayName() + ChatColor.GOLD + ", the newest peasant on Badlion Factions.");
						//player.performCommand("load " + plugin.getCmdSigns().generateHash() + " " + "faction-starter-kit");

						//for (String s : joinMessages) {
						//	player.sendMessage(s);
						//}
					}
					
				}, 1);
				
				// Add to server that is currently running
				this.plugin.getMapNameToJoinTime().put(player.getUniqueId().toString(), System.currentTimeMillis());
				this.plugin.getMapNameToPvPTimeRemaining().put(player.getUniqueId().toString(), GFactions.PVP_PROTECTION_TIME * 1000);
				
				// Sync to database
				this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
					public void run() {
						SpawnListener.this.plugin.updateProtection(player, GFactions.PVP_PROTECTION_TIME * 1000);
					}
				});
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LAST)
	public void onPlayerSpawn(final PlayerRespawnEvent event) {
        event.setRespawnLocation(this.plugin.getSpawnLocation());

        // Add to server that is currently running
        this.plugin.getMapNameToJoinTime().put(event.getPlayer().getUniqueId().toString(), System.currentTimeMillis());
        this.plugin.getMapNameToPvPTimeRemaining().put(event.getPlayer().getUniqueId().toString(), GFactions.PVP_PROTECTION_TIME * 1000);

        // Sync to database
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            public void run() {
                plugin.updateProtection(event.getPlayer(), GFactions.PVP_PROTECTION_TIME * 1000);
            }
        });

		/*final Player player = event.getPlayer();
		FPlayer fPlayer = FPlayers.i.get(player);
		Faction faction = fPlayer.getFaction();

		// If they aren't in a faction, respawn them at Spawn
		if (faction.getId().equals("0") || Board.getFactionAt(event.getRespawnLocation()).getId().equals("0")) {
			event.setRespawnLocation(this.plugin.getSpawnLocation());
			return;
		}

		// Last ditched effort cuz we are not spawning in the right spot
		ProtectedRegion region = this.plugin.getgGuardPlugin().getProtectedRegion(event.getRespawnLocation(), this.plugin.getgGuardPlugin().getProtectedRegions());
		if (region != null && region.getRegionName().equals("spawn")) {
            event.setRespawnLocation(this.plugin.getSpawnLocation());

            // Add to server that is currently running
            this.plugin.getMapNameToJoinTime().put(player.getUniqueId().toString(), System.currentTimeMillis());
            this.plugin.getMapNameToPvPTimeRemaining().put(player.getUniqueId().toString(), GFactions.PVP_PROTECTION_TIME * 1000);

            // Sync to database
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                public void run() {
                    plugin.updateProtection(player, GFactions.PVP_PROTECTION_TIME * 1000);
                }
            });
		}*/
	}

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerPortal(PlayerPortalEvent event) {
        // Don't let them use a portal if they are in combat
        if (this.plugin.isInCombat(event.getPlayer())) {
			if (!event.getFrom().getWorld().getName().equals("world_the_end")) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "Cannot use a portal when combat tagged.");
			}
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerPearlIntoSpawn(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            ProtectedRegion region = this.plugin.getgGuardPlugin().getProtectedRegion(event.getTo(), this.plugin.getgGuardPlugin().getProtectedRegions());
            if (region != null && region.getRegionName().equals("spawn")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "Cannot pearl into spawn.");
            } else if (region != null && region.getRegionName().equals("theendportal")) {
	            event.setCancelled(true);
	            event.getPlayer().sendMessage(ChatColor.RED + "Cannot pearl into end portal area.");
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerUseSpawn(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Material type = event.getClickedBlock().getType();
            if (type == Material.ENCHANTMENT_TABLE || type == Material.WORKBENCH) {
                ProtectedRegion region = this.plugin.getgGuardPlugin().getProtectedRegion(event.getClickedBlock().getLocation(), this.plugin.getgGuardPlugin().getProtectedRegions());
                if (region != null && region.getRegionName().equals("spawn")) {
                    event.setUseInteractedBlock(Event.Result.ALLOW);
                    event.setCancelled(false);
                }
            } else if (type == Material.TRAP_DOOR || type == Material.BEACON) {
				ProtectedRegion region = this.plugin.getgGuardPlugin().getProtectedRegion(event.getClickedBlock().getLocation(), this.plugin.getgGuardPlugin().getProtectedRegions());
				if (region != null && region.getRegionName().equals("spawn")) {
					event.getPlayer().sendMessage(ChatColor.RED + "Cannot use this in spawn.");
					event.setUseInteractedBlock(Event.Result.DENY);
					event.setCancelled(true);
				}
			}
        }
    }

	@EventHandler(priority=EventPriority.LAST)
	public void onPigSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
			if (event.getEntityType() == EntityType.PIG) {
				ProtectedRegion region = this.plugin.getgGuardPlugin().getProtectedRegion(event.getEntity().getLocation(), this.plugin.getgGuardPlugin().getProtectedRegions());
				if (region != null && region.getRegionName().equals("spawn")) {
					event.setCancelled(false);
				}
			}
		}
	}
}
