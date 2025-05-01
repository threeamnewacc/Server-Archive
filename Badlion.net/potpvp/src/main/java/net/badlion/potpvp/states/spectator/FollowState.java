package net.badlion.potpvp.states.spectator;

import net.badlion.gberry.Gberry;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.statemachine.GState;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class FollowState extends GState<Group> implements Listener {

    public static Map<UUID, UUID> followerToPlayers = new HashMap<>();
    public static Map<UUID, Set<UUID>> playerToFollowers = new HashMap<>();

    public FollowState() {
        super("follower", "they are in spectator mode.", GroupStateMachine.getInstance());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        FollowState.playerToFollowers.put(event.getPlayer().getUniqueId(), new HashSet<UUID>());
    }

    @EventHandler
    public void onEnterGame(FollowedPlayerTeleportEvent event) {
        Set<UUID> players = FollowState.playerToFollowers.get(event.getPlayer().getUniqueId());
        if (players != null) {
	        if (event.getLocation() != null) {
		        for (UUID uuid : players) {
			        Player player = PotPvP.getInstance().getServer().getPlayer(uuid);
			        player.teleport(event.getLocation());
		        }
	        } else {
		        for (UUID uuid : players) {
			        Player player = PotPvP.getInstance().getServer().getPlayer(uuid);
			        player.teleport(event.getPlayer().getLocation());
		        }
	        }
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
	    // Is this player following someone?
	    UUID followingUUID = FollowState.followerToPlayers.remove(event.getPlayer().getUniqueId());
	    Player following = PotPvP.getInstance().getServer().getPlayer(followingUUID);

	    if (following != null) {
		    Set<UUID> players = FollowState.playerToFollowers.get(following.getUniqueId());
		    if (players != null) {
			    players.remove(event.getPlayer().getUniqueId());
		    }
	    }

	    Set<UUID> players = FollowState.playerToFollowers.get(event.getPlayer().getUniqueId());
	    if (players != null) {
		    for (UUID uuid : players) {
			    Player player = PotPvP.getInstance().getServer().getPlayer(uuid);
			    if (Gberry.isPlayerOnline(player)) {
				    player.sendMessage(ChatColor.GREEN + event.getPlayer().getName() + " has logged off. You are no longer following this player.");

				    try {
					    this.pop(PotPvP.getInstance().getPlayerGroup(player));
				    } catch (IllegalStateTransitionException e) {
					    PotPvP.getInstance().somethingBroke(player, PotPvP.getInstance().getPlayerGroup(player));
				    }
			    }
		    }
	    }
    }

}
