package net.badlion.uhc.listeners;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class MaxPlayerListener implements Listener {

    @EventHandler(priority=EventPriority.MONITOR)
    public void playerLogin(PlayerLoginEvent event) {
        if (BadlionUHC.getInstance().getConfigurator() == null) {
            return;
        }

        // Not open to the public yet
        if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.MAXPLAYERS.name()).getValue() == null) {
            return;
        }

        // Automated server
        if (BadlionUHC.getInstance().isMiniUHC()) {
            return;
        }

        // Nothing has interfered yet...
        if (event.getResult() == PlayerLoginEvent.Result.KICK_OTHER) {
            // We need to make a promise that whitelist is ALWAYS on when countdown starts
            if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.PRE_START && BadlionUHC.getInstance().isWhitelistBoolean()) {
                // Pre-Whitelist allowed
                if (BadlionUHC.getInstance().isAllowDonators() && event.getPlayer().hasPermission("badlion.donator")) {
                    event.setResult(PlayerLoginEvent.Result.ALLOWED);
                } else if (!BadlionUHC.getInstance().getWhitelist().contains(event.getPlayer().getName().toLowerCase())) {
                    event.setKickMessage("You are not whitelisted! Become a donator to get access to pre-whitelist.");
                    event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
                } else {
                    event.setResult(PlayerLoginEvent.Result.ALLOWED);
                }
            } else if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.PRE_START) {
                // At this point, whitelist is off, server might be full but we check that below
                // Everyone should be able to get on...period
                // Might need fixing
                event.setResult(PlayerLoginEvent.Result.ALLOWED);

                // If the UHC has not started and they are not a donator check to see if we are full
                if (!event.getPlayer().hasPermission("badlion.donatorplus")) { // Donator+ and higher can late join
                    int playerCount = UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER).size();
                    if (playerCount >= (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.MAXPLAYERS.name()).getValue()) {
                        event.setKickMessage("There is no more room for UHC players! Become a donator to get access to pre-whitelist and reserved slots.");
                        event.setResult(PlayerLoginEvent.Result.KICK_FULL);
                    }
                }
            } else if (BadlionUHC.getInstance().isAllowDonators() && BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.COUNTDOWN && event.getPlayer().hasPermission("badlion.donatorplus")
                    && UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()) == null) { // Make sure they haven't been on already
                event.setResult(PlayerLoginEvent.Result.ALLOWED);
            } else if (BadlionUHC.getInstance().isAllowDonators() && BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED && event.getPlayer().hasPermission("badlion.donatorplus")
                    && UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()) == null) { // Make sure they haven't been on already
                event.setResult(PlayerLoginEvent.Result.ALLOWED);
            }
        }

    }

}
