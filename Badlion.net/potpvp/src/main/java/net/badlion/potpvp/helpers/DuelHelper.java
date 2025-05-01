package net.badlion.potpvp.helpers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.exceptions.OutOfArenasException;
import net.badlion.potpvp.inventories.duel.DuelRequestChooseCustomKitInventory;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.matchmaking.Match;
import net.badlion.potpvp.matchmaking.RedRoverMatch;
import net.badlion.potpvp.rulesets.CustomRuleSet;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.tasks.DuelRequestTimeoutTask;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class DuelHelper {

	private static ItemStack chooseCustomKitItem;

	public static void initialize() {
		DuelHelper.chooseCustomKitItem = ItemStackUtil.createItem(Material.BOOK, ChatColor.GREEN + "Choose Custom Kit");
	}

	public static void givePlayerChooseCustomKitItem(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		player.getInventory().setItem(0, DuelHelper.chooseCustomKitItem);

		//player.getInventory().setHeldItemSlot(0);

		player.updateInventory();
	}

	/**
	 * Creates a duel with a specific kit rule set
	 */
	public static void createDuel(Player player, DuelCreator duelCreator) {
		Gberry.log("DUEL", "Duel created");
		// Get an arena
		Arena arena;
		try {
            Gberry.log("DUEL", "KitRuleSet " + duelCreator.getKitRuleSet());
            Gberry.log("DUEL", "ArenaType " + duelCreator.getKitRuleSet().getArenaType());
			arena = ArenaManager.getArena(duelCreator.getKitRuleSet().getArenaType());
		} catch (OutOfArenasException e) {
			duelCreator.getReceiver().sendMessage(ChatColor.RED + "Out of arenas at the moment, sorry!");
			duelCreator.getSender().sendMessage(ChatColor.RED + "Out of arenas at the moment, sorry!");
            GroupStateMachine.duelRequestState.removeDuelCreator(duelCreator.getSender());

			try {
				// Transfer states to lobby state again
				GroupStateMachine.transitionBackToDefaultState(GroupStateMachine.duelRequestState, duelCreator.getSender());
				GroupStateMachine.transitionBackToDefaultState(GroupStateMachine.duelRequestState, duelCreator.getReceiver());
			} catch (IllegalStateTransitionException ex) {
				PotPvP.getInstance().somethingBroke(player, duelCreator.getSender(), duelCreator.getReceiver());
			}
			return;
		}

		try {
			Match match;

			if (duelCreator.isRedRover()) {
				if (duelCreator.getKitRuleSet() instanceof CustomRuleSet) {
					match = new RedRoverMatch(arena, duelCreator.getKitRuleSet(), duelCreator.getCustomKitSelections());
				} else {
					match = new RedRoverMatch(arena, duelCreator.getKitRuleSet());
				}
			} else {
				if (duelCreator.getKitRuleSet() instanceof CustomRuleSet) {
					match = new Match(arena, false, duelCreator.getKitRuleSet(), duelCreator.getCustomKitSelections());
				} else {
					match = new Match(arena, false, duelCreator.getKitRuleSet());
				}
			}

			// Transfer states
			GroupStateMachine.duelRequestState.transition(GroupStateMachine.matchMakingState, duelCreator.getReceiver());
			GroupStateMachine.duelRequestState.transition(GroupStateMachine.matchMakingState, duelCreator.getSender());
			GroupStateMachine.matchMakingState.push(GroupStateMachine.regularMatchState, duelCreator.getReceiver(), match);
			GroupStateMachine.matchMakingState.push(GroupStateMachine.regularMatchState, duelCreator.getSender(), match);

            match.setLadderType(Ladder.LadderType.Duel);
			match.prepGame(duelCreator.getReceiver(), duelCreator.getSender());
			match.startGame();
		} catch (IllegalStateTransitionException e) {
			arena.toggleBeingUsed();
			Bukkit.getLogger().info("GBERRY HALP 2");
			PotPvP.getInstance().somethingBroke(player, duelCreator.getReceiver(), duelCreator.getSender());
		}

		// Remove the duel creator object at the very end
		GroupStateMachine.duelRequestState.removeDuelCreator(duelCreator.getSender());
	}

	public static void handleDuelAccept(final Player player, final Group group, final DuelCreator duelCreator) {
		Gberry.log("DUEL", "handleAcceptDeny");
        if (duelCreator.getDuelTimeoutTask() != null) {
            duelCreator.getDuelTimeoutTask().cancel();
        }

		duelCreator.setAccepted(true);

		// Remove duel creator since we already have a reference to it
		//GroupStateMachine.duelRequestState.removeDuelCreator(group);

		PotPvPPlayerManager.addDebug(player, "Accepted duel request");

		if (duelCreator.getKitRuleSet() instanceof CustomRuleSet) {
			duelCreator.getSender().sendMessage(ChatColor.YELLOW + "Duel request accepted, please choose your custom kit.");
			duelCreator.getSender().sendMessage(ChatColor.GREEN + "Duel starting in 15 seconds.");
			duelCreator.getReceiver().sendMessage(ChatColor.YELLOW + "Duel request accepted, please choose your custom kit.");
			duelCreator.getReceiver().sendMessage(ChatColor.GREEN + "Duel starting in 15 seconds.");

			// Give all players the choose custom kit item
			if (duelCreator.getKitRuleSet() instanceof CustomRuleSet) {
				for (Player pl : duelCreator.getSender().players()) {
					DuelHelper.givePlayerChooseCustomKitItem(pl);
				}

				for (Player pl : duelCreator.getReceiver().players()) {
					DuelHelper.givePlayerChooseCustomKitItem(pl);
				}
			}

			// Run task 15 seconds later to check if everyone selected a custom kit
			BukkitUtil.runTaskLater(new Runnable() {
				@Override
				public void run() {
					if (!duelCreator.allCustomKitsSelected()) {
						// Remove the duel creator
						GroupStateMachine.duelRequestState.removeDuelCreator(group);

						// Close their inventories if they're still open
						for (Player pl : duelCreator.getSender().players()) {
							BukkitUtil.closeInventory(pl);
						}

						for (Player pl : duelCreator.getReceiver().players()) {
							BukkitUtil.closeInventory(pl);
						}

						// What if they logged off or somehow changed groups
						if (Gberry.isPlayerOnline(duelCreator.getSender().getLeader())
								&& PotPvP.getInstance().getPlayerGroup(duelCreator.getSender().getLeader()) == duelCreator.getSender()) {
							try {
								GroupStateMachine.transitionBackToDefaultState(GroupStateMachine.duelRequestState, duelCreator.getSender());
							} catch (IllegalStateTransitionException e) {
								PotPvP.getInstance().somethingBroke(player, duelCreator.getSender());
							}
						}

						if (Gberry.isPlayerOnline(duelCreator.getReceiver().getLeader())
								&& PotPvP.getInstance().getPlayerGroup(duelCreator.getReceiver().getLeader()) == duelCreator.getReceiver()) {
							try {
								GroupStateMachine.transitionBackToDefaultState(GroupStateMachine.duelRequestState, duelCreator.getReceiver());
							} catch (IllegalStateTransitionException e) {
								PotPvP.getInstance().somethingBroke(player, duelCreator.getReceiver());
							}
						}

						// Send messages
						duelCreator.getSender().sendMessage(ChatColor.RED + "Not all players have selected their custom kits, duel cancelled.");
						duelCreator.getReceiver().sendMessage(ChatColor.RED + "Not all players have selected their custom kits, duel cancelled.");
					}
				}
			}, 300L);

			// Open the custom kit selection inventory for all players
			for (Player pl : duelCreator.getSender().players()) {
				DuelRequestChooseCustomKitInventory.openDuelRequestChooseCustomKitInventory(pl);
			}

			for (Player pl : duelCreator.getReceiver().players()) {
				DuelRequestChooseCustomKitInventory.openDuelRequestChooseCustomKitInventory(pl);
			}

			// Set boolean in duel creator
			duelCreator.setSelectingCustomKits(true);
		} else {
			DuelHelper.createDuel(player, duelCreator);
		}
	}

	public static void handleDuelDeny(boolean sendDenyMessage, Player player, Group group) {
		Gberry.log("DUEL", "Handling duel deny");

		PotPvPPlayerManager.addDebug(player, "Denied duel request");

		// Remove the duel creator
		DuelCreator duelCreator = GroupStateMachine.duelRequestState.removeDuelCreator(group);

		if (sendDenyMessage) {
			duelCreator.getSender().sendMessage(ChatColor.RED + player.getName() + " has denied the duel request.");
			duelCreator.getReceiver().sendLeaderMessage(ChatColor.RED + "You have denied the duel request.");

			if (duelCreator.getReceiver().isParty()) {
				duelCreator.getReceiver().sendMessageWithoutLeader(ChatColor.RED + "Your party leader has denied the duel request.");
			}
		}

		// Cancel duel timeout task
		if (duelCreator.getDuelTimeoutTask() != null) {
			duelCreator.getDuelTimeoutTask().cancel();
		}

		// Close inventory for players/party leaders
		BukkitUtil.closeInventory(duelCreator.getSender().getLeader());
		BukkitUtil.closeInventory(duelCreator.getReceiver().getLeader());

		try {
			// Transfer states back to default state
			GroupStateMachine.transitionBackToDefaultState(GroupStateMachine.duelRequestState, duelCreator.getSender());
			GroupStateMachine.transitionBackToDefaultState(GroupStateMachine.duelRequestState, duelCreator.getReceiver());
		} catch (IllegalStateTransitionException e) {
			PotPvP.getInstance().somethingBroke(player, duelCreator.getSender(), duelCreator.getReceiver());
		}
	}

	public static class DuelCreator {

		private Group sender;
		private Group receiver;

		private boolean accepted = false;

		private boolean redRover = false;

		private boolean selectingCustomKits = false;
		private KitRuleSet kitRuleSet;
        private BukkitTask duelTimeoutTask = null;

		private Map<Player, Integer> customKitSelections = new HashMap<>();

		public DuelCreator(Group sender, Group receiver) {
			Gberry.log("DUEL", "Duel creator created");
			this.sender = sender;
			this.receiver = receiver;

			// Save the duel request
			GroupStateMachine.duelRequestState.addDuelCreator(sender, receiver, this);
		}

		public Group getSender() {
			return this.sender;
		}

		public Group getReceiver() {
			return this.receiver;
		}

		public boolean isAccepted() {
			return accepted;
		}

		public void setAccepted(boolean accepted) {
			this.accepted = accepted;
		}

		public boolean isRedRover() {
			return redRover;
		}

		public void setRedRover(boolean redRover) {
			this.redRover = redRover;
		}

		public boolean isSelectingCustomKits() {
			return selectingCustomKits;
		}

		public void setSelectingCustomKits(boolean selectingCustomKits) {
			this.selectingCustomKits = selectingCustomKits;
		}

		public KitRuleSet getKitRuleSet() {
			return this.kitRuleSet;
		}

		public void setKitRuleSet(KitRuleSet kitRuleSet) {
			this.kitRuleSet = kitRuleSet;
		}

		public Map<Player, Integer> getCustomKitSelections() {
			return customKitSelections;
		}

		public void setCustomKit(Player player, int customKitNumber) {
			this.customKitSelections.put(player, customKitNumber);
		}

		public boolean allCustomKitsSelected() {
			boolean flag = true;

			for (Player player : this.sender.players()) {
				if (flag) {
					flag = this.customKitSelections.get(player) != null;
				}
			}

			for (Player player : this.receiver.players()) {
				if (flag) {
					flag = this.customKitSelections.get(player) != null;
				}
			}

			return flag;
		}

        public BukkitTask getDuelTimeoutTask() {
            return duelTimeoutTask;
        }

		public void startDuelTimeoutTask() {
			// Create a timeout task
			DuelRequestTimeoutTask duelRequestTimeoutTask = new DuelRequestTimeoutTask(this);
			this.duelTimeoutTask = BukkitUtil.runTaskLater(duelRequestTimeoutTask, 300L);
		}

    }

}
