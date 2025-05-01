package net.badlion.capturetheflag.listeners;

import net.badlion.capturetheflag.CTF;
import net.badlion.capturetheflag.inventories.TeamSelectorInventory;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.logging.Level;

public class CTFLobbyListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST) // Call after MPG PlayerJoinEvent
    public void onPlayerJoin(PlayerJoinEvent event) {
	    Bukkit.getLogger().log(Level.INFO, MPG.getInstance().getServerState().toString() + " - " + MPGPlayerManager.getMPGPlayer(event.getPlayer()).getState().toString());
	    if (MPG.getInstance().getServerState() == MPG.ServerState.LOBBY &&
                MPGPlayerManager.getMPGPlayer(event.getPlayer()).getState() == MPGPlayer.PlayerState.PLAYER) {
            TeamSelectorInventory.giveSelectTeamItem(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
	    if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
		    if (MPG.getInstance().getServerState() == MPG.ServerState.LOBBY &&
				    MPGPlayerManager.getMPGPlayer(event.getPlayer()).getState() == MPGPlayer.PlayerState.PLAYER) {
			    if (event.getItem() != null && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()
					    && event.getItem().getItemMeta().getDisplayName().equals(TeamSelectorInventory.selectTeamItem.getItemMeta().getDisplayName())) {
				    BukkitUtil.openInventory(event.getPlayer(), TeamSelectorInventory.teamSelector.getMainInventory());
			    }
		    }
	    }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory().getHolder() != null) {
            if (event.getClickedInventory().getHolder() instanceof Player) {
                Player player = (Player) event.getClickedInventory().getHolder();
                if (player != null) {
                    if (CTF.getInstance().getCTFGame().getGameState() == MPGGame.GameState.LOBBY &&
                            MPGPlayerManager.getMPGPlayer(player).getState() == MPGPlayer.PlayerState.PLAYER
                            && CTF.getInstance().getCTFGame().getGameState() != MPGGame.GameState.GAME) {
                        event.setCancelled(true);
                    }
                }
            }

        }
    }

    @EventHandler (priority = EventPriority.LAST)
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        if (CTF.getInstance().getCTFGame().getGameState() == MPGGame.GameState.LOBBY) {
            TeamSelectorInventory.giveSelectTeamItem(event.getPlayer());
        }
    }

    // TODO deal with the disconnected players
}
