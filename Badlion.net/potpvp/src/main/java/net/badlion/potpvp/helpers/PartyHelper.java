package net.badlion.potpvp.helpers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.Party;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.exceptions.OutOfArenasException;
import net.badlion.potpvp.inventories.party.PartyListInventory;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.matchmaking.PartyFFAMatch;
import net.badlion.potpvp.matchmaking.PartyFightMatch;
import net.badlion.potpvp.matchmaking.RedRoverMatch;
import net.badlion.potpvp.rulesets.BuildUHCRuleSet;
import net.badlion.potpvp.rulesets.IronBuildUHCRuleSet;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.rulesets.SpleefRuleSet;
import net.badlion.potpvp.states.matchmaking.GameState;
import net.badlion.potpvp.states.party.PartyState;
import net.badlion.smellychat.Channel;
import net.badlion.smellychat.managers.ChannelManager;
import net.badlion.smellychat.managers.ChatSettingsManager;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartyHelper {

	public static final int MIN_PARTY_FFA_PLAYERS = 3;
	public static final int MAX_PARTY_FFA_PLAYERS = 16;
	public static final int MIN_PARTY_FIGHT_PLAYERS = 3;
	public static final int MAX_PARTY_FIGHT_PLAYERS = 16;
	public static final int MIN_RED_ROVER_PLAYERS = 3;

	private static ItemStack partyInventoriesItem;
	private static ItemStack ranked2v2Item;
	private static ItemStack ranked3v3Item;
	private static ItemStack ranked5v5Item;
	private static ItemStack partyListingItem;
	private static ItemStack partyEventItem;
	private static ItemStack leavePartyItem;
	private static ItemStack leavePartyRandomQueueItem;

	public static void initialize() {
		// Hotbar items
		PartyHelper.partyInventoriesItem = ItemStackUtil.createItem(Material.BOOK, ChatColor.GREEN + "View Player Inventories");

		PartyHelper.ranked2v2Item = ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "Ranked 2v2");

		PartyHelper.ranked3v3Item = ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "Ranked 3v3");

		PartyHelper.ranked5v5Item = ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "Ranked 5v5");

		PartyHelper.partyListingItem = ItemStackUtil.createItem(Material.NAME_TAG, ChatColor.GREEN + "View All Parties");

		PartyHelper.partyEventItem = ItemStackUtil.createItem(Material.IRON_SWORD, ChatColor.GREEN + "Party Events");

		PartyHelper.leavePartyItem = ItemStackUtil.createItem(Material.REDSTONE, ChatColor.GREEN + "Leave Party");

		PartyHelper.leavePartyRandomQueueItem = ItemStackUtil.createItem(Material.REDSTONE, ChatColor.GREEN + "Leave Party Random Queue");
	}

	public static ItemStack getPartyInventoriesItem() {
		return partyInventoriesItem;
	}

	public static ItemStack getRanked2v2Item() {
        return ranked2v2Item;
    }

	public static ItemStack getRanked3v3Item() {
		return ranked3v3Item;
	}

	public static ItemStack getRanked5v5Item() {
		return ranked5v5Item;
	}

	public static ItemStack getPartyListingItem() {
		return partyListingItem;
	}

	public static ItemStack getPartyEventItem() {
		return partyEventItem;
	}

	public static ItemStack getLeavePartyItem() {
		return leavePartyItem;
	}

	public static ItemStack getLeavePartyRandomQueueItem() {
		return leavePartyRandomQueueItem;
	}

	public static boolean isPlayerInventory(Inventory inventory) {
		return inventory != null && inventory.getName().endsWith("s Inventory");
	}

	public static Inventory createPlayerInventory(SmellyInventory smellyInventory, Player player) {
		Inventory inventory;

		if (smellyInventory != null) {
			inventory = PotPvP.getInstance().getServer().createInventory(smellyInventory.getFakeHolder(), 54,
					ChatColor.BOLD + ChatColor.AQUA.toString() + player.getName() + "'s Inventory");

			inventory.setItem(53, SmellyInventory.getCloseInventoryItem());
		} else {
			inventory = PotPvP.getInstance().getServer().createInventory(null, 54,
					ChatColor.BOLD + ChatColor.AQUA.toString() + player.getName() + "'s Inventory");
		}

		// Fill inventory
		ItemStack[] extraItems = new ItemStack[1];
		if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
			extraItems[0] = player.getInventory().getItemInOffHand();
		}
		KitHelper.fillInventoryWithContents(inventory, player.getInventory().getArmorContents(), player.getInventory().getContents(), extraItems);

		return inventory;
	}

	public static Inventory createPlayerInventory(SmellyInventory smellyInventory, Player player, ItemStack[] armorContents, ItemStack[] inventoryContents, ItemStack[] extraItemContents) {
		Inventory inventory = PotPvP.getInstance().getServer().createInventory(smellyInventory.getFakeHolder(), 54,
				ChatColor.BOLD + ChatColor.AQUA.toString() + player.getName() + "s Inventory");

		// Fill inventory
		KitHelper.fillInventoryWithContents(inventory, armorContents, inventoryContents, extraItemContents);

		inventory.setItem(53, SmellyInventory.getCloseInventoryItem());

		return inventory;
	}

	public static Group handleCreate(Player player, State<Group> newState,
                                    boolean verbose) {
        // New group/party
        Party party = new Party(player, true);
        //currentState.remove(oldGroup, true);
        Group group = new Group(party, newState);
        PotPvP.getInstance().updatePlayerGroup(player, group);

        if (verbose) {
            player.sendMessage(ChatColor.BLUE + "Party created, use \"/invite <name>\" to invite people to your party");
        }

        return group;
    }

    public static Group addToPartyGroup(Player player, Party party, boolean verbose) {
        // Party management
        party.addPlayer(player);
        //currentState.remove(oldGroup, true); // Don't leak memory from old state
        Group group = PotPvP.getInstance().getPlayerGroup(party.getPartyLeader());
        PotPvP.getInstance().updatePlayerGroup(player, group);

		PartyState.givePlayerItems(player, false);

        // Send pretty messages
        if (verbose) {
            player.sendMessage(ChatColor.BLUE + "Accepted party invite.");
            party.getPartyLeader().sendMessage(ChatColor.BLUE + player.getName() + " has accepted your party invite.");
        }

	    // Update party listing item for the new set of players
	    PartyListInventory.updatePartyListing(group);

        return group;
    }

    public static void handleLeave(Player player, Party party, boolean verbose) {
	    //PotPvP.getInstance().getServer().getPluginManager().callEvent(new PlayerLeavePartyEvent(player));

        // Host left party?
        if (player == party.getPartyLeader()) {
	        // Fuck it if they're in a war
	        if (party.getPlayers().size() > 1) {
		        // Find a new leader
		        Player newLeader = party.getPlayers().get(0);
		        if (newLeader == player) newLeader = party.getPlayers().get(1);

		        Group group = PotPvP.getInstance().getPlayerGroup(newLeader);

		        party.setPartyLeader(newLeader); // promote index 0 to leader

		        // Set new inventory for leader
		        if (GameState.getGroupGame(group) == null) {
					PartyState.givePlayerItems(party.getPartyLeader(), true);
		        }

		        // Inform party of new leader
		        Gberry.log("PARTY", "New leader: " + party.getPartyLeader().getName());
		        if (verbose) {
			        for (Player pl : party.getPlayers()) {
				        pl.sendMessage(ChatColor.BLUE + "Host left party, new party leader is now " + party.getPartyLeader().getName());
			        }
		        }

		        // Update party listing item for the new set of players
		        PartyListInventory.updatePartyListing(PotPvP.getInstance().getPlayerGroup(party.getPartyLeader()));
	        }
        } else {
            if (verbose) {
                for (Player pl : party.getPlayers()) {
                    pl.sendMessage(ChatColor.BLUE + player.getName() + " left the party.");
                }
            }
        }

        // Remove from matchmaking if in queue
        if (GroupStateMachine.getInstance().getCurrentState(PotPvP.getInstance().getPlayerGroup(party.getPartyLeader()))
                == GroupStateMachine.matchMakingState) {
            party.getPartyLeader().performCommand("leave");
        }

	    if (Gberry.isPlayerOnline(player)) {
		    // Hook in the /pc -> /gc
		    ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);
		    Channel channel = ChannelManager.getChannel(chatSettings.getActiveChannel());
		    if (channel.getIdentifier().equalsIgnoreCase("P")) {
			    chatSettings.setActiveChannel("G");
		    }

		    if (verbose) {
			    player.sendMessage(ChatColor.BLUE + "Left party.");
		    }
	    }
    }

	public static void startPartyFight(Group group, KitRuleSet kitRuleSet, ItemStack[] armorContents, ItemStack[] inventoryContents) {
		if (kitRuleSet == null) {
			Gberry.log("PARTY", "null kitruleset detected, backing out");
			group.sendMessage(ChatColor.RED + "An error has occured, please notify an administrator and post this on the forums under bug reports. #1");
			return;
		}

		// Is the party leader a pleb? (not famous)
		if (!group.getLeader().hasPermission("badlion.famous") && !group.getLeader().hasPermission("badlion.twitch")
				&& !group.getLeader().hasPermission("badlion.youtube") && !group.getLeader().hasPermission("badlion.staff")) {
			// Correct number of players in party?
			if (group.players().size() < PartyHelper.MIN_PARTY_FIGHT_PLAYERS) {
				group.sendLeaderMessage(ChatColor.RED + "You do not have enough players in your party (" + PartyHelper.MIN_PARTY_FIGHT_PLAYERS + " required).");
				return;
			} else if (group.players().size() > PartyHelper.MAX_PARTY_FIGHT_PLAYERS) {
				group.sendLeaderMessage(ChatColor.RED + "You have too many players in your party (max of " + PartyHelper.MAX_PARTY_FIGHT_PLAYERS + ").");
				return;
			}
		}

		// Get an arena before we do all the party shit
		Arena arena;
		try {
			arena = ArenaManager.getArena(kitRuleSet.getArenaType());
		} catch (OutOfArenasException e) {
			group.sendLeaderMessage(ChatColor.RED + "Out of arenas, try again in a few seconds.");
			return;
		}

		// Shuffle players list
		List<Player> players = new ArrayList<>(group.players());
		Collections.shuffle(players);

		// Select random player
		Player leader = players.get(0);
		if (leader == group.getLeader()) leader = players.get(1);

		// Leave party
		Group leaderGroup = new Group(leader, true);
		PotPvP.getInstance().updatePlayerGroup(leader, leaderGroup);
		PartyHelper.handleLeave(leader, group.getParty(), false);

		// Create party
		leaderGroup = PartyHelper.handleCreate(leader, GroupStateMachine.partyState, false);

		// Add people to second party (if main party uneven, add extra person to random party)
		int size = (int) Math.ceil(players.size() / 2D) - 1; // -1 because of the leader
		for (int i = 0; i < size; i++) {
			Player pl = players.get(i);
			if (pl == group.getLeader() || pl == leader) {
				size++; // Increase size by 1 because we still need a party member and we're skipping this loop cycle
				continue;
			}

			// Is this the last person we're cycling through?
			if (i == size - 1) {
				if (group.players().size() - 1 == leaderGroup.players().size()) { // -1 because player is currently in leader party
					// Randomly assign the extra person to one of the parties
					if (Math.random() < 0.5D) { // Use Math.random() to avoid making/storing a Random object
						// Keep person in leader's party
						break;
					}
				}
			}

			// Leave party
			Group group2 = new Group(pl, true);
			PotPvP.getInstance().updatePlayerGroup(pl, group2);
			PartyHelper.handleLeave(pl, group.getParty(), false);

			PartyHelper.addToPartyGroup(pl, leaderGroup.getParty(), false);
		}

		try {
			// Now start a match
			PartyFightMatch match = new PartyFightMatch(arena, kitRuleSet, armorContents, inventoryContents);

			// Transfer states
			GroupStateMachine.partyState.transition(GroupStateMachine.matchMakingState, group);
			GroupStateMachine.partyState.transition(GroupStateMachine.matchMakingState, leaderGroup);
			GroupStateMachine.matchMakingState.push(GroupStateMachine.regularMatchState, group, match);
			GroupStateMachine.matchMakingState.push(GroupStateMachine.regularMatchState, leaderGroup, match);

			match.prepGame(group, leaderGroup);
			match.startGame();
		} catch (IllegalStateTransitionException e) {
			arena.toggleBeingUsed();
			Bukkit.getLogger().info("GBERRY HALP 3");
			PotPvP.getInstance().somethingBroke(group.getLeader(), group, leaderGroup);
		}
	}

	public static void startPartyFFA(Group group, KitRuleSet kitRuleSet, ItemStack[] armorContents, ItemStack[] inventoryContents) {
		if (kitRuleSet == null) {
			Gberry.log("PARTY", "null kitruleset detected, backing out");
			group.sendMessage(ChatColor.RED + "An error has occured, please notify an administrator and post this on the forums under bug reports. #1");
			return;
		}

		// Is the party leader a pleb? (not famous)
		if (!group.getLeader().hasPermission("badlion.famous") && !group.getLeader().hasPermission("badlion.twitch")
				&& !group.getLeader().hasPermission("badlion.youtube") && !group.getLeader().hasPermission("badlion.staff")) {
			// Correct number of players in party?
			if (group.players().size() < PartyHelper.MIN_PARTY_FFA_PLAYERS) {
				group.sendLeaderMessage(ChatColor.RED + "You do not have enough players in your party (" + PartyHelper.MIN_PARTY_FFA_PLAYERS + " required).");
				return;
			} else if (group.players().size() > PartyHelper.MAX_PARTY_FFA_PLAYERS) {
				group.sendLeaderMessage(ChatColor.RED + "You have too many players in your party (max of " + PartyHelper.MAX_PARTY_FFA_PLAYERS + ").");
				return;
			}
		}

		// Get an arena before we do all the party shit
		Arena arena;
		try {
			if (kitRuleSet instanceof SpleefRuleSet) {
				arena = ArenaManager.getArena(ArenaManager.ArenaType.SPLEEF_FFA);
			} else if (kitRuleSet instanceof BuildUHCRuleSet || kitRuleSet instanceof IronBuildUHCRuleSet) {
				arena = ArenaManager.getArena(ArenaManager.ArenaType.BUILD_UHC_FFA);
			} else {
				arena = ArenaManager.getArena(ArenaManager.ArenaType.PARTY_FFA);
			}
		} catch (OutOfArenasException e) {
			group.sendLeaderMessage(ChatColor.RED + "Out of arenas, try again in a few seconds.");
			return;
		}

		try {
			// Now start a match
			PartyFFAMatch match = new PartyFFAMatch(arena, kitRuleSet, armorContents, inventoryContents);

			// Transfer states
			GroupStateMachine.partyState.transition(GroupStateMachine.matchMakingState, group);
			GroupStateMachine.matchMakingState.push(GroupStateMachine.regularMatchState, group, match);

			match.prepGame(group, group);
			match.startGame();
		} catch (IllegalStateTransitionException e) {
			arena.toggleBeingUsed();
			PotPvP.getInstance().somethingBroke(group.getLeader(), group);
		}
	}

	public static void startPartyRedRover(Group group, KitRuleSet kitRuleSet, ItemStack[] armorContents, ItemStack[] inventoryContents) {
		if (kitRuleSet == null) {
			Gberry.log("PARTY", "null kitruleset detected, backing out");
			group.sendMessage(ChatColor.RED + "An error has occured, please notify an administrator and post this on the forums under bug reports. #1");
			return;
		}

		// At least 4 people in party?
		if (group.players().size() < PartyHelper.MIN_RED_ROVER_PLAYERS) {
			group.sendLeaderMessage(ChatColor.RED + "You do not have enough players in your party.");
			return;
		}

		// Get an arena before we do all the party shit
		Arena arena;
		try {
			arena = ArenaManager.getArena(kitRuleSet.getArenaType());
		} catch (OutOfArenasException e) {
			group.sendLeaderMessage(ChatColor.RED + "Out of arenas, try again in a few seconds.");
			return;
		}

		// Shuffle players list
		List<Player> players = new ArrayList<>(group.players());
		Collections.shuffle(players);

		// Select random player
		Player leader = players.get(0);
		if (leader == group.getLeader()) leader = players.get(1);

		// Leave party
		Group leaderGroup = new Group(leader, true);
		PotPvP.getInstance().updatePlayerGroup(leader, leaderGroup);
		PartyHelper.handleLeave(leader, group.getParty(), false);

		// Create party
		leaderGroup = PartyHelper.handleCreate(leader, GroupStateMachine.partyState, false);

		// Add people to second party (if main party uneven, add extra person to random party)
		int size = (int) Math.ceil(players.size() / 2D) - 1; // -1 because of the leader
		for (int i = 0; i < size; i++) {
			Player pl = players.get(i);
			if (pl == group.getLeader() || pl == leader) {
				size++; // Increase size by 1 because we still need a party member and we're skipping this loop cycle
				continue;
			}

			// Is this the last person we're cycling through?
			if (i == size - 1) {
				if (group.players().size() - 1 == leaderGroup.players().size()) { // -1 because player is currently in leader party
					// Randomly assign the extra person to one of the parties
					if (Math.random() < 0.5D) { // Use Math.random() to avoid making/storing a Random object
						// Keep person in leader's party
						break;
					}
				}
			}

			// Leave party
			Group group2 = new Group(pl, true);
			PotPvP.getInstance().updatePlayerGroup(pl, group2);
			PartyHelper.handleLeave(pl, group.getParty(), false);

			PartyHelper.addToPartyGroup(pl, leaderGroup.getParty(), false);
		}

		try {
			// Now start a match
			RedRoverMatch match = new RedRoverMatch(arena, kitRuleSet, armorContents, inventoryContents);

			// Transfer states
			GroupStateMachine.partyState.transition(GroupStateMachine.matchMakingState, group);
			GroupStateMachine.partyState.transition(GroupStateMachine.matchMakingState, leaderGroup);
			GroupStateMachine.matchMakingState.push(GroupStateMachine.regularMatchState, group, match);
			GroupStateMachine.matchMakingState.push(GroupStateMachine.regularMatchState, leaderGroup, match);

			match.prepGame(group, leaderGroup);
			match.startGame();
		} catch (IllegalStateTransitionException e) {
			arena.toggleBeingUsed();
			Bukkit.getLogger().info("GBERRY HALP 5");
			PotPvP.getInstance().somethingBroke(group.getLeader(), group, leaderGroup);
		}
	}

}
