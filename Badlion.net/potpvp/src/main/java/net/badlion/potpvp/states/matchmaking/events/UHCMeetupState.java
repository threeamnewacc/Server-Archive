package net.badlion.potpvp.states.matchmaking.events;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class UHCMeetupState extends GameState implements Listener {

    public UHCMeetupState() {
        super("uhc_meetup", "they are in a UHC Meetup.", GroupStateMachine.getInstance());
    }

    @EventHandler(priority = EventPriority.LAST)
    public void onUseWorkbenchEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Group group = PotPvP.getInstance().getPlayerGroup(player);
        if (this.contains(group)) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.WORKBENCH) {
                event.setCancelled(false);
                event.setUseInteractedBlock(Event.Result.DENY);
            }
        }
    }

}
