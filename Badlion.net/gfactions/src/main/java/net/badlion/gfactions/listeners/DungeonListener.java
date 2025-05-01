package net.badlion.gfactions.listeners;

import java.util.HashSet;

import net.badlion.gfactions.managers.DungeonManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;

public class DungeonListener implements Listener {
	
	private GFactions plugin;
	private DungeonManager dm;
	private HashSet<EntityType> allowedEntities;
	
	public DungeonListener(GFactions plugin) {
		this.plugin = plugin;
		this.dm = this.plugin.getDungeonManager();
		this.allowedEntities = new HashSet<EntityType>();
		this.allowedEntities.add(EntityType.ZOMBIE);
		this.allowedEntities.add(EntityType.SKELETON);
		this.allowedEntities.add(EntityType.CREEPER);
		this.allowedEntities.add(EntityType.SPIDER);
		this.allowedEntities.add(EntityType.BLAZE);
		this.allowedEntities.add(EntityType.GHAST);
		this.allowedEntities.add(EntityType.GIANT);
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onMobSpawn(CreatureSpawnEvent event) {
		if (this.dm.getSpawnedDungeonWorldName() != null && this.dm.getSpawnedDungeonWorldName().equals(event.getLocation().getWorld().getName())) {
			if (!this.allowedEntities.contains(event.getEntityType())) {
				event.setCancelled(true);
				return;
			}
			// SPAWN MORE MOBS!
			Location location = event.getLocation();
			if (location.getBlockY() > this.dm.getCurrentDungeon().getMaxMobLevelY()) {
				event.setCancelled(true);
				return;
			}
			
			if (event.getSpawnReason() == SpawnReason.NATURAL) {
				for (int i = 0; i < 2; i++) {
					int num = this.plugin.generateRandomInt(0, 99);
					if (this.dm.getZombieSpawnPercentage() != 0 && 0 <= num && num < this.dm.getZombieRawNumber()) {
						// Spawn Zombie
						location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
					} else if (this.dm.getSkeletonSpawnPercentage() != 0 && this.dm.getZombieRawNumber() <= num && num < this.dm.getSkeletonRawNumber()) {
						// Spawn Skeleton
						((Skeleton)location.getWorld().spawnEntity(location, EntityType.SKELETON)).getEquipment().setItemInHand(new ItemStack(Material.BOW));
					} else if (this.dm.getCreeperSpawnPercentage() != 0 && this.dm.getSkeletonRawNumber() <= num && num < this.dm.getCreeperRawNumber()) {
						// Spawn Creeper
						location.getWorld().spawnEntity(location, EntityType.CREEPER);
					} else if (this.dm.getSpiderSpawnPercentage() != 0 && this.dm.getCreeperRawNumber() <= num && num < this.dm.getSpiderRawNumber()) {
						// Spawn Spider
						location.getWorld().spawnEntity(location, EntityType.SPIDER);
					} else if (this.dm.getBlazeSpawnPercentage() != 0 && this.dm.getSpiderRawNumber() <= num && num < this.dm.getBlazeRawNumber()) {
						// Spawn Blaze
						location.getWorld().spawnEntity(location, EntityType.BLAZE);
					} else if (this.dm.getGhastSpawnPercentage() != 0 && this.dm.getBlazeRawNumber() <= num && num < this.dm.getGhastRawNumber()) {
						// Spawn Ghast
						location.getWorld().spawnEntity(location, EntityType.GHAST);
					} else if (this.dm.getGiantSpawnPercentage() != 0 && this.dm.getGhastRawNumber() <= num && num <= this.dm.getGiantRawNumber()) {
						// Spawn Giant
						location.getWorld().spawnEntity(location, EntityType.GIANT);
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onCreeperExplode(EntityDamageEvent event) {
		// If a creeper blows up and it damages another mob don't hurt it
		if (this.dm.getSpawnedDungeonWorldName() != null && this.dm.getSpawnedDungeonWorldName().equals(event.getEntity().getLocation().getWorld().getName())) {
			if (event.getCause() == DamageCause.ENTITY_EXPLOSION) {
				if (!(event.getEntity() instanceof Player)) {
					event.setDamage(0);
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerEnterExitDungeon(final PlayerPortalEvent event) {
		Location from = event.getFrom();
		//Location to = event.getTo();
		//this.plugin.getLogger().info("hi");
		//this.plugin.getLogger().info(event.getCause().name());
		//this.plugin.getLogger().info(from.getWorld().getName());
		//this.plugin.getLogger().info(from.toString());
		//this.plugin.getLogger().info(event.getTo().toString());
//		this.plugin.getLogger().info(this.dm.getLocation1().toString());
//		this.plugin.getLogger().info(this.dm.getLocation2().toString());
		
		if (event.getCause() == TeleportCause.NETHER_PORTAL && from.getWorld().getName().equals(this.dm.getSpawnedDungeonWorldName())) {
			// Teleporting out of the dungeon
			int x = this.plugin.generateRandomInt(0, 1) == 0 ? this.plugin.generateRandomInt(20, 200) : this.plugin.generateRandomInt(-200, -20);
			int z = this.plugin.generateRandomInt(0, 1) == 0 ? this.plugin.generateRandomInt(20, 200) : this.plugin.generateRandomInt(-200, -20);
			event.setTo(new Location(Bukkit.getWorld("world"), x + this.dm.getLocation1().getBlockX(), Bukkit.getWorld("world").getHighestBlockYAt(x + this.dm.getLocation1().getBlockX(), 
					z + this.dm.getLocation1().getBlockZ()), z + this.dm.getLocation1().getBlockZ()));
		} else if (event.getCause() == TeleportCause.NETHER_PORTAL && from.getWorld().getName().equals("world") && this.dm.isAllowEntry() && 
				Gberry.isLocationInBetween(this.dm.getLocation1(), this.dm.getLocation2(), from)) { 
			if (this.dm.getSpawnLocation() != null) { // DEBUG
				// Do they have their PVP prot enabled?
				final Player player = event.getPlayer();
				if (this.plugin.getMapNameToJoinTime().containsKey(player.getUniqueId().toString())) {
					this.plugin.getMapNameToPvPTimeRemaining().remove(player.getUniqueId().toString());
					this.plugin.getMapNameToJoinTime().remove(player.getUniqueId().toString());
					
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
						
						@Override
						public void run() {
							// Purge from DB
							plugin.removeProtection(player);
						}
					});
					
					player.sendMessage(ChatColor.RED + "PVP Protection disabled from entering Dungeon.");
				}
				
				this.plugin.getLogger().info(this.dm.getSpawnLocation().toString());
				// one tick later
				this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
					
					@Override
					public void run() {
						event.getPlayer().teleport(dm.getSpawnLocation());
					}
					
				}, 1);
			} else {
				this.plugin.getLogger().info("NULL LOCATION");
			}
		} else {
			// Is this a dungeon portal? not allowing entry
			for (int i = 0; i < this.dm.getDungeonPortal1().size(); i++) {
				if (Gberry.isLocationInBetween(this.dm.getDungeonPortal1().get(i), this.dm.getDungeonPortal2().get(i), from)) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "This dungeon portal is not activated.");
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		// TP this man back to the real world quickly!
		if (player.getLocation().getWorld().getName().equals(this.dm.getSpawnedDungeonWorldName())) {
			int x = this.plugin.generateRandomInt(0, 1) == 0 ? this.plugin.generateRandomInt(20, 200) : this.plugin.generateRandomInt(-200, -20);
			int z = this.plugin.generateRandomInt(0, 1) == 0 ? this.plugin.generateRandomInt(20, 200) : this.plugin.generateRandomInt(-200, -20);
			player.teleport(new Location(Bukkit.getWorld("world"), x + this.dm.getLocation1().getBlockX(), Bukkit.getWorld("world").getHighestBlockYAt(x + this.dm.getLocation1().getBlockX(), 
					z + this.dm.getLocation1().getBlockZ()), z + this.dm.getLocation1().getBlockZ()));
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String name = player.getLocation().getWorld().getName();
		
		// Hack
		if (name.startsWith("dungeon")) {
			int x = this.plugin.generateRandomInt(0, 1) == 0 ? this.plugin.generateRandomInt(20, 200) : this.plugin.generateRandomInt(-200, -20);
			int z = this.plugin.generateRandomInt(0, 1) == 0 ? this.plugin.generateRandomInt(20, 200) : this.plugin.generateRandomInt(-200, -20);
			player.teleport(new Location(Bukkit.getWorld("world"), x + this.dm.getLocation1().getBlockX(), Bukkit.getWorld("world").getHighestBlockYAt(x + this.dm.getLocation1().getBlockX(), 
					z + this.dm.getLocation1().getBlockZ()), z + this.dm.getLocation1().getBlockZ()));
		}
	}
}
