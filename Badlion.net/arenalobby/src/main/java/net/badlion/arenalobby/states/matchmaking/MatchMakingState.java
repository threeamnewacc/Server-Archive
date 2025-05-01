package net.badlion.arenalobby.states.matchmaking;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.GroupStateMachine;
import net.badlion.arenalobby.helpers.KitInventoryHelper;
import net.badlion.arenalobby.helpers.MatchmakingHelper;
import net.badlion.arenalobby.managers.MatchMakingManager;
import net.badlion.arenalobby.managers.StasisManager;
import net.badlion.arenalobby.matchmaking.EventQueueService;
import net.badlion.arenalobby.matchmaking.MatchMakingService;
import net.badlion.statemachine.GState;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class MatchMakingState extends GState<Group> implements Listener {

	public MatchMakingState() {
		super("matchmaking", "they are in matchmaking.", GroupStateMachine.getInstance());
	}

	@Override
	public void before(Group group) {
		super.before(group);

		for (Player player : group.players()) {
			MatchMakingState.givePlayerItems(player);
		}
	}

	@Override
	public void after(Group group) {
		super.after(group);
	}

	public static void givePlayerItems(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		player.getInventory().setItem(0, MatchmakingHelper.getCurrentQueueItem());
		player.getInventory().setItem(1, MatchmakingHelper.getCurrentKitItem());
		player.getInventory().setItem(8, MatchmakingHelper.getLeaveQueueItem());

		player.getInventory().setHeldItemSlot(0);

		player.updateInventory();
	}

	@EventHandler(ignoreCancelled = true)
	public void playerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Group group = ArenaLobby.getInstance().getPlayerGroup(player);
		if (this.contains(group) && GroupStateMachine.getInstance().getCurrentState(group) == this) {
			ItemStack item = event.getItem();

			event.setCancelled(true);

			if (item == null || item.getType().equals(Material.AIR)) return;

			if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				switch (player.getInventory().getHeldItemSlot()) {
					case 0: // Show current queue
						player.sendFormattedMessage("{0}Currently in queue for: {1}", ChatColor.YELLOW, this.getMatchMakingInfo(group));
						break;
					case 1: // Show current kit
						KitRuleSet kitRuleSet = this.getMatchMakingKit(group);
						if (kitRuleSet != null) {
							KitInventoryHelper.openKitPreviewInventory(null, null, player, kitRuleSet);
						} else {
							player.sendFormattedMessage("{0}Kit for this game is unknown.", ChatColor.RED);
						}
						break;
					case 8: // Leave queue
						player.performCommand("leave");
						break;
				}
			}
		}
	}

	private KitRuleSet getMatchMakingKit(Group group) {
		// In match queue
		MatchMakingService queueService = MatchMakingManager.getMatchMakingService(group);
		if (queueService != null) {
			if(queueService instanceof EventQueueService){
				EventQueueService eventQueueService = (EventQueueService) queueService;
				return eventQueueService.getKitRuleSet();
			}else {
				return queueService.getLadder().getKitRuleSet();
			}
		}

		return null;
	}


	private String getMatchMakingInfo(Group group) {
		MatchMakingService matchMakingService = MatchMakingManager.getMatchMakingService(group);
		if (matchMakingService != null) {
			if(matchMakingService instanceof EventQueueService){
				EventQueueService eventQueueService = (EventQueueService) matchMakingService;
				return matchMakingService.getServiceInfo() + " (" + eventQueueService.getInQueue() + ")";
			}else {
				return matchMakingService.getServiceInfo() + " (" + matchMakingService.getNumberInQueue() + ")";
			}
		}

		return "";
	}

	public static class MatchStasisHandler implements StasisManager.StasisHandler {

		@Override
		public void run(Group group) {
			try {
				// Current state might not exist cuz it was cleaned up already if someone left
				// BUG FIX: Check if they are in matchmakingstate since they might respawn and get transferred?
				State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);
				if (currentState != null && GroupStateMachine.matchMakingState.contains(group)) {
					GroupStateMachine.transitionBackToDefaultState(currentState, group);
				}
			} catch (IllegalStateTransitionException e) {
				ArenaLobby.getInstance().somethingBroke(group.getLeader(), group);
			}

			// Remove from mapping
			// Moved to RegularMatchState
			//Game game = GroupStateMachine.matchMakingState.removeGroupGame(group);

			// Safe to free arena now
			//game.getArena().toggleBeingUsed();

			// War hack
			//if (game instanceof WarMatch) {
			//    GroupStateMachine.getInstance().cleanupElement(group);
			//}
		}

	}

}
