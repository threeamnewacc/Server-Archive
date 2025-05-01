package net.badlion.potpvp.states.party;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.tasks.PartyInviteTimeoutTask;
import net.badlion.statemachine.GState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartyRequestState extends GState<Group> implements Listener {

	private static List<Player> invitingPlayer = new ArrayList<>();
    private static Map<Player, PartyInviteTimeoutTask> partyInviteTaskMap = new HashMap<>();

    public PartyRequestState() {
        super("party", "they have a party request.", GroupStateMachine.getInstance());
    }

	public boolean containsInvitingPlayer(Player player) {
		return PartyRequestState.invitingPlayer.contains(player);
	}

	public void addInvitingPlayer(Player player) {
		PartyRequestState.invitingPlayer.add(player);
	}

	public boolean removeInvitingPlayer(Player player) {
		return PartyRequestState.invitingPlayer.remove(player);
	}

	public boolean containsPlayerInvite(Player player) {
		return PartyRequestState.partyInviteTaskMap.containsKey(player);
	}

	public void addPartyInvite(Player player, PartyInviteTimeoutTask task) {
		PartyRequestState.partyInviteTaskMap.put(player, task);
	}

	public PartyInviteTimeoutTask removePartyInvite(Player player) {
		return PartyRequestState.partyInviteTaskMap.remove(player);
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);
		if (this.contains(group)) {
			ItemStack item = event.getItem();

			event.setCancelled(true);

			if (item == null || item.getType().equals(Material.AIR)) return;

			if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				// Only item they should have is the leave party random queue item
				player.performCommand("leave");
			}
		}
	}

}
