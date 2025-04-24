package com.massivecraft.factions;

import club.minemen.hcfactions.HCFactions;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.zcore.persist.PlayerEntityCollection;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class FPlayers extends PlayerEntityCollection<FactionPlayer> {

	@Getter private static final FPlayers instance = new FPlayers();

	private final Map<String, FactionPlayer> nameMap = new HashMap<>();

	private final HCFactions plugin = HCFactions.getInstance();

	private FPlayers() {
		super(FactionPlayer.class, new CopyOnWriteArrayList<>(), new ConcurrentHashMap<>(), new File(HCFactions.getInstance().getDataFolder(), "players.json"), HCFactions.getInstance().gson);

		this.setCreative(true);
	}

	@Override
	public Type getMapType() {
		return new TypeToken<Map<String, FactionPlayer>>() {
		}.getType();
	}

	public void clean() {
		for (FactionPlayer fplayer : this.getAll()) {
			if (!Factions.getInstance().exists(fplayer.getFactionId())) {
				this.plugin.log("Reset faction data (invalid faction) for player " + fplayer.getName());
				fplayer.resetFactionData();
			}
		}
	}

	public void login(Player player) {
		String id = player.getUniqueId().toString();
		FactionPlayer fplayer;
		if (this.entityIdMap.containsKey(id)) {
			fplayer = (FactionPlayer) this.entityIdMap.get(id);
		} else {
			fplayer = this.create(id);
			FactionPlayer invalidName = this.nameMap.get(player.getName().toLowerCase());
			if (invalidName != null) {
				invalidName.setName(null);
			}
		}
		fplayer.setPlayer(player);
		fplayer.setName(player.getName());
		this.nameMap.put(player.getName().toLowerCase(), fplayer);
	}

	@Override
	public boolean loadFromDisc() {
		boolean ret = super.loadFromDisc();
		this.nameMap.clear();
		for (FactionPlayer fplayer : this.getAll()) {
			if (fplayer.getName() != null) {
				this.nameMap.put(fplayer.getName().toLowerCase(), fplayer);
			}
		}
		return ret;
	}

	public FactionPlayer getByName(String name) {
		return this.nameMap.get(name.toLowerCase());
	}
}
