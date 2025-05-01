package net.badlion.uhc.listeners;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.commands.VanishCommand;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class SpectatorListener implements Listener {

    @EventHandler(priority=EventPriority.LOW)
    public void onSpectatorJoin(PlayerLoginEvent event) {
        if (UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.SPEC).contains(UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()))) {
            event.setResult(PlayerLoginEvent.Result.ALLOWED);
        }
    }

    @EventHandler
    public void spectatorJoinEvent(final PlayerJoinEvent event) {
        UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId());
        if (UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.SPEC).contains(uhcPlayer)) {
            // They already are done with the game
            BadlionUHC.getInstance().getServer().getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
                @Override
                public void run() {
                    VanishCommand.vanishPlayer(event.getPlayer());
                }
            }, 1);
        } else if (UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.SPEC_IN_GAME).contains(uhcPlayer)) {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
    }

}
