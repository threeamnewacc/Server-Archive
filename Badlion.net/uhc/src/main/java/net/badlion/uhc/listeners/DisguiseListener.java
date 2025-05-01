package net.badlion.uhc.listeners;

import net.badlion.disguise.events.PlayerDisguiseEvent;
import net.badlion.disguise.events.PlayerUndisguiseEvent;
import net.badlion.disguise.managers.DisguiseManager;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.practice.PracticeManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DisguiseListener implements Listener {

	// Must execute after Disguise's PlayerJoinEvent
	@EventHandler(priority = EventPriority.LASTER)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if (player.isDisguised()) {
			// Are they a mod or host?
			if (UHCPlayerManager.getUHCPlayer(player.getUniqueId()).getState().ordinal() >= UHCPlayer.State.MOD.ordinal()) {
				DisguiseManager.undisguisePlayer(player, false);

				player.sendFormattedMessage(ChatColor.RED + "You cannot be disguised when you are a mod or host, you have been undisguised!");
			} else {
				// Cache this username instead of their disguised username
				BadlionUHC.getInstance().removeUUID(event.getPlayer().getName());
				BadlionUHC.getInstance().putUUID(event.getPlayer().getUniqueId(), event.getPlayer().getDisguisedName());
				BadlionUHC.getInstance().putUsername(event.getPlayer().getUniqueId(), event.getPlayer().getDisguisedName());
				UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()).setDisguisedName(event.getPlayer().getDisguisedName());
			}
		}
	}

    @EventHandler
    public void onPlayerDisguiseEvent(PlayerDisguiseEvent event) {
	    if (UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()).getState().ordinal() >= UHCPlayer.State.MOD.ordinal()) {
		    event.setCancelled(true);
		    event.getPlayer().sendFormattedMessage(ChatColor.RED + "You cannot disguise when you are a mod or host!");
		    return;
	    }

        if (event.isFromCommand() && BadlionUHC.getInstance().getState().ordinal() >= BadlionUHC.BadlionUHCState.COUNTDOWN.ordinal()) {
            event.setCancelled(true);
            event.getPlayer().sendFormattedMessage(ChatColor.RED + "Cannot use the disguise command after the game has started.");
            return;
        }

	    if (PracticeManager.isInPractice(UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()))) {
		    event.setCancelled(true);
		    event.getPlayer().sendFormattedMessage(ChatColor.RED + "You cannot disguise while in practice, do /practice to leave the practice arena!");
		    return;
	    }

	    event.getPlayer().teleport(BadlionUHC.getInstance().getSpawnLocation());

        BadlionUHC.getInstance().removeUUID(event.getPlayer().getName());
        BadlionUHC.getInstance().putUUID(event.getPlayer().getUniqueId(), event.getPlayer().getDisguisedName());
        BadlionUHC.getInstance().putUsername(event.getPlayer().getUniqueId(), event.getPlayer().getDisguisedName());
        UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId()).setDisguisedName(event.getPlayer().getDisguisedName());
    }

    @EventHandler
    public void onPlayerUndisguiseEvent(PlayerUndisguiseEvent event) {
        UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId());
        if (event.isFromCommand() && BadlionUHC.getInstance().getState().ordinal() >= BadlionUHC.BadlionUHCState.COUNTDOWN.ordinal() && uhcPlayer.getState().ordinal() < UHCPlayer.State.MOD.ordinal()) {
            event.setCancelled(true);
	        event.getPlayer().sendFormattedMessage(ChatColor.RED + "Cannot use the undisguise command after the game has started.");
            return;
        }

        BadlionUHC.getInstance().removeUUID(event.getPlayer().getDisguisedName());
        BadlionUHC.getInstance().putUUID(event.getPlayer().getUniqueId(), event.getPlayer().getName());
        BadlionUHC.getInstance().putUsername(event.getPlayer().getUniqueId(), event.getPlayer().getName());

	    // UHCPlayer can be null if this player is getting undisguised upon login if their rank changed
	    if (uhcPlayer != null) {
		    uhcPlayer.setDisguisedName(null);
	    }
    }

}
