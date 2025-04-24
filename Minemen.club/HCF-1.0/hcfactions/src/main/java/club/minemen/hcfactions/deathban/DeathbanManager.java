package club.minemen.hcfactions.deathban;

import club.minemen.core.util.Config;
import club.minemen.core.util.finalutil.CC;
import club.minemen.hcfactions.HCFactions;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.configuration.MemorySection;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @since 12/3/2017
 */
public class DeathbanManager {

	private final Map<UUID, Timestamp> deathBansMap = new HashMap<>();
	private final Map<UUID, Integer> livesMap = new HashMap<>();

	private final HCFactions plugin;

	private final Config deathsConfig;
	private final Config livesConfig;

	public DeathbanManager(HCFactions plugin) {
		this.plugin = plugin;
		this.deathsConfig = new Config("deathbans", this.plugin);
		this.livesConfig = new Config("lives", this.plugin);
	}

	public void loadData() {
		try {
			MemorySection memorySection = (MemorySection) this.livesConfig.getConfig().get("lives");
			for (String key : memorySection.getKeys(false)) {
				this.livesMap.put(UUID.fromString(key), this.livesConfig.getConfig().getInt("lives." + key));
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.plugin.getLogger().severe("There was an issue while load lives...");
		}

		try {
			MemorySection memorySection = (MemorySection) this.deathsConfig.getConfig().get("deaths");
			for (String key : memorySection.getKeys(false)) {
				this.deathBansMap.put(UUID.fromString(key), new Timestamp(this.deathsConfig.getConfig().getLong("deaths." + key)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.plugin.getLogger().severe("There was an issue while load lives...");
		}

		this.plugin.getLogger().info("Loaded Deathban data!");
	}

	public void saveData() {
		try {
			Map<String, Integer> livesSaveMap = new LinkedHashMap<>(this.livesMap.size());
			this.livesMap.forEach((key, value) -> livesSaveMap.put(key.toString(), value));
			this.livesConfig.getConfig().set("lives", livesSaveMap);
			this.livesConfig.save();
		} catch (Exception e) {
			e.printStackTrace();
			this.plugin.getLogger().severe("There was an issue while saving lives...");
		}

		try {
			Map<String, Long> deathBansSaveMap = new LinkedHashMap<>(this.deathBansMap.size());
			this.deathBansMap.forEach((key, value) -> deathBansSaveMap.put(key.toString(), value.getTime()));
			this.deathsConfig.getConfig().set("deaths", deathBansSaveMap);
			this.deathsConfig.save();
		} catch (Exception e) {
			e.printStackTrace();
			this.plugin.getLogger().severe("There was an issue while saving deathbans...");
		}
		this.plugin.getLogger().info("Saved Deathban data!");
	}

	public Timestamp getDeathbanTime(UUID uuid) {
		return this.deathBansMap.get(uuid);
	}

	public boolean isDeathBanned(UUID uuid) {
		Timestamp timestamp = this.getDeathbanTime(uuid);

		if (timestamp == null) {
			return false;
		}

		if (timestamp.getTime() <= System.currentTimeMillis()) {
			this.deathBansMap.remove(uuid);
			return false;
		}

		return true;
	}

	public void deathbanPlayer(UUID uuid, long banDuration) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis() + banDuration);
		this.deathBansMap.put(uuid, timestamp);

		new BukkitRunnable() {
			@Override
			public void run() {
				saveData();
			}
		}.runTaskAsynchronously(this.plugin);
	}

	public void unDeathbanPlayer(UUID uuid) {
		Timestamp playerTimestamp = this.deathBansMap.remove(uuid);
		if (playerTimestamp == null) {
			return;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				saveData();
			}
		}.runTaskAsynchronously(this.plugin);
	}

	public String getDeathbanMessage(UUID playerId, boolean hasLives, int lives) {
		String message = CC.PRIMARY + "You are currently deathbanned for " + DurationFormatUtils.formatDurationWords(this.getDeathbanTime(playerId).getTime() - System.currentTimeMillis(), true, true) + ".\n";
		message += CC.PRIMARY + "You currently have " + CC.SECONDARY + lives + CC.PRIMARY + " live" + (lives == 1 ? "" : "s") + " left.\n";
		if (hasLives) {
			message += CC.PRIMARY + "Reconnect to use a life!\n";
			message += CC.PRIMARY + "You can purchase more lives at " + CC.SECONDARY + "http://store.minemen.club";
		} else {
			message += CC.PRIMARY + "You can purchase lives at " + CC.SECONDARY + "http://store.minemen.club";
		}

		return message;
	}

	public int getLives(UUID uuid) {
		Integer lives = this.livesMap.get(uuid);
		if (lives == null) {
			return 0;
		}

		return lives;
	}

	public void addLives(UUID uuid, int lives) {
		this.livesMap.put(uuid, this.getLives(uuid) + lives);

		new BukkitRunnable() {
			@Override
			public void run() {
				saveData();
			}
		}.runTaskAsynchronously(this.plugin);
	}

	public void removeLives(UUID uuid, int lives) {
		this.livesMap.put(uuid, this.getLives(uuid) - lives);

		new BukkitRunnable() {
			@Override
			public void run() {
				saveData();
			}
		}.runTaskAsynchronously(this.plugin);
	}


}

