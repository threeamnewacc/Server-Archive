package net.badlion.potpvp.states.matchmaking;

import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.events.UHCMeetup;
import net.badlion.potpvp.matchmaking.Match;
import net.badlion.potpvp.matchmaking.PartyFFAMatch;
import net.badlion.potpvp.matchmaking.RedRoverMatch;
import org.bukkit.GameMode;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class RegularMatchState extends GameState implements Listener {

    public RegularMatchState() {
        super("regular_match", "they are in a match.", GroupStateMachine.getInstance());
    }

	@Override
	public void after(Group element) {
		super.after(element);

		// Reset their maximum no damage ticks for Combo
		for (Player player : element.players()) {
			player.setMaximumNoDamageTicks(20);
		}
	}

	public Match getMatchFromGroup(Group group) {
        Game game = GameState.getGroupGame(group);

        if (game == null) {
            throw new RuntimeException("Game requested was null for " + group);
        } else if (game instanceof Match) {
            return (Match) game;
        } else {
            throw new RuntimeException("Internal error with invalid states/matches " + group);
        }
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Group group = PotPvP.getInstance().getPlayerGroup(player);
        Game game = GameState.getGroupGame(group);

	    // Check to be safe & for red rover matches
	    if (game instanceof RedRoverMatch) {
		    if (player.getGameMode() != GameMode.SURVIVAL || ((RedRoverMatch) game).isSelectingFighter(player)) {
			    event.setCancelled(true);
			    return;
		    }
	    }

        // Remove item from our cache if party match
        if ((group.isParty() && this.contains(group)) || game instanceof UHCMeetup) {
            game.getArena().removeItemDrop(event.getItem());
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onTeamDamage(EntityDamageByEntityEvent event) {
        Player target = null;
        if (event.getEntity() instanceof Player) {
            target = (Player) event.getEntity();
        }

        // Nothing more to do here
        if (target == null) {
            return;
        }

        Player player = null;
        if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            player = (Player) ((Projectile) event.getDamager()).getShooter();
        }

        Group group = PotPvP.getInstance().getPlayerGroup(player);
        if (this.contains(group) && group.isParty()) { // Avoid weird issues where someone can not hurt themselves with an arrow
            Match match = this.getMatchFromGroup(group);

            if (match.getGroup1() == group) {
                if (match.getParty1AlivePlayers().contains(player) && match.getParty1AlivePlayers().contains(target)) {
	                // Party FFA check
	                if (!(match instanceof PartyFFAMatch)) {
		                event.setCancelled(true);
	                }
                }
            } else if (match.getGroup2() == group) {
                if (match.getParty2AlivePlayers().contains(player) && match.getParty2AlivePlayers().contains(target)) {
	                // Party FFA check
	                if (!(match instanceof PartyFFAMatch)) {
		                event.setCancelled(true);
	                }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onTeamDamageHorse(EntityDamageByEntityEvent event) {
        Horse horse = null;
        if (event.getEntity() instanceof Horse) {
            horse = (Horse) event.getEntity();
        }

        // Nothing more to do here
        if (horse == null) {
            return;
        }

        Player player = null;
        if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            player = (Player) ((Projectile) event.getDamager()).getShooter();
        }

        if (horse.getPassenger() == null || !(horse.getPassenger() instanceof Player)) return;

        Group group = PotPvP.getInstance().getPlayerGroup(player);
        if (this.contains(group) && group.isParty()) { // Avoid weird issues where someone can not hurt themselves with an arrow
            Match match = this.getMatchFromGroup(group);

	        if (match instanceof PartyFFAMatch) return;

            if (match.getGroup1() == group) {
                if (match.getParty1AlivePlayers().contains(player) && match.getParty1AlivePlayers().contains(((Player) horse.getPassenger()))) {
                    event.setCancelled(true);
                }
            } else if (match.getGroup2() == group) {
                if (match.getParty2AlivePlayers().contains(player) && match.getParty2AlivePlayers().contains(((Player) horse.getPassenger()))) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
