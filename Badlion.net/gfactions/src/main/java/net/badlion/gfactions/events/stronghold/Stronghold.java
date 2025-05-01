package net.badlion.gfactions.events.stronghold;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gguard.ProtectedRegion;
import net.badlion.smellyloot.managers.LootManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class Stronghold extends BukkitRunnable {

	private long startTime = System.currentTimeMillis();

	private Set<String> involvedFactions = new HashSet<>();

	public Stronghold() {
		GFactions.plugin.setStronghold(this);

		// 10 minute message
		Gberry.broadcastMessage(ChatColor.YELLOW + "Stronghold event starting in 10 minutes!");

		for (Keep keep : Keep.getKeeps()) {
			// Make sure the doors are open
			for (Keep.Door door : keep.getKeepDoors().values()) {
				if (!door.isClosed()) {
					door.toggle();
				}
			}
		}

		// 5 minute message // TODO: RE-ENABLE
		/*BukkitUtil.runTaskLater(new Runnable() {
			@Override
			public void run() {
				Gberry.broadcastMessage(ChatColor.YELLOW + "Stronghold event starting in 5 minutes!");
			}
		}, 6000L);
                   // TODO: RE -ENABLE
		// 1 minute message
		BukkitUtil.runTaskLater(new Runnable() {
			@Override
			public void run() {
				Gberry.broadcastMessage(ChatColor.YELLOW + "Stronghold event starting in 1 minute!");
			}
		}, 10800L);*/ // TODO: RE -ENABLE

		// Start event
		BukkitUtil.runTaskLater(new Runnable() {
			@Override
			public void run() {
				// Start tracking for all the keeps
				for (Keep keep : Keep.getKeeps()) {
					keep.startKeepTrackerTask();

					// Make sure the doors are open
					for (Keep.Door door : keep.getKeepDoors().values()) {
						if (door.isClosed()) {
							door.toggle();
						}
					}

					// Add to involved factions set
					if (keep.getPreviousOwner() != null) {
						Stronghold.this.involvedFactions.add(keep.getPreviousOwner().getId());

						// Lock all controlling factions
						P.setFactionLocked(keep.getPreviousOwner(), true);

						// Send a message to online faction members
						for (Player pl : keep.getPreviousOwner().getOnlinePlayers()) {
							pl.sendMessage(ChatColor.YELLOW + "Your faction has been locked because you own a stronghold keep!");
						}
					}
				}

				// Start stronghold drop party
				LootManager.startDropParty("stronghold", "stronghold", false);

				// Start the stronghold player tracker task
				Stronghold.this.runTaskTimer(GFactions.plugin, 0L, 20L);

				Gberry.broadcastMessage(ChatColor.YELLOW + "Stronghold event has started!!!!");
			}
		}, 300L); // TODO: CHANGE TO 12000L
	}

	public void stop(boolean forcedEnd) {
		// Stop involved faction tracker task
		try {
			if (this.getTaskId() != -1) {
				this.cancel();
			}
		} catch (IllegalStateException e) {
		}

		// Keep stuff
		for (Keep keep : Keep.getKeeps()) {
			// Cancel all the tracker tasks
			if (keep.getKeepTrackerTask() != null) {
				keep.getKeepTrackerTask().cancel();
				keep.setKeepTrackerTask(null);
			}
		}

		// Unlock all factions
		for (Faction faction : P.lockedFactions.keySet()) {
			P.setFactionLocked(faction, false);
		}

		// End stronghold drop party
		LootManager.endDropParty("stronghold");

		// Clear involved factions
		this.involvedFactions.clear();

		GFactions.plugin.setStronghold(null);

		// SQL Stuff - CME's should never be hit
		if (!forcedEnd) {
			BukkitUtil.runTaskAsync(new Runnable() {
				@Override
				public void run() {
					String query = "INSERT INTO " + GFactions.PREFIX + "_stronghold_events (start_time, end_time, beginning_owners, ending_owners) " +
							"VALUES (?, ?, ?, ?);";

					Connection connection = null;
					PreparedStatement ps = null;

					try {
						connection = Gberry.getConnection();
						ps = connection.prepareStatement(query);

						ps.setTimestamp(1, new Timestamp(Stronghold.this.startTime));
						ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));

						// Serialize old keep owners
						StringBuilder sb = new StringBuilder();
						for (Keep keep : Keep.getKeeps()) {
							sb.append(keep.getName());
							sb.append(":");

							if (keep.getPreviousOwner() == null) {
								sb.append("none");
							} else {
								sb.append(keep.getPreviousOwner().getId());
							}

							sb.append(",");
						}
						String str = sb.toString();
						ps.setString(3, str.substring(0, str.length() - 1));

						// Serialize new keep owners
						sb.setLength(0);
						for (Keep keep : Keep.getKeeps()) {
							sb.append(keep.getName());
							sb.append(":");

							if (keep.getOwner() == null) {
								sb.append("none");
							} else {
								sb.append(keep.getOwner().getId());
							}

							sb.append(",");
						}
						str = sb.toString();
						ps.setString(4, str.substring(0, str.length() - 1));

						Gberry.executeUpdate(connection, ps);

						// Start tracking deterioration stuff for the new owners
						sb.setLength(0);
						sb.append("INSERT INTO ");
						sb.append(GFactions.PREFIX);
						sb.append("_stronghold_deterioration (stronghold_id, keep_name,  money_owed, items_owed) VALUES ");
						for (Keep keep : Keep.getKeeps()) {
							if (keep.getOwner() != null) {
								sb.append("(?,?,?,?), ");
							}
						}
						sb.setLength(sb.length() - 2); // Cut off the extra ", "
						sb.append(";");

						ps = connection.prepareStatement(sb.toString());

						int counter = 1;
						for (Keep keep : Keep.getKeeps()) {
							if (keep.getOwner() != null) {
								// Stronghold id
								ps.setInt(counter, GFactions.plugin.getStrongholdConfig().getStrongholdEventId());

								// Keep name
								ps.setString(counter + 1, keep.getName());

								// Money owed
								keep.setDeteriorationMoneyOwed(GFactions.plugin.getStrongholdConfig().getDeteriorationMoney());
								ps.setInt(counter + 2, GFactions.plugin.getStrongholdConfig().getDeteriorationMoney());

								// Items owed
								keep.setDeteriorationItemsOwed(GFactions.plugin.getStrongholdConfig().getDeteriorationItems());
								ps.setString(counter + 3, GFactions.plugin.getStrongholdConfig().serializeDeteriorationItemsOwed());

								counter += 4;
							}
						}

						Gberry.executeUpdate(connection, ps);

						// Faction participant stuff
						sb.setLength(0);
						sb.append("INSERT INTO ");
						sb.append(GFactions.PREFIX);
						sb.append("_stronghold_participants (stronghold_id, participant) VALUES ");
						for (int i = 0; i < Stronghold.this.involvedFactions.size(); i++) {
							sb.append("(?,?), ");
						}
						sb.setLength(sb.length() - 2); // Cut off the extra ", "
						sb.append(";");

						ps = connection.prepareStatement(sb.toString());

						counter = 1;
						for (String involvedFaction : Stronghold.this.involvedFactions) {
							ps.setInt(counter, GFactions.plugin.getStrongholdConfig().getStrongholdEventId());
							ps.setString(counter + 1, involvedFaction);

							counter += 2;
						}

						Gberry.executeUpdate(connection, ps);
					} catch (SQLException | NumberFormatException e) {
						e.printStackTrace();
					} finally {
						if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
						if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
					}
				}
			});
		}
	}

	@Override
	public void run() {
		for (final Player player : GFactions.plugin.getServer().getOnlinePlayers()) {
			// Are they in a stronghold region?
			ProtectedRegion region = GFactions.plugin.getgGuardPlugin().getProtectedRegion(player.getLocation(),
					GFactions.plugin.getgGuardPlugin().getProtectedRegions());
			if (region != null && GFactions.plugin.getStrongholdConfig().getEventRegions().contains(region.getRegionName())) {
				// Remove pvp protection if they have it
				if (GFactions.plugin.getMapNameToPvPTimeRemaining().containsKey(player.getUniqueId().toString())) {
					// Their PVP protection is over, time to remove from the system
					GFactions.plugin.getMapNameToPvPTimeRemaining().remove(player.getUniqueId().toString());
					GFactions.plugin.getMapNameToJoinTime().remove(player.getUniqueId().toString());

					GFactions.plugin.getServer().getScheduler().runTaskAsynchronously(GFactions.plugin, new Runnable() {

						@Override
						public void run() {
							// Purge from DB
							GFactions.plugin.removeProtection(player);
						}
					});

					player.sendMessage(ChatColor.RED + "Entered Stronghold region, lost PVP Protection.");
				}

				Faction faction = FPlayers.i.get(player).getFaction();
				if (faction != null && !faction.getId().equals("0") && this.involvedFactions.add(faction.getId())) {
					// Lock this faction
					P.setFactionLocked(faction, true);

					// Send a message to online faction members
					for (Player pl : faction.getOnlinePlayers()) {
						pl.sendMessage(ChatColor.YELLOW + "Your faction has been locked because someone entered a stronghold region!");
					}
				}
			}
		}
	}

	public Set<String> getInvolvedFactions() {
		return involvedFactions;
	}

}
