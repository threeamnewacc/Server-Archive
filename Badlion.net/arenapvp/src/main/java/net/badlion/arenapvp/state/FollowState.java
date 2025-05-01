package net.badlion.arenapvp.state;

import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.gberry.Gberry;
import net.badlion.statemachine.GState;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FollowState extends GState<Player> implements Listener {

	public static Map<UUID, UUID> followerToPlayers = new HashMap<>();
	public static Map<UUID, Set<UUID>> playerToFollowers = new HashMap<>();

	public FollowState() {
		super("follower", "they are in spectator mode.", TeamStateMachine.getInstance());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		FollowState.playerToFollowers.put(event.getPlayer().getUniqueId(), new HashSet<UUID>());
	}

    /*
    @EventHandler
    public void onEnterGame(FollowedPlayerTeleportEvent event) {
        Set<UUID> players = FollowState.playerToFollowers.get(event.getPlayer().getUniqueId());
        if (players != null) {
            if (event.getLocation() != null) {
                for (UUID uuid : players) {
                    Player player = ArenaLobby.getInstance().getServer().getPlayer(uuid);
                    player.teleport(event.getLocation());
                }
            } else {
                for (UUID uuid : players) {
                    Player player = ArenaLobby.getInstance().getServer().getPlayer(uuid);
                    player.teleport(event.getPlayer().getLocation());
                }
            }
        }
    }
        */

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		// Is this player following someone?
		UUID followingUUID = FollowState.followerToPlayers.remove(event.getPlayer().getUniqueId());
		Player following = ArenaPvP.getInstance().getServer().getPlayer(followingUUID);

		if (following != null) {
			Set<UUID> players = FollowState.playerToFollowers.get(following.getUniqueId());
			if (players != null) {
				players.remove(event.getPlayer().getUniqueId());
			}
		}

		Set<UUID> players = FollowState.playerToFollowers.get(event.getPlayer().getUniqueId());
		if (players != null) {
			for (UUID uuid : players) {
				Player player = ArenaPvP.getInstance().getServer().getPlayer(uuid);
				if (Gberry.isPlayerOnline(player)) {
					player.sendFormattedMessage("{0} has logged off. You are no longer following this player.", ChatColor.GREEN + event.getPlayer().getDisguisedName());

					try {
						this.transition(TeamStateMachine.spectatorState, player);
					} catch (IllegalStateTransitionException e) {
						//ArenaLobby.getInstance().somethingBroke(player, ArenaLobby.getInstance().getPlayerGroup(player));
					}
				}
			}
		}
	}
}
