package com.massivecraft.factions;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.type.WBScheduleItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Conf {


	public static double map = 0.5;
	public static int season = 1;

	// unclaimed lands
	public static Map<String, String> worldLandDefault = new LinkedHashMap<>();

	// claim beyond certain distance in chunks (factions)
	public static Map<String, Integer> warzoneOutside = new LinkedHashMap<>();

	// world dtr loss
	public static Map<String, Double> worldDtrLoss = new LinkedHashMap<>();

	// no pvp tag worlds
	public static Set<String> worldsNoPvpTag = new LinkedHashSet<>();

	// Colors
	public static ChatColor colorFocus = ChatColor.LIGHT_PURPLE;
	public static ChatColor colorMember = ChatColor.DARK_GREEN;
	public static ChatColor colorAlly = ChatColor.DARK_PURPLE;
	public static ChatColor colorNeutral = ChatColor.GRAY;

	public static ChatColor colorPeaceful = ChatColor.GOLD;
	public static ChatColor colorWar = ChatColor.DARK_RED;
	// public static ChatColor colorWilderness = ChatColor.DARK_GREEN;

	// DTR
	public static double dtrPerPlayer = 0.75;
	public static double dtrMax = 6.0;
	public static int dtrDeathRegenCooldown = 60; // minutes
	public static double dtrRegenRateOnline = 0.03; // dtr per minute
	public static double dtrRegenRateOffline = 0.015; // dtr per minute

	// Deathban
	public static int deathbanTime = 180; // minutes
	public static double betrayerDeathbanMultiplier = 4.0;

	public static Map<String, Integer> deathbanHandicaps = new HashMap<>();

	// PVP Protection

	public static int pvpProtectionTime = 20; // minutes
	public static boolean pvpProtectionHungerLoss = false;

	// Economy

	public static int factionSetWarpCost = 2000;
	public static int firstClaimCost = 200;
	public static int increasePerClaimCost = 50;
	public static int fHomeCost = 50;

	// Faction warps

	public static int maxFactionWarps = 3;

	// PVP
	public static int pvpTagAttack = 30;
	public static int pvpTagHurt = 30;
	public static int pvpTagEnderpearl = 5;
	public static boolean denyPvpTaggedEndPortalEnter = false;

	public static long joinFactionEventCooldown = 0; // minues after joining a faction that a player must wait until they can enter event regions
	public static long factionSlotLockTime = 0; // minutes that a 'slot' in a faction is considered occupied after a player leaves

	public static String prefixAdmin = "**";
	public static String prefixMod = "*";

	public static int factionTagLengthMin = 3;
	public static int factionTagLengthMax = 10;
	public static boolean factionTagForceUpperCase = false;

	// when faction membership hits this limit, players will no longer be able to join using /f join; default is 0, no limit
	public static int factionMemberLimit = 0;
	public static int allyLimit = 2;
	public static int allyCooldown = 1440; // minutes

	// what faction ID to start new players in when they first join the server; default is 0, "no faction"
	public static String newPlayerStartingFactionID = "0";

	public static boolean showMapFactionKey = true;
	public static boolean showNeutralFactionsOnMap = true;

	// Configuration for faction-only chat
	public static boolean factionOnlyChat = true;
	public static boolean chatTagRelationColored = true;
	public static boolean chatTagIncludeRole = false;
	public static String chatTagFormat = ChatColor.GOLD + "[%s" + ChatColor.GOLD + "]" + ChatColor.RESET;
	public static String factionChatFormat = "%s:" + ChatColor.WHITE + " %s";
	public static String allianceChatFormat = ChatColor.LIGHT_PURPLE + "%s:" + ChatColor.WHITE + " %s";

	public static double saveToFileEveryXMinutes = 30.0;

	public static double autoLeaveAfterDaysOfInactivity = 10.0;
	public static double autoLeaveRoutineRunsEveryXMinutes = 5.0;
	public static int autoLeaveRoutineMaxMillisecondsPerTick = 5; // 1 server tick is roughly 50ms, so default max 10% of a tick

	// server logging options
	public static boolean logFactionCreate = true;
	public static boolean logFactionDisband = true;
	public static boolean logFactionJoin = true;
	public static boolean logFactionKick = true;
	public static boolean logFactionLeave = true;
	public static boolean logLandClaims = true;
	public static boolean logLandUnclaims = true;
	public static boolean logPlayerCommands = true;

	// prevent some potential exploits
	public static boolean handleExploitObsidianGenerators = true;
	public static boolean handleExploitEnderPearlClipping = true;
	public static boolean handleExploitInteractionSpam = true;
	public static boolean handleExploitTNTWaterlog = false;

	public static boolean homesEnabled = true;
	public static boolean homesMustBeInClaimedTerritory = true;
	public static boolean homesTeleportToOnDeath = true;
	public static boolean homesTeleportCommandEnabled = true;
	public static boolean homesTeleportAllowedFromDifferentWorld = true;

	public static boolean permanentFactionsDisableLeaderPromotion = false;

	public static boolean claimsMustBeConnected = false;
	public static boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = true;
	public static int claimsRequireMinFactionMembers = 1;
	public static int landBorder = 2;

	// claiming amounts
	// factions allowed claims is baseClaimsAmount + memberCount * claimsPerPlayer, capped at claimsLimit
	public static int baseClaimsAmount = 2;
	public static int claimsPerPlayer = 2;
	public static int claimsLimit = 36;

	// if someone is doing a radius claim and the process fails to claim land this many times in a row, it will exit
	public static int radiusClaimFailureLimit = 9;

	public static double considerFactionsReallyOfflineAfterXMinutes = 0.0;

	public static int actionDeniedPainAmount = 1;

	// commands which will be prevented if the player is a member of a permanent faction
	public static Set<String> permanentFactionMemberDenyCommands = new LinkedHashSet<>();

	// commands which will be prevented when in claimed territory of another faction
	public static Set<String> territoryNeutralDenyCommands = new LinkedHashSet<>();

	public static boolean territoryDenyBuild = true;
	public static boolean territoryDenyBuildWhenOffline = true;
	public static boolean territoryPainBuild = false;
	public static boolean territoryPainBuildWhenOffline = false;
	public static boolean territoryDenyUseage = true;
	public static boolean territoryAllyDenyBuild = true;
	public static boolean territoryAllyDenyBuildWhenOffline = true;
	public static boolean territoryAllyPainBuild = false;
	public static boolean territoryAllyPainBuildWhenOffline = false;
	public static boolean territoryAllyDenyUseage = true;
	public static boolean territoryAllyProtectMaterials = true;
	public static boolean territoryBlockCreepers = false;
	public static boolean territoryBlockCreepersWhenOffline = false;
	public static boolean territoryBlockFireballs = false;
	public static boolean territoryBlockFireballsWhenOffline = false;
	public static boolean territoryBlockTNT = false;
	public static boolean territoryBlockTNTWhenOffline = false;
	public static boolean territoryDenyEndermanBlocks = true;
	public static boolean territoryDenyEndermanBlocksWhenOffline = true;

	public static boolean safeZoneDenyBuild = true;
	public static boolean safeZoneDenyUseage = true;
	public static boolean safeZoneBlockTNT = true;
	public static boolean safeZonePreventAllDamageToPlayers = false;
	public static boolean safeZonePreventHungerLoss = false;
	public static boolean safeZoneDenyEndermanBlocks = true;

	public static boolean warZoneDenyBuild = true;
	public static boolean warZoneDenyUseage = true;
	public static boolean warZoneBlockCreepers = false;
	public static boolean warZoneBlockFireballs = false;
	public static boolean warZoneBlockTNT = true;
	public static boolean warZoneFriendlyFire = false;
	public static boolean warZoneDenyEndermanBlocks = true;

	public static boolean wildernessDenyBuild = false;
	public static boolean wildernessDenyUseage = false;
	public static boolean wildernessBlockCreepers = false;
	public static boolean wildernessBlockFireballs = false;
	public static boolean wildernessBlockTNT = false;
	public static boolean wildernessDenyEndermanBlocks = false;

	// for claimed areas where further faction-member ownership can be defined
	public static boolean ownedAreasEnabled = true;
	public static boolean ownedAreaModeratorsBypass = true;
	public static boolean ownedAreaDenyBuild = true;
	public static boolean ownedAreaPainBuild = false;
	public static boolean ownedAreaProtectMaterials = true;
	public static boolean ownedAreaDenyUseage = true;

	public static String ownedLandMessage = "Owner(s): ";
	public static String publicLandMessage = "Public faction land.";
	public static boolean ownedMessageOnBorder = true;
	public static boolean ownedMessageInsideTerritory = true;
	public static boolean ownedMessageByChunk = false;

	public static boolean pistonProtectionThroughDenyBuild = true;

	public static Set<Material> territoryProtectedMaterials = EnumSet.noneOf(Material.class);
	public static Set<Material> territoryProtectedMaterialsPhysical = EnumSet.noneOf(Material.class);
	public static Set<Material> territoryDenyUseageMaterials = EnumSet.noneOf(Material.class);

	public static Set<String> denyCraftingOfMaterials = new LinkedHashSet<>();

	public static transient Set<EntityType> safeZoneNerfedCreatureTypes = EnumSet.noneOf(EntityType.class);

	public static Set<Material> allowBreakingInWarzoneMaterials = EnumSet.noneOf(Material.class);

	// mainly for other plugins/mods that use a fake player to take actions, which shouldn't be subject to our protections
	public static Set<String> playersWhoBypassAllProtection = new LinkedHashSet<>();

	public static Set<String> worldsNoClaiming = new LinkedHashSet<>();
	public static Set<String> worldsIgnorePvP = new LinkedHashSet<>();
	public static Set<String> worldsNoWildernessProtection = new LinkedHashSet<>();

	public static Set<WBScheduleItem> worldBorderExpandSchedule = new LinkedHashSet<>();

	public static transient int mapHeight = 8;
	public static transient int mapWidth = 39;
	public static transient char[] mapKeyChrs = "\\/#?$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz".toCharArray();
	public static boolean canHurtAllies = true;
	public static boolean allowOverclaiming = true;
	public static boolean safeZonePreventDamageToHorses = true;
	public static double expMultiplier = 2.0;
	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	private static transient Conf i = new Conf();

	static {

		territoryProtectedMaterials.add(Material.WOODEN_DOOR);
		territoryProtectedMaterials.add(Material.TRAP_DOOR);
		territoryProtectedMaterials.add(Material.FENCE_GATE);
		territoryProtectedMaterials.add(Material.DISPENSER);
		territoryProtectedMaterials.add(Material.CHEST);
		territoryProtectedMaterials.add(Material.FURNACE);
		territoryProtectedMaterials.add(Material.BURNING_FURNACE);
		territoryProtectedMaterials.add(Material.DIODE_BLOCK_OFF);
		territoryProtectedMaterials.add(Material.DIODE_BLOCK_ON);
		territoryProtectedMaterials.add(Material.JUKEBOX);
		territoryProtectedMaterials.add(Material.BREWING_STAND);
		territoryProtectedMaterials.add(Material.ENCHANTMENT_TABLE);
		territoryProtectedMaterials.add(Material.CAULDRON);
		territoryProtectedMaterials.add(Material.SOIL);
		territoryProtectedMaterials.add(Material.BEACON);
		territoryProtectedMaterials.add(Material.ANVIL);
		territoryProtectedMaterials.add(Material.TRAPPED_CHEST);
		territoryProtectedMaterials.add(Material.DROPPER);
		territoryProtectedMaterials.add(Material.HOPPER);

		territoryDenyUseageMaterials.add(Material.FIREBALL);
		territoryDenyUseageMaterials.add(Material.FLINT_AND_STEEL);
		territoryDenyUseageMaterials.add(Material.BUCKET);
		territoryDenyUseageMaterials.add(Material.WATER_BUCKET);
		territoryDenyUseageMaterials.add(Material.LAVA_BUCKET);

		safeZoneNerfedCreatureTypes.add(EntityType.BLAZE);
		safeZoneNerfedCreatureTypes.add(EntityType.CAVE_SPIDER);
		safeZoneNerfedCreatureTypes.add(EntityType.CREEPER);
		safeZoneNerfedCreatureTypes.add(EntityType.ENDER_DRAGON);
		safeZoneNerfedCreatureTypes.add(EntityType.ENDERMAN);
		safeZoneNerfedCreatureTypes.add(EntityType.GHAST);
		safeZoneNerfedCreatureTypes.add(EntityType.MAGMA_CUBE);
		safeZoneNerfedCreatureTypes.add(EntityType.PIG_ZOMBIE);
		safeZoneNerfedCreatureTypes.add(EntityType.SILVERFISH);
		safeZoneNerfedCreatureTypes.add(EntityType.SKELETON);
		safeZoneNerfedCreatureTypes.add(EntityType.SPIDER);
		safeZoneNerfedCreatureTypes.add(EntityType.SLIME);
		safeZoneNerfedCreatureTypes.add(EntityType.WITCH);
		safeZoneNerfedCreatureTypes.add(EntityType.WITHER);
		safeZoneNerfedCreatureTypes.add(EntityType.ZOMBIE);

		allowBreakingInWarzoneMaterials.add(Material.LOG);
		allowBreakingInWarzoneMaterials.add(Material.LOG_2);

		denyCraftingOfMaterials.add("ENDER_CHEST");
		denyCraftingOfMaterials.add("GOLDEN_APPLE:1");

		deathbanHandicaps.put("lion", 65);
		deathbanHandicaps.put("donator+", 40);
		deathbanHandicaps.put("donator", 25);
		deathbanHandicaps.put("basic", 10);

		worldBorderExpandSchedule.add(new WBScheduleItem(WBScheduleItem.Day.MONDAY, "14:00", 250));
		worldBorderExpandSchedule.add(new WBScheduleItem(WBScheduleItem.Day.WEDNESDAY, "14:00", 250));
		worldBorderExpandSchedule.add(new WBScheduleItem(WBScheduleItem.Day.FRIDAY, "14:00", 250));
	}

	public static void load() {
		HCFactions.getInstance().persist.loadOrSaveDefault(i, Conf.class, "conf");
	}

	public static void save() {
		HCFactions.getInstance().persist.save(i);
	}
}
