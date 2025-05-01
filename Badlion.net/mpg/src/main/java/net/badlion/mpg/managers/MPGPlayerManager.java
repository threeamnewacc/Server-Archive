package net.badlion.mpg.managers;

import net.badlion.gberry.GMap;
import net.badlion.gberry.GMapManager;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.bukkitevents.MPGCreatePlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MPGPlayerManager implements Listener, GMap<MPGPlayer> {

    private static Map<UUID, MPGPlayer> playerMap = new HashMap<>();
    private static Map<MPGPlayer.PlayerState, ConcurrentLinkedQueue<MPGPlayer>> stateToPlayers = new HashMap<>();

    public static void initialize() {
        for (MPGPlayer.PlayerState state : MPGPlayer.PlayerState.values()) {
            MPGPlayerManager.stateToPlayers.put(state, new ConcurrentLinkedQueue<MPGPlayer>());
        }

        MPGPlayerManager mpgPlayerManager = new MPGPlayerManager();
        GMapManager.getInstance().register(mpgPlayerManager);
        MPG.getInstance().getServer().getPluginManager().registerEvents(mpgPlayerManager, MPG.getInstance());
    }

    @Override
    public String getName() {
        return "mpg_player_manager";
    }

    @Override
    public Map<UUID, MPGPlayer> getMap() {
        return MPGPlayerManager.playerMap;
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
	    Player player = event.getPlayer();

        if (MPGPlayerManager.getMPGPlayer(player) != null) {
            return;
        }

	    // MPGPlayers are made manually for matchmaking servers
	    // If a spectator joins after the game starts, we call MPGCreatePlayerEvent elsewhere
	    if (!MPG.USES_MATCHMAKING) {
		    MPGCreatePlayerEvent mpgCreatePlayerEvent = new MPGCreatePlayerEvent(player);
		    MPG.getInstance().getServer().getPluginManager().callEvent(mpgCreatePlayerEvent);

		    if (mpgCreatePlayerEvent.getMPGPlayer() == null) {
			    throw new RuntimeException("MPGPlayer object not properly extended and created");
		    }
	    }
    }

    /**
     * All players need to be added to this as soon as they join the server
     */
    public static void storeMPGPlayer(MPGPlayer mpgPlayer, UUID uuid, MPGPlayer.PlayerState state) {
        MPGPlayerManager.stateToPlayers.get(state).add(mpgPlayer);
        MPGPlayerManager.playerMap.put(uuid, mpgPlayer);
    }

    public static void updateMPGPlayerState(UUID uuid, MPGPlayer.PlayerState state) {
        MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(uuid);
        MPGPlayer.PlayerState oldState = mpgPlayer.getState();

        MPGPlayerManager.stateToPlayers.get(oldState).remove(mpgPlayer);
        MPGPlayerManager.stateToPlayers.get(state).add(mpgPlayer);
    }

	public static MPGPlayer getMPGPlayer(Player player) {
		return MPGPlayerManager.getMPGPlayer(player.getUniqueId());
	}

    public static MPGPlayer getMPGPlayer(UUID uuid) {
        return MPGPlayerManager.playerMap.get(uuid);
    }

    public static ConcurrentLinkedQueue<MPGPlayer> getMPGPlayersByState(MPGPlayer.PlayerState state) {
        return MPGPlayerManager.stateToPlayers.get(state);
    }

    public static Collection<MPGPlayer> getAllMPGPlayers() {
        List<MPGPlayer> mpgPlayers = new ArrayList<>();
        for (Object o : MPGPlayerManager.playerMap.values()) {
            mpgPlayers.add((MPGPlayer) o);
        }

        return Collections.unmodifiableCollection(mpgPlayers);
    }

    /**
     * Used to remove people completely from being able to join (they dont have a state)
     */
    public static boolean removeMPGPlayer(UUID uuid) {
        MPGPlayer mpgPlayer = (MPGPlayer) MPGPlayerManager.playerMap.remove(uuid);
        return MPGPlayerManager.stateToPlayers.get(mpgPlayer.getState()).remove(mpgPlayer);
    }


}
