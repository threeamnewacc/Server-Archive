package com.massivecraft.factions;

import club.minemen.hcfactions.HCFactions;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.zcore.persist.EntityCollection;
import com.massivecraft.factions.zcore.util.TextUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class Factions extends EntityCollection<Faction> {

	@Getter private static final Factions instance = new Factions();

	private final HCFactions plugin = HCFactions.getInstance();

	private Factions() {
		super(Faction.class, new CopyOnWriteArrayList<>(), new ConcurrentHashMap<>(), new File(HCFactions.getInstance().getDataFolder(), "factions.json"), HCFactions.getInstance().gson);
	}

	// ----------------------------------------------//
	// Faction tag
	// ----------------------------------------------//
	public static ArrayList<String> validateTag(String str) {
		ArrayList<String> errors = new ArrayList<String>();

		if (MiscUtil.getComparisonString(str).length() < Conf.factionTagLengthMin) {
			errors.add(HCFactions.getInstance().txt.parse("<instance>The faction tag can't be shorter than <h>%s<instance> chars.", Conf.factionTagLengthMin));
		}

		if (str.length() > Conf.factionTagLengthMax) {
			errors.add(HCFactions.getInstance().txt.parse("<instance>The faction tag can't be longer than <h>%s<instance> chars.", Conf.factionTagLengthMax));
		}

		for (char c : str.toCharArray()) {
			if (!MiscUtil.substanceChars.contains(String.valueOf(c))) {
				errors.add(HCFactions.getInstance().txt.parse("<instance>Faction tag must be alphanumeric. \"<h>%s<instance>\" is not allowed.", c));
			}
		}

		return errors;
	}

	@Override
	public Type getMapType() {
		return new TypeToken<Map<String, Faction>>() {
		}.getType();
	}

	@Override
	public boolean loadFromDisc() {
		if (!super.loadFromDisc()) {
			return false;
		}

		// Make sure the default neutral faction exists
		if (!this.exists("0")) {
			Faction faction = this.create("0");
			faction.setTag(ChatColor.DARK_GREEN + "Wilderness");
		}

		// Make sure the safe zone faction exists
		if (!this.exists("-1")) {
			Faction faction = this.create("-1");
			faction.setTag("SafeZone");
		} else {
			// if SafeZone has old pre-1.6.0 name, rename it to remove troublesome " "
			Faction faction = this.getSafeZone();
			if (faction.getTag().contains(" ")) {
				faction.setTag("SafeZone");
			}
		}

		// Make sure the war zone faction exists
		if (!this.exists("-2")) {
			Faction faction = this.create("-2");
			faction.setTag("WarZone");
		} else {
			// if WarZone has old pre-1.6.0 name, rename it to remove troublesome " "
			Faction faction = this.getWarZone();
			if (faction.getTag().contains(" ")) {
				faction.setTag("WarZone");
			}
		}

		// populate all faction player lists
		for (Faction faction : instance.getAll()) {
			faction.refreshFPlayers();
		}

		return true;
	}

	// ----------------------------------------------//
	// GET
	// ----------------------------------------------//
	@Override
	public Faction getById(String id) {
		if (!this.exists(id)) {
			plugin.log(Level.WARNING, "Non existing factionId " + id + " requested! Issuing cleaning!");
			Board.clean();
			FPlayers.getInstance().clean();
		}

		return super.getById(id);
	}

	public Faction getNone() {
		return this.getById("0");
	}

	public Faction getSafeZone() {
		return this.getById("-1");
	}

	public Faction getWarZone() {
		return this.getById("-2");
	}

	public Faction getByTag(String str) {
		String compStr = MiscUtil.getComparisonString(str);
		for (Faction faction : this.getAll()) {
			if (faction.getComparisonTag().equals(compStr)) {
				return faction;
			}
		}
		return null;
	}

	public Faction getBestTagMatch(String searchFor) {
		Map<String, Faction> tag2faction = new HashMap<String, Faction>();

		// TODO: Slow index building
		for (Faction faction : this.getAll()) {
			tag2faction.put(ChatColor.stripColor(faction.getTag()), faction);
		}

		String tag = TextUtil.getBestStartWithCI(tag2faction.keySet(), searchFor);
		if (tag == null) {
			return null;
		}
		return tag2faction.get(tag);
	}

	public boolean isTagTaken(String str) {
		return this.getByTag(str) != null;
	}

	public Set<Faction> getWhereOnline() {
		Set<Faction> ret = new HashSet<Faction>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			FactionPlayer factionPlayer = FPlayers.getInstance().get(player);
			if (exists(factionPlayer.getFactionId())) {
				ret.add(factionPlayer.getFaction());
			}
		}
		return ret;
	}
}
