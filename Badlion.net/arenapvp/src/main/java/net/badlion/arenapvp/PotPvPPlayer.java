package net.badlion.arenapvp;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class PotPvPPlayer {

	private List<String> debug = new ArrayList<>();

	private boolean kitsLoaded = false;
	private boolean selectingKit = false;
	private boolean settingsLoaded = false;

	public void addDebug(String str) {
		this.debug.add(str);
	}

	public void printDebug() {
		for (String str : this.debug) {
			Bukkit.getLogger().info(str);
		}
	}

	public boolean isLoaded() {
		return this.kitsLoaded;
	}

	public void setKitsLoaded(boolean kitsLoaded) {
		this.kitsLoaded = kitsLoaded;
	}

	public void setSelectingKit(boolean selectingKit) {
		this.selectingKit = selectingKit;
	}

	public void setSettingsLoaded(boolean settingsLoaded) {
		this.settingsLoaded = settingsLoaded;
	}

	public boolean isSelectingKit() {
		return this.selectingKit;
	}

}
