package net.badlion.capturetheflag.listeners;

import net.badlion.capturetheflag.CTFGame;
import net.badlion.capturetheflag.CTFPlayer;
import net.badlion.capturetheflag.CTFWorld;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.bukkitevents.MPGCreatePlayerEvent;
import net.badlion.mpg.bukkitevents.MPGPlayerStateChangeEvent;
import net.badlion.mpg.bukkitevents.MPGServerStateChangeEvent;
import net.badlion.mpg.bukkitevents.MapManagerInitializeEvent;
import net.badlion.mpg.managers.MPGMapManager;
import net.badlion.worldrotator.GWorld;
import net.badlion.worldrotator.WorldRotator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MPGListener implements Listener {

    @EventHandler
    public void onMapManagerInitialize(MapManagerInitializeEvent event) {
        for (GWorld world : WorldRotator.getInstance().getWorlds()) {
            event.getWorlds().add(new CTFWorld(world));
        }
    }

    @EventHandler
    public void onMPGServerStateChangeEvent(MPGServerStateChangeEvent event) {
        if (event.getNewState() == MPG.ServerState.LOBBY) {
            CTFGame ctfGame = new CTFGame();
            ctfGame.setWorld(MPGMapManager.getRandomWorld());
        }
    }

    @EventHandler
    public void onMPGCreatePlayerEvent(MPGCreatePlayerEvent event) {
        CTFPlayer ctfPlayer = new CTFPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getDisguisedName());
        event.setMpgPlayer(ctfPlayer);

        // Set spectator if the game already started    // TODO: DON'T KNOW IF WE NEED THIS, LOOK INTO HOW MPG HANDLES THIS IN GAMELISTENER
        if (MPG.getInstance().getMPGGame().getGameState().ordinal() >= MPGGame.GameState.GAME_COUNTDOWN.ordinal()) {
            ctfPlayer.setState(MPGPlayer.PlayerState.SPECTATOR);
        }
    }

    @EventHandler                                     // TODO: NO LONGER NEEDED BECAUSE OF COMBAT TAGS?
    public void onMPGPlayerStateChangeEvent(MPGPlayerStateChangeEvent event) {
        if (event.getNewState() == MPGPlayer.PlayerState.DC) {
            // Transition them to spectator instead
            event.getMPGPlayer().setState(MPGPlayer.PlayerState.SPECTATOR);
            event.setCancelled(true);
        }
    }

}
