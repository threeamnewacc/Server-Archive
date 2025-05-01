package net.badlion.potpvp.commands;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.PartyHelper;
import net.badlion.potpvp.inventories.party.PartyListInventory;
import net.badlion.potpvp.inventories.party.PartyRequestInventory;
import net.badlion.potpvp.managers.MessageManager;
import net.badlion.potpvp.states.matchmaking.GameState;
import net.badlion.potpvp.states.party.PartyState;
import net.badlion.potpvp.tasks.PartyInviteTimeoutTask;
import net.badlion.smellychat.managers.ChatSettingsManager;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyCommand extends GCommandExecutor {

	public PartyCommand() {
		super(1, false);
	}

	@Override
	public void onGroupCommand(Command command, String label, String[] args) {
		if (args[0].equalsIgnoreCase("create")) {
			try {
				// Edge case
				if (GroupStateMachine.duelRequestState.getDuelCreator(this.group) == null
						&& this.currentState != GroupStateMachine.matchMakingState) {
					this.currentState.transition(GroupStateMachine.partyState, this.group);
				} else {
					this.player.sendMessage(ChatColor.RED + "Cannot create a party because " + this.currentState.description());
					return;
				}
			} catch (IllegalStateTransitionException exception) {
				this.player.sendMessage(ChatColor.RED + "Cannot create a party because " + this.currentState.description());
				return;
			}

			this.handleCreate();
		} else if (args[0].equalsIgnoreCase("random")) {
			try {
				this.currentState.transition(GroupStateMachine.partyRequestState, this.group);
			} catch (IllegalStateTransitionException exception) {
				this.player.sendMessage(ChatColor.RED + "Cannot go into random party queue because " + this.currentState.description());
				return;
			}

			this.handleRandom();
			this.player.sendMessage(ChatColor.BLUE + "Searching for party");
		} else if (args[0].equalsIgnoreCase("accept")) {
			if (GroupStateMachine.partyRequestState.containsPlayerInvite(this.player)) {
				this.handleAccept();
			} else {
				this.player.sendMessage(ChatColor.RED + "You do not currently have any party invites.");
			}
		} else if (args[0].equalsIgnoreCase("deny")) {
			if (GroupStateMachine.partyRequestState.containsPlayerInvite(this.player)) {
				this.handleDeny();
			} else {
				this.player.sendMessage(ChatColor.RED + "No invite exists for a party.");
			}
		} else if (args[0].equalsIgnoreCase("leave")) {
			if (this.group.isParty()) {
				// Edge case
				if (GroupStateMachine.duelRequestState.getDuelCreator(this.group) == null
						&& this.currentState != GroupStateMachine.matchMakingState) {
					// Are they in a game?
					if (GameState.getGroupGame(this.group) == null) {
						Group group = new Group(this.player, true);

						// Does all the work
						Gberry.log("PARTY", "Player left party: " + this.player.getName());
						PartyHelper.handleLeave(this.player, this.group.getParty(), true);

						// Call this after because it calls party.removePlayer()
						PotPvP.getInstance().updatePlayerGroup(this.player, group);
					} else {
						this.player.sendMessage(ChatColor.RED + "You cannot leave your party because you are in a game.");
					}
				} else {
					this.player.sendMessage(ChatColor.RED + "You can not leave the party at this time.");
				}
			} else {
				this.player.sendMessage(ChatColor.RED + "You are not currently in a party or can not leave the party at this time.");
			}
		} else if (args.length >= 2) {
			// /party msg <msg> /party kick <name> /party invite <name>
			if (this.group.isParty()) {
				if (args[0].equalsIgnoreCase("kick")) {
					// Only allow them to leave if they are in the party state >:D
					if (GroupStateMachine.partyState.contains(this.group)) {
						this.handleKick(args[1]);
					} else {
						this.player.sendMessage(ChatColor.RED + "Can only kick players when in a party and in the lobby.");
					}
				} else if (args[0].equalsIgnoreCase("invite")) {
					// Only allow them to leave if they are in the party state >:D
					if (GroupStateMachine.partyState.contains(this.group)) {
						this.handleInvite(args[1]);
					} else {
						this.player.sendMessage(ChatColor.RED + "Can only invite players when in a party and in the lobby.");
					}
				} else if (args[0].equalsIgnoreCase("promote")) {
					// Only allow them to leave if they are in the party state >:D
					if (GroupStateMachine.partyState.contains(this.group)) {
						this.handlePromote(args[1]);
					} else {
						this.player.sendMessage(ChatColor.RED + "Can only invite players when in a party and in the lobby.");
					}
				}
			} else {
				this.player.sendMessage(ChatColor.RED + "Must be in a party to use this command.");
			}
		} else {
			this.usage(this.player);
		}
	}

	private void handleCreate() {
		PartyHelper.handleCreate(this.player, GroupStateMachine.partyState, true);

		// Update player listing item only for parties (not for wars) using their new group
		PartyListInventory.updatePartyListing(PotPvP.getInstance().getPlayerGroup(this.player));
	}

	private void handleRandom() {
		this.player.sendMessage(ChatColor.BLUE + "Added to random party queue.");
		PartyState.getPartyFromRandomQueue(this.player);
	}

	private void handleAccept() {
		// Task shit
		PartyInviteTimeoutTask task = GroupStateMachine.partyRequestState.removePartyInvite(player);
		task.cancel();

		GroupStateMachine.partyRequestState.removeInvitingPlayer(task.getParty().getPartyLeader());

		BukkitUtil.closeInventory(this.player);

		boolean invalidParty = false;

		// Is party leader online?
		if (!Gberry.isPlayerOnline(task.getParty().getPartyLeader())) {
			invalidParty = true;
		}

		// Get leader's group
		Group leaderGroup = PotPvP.getInstance().getPlayerGroup(task.getParty().getPartyLeader());

		// Is the party not the same as the party that sent the invite?
		if (leaderGroup.getParty() == null || leaderGroup.getParty() != task.getParty()) {
			invalidParty = true;
		}

		if (invalidParty) {
			this.player.sendMessage(ChatColor.RED.toString() + task.getParty().getPartyLeader().getName() + "'s party no longer exists.");

			try {
				GroupStateMachine.transitionBackToDefaultState(this.currentState, this.group);
			} catch (IllegalStateTransitionException e) {
				PotPvP.getInstance().somethingBroke(this.player, this.group);
			}

			return;
		}

		PartyHelper.addToPartyGroup(this.player, task.getParty(), true);
	}

	private void handleDeny() {
		// Task shit
		PartyInviteTimeoutTask task = GroupStateMachine.partyRequestState.removePartyInvite(this.player);
		task.cancel();

		GroupStateMachine.partyRequestState.removeInvitingPlayer(task.getParty().getPartyLeader());

		BukkitUtil.closeInventory(this.player);

		try {
			GroupStateMachine.partyRequestState.transition(GroupStateMachine.lobbyState, this.group);
		} catch (IllegalStateTransitionException e) {
			e.printStackTrace();
			this.player.sendMessage(ChatColor.RED + "Internal error denying party invite, contact an admin and relog.");
		}

		task.getParty().getPartyLeader().sendMessage(ChatColor.RED + this.player.getName() + " has denied the party invite.");
		this.player.sendMessage(ChatColor.BLUE + "Denied party invite.");
	}

	private void handleInvite(String name) {
		// Are we leader?
		if (this.group.getLeader() == this.player) {
			// Inviting a valid player?
			Player invited = PotPvP.getInstance().getServer().getPlayerExact(name);

			if (invited != null) {
				// Is the sender ignored by the person being invited?
				if (ChatSettingsManager.getChatSettings(invited).getIgnoredList().contains(this.player.getUniqueId())) {
					this.player.sendMessage(ChatColor.RED + "Cannot invite player because they have you on their ignore list.");
					return;
				}

				// Are they accepting party invites?
				MessageManager.MessageOptions messageOptions = MessageManager.getMessageOptions(invited);
				if (!messageOptions.getMessageTagBoolean(MessageManager.MessageType.PARTY)) {
					if (!this.player.hasPermission("badlion.staff") && !this.player.hasPermission("badlion.famous")) {
						this.player.sendMessage(ChatColor.RED + "Player is not accepting party invites.");
						return;
					}
				}

				Group theirGroup = PotPvP.getInstance().getPlayerGroup(invited);
				State<Group> theirState = GroupStateMachine.getInstance().getCurrentState(theirGroup);

				try {
					theirState.transition(GroupStateMachine.partyRequestState, theirGroup);
				} catch (IllegalStateTransitionException e) {
					this.player.sendMessage(ChatColor.RED + "Cannot invite " + name + " because " + theirState.description());
					return;
				}

				// Create the timeout task
				PartyInviteTimeoutTask task = new PartyInviteTimeoutTask(invited, this.group.getParty());
				task.runTaskLater(PotPvP.getInstance(), 15 * 20);
				GroupStateMachine.partyRequestState.addInvitingPlayer(this.player);
				GroupStateMachine.partyRequestState.addPartyInvite(invited, task);
				invited.sendMessage(ChatColor.BLUE + "Party invite from " + this.player.getName() + ".");
				this.player.sendMessage(ChatColor.BLUE + "Party invite sent to " + invited.getName() + ".");

				PartyRequestInventory.openPartyRequestInventory(invited, this.group);
			} else {
				this.player.sendMessage(ChatColor.RED + "Player does not exist or is offline.");
			}
		} else {
			this.player.sendMessage(ChatColor.RED + "Must be the party leader to invite people to the party.");
		}
	}

	private void handleKick(String name) {
		if (this.group.getLeader() == this.player) {
			// Kicking a valid player?
			Player kicked = PotPvP.getInstance().getServer().getPlayer(name);

			if (kicked != null) {
				// Can't kick self
				if (this.player == kicked) {
					this.player.sendMessage(ChatColor.RED + "Cannot kick self from party.  Use \"/party leave\" to disband the party");
					return;
				}

				// Clean up
				if (this.group.contains(kicked)) {
					// New Group
					Group group = new Group(kicked, true);
					PotPvP.getInstance().updatePlayerGroup(kicked, group);

					kicked.sendMessage(ChatColor.BLUE + "Kicked from party.");
					this.player.sendMessage(ChatColor.BLUE + "Kicked " + kicked.getName() + " from the party.");
				} else {
					this.player.sendMessage(ChatColor.RED + "Player is not part of your party.");
				}
			} else {
				this.player.sendMessage(ChatColor.RED + "Player does not exist or is offline.");
			}
		} else {
			this.player.sendMessage(ChatColor.RED + "Must be party leader to kick people from the party.");
		}
	}

	private void handlePromote(String name) {
		if (this.group.getLeader() == this.player) {
			// Promoting a valid player?
			Player promoted = PotPvP.getInstance().getServer().getPlayer(name);

			if (promoted != null) {
				// Can't kick self
				if (this.player == promoted) {
					this.player.sendMessage(ChatColor.RED + "Cannot kick promote self to party leader");
					return;
				}

				// Clean up
				if (this.group.contains(promoted)) {
					this.group.getParty().setPartyLeader(promoted);
					PartyListInventory.updatePartyListing(this.group);

					PartyState.givePlayerItems(this.player, false);
					PartyState.givePlayerItems(promoted, true);

					promoted.sendMessage(ChatColor.BLUE + "You are now the party leader.");
					this.player.sendMessage(ChatColor.BLUE + "Promoted " + promoted.getName() + " to leader of party.");
				} else {
					this.player.sendMessage(ChatColor.RED + "Player is not part of your party.");
				}
			} else {
				this.player.sendMessage(ChatColor.RED + "Player does not exist or is offline.");
			}
		} else {
			this.player.sendMessage(ChatColor.RED + "Must be party leader to promote a new party leader.");
		}
	}

	public void usage(CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "/party create - Create a party.");
		sender.sendMessage(ChatColor.GREEN + "/party random - Find a random party partner.");
		sender.sendMessage(ChatColor.GREEN + "/party accept - Accept a party invite.");
		sender.sendMessage(ChatColor.GREEN + "/party deny - Deny a party invite.");
		sender.sendMessage(ChatColor.GREEN + "/party leave - Leave a party.");
		sender.sendMessage(ChatColor.GREEN + "/party invite <name> - Invite <name> to party.");
		sender.sendMessage(ChatColor.GREEN + "/party kick <name> - Kick <name> from party.");
		sender.sendMessage(ChatColor.GREEN + "/party promote <name> - Promote <name> to party leader.");
		sender.sendMessage(ChatColor.GREEN + "/pc changes to party chat. /gc back to global chat");
	}

}
