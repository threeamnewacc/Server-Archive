package net.badlion.smellylobby.tasks;

import net.badlion.common.GetCommon;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.smellylobby.SmellyLobby;
import net.badlion.smellylobby.helpers.NavigationInventoryHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UpdatePlayerCountTask extends BukkitRunnable {

	private Map<String, Score> serverPlayerCounts = new HashMap<>();

	public UpdatePlayerCountTask() {
		// Create the scoreboard
		Scoreboard scoreboard = SmellyLobby.getInstance().getServer().getScoreboardManager().getNewScoreboard();
		SmellyLobby.getInstance().setScoreboard(scoreboard);

		Objective objective = scoreboard.registerNewObjective("playerCounts", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "Badlion Network");

		Score score = objective.getScore(ChatColor.GREEN + "Arena PvP");
		this.serverPlayerCounts.put("pvp", score);
		score.setScore(0);
		score = objective.getScore(ChatColor.YELLOW + "Hosted UHC");
		this.serverPlayerCounts.put("uhc", score);
		score.setScore(0);
		score = objective.getScore(ChatColor.AQUA + "UHC Factions");
		this.serverPlayerCounts.put("factions", score);
		score.setScore(0);
		score = objective.getScore(ChatColor.RED + "Lobby");
		this.serverPlayerCounts.put("lobby", score);
		score.setScore(0);
		score = objective.getScore(ChatColor.DARK_RED + "Survival Games");
		this.serverPlayerCounts.put("sg", score);
		score.setScore(0);
		score = objective.getScore(ChatColor.DARK_PURPLE + "Mini UHC");
		this.serverPlayerCounts.put("uhcmini", score);
		score.setScore(0);
		score = objective.getScore(ChatColor.LIGHT_PURPLE + "Kohi Games");
		this.serverPlayerCounts.put("kohigames", score);
		score.setScore(0);
		score = objective.getScore(ChatColor.GOLD + "Free For All");
		this.serverPlayerCounts.put("ffa", score);
		score.setScore(0);
		score = objective.getScore(ChatColor.DARK_GREEN + "Vault Battle");
		this.serverPlayerCounts.put("vaultbattle", score);
		score.setScore(0);
		score = objective.getScore(ChatColor.GOLD + "UHC Meetup");
		this.serverPlayerCounts.put("uhcmeetup", score);
		score.setScore(0);


		this.runTaskTimerAsynchronously(SmellyLobby.getInstance(), 100L, 100L);
	}

	@Override
	public void run() {
		try {
			final JSONObject response = this.executeArchAPIGETRequest("http://" + GetCommon.getIpForArchAPI() + ":9011/" + "GetServerPlayerCounts");
			SmellyLobby.getInstance().getServer().getScheduler().runTask(SmellyLobby.getInstance(), new Runnable() {
				@Override
				public void run() {
					int lobbyPlayerCount = 0;
					int arenaPlayerCount = 0;
					int factionsPlayerCount = 0;
					int miniUHCPlayerCount = 0;
					int sgPlayerCount = 0;
					int uhcPlayerCount = 0;
					int kohiGamesPlayerCount = 0;
					int vaultBattlePlayerCount = 0;
					int ffaPlayerCount = 0;
					int uhcMeetupPlayerCount = 0;

					int naArenaPlayerCount = 0;
					int euArenaPlayerCount = 0;
					int auArenaPlayerCount = 0;
					int saArenaPlayerCount = 0;
					int asArenaPlayerCount = 0;

					int naFFAPlayerCount = 0;
					int euFFAPlayerCount = 0;
					int auFFAPlayerCount = 0;
					int saFFAPlayerCount = 0;
					int asFFAPlayerCount = 0;

					int naSGPlayerCount = 0;
					int euSGPlayerCount = 0;
					int auSGPlayerCount = 0;
					int saSGPlayerCount = 0;
					int asSGPlayerCount = 0;

					int naUHCMeetupPlayerCount = 0;
					int euUHCMeetupPlayerCount = 0;
					int auUHCMeetupPlayerCount = 0;
					int saUHCMeetupPlayerCount = 0;
					int asUHCMeetupPlayerCount = 0;

					Inventory arenaPvPInventory = NavigationInventoryHelper.getArenaPvPInventory();
					Inventory ffaInventory = NavigationInventoryHelper.getFFAInventory();
					Inventory uhcMeetupInventory = NavigationInventoryHelper.getUHCMeetupInventory();
					Inventory factionsInventory = NavigationInventoryHelper.getFactionsInventory();
					Inventory uhcInventory = NavigationInventoryHelper.getUHCInventory();
					Inventory sgInventory = NavigationInventoryHelper.getSGInventory();

					for (Map.Entry<String, Long> entry : (Set<Map.Entry<String, Long>>) response.entrySet()) {
						if (!entry.getKey().equals("servers") && !entry.getKey().equals("players")){
							String serverName = entry.getKey();
							int playerCount = (int) ((long) entry.getValue());

							// Check if there are multiple of the same server type
							if (serverName.contains("bllobby") && !serverName.contains("sg") && !serverName.contains("sw")) {
								lobbyPlayerCount += playerCount;
							} else if (serverName.contains("arena")) {
								arenaPlayerCount += playerCount;

								if (arenaPvPInventory != null) {
									// Update the inventory item
									ItemStack item;
									if (serverName.startsWith("na")) {
										naArenaPlayerCount += playerCount;

										item = arenaPvPInventory.getItem(0);

										UpdatePlayerCountTask.updateItemLore(item, naArenaPlayerCount);
									} else if (serverName.startsWith("eu")) {
										euArenaPlayerCount += playerCount;

										item = arenaPvPInventory.getItem(4);

										UpdatePlayerCountTask.updateItemLore(item, euArenaPlayerCount);
									} else if (serverName.startsWith("au")) {
										auArenaPlayerCount += playerCount;

										item = arenaPvPInventory.getItem(8);

										UpdatePlayerCountTask.updateItemLore(item, auArenaPlayerCount);
									} else if (serverName.startsWith("sa")) {
										saArenaPlayerCount += playerCount;

										item = arenaPvPInventory.getItem(9);

										UpdatePlayerCountTask.updateItemLore(item, saArenaPlayerCount);
									} else if (serverName.startsWith("as")) {
										asArenaPlayerCount += playerCount;

										item = arenaPvPInventory.getItem(13);

										UpdatePlayerCountTask.updateItemLore(item, asArenaPlayerCount);
									}
								}
							} else if (serverName.endsWith("ffa")) {
								ffaPlayerCount += playerCount;

								SmellyInventory.FakeHolder fakeHolder = (SmellyInventory.FakeHolder) ffaInventory.getHolder();

								if (ffaInventory != null) {
									// Update the inventory item
									ItemStack item;
									if (serverName.startsWith("na")) {
										naFFAPlayerCount += playerCount;

										item = ffaInventory.getItem(0);

										UpdatePlayerCountTask.updateItemLore(item, naFFAPlayerCount);

										Inventory naFFAInventory = fakeHolder.getSubInventory(0);

										if (serverName.contains("nodebuff")) {
											UpdatePlayerCountTask.updateItemLore(naFFAInventory.getItem(0), playerCount);
										} else if (serverName.contains("sg")) {
											UpdatePlayerCountTask.updateItemLore(naFFAInventory.getItem(1), playerCount);
										} else if (serverName.contains("soup")) {
											UpdatePlayerCountTask.updateItemLore(naFFAInventory.getItem(2), playerCount);
										} else if (serverName.contains("uhc")) {
											UpdatePlayerCountTask.updateItemLore(naFFAInventory.getItem(3), playerCount);
										}
									} else if (serverName.startsWith("sa")) {
										saFFAPlayerCount += playerCount;

										item = ffaInventory.getItem(2);

										UpdatePlayerCountTask.updateItemLore(item, saFFAPlayerCount);

										Inventory saFFAInventory = fakeHolder.getSubInventory(2);

										if (serverName.contains("nodebuff")) {
											UpdatePlayerCountTask.updateItemLore(saFFAInventory.getItem(0), playerCount);
										} else if (serverName.contains("sg")) {
											UpdatePlayerCountTask.updateItemLore(saFFAInventory.getItem(1), playerCount);
										} else if (serverName.contains("soup")) {
											UpdatePlayerCountTask.updateItemLore(saFFAInventory.getItem(2), playerCount);
										} else if (serverName.contains("uhc")) {
											UpdatePlayerCountTask.updateItemLore(saFFAInventory.getItem(3), playerCount);
										}
									} else if (serverName.startsWith("eu")) {
										euFFAPlayerCount += playerCount;

										item = ffaInventory.getItem(4);

										UpdatePlayerCountTask.updateItemLore(item, euFFAPlayerCount);

										Inventory euFFAInventory = fakeHolder.getSubInventory(4);

										if (serverName.contains("nodebuff")) {
											UpdatePlayerCountTask.updateItemLore(euFFAInventory.getItem(0), playerCount);
										} else if (serverName.contains("sg")) {
											UpdatePlayerCountTask.updateItemLore(euFFAInventory.getItem(1), playerCount);
										} else if (serverName.contains("soup")) {
											UpdatePlayerCountTask.updateItemLore(euFFAInventory.getItem(2), playerCount);
										} else if (serverName.contains("uhc")) {
											UpdatePlayerCountTask.updateItemLore(euFFAInventory.getItem(3), playerCount);
										}
									} else if (serverName.startsWith("as")) {
										asFFAPlayerCount += playerCount;

										item = ffaInventory.getItem(6);

										UpdatePlayerCountTask.updateItemLore(item, asFFAPlayerCount);

										Inventory asFFAInventory = fakeHolder.getSubInventory(6);

										if (serverName.contains("nodebuff")) {
											UpdatePlayerCountTask.updateItemLore(asFFAInventory.getItem(0), playerCount);
										} else if (serverName.contains("sg")) {
											UpdatePlayerCountTask.updateItemLore(asFFAInventory.getItem(1), playerCount);
										} else if (serverName.contains("soup")) {
											UpdatePlayerCountTask.updateItemLore(asFFAInventory.getItem(2), playerCount);
										} else if (serverName.contains("uhc")) {
											UpdatePlayerCountTask.updateItemLore(asFFAInventory.getItem(3), playerCount);
										}
									} else if (serverName.startsWith("au")) {
										auFFAPlayerCount += playerCount;

										item = ffaInventory.getItem(8);

										UpdatePlayerCountTask.updateItemLore(item, auFFAPlayerCount);

										Inventory auFFAInventory = fakeHolder.getSubInventory(8);

										if (serverName.contains("nodebuff")) {
											UpdatePlayerCountTask.updateItemLore(auFFAInventory.getItem(0), playerCount);
										} else if (serverName.contains("sg")) {
											UpdatePlayerCountTask.updateItemLore(auFFAInventory.getItem(1), playerCount);
										} else if (serverName.contains("soup")) {
											UpdatePlayerCountTask.updateItemLore(auFFAInventory.getItem(2), playerCount);
										} else if (serverName.contains("uhc")) {
											UpdatePlayerCountTask.updateItemLore(auFFAInventory.getItem(3), playerCount);
										}
									}
								}
							} else if (serverName.contains("uhcmeetup")) {
								uhcMeetupPlayerCount += playerCount;

								if (uhcMeetupInventory != null) {
									// Update the inventory item
									ItemStack item;
									if (serverName.startsWith("na")) {
										naUHCMeetupPlayerCount += playerCount;

										item = uhcMeetupInventory.getItem(0);

										UpdatePlayerCountTask.updateItemLore(item, naUHCMeetupPlayerCount);
									} else if (serverName.startsWith("sa")) {
										saUHCMeetupPlayerCount += playerCount;

										item = uhcMeetupInventory.getItem(2);

										UpdatePlayerCountTask.updateItemLore(item, saUHCMeetupPlayerCount);
									} else if (serverName.startsWith("eu")) {
										euUHCMeetupPlayerCount += playerCount;

										item = uhcMeetupInventory.getItem(4);

										UpdatePlayerCountTask.updateItemLore(item, euUHCMeetupPlayerCount);
									} else if (serverName.startsWith("as")) {
										asUHCMeetupPlayerCount += playerCount;

										item = uhcMeetupInventory.getItem(6);

										UpdatePlayerCountTask.updateItemLore(item, asUHCMeetupPlayerCount);
									} else if (serverName.startsWith("au")) {
										auUHCMeetupPlayerCount += playerCount;

										item = uhcMeetupInventory.getItem(8);

										UpdatePlayerCountTask.updateItemLore(item, auUHCMeetupPlayerCount);
									}
								}
							} else if (serverName.contains("practice")) {
								// Just add this under FFA
								ffaPlayerCount += playerCount;

								ItemStack item = null;

								switch (serverName) {
									case "ccpractice":
										if (item == null) item = ffaInventory.getItem(18);
									default:
										UpdatePlayerCountTask.updateItemLore(item, playerCount);
								}
							} else if (serverName.equals("factions")) {
								factionsPlayerCount += playerCount;
								if (SmellyLobby.getInstance().getServer().getSpigotJarVersion() == Server.SERVER_VERSION.V1_7) {
									ItemStack item = factionsInventory.getItem(0);
									if (item != null) {
										ItemMeta itemMeta = item.getItemMeta();
										List<String> lore = item.getItemMeta().getLore();
										for (int i = 0; i < lore.size(); i++) {
											if (lore.get(i).startsWith(ChatColor.BLUE + "Players: ")) {
												lore.set(i, ChatColor.BLUE + "Players: " + playerCount + "/500");
												break;
											}
										}
										itemMeta.setLore(lore);
										item.setItemMeta(itemMeta);
									}
								}
							} else if (serverName.contains("uhcmini")) {
								miniUHCPlayerCount += playerCount;
							} else if (serverName.contains("sglobby") || serverName.contains("sgserver")) {
								sgPlayerCount += playerCount;

								if (sgInventory != null) {
									// Update the inventory item
									ItemStack item;
									if (serverName.startsWith("na")) {
										naSGPlayerCount += playerCount;

										item = sgInventory.getItem(0);

										UpdatePlayerCountTask.updateItemLore(item, naSGPlayerCount);
									} else if (serverName.startsWith("sa")) {
										saSGPlayerCount += playerCount;

										item = sgInventory.getItem(2);

										UpdatePlayerCountTask.updateItemLore(item, saSGPlayerCount);
									} else if (serverName.startsWith("eu")) {
										euSGPlayerCount += playerCount;

										item = sgInventory.getItem(4);

										UpdatePlayerCountTask.updateItemLore(item, euSGPlayerCount);
									} else if (serverName.startsWith("as")) {
										asSGPlayerCount += playerCount;

										item = sgInventory.getItem(6);

										UpdatePlayerCountTask.updateItemLore(item, asSGPlayerCount);
									} else if (serverName.startsWith("au")) {
										auSGPlayerCount += playerCount;

										item = sgInventory.getItem(8);

										UpdatePlayerCountTask.updateItemLore(item, auSGPlayerCount);
									}
								}
							} else if (serverName.contains("vb")) {
								vaultBattlePlayerCount += playerCount;
							} else if (serverName.contains("kg")) {
								kohiGamesPlayerCount += playerCount;
							} else if (serverName.contains("uhc") && !serverName.contains("uhcmini")) {
								uhcPlayerCount += playerCount;

								if (uhcInventory != null) {
									// Update the inventory item
									ItemStack item = null;
									switch (serverName) {
										case "uhc":
											item = uhcInventory.getItem(0);
										case "uhc2":
											if (item == null) item = uhcInventory.getItem(1);
										case "uhc3":
											if (item == null) item = uhcInventory.getItem(2);
										case "euuhc1":
											if (item == null) item = uhcInventory.getItem(4);
										case "euuhc2":
											if (item == null) item = uhcInventory.getItem(5);
										case "euuhc3":
											if (item == null) item = uhcInventory.getItem(6);
										case "sauhc1":
											if (item == null) item = uhcInventory.getItem(9);
										case "auuhc":
											if (item == null) item = uhcInventory.getItem(13);
										case "auuhc2":
											if (item == null) item = uhcInventory.getItem(14);
										default:
											// Avoid edge cases
											if (item != null) {
												ItemMeta itemMeta = item.getItemMeta();
												List<String> lore = item.getItemMeta().getLore();

												if (lore == null) {
													lore = new ArrayList<>();
													lore.add(ChatColor.BLUE + "Players: " + playerCount);
												} else {
													// Iterate through to fix the right spot
													int i = 0;
													boolean found = false;
													for (; i < lore.size(); i++) {
														if (lore.get(i).startsWith(ChatColor.BLUE + "Players: ")) {
															found = true;
															break;
														}
													}

													if (found) {
														lore.remove(i);
														lore.add(i, ChatColor.BLUE + "Players: " + playerCount);
													}
												}

												itemMeta.setLore(lore);
												item.setItemMeta(itemMeta);
											}
											break;
									}
								}
							} else {
								// Just update the scores for these now since only one server is sending player count
								Score score = UpdatePlayerCountTask.this.serverPlayerCounts.get(serverName);
								if (score != null) {
									score.setScore(playerCount);
								}
							}
						}
					}

					// Update the other servers that have multiple servers sending player counts
					UpdatePlayerCountTask.this.serverPlayerCounts.get("lobby").setScore(lobbyPlayerCount);
					UpdatePlayerCountTask.this.serverPlayerCounts.get("pvp").setScore(arenaPlayerCount);
					UpdatePlayerCountTask.this.serverPlayerCounts.get("uhcmini").setScore(miniUHCPlayerCount);
					UpdatePlayerCountTask.this.serverPlayerCounts.get("sg").setScore(sgPlayerCount);
					UpdatePlayerCountTask.this.serverPlayerCounts.get("uhc").setScore(uhcPlayerCount);
					UpdatePlayerCountTask.this.serverPlayerCounts.get("factions").setScore(factionsPlayerCount);
					UpdatePlayerCountTask.this.serverPlayerCounts.get("ffa").setScore(ffaPlayerCount);
					UpdatePlayerCountTask.this.serverPlayerCounts.get("vaultbattle").setScore(vaultBattlePlayerCount);
					UpdatePlayerCountTask.this.serverPlayerCounts.get("kohigames").setScore(kohiGamesPlayerCount);
					UpdatePlayerCountTask.this.serverPlayerCounts.get("uhcmeetup").setScore(uhcMeetupPlayerCount);
				}
			});
		} catch (HTTPRequestFailException e) {
			Bukkit.getLogger().info("Oh fuck wtf happened man? Exception with HTTP code " + e.getResponseCode());
		}
	}

	private JSONObject executeArchAPIGETRequest(String urlString) throws HTTPRequestFailException {
		urlString += "/8qPqqR324esK9hGrNkTzT3DUPp9UC9pC";
		return HTTPCommon.executeGETRequest(urlString);
	}

	public static void updateItemLore(ItemStack item, int playerCount) {
		// Avoid edge cases
		if (item == null) return;

		ItemMeta itemMeta = item.getItemMeta();
		List<String> lore = item.getItemMeta().getLore();
		// Iterate through to fix the right spot
		int i = 0;
		boolean found = false;
		for (; i < lore.size(); i++) {
			if (lore.get(i).startsWith(ChatColor.BLUE + "Players: ")) {
				found = true;
				break;
			}
		}

		if (found) {
			lore.remove(i);
			lore.add(i, ChatColor.BLUE + "Players: " + playerCount);
		}
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
	}

}