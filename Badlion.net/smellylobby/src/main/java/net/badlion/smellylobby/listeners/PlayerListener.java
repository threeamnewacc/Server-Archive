package net.badlion.smellylobby.listeners;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.FlightGCheatManager;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.events.AsyncPlayerQuitEvent;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellylobby.SmellyLobby;
import net.badlion.smellylobby.helpers.NavigationInventoryHelper;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {

	public static Map<UUID, Boolean> playerVisibility = new LinkedHashMap<>();

	private Map<UUID, Long> hidePlayerCooldown = new HashMap<>();

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		if (event.getPlayer().isOp()) {
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		if (event.getItem() != null && event.getItem().getType() == Material.INK_SACK) {
			if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
				return;
			}

			if (this.hidePlayerCooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis() - hidePlayerCooldown.get(player.getUniqueId()) <= 1000 * 3) {
				player.sendMessage(ChatColor.RED + "Please wait " + (3 - (Math.round(System.currentTimeMillis() - hidePlayerCooldown.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
				return;
			}

			if (event.getItem().getData().getData() == (short) 10) {
				// Players are on
				PlayerListener.playerVisibility.put(player.getUniqueId(), false);
			} else if (event.getItem().getData().getData() == (short) 8) {
				// Players are off
				PlayerListener.playerVisibility.put(player.getUniqueId(), true);
			}

			PlayerListener.updatePlayerVisibility(player.getUniqueId(), false);

			this.hidePlayerCooldown.put(player.getUniqueId(), System.currentTimeMillis());

			return;
		}

		if (event.getClickedBlock() != null && event.getAction() == Action.PHYSICAL
				&& event.getClickedBlock().getType().equals(Material.IRON_PLATE)) {
			event.setCancelled(true);

			// I BELIEVE I CAN FLY!!!!!!!!!!!!!!!
			FlightGCheatManager.addToMapping(player, 20 * 7);
			player.setVelocity(player.getLocation().getDirection().multiply(3));
			player.setVelocity(new Vector(player.getVelocity().getX(), 1.25D, player.getVelocity().getZ()));
		} else if (event.getItem() != null && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
			if (event.getItem().getType() == Material.WATCH) {
				event.setCancelled(true);

				// Open up the server inventory
				NavigationInventoryHelper.openNavigationInventory(player);
			} else if (event.getItem().getType() == Material.REDSTONE) {
				event.setCancelled(true);

				// Leave queue
				BukkitUtil.runTaskAsync(new Runnable() {
					@Override
					public void run() {
						SmellyLobby.getInstance().leaveQueue(player.getUniqueId(), true);
					}
				});
			}
		}
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		player.setWalkSpeed(0.2F);
		player.setGameMode(GameMode.SURVIVAL);
		player.teleport(SmellyLobby.getInstance().getSpawnLocation());

		// Disables Zen's Minimap entity/cave trackers
		player.sendMessage("§3 §6 §3 §6 §3 §6 §e ");
		player.sendMessage("§3 §6 §3 §6 §3 §6 §d ");

		// Send MOTD
		for (String string : SmellyLobby.getInstance().getMessageOfTheDay()) {
			player.sendMessage(string);
		}

		SmellyLobby.getInstance().giveLobbyItems(player);

		// Give them the server's scoreboard
		// Gberry - Hack to get around his bug with mainscoreboard
		// TODO: Fix actual scoreboard bug
		player.setScoreboard(SmellyLobby.getInstance().getServer().getScoreboardManager().getNewScoreboard());
		player.setScoreboard(SmellyLobby.getInstance().getScoreboard());


		UserDataManager.UserData userData = UserDataManager.getUserData(player);

		// Player visibility junk, moved from async delayed login event
		PlayerListener.playerVisibility.put(player.getUniqueId(), userData.arePlayersVisible());
		PlayerListener.updatePlayerVisibility(player.getUniqueId(), true);

		if (userData.isLobbyFlight()) {
			SmellyLobby.tryEnableFlight(player, userData);
		}
	}

	@EventHandler
	public void onAsyncPlayerQuitEvent(AsyncPlayerQuitEvent event) {
		SmellyLobby.getInstance().leaveQueue(event.getUuid(), false);
	}

	// Disable NPC's getting damaged
	@EventHandler
	public void onDamageNPC(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && SmellyLobby.npcs.contains(event.getEntity())) {
			event.setCancelled(true);
		}
	}

	// Handle players opening inventories via NPC interaction
	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();

		// Did they click on an NPC?
		if (event.getRightClicked() instanceof Player && SmellyLobby.npcs.contains(event.getRightClicked())) {
			switch (ChatColor.stripColor(((Player) event.getRightClicked()).getName())) {
				case "Arena PvP":
					NavigationInventoryHelper.openArenaPvPInventory(player);
					break;
				case "Hosted UHC":
					NavigationInventoryHelper.openUHCInventory(player);
					break;
				case "Survival Games":
					NavigationInventoryHelper.openUnrankedSGInventory(player);
					break;
				case "Mini UHC":
					NavigationInventoryHelper.openMiniUHCInventory(player);
					break;
				case "Free For All":
					NavigationInventoryHelper.openFFAInventory(player);
					break;
				case "UHC Meetup":
					NavigationInventoryHelper.openUHCMeetupInventory(player);
					break;
				default:
					// They interacted with an NPC that shouldn't have been spawned? Report that shit
					throw new IllegalArgumentException("\"" + ((Player) event.getRightClicked()).getName()
							+ "\" is an invalid type when player interacts with NPC.");
			}
		}
	}

	@EventHandler
	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		// Reset walk speed
		event.getPlayer().setWalkSpeed(0.2F);

		// Respawn the bitch
		event.setRespawnLocation(SmellyLobby.getInstance().getSpawnLocation());
	}

	@EventHandler
	public void onVoidDamageEvent(EntityDamageEvent event) {
		if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
			event.setCancelled(true);

			if (event.getEntity() instanceof Player) {
				final Player player = (Player) event.getEntity();
				if (player.getVehicle() != null) {
					final Entity vehicle = player.getVehicle();
					vehicle.eject();

					new BukkitRunnable() {
						public void run() {
							player.teleport(SmellyLobby.getInstance().getSpawnLocation());
							vehicle.teleport(SmellyLobby.getInstance().getSpawnLocation());
						}
					}.runTaskLater(SmellyLobby.getInstance(), 1L);

					new BukkitRunnable() {
						public void run() {
							vehicle.setPassenger(player);

						}
					}.runTaskLater(SmellyLobby.getInstance(), 2L);
				} else {
					player.teleport(SmellyLobby.getInstance().getSpawnLocation());
				}
			}
		}
	}

	public static void updatePlayerVisibility(UUID uuid, boolean login) {
		// Le player
		final Player player = SmellyLobby.getInstance().getServer().getPlayer(uuid);
		// Check if they are vanishing or showing players
		boolean visible = PlayerListener.playerVisibility.get(uuid);
		for (Player op : SmellyLobby.getInstance().getServer().getOnlinePlayers()) {
			if (!visible) {
				// They are hiding players
				player.hidePlayer(op);
			} else {
				// They are showing players
				player.showPlayer(op);

				if (Cosmetics.getInstance().isMorphsEnabled()) {
					if (op != player && CosmeticsManager.getCosmeticsSettings(op.getUniqueId()) != null && CosmeticsManager.getCosmeticsSettings(op.getUniqueId()).getActiveMorph() != null) {
						// They are showing an unmorphed player, so show the morph
						final Player opFinal = op;
						BukkitUtil.runTaskLater(new Runnable() {
							@Override
							public void run() {
								new MorphUtil(CosmeticsManager.getCosmeticsSettings(opFinal.getUniqueId()).getActiveMorph().getMorphType(), opFinal).sendPlayerSetMorph(player, true);
							}
						}, 5L);
					}
				}
			}

			if (PlayerListener.playerVisibility.get(op.getUniqueId()) != null) {
				if (!PlayerListener.playerVisibility.get(op.getUniqueId())) {
					op.hidePlayer(player);
				} else {
					op.showPlayer(player);

					if (Cosmetics.getInstance().isMorphsEnabled()) {
						if (op != player && CosmeticsManager.getCosmeticsSettings(player.getUniqueId()) != null && CosmeticsManager.getCosmeticsSettings(player.getUniqueId()).getActiveMorph() != null) {
							final Player opFinal = op;
							BukkitUtil.runTaskLater(new Runnable() {
								@Override
								public void run() {
									new MorphUtil(CosmeticsManager.getCosmeticsSettings(player.getUniqueId()).getActiveMorph().getMorphType(), player).sendPlayerSetMorph(opFinal, true);
								}
							}, 5L);
						}
					}
				}
			}
		}

		// Update their inventory item
		player.getInventory().setItem(1, ItemStackUtil.createItem(Material.INK_SACK, (short) (!visible ? 8 : 10), ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Player Visibility - " + (!visible ? ChatColor.RED + "OFF" : ChatColor.GREEN + "ON")));

		// If they haven't just logged in, change it their UserData
		if (!login) {
			UserDataManager.getUserData(uuid).setArePlayersVisible(visible);
		}
	}

}
