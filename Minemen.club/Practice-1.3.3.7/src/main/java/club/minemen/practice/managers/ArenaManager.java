package club.minemen.practice.managers;

import club.minemen.core.util.Config;
import club.minemen.core.util.CustomLocation;
import club.minemen.practice.Practice;
import club.minemen.practice.arena.Arena;
import club.minemen.practice.arena.StandaloneArena;
import club.minemen.practice.kit.Kit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class ArenaManager {
	private final Practice plugin = Practice.getInstance();

	private final Config config = new Config("arenas", this.plugin);

	@Getter
	private final Map<String, Arena> arenas = new HashMap<>();

	@Getter
	private final Map<StandaloneArena, UUID> arenaMatchUUIDs = new HashMap<>();

	@Getter
	@Setter
	private int generatingArenaRunnables;

	public ArenaManager() {
		this.loadArenas();
	}

	private void loadArenas() {
		FileConfiguration fileConfig = config.getConfig();
		ConfigurationSection arenaSection = fileConfig.getConfigurationSection("arenas");

		if (arenaSection == null) {
			return;
		}

		arenaSection.getKeys(false).forEach(name -> {
			String a = arenaSection.getString(name + ".a");
			String b = arenaSection.getString(name + ".b");
			String min = arenaSection.getString(name + ".min");
			String max = arenaSection.getString(name + ".max");

			CustomLocation locA = CustomLocation.stringToLocation(a);
			CustomLocation locB = CustomLocation.stringToLocation(b);
			CustomLocation locMin = CustomLocation.stringToLocation(min);
			CustomLocation locMax = CustomLocation.stringToLocation(max);

			List<StandaloneArena> standaloneArenas = new ArrayList<>();

			ConfigurationSection saSection = arenaSection.getConfigurationSection(name + ".standaloneArenas");

			if (saSection != null) {
				saSection.getKeys(false).forEach(id -> {
					String saA = saSection.getString(id + ".a");
					String saB = saSection.getString(id + ".b");
					String saMin = saSection.getString(id + ".min");
					String saMax = saSection.getString(id + ".max");

					CustomLocation locSaA = CustomLocation.stringToLocation(saA);
					CustomLocation locSaB = CustomLocation.stringToLocation(saB);
					CustomLocation locSaMin = CustomLocation.stringToLocation(saMin);
					CustomLocation locSaMax = CustomLocation.stringToLocation(saMax);

					standaloneArenas.add(new StandaloneArena(locSaA, locSaB, locSaMin, locSaMax));
				});
			}

			boolean enabled = arenaSection.getBoolean(name + ".enabled", false);

			Arena arena = new Arena(name, standaloneArenas, new ArrayList<>(standaloneArenas), locA, locB, locMin, locMax, enabled);

			this.arenas.put(name, arena);
		});
	}

	public void saveArenas() {
		FileConfiguration fileConfig = this.config.getConfig();

		fileConfig.set("arenas", null);
		arenas.forEach((arenaName, arena) -> {
			String a = CustomLocation.locationToString(arena.getA());
			String b = CustomLocation.locationToString(arena.getB());
			String min = CustomLocation.locationToString(arena.getMin());
			String max = CustomLocation.locationToString(arena.getMax());

			String arenaRoot = "arenas." + arenaName;

			fileConfig.set(arenaRoot + ".a", a);
			fileConfig.set(arenaRoot + ".b", b);
			fileConfig.set(arenaRoot + ".min", min);
			fileConfig.set(arenaRoot + ".max", max);
			fileConfig.set(arenaRoot + ".enabled", arena.isEnabled());
			fileConfig.set(arenaRoot + ".standaloneArenas", null);
			int i = 0;
			if (arena.getStandaloneArenas() != null) {
				for (StandaloneArena saArena : arena.getStandaloneArenas()) {
					String saA = CustomLocation.locationToString(saArena.getA());
					String saB = CustomLocation.locationToString(saArena.getB());
					String saMin = CustomLocation.locationToString(saArena.getMin());
					String saMax = CustomLocation.locationToString(saArena.getMax());

					String standAloneRoot = arenaRoot + ".standaloneArenas." + i;

					fileConfig.set(standAloneRoot + ".a", saA);
					fileConfig.set(standAloneRoot + ".b", saB);
					fileConfig.set(standAloneRoot + ".min", saMin);
					fileConfig.set(standAloneRoot + ".max", saMax);

					i++;
				}
			}
		});

		this.config.save();
	}

	public void createArena(String name) {
		this.arenas.put(name, new Arena(name));
	}

	public void deleteArena(String name) {
		this.arenas.remove(name);
	}

	public Arena getArena(String name) {
		return this.arenas.get(name);
	}

	public Arena getRandomArena(Kit kit) {
		List<Arena> enabledArenas = new ArrayList<>();

		for (Arena arena : this.arenas.values()) {
			if (!arena.isEnabled()) {
				continue;
			}

			if (kit.getExcludedArenas().contains(arena.getName())) {
				continue;
			}

			if (kit.getArenaWhiteList().size() > 0 && !kit.getArenaWhiteList().contains(arena.getName())) {
				continue;
			}

			enabledArenas.add(arena);
		}

		if (enabledArenas.size() == 0) {
			return null;
		}

		return enabledArenas.get(ThreadLocalRandom.current().nextInt(enabledArenas.size()));
	}

	public void removeArenaMatchUUID(StandaloneArena arena) {
		this.arenaMatchUUIDs.remove(arena);
	}

	public UUID getArenaMatchUUID(StandaloneArena arena) {
		return this.arenaMatchUUIDs.get(arena);
	}

	public void setArenaMatchUUID(StandaloneArena arena, UUID matchUUID) {
		this.arenaMatchUUIDs.put(arena, matchUUID);
	}
}
