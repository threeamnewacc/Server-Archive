package net.badlion.survivalgames.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.managers.SGMapManager;
import net.badlion.survivalgames.managers.SGPlayerManager;
import net.badlion.survivalgames.SGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Set;

public class LobbyListener implements Listener {

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            event.getPlayer().setOp(false);
        }

        int onlinePlayers = SurvivalGames.getInstance().getServer().getOnlinePlayers().size();

        if (SurvivalGames.getInstance().getState() == SurvivalGames.SGState.PRE_START && onlinePlayers < SGMapManager.MIN_PLAYERS) {
            // We don't have enough players to start voting yet, tell the ones who are on
            Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.RED + (SGMapManager.MIN_PLAYERS - onlinePlayers) + ChatColor.DARK_GREEN + " more players need to start the game!");
            // wait for more onPlayerJoin events
        } else if (SurvivalGames.getInstance().getState() == SurvivalGames.SGState.PRE_START) {
             SGMapManager.startMapVoting();
        } else if (SurvivalGames.getInstance().getState() == SurvivalGames.SGState.VOTING) {
            event.getPlayer().sendMessage(SurvivalGames.SG_PREFIX + ChatColor.GOLD + "Vote for the next map :");

            for (int i = 0; i < SGMapManager.NUM_OF_MAP_CHOICES; i++) {
                event.getPlayer().sendMessage(SurvivalGames.SG_PREFIX + ChatColor.DARK_GREEN + "Map " + ChatColor.GOLD + (i + 1) + ": " + ChatColor.RED + SGMapManager.getVoteWorlds().get(i).getgWorld().getNiceWorldName() + ChatColor.DARK_GREEN + " currently has " + SGMapManager.getVoteWorldsVotes().get(SGMapManager.getVoteWorlds().get(i)) + " votes");
            }
            event.getPlayer().sendMessage(SurvivalGames.SG_PREFIX + ChatColor.DARK_GREEN + "Use the " + ChatColor.GOLD + "/vote \"<map number>\"" + ChatColor.DARK_GREEN + " command to cast your vote!");
        }

        if (SurvivalGames.getInstance().getState().ordinal() < SurvivalGames.SGState.START_COUNTDOWN.ordinal()) {
            SurvivalGames.getInstance().prepLobby(event.getPlayer());
        }

        Set<SGPlayer> sgPlayers = SGPlayerManager.getPlayersByState(SGPlayer.State.SPECTATOR);
        for (SGPlayer sgPlayer : sgPlayers) {
            Player pl = SurvivalGames.getInstance().getServer().getPlayer(sgPlayer.getUuid());
            if (pl != null) {
                event.getPlayer().hidePlayer(pl);
            }
        }

	    // Hotbar items for pets/particles
	    //event.getPlayer().getInventory().setItem(7, ParticleInventory.getOpenParticleInventoryItem());
	    //event.getPlayer().getInventory().setItem(8, PetInventory.getOpenPetInventoryItem());
    }

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		if (SurvivalGames.getInstance().getState().ordinal() < SurvivalGames.SGState.STARTED.ordinal()) {
			event.setCancelled(true);
		}
	}

}
