package net.badlion.capturetheflag.listeners;

import net.badlion.capturetheflag.CTF;
import net.badlion.capturetheflag.CTFPlayer;
import net.badlion.capturetheflag.manager.FlagManager;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;


public class CTFGameListener implements Listener {

    @EventHandler
    public void onPlayerItemDrop(PlayerDropItemEvent event) {
        if (CTF.getInstance().getCTFGame().getGameState().ordinal() >= MPGGame.GameState.GAME.ordinal()) {
            CTFPlayer ctfPlayer = (CTFPlayer) MPGPlayerManager.getMPGPlayer(event.getPlayer());
            if (ctfPlayer.getState() == MPGPlayer.PlayerState.PLAYER && ctfPlayer.isCarryingFlag()) {
                if (FlagManager.isFlag(event.getItemDrop().getItemStack())) {
                    FlagManager.dropFlag(ctfPlayer, ctfPlayer.getFlagTeam(), event.getPlayer().getLocation());
                    event.getItemDrop().remove();
                }
            }
        }
    }

    /* MPG has this covered?
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
    }
    */
}
