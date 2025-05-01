package net.badlion.mpglobby.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.AsyncPlayerQuitEvent;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.mpglobby.MPGLobby;
import net.badlion.mpglobby.QueueType;
import net.badlion.mpglobby.inventories.GameQueueInventory;
import net.badlion.mpglobby.inventories.SettingsInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class LobbyListener implements Listener {

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
	    final Player player = event.getPlayer();

	    // Always give mute/ban permissions to staff members
	    if (player.hasPermission("badlion.staff")) {
		    MPGLobby.getInstance().addMuteBanPerms(player);
	    }

	    MPGLobby.getInstance().teleportToSpawnAndGiveItems(event.getPlayer());

	    // Login messages
	    player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
	    player.sendMessage(ChatColor.BLUE + "              Welcome to " + MPGLobby.MOTD_DESCRIPTION + "!");
	    player.sendMessage(ChatColor.BLUE + "           Please report any bugs on the forums at:");
	    player.sendMessage(ChatColor.BLUE + "           https://www.badlion.net/forum/thread/114054");
	    player.sendMessage("");
	    player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Right click a sign or the compass to join a queue!");
	    player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Right click a sign or the compass to join a queue!");
	    player.sendMessage(ChatColor.GREEN + "");
	    player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
	    player.sendMessage(ChatColor.AQUA + "Use the '/party' command to create a party!");
	    player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));

	    BukkitUtil.runTaskAsync(new Runnable() {
		    @Override
		    public void run() {
			    String serverName = MPGLobby.getInstance().checkIfInMatch(player.getUniqueId());

			    // Check if player is already in a match and failed to connect
			    if (serverName != null) {
				    String message = ChatColor.BOLD.toString() + ChatColor.YELLOW + "You're already in a match on " + serverName + ", transferring you now...";
				    player.sendMessage(message);
				    player.sendMessage(message);
				    player.sendMessage(message);

				    Gberry.sendToServer(player, serverName);
			    }
		    }
	    });
    }

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event) {
		if (!event.getWhoClicked().isOp()) {
			event.setCancelled(true);
		}
	}

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();

	    // Cancel before our action check so we can cancel the compass thing
        if (!player.isOp()) {
            event.setCancelled(true);
        }

	    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

	    if (UserDataManager.getUserData(player) == null) {
		    player.sendMessage(ChatColor.RED + "Your data has not loaded yet, try again in a few seconds.");
		    return;
	    }

	    if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.WALL_SIGN) {
		    Block block = event.getClickedBlock();

		    // Is this the leave queue sign?
		    if (block.getLocation().equals(MPGLobby.getInstance().getLeaveQueueSignLocation())) {
			    // Check cooldown
			    if (MPGLobby.getInstance().hasCooldown(player.getUniqueId())) {
				    player.sendMessage(ChatColor.RED + "Please do not spam the queue signs!");
				    return;
			    }

			    player.performCommand("leave");
		    } else {
			    // Find the queue this sign corresponds to
			    for (QueueType queueType : QueueType.values()) {
				    if (block.getLocation().equals(queueType.getSign().getLocation())) {
					    // Check cooldown
					    if (MPGLobby.getInstance().hasCooldown(player.getUniqueId())) {
						    player.sendMessage(ChatColor.RED + "Please do not spam the queue signs!");
						    return;
					    }

					    MPGLobby.getInstance().joinQueue(event.getPlayer(), queueType);
				    }
			    }
		    }
	    } else if (event.getItem() != null) {
            switch (event.getItem().getType()) {
	            case COMPASS: // Join queue item
                    GameQueueInventory.openGameQueueInventory(player);
		            break;
	            case WATCH: // Settings item
		            SettingsInventory.getInstance().openSettingsInventory(player);
		            break;
	            case REDSTONE: // Leave queue item
		            player.performCommand("leave");
		            break;
            }
        }
    }

    @EventHandler
    public void onAsyncPlayerQuitEvent(AsyncPlayerQuitEvent event) {
	    MPGLobby.getInstance().leaveQueue(event.getUuid(), false);
    }

	@EventHandler
	public void onChunkUnloadEvent(ChunkUnloadEvent event) {
		event.setCancelled(true);
	}

}
