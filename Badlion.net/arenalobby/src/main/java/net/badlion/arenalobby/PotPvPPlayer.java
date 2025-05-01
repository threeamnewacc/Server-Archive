package net.badlion.arenalobby;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class PotPvPPlayer {

	private List<String> debug = new ArrayList<>();

	private boolean ratingsLoaded = false;
	private boolean rankedLeftLoaded = false;

	private boolean kitsLoaded = false;
	private boolean loadingKits = false;


	public void addDebug(String str) {
		this.debug.add(str);
	}

	public void printDebug() {
		for (String str : this.debug) {
			Bukkit.getLogger().info(str);
		}
	}

	public boolean isLoaded() {
		return this.ratingsLoaded && this.rankedLeftLoaded;
	}

	public void setRatingsLoaded(boolean ratingsLoaded) {
		this.ratingsLoaded = ratingsLoaded;
	}

	public void setKitsLoaded(boolean kitsLoaded) {
		this.kitsLoaded = kitsLoaded;
	}

	public void setRankedLeftLoaded(boolean rankedLeftLoaded) {
		this.rankedLeftLoaded = rankedLeftLoaded;
	}

	public void setLoadingKits(boolean loadingKits) {
		this.loadingKits = loadingKits;
	}

	public boolean isRankedLeftLoaded() {
		return rankedLeftLoaded;
	}

	public boolean isKitsLoaded() {
		return kitsLoaded;
	}

	public boolean isLoadingKits() {
		return loadingKits;
	}
}
