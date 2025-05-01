package net.badlion.survivalgames.listeners;

import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
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
        if (MPG.getInstance().getMPGGame() != null
		        && MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.DEATH_MATCH_COUNTDOWN) {
            SGPlayer sgPlayer = (SGPlayer)MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId());
            if (sgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (event.getPlayer().getItemInHand() != null
		                    && (event.getPlayer().getItemInHand().getType() == Material.BOW
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
            if (MPG.getInstance().getMPGGame() != null
		            && MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.DEATH_MATCH_COUNTDOWN) {
                SGPlayer sgPlayer = (SGPlayer)MPGPlayerManager.getMPGPlayer(event.getEntity().getUniqueId());
                if (sgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
