package me.devkevin.practice.match.listener.game;

import club.inverted.chatcolor.CC;
import com.google.common.base.Joiner;
import me.devkevin.practice.Practice;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.elo.EloCalculator;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchState;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.match.event.impl.MatchEndEvent;
import me.devkevin.practice.match.history.MatchLocatedData;
import me.devkevin.practice.match.menu.MatchDetailsMenu;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.queue.QueueType;
import me.devkevin.practice.util.Clickable;
import me.devkevin.practice.util.TaskUtil;
import me.devkevin.practice.util.TimeUtils;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;

import java.util.*;

/**
 * Copyright 12/01/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class MatchEndListener implements Listener {

	private final Practice plugin = Practice.getInstance();

	@EventHandler
	public void onMatchEnd(MatchEndEvent event) {
		Match match = event.getMatch();
		Clickable inventories = new Clickable();


		match.setMatchState(MatchState.ENDING);
		match.setWinningTeamId(event.getWinningTeam().getTeamID());
		match.setCountdown(4);

		List<UUID> spectators = new ArrayList<>(match.getSpectators());
		spectators.stream()
				.filter(spec -> !Practice.getInstance().getProfileManager().getProfileData(spec).getCachedPlayer().isEmpty())
				.forEach(spec -> Practice.getInstance().getProfileManager().getProfileData(spec).getCachedPlayer().clear()
		);

		match.getTeams().forEach(team -> team.players().forEach(player -> {
			if (!match.hasSnapshot(player.getUniqueId())) {
				match.addSnapshot(player);
			}

			Profile profile = plugin.getProfileManager().getProfileData(player.getUniqueId());

			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
			player.setHealth(20.0D);
			player.setFoodLevel(20);
			player.setSaturation(12.8F);
			player.setFireTicks(0);
			player.setMaximumNoDamageTicks(20);
		}));

		Optional<Player> winnerPlayer = Optional.ofNullable(this.plugin.getServer().getPlayer(event.getWinningTeam().getLeader()));
		Optional<Player> loserPlayer = Optional.ofNullable(this.plugin.getServer().getPlayer(event.getLosingTeam().getLeader()));

		//  Implement Data practice for winner & losser
		Player winnerLeader = this.plugin.getServer().getPlayer(event.getWinningTeam().getPlayers().get(0));
		Profile winnerLeaderData = this.plugin.getProfileManager()
				.getProfileData(winnerLeader.getUniqueId());

		Player loserLeader = this.plugin.getServer().getPlayer(event.getLosingTeam().getPlayers().get(0));
		Profile loserLeaderData = this.plugin.getProfileManager()
				.getProfileData(loserLeader.getUniqueId());

		MatchTeam winnerTeam = event.getWinningTeam();
		MatchTeam losingTeam = event.getLosingTeam();


		if (match.isPartyMatch() && !match.isFFA() && match.getKit().getName().equalsIgnoreCase("HCF")) {
			Party partyOne = plugin.getPartyManager().getParty(match.getTeams().get(0).getLeader()),
					partyTwo = plugin.getPartyManager().getParty(match.getTeams().get(1).getLeader());

			partyOne.getBards().parallelStream().forEach(bardUuid -> plugin.getProfileManager().getProfileData(bardUuid).incrementPlayedBard());
			partyTwo.getBards().parallelStream().forEach(bardUuid -> plugin.getProfileManager().getProfileData(bardUuid).incrementPlayedBard());

			partyOne.getArchers().parallelStream().forEach(archerUuid -> plugin.getProfileManager().getProfileData(archerUuid).incrementPlayedArcher());
			partyTwo.getArchers().parallelStream().forEach(archerUuid -> plugin.getProfileManager().getProfileData(archerUuid).incrementPlayedArcher());

		}

		if (match.isFFA()) {
			Player winner = this.plugin.getServer().getPlayer(event.getWinningTeam().getAlivePlayers().get(0));
			match.broadcastMessage(PracticeLang.line);
			String winnerMessage = ChatColor.DARK_PURPLE + "Winner: " + CC.PINK + winner.getName();

			event.getWinningTeam().players().forEach(player -> {

				if (!match.hasSnapshot(player.getUniqueId())) {
					match.addSnapshot(player);
				}

				inventories.add((player.getUniqueId() == winner.getUniqueId() ? ChatColor.GREEN : ChatColor.RED)
								+ player.getName() + " ",
						ChatColor.RED + "View Inventory",
						"/inventory " + match.getSnapshot(player.getUniqueId()).getSnapshotId());

				player.getInventory().clear();
				player.getInventory().setArmorContents(null);
				player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
				player.setHealth(20.0D);
				player.setFoodLevel(20);
				player.setSaturation(12.8F);
				player.setFireTicks(0);
			});

			for (MatchDetailsMenu snapshot : match.getSnapshots().values()) {
				this.plugin.getMatchDetailSnapshot().addSnapshot(snapshot);
			}

			match.broadcastMessage(winnerMessage);
			match.broadcastMessage(inventories);
			match.broadcastMessage(PracticeLang.line);
		} else if (match.isParty()) {
			match.getTeams().forEach(team -> team.players().forEach(player -> {
				if (!match.hasSnapshot(player.getUniqueId())) {
					match.addSpectator(player.getUniqueId());
				}

				boolean winningTeam = this.plugin.getProfileManager().getProfileData(player.getUniqueId()).getTeamID() == event.getWinningTeam().getTeamID();

				inventories.add((winningTeam ? CC.GREEN : CC.RED)
								+ player.getName() + " ",
						ChatColor.DARK_PURPLE + "View menus",
						"/inventory " + match.getSnapshot(player.getUniqueId()).getSnapshotId());

			}));

			for (MatchDetailsMenu snapshot : match.getSnapshots().values()) {
				this.plugin.getMatchDetailSnapshot().addSnapshot(snapshot);
			}

			String winnerMessage = ChatColor.DARK_PURPLE + "Winning Team: "
					+ CC.PINK + event.getWinningTeam().getLeaderName();

			match.broadcastMessage("");
			match.broadcastMessage(winnerMessage);
			match.broadcastMessage(inventories);
		}
		else if (match.getKit().isBedWars()) {
			match.broadcastMessage(PracticeLang.line);
			match.broadcastMessage(CC.GOLD + "Match Results");
			match.broadcastMessage("");

			match.broadcastMessage(CC.translate(
					String.valueOf(winnerTeam.getTeamID() == 1 ? "&9Blue" : "&cRed" + " &ahas won!")
			));

			match.broadcastMessage(CC.translate(
					String.valueOf(losingTeam.getTeamID() == 1 ? "&9Blue" : "&cRed" + " &ahas won!")
			));

			match.broadcastMessage(CC.translate(
					String.valueOf(winnerTeam.getTeamID() == 1 ? "&9" : "&c") +
							String.valueOf(winnerTeam.getBridgesPoints()) + " &7- " +
							String.valueOf(losingTeam.getTeamID() == 1 ? "&9" : "&c") +
							String.valueOf(losingTeam.getBridgesPoints())
			));

			match.broadcastMessage("");
			match.broadcastMessage(CC.GOLD + "Inventories");

			Map<UUID, MatchDetailsMenu> inventorySnapshotMap = new LinkedHashMap<>();

			match.getTeams().forEach(team -> team.players().forEach(player -> {
				if (!match.hasSnapshot(player.getUniqueId())) {
					match.addSnapshot(player);
				}

				inventorySnapshotMap.put(player.getUniqueId(), match.getSnapshot(player.getUniqueId()));

				boolean onWinningTeam = this.plugin.getProfileManager().getProfileData(player.getUniqueId()).getTeamID() == event.getWinningTeam().getTeamID();

				inventories.add((onWinningTeam ? ChatColor.GREEN : ChatColor.RED)
								+ player.getName() + " ",
						ChatColor.AQUA + "View menus",
						"/inventory " + match.getSnapshot(player.getUniqueId()).getSnapshotId());

				player.setMaximumNoDamageTicks(20); // Double setting the damage ticks.
			}));

			for (MatchDetailsMenu snapshot : match.getSnapshots().values()) {
				this.plugin.getMatchDetailSnapshot().addSnapshot(snapshot);
			}

			match.broadcastMessage(inventories);
			match.broadcastMessage(PracticeLang.line);
		}
		else if (match.isRedrover()) {
			match.broadcastMessage(CC.GREEN + event.getWinningTeam().getLeaderName() + CC.GRAY + " has won the redrover!");
		}
		else {
			match.broadcastMessage("");
			match.broadcastMessage(CC.YELLOW + CC.BOLD + "Match Results: " + CC.GRAY + "(Click player to view):");

			Map<UUID, MatchDetailsMenu> inventorySnapshotMap = new LinkedHashMap<>();

			match.getTeams().forEach(team -> team.players().forEach(player -> {
				if (!match.hasSnapshot(player.getUniqueId())) {
					match.addSnapshot(player);
				}

				inventorySnapshotMap.put(player.getUniqueId(), match.getSnapshot(player.getUniqueId()));

				boolean onWinningTeam = this.plugin.getProfileManager().getProfileData(player.getUniqueId()).getTeamID() == event.getWinningTeam().getTeamID();

				inventories.add(CC.YELLOW + "Click to view " + (onWinningTeam ? ChatColor.GREEN : ChatColor.RED) + player.getName(), CC.YELLOW + "'s inventory",
						"/inventory " + match.getSnapshot(player.getUniqueId()).getSnapshotId());


				// Pvplounge death animation
				player.getInventory().clear();
				player.getInventory().setArmorContents(null);
				player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
				player.setHealth(20.0D);
				player.setFoodLevel(20);
				player.setSaturation(12.8F);
				player.setFireTicks(0);

				player.setMaximumNoDamageTicks(20); // Double setting the damage ticks.

				MatchTeam otherTeam = team == match.getTeams().get(0) ? match.getTeams().get(1) : match.getTeams().get(0);
			}));

			for (MatchDetailsMenu snapshot : match.getSnapshots().values()) {
				this.plugin.getMatchDetailSnapshot().addSnapshot(snapshot);
			}

			TextComponent first = new TextComponent();
			first.setText(ChatColor.translateAlternateColorCodes('&', "&aWinner: &e"));


			TextComponent winner = new TextComponent();
			winner.setText(ChatColor.translateAlternateColorCodes('&', "&e" + winnerLeader.getName()));
			winner.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inventory " + match.getSnapshot(winnerLeader.getUniqueId()).getSnapshotId()));
			winner.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&eClick to see the &6" + winnerLeader.getName() + "&e's menus")).create()));

			TextComponent second = new TextComponent();
			second.setText(ChatColor.translateAlternateColorCodes('&', " &7\u239c "));
			TextComponent third = new TextComponent();
			third.setText(ChatColor.translateAlternateColorCodes('&', "&cLoser: &e"));
			TextComponent loser = new TextComponent();
			loser.setText(ChatColor.translateAlternateColorCodes('&', "&e" + loserLeader.getName()));
			loser.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inventory " + match.getSnapshot(loserLeader.getUniqueId()).getSnapshotId()));
			loser.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&eClick to see the &6" + loserLeader.getName() + "&e's menus")).create()));

			BaseComponent[] bases = {
					first,
					winner,
					second,
					third,
					loser
			};

			match.broadcastMessage(bases);
			match.broadcastMessage("");

			/*Player winnerLeader = this.plugin.getServer().getPlayer(event.getWinningTeam().getPlayers().get(0));
			Profile winnerLeaderData = this.plugin.getProfileManager()
					.getProfileData(winnerLeader.getUniqueId());
			Player loserLeader = this.plugin.getServer().getPlayer(event.getLosingTeam().getPlayers().get(0));
			Profile loserLeaderData = this.plugin.getProfileManager()
					.getProfileData(loserLeader.getUniqueId());*/


			winnerLeaderData.setMatchesPlayed(winnerLeaderData.getMatchesPlayed() + 1);
			loserLeaderData.setMatchesPlayed(loserLeaderData.getMatchesPlayed() + 1);

			String kitName = match.getKit().getName();

			winnerLeaderData.setCurrentWinstreak(kitName, winnerLeaderData.getCurrentWinstreak(kitName) + 1);
			loserLeaderData.setCurrentWinstreak(kitName, 0);
			if (winnerLeaderData.getCurrentWinstreak(kitName) > winnerLeaderData.getHighestWinStreak(kitName)) {
				winnerLeaderData.setHighestWinStreak(kitName, winnerLeaderData.getCurrentWinstreak(kitName));
			}

			winnerLeaderData.setGlobalWinStreak(winnerLeaderData.getGlobalWinStreak() + 1);
			loserLeaderData.setGlobalWinStreak(0);
			if (winnerLeaderData.getGlobalWinStreak() > winnerLeaderData.getGlobalHighestWinStreak()) {
				winnerLeaderData.setGlobalHighestWinStreak(winnerLeaderData.getGlobalWinStreak());
			}

			/*//TODO:
			if (match.getType().isBoth()) {
				return;
			}*/

			if (match.getType().isRanked()) {

				UUID loserUuid = loserPlayer.map(Player::getUniqueId).orElse(UUID.randomUUID());
				UUID winnerUuid = winnerPlayer.map(Player::getUniqueId).orElse(UUID.randomUUID());

				String eloMessage;

				int winnerElo;
				int loserElo;

				winnerElo = winnerLeaderData.getElo(kitName);
				loserElo = loserLeaderData.getElo(kitName);

				int[] rankings = EloCalculator.getNewRankings(winnerElo, loserElo, true);


				eloMessage =
						CC.DARK_PURPLE + "Elo changes: " + CC.PINK + winnerLeader.getName() + " +" + (rankings[0] - winnerElo) + " (" + rankings[0] + ")" +

								CC.DARK_PURPLE + " / " +

								CC.RED + loserLeader.getName() + " " + (rankings[1] - loserElo) + " (" + rankings[1] + ")";

				if (match.getType() == QueueType.RANKED) {
					winnerLeaderData.setElo(kitName, rankings[0]);
					loserLeaderData.setElo(kitName, rankings[1]);

					winnerLeaderData.setWins(kitName, winnerLeaderData.getWins(kitName) + 1);
					loserLeaderData.setLosses(kitName, loserLeaderData.getLosses(kitName) + 1);

					winnerLeaderData.setWins(kitName, winnerLeaderData.getRankedWins().size() + 1);
					loserLeaderData.setLosses(kitName, loserLeaderData.getRankedLosses().size() + 1);

					match.broadcastMessage(eloMessage);

					MatchLocatedData matchLocatedData = new MatchLocatedData();
					matchLocatedData.setId(UUID.randomUUID().toString().split("-")[0]);

					matchLocatedData.setWinnerUUID(winnerUuid);
					matchLocatedData.setLoserUUID(loserUuid);

					matchLocatedData.setWinnerEloModifier(rankings[0] - winnerElo);
					matchLocatedData.setLoserEloModifier(rankings[1] - loserElo);

					matchLocatedData.setWinnerElo(rankings[0]);
					matchLocatedData.setLoserElo(rankings[1]);

					matchLocatedData.setDate(TimeUtils.nowDate());
					matchLocatedData.setKit(match.getKit().getName());

					matchLocatedData.setWinnerArmor(event.getMatch().getSnapshot(winnerUuid).getArmor());
					matchLocatedData.setWinnerContents(event.getMatch().getSnapshot(winnerUuid).getInventory());

					matchLocatedData.setLoserArmor(event.getMatch().getSnapshot(loserUuid).getArmor());
					matchLocatedData.setLoserContents(event.getMatch().getSnapshot(loserUuid).getInventory());
					// new
					matchLocatedData.setMissedPotsWinner(winnerLeaderData.getPotionsMissed());
					matchLocatedData.setMissedPotsLoser(loserLeaderData.getPotionsMissed());

					matchLocatedData.setThrownPotsWinner(winnerLeaderData.getPotionsThrown());
					matchLocatedData.setThrownPotsLoser(loserLeaderData.getPotionsThrown());

					matchLocatedData.setLongestComboWinner(winnerLeaderData.getLongestCombo());
					matchLocatedData.setLongestComboLoser(loserLeaderData.getLongestCombo());

					matchLocatedData.setComboWinner(winnerLeaderData.getCombo());
					matchLocatedData.setComboLoser(loserLeaderData.getCombo());

					matchLocatedData.setHitsWinner(winnerLeaderData.getHits());
					matchLocatedData.setHitsLoser(loserLeaderData.getHits());

					matchLocatedData.setPotionAccuracyWinner(winnerLeaderData.getPotionAccuracy());
					matchLocatedData.setPotionAccuracyLoser(loserLeaderData.getPotionAccuracy());

					matchLocatedData.setMatchDuration(event.getMatch().getDuration());
					matchLocatedData.setArenaName(event.getMatch().getArena().getName());
					matchLocatedData.save();
				}
			}
			plugin.getMatchManager().processRequeue(loserLeader, match);
			plugin.getMatchManager().processRequeue(winnerLeader, match);
			plugin.getMatchManager().saveRematches(match);
		}
		if (spectators.size() > 1) {
			List<String> spectatorNames = new ArrayList<>();
			for (UUID uuid : spectators) {
				spectatorNames.add(Bukkit.getPlayer(uuid).getName());
				spectatorNames.remove(Bukkit.getPlayer(event.getLosingTeam().getLeader()).getName());
			}

			spectatorNames.sort(String::compareToIgnoreCase);

			String firstFourNames = Joiner.on(", ").join(
					spectatorNames.subList(
							0,
							Math.min(spectatorNames.size(), 4)
					)
			);

			if (spectatorNames.size() > 4) {
				firstFourNames += " (+" + (spectatorNames.size() - 4) + " more)";
			}

			String spectators2 = ChatColor.YELLOW + "Spectators (" + spectatorNames.size() + "): " + ChatColor.LIGHT_PURPLE + firstFourNames;
			String space = " ";

			match.broadcastMessage(space);
			match.broadcastMessage(spectators2);
		}

		match.getTeams().forEach(matchTeam -> matchTeam.players().forEach(player -> {
			Profile data = plugin.getProfileManager().getProfileData(player.getUniqueId());
			data.getPotions().clear();
			data.getPackets().clear();

			data.setLastArenaPlayed(match.getArena());

			TaskUtil.runAsync(() -> this.plugin.getVoteManager().sendVoteMessage(player, match.getArena()));
		}));
	}
}
