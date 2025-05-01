package net.badlion.potpvp;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class PotPvPPlayer {

	private List<String> debug = new ArrayList<>();

    private boolean ratingsLoaded = false;
    private boolean kitsLoaded = false;
    private boolean rankedLeftLoaded = false;
    private boolean ffaLoaded = false;
    private boolean tdmLoaded = false;

	public void addDebug(String str) {
		this.debug.add(str);
	}

	public void printDebug() {
		for (String str : this.debug) {
			Bukkit.getLogger().info(str);
		}
	}

    public boolean isLoaded() {
        return this.ratingsLoaded && this.kitsLoaded && this.rankedLeftLoaded && this.ffaLoaded && this.tdmLoaded;
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

	public void setFFALoaded(boolean ffaLoaded) {
		this.ffaLoaded = ffaLoaded;
	}

	public void setTDMLoaded(boolean tdmLoaded) {
		this.tdmLoaded = tdmLoaded;
	}

}
