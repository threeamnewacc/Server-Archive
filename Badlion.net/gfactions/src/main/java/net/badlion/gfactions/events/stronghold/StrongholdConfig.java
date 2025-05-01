package net.badlion.gfactions.events.stronghold;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import net.badlion.common.libraries.DateCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gfactions.Config;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.tasks.CheckTimeTask;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.joda.time.DateTime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class StrongholdConfig extends Config {

	private int strongholdEventId;

	private List<DateTime> eventTimes = new ArrayList<>();
	private Set<Faction> blacklistedFactions = new HashSet<>();

	// Administration
	private boolean announceCapper;
	private boolean lootEnabled;
	private boolean captureEnabled;
	private boolean buffsEnabled;
	private List<String> eventRegions = new ArrayList<>();

	// Balance
	private int blacklistCaptureTimeModifier;
	private double captureStealValue;
	private int passiveLootDropInterval;
	private double passiveLootDropChanceOverExtendedBuff;
	private double passiveLootDropChanceModifiersSum;
	private int captureScore;
	private int passiveScoreIncrease;
	private int passiveScoreDistribution;
	private int captureLength;
	private int underPopulationLimit;
	private int overPopulationLimit;
	private double captureLengthOverExtendedOffense;
	private double captureLengthOverExtendedDefense;
	private double captureLengthOverPopulationBuff;
	private double captureLengthUnderPopulationBuff;
	private double deteriorationBuff;
	private int deteriorationMoney;
	private Map<ItemStack, Integer> deteriorationItems;

    private long deathBanTime;

	public StrongholdConfig(String fileName) {
		super(fileName);

		// Load config
		this.load();

		// Add to check time component
		CheckTimeTask.addCheckTimeComponent(new CheckTimeTask.CheckTimeComponent() {
			@Override
			public void run(CheckTimeTask task) {
				if (GFactions.plugin.getStronghold() == null) {
					DateTime now = DateTime.now();
					for (DateTime dt : StrongholdConfig.this.eventTimes) {
						// The datetimes are almost never going to be equal, check day of week and minute of day
						//if (now.getDayOfWeek() == dt.getDayOfWeek() && now.getMinuteOfDay() == dt.getMinuteOfDay() - 10) {
						if (now.getDayOfWeek() == dt.getDayOfWeek() && now.getSecondOfDay() == dt.getSecondOfDay() - 15) {
							GFactions.plugin.getServer().dispatchCommand(GFactions.plugin.getServer().getConsoleSender(), "stronghold start");
							break;
						}
					}
				}
			}
		});
	}

	@Override
	public void load() {
		// Don't reload config if a stronghold is running
		if (GFactions.plugin.getStronghold() != null) {
			return;
		}

		World world = GFactions.plugin.getSpawnLocation().getWorld();

		// Load times
		this.eventTimes.clear();
		for (String time : this.config.getStringList("event_times")) {
			DateTime dateTime = DateCommon.parseDateTime(time);
			if (dateTime != null) {
				this.eventTimes.add(dateTime.withSecondOfMinute(0).withMillisOfSecond(0));
			} else {
				dateTime = DateCommon.parseDayTime(time);
				if (dateTime != null) {
					this.eventTimes.add(dateTime.withSecondOfMinute(0).withMillisOfSecond(0));
				} else {
					GFactions.plugin.getServer().getLogger().severe("Unable to parse event time in supermine config: " + time);
				}
			}
		}

		// Administration
		this.announceCapper = this.config.getBoolean("administration.announce_capper");
		this.lootEnabled = this.config.getBoolean("administration.loot_on");
		this.buffsEnabled = this.config.getBoolean("administration.buffs_on");
		this.captureEnabled = this.config.getBoolean("administration.capture_on");
		for (String eventRegion : this.config.getStringList("administration.event_regions")) {
			this.eventRegions.add(eventRegion);
		}

		// Balance
		this.blacklistCaptureTimeModifier = this.config.getInt("balance.blacklist_capture_time_modifier");
		this.captureStealValue = this.config.getDouble("balance.capture_steal_value");
		this.passiveLootDropInterval = this.config.getInt("balance.passive_loot_drop_interval");
		this.passiveLootDropChanceOverExtendedBuff = this.config.getDouble("balance.passive_loot_drop_chance_over_extended_buff");
		for (String modifier : this.config.getStringList("balance.passive_loot_drop_chance_modifiers_keys")) {
			this.passiveLootDropChanceModifiersSum += this.config.getDouble("balance.passive_loot_drop_chance_modifiers." + modifier);
		}
		this.captureScore = this.config.getInt("balance.capture_score");
		this.passiveScoreIncrease = this.config.getInt("balance.passive_score_increase");
		this.passiveScoreDistribution = this.config.getInt("balance.passive_score_distribution");
		this.captureLength = this.config.getInt("balance.capture_length");
		this.underPopulationLimit = this.config.getInt("balance.under_population_limit");
		this.overPopulationLimit = this.config.getInt("balance.over_population_limit");
		this.captureLengthOverExtendedOffense = this.config.getDouble("balance.capture_length_modifiers.over_extended_offense");
		this.captureLengthOverExtendedDefense = this.config.getDouble("balance.capture_length_modifiers.over_extended_defense");
		this.captureLengthOverPopulationBuff = this.config.getDouble("balance.capture_length_modifiers.over_population_buff");
		this.captureLengthUnderPopulationBuff = this.config.getDouble("balance.capture_length_modifiers.under_population_buff");
		this.deteriorationBuff = this.config.getDouble("balance.capture_length_modifiers.deterioration_buff");

        this.deathBanTime = this.config.getLong("deathban_time");

		// Load deterioration items
		this.deteriorationMoney = this.config.getInt("balance.deterioration_money");
		this.deteriorationItems = new HashMap<>();
		for (String serializedItem : this.config.getStringList("balance.deterioration_items")) {
			try {
				String[] itemComponents = serializedItem.split(":");
				ItemStack item = new ItemStack(Material.valueOf(itemComponents[0]), 1, Short.valueOf(itemComponents[1]));
				this.deteriorationItems.put(item, Integer.valueOf(itemComponents[2]));
			} catch (Exception exception) {
				GFactions.plugin.getLogger().severe("Invalid item and amount specified for " + serializedItem + ". Fix config and reload to fix error!!!");
				GFactions.plugin.getLogger().severe("Invalid item and amount specified for " + serializedItem + ". Fix config and reload to fix error!!!");
				GFactions.plugin.getLogger().severe("Invalid item and amount specified for " + serializedItem + ". Fix config and reload to fix error!!!");
			}
		}

		// Load keeps
		Keep.flushKeeps();
		for (String keepName : this.config.getStringList("keeps_keys")) {
			// Create keep
			Keep keep = new Keep(this.config.getString("keeps." + keepName + ".name").replaceAll("&", "§"),
					this.config.getBoolean("keeps." + keepName + ".balance.loot_on"),
					this.passiveLootDropChanceModifiersSum,
					GFactions.plugin.parseLocation(this.config.getString("keeps." + keepName + ".capture_point.corner1")),
					GFactions.plugin.parseLocation(this.config.getString("keeps." + keepName + ".capture_point.corner2")),
					GFactions.plugin.parseLocation(this.config.getString("keeps." + keepName + ".deterioration_sign")));


			// Add loot chests
			for (String location : this.config.getStringList("keeps." + keepName + ".loot_drop_locations_keys")) {
				keep.addLootChest(GFactions.plugin.parseLocation(this.config.getString("keeps." + keepName + ".loot_drop_locations." + location)));
			}

			// Add doors
			for (String doorName : this.config.getStringList("keeps." + keepName + ".doors_keys")) {
				// Get the lever blocks
				Block lever1 = world.getBlockAt(GFactions.plugin.parseLocation(this.config.getString("keeps." + keepName + ".doors." + doorName + ".lever1")));
				Block lever2 = world.getBlockAt(GFactions.plugin.parseLocation(this.config.getString("keeps." + keepName + ".doors." + doorName + ".lever2")));

				// Get the door blocks now and store them
				List<Block> blocks = new ArrayList<>();
				// Organize all these numbers so we go for (low : high, int++)
				int x = this.config.getInt("keeps." + keepName + ".doors." + doorName + ".x");
				int x1 = this.config.getInt("keeps." + keepName + ".doors." + doorName + ".x1");
				int y = this.config.getInt("keeps." + keepName + ".doors." + doorName + ".y");
				int y1 = this.config.getInt("keeps." + keepName + ".doors." + doorName + ".y1");
				int z = this.config.getInt("keeps." + keepName + ".doors." + doorName + ".z");
				int z1 = this.config.getInt("keeps." + keepName + ".doors." + doorName + ".z1");
				if (x > x1) {
					int temp = x1;
					x1 = x;
					x = temp;
				}
				if (y > y1) {
					int temp = y1;
					y1 = y;
					y = temp;
				}
				if (z > z1) {
					int temp = z1;
					z1 = z;
					z = temp;
				}
				for (int a = x; a <= x1; a++) {
					for (int b = y; b <= y1; b++) {
						for (int c = z; c <= z1; c++) {
							blocks.add(world.getBlockAt(a, b, c));
						}
					}
				}

				keep.addDoor(Material.valueOf(this.config.getString("keeps." + keepName + ".doors." + doorName + ".material")),
						(byte) this.config.getInt("keeps." + keepName + ".doors." + doorName + ".data"),
						lever1, lever2, blocks);
			}
		}

		// SQL Stuff - Risk of CME's here but should never be hit unless config reloaded twice in a row very quickly
		final Map<String, Integer> keepOwnerIds = new HashMap<>();
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				String query = "";

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					// Get the last owners of the keeps
					query = "SELECT stronghold_id, ending_owners FROM " + GFactions.PREFIX + "_stronghold_events ORDER BY stronghold_id DESC LIMIT 1;";

					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					rs = Gberry.executeQuery(connection, ps);

					if (rs.next()) {
						// Stronghold id
						StrongholdConfig.this.strongholdEventId = rs.getInt(1) + 1;

						String[] keepOwners = rs.getString(2).split(",");
						for (String keepOwner : keepOwners) {
							String[] components = keepOwner.split(":");
							if (!components[1].equals("none")) {
								keepOwnerIds.put(components[0], Integer.valueOf(components[1]));
							} else {
								keepOwnerIds.put(components[0], 0);
							}
						}

						BukkitUtil.runTask(new Runnable() {
							@Override
							public void run() {
								for (Keep keep : Keep.getKeeps()) {
									keep.setPreviousOwner(keepOwnerIds.get(keep.getName()));
								}
							}
						});
					} else {
						// Stronghold id
						StrongholdConfig.this.strongholdEventId = 1;
					}

					// Now get the deterioration paid info for each owner
					query = "SELECT * FROM " + GFactions.PREFIX + "_stronghold_deterioration WHERE stronghold_id = ?;";

					ps = connection.prepareStatement(query);

					ps.setInt(1, StrongholdConfig.this.strongholdEventId - 1);

					rs = ps.executeQuery();

					while (rs.next()) {
						String keepName = rs.getString("keep_name");

						// Figure out which keep this is for
						for (Keep keep : Keep.getKeeps()) {
							if (keep.getName().equals(keepName)) {
								// Money
								keep.setDeteriorationMoneyOwed(rs.getInt(3));

								// Items
								String str = rs.getString(4);
								if (str.length() > 0) {
									String[] itemsOwed = str.split(",");
									for (String serializedItem : itemsOwed) {
										String[] itemComponents = serializedItem.split(":");
										ItemStack item = new ItemStack(Material.valueOf(itemComponents[0]), 1, Short.valueOf(itemComponents[1]));
										keep.getDeteriorationItemsOwed().put(item, Integer.valueOf(itemComponents[2]));
									}
								}

								keep.checkDeteriorationApplied(null);
								break;
							}
						}
					}

					// Get all blacklisted factions
					query = "SELECT * FROM " + GFactions.PREFIX + "_stronghold_blacklists";

					ps = connection.prepareStatement(query);
					rs = ps.executeQuery();

					while (rs.next()) {
						StrongholdConfig.this.blacklistedFactions.add(Factions.i.get(rs.getInt(1) + ""));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
				}
			}
		});
	}

	public String serializeDeteriorationItemsOwed() {
		StringBuilder sb = new StringBuilder();
		for (ItemStack item : GFactions.plugin.getStrongholdConfig().getDeteriorationItems().keySet()) {
			sb.append(item.getType());
			sb.append(":");
			sb.append(item.getDurability());
			sb.append(":");
			sb.append(GFactions.plugin.getStrongholdConfig().getDeteriorationItems().get(item));
			sb.append(",");
		}
		sb.setLength(sb.length() - 1);

		return sb.toString();
	}

	public String serializeDeteriorationItemsOwed(Map<ItemStack, Integer> deteriorationItemsOwed) {
		StringBuilder sb = new StringBuilder();
		for (ItemStack item : deteriorationItemsOwed.keySet()) {
			sb.append(item.getType());
			sb.append(":");
			sb.append(item.getDurability());
			sb.append(":");
			sb.append(deteriorationItemsOwed.get(item));
			sb.append(",");
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
			return sb.toString();
		}

		return "";
	}

	public int getStrongholdEventId() {
		return strongholdEventId;
	}

	public Set<Faction> getBlacklistedFactions() {
		return blacklistedFactions;
	}

	public boolean isAnnounceCapper() {
		return announceCapper;
	}

	public boolean isLootEnabled() {
		return lootEnabled;
	}

	public boolean isCaptureEnabled() {
		return captureEnabled;
	}

	public boolean isBuffsEnabled() {
		return buffsEnabled;
	}

	public List<String> getEventRegions() {
		return eventRegions;
	}

	public double getCaptureStealValue() {
		return captureStealValue;
	}

	public int getPassiveLootDropInterval() {
		return passiveLootDropInterval;
	}

	public double getPassiveLootDropChanceOverExtendedBuff() {
		return passiveLootDropChanceOverExtendedBuff;
	}

	public double getPassiveLootDropChanceModifiersSum() {
		return passiveLootDropChanceModifiersSum;
	}

	public int getCaptureScore() {
		return captureScore;
	}

	public int getPassiveScoreIncrease() {
		return passiveScoreIncrease;
	}

	public int getPassiveScoreDistribution() {
		return passiveScoreDistribution;
	}

	public int getCaptureLength(Faction faction) {
		if (faction != null && this.blacklistedFactions.contains(faction)) {
			return captureLength + this.blacklistCaptureTimeModifier;
		}

		return captureLength;
	}

	public int getUnderPopulationLimit() {
		return underPopulationLimit;
	}

	public int getOverPopulationLimit() {
		return overPopulationLimit;
	}

	public double getCaptureLengthOverExtendedOffense() {
		return captureLengthOverExtendedOffense;
	}

	public double getCaptureLengthOverExtendedDefense() {
		return captureLengthOverExtendedDefense;
	}

	public double getCaptureLengthOverPopulationBuff() {
		return captureLengthOverPopulationBuff;
	}

	public double getCaptureLengthUnderPopulationBuff() {
		return captureLengthUnderPopulationBuff;
	}

	public double getDeteriorationBuff() {
		return deteriorationBuff;
	}

	public int getDeteriorationMoney() {
		return deteriorationMoney;
	}

	public Map<ItemStack, Integer> getDeteriorationItems() {
		return deteriorationItems;
	}

    public long getDeathBanTime() {
        return deathBanTime;
    }
}
