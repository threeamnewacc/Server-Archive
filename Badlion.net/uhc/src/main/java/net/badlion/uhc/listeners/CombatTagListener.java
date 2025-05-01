package net.badlion.uhc.listeners;

import net.badlion.combattag.CombatTagPlugin;
import net.badlion.combattag.events.CombatTagCreateEvent;
import net.badlion.combattag.events.CombatTagDamageEvent;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCLoggerNPC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class CombatTagListener implements Listener {

	@EventHandler(priority=EventPriority.FIRST)
	public void playerQuitEvent(PlayerQuitEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		final UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(uuid);
		if (uhcPlayer != null && uhcPlayer.getState() == UHCPlayer.State.PLAYER
				&& BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED) {
			// Always tag
			CombatTagPlugin.getInstance().addCombatTagged(event.getPlayer());
		}
	}

	@EventHandler
	public void onCombatTagCreate(CombatTagCreateEvent event) {
		// Cancel the event if the game did not start
		if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.STARTED) {
			event.setCancelled(true);
			return;
		}

		event.setLoggerNPC(new UHCLoggerNPC(event.getPlayer()));
	}

    @EventHandler
    public void entityCombatLoggerHurt(CombatTagDamageEvent event) {
		if (!BadlionUHC.getInstance().isPVP()) {
			event.setCancelled(true);

			if (event.getDamager() != null) {
				event.getDamager().sendMessage(ChatColor.RED + "PVP is currently disabled.");
            }
        }
    }

}
