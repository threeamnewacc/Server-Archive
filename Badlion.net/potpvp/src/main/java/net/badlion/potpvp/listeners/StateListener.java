package net.badlion.potpvp.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.PartyHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StateListener extends BukkitUtil.Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Group group = new Group(event.getPlayer());
        PotPvP.getInstance().updatePlayerGroup(event.getPlayer(), group);
    }

    // THIS HAS TO BE LAST, EVERYTHING NEEDS TO HAPPEN BEFORE WE REMOVE THEM FROM THE STATE MACHINE
    @EventHandler(priority=EventPriority.LAST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
	        Group group = PotPvP.getInstance().getPlayerGroup(event.getPlayer());

	        // Call this before we remove the group from the state machine because
	        // we might access the player's group in the handleLeave() method
	        if (group.isParty()) {
		        PartyHelper.handleLeave(event.getPlayer(), group.getParty(), true);
	        }

	        // If party has more players in it then don't remove from state machine
	        PotPvP.getInstance().handlePlayerLeaveGroup(event.getPlayer(), group);

	        PotPvP.getInstance().removePlayerGroup(event.getPlayer());
	        Gberry.log("GROUP", event.getPlayer().getName() + " removed from group");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
