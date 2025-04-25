package cc.patrone.practice.managers;

import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.constants.ItemHotbars;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.party.PartyState;
import cc.patrone.practice.player.PlayerState;
import cc.patrone.practice.player.PracticeProfile;
import cc.patrone.practice.utils.PlayerUtil;
import cc.patrone.practice.utils.StringUtil;
import cc.patrone.practice.wrapper.EloWrapper;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class PlayerManager {
	private final Map<UUID, PracticeProfile> profiles = new HashMap<>();
	private final PracticePlugin plugin;

	public void createProfile(UUID id, String name) {
		PracticeProfile profile = new PracticeProfile(id, name, plugin);
		profiles.put(id, profile);
	}

	public PracticeProfile getProfile(UUID id) {
		return profiles.get(id);
	}

	public void removeProfile(UUID id) {
		profiles.remove(id);
	}

	public void displayStats(Player player) {
		displayStats(player, player);
	}

	public void displayStats(Player player, Player target) {
		PracticeProfile profile = getProfile(target.getUniqueId());

		player.sendMessage(CC.PRIMARY + "Stats for " + target.getName());
		player.sendMessage(CC.PRIMARY + "Wins: " + CC.SECONDARY + StringUtil.formatNumberWithCommas(profile.getWins()));
		player.sendMessage(CC.PRIMARY + "Global ELO: " + CC.SECONDARY + StringUtil.formatNumberWithCommas(profile.getGlobalElo()));

		for (Kit kit : Kit.values()) {
			EloWrapper eloWrapper = profile.getElo(kit);
			String formattedSolo = StringUtil.formatNumberWithCommas(eloWrapper.getSoloRating());
			String formattedParty = StringUtil.formatNumberWithCommas(eloWrapper.getPartyRating());

			player.sendMessage(CC.PRIMARY + kit.getName() + " ELO: "
					+ CC.SECONDARY + formattedSolo + CC.ACCENT + " (Solo) "
					+ CC.SECONDARY + formattedParty + CC.ACCENT + " (Party)");
		}
	}

	public void setupPlayer(Player player) {
		zone.potion.utils.player.PlayerUtil.clearPlayer(player);
		ItemHotbars.SPAWN_ITEMS.apply(player);

		player.teleport(plugin.getLocationManager().getSpawn());

		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
			PracticeProfile onlineProfile = getProfile(onlinePlayer.getUniqueId());

			if (onlineProfile.isHidingPlayers()) {
				onlinePlayer.hidePlayer(player);
			}
		}

		PlayerUtil.toggleFlyFor(player);
	}

	void resetPlayerMinimally(Player player, PracticeProfile profile, boolean teleport) {
		profile.setPlayerState(PlayerState.SPAWN);

		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setScoreboard(plugin.getServer().getScoreboardManager().getNewScoreboard());
		plugin.getCustomScoreboard().forceUpdate(player);

		ItemHotbars.giveSpawnItems(player, profile.isInParty());

		if (teleport) {
			player.teleport(plugin.getLocationManager().getSpawn());
		}
	}

	public void resetPlayer(Player player, boolean teleport) {
		PracticeProfile profile = getProfile(player.getUniqueId());
		boolean inParty = profile.isInParty();

		if (inParty) {
			profile.getParty().setState(PartyState.SPAWN);
		}

		profile.setPlayerState(PlayerState.SPAWN);
		profile.setMatch(null);

		zone.potion.utils.player.PlayerUtil.clearPlayer(player);
		ItemHotbars.giveSpawnItems(player, inParty);

		if (teleport) {
			player.teleport(plugin.getLocationManager().getSpawn());
		}

		if (profile.isHidingPlayers()) {
			PlayerUtil.hideAllPlayersFor(player);
		} else {
			PlayerUtil.showAllPlayersFor(player);
		}

		PlayerUtil.toggleFlyFor(player);
	}

	public Collection<PracticeProfile> getProfiles() {
		return profiles.values();
	}

	public void saveProfiles() {
		for (PracticeProfile profile : getProfiles()) {
			profile.save(false, plugin);
		}
	}
}
