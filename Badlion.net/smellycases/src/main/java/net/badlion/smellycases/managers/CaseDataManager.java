package net.badlion.smellycases.managers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.AsyncDelayedPlayerJoinEvent;
import net.badlion.gberry.events.GSyncEvent;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.smellycases.Case;
import net.badlion.smellycases.SmellyCases;
import net.badlion.smellycases.events.RequestPlayerOwnedCases;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CaseDataManager implements Listener {

	public static Case getNextCase(UUID uuid, Gberry.ServerType serverType) {
		UserDataManager.UserData userData = UserDataManager.getUserData(uuid);
		JSONObject casesObject = userData.getCases();

		List<Map<String, Object>> casesList = (List<Map<String, Object>>) casesObject.get(serverType.getInternalName() + "_cases");
		if (casesList == null) {
			return null;
		}

		for (Map<String, Object> map : casesList) {
			int totalCases = Gberry.getObjectInteger(map.get("total_cases"));
			int openedCases = Gberry.getObjectInteger(map.get("opened_cases"));
			int remainingCases = totalCases - openedCases;
			if (remainingCases <= 0) {
				continue; // There are no more cases left
			}

			// Great! Use this case
			return new Case((String) map.get("transaction_id"), (List<String>) map.get("prizes"), Gberry.getObjectInteger(map.get("rare_cases")),
					Gberry.getObjectInteger(map.get("super_rare_cases")), Gberry.getObjectInteger(map.get("legendary_cases")), Gberry.getObjectInteger(map.get("total_cases")),
					Gberry.getObjectInteger(map.get("opened_cases")), Gberry.getObjectInteger(map.get("rare_items_received")), Gberry.getObjectInteger(map.get("super_rare_items_received")),
					Gberry.getObjectInteger(map.get("legendary_items_received")));
		}

		return null;
	}

	public static int getRemainingCases(Player player, Gberry.ServerType serverType) {
		UserDataManager.UserData userData = UserDataManager.getUserData(player.getUniqueId());
		JSONObject casesObject = userData.getCases();

		List<Map<String, Object>> casesList = (List<Map<String, Object>>) casesObject.get(serverType.getInternalName() + "_cases");
		if (casesList == null) {
			return 0;
		}

		int remainingCases = 0;
		for (Map<String, Object> map : casesList) {
			int totalCases = Gberry.getObjectInteger(map.get("total_cases"));
			int openedCases = Gberry.getObjectInteger(map.get("opened_cases"));
			remainingCases += totalCases - openedCases;
		}

		return remainingCases;
	}

	public static void giveCases(UUID uuid, Gberry.ServerType serverType, int cases, int rareCases, int superRareCases,
	                             int legendaryCases, String transactionID) {
		UserDataManager.UserData userData = UserDataManager.getUserData(uuid);
		JSONObject casesObject = userData.getCases();

		List<Map<String, Object>> casesList = (List<Map<String, Object>>) casesObject.get(serverType.getInternalName() + "_cases");

		if (casesList == null) {
			casesList = new ArrayList<>();
		}

		Map<String, Object> casesMap = new LinkedHashMap<>();
		casesMap.put("transaction_id", transactionID);

		casesMap.put("total_cases", cases);
		casesMap.put("opened_cases", 0);
		casesMap.put("prizes", new LinkedList<String>());

		casesMap.put("rare_cases", rareCases);
		casesMap.put("super_rare_cases", superRareCases);
		casesMap.put("legendary_cases", legendaryCases);

		casesMap.put("rare_items_received", 0);
		casesMap.put("super_rare_items_received", 0);
		casesMap.put("legendary_items_received", 0);
		casesList.add(casesMap);

		casesObject.put(serverType.getInternalName() + "_cases", casesList);

		userData.setCases(casesObject, false);
	}

	public static void giveCasesOffline(final UUID uuid, final Gberry.ServerType serverType, final int cases, final int rareCases, final int superRareCases,
								 final int legendaryCases, final String transactionID) {
		new BukkitRunnable() {
			public void run() {
				UserDataManager.UserData userData = UserDataManager.getUserDataFromDB(uuid);
				JSONObject casesObject = userData.getCases();

				List<Map<String, Object>> casesList = (List<Map<String, Object>>) casesObject.get(serverType.getInternalName() + "_cases");

				if (casesList == null) {
					casesList = new ArrayList<>();
				}

				Map<String, Object> casesMap = new LinkedHashMap<>();
				casesMap.put("transaction_id", transactionID);

				casesMap.put("total_cases", cases);
				casesMap.put("opened_cases", 0);
				casesMap.put("prizes", new LinkedList<String>());

				casesMap.put("rare_cases", rareCases);
				casesMap.put("super_rare_cases", superRareCases);
				casesMap.put("legendary_cases", legendaryCases);

				casesMap.put("rare_items_received", 0);
				casesMap.put("super_rare_items_received", 0);
				casesMap.put("legendary_items_received", 0);
				casesList.add(casesMap);

				casesObject.put(serverType.getInternalName() + "_cases", casesList);

				userData.setCases(casesObject, true);
			}
		}.runTaskAsynchronously(SmellyCases.getInstance());
	}


	public static void removeCases(UUID uuid, Gberry.ServerType serverType, String transactionID) {
		UserDataManager.UserData userData = UserDataManager.getUserData(uuid);
		JSONObject casesObject = userData.getCases();

		List<Map<String, Object>> casesList = (List<Map<String, Object>>) casesObject.get(serverType.getInternalName() + "_cases");

		if (casesList == null) {
			return;
		}

		Map<String, Object> casesMap = null;
		for (Map<String, Object> map : casesList) {
			if (map.get("transaction_id").equals(transactionID)) {
				casesMap = map;
				break;
			}
		}

		if (casesMap == null) {
			Gberry.log("ERROR-Cases", "Cases for UUID - " + uuid.toString() + " with Transaction ID - " + transactionID + " for server type - " + serverType + " was not found! Cannot remove from " + transactionID);
			return;
		}

		int totalCases = Gberry.getObjectInteger(casesMap.get("total_cases"));
		casesMap.put("opened_cases_cache", casesMap.get("opened_cases")); // Incase they are removed by accident
		casesMap.put("opened_cases", totalCases);

		userData.setCases(casesObject, false);
	}

	public static List<String> getCasePrizesFromTransactionId(UUID uuid, Gberry.ServerType serverType, String transactionId) {
		UserDataManager.UserData userData = UserDataManager.getUserData(uuid);
		JSONObject casesObject = userData.getCases();
		List<Map<String, Object>> casesList = (List<Map<String, Object>>) casesObject.get(serverType.getInternalName() + "_cases");

		if (casesList == null) {
			return null;
		}

		for (Map<String, Object> map : casesList) {
			if (map.get("transaction_id").equals(transactionId)) {
				return (List<String>) map.get("prizes");
			}
		}

		return null;
	}

	public static void onOpenCase(UUID uuid, Gberry.ServerType serverType, String prize, Case caseToOpen) {
		UserDataManager.UserData userData = UserDataManager.getUserData(uuid);
		JSONObject casesObject = userData.getCases();

		List<Map<String, Object>> casesList = (List<Map<String, Object>>) casesObject.get(serverType.getInternalName() + "_cases");

		Map<String, Object> casesMap = null;

		int casesMapLocation = 0;
		for (Map<String, Object> map : casesList) {
			if (map.get("transaction_id").equals(caseToOpen.getTransactionId())) {
				casesMap = map;
				break;
			}
			casesMapLocation++;
		}

		if (casesMap == null) {
			return;
		}

		if (caseToOpen.isRare()) {
			casesMap.put("rare_items_received", caseToOpen.getRareItemsReceived() + 1);
		}

		List<String> prizes = caseToOpen.getPrizes();
		prizes.add(prize);
		casesMap.put("prizes", prizes);
		casesMap.put("opened_cases", caseToOpen.getOpenedCases() + 1);

		casesList.set(casesMapLocation, casesMap);

		casesObject.put(serverType.getInternalName() + "_cases", casesList);

		userData.setCases(casesObject, true);
	}

	@EventHandler
	public void onGSync(GSyncEvent event) {
		if (event.getArgs().size() < 2) {
			return;
		}

		String subChannel = event.getArgs().get(0);
		if (subChannel.equals("Cases")) {
			String msg = event.getArgs().get(1);
			UUID uuid = UUID.fromString(event.getArgs().get(2));

			if (!Gberry.isPlayerOnline(SmellyCases.getInstance().getServer().getPlayer(uuid))) {
				return; // Only handle if they are online
			}

			Gberry.ServerType serverType = Gberry.ServerType.valueOf(event.getArgs().get(3));
			String transactionID = event.getArgs().get(4);

			if (msg.equals("add")) {
				int cases = Integer.parseInt(event.getArgs().get(5));
				int rareCases = Integer.parseInt(event.getArgs().get(6));
				int superRareCases = Integer.parseInt(event.getArgs().get(7));
				int legendaryCases = Integer.parseInt(event.getArgs().get(8));
				giveCases(uuid, serverType, cases, rareCases, superRareCases, legendaryCases, transactionID);
			} else if (msg.equals("remove")) {
				removeCases(uuid, serverType, transactionID);
			}
		} else if (subChannel.equals("LobbyMessage")) {
			if (Gberry.serverType != Gberry.ServerType.LOBBY) {
				return;
			}

			String msg = "";
			for (String string : event.getArgs().subList(1, event.getArgs().size())) {
				if (msg.equals("")) {
					msg += string;
				} else {
					msg += " " + string;
				}
			}

			Gberry.broadcastMessage(msg);
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onAsyncDelayedPlayerJoinEvent(final AsyncDelayedPlayerJoinEvent event) {
		event.getRunnables().add(new Runnable() {
			@Override
			public void run() {
				if (!Gberry.isPlayerOnline(Bukkit.getPlayer(event.getUuid()))) {
					return;
				}

				// This needs to be fired after cosmetic settings have loaded, which use this same event on the HIGH priority
				SmellyCases.getInstance().getServer().getPluginManager().callEvent(new RequestPlayerOwnedCases(Bukkit.getPlayer(event.getUuid())));
			}
		});
	}

}
