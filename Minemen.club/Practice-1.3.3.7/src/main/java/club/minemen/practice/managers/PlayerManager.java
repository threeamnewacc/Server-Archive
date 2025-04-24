package club.minemen.practice.managers;

import club.minemen.core.CorePlugin;
import club.minemen.core.api.abstr.AbstractBukkitCallback;
import club.minemen.core.mineman.Mineman;
import club.minemen.core.timer.impl.EnderpearlTimer;
import club.minemen.core.util.Config;
import club.minemen.core.util.finalutil.CC;
import club.minemen.core.util.finalutil.ItemUtil;
import club.minemen.practice.Practice;
import club.minemen.practice.kit.Kit;
import club.minemen.practice.kit.PlayerKit;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.player.PlayerState;
import club.minemen.practice.request.PracticeFetchStatsRequest;
import club.minemen.practice.request.UpdateStatsRequest;
import club.minemen.practice.util.PlayerUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

public class PlayerManager {

	private final Practice plugin = Practice.getInstance();
	private final Map<UUID, PlayerData> playerData = new ConcurrentHashMap<>();

	public void createPlayerData(Player player) {
		PlayerData data = new PlayerData(player.getUniqueId());

		this.playerData.put(data.getUniqueId(), data);
		this.loadData(data);
	}

	@SuppressWarnings("unchecked")
	private void loadData(PlayerData playerData) {
		Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(playerData.getUniqueId());
		if (mineman == null || !mineman.isDataLoaded() || mineman.isErrorLoadingData()) {
			return;
		}

		playerData.setMinemanID(mineman.getId());
		playerData.setPlayerState(PlayerState.SPAWN);

		/*
		 * Loading player stats.
		 */
		InetAddress address = mineman.getIpAddress();
		CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(
				new PracticeFetchStatsRequest(playerData.getUniqueId()),
				new AbstractBukkitCallback() {
					@Override
					public void callback(JsonElement jsonElement) {
						if (!jsonElement.isJsonNull()) {
							JsonObject stats = jsonElement.getAsJsonObject();

							JsonElement element = stats.get("allowingSpectators");
							if (element != null && !element.isJsonNull()) {
								playerData.setAllowingSpectators(element.getAsBoolean());
							}

							element = stats.get("acceptingDuels");
							if (element != null && !element.isJsonNull()) {
								playerData.setAcceptingDuels(element.getAsBoolean());
							}

							element = stats.get("scoreboardEnabled");
							if (element != null && !element.isJsonNull()) {
								playerData.setScoreboardEnabled(element.getAsBoolean());
							}

							element = stats.get("pingRange");
							if (element != null && !element.isJsonNull()) {
								playerData.setPingRange(element.getAsInt());
							}

							element = stats.get("eloRange");
							if (element != null && !element.isJsonNull()) {
								playerData.setEloRange(element.getAsInt());
							}

							element = stats.get("premiumElo");
							if (element != null && !element.isJsonNull()) {
								playerData.setPremiumElo(element.getAsInt());
							}

							element = stats.get("premiumWins");
							if (element != null && !element.isJsonNull()) {
								playerData.setPremiumWins(element.getAsInt());
							}

							element = stats.get("premiumLosses");
							if (element != null && !element.isJsonNull()) {
								playerData.setPremiumLosses(element.getAsInt());
							}

							element = stats.get("premiumMatchesExtra");
							if (element != null && !element.isJsonNull()) {
								playerData.setPremiumMatchesExtra(element.getAsInt());
							}

							element = stats.get("premiumMatchesPlayed");
							if (element != null && !element.isJsonNull()) {
								playerData.setPremiumMatchesPlayed(element.getAsInt());
							}

							for (Kit kit : Practice.getInstance().getKitManager().getKits()) {
								String kitName = kit.getName();

								element = stats.get(kitName.toLowerCase() + "Elo");
								if (element != null && !element.isJsonNull()) {
									playerData.setElo(kitName, element.getAsInt());
								}

								element = stats.get(kitName + "Wins");
								if (element != null && !element.isJsonNull()) {
									playerData.setWins(kitName, element.getAsInt());
								}

								element = stats.get(kitName + "Losses");
								if (element != null && !element.isJsonNull()) {
									playerData.setLosses(kitName, element.getAsInt());
								}

								element = stats.get(kitName + "EloParty");
								if (element != null && !element.isJsonNull()) {
									playerData.setPartyElo(kitName, element.getAsInt());
								}
							}
						}

						Config config = new Config("/players/" + playerData.getUniqueId().toString(),
								PlayerManager.this.plugin);
						ConfigurationSection playerKitsSection = config.getConfig()
								.getConfigurationSection("playerkits");

						if (playerKitsSection != null) {
							PlayerManager.this.plugin.getKitManager().getKits().forEach(kit -> {
								ConfigurationSection kitSection = playerKitsSection
										.getConfigurationSection(kit.getName());

								if (kitSection != null) {
									kitSection.getKeys(false).forEach(kitKey -> {
										Integer kitIndex = Integer.parseInt(kitKey);
										String displayName = kitSection.getString(kitKey + ".displayName");

										ItemStack[] contents = ((List<ItemStack>) kitSection
												.get(kitKey + ".contents"))
												.toArray(new ItemStack[0]);

										PlayerKit playerKit = new PlayerKit(kit.getName(), kitIndex, contents,
												displayName);

										playerData.addPlayerKit(kitIndex, playerKit);
									});
								}
							});
						}
					}

					@Override
					public void onError(String message) {
						super.onError(message);
						PlayerManager.this.plugin.getLogger()
								.severe("Error fetching practice stats for " +
										playerData.getUniqueId());
					}
				}
		);
	}

