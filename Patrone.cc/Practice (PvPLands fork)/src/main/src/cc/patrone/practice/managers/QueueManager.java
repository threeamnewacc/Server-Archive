package cc.patrone.practice.managers;

import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.constants.ItemHotbars;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.match.MatchBuilder;
import cc.patrone.practice.party.Party;
import cc.patrone.practice.party.PartyState;
import cc.patrone.practice.player.PlayerState;
import cc.patrone.practice.player.PracticeProfile;
import cc.patrone.practice.queue.QueueEntry;
import cc.patrone.practice.tasks.QueueSearchTask;
import cc.patrone.practice.utils.MathUtil;
import cc.patrone.practice.utils.StringUtil;
import cc.patrone.practice.wrapper.EloWrapper;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class QueueManager {
	private final Map<PracticeProfile, QueueEntry> queueEntries = new HashMap<>();
	private final PracticePlugin plugin;
	@Getter
	@Setter
	private boolean rankedEnabled = true;

	public QueueEntry getEntry(PracticeProfile profile) {
		return queueEntries.get(profile);
	}

	public int playersQueued(String kitName, boolean ranked, boolean party) {
		return (int) queueEntries.values().stream()
				.filter(entry -> entry.isRanked() == ranked)
				.filter(entry -> entry.getKit().getName().equals(kitName))
				.filter(entry -> entry.isParty() == party)
				.count();
	}

	public void enqueueTeam(Player player, PracticeProfile profile, Kit kit, boolean ranked) {
		if (plugin.getArenaManager().getArenas().size() == 0 || kit.getAvailableArenas().size() == 0) {
			player.sendMessage(CC.RED + "There are no arenas available!");
			return;
		}

		if (queueEntries.containsKey(profile)) {
			player.sendMessage(CC.RED + "You are already in the queue!", true);
			return;
		}

		boolean isParty = profile.isInParty();

		if (isParty && profile.getParty().getMembers().size() != 2) {
			player.sendMessage(CC.RED + "You can only join the 2v2 queue with 2 players in your party!");
			return;
		}

		EloWrapper eloWrapper = profile.getElo(kit);
		int elo = ranked ? (isParty ? eloWrapper.getPartyRating() : eloWrapper.getSoloRating()) : 0;
		QueueEntry entry = new QueueEntry(profile, kit, ranked, isParty, elo);

		if (queueSearch(entry, 50)) {
			return;
		}

		Party party = profile.getParty();

		if (isParty) {
			party.setState(PartyState.QUEUED);
		} else {
			profile.setPlayerState(PlayerState.QUEUED);
		}

		queueEntries.put(profile, entry);

		plugin.getCustomScoreboard().forceUpdate(player);
		player.closeInventory();
		ItemHotbars.QUEUE_ITEMS.apply(player);

		if (isParty) {
			party.broadcast(ranked
					? CC.YELLOW + "Your party has been added to the " + CC.AQUA + "Ranked " + kit.getName()
					+ CC.YELLOW + " queue with " + CC.AQUA + StringUtil.formatNumberWithCommas(elo) + CC.YELLOW + " elo!."
					: CC.YELLOW + "You party has been added to the " + "Unranked " + CC.AQUA + kit.getName() + CC.YELLOW + " queue!");
		} else {
			player.sendMessage(ranked
							? CC.YELLOW + "You have been added to the " + CC.AQUA + "Ranked " + kit.getName()
							+ CC.YELLOW + " queue with " + CC.AQUA + StringUtil.formatNumberWithCommas(elo) + CC.YELLOW + " elo!"
							: CC.YELLOW + "You have been added to the " + CC.AQUA + "Unranked " + kit.getName() + CC.YELLOW + " queue!",
					true
			);
		}

		if (ranked) {
			QueueSearchTask task = new QueueSearchTask(plugin, entry);

			entry.setSearchTask(task);
			task.start();
		}
	}

	public void dequeueTeam(Player player, PracticeProfile profile) {
		plugin.getPlayerManager().resetPlayerMinimally(player, profile, false);

		QueueEntry entry = queueEntries.remove(profile);

		entry.cancelSearchTask();

		if (profile.isInParty()) {
			Party party = profile.getParty();

			party.setState(PartyState.SPAWN);
			party.broadcast(CC.RED + "Your party left the queue.");
		} else {
			plugin.getCustomScoreboard().forceUpdate(player);
			player.sendMessage(CC.RED + "You left the queue.", true);
		}
	}

	public boolean queueSearch(QueueEntry entry, int range) {
		if (queueEntries.isEmpty()) {
			return false;
		}

		QueueEntry found = queueEntries.values().stream()
				.filter(other -> other.getProfile() != entry.getProfile()
						&& other.isParty() == entry.isParty()
						&& other.isRanked() == entry.isRanked()
						&& other.getKit() == entry.getKit()
						&& MathUtil.isWithin(other.getElo(), entry.getElo(), range))
				.findAny()
				.orElse(null);

		if (found == null) {
			return false;
		}

		found.cancelSearchTask();
		queueEntries.remove(found.getProfile());

		entry.cancelSearchTask();

		PracticeProfile entryProfile = entry.getProfile();
		if (queueEntries.containsValue(entry)) {
			queueEntries.remove(entryProfile);
		}

		boolean party = entry.isParty();
		PracticeProfile foundProfile = found.getProfile();
		MatchBuilder matchBuilder = new MatchBuilder(plugin)
				.party(party)
				.ranked(found.isRanked())
				.kit(found.getKit());

		if (party) {
			Party foundParty = foundProfile.getParty();
			Party entryParty = entryProfile.getParty();

			matchBuilder.team(0, foundParty.getMembers());
			matchBuilder.team(1, entryParty.getMembers());

			String message = entry.isRanked()
					? CC.YELLOW + "Found Ranked Match: " + CC.GREEN + foundProfile.getName() + " (" + StringUtil.formatNumberWithCommas(found.getElo()) + " elo) "
					+ CC.YELLOW + "vs. " + CC.RED + entryProfile.getName() + "'s party (" + StringUtil.formatNumberWithCommas(entry.getElo()) + " elo)"
					: CC.YELLOW + "Found Unranked Match: " + CC.GREEN + foundProfile.getName() + "'s party "
					+ CC.YELLOW + "vs. " + CC.RED + entryProfile.getName() + "'s party ";

			foundParty.broadcast(message);
			entryParty.broadcast(message);
		} else {
			matchBuilder.team(0, foundProfile);
			matchBuilder.team(1, entryProfile);

			String message = entry.isRanked()
					? CC.YELLOW + "Found Ranked Match: " + CC.GREEN + foundProfile.getName() + " (" + StringUtil.formatNumberWithCommas(found.getElo()) + " elo) "
					+ CC.YELLOW + "vs. " + CC.RED + entryProfile.getName() + " (" + StringUtil.formatNumberWithCommas(entry.getElo()) + " elo)"
					: CC.YELLOW + "Found Unranked Match: " + CC.GREEN + foundProfile.getName() + " "
					+ CC.YELLOW + "vs. " + CC.RED + entryProfile.getName();

			Player foundPlayer = foundProfile.asPlayer();
			Player entryPlayer = entryProfile.asPlayer();

			foundPlayer.sendMessage(message, true);
			entryPlayer.sendMessage(message, true);
		}

		plugin.getMatchManager().startMatch(matchBuilder.build());
		return true;
	}
}
