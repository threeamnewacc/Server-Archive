package net.badlion.survivalgames.listeners;

import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.managers.SGPlayerManager;
import net.badlion.survivalgames.SGPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class DeathMatchListener implements Listener {

    @EventHandler
    public void onPlayerDoAnything(PlayerInteractEvent event) {
        if (SurvivalGames.getInstance().getState() == SurvivalGames.SGState.DEATH_MATCH_COUNTDOWN) {
            SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(event.getPlayer().getUniqueId());
            if (sgPlayer.getState() == SGPlayer.State.ALIVE) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (event.getPlayer().getItemInHand() != null && (event.getPlayer().getItemInHand().getType() == Material.BOW
                                                                    || event.getPlayer().getItemInHand().getType() == Material.FISHING_ROD
                                                                    || event.getPlayer().getItemInHand().getType() == Material.FLINT_AND_STEEL)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerHurt(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (SurvivalGames.getInstance().getState() == SurvivalGames.SGState.DEATH_MATCH_COUNTDOWN) {
                SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(((Player)event.getEntity()).getUniqueId());
                if (sgPlayer.getState() == SGPlayer.State.ALIVE) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
