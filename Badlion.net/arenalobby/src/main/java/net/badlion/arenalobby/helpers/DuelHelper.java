package net.badlion.arenalobby.helpers;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.managers.DuelRequestManager;
import net.badlion.arenalobby.managers.PotPvPPlayerManager;
import net.badlion.arenalobby.tasks.DuelRequestTimeoutTask;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

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

	public static void handleDuelAccept(final Player player, final DuelCreator duelCreator) {
		Gberry.log("DUEL", "handleAcceptDeny");
		if (duelCreator.getDuelTimeoutTask() != null) {
			duelCreator.getDuelTimeoutTask().cancel();
		}

		if(!player.getUniqueId().equals(duelCreator.getReceiverId())){
			player.sendFormattedMessage("{0}You are not able to accept this duel request.", ChatColor.RED);
			return;
		}

		duelCreator.setAccepted(true);

		// Remove duel creator since we already have a reference to it
		//GroupStateMachine.duelRequestState.removeDuelCreator(group);

		PotPvPPlayerManager.addDebug(player, "Accepted duel request");

		DuelRequestManager.removeDuelCreator(duelCreator.getReceiverId());
		DuelRequestManager.removeDuelCreator(duelCreator.getSenderId());

		new BukkitRunnable() {
			@Override
			public void run() {
				JSONObject data = new JSONObject();
				data.put("sender_uuid", duelCreator.getSenderId().toString());
				data.put("target_uuid", duelCreator.getReceiverId().toString());
				data.put("server_region", Gberry.serverRegion.toString().toLowerCase());
				data.put("arena_type", String.valueOf(duelCreator.getKitRuleSet().getArenaType().ordinal()));
				data.put("ladder", String.valueOf(duelCreator.getKitRuleSet().getName()));
				data.put("accepted", "true");
				switch (duelCreator.getBestOf()) {
					case 1:
						break;
					case 3:
						data.put("best_of", "3");
						break;
					case 5:
						data.put("best_of", "5");
				}
				try {
					JSONObject response = Gberry.contactMCP("arena-accept-or-deny-duel-request", data);
					ArenaLobby.getInstance().getLogger().log(Level.INFO, "[sending duel accept]: " + data);
					ArenaLobby.getInstance().getLogger().log(Level.INFO, "Getting duel accept response " + response);
					if (!response.equals(MCPManager.successResponse)) {
						player.sendMessage(ChatColor.RED + DuelHelper.duelAcceptDenyErrorString((String) response.get("error")));
					} else {
						player.sendFormattedMessage("{0}Accepted duel request from {1}", ChatColor.GREEN, duelCreator.getSenderName());
					}
				} catch (HTTPRequestFailException e) {
					player.sendFormattedMessage("{0}Unable to accept duel request try again later.", ChatColor.RED);
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(ArenaLobby.getInstance());
	}

	public static void handleDuelDeny(boolean sendDenyMessage, UUID receiverId, UUID senderId) {
		Gberry.log("DUEL", "Handling duel deny");


		// Remove the duel creator
		final DuelCreator duelCreator = DuelRequestManager.getDuelCreator(receiverId);

		// Cancel duel timeout task
		if (duelCreator.getDuelTimeoutTask() != null) {
			duelCreator.getDuelTimeoutTask().cancel();
		}

		// Close inventory for players/party leaders
	    /*if(duelCreator.getSender().getLeader() != null) {
            BukkitUtil.closeInventory(duelCreator.getSender().getLeader());
        }
        if(duelCreator.getReceiverIfOnline() != null) {
            BukkitUtil.closeInventory(duelCreator.getReceiverIfOnline());
        }*/
		DuelRequestManager.removeDuelCreator(receiverId);
		DuelRequestManager.removeDuelCreator(senderId);

		new BukkitRunnable() {
			@Override
			public void run() {
				JSONObject data = new JSONObject();
				data.put("sender_uuid", duelCreator.getSenderId().toString());
				data.put("target_uuid", duelCreator.getReceiverId().toString());
				data.put("accepted", "false");
				try {
					JSONObject response = Gberry.contactMCP("arena-accept-or-deny-duel-request", data);
					ArenaLobby.getInstance().getLogger().log(Level.INFO, "[sending duel deny]: " + data);
					ArenaLobby.getInstance().getLogger().log(Level.INFO, "Getting duel deny response " + response);
					if (!response.equals(MCPManager.successResponse)) {
						// Maybe we dont need to tell them about the duel deny idk
						//duelCreator.sendMessageToSenderIfOnline(ChatColor.RED + DuelHelper.duelAcceptDenyErrorString((String) response.get("error")));
					}
				} catch (HTTPRequestFailException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(ArenaLobby.getInstance());
	}


	private static String duelAcceptDenyErrorString(String error) {
		switch (error) {
			case "not_online":
				return "That player is no longer online.";
			case "opponent_not_party_leader ":
				return "That player is not the party leader.";
			case "offline_opponent_party_member":
				return "That party has a member offline.";
			case "not_party_leader":
				return "You are not the party leader.";
			case "offline_party_member":
				return "A member in your party is offline.";
		}
		return "ERROR: " + error;
	}

	public static String duelRequestErrorString(String error) {
		switch (error) {
			case "not_on_arena":
				return "That player is not on arena.";
			case "opponent_not_party_leader ":
				return "That player is not the party leader.";
			case "offline_opponent_party_member":
				return "That party has a member offline.";
			case "not_party_leader":
				return "You are not the party leader.";
			case "offline_party_member":
				return "A member in your party is offline.";
		}
		return "ERROR: " + error;
	}

	public static class DuelCreator {

		private UUID senderId;

		private String senderName;

		private UUID receiverId;

		private String receiverName;

		private boolean accepted = false;

		private boolean redRover = false;

		private int bestOf = 1;

		private boolean selectingCustomKits = false;
		private KitRuleSet kitRuleSet;
		private BukkitTask duelTimeoutTask = null;

		private Map<Player, Integer> customKitSelections = new HashMap<>();

		public DuelCreator(UUID senderId, UUID receiverId) {
			Gberry.log("DUEL", "Duel creator created");
			this.senderId = senderId;
			this.receiverId = receiverId;

			// Save the duel request
			DuelRequestManager.addDuelCreator(senderId, receiverId, this);
		}

		public int getBestOf() {
			return bestOf;
		}

		public void setBestOf(int bestOf) {
			this.bestOf = bestOf;
		}

		public UUID getSenderId() {
			return this.senderId;
		}

		public UUID getReceiverId() {
			return this.receiverId;
		}

		public String getSenderName() {
			return senderName;
		}

		public String getReceiverName() {
			return receiverName;
		}

		public void setSenderName(String name) {
			this.senderName = name;
		}

		public void setReceiverName(String name) {
			this.receiverName = name;
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

		public KitRuleSet getKitRuleSet() {
			return this.kitRuleSet;
		}

		public void setKitRuleSet(KitRuleSet kitRuleSet) {
			this.kitRuleSet = kitRuleSet;
		}

		public BukkitTask getDuelTimeoutTask() {
			return duelTimeoutTask;
		}

		public void startDuelTimeoutTask() {
			// Create a timeout task
			DuelRequestTimeoutTask duelRequestTimeoutTask = new DuelRequestTimeoutTask(this);
			this.duelTimeoutTask = BukkitUtil.runTaskLater(duelRequestTimeoutTask, 300L);
		}

		public void sendMessageToSenderIfOnline(String message) {
			Player sender = Bukkit.getPlayer(senderId);
			if (sender != null) {
				sender.sendMessage(message);
			}
		}

		public void sendMessageToReceiverIfOnline(String message) {
			Player receiver = Bukkit.getPlayer(receiverId);
			if (receiver != null) {
				receiver.sendMessage(message);
			}
		}

	}

}
