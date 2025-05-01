package net.badlion.potpvp.states;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.statemachine.GState;
import org.bukkit.entity.Player;

public class LoginState extends GState<Group> {

    public LoginState() {
        super("login", "they are logging in.", GroupStateMachine.getInstance());
    }

    @Override
    public void before(Group element) {
        super.before(element);

        for (Player player : element.players()) {
            LobbyState.givePlayerItems(player);

            PotPvP.getInstance().healAndTeleportToSpawn(player);
        }
    }

}
