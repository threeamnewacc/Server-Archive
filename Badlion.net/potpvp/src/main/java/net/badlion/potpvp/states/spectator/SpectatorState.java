package net.badlion.potpvp.states.spectator;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.helpers.SpectatorHelper;
import net.badlion.statemachine.GState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SpectatorState extends GState<Group> implements Listener {

    public SpectatorState() {
        super("spectator", "they are in spectator mode.", GroupStateMachine.getInstance());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (Group group : this.elements()) {
            for (Player pl : group.players()) {
                event.getPlayer().hidePlayer(pl);
            }
        }
    }

    @Override
    public void after(Group element) {
        SpectatorHelper.showSpectatorToEveryone(element);
    }

}
