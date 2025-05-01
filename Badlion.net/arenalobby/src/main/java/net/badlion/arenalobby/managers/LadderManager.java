package net.badlion.arenalobby.managers;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.GroupStateMachine;
import net.badlion.arenalobby.inventories.clan.ClanRanked5v5Inventory;
import net.badlion.arenalobby.inventories.lobby.Ranked1v1Inventory;
import net.badlion.arenalobby.inventories.lobby.Unranked1v1Inventory;
import net.badlion.arenalobby.inventories.party.Ranked2v2Inventory;
import net.badlion.arenalobby.inventories.party.Ranked3v3Inventory;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.arenalobby.ladders.MatchLadder;
import net.badlion.arenalobby.matchmaking.EventQueueService;
import net.badlion.arenalobby.matchmaking.QueueService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LadderManager {

	// All we really need is the amount of people in each ladder, this is updated from the bukkit keepalive

	private static Map<String, Ladder> ladder1v1RankedMap = new LinkedHashMap<>();
	private static Map<String, Ladder> ladder2v2RankedMap = new LinkedHashMap<>();
	private static Map<String, Ladder> ladder3v3RankedMap = new LinkedHashMap<>();
	private static Map<String, Ladder> ladderClan5v5RankedMap = new LinkedHashMap<>();

	private static Map<String, Ladder> ladder1v1UnrankedMap = new LinkedHashMap<>();

	private static Map<Integer, String> ladderIdToLadderNameMap = new HashMap<>();

	public static EventQueueService uhcMeetupQueue;


	public static Map<String, Ladder> getLadderMap(ArenaCommon.LadderType ladderType) {
		if (ladderType == ArenaCommon.LadderType.UNRANKED_1V1) {
			return ladder1v1UnrankedMap;
		} else if (ladderType == ArenaCommon.LadderType.RANKED_1V1) {
			return ladder1v1RankedMap;
		} else if (ladderType == ArenaCommon.LadderType.RANKED_2V2) {
			return ladder2v2RankedMap;
		} else if (ladderType == ArenaCommon.LadderType.RANKED_3V3) {
			return ladder3v3RankedMap;
		} else if (ladderType == ArenaCommon.LadderType.RANKED_5V5_CLAN) {
			return ladderClan5v5RankedMap;
		}

		return null;
	}

	public static void updateTotalPlayersForLadder(ArenaCommon.LadderType ladderType) {
		switch (ladderType) {
			case RANKED_1V1:
				Ranked1v1Inventory.updateRanked1v1Inventory();
				break;
			case RANKED_2V2:
				Ranked2v2Inventory.updateRanked2v2Inventory();
				break;
			case RANKED_3V3:
				Ranked3v3Inventory.updateRanked3v3Inventory();
				break;
			case UNRANKED_1V1:
				Unranked1v1Inventory.updateUnranked1v1Inventory();
				break;
			case RANKED_5V5_CLAN:
				ClanRanked5v5Inventory.updateRanked5v5Inventory();
				break;
		}
	}

	public static ArenaCommon.LadderType ladderStringToType(String tag) {
		for (ArenaCommon.LadderType ladderType : ArenaCommon.LadderType.values()) {
			if (ladderType.getTag().equals(tag)) {
				return ladderType;
			}
		}
		return null;
	}

	public static void registerLadder(String key, Ladder ladder) {
		LadderManager.getLadderMap(ladder.getLadderType()).put(key, ladder);
		LadderManager.ladderIdToLadderNameMap.put(ladder.getLadderId(), key);
	}

	public static Ladder getLadder(String key, ArenaCommon.LadderType ladderType) {
		return LadderManager.getLadderMap(ladderType).get(key);
	}

	public static Ladder getLadder(int key, ArenaCommon.LadderType ladderType) {
		String ladderName = LadderManager.ladderIdToLadderNameMap.get(key);
		return LadderManager.getLadder(ladderName, ladderType);
	}


	public static void joinLadderQueue(Group group, Player player, String kitRuleSetName, ArenaCommon.LadderType ladderType) {
		if (!ArenaLobby.getInstance().isAllowRankedMatches()) {
			player.sendFormattedMessage("{0}Server restart soon.  Wait until after restart to play ranked matches.", ChatColor.RED);
			return;
		}

		if(GroupStateMachine.matchMakingState.contains(group)){
			player.sendFormattedMessage("{0}You are already in a queue.", ChatColor.RED);
			return;
		}

		Ladder ladder = LadderManager.getLadder(kitRuleSetName, ladderType);

		if (ladder == null) {
			player.sendFormattedMessage("{0}Invalid ladder specified.", ChatColor.RED);
			return;
		}

		ladder.addGroup(group);
	}

	public static void joinEventQueue(Group group, Player player, ArenaCommon.EventType eventType){
		if (!ArenaLobby.getInstance().isAllowRankedMatches()) {
			player.sendFormattedMessage("{0}Server restart soon.  Wait until after restart to play ranked matches.", ChatColor.RED);
			return;
		}

		switch (eventType){
			case UHCMEETUP:
				LadderManager.uhcMeetupQueue.addGroup(group);
		}
	}

	public static void registerActiveRulesetLadders() {
		LadderManager.uhcMeetupQueue = new EventQueueService(ArenaCommon.EventType.UHCMEETUP, KitRuleSet.buildUHCRuleSet);

		// Only register unranked archer since thats the only arena we have for testing
		LadderManager.registerLadder(KitRuleSet.ARCHER_LADDER_NAME, new MatchLadder(KitRuleSet.archerRuleSet.getId(), KitRuleSet.archerRuleSet, new QueueService(), ArenaCommon.LadderType.UNRANKED_1V1, false, false));
		LadderManager.registerLadder(KitRuleSet.BUILD_UHC_LADDER_NAME, new MatchLadder(KitRuleSet.buildUHCRuleSet.getId(), KitRuleSet.buildUHCRuleSet, new QueueService(), ArenaCommon.LadderType.UNRANKED_1V1, false, false));
		LadderManager.registerLadder(KitRuleSet.DIAMOND_LADDER_NAME, new MatchLadder(KitRuleSet.DIAMOND_OCN_LADDER_ID, KitRuleSet.diamondOCNRuleSet, new QueueService(), ArenaCommon.LadderType.UNRANKED_1V1, false, false));
		LadderManager.registerLadder(KitRuleSet.GAPPLE_LADDER_NAME, new MatchLadder(KitRuleSet.godAppleRuleSet.getId(), KitRuleSet.godAppleRuleSet, new QueueService(), ArenaCommon.LadderType.UNRANKED_1V1, false, false));
		LadderManager.registerLadder(KitRuleSet.IRON_LADDER_NAME, new MatchLadder(KitRuleSet.IRON_OCN_LADDER_ID, KitRuleSet.ironOCNRuleSet, new QueueService(), ArenaCommon.LadderType.UNRANKED_1V1, false, false));
		LadderManager.registerLadder(KitRuleSet.IRON_SOUP_LADDER_NAME, new MatchLadder(KitRuleSet.ironSoupRuleSet.getId(), KitRuleSet.ironSoupRuleSet, new QueueService(), ArenaCommon.LadderType.UNRANKED_1V1, false, false));
		LadderManager.registerLadder(KitRuleSet.KOHI_LADDER_NAME, new MatchLadder(KitRuleSet.kohiRuleSet.getId(), KitRuleSet.kohiRuleSet, new QueueService(), ArenaCommon.LadderType.UNRANKED_1V1, false, false));
		LadderManager.registerLadder(KitRuleSet.NODEBUFF_LADDER_NAME, new MatchLadder(KitRuleSet.NO_DEBUFF_LADDER_ID, KitRuleSet.noDebuffRuleSet, new QueueService(), ArenaCommon.LadderType.UNRANKED_1V1, false, false));
		LadderManager.registerLadder(KitRuleSet.SG_LADDER_NAME, new MatchLadder(KitRuleSet.sgRuleSet.getId(), KitRuleSet.sgRuleSet, new QueueService(), ArenaCommon.LadderType.UNRANKED_1V1, false, false));
		LadderManager.registerLadder(KitRuleSet.UHC_LADDER_NAME, new MatchLadder(KitRuleSet.UHC_LADDER_ID, KitRuleSet.uhcRuleSet, new QueueService(), ArenaCommon.LadderType.UNRANKED_1V1, false, false));
		LadderManager.registerLadder(KitRuleSet.VANILLA_LADDER_NAME, new MatchLadder(KitRuleSet.VANILLA_LADDER_ID, KitRuleSet.vanillaRuleSet, new QueueService(), ArenaCommon.LadderType.UNRANKED_1V1, false, false));
		LadderManager.registerLadder(KitRuleSet.HORSE_LADDER_NAME, new MatchLadder(KitRuleSet.HORSE_LADDER_ID, KitRuleSet.horseRuleSet, new QueueService(), ArenaCommon.LadderType.UNRANKED_1V1, false, false));
		LadderManager.registerLadder(KitRuleSet.COMBO_LADDER_NAME, new MatchLadder(KitRuleSet.COMBO_LADDER_ID, KitRuleSet.comboRuleSet, new QueueService(), ArenaCommon.LadderType.UNRANKED_1V1, false, false));
		LadderManager.registerLadder(KitRuleSet.AXE_PVP_LADDER_NAME, new MatchLadder(KitRuleSet.AXE_PVP_LADDER_ID, KitRuleSet.axePvPRuleSet, new QueueService(), ArenaCommon.LadderType.UNRANKED_1V1, false, false));


		LadderManager.registerLadder(KitRuleSet.ARCHER_LADDER_NAME, new MatchLadder(KitRuleSet.archerRuleSet.getId(), KitRuleSet.archerRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_1V1, true, true));
		LadderManager.registerLadder(KitRuleSet.ARCHER_LADDER_NAME, new MatchLadder(KitRuleSet.archerRuleSet.getId(), KitRuleSet.archerRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_2V2, true, true));
		LadderManager.registerLadder(KitRuleSet.ARCHER_LADDER_NAME, new MatchLadder(KitRuleSet.archerRuleSet.getId(), KitRuleSet.archerRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_5V5_CLAN, true, true));

		// BuildUHCRuleSet
		LadderManager.registerLadder(KitRuleSet.BUILD_UHC_LADDER_NAME, new MatchLadder(KitRuleSet.buildUHCRuleSet.getId(), KitRuleSet.buildUHCRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_1V1, true, true));
		LadderManager.registerLadder(KitRuleSet.BUILD_UHC_LADDER_NAME, new MatchLadder(KitRuleSet.buildUHCRuleSet.getId(), KitRuleSet.buildUHCRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_2V2, true, true));
		LadderManager.registerLadder(KitRuleSet.BUILD_UHC_LADDER_NAME, new MatchLadder(KitRuleSet.buildUHCRuleSet.getId(), KitRuleSet.buildUHCRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_3V3, true, true));

		// DiamondOCNRuleSet
		LadderManager.registerLadder(KitRuleSet.DIAMOND_LADDER_NAME, new MatchLadder(KitRuleSet.DIAMOND_OCN_LADDER_ID, KitRuleSet.diamondOCNRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_1V1, true, true));
		LadderManager.registerLadder(KitRuleSet.DIAMOND_LADDER_NAME, new MatchLadder(KitRuleSet.DIAMOND_OCN_LADDER_ID, KitRuleSet.diamondOCNRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_2V2, true, true));

		// GodAppleRuleSet
		LadderManager.registerLadder(KitRuleSet.GAPPLE_LADDER_NAME, new MatchLadder(KitRuleSet.GAPPLE_LADDER_ID, KitRuleSet.godAppleRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_1V1, true, true));
		LadderManager.registerLadder(KitRuleSet.GAPPLE_LADDER_NAME, new MatchLadder(KitRuleSet.GAPPLE_LADDER_ID, KitRuleSet.godAppleRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_2V2, true, true));

		// IronOCNRuleSet.java
		LadderManager.registerLadder(KitRuleSet.IRON_LADDER_NAME, new MatchLadder(KitRuleSet.IRON_OCN_LADDER_ID, KitRuleSet.ironOCNRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_1V1, true, true));
		LadderManager.registerLadder(KitRuleSet.IRON_LADDER_NAME, new MatchLadder(KitRuleSet.IRON_OCN_LADDER_ID, KitRuleSet.ironOCNRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_2V2, true, true));

		// IronSoupRuleSet.java
		LadderManager.registerLadder(KitRuleSet.IRON_SOUP_LADDER_NAME, new MatchLadder(KitRuleSet.IRON_SOUP_LADDER_ID, KitRuleSet.ironSoupRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_1V1, true, true));
		LadderManager.registerLadder(KitRuleSet.IRON_SOUP_LADDER_NAME, new MatchLadder(KitRuleSet.IRON_SOUP_LADDER_ID, KitRuleSet.ironSoupRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_2V2, true, true));

		// KohiRuleSet.java
		LadderManager.registerLadder(KitRuleSet.KOHI_LADDER_NAME, new MatchLadder(KitRuleSet.KOHI_LADDER_ID, KitRuleSet.kohiRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_1V1, true, true));
		LadderManager.registerLadder(KitRuleSet.KOHI_LADDER_NAME, new MatchLadder(KitRuleSet.KOHI_LADDER_ID, KitRuleSet.kohiRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_2V2, true, true));
		LadderManager.registerLadder(KitRuleSet.KOHI_LADDER_NAME, new MatchLadder(KitRuleSet.KOHI_LADDER_ID, KitRuleSet.kohiRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_3V3, true, true));

		// NoDebuffRuleSet.java
		LadderManager.registerLadder(KitRuleSet.NODEBUFF_LADDER_NAME, new MatchLadder(KitRuleSet.NO_DEBUFF_LADDER_ID, KitRuleSet.noDebuffRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_1V1, true, true));
		LadderManager.registerLadder(KitRuleSet.NODEBUFF_LADDER_NAME, new MatchLadder(KitRuleSet.NO_DEBUFF_LADDER_ID, KitRuleSet.noDebuffRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_2V2, true, true));

		// SGRuleSet.java
		LadderManager.registerLadder(KitRuleSet.SG_LADDER_NAME, new MatchLadder(KitRuleSet.SG_LADDER_ID, KitRuleSet.sgRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_1V1, true, true));
		LadderManager.registerLadder(KitRuleSet.SG_LADDER_NAME, new MatchLadder(KitRuleSet.SG_LADDER_ID, KitRuleSet.sgRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_2V2, true, true));
		LadderManager.registerLadder(KitRuleSet.SG_LADDER_NAME, new MatchLadder(KitRuleSet.SG_LADDER_ID, KitRuleSet.sgRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_3V3, true, true));

		// UHCRuleSet.java
		LadderManager.registerLadder(KitRuleSet.UHC_LADDER_NAME, new MatchLadder(KitRuleSet.UHC_LADDER_ID, KitRuleSet.uhcRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_1V1, true, true));
		LadderManager.registerLadder(KitRuleSet.UHC_LADDER_NAME, new MatchLadder(KitRuleSet.UHC_LADDER_ID, KitRuleSet.uhcRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_2V2, true, true));
		LadderManager.registerLadder(KitRuleSet.UHC_LADDER_NAME, new MatchLadder(KitRuleSet.UHC_LADDER_ID, KitRuleSet.uhcRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_3V3, true, true));

		// VanillaRuleSet.java
		LadderManager.registerLadder(KitRuleSet.VANILLA_LADDER_NAME, new MatchLadder(KitRuleSet.VANILLA_LADDER_ID, KitRuleSet.vanillaRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_1V1, true, true));
		LadderManager.registerLadder(KitRuleSet.VANILLA_LADDER_NAME, new MatchLadder(KitRuleSet.VANILLA_LADDER_ID, KitRuleSet.vanillaRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_2V2, true, true));

		// HorseRuleset
		LadderManager.registerLadder(KitRuleSet.HORSE_LADDER_NAME, new MatchLadder(KitRuleSet.HORSE_LADDER_ID, KitRuleSet.horseRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_1V1, true, true));
		LadderManager.registerLadder(KitRuleSet.HORSE_LADDER_NAME, new MatchLadder(KitRuleSet.HORSE_LADDER_ID, KitRuleSet.horseRuleSet, new QueueService(), ArenaCommon.LadderType.RANKED_2V2, true, true));
	}
}
