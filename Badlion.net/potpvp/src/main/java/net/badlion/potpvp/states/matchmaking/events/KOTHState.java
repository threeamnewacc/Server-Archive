package net.badlion.potpvp.states.matchmaking.events;

import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.events.KOTH;
import net.badlion.potpvp.managers.RespawnManager;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class KOTHState extends GameState implements Listener {

    public KOTHState() {
        super("koth_state", "they are in a KOTH.", GroupStateMachine.getInstance());
    }

	@Override
	public void before(Group group, Object o) {
		super.before(group, o);

		// Hide respawning players
		RespawnManager.handlePlayerRespawningVisibility((Game) o, group, true);
	}

	@Override
	public void after(Group group) {
		// Show respawning players
		RespawnManager.handlePlayerRespawningVisibility(GameState.getGroupGame(group), group, false);

		// Call after, we need the group game above
		super.after(group);
	}

    @EventHandler(priority = EventPriority.LAST, ignoreCancelled=true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Group group = PotPvP.getInstance().getPlayerGroup(player);

            if (this.contains(group)) {
                KOTH game = (KOTH) GameState.getGroupGame(group);

                Entity damager = event.getDamager();
                Player damagePlayer = null;
                if (damager instanceof Projectile) {
                    damagePlayer = ((Projectile) damager).getShooter() != null && ((Projectile) damager).getShooter() instanceof Player ? (Player) ((Projectile) damager).getShooter() : null;
                } else if (damager instanceof Player) {
                    damagePlayer = (Player) damager;
                }

                if (damagePlayer != null) {
                    // Don't let allies hurt each other
                    if (!game.canHurt(damagePlayer, player)) {
	                    event.setCancelled(true);
                        return;
                    }

                    // Don't track damage that we did to ourselves
                    if (!damagePlayer.getUniqueId().equals(player.getUniqueId())) {
                        game.putLastDamage(damagePlayer.getUniqueId(), player.getUniqueId(), event.getDamage(), event.getFinalDamage());
                    }
                }
            }
        }
    }

}
