package net.badlion.arenalobby.managers;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.inventories.lobby.TournamentsInventory;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class TournamentManager {

	// TODO: Update this set through the keep alive request
	private static Set<Tournament> activeTournaments = new HashSet<>();

	public static void addTournament(Tournament tournament) {
		activeTournaments.add(tournament);
		TournamentsInventory.updateTournamentsInventory();
	}

	public static void removeTournament(Tournament tournament) {
		activeTournaments.remove(tournament);
		TournamentsInventory.updateTournamentsInventory();
	}

	public static Tournament getTournament(UUID uuid) {
		for (Tournament tournament : activeTournaments) {
			if (tournament.tournamentId.equals(uuid)) {
				return tournament;
			}
		}
		return null;
	}

	public static void joinQueue(final Player player, final Tournament tournament) {
		new BukkitRunnable() {
			@Override
			public void run() {
				JSONObject data = new JSONObject();
				data.put("uuid", player.getUniqueId().toString());
				data.put("tournament_id", tournament.getTournamentId().toString());
				try {
					JSONObject response = Gberry.contactMCP("arena-tournament", data);
					ArenaLobby.getInstance().getLogger().log(Level.INFO, "[sending tournament queue]: " + data);
					ArenaLobby.getInstance().getLogger().log(Level.INFO, "Getting tournament queue response " + response);
					if (!response.equals(MCPManager.successResponse)) {
						String error = (String) response.get("error");
						player.sendMessage(ChatColor.RED + error);
						if (error.equalsIgnoreCase("no_tournament_found") || error.equalsIgnoreCase("tournament_started")) {
							removeTournament(tournament);
						}
					}
				} catch (HTTPRequestFailException e) {
					player.sendFormattedMessage("{0}Unable to join tournament queue, try again later.", ChatColor.RED);
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(ArenaLobby.getInstance());
	}

	public static Set<Tournament> getActiveTournaments() {
		return activeTournaments;
	}

	public static class Tournament {
		private UUID tournamentId;
		private Ladder ladder;
		private int slots;

		public Tournament(UUID tournamentId, Ladder ladder, int slots) {
			this.tournamentId = tournamentId;
			this.ladder = ladder;
			this.slots = slots;
		}

		public UUID getTournamentId() {
			return tournamentId;
		}

		public Ladder getLadder() {
			return ladder;
		}

		public int getSlots() {
			return slots;
		}
	}

}
