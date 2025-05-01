package net.badlion.arenalobby.managers;

import net.badlion.arenalobby.helpers.DuelHelper;
import net.badlion.gberry.Gberry;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DuelRequestManager {

	private static Map<UUID, DuelHelper.DuelCreator> senderDuelCreator = new HashMap<>(); // Stores sender, duel creator
	private static Map<UUID, DuelHelper.DuelCreator> receiverDuelCreator = new HashMap<>(); // Stores receiver, duel creator


	public static void addDuelCreator(UUID senderId, UUID receiverId, DuelHelper.DuelCreator duelCreator) {
		Gberry.log("DUEL", "Duel creator added");
		DuelRequestManager.senderDuelCreator.put(senderId, duelCreator);
		DuelRequestManager.receiverDuelCreator.put(receiverId, duelCreator);
	}

	public static DuelHelper.DuelCreator getDuelCreator(UUID uuid) {
		DuelHelper.DuelCreator duelCreator = DuelRequestManager.senderDuelCreator.get(uuid);
		if (duelCreator != null) {
			return duelCreator;
		}
		duelCreator = DuelRequestManager.receiverDuelCreator.get(uuid);
		if (duelCreator != null) {
			return duelCreator;
		}

		return null;
	}

	public static DuelHelper.DuelCreator removeDuelCreator(UUID uuid) {
		Gberry.log("DUEL", "Remove duel creator called");
		DuelHelper.DuelCreator duelCreator = DuelRequestManager.senderDuelCreator.remove(uuid);
		if (duelCreator != null) { // Sender
			Gberry.log("DUEL", "Remove duel creator WAS NOT null");
			DuelRequestManager.receiverDuelCreator.remove(duelCreator.getReceiverId());
			return duelCreator;
		}
		Gberry.log("DUEL", "Remove duel creator WAS null");

		duelCreator = DuelRequestManager.receiverDuelCreator.remove(uuid);

		// Null check
		if (duelCreator == null) return null;

		DuelRequestManager.senderDuelCreator.remove(duelCreator.getSenderId());
		return duelCreator;
	}
}
