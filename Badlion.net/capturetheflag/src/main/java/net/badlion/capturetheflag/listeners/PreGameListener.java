package net.badlion.capturetheflag.listeners;

import net.badlion.capturetheflag.CTFPlayer;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PreGameListener implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.GAME_COUNTDOWN || MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.PRE_GAME) {
            CTFPlayer player = (CTFPlayer) MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId());
            if (player.getState() == MPGPlayer.PlayerState.PLAYER) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.GAME_COUNTDOWN || MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.PRE_GAME) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerShootBow(EntityShootBowEvent event) {
        if (MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.GAME_COUNTDOWN || MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.PRE_GAME) {
            if (event.getEntity() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }

}
