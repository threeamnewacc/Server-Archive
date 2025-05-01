package net.badlion.uhc.listeners;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.commands.VanishCommand;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class HostListener implements Listener {

    @EventHandler(priority=EventPriority.LOW)
    public void onHostLogin(PlayerLoginEvent event) {
        // Automated server
        if (BadlionUHC.getInstance().isMiniUHC()) {
            return;
        }

        // By default first person who connects is made host
        if (BadlionUHC.getInstance().getHost() == null && (event.getPlayer().hasPermission("badlion.uhctrial") || event.getPlayer().isOp())) {
            UHCPlayerManager.addNewUHCPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName(), UHCPlayer.State.HOST);
        }

        if (BadlionUHC.getInstance().getHost() != null && BadlionUHC.getInstance().getHost().getUUID().equals(event.getPlayer().getUniqueId())) {
            event.setResult(PlayerLoginEvent.Result.ALLOWED);
        }
    }

    @EventHandler
    public void hostJoinEvent(final PlayerJoinEvent event) {
        if (UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.HOST).contains(UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()))) {
            event.getPlayer().setGameMode(GameMode.CREATIVE);
            BadlionUHC.getInstance().getServer().getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
                @Override
                public void run() {
                    VanishCommand.vanishPlayer(event.getPlayer());
                    BadlionUHC.getInstance().addMuteBanPerms(event.getPlayer());
                    event.getPlayer().setIgnoreXray(true);
                }
            }, 1);
        }
    }

}
