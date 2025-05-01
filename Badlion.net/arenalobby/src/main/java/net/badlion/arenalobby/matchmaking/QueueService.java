package net.badlion.arenalobby.matchmaking;

import com.google.common.base.Joiner;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.GroupStateMachine;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class QueueService extends MatchMakingService {

	private Ladder ladder;


	/**
	 * Add group to matchmaking service
	 */
	public void addGroup(final Group group) {
		super.addGroup(group);
		State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);
		try {
			currentState.transition(GroupStateMachine.matchMakingState, group);
		} catch (IllegalStateTransitionException e) {
			group.sendLeaderMessage(ChatColor.RED + "Cannot queue up for ranked right now (1).");
			return;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				JSONObject data = new JSONObject();
				data.put("uuid", group.getLeader().getUniqueId().toString());
				data.put("ladder", ladder.getKitRuleSet().getName());
				data.put("server_region", Gberry.serverRegion.toString().toLowerCase());
				data.put("server_name", Gberry.serverName);
				data.put("server_type", "arena");
				data.put("type", ladder.getLadderType().getTag());
				try {
					JSONObject response = Gberry.contactMCP("matchmaking-default-queue-up", data);
					if (response != null && !response.containsKey("success")) {
						if (response.containsKey("error")) {
							String error = (String) response.get("error");

							if (error.equals("in_a_party")) {
								group.sendLeaderMessage(ChatColor.RED + "You may not be in a party to join that queue.");
							} else if (error.equals("not_in_a_party")) {
								group.sendLeaderMessage(ChatColor.RED + "You must be in a party for that queue.");
							} else if (error.equals("not_party_leader")) {
								group.sendLeaderMessage(ChatColor.RED + "You must be the party leader to queue up.");
							} else if (error.equals("not_in_a_clan")) {
								group.sendLeaderMessage(ChatColor.RED + "You must be in a clan to join that queue.");
							} else if (error.equals("not_an_officer")) {
								group.sendLeaderMessage(ChatColor.RED + "You are not an officer in your clan.");
							} else if (error.equals("too_few_in_party")) {
								group.sendLeaderMessage(ChatColor.RED + "There are not enough members in your party for this queue.");
							} else if (error.equals("too_many_in_party")) {
								group.sendLeaderMessage(ChatColor.RED + "There are too many members in your party for this queue.");
							} else if (error.startsWith("party_member_not_in_clan")) {
								String username = error.split(" ")[1];
								group.sendLeaderMessage(ChatColor.YELLOW + username + ChatColor.RED + " is not in your clan.");
							} else if (error.startsWith("offline_party_member")) {
								String username = error.split(" ")[1];
								group.sendLeaderMessage(ChatColor.YELLOW + username + ChatColor.RED + " is offline.");
							} else if (error.startsWith("already_in_different_game_queue")) {
								String username = error.split(" ")[1];
								group.sendLeaderMessage(ChatColor.YELLOW + username + ChatColor.RED + " is in a different game queue.");
							} else if (error.startsWith("already_in_queue")) {
								String username = error.split(" ")[1];
								group.sendLeaderMessage(ChatColor.YELLOW + username + ChatColor.RED + " is already in a queue.");
							} else if (error.startsWith("already_in_match")) {
								String username = error.split(" ")[1];
								group.sendLeaderMessage(ChatColor.YELLOW + username + ChatColor.RED + " is already in a match.");
							} else if (error.startsWith("already_playing")) {
								String username = error.split(" ")[1];
								group.sendLeaderMessage(ChatColor.YELLOW + username + ChatColor.RED + " is playing a different game.");
							} else if (error.equals("following_player")) {
								group.sendLeaderMessage(ChatColor.RED + "You are currently following somebody.");
							} else if (error.startsWith("not_enough_unranked ")) {
								int amount = Integer.valueOf(error.split(" ")[1]);
								group.sendLeaderMessage(ChatColor.RED + "You must win " + RatingUtil.ARENA_UNRANKED_WINS_NEEDED_FOR_RANKED + " unranked matches before you can play ranked. You have won " + amount + ".");
							} else if (error.equals("not_enough_ranked_matches_left")) {
								group.sendMessage("§3=§b=§3=§b=§3=§b=§3=§b= " + ChatColor.YELLOW + ChatColor.BOLD + "Out of Ranked Matches" + ChatColor.RESET + " §3=§b=§3=§b=§3=§b=§3=§b=");
								group.sendMessage(ChatColor.AQUA + "You have reached your daily quota of ranked matches. You can get more by donating at http://store.badlion.net/");
							} else if (error.startsWith("not_enough_unranked_matches_left")) {
								String[] lids = error.split("#");
								StringBuilder sb = new StringBuilder();

								// NOTE: Keep alive sends unlimited unranked ladder information, but those ladders
								//       will be out of date if new unlimited unranked ladders are chosen at the
								//       time when this player joins an unranked queue

								// Start at 1 because we are returned "not_enough_ranked_matches_left#1#2#3"
								for (int i = 1; i < lids.length; i++) {
									sb.append(ChatColor.YELLOW);
									sb.append(KitRuleSet.getKitRuleSet(Integer.valueOf(lids[i])));
									sb.append(ChatColor.DARK_AQUA);
									sb.append(", ");
								}

								String ladders = sb.toString();
								ladders = ladders.substring(0, ladders.length() - 2);

								group.sendMessage("§3=§b=§3=§b=§3=§b=§3=§b= " + ChatColor.YELLOW + ChatColor.BOLD + "Out of Unranked Matches" + ChatColor.RESET + " §3=§b=§3=§b=§3=§b=§3=§b=");
								group.sendMessage(ChatColor.DARK_AQUA + "Today's ladders in which you can play unlimited unranked matches in are: " + ladders);
								group.sendMessage(ChatColor.AQUA + "You have reached your daily quota of unranked matches. You can get more by donating at http://store.badlion.net/");
							} else {
								group.sendLeaderMessage(ChatColor.RED + "Cannot queue up because: " + error);
							}
						} else {
							group.sendLeaderMessage(ChatColor.RED + "Cannot queue up for (un)ranked right now (2).");
						}

						new BukkitRunnable() {
							@Override
							public void run() {
								State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);
								try {
									currentState.transition(GroupStateMachine.lobbyState, group);
								} catch (IllegalStateTransitionException e) {
									group.sendLeaderMessage(ChatColor.RED + "Cannot queue up for (un)ranked right now (3).");
									return;
								}
							}
						}.runTask(ArenaLobby.getInstance());

					} else {
						group.sendMessage(ChatColor.GREEN + "Added to " + ladder.getKitRuleSet().getName() + " matchmaking.");
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
								return;
							}
						}
					}.runTask(ArenaLobby.getInstance());
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(ArenaLobby.getInstance());
		return;
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
					ArenaLobby.getInstance().getLogger().info("DEBUG REMOVE QUEUE:  PLayerUUID: " + group.getLeader().getUniqueId().toString() + " response: '" + response + "' transitionState: " + transitionState);
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
							group.getLeader().sendMessage(ChatColor.RED + "Unable to leave the queue right now.");
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
		return this.ladder;
	}

	public void setLadder(Ladder ladder) {
		this.ladder = ladder;
	}

	public String getServiceInfo() {
		String ret = this.ladder.getLadderType().getNiceName() + " ";

		List<String> names = new ArrayList<>();
		for (KitRuleSet kitRuleSet : this.ladder.getAllKitRuleSets()) {
			names.add(kitRuleSet.getName());
		}

		return ret + Joiner.on("/").skipNulls().join(names);
	}

	public int getNumberInQueue() {
		return this.ladder.getInQueue();
	}

}