	public void removePlayerData(UUID uuid) {
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, ()
				-> {
			PlayerManager.this.saveData(PlayerManager.this.playerData.get(uuid));
			PlayerManager.this.playerData.remove(uuid);
		});
	}

	@SuppressWarnings("unchecked")
	public void saveData(PlayerData playerData) {
		/*
		 * Saving player kits.
		 */
		Config config = new Config("/players/" + playerData.getUniqueId().toString(), this.plugin);

		this.plugin.getKitManager().getKits().forEach(kit -> {
			Map<Integer, PlayerKit> playerKits = playerData.getPlayerKits(kit.getName());
			if (playerKits != null) {
				playerKits.forEach((key, value) -> {
					config.getConfig().set("playerkits." + kit.getName() + "." + key + ".displayName",
							value.getDisplayName());
					config.getConfig()
							.set("playerkits." + kit.getName() + "." + key + ".contents", value.getContents());
				});
			}
		});

		config.save();

		/*
		 * Saving player stats.
		 */
		JSONObject data = new JSONObject();

		for (String kitName : this.plugin.getKitManager().getRankedKits()) {
			JSONObject kitData = new JSONObject();

			kitData.put("elo", playerData.getElo(kitName));
			kitData.put("party-elo", playerData.getPartyElo(kitName));

			data.put(kitName, kitData);
		}

		UpdateStatsRequest request = UpdateStatsRequest.builder()
				.uuid(playerData.getUniqueId())
				.scoreboardEnabled(playerData.isScoreboardEnabled())
				.acceptingDuels(playerData.isAcceptingDuels())
				.allowingSpectators(playerData.isAllowingSpectators())
				.pingRange(playerData.getPingRange())
				.eloRange(playerData.getEloRange())
				.id(playerData.getMinemanID())

				.nodebuffEloParty(playerData.getPartyElo("NoDebuff"))
				.nodebuffLosses(playerData.getLosses("NoDebuff"))
				.nodebuffWins(playerData.getWins("NoDebuff"))
				.nodebuffElo(playerData.getElo("NoDebuff"))

				.debuffEloParty(playerData.getPartyElo("Debuff"))
				.debuffLosses(playerData.getLosses("Debuff"))
				.debuffWins(playerData.getWins("Debuff"))
				.debuffElo(playerData.getElo("Debuff"))

				.gappleEloParty(playerData.getPartyElo("Gapple"))
				.gappleLosses(playerData.getLosses("Gapple"))
				.gappleWins(playerData.getWins("Gapple"))
				.gappleElo(playerData.getElo("Gapple"))

				.archerEloParty(playerData.getPartyElo("Archer"))
				.archerLosses(playerData.getLosses("Archer"))
				.archerWins(playerData.getWins("Archer"))
				.archerElo(playerData.getElo("Archer"))

				.axeEloParty(playerData.getPartyElo("Axe"))
				.axeLosses(playerData.getLosses("Axe"))
				.axeWins(playerData.getWins("Axe"))
				.axeElo(playerData.getElo("Axe"))

				.classicEloParty(playerData.getPartyElo("Classic"))
				.classicLosses(playerData.getLosses("Classic"))
				.classicWins(playerData.getWins("Classic"))
				.classicElo(playerData.getElo("Classic"))

				.hcfEloParty(playerData.getPartyElo("HCF"))
				.hcfLosses(playerData.getLosses("HCF"))
				.hcfWins(playerData.getWins("HCF"))
				.hcfElo(playerData.getElo("HCF"))

				.sumoEloParty(playerData.getPartyElo("Sumo"))
				.sumoLosses(playerData.getLosses("Sumo"))
				.sumoWins(playerData.getWins("Sumo"))
				.sumoElo(playerData.getElo("Sumo"))

				.builduhcEloParty(playerData.getPartyElo("BuildUHC"))
				.builduhcLosses(playerData.getLosses("BuildUHC"))
				.builduhcWins(playerData.getWins("BuildUHC"))
				.builduhcElo(playerData.getElo("BuildUHC"))

				.premiumMatchesPlayed(playerData.getPremiumMatchesPlayed())
				.premiumMatchesExtra(playerData.getPremiumMatchesExtra())
				.premiumLosses(playerData.getPremiumLosses())
				.premiumWins(playerData.getPremiumWins())
				.premiumElo(playerData.getPremiumElo())

				.build();

		CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(request);
	}

	public Collection<PlayerData> getAllData() {
		return this.playerData.values();
	}

	public PlayerData getPlayerData(UUID uuid) {
		return this.playerData.get(uuid);
	}

	public void giveLobbyItems(Player player) {
		boolean inParty = this.plugin.getPartyManager().getParty(player.getUniqueId()) != null;
		boolean inTournament = this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null;
		boolean isRematching = this.plugin.getMatchManager().isRematching(player.getUniqueId());
		ItemStack[] items = this.plugin.getItemManager().getSpawnItems();

		if (inTournament) {
			items = this.plugin.getItemManager().getTournamentItems();
		} else if (inParty) {
			items = this.plugin.getItemManager().getPartyItems();
		}

		player.getInventory().setContents(items);

		if (isRematching && !inParty && !inTournament) {
			player.getInventory()
					.setItem(3, ItemUtil.createItem(Material.BLAZE_POWDER, CC.PRIMARY + "Request Rematch"));
			player.getInventory()
					.setItem(6, ItemUtil.createItem(Material.PAPER, CC.PRIMARY + "View Opponent's Inventory"));
		}

		player.updateInventory();
	}

	public void sendToSpawnAndReset(Player player) {
		PlayerData playerData = this.getPlayerData(player.getUniqueId());

		playerData.setPlayerState(PlayerState.SPAWN);
		PlayerUtil.clearPlayer(player);
		CorePlugin.getInstance().getTimerManager().getTimer(EnderpearlTimer.class).clearCooldown(player.getUniqueId());

		this.giveLobbyItems(player);

		if (!player.isOnline()) {
			return;
		}

		this.plugin.getServer().getOnlinePlayers().forEach(p -> {
			player.hidePlayer(p);
			p.hidePlayer(player);
		});

		player.teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
	}

	public int getPremiumMatches(UUID uuid) {
		Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(uuid);
		switch (mineman.getRank()) {
			// Staff don't get any
			case MOD:
			case MODPLUS:
			case NORMAL:
			case SR_MOD:
			case TRAINEE:
			case TRIAL_MOD:
			case BUILDER:
				return 0;

			// Donors get a set amount
			case MEMBER:
				return 10;

			case CLUBBER:
				return 20;

			case BARTENDER:
				return 30;

			case PARTYMAN:
				return 40;

			case PREMIUM:
				return 50;

			case VIP:
				return 30;

			case YOUTUBER:
				return 15;

			// YouTubers and shit get unlimited
			case ADMIN:
			case PLAT_ADMIN:
			case SR_ADMIN:
			case DEVELOPER:
			case OWNER:
				return 1337;

			// Make IntelliJ happy
			default:
				return 0;
		}
	}

}
