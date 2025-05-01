package net.badlion.potpvp.states;

import net.badlion.gberry.Gberry;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.DuelHelper;
import net.badlion.potpvp.inventories.duel.DuelRequestChooseCustomKitInventory;
import net.badlion.statemachine.GState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DuelRequestState extends GState<Group> implements Listener {

	private Map<Group, DuelHelper.DuelCreator> senderDuelCreator = new HashMap<>(); // Stores sender, duel creator
	private Map<Group, DuelHelper.DuelCreator> receiverDuelCreator = new HashMap<>(); // Stores receiver, duel creator

    public DuelRequestState() {
        // e.g. X cannot join your party at the moment because they "have a duel request"
        super("duel_request", "they have a duel request.", GroupStateMachine.getInstance());
    }

	@Override
	public void before(Group element) {
		super.before(element);

		PotPvP.getInstance().givePlayerDuelStateItems(element.getLeader());
	}

	public void addDuelCreator(Group sender, Group receiver, DuelHelper.DuelCreator duelCreator) {
		Gberry.log("DUEL", "Duel creator added");
		this.senderDuelCreator.put(sender, duelCreator);
		this.receiverDuelCreator.put(receiver, duelCreator);
	}

	public DuelHelper.DuelCreator getDuelCreator(Group group) {
		DuelHelper.DuelCreator duelCreator = this.senderDuelCreator.get(group);
		if (duelCreator != null) {
			return duelCreator;
		}

		return this.receiverDuelCreator.get(group);
	}

	public DuelHelper.DuelCreator removeDuelCreator(Group group) {
		Gberry.log("DUEL", "Remove duel creator called");
		DuelHelper.DuelCreator duelCreator = this.senderDuelCreator.remove(group);
		if (duelCreator != null) { // Sender
			Gberry.log("DUEL", "Remove duel creator WAS NOT null");
			this.receiverDuelCreator.remove(duelCreator.getReceiver());
			return duelCreator;
		}
		Gberry.log("DUEL", "Remove duel creator WAS null");

		duelCreator = this.receiverDuelCreator.remove(group);

		// Null check
		if (duelCreator == null) return null;

		this.senderDuelCreator.remove(duelCreator.getSender());
		return duelCreator;
	}

    @EventHandler(ignoreCancelled=true)
    public void playerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Group group = PotPvP.getInstance().getPlayerGroup(player);
        if (this.contains(group)) {
            ItemStack item = event.getItem();

            event.setCancelled(true);

	        // This checks to see if they actually have the custom kit item in their hotbar slot
            if (item == null || item.getType().equals(Material.AIR)) return;

            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                switch (player.getInventory().getHeldItemSlot()) {
                    case 0: // Select custom kit
	                    DuelRequestChooseCustomKitInventory.openDuelRequestChooseCustomKitInventory(player);
                        break;
                }
            }
        }
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
	    Player player = event.getPlayer();
        Group group = PotPvP.getInstance().getPlayerGroup(player);
        DuelHelper.DuelCreator duelCreator = this.getDuelCreator(group);
        if (duelCreator != null) {
	        // Send messages
	        duelCreator.getSender().sendMessage(ChatColor.RED + player.getName() + " has logged out, duel request cancelled.");
	        duelCreator.getReceiver().sendMessage(ChatColor.RED + player.getName() + " has logged out, duel request cancelled.");

            DuelHelper.handleDuelDeny(false, player, group);
        }
    }

}
