package net.badlion.survivalgames.managers;

import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.util.BukkitUtil;
import net.badlion.survivalgames.SGPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class SGPlayerManager extends BukkitUtil.Listener {

    private static Map<UUID, SGPlayer> players = new HashMap<>();
    private static Map<SGPlayer.State, Set<SGPlayer>> stateToPlayers = new HashMap<>();

    public static void initialize() {
        for (SGPlayer.State state : SGPlayer.State.values()) {
            SGPlayerManager.stateToPlayers.put(state, new LinkedHashSet<SGPlayer>());
        }
    }

    public static Collection<SGPlayer> getAllSGPlayers() {
        return Collections.unmodifiableCollection(SGPlayerManager.players.values());
    }

    public static SGPlayer createSGPlayer(UUID uuid, String username) {
        SGPlayer sgPlayer = new SGPlayer(uuid, username);
        SGPlayerManager.players.put(uuid, sgPlayer);
        SGPlayerManager.stateToPlayers.get(sgPlayer.getState()).add(sgPlayer);
        return sgPlayer;
    }

    public static SGPlayer getSGPlayer(UUID uuid) {
        return SGPlayerManager.players.get(uuid);
    }

    public static void updateState(UUID uuid, SGPlayer.State state) {
        SGPlayer sgPlayer = SGPlayerManager.players.get(uuid);

        // Race condition w/ tasks...just skip here
        if (sgPlayer == null) {
            return;
        }

        SGPlayerManager.stateToPlayers.get(sgPlayer.getState()).remove(sgPlayer);
        sgPlayer.setState(state);
        SGPlayerManager.stateToPlayers.get(state).add(sgPlayer);
    }

    public static Set<SGPlayer> getPlayersByState(SGPlayer.State state) {
        return SGPlayerManager.stateToPlayers.get(state);
    }

    /**
     * Should only be called if someone leaves the game before it starts
     */
    private static SGPlayer removeSGPlayer(UUID uuid) {
        SGPlayer sgPlayer = SGPlayerManager.players.remove(uuid);

        if (sgPlayer != null) {
            SGPlayerManager.stateToPlayers.get(sgPlayer.getState()).remove(sgPlayer);
        }

        return sgPlayer;
    }

    @EventHandler(priority=EventPriority.FIRST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (SurvivalGames.getInstance().getState().ordinal() < SurvivalGames.SGState.START_COUNTDOWN.ordinal()
                    && SGPlayerManager.getSGPlayer(event.getPlayer().getUniqueId()) == null) {
            SGPlayerManager.createSGPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
        }
    }

    @EventHandler(priority=EventPriority.LAST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // TODO: maybe just keep the SGPlayers?
        if (SurvivalGames.getInstance().getState().ordinal() < SurvivalGames.SGState.START_COUNTDOWN.ordinal()) {
            SGPlayerManager.removeSGPlayer(event.getPlayer().getUniqueId());
        }
    }

    public static int getOnlinePlayers(){
        return players.size();
    }

}
