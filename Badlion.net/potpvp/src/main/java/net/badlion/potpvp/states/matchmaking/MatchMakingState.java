package net.badlion.potpvp.states.matchmaking;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.events.Event;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.potpvp.helpers.MatchmakingHelper;
import net.badlion.potpvp.managers.MatchMakingManager;
import net.badlion.potpvp.managers.StasisManager;
import net.badlion.potpvp.matchmaking.MatchMakingService;
import net.badlion.potpvp.matchmaking.QueueService;
import net.badlion.potpvp.rulesets.EventRuleSet;
import net.badlion.potpvp.rulesets.KitRuleSet;
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

		//player.getInventory().setHeldItemSlot(0);

		player.updateInventory();
	}

	@EventHandler(ignoreCancelled=true)
	public void playerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);
		if (this.contains(group) && GroupStateMachine.getInstance().getCurrentState(group) == this) {
			ItemStack item = event.getItem();

			event.setCancelled(true);

			if (item == null || item.getType().equals(Material.AIR)) return;

			if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				switch (player.getInventory().getHeldItemSlot()) {
					case 0: // Show current queue
						player.sendMessage(ChatColor.YELLOW + "Currently in queue for: " + this.getMatchMakingInfo(group));
						break;
                    case 1: // Show current kit
	                    KitRuleSet kitRuleSet = this.getMatchMakingKit(group);
	                    if (kitRuleSet != null) {
		                    KitHelper.openKitPreviewInventory(null, null, player, kitRuleSet);
	                    } else {
		                    Event event1 = this.getEvent(group);
		                    if (event1 != null) {
			                    if (event1.getKitRuleSet() instanceof EventRuleSet) {
				                    KitHelper.openEventKitPreviewInventoryForEvent(null, null, player, event1);
			                    } else {
				                    KitHelper.openKitPreviewInventory(null, null, player, event1.getKitRuleSet());
			                    }
		                    } else {
			                    player.sendMessage(ChatColor.RED + "Kit for this game is unknown.");
		                    }
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
		QueueService queueService = (QueueService) MatchMakingManager.getMatchMakingService(group);
		if (queueService != null) {
			return queueService.getLadder().getKitRuleSet();
		}

		return null;
	}

	private Event getEvent(Group group) {
		// In event queue
		for (Event gameEvent : Event.getEvents().values()) {
			if (!gameEvent.isStarted()) {
				if (gameEvent.inQueue(group.getLeader())) {
					return gameEvent;
				}
			}
		}

		return null;
	}

    private String getMatchMakingInfo(Group group) {
        MatchMakingService matchMakingService = MatchMakingManager.getMatchMakingService(group);
        if (matchMakingService != null) {
            return matchMakingService.getServiceInfo() + " (" + matchMakingService.getNumberInQueue() + ")";
        }

        for (Event gameEvent : Event.getEvents().values()) {
            if (!gameEvent.isStarted()) {
                if (gameEvent.inQueue(group.getLeader())) {
                    return gameEvent.getInfo() + " (" + gameEvent.getNumberInQueue() + ")";
                }
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
                PotPvP.getInstance().somethingBroke(group.getLeader(), group);
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
