package net.badlion.survivalgames.listeners;

import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.managers.SGPlayerManager;
import net.badlion.survivalgames.SGPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PreGameListener implements Listener {

    @EventHandler
    public void onPlayerDoAnything(PlayerInteractEvent event) {
        if (SurvivalGames.getInstance().getState() == SurvivalGames.SGState.START_COUNTDOWN) {
            SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(event.getPlayer().getUniqueId());
            if (sgPlayer.getState() == SGPlayer.State.ALIVE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (SurvivalGames.getInstance().getState() == SurvivalGames.SGState.START_COUNTDOWN) {
                SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(((Player) event.getEntity()).getUniqueId());
                if (sgPlayer.getState() == SGPlayer.State.ALIVE) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
