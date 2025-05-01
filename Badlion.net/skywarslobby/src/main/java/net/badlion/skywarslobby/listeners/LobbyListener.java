package net.badlion.skywarslobby.listeners;

import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.events.AsyncPlayerQuitEvent;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.skywarslobby.SkyWarsLobby;
import net.badlion.skywarslobby.inventories.GameQueueInventory;
import net.badlion.skywarslobby.inventories.KitSelectionInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

public class LobbyListener implements Listener {

	public static Location ffaClassicLocation;
    public static Location ffaOPLocation;
	public static Location kitCreationLocation;

    public LobbyListener() {
        LobbyListener.ffaClassicLocation = new Location(Bukkit.getWorld("world"), 5, 151, -15);
        LobbyListener.ffaOPLocation = new Location(Bukkit.getWorld("world"), 6, 151, -15);

        LobbyListener.kitCreationLocation = new Location(Bukkit.getWorld("world"), 15, 151, 3);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
	    SkyWarsLobby.getInstance().teleportToSpawnAndGiveItems(event.getPlayer());

	    // Hide new player from ppl who want players hidden
	    new BukkitRunnable() {
		    public void run() {
			    for (Player pl : SkyWarsLobby.getInstance().getHidingPlayers()) {
				    pl.hidePlayer(event.getPlayer());
			    }
		    }
	    }.runTaskLater(SkyWarsLobby.getInstance(), 1);

	    Player player = event.getPlayer();

	    // Login messages (Willkommen Nachrichten)
	    player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
	    player.sendMessage(ChatColor.BLUE + "                   Welcome to the Badlion SkyWars Open Beta!");
	    player.sendMessage(ChatColor.BLUE + "                     Please report any bugs on the forums.");
	    player.sendMessage("");
	    player.sendMessage(ChatColor.GREEN + "     Right click with the compass or sign to join a SkyWars Game!");
	    player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void playerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // Always cancel this if not op
        if (!player.isOp()) {
            event.setCancelled(true);
        }

        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            switch (event.getItem().getType()) {
	            case COMPASS: // Join queue item
                    GameQueueInventory.openGameQueueInventory(player);
		            break;
	            case BOOK:
		            KitSelectionInventory.openKitInventory(player);
		            break;
	            case REDSTONE_COMPARATOR: // Toggle player visibility item
                    SkyWarsLobby.getInstance().toggleHidingPlayers(player);
		            break;
	            case REDSTONE_TORCH_ON: // Leave queue item
		            player.performCommand("leave");
		            break;
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(SkyWarsLobby.getInstance().getSpawnLocation());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        SkyWarsLobby.getInstance().getHidingPlayers().remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerAsyncQuit(AsyncPlayerQuitEvent event) {
        try {
            HTTPCommon.executePOSTRequest("http://127.0.0.1:9014/RemoveFromQueue/" + event.getUuid().toString() + "/" + event.getUsername() + "/4jzyuUGb5AQUvVGLeUpx11ih4vGFF", new JSONObject());
        } catch (HTTPRequestFailException e) {
            Bukkit.getLogger().info("Error when player " + event.getUsername() + " quit " + e.getResponseCode() + ": " + e.getResponse());
        }
    }

    @EventHandler
    public void onPlayerClickSign(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.WALL_SIGN) {
	            Location location = event.getClickedBlock().getLocation();
	            if (location.equals(LobbyListener.ffaClassicLocation)) {
		            LobbyListener.joinFFAClassicQueue(event.getPlayer());
	            } else if (location.equals(LobbyListener.ffaOPLocation)) {
                    LobbyListener.joinFFAOPQueue(event.getPlayer());
                } else if (location.equals(LobbyListener.kitCreationLocation)) {
		            KitSelectionInventory.openKitInventory(event.getPlayer());
	            }
            }
        }
    }

    public static void joinFFAClassicQueue(final Player player) {
        new BukkitRunnable() {
            public void run() {
                try {
                    JSONObject result = HTTPCommon.executePOSTRequest("http://127.0.0.1:9014/AddToQueue/ffa_classic/" + player.getUniqueId().toString() + "/" + player.getName() + "/4jzyuUGb5AQUvVGLeUpx11ih4vGFF", new JSONObject());

                    if (result == null) {
                        player.sendMessage(ChatColor.RED + "Error when trying to join queue");
                    } else if (result.containsKey("success")) {
                        player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
                        player.sendMessage(ChatColor.GREEN + "Added to matchmaking. You will be put into a match when enough players have joined");
                        player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));

                        new BukkitRunnable() {
                            public void run() {
                                player.getInventory().setItem(8, ItemStackUtil.createItem(Material.REDSTONE_TORCH_ON, ChatColor.AQUA + "Leave Queue"));
                                player.updateInventory();
                            }
                        }.runTask(SkyWarsLobby.getInstance());
                    } else {
                        player.sendMessage(ChatColor.RED + "Already in queue. Wait for a match to start or /leave");
                    }
                } catch (HTTPRequestFailException e) {
                    Bukkit.getLogger().info("Error when player " + player.getName() + " joined ffa classic " + e.getResponseCode() + ": " + e.getResponse());
                }
            }
        }.runTaskAsynchronously(SkyWarsLobby.getInstance());
    }

    public static void joinFFAOPQueue(final Player player) {
        new BukkitRunnable() {
            public void run() {
                try {
                    JSONObject result = HTTPCommon.executePOSTRequest("http://127.0.0.1:9014/AddToQueue/ffa_op/" + player.getUniqueId().toString() + "/" + player.getName() + "/4jzyuUGb5AQUvVGLeUpx11ih4vGFF", new JSONObject());

                    if (result == null) {
                        player.sendMessage(ChatColor.RED + "Error when trying to join queue");
                    } else if (result.containsKey("success")) {
                        player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
                        player.sendMessage(ChatColor.GREEN + "Added to matchmaking. You will be put into a match when enough players have joined");
                        player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));

                        new BukkitRunnable() {
                            public void run() {
                                player.getInventory().setItem(8, ItemStackUtil.createItem(Material.REDSTONE_TORCH_ON, ChatColor.AQUA + "Leave Queue"));
                                player.updateInventory();
                            }
                        }.runTask(SkyWarsLobby.getInstance());
                    } else {
                        player.sendMessage(ChatColor.RED + "Already in queue. Wait for a match to start or /leave");
                    }
                } catch (HTTPRequestFailException e) {
                    Bukkit.getLogger().info("Error when player " + player.getName() + " joined ffa classic " + e.getResponseCode() + ": " + e.getResponse());
                }
            }
        }.runTaskAsynchronously(SkyWarsLobby.getInstance());
    }

}
