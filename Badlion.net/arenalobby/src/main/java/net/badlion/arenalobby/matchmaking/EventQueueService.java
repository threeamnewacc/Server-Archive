package net.badlion.arenalobby.matchmaking;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.GroupStateMachine;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.logging.Level;

public class EventQueueService extends MatchMakingService {

	// TODO: Hardcoded for now until we have more events, redo once more events are ready
	private ArenaCommon.EventType eventType;
	private KitRuleSet kitRuleSet = KitRuleSet.uhcRuleSet;

	private int inQueue = 0;
	private int inGame = 0;


	public EventQueueService(ArenaCommon.EventType eventType, KitRuleSet kitRuleSet) {
		this.eventType = eventType;
		this.kitRuleSet = kitRuleSet;
	}

	/**
	 * Add group to matchmaking service
	 */
	public void addGroup(final Group group) {
		super.addGroup(group);
		State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);
		try {
			currentState.transition(GroupStateMachine.matchMakingState, group);
		} catch (IllegalStateTransitionException e) {
			group.sendLeaderMessage(ChatColor.RED + "Cannot queue up right now.");
			return;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				JSONObject data = new JSONObject();
				data.put("uuid", group.getLeader().getUniqueId().toString());
				data.put("ladder", "classic");
				data.put("server_region", Gberry.serverRegion.toString().toLowerCase());
				data.put("server_name", Gberry.serverName);
				data.put("server_type", "uhcmeetup");
				data.put("type", eventType.getTag());
				try {
					JSONObject response = Gberry.contactMCP("matchmaking-default-queue-up", data);
					if (response != null && !response.containsKey("success")) {
						if (response.containsKey("error")) {
							String error = (String) response.get("error");

							if (error.equals("in_a_party")) {
								group.sendLeaderMessage(ChatColor.RED + "You may not be in a party to join that queue.");
							} else if (error.equals("not_in_a_party")) {
								group.sendLeaderMessage(ChatColor.RED + "You must be in a party for that queue.");
							} else if (error.equals("not_in_a_clan")) {
								group.sendLeaderMessage(ChatColor.RED + "You must be in a clan to join that queue.");
							} else if (error.equals("not_an_officer")) {
								group.sendLeaderMessage(ChatColor.RED + "You are not an officer in your clan.");
							} else if (error.equals("too_few_in_party")) {
								group.sendLeaderMessage(ChatColor.RED + "There are not enough members in your party for this queue.");
							} else if (error.equals("too_many_in_party")) {
								group.sendLeaderMessage(ChatColor.RED + "There are too many members in your party for this queue.");
							} else if (error.equals("party_member_not_in_clan")) {
								group.sendLeaderMessage(ChatColor.RED + "One of your party members is not in a clan.");
							} else if (error.equals("party_member_in_wrong_clan")) {
								group.sendLeaderMessage(ChatColor.RED + "One of your party members is not in your clan.");
							} else if (error.equals("offline_party_member")) {
								group.sendLeaderMessage(ChatColor.RED + "One of your party members is offline.");
							} else if (error.equals("already_in_different_game_queue")) {
								group.sendLeaderMessage(ChatColor.RED + "One person in your party is in a different queue.");
							} else if (error.equals("already_in_queue")) {
								group.sendLeaderMessage(ChatColor.RED + "One person in your party is in a queue.");
							} else if (error.equals("already_in_game")) {
								group.sendLeaderMessage(ChatColor.RED + "One person in your party is in game.");
							} else if (error.equals("following_player")) {
								group.sendLeaderMessage(ChatColor.RED + "You are currently following somebody.");
							} else if (error.startsWith("not_enough_unranked")) {
								int amount = Integer.valueOf(error.split(" ")[1]);
								group.sendLeaderMessage(ChatColor.RED + "You must play 20 unranked matches before you can play ranked. You have played " + amount);
							} else {
								group.sendLeaderMessage(ChatColor.RED + "Cannot queue up for right now because: " + error);
							}
						} else {
							group.sendLeaderMessage(ChatColor.RED + "Cannot queue up for ranked right now.");
						}
						new BukkitRunnable() {
							@Override
							public void run() {
								State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);
								try {
									currentState.transition(GroupStateMachine.lobbyState, group);
								} catch (IllegalStateTransitionException e) {
									group.sendLeaderMessage(ChatColor.RED + "Cannot queue up right now.");
								}
							}
						}.runTask(ArenaLobby.getInstance());

					} else {
						group.sendMessage(ChatColor.GREEN + "Added to " + eventType.getNiceName() + " queue.");
						group.sendMessage(ChatColor.YELLOW + "A UHC Meetup will start in a few minutes when enough players join queue.");

						// Yolo async access here
						ArenaLobby.getInstance().getPlayersInQueue().add(group.getLeader().getUniqueId());
					}

					ArenaLobby.getInstance().getLogger().log(Level.INFO, "[sending join queue]: " + data);
					ArenaLobby.getInstance().getLogger().log(Level.INFO, "[response join queue]: " + response);
				} catch (HTTPRequestFailException e) {
					group.sendMessage(ChatColor.RED + "Unable to join a queue, try again later.");
					new BukkitRunnable() {
						@Override
						public void run() {
							State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);
							try {
								currentState.transition(GroupStateMachine.lobbyState, group);
							} catch (IllegalStateTransitionException e) {
								group.sendLeaderMessage(ChatColor.RED + "Cannot queue up right now.");
							}
						}
					}.runTask(ArenaLobby.getInstance());
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(ArenaLobby.getInstance());
	}

	/**
	 * Remove group from matchmaking service
	 */
	public boolean removeGroup(final Group group, final boolean transitionState) {

		new BukkitRunnable() {
			@Override
			public void run() {
				JSONObject data = new JSONObject();
				data.put("uuid", group.getLeader().getUniqueId().toString());
				try {
					JSONObject response = Gberry.contactMCP("matchmaking-default-remove", data);
					if (response != null && response.equals(MCPManager.successResponse)) {

						// Send them back into the default state if the request went through
						if (transitionState) {
							new BukkitRunnable() {
								@Override
								public void run() {
									if (group != null && group.getLeader() != null && group.getLeader().isOnline()) {
										try {
											State<Group> state = GroupStateMachine.getInstance().getCurrentState(group);
											GroupStateMachine.transitionBackToDefaultState(state, group);
											group.sendMessage(ChatColor.RED + "Left matchmaking.");
										} catch (IllegalStateTransitionException exception) {
											return;
										}
									}
								}
							}.runTask(ArenaLobby.getInstance());
						}
					} else if (response.equals(MCPManager.errorResponse)) {
						// If it errored then leave them in their current state
						if (group != null && group.getLeader() != null && group.getLeader().isOnline()) {
							group.getLeader().sendFormattedMessage("{0}Unable to leave the queue right now.", ChatColor.RED);
						}
					}
					ArenaLobby.getInstance().getLogger().log(Level.INFO, "[sending remove queue]: " + data);
					ArenaLobby.getInstance().getLogger().log(Level.INFO, "[response remove queue]: " + response);
				} catch (HTTPRequestFailException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(ArenaLobby.getInstance());
		return true;
	}

	public Ladder getLadder() {
		return null;
	}

	public void setLadder(Ladder ladder) {
	}

	public String getServiceInfo() {
		return this.eventType.getNiceName();
	}

	public int getNumberInQueue() {
		return 0;
	}

	public KitRuleSet getKitRuleSet() {
		return this.kitRuleSet;
	}

	public int getInGame() {
		return inGame;
	}

	public int getInQueue() {
		return inQueue;
	}

	public void setInGame(int inGame) {
		this.inGame = inGame;
	}

	public void setInQueue(int inQueue) {
		this.inQueue = inQueue;
	}
}
