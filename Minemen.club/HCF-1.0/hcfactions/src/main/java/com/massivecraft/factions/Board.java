package com.massivecraft.factions;

import club.minemen.hcfactions.HCFactions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.AsciiCompass;
import com.massivecraft.factions.zcore.util.DiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Board {

	private static final transient Gson gson = new GsonBuilder().registerTypeAdapter(
			new TypeToken<List<StringPair>>() {
			}.getType(),
			new TypeAdapter<List<StringPair>>() {
				@Override
				public void write(JsonWriter writer, List<StringPair> entries) throws IOException {
					writer.beginObject();
					for (StringPair entry : entries) {
						writer.name(entry.a).value(entry.b);
					}
					writer.endObject();
				}

				@Override
				public List<StringPair> read(JsonReader reader) throws IOException {
					return null;
				}
			}).create();
	private static final transient File file = new File(HCFactions.getInstance().getDataFolder(), "board.json");
	private static transient HashMap<FLocation, String> flocationIds = new HashMap<FLocation, String>();

	// ----------------------------------------------//
	// Get and Set
	// ----------------------------------------------//
	public static String getIdAt(FLocation flocation) {
		if (!flocationIds.containsKey(flocation)) {
			if (Conf.worldLandDefault.containsKey(flocation.getWorldName())) {
				return Conf.worldLandDefault.get(flocation.getWorldName());
			}
			if (Conf.warzoneOutside.containsKey(flocation.getWorldName())) {
				int outside = Conf.warzoneOutside.get(flocation.getWorldName());
				if (flocation.getX() < -outside || flocation.getX() >= outside || flocation.getZ() < -outside || flocation.getZ() >= outside) {
					return "-2";
				}
			}
			return "0";
		}

		return flocationIds.get(flocation);
	}

	// Get actual claimed warzone area, ignore the Conf.warzoneOutisde chunks, this is slow
	public static List<FLocation> getWarzoneClaimedLand() {
		List<FLocation> warzone = new ArrayList<>();
		Iterator<Entry<FLocation, String>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<FLocation, String> entry = iter.next();
			if (entry.getValue().equals("-2")) {
				warzone.add(entry.getKey());
			}
		}
		return warzone;
	}

	// is location a "real" warzone claim, not a warzoneOutside or worldLandDefault claim
	public static boolean isRealWarzone(FLocation flocation) {
		return "-2".equals(flocationIds.get(flocation));
	}

	public static Faction getFactionAt(FLocation flocation) {
		return Factions.getInstance().getById(getIdAt(flocation));
	}

	public static void setIdAt(String id, FLocation flocation) {
		Faction faction = getFactionAt(flocation);
		if (faction != null) {
			// Dont give balance back if they getAll overclaimed, since only admins can overclaim
			if (faction.getAmountPaidForLand().containsKey(flocation)) {
				faction.getAmountPaidForLand().remove(flocation);
			}
			faction.alterLandCount(-1);
		}
		clearOwnershipAt(flocation);

		if (id.equals("0")) {
			removeAt(flocation);
		}

		flocationIds.put(flocation, id);
		Factions.getInstance().getById(id).alterLandCount(1);
	}

	public static void setFactionAt(Faction faction, FLocation flocation, int amountPaid) {
		if (amountPaid != 0 && faction.isNormal()) {
			faction.getAmountPaidForLand().put(flocation, amountPaid);
		}
		setIdAt(faction.getId(), flocation);
	}

	public static void removeAt(FLocation flocation) {
		clearOwnershipAt(flocation);
		Faction faction = getFactionAt(flocation);
		if (faction != null) {
			faction.alterLandCount(-1);
		}
		flocationIds.remove(flocation);
	}

	// not to be confused with claims, ownership referring to further member-specific ownership of a claim
	public static void clearOwnershipAt(FLocation flocation) {
		Faction faction = getFactionAt(flocation);
		if (faction != null && faction.isNormal()) {
			faction.clearClaimOwnership(flocation);
		}
	}

	public static void unclaimAll(String factionId) {
		Faction faction = Factions.getInstance().getById(factionId);
		if (faction != null && faction.isNormal()) {
			faction.clearAllClaimOwnership();
			faction.setLandCount(0);
		}

		Iterator<Entry<FLocation, String>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<FLocation, String> entry = iter.next();
			if (entry.getValue().equals(factionId)) {
				iter.remove();
			}
		}
	}

	// Is this coord NOT completely surrounded by coords claimed by the same faction?
	// Simpler: Is there any nearby coord with a faction other than the faction here?
	public static boolean isBorderLocation(FLocation flocation) {
		Faction faction = getFactionAt(flocation);
		FLocation a = flocation.getRelative(1, 0);
		FLocation b = flocation.getRelative(-1, 0);
		FLocation c = flocation.getRelative(0, 1);
		FLocation d = flocation.getRelative(0, -1);
		return faction != getFactionAt(a) || faction != getFactionAt(b) || faction != getFactionAt(c) || faction != getFactionAt(d);
	}

	// Is this coord connected to any coord claimed by the specified faction?
	public static boolean isConnectedLocation(FLocation flocation, Faction faction) {
		FLocation a = flocation.getRelative(1, 0);
		FLocation b = flocation.getRelative(-1, 0);
		FLocation c = flocation.getRelative(0, 1);
		FLocation d = flocation.getRelative(0, -1);
		return faction == getFactionAt(a) || faction == getFactionAt(b) || faction == getFactionAt(c) || faction == getFactionAt(d);
	}

	public static boolean hasSafeSpot(FLocation fLocation) {
		Chunk chunk = fLocation.getWorld().getChunkAt((int) fLocation.getX(), (int) fLocation.getZ());
		int groundLvl = 70;
		Location closest = null;
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				Location chunkLoc = new Location(chunk.getWorld(), (chunk.getX() << 4) + x, 0, (chunk.getZ() << 4) + z);
				Location highestChunkLoc = chunk.getWorld().getHighestBlockAt(chunkLoc).getLocation();
				double distance = Math.abs(highestChunkLoc.getY() - groundLvl);
				if (highestChunkLoc.clone().add(0, -1, 0).getBlock().getType().isSolid()) {
					if (closest != null) {
						if (Math.abs(closest.getY() - groundLvl) > distance) {
							closest = highestChunkLoc;
						}
					} else {
						closest = highestChunkLoc;
					}
				}
			}
		}
		return closest != null;
	}

	public static FLocation getClosestNonClaimed(FLocation fLocation) {
		int x = 0;
		int z = 0;
		//Loop through 50 chunks
		for (int i = 0; i < 50; i++) {
			FLocation relative = fLocation.getRelative(x, z);
			if (getFactionAt(relative).isNone() || getFactionAt(relative).isWarZone()) {
				if (hasSafeSpot(relative)) {
					return relative;
				}
			}
			if (x < z) {
				if (-1 * x < z) {
					x++;
					continue;
				}
				z++;
				continue;
			}
			if (x > z) {
				if (-1 * x >= z) {
					x--;
					continue;
				}
				z--;
				continue;
			}
			if (x <= 0) {
				z++;
				continue;
			}
			z--;
		}
		return null;
	}

	// ----------------------------------------------//
	// Cleaner. Remove orphaned foreign keys
	// ----------------------------------------------//
	public static void clean() {
		Iterator<Entry<FLocation, String>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<FLocation, String> entry = iter.next();
			if (!Factions.getInstance().exists(entry.getValue())) {
				HCFactions.getInstance().log("Board cleaner removed " + entry.getValue() + " from " + entry.getKey());
				iter.remove();
			}
		}
	}

	// ----------------------------------------------//
	// Coord count
	// ----------------------------------------------//
	public static List<FLocation> getFactionClaims(String factionId) {
		List<FLocation> ret = new ArrayList<>();
		for (Entry<FLocation, String> thatFactionClaim : flocationIds.entrySet()) {
			if (thatFactionClaim.getValue().equals(factionId)) {
				ret.add(thatFactionClaim.getKey());
			}
		}
		return ret;
	}

	public static int getFactionCoordCount(String factionId) {
		int ret = 0;
		for (String thatFactionId : flocationIds.values()) {
			if (thatFactionId.equals(factionId)) {
				ret += 1;
			}
		}
		return ret;
	}

	public static int getFactionCoordCount(Faction faction) {
		return getFactionCoordCount(faction.getId());
	}

	public static int getFactionCoordCountInWorld(Faction faction, String worldName) {
		String factionId = faction.getId();
		int ret = 0;
		Iterator<Entry<FLocation, String>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<FLocation, String> entry = iter.next();
			if (entry.getValue().equals(factionId) && entry.getKey().getWorldName().equals(worldName)) {
				ret += 1;
			}
		}
		return ret;
	}

	/**
	 * The map is relative to a coord and a faction north is in the direction of
	 * decreasing x east is in the direction of decreasing z
	 */
	public static ArrayList<String> getMap(Faction faction, FLocation flocation, double inDegrees) {
		ArrayList<String> ret = new ArrayList<String>();
		Faction factionLoc = getFactionAt(flocation);
		ret.add(HCFactions.getInstance().txt.titleize("(" + flocation.getCoordString() + ") " + factionLoc.getTag(faction)));

		int halfWidth = Conf.mapWidth / 2;
		int halfHeight = Conf.mapHeight / 2;
		FLocation topLeft = flocation.getRelative(-halfWidth, -halfHeight);
		int width = halfWidth * 2 + 1;
		int height = halfHeight * 2 + 1;

		if (Conf.showMapFactionKey) {
			height--;
		}

		Map<String, Character> fList = new HashMap<String, Character>();
		int chrIdx = 0;

		// For each row
		for (int dz = 0; dz < height; dz++) {
			// Draw and add that row
			String row = "";
			for (int dx = 0; dx < width; dx++) {
				if (dx == halfWidth && dz == halfHeight) {
					row += ChatColor.AQUA + "+";
				} else {
					FLocation flocationHere = topLeft.getRelative(dx, dz);
					Faction factionHere = getFactionAt(flocationHere);
					Relation relation = faction.getRelationTo(factionHere);
					if (factionHere.isNone()) {
						row += ChatColor.GRAY + "-";
					} else if (factionHere.isSafeZone()) {
						row += Conf.colorPeaceful + "+";
					} else if (factionHere.isWarZone()) {
						row += ChatColor.DARK_RED + "+";
					} else if (factionHere == faction || factionHere == factionLoc || relation.isAtLeast(Relation.ALLY) || (Conf.showNeutralFactionsOnMap && relation.equals(Relation.NEUTRAL))) {
						if (!fList.containsKey(factionHere.getTag())) {
							fList.put(factionHere.getTag(), Conf.mapKeyChrs[chrIdx++]);
						}
						char tag = fList.get(factionHere.getTag());
						row += factionHere.getColorTo(faction) + "" + tag;
					} else {
						row += ChatColor.GRAY + "-";
					}
				}
			}
			ret.add(row);
		}

		// Get the compass
		ArrayList<String> asciiCompass = AsciiCompass.getAsciiCompass(inDegrees, ChatColor.RED, HCFactions.getInstance().txt.parse("<a>"));

		// Add the compass
		ret.set(1, asciiCompass.get(0) + ret.get(1).substring(3 * 3));
		ret.set(2, asciiCompass.get(1) + ret.get(2).substring(3 * 3));
		ret.set(3, asciiCompass.get(2) + ret.get(3).substring(3 * 3));

		// Add the faction key
		if (Conf.showMapFactionKey) {
			String fRow = "";
			for (String key : fList.keySet()) {
				fRow += String.format("%s%s: %s ", ChatColor.GRAY, fList.get(key), key);
			}
			ret.add(fRow);
		}

		return ret;
	}

	// ----------------------------------------------//
	// Map generation
	// ----------------------------------------------//

	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	public static Map<String, List<StringPair>> dumpAsSaveFormat() {
		long start = System.currentTimeMillis();
		Map<String, List<StringPair>> worldCoordIds = new HashMap<>();

		String worldName, coords;
		String id;

		for (Entry<FLocation, String> entry : flocationIds.entrySet()) {
			worldName = entry.getKey().getWorldName();
			List<StringPair> list = worldCoordIds.get(worldName);
			if (list == null) {
				list = new ArrayList<>(flocationIds.size() / 2);
				worldCoordIds.put(worldName, list);
			}
			list.add(new StringPair(entry.getKey().getCoordString(), entry.getValue()));
		}

		HCFactions.getInstance().getLogger().info("Board.dumpAsSaveFormat " + (System.currentTimeMillis() - start) + "ms");

		return worldCoordIds;
	}

	public static void loadFromSaveFormat(Map<String, Map<String, String>> worldCoordIds) {
		flocationIds.clear();
		for (Faction faction : Factions.getInstance().getAll()) {
			faction.setLandCount(0);
		}

		String worldName;
		String[] coords;
		int x, z;
		String factionId;

		for (Entry<String, Map<String, String>> entry : worldCoordIds.entrySet()) {
			worldName = entry.getKey();
			for (Entry<String, String> entry2 : entry.getValue().entrySet()) {
				coords = entry2.getKey().trim().split("[,\\s]+");
				x = Integer.parseInt(coords[0]);
				z = Integer.parseInt(coords[1]);
				factionId = entry2.getValue();
				flocationIds.put(new FLocation(worldName, x, z), factionId);
				Faction faction = Factions.getInstance().getById(factionId);
				if (faction != null) {
					faction.alterLandCount(1);
				}
			}
		}
	}

	public static boolean save() {
		final Map<String, List<StringPair>> dump = dumpAsSaveFormat();

		if (!HCFactions.getInstance().isEnabled()) {
			// cant do async tasks while disabling
			save(dump);
			return true;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				synchronized (file) {
					save(dump);
				}
			}
		}.runTaskAsynchronously(HCFactions.getInstance());

		return true;
	}

	private static void save(Map<String, List<StringPair>> dump) {
		final long start = System.currentTimeMillis();
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file), 0x40000)) {
			gson.toJson(dump, new TypeToken<Map<String, List<StringPair>>>() {
			}.getType(), writer);
		} catch (Exception e) {
			e.printStackTrace();
			HCFactions.getInstance().log("Failed to save the board to disk.");
		}
		HCFactions.getInstance().getLogger().info("Board.save " + (System.currentTimeMillis() - start) + "ms");
	}

	public static boolean load() {
		HCFactions.getInstance().log("Loading board from disk");

		if (!file.exists()) {
			HCFactions.getInstance().log("No board to load from disk. Creating new file.");
			save();
			return true;
		}

		try {
			Type type = new TypeToken<Map<String, Map<String, String>>>() {
			}.getType();
			Map<String, Map<String, String>> worldCoordIds = HCFactions.getInstance().gson.fromJson(DiscUtil.read(file), type);
			loadFromSaveFormat(worldCoordIds);
		} catch (Exception e) {
			e.printStackTrace();
			HCFactions.getInstance().log("Failed to load the board from disk.");
			return false;
		}

		return true;
	}

	// serializer shit #blameprplz
	private static class StringPair {
		public final String a;
		public final String b;

		public StringPair(String a, String b) {
			this.a = a;
			this.b = b;
		}
	}
}
