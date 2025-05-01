package net.badlion.premium;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.badlion.common.libraries.StringCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gpermissions.GPermissions;
import net.badlion.premium.utils.PurchaseUtil;
import net.buycraft.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// Reference for more advanced program: http://forums.bukkit.org/threads/serializing-itemmeta-and-all-your-wildest-dreams.137325/

public class Premium extends JavaPlugin {

	private static HikariConfig hikariConfig;
	private static HikariDataSource hikariDataSource;

    private Set<String> donatorGroups = new HashSet<>();

	private static Plugin buycraftPlugin;
	
	@Override
	public void onEnable() {
		Gberry.throwNonAsyncErrors = false;

        this.donatorGroups.add("donator");
        this.donatorGroups.add("donatorplus");
        this.donatorGroups.add("lion");

		// SQL Connection
		String url = "jdbc:postgresql://" + this.getConfig().getString("host") + "/" + this.getConfig().getString("db");

		Premium.hikariConfig = new HikariConfig();
		Premium.hikariConfig.setJdbcUrl(url);
		Premium.hikariConfig.setUsername(this.getConfig().getString("user"));
		Premium.hikariConfig.setPassword(this.getConfig().getString("pass"));
		Premium.hikariConfig.setConnectionTimeout(10 * 1000);
		Premium.hikariConfig.setIdleTimeout(120 * 1000);
		Premium.hikariConfig.setMaxLifetime(300 * 1000);
		Premium.hikariConfig.setMinimumIdle(1);
		Premium.hikariConfig.setMaximumPoolSize(20);

		try {
			Premium.hikariDataSource = new HikariDataSource(Premium.hikariConfig);
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
		}

		Premium.buycraftPlugin = (Plugin) this.getServer().getPluginManager().getPlugin("Buycraft");

        new BukkitRunnable() {
            public void run() {
				if (!Premium.buycraftPlugin.isAuthenticated(null)) {
					Premium.this.getServer().dispatchCommand(Premium.this.getServer().getConsoleSender(), "stop");
					return;
				}

                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "buycraft forcecheck");
            }
        }.runTaskTimer(this, 20 * 60, 20 * 60);

		Gberry.loggingTags.add("CASES");
	}
	
	@Override
	public void onDisable() {

	}
	
	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
		if (command.getName().equalsIgnoreCase("addpremiumranked")) {
			Premium.this.addRank(args, "donator");
  		} else if (command.getName().equalsIgnoreCase("removepremiumranked")) {
			Premium.this.removeRank(args[0], args[1], false);
  		} else if (command.getName().equalsIgnoreCase("adddonatorplus")) {
			Premium.this.addRank(args, "donatorplus");
        } else if (command.getName().equalsIgnoreCase("removedonatorplus")) {
			Premium.this.removeRank(args[0], args[1], false);
        } else if (command.getName().equalsIgnoreCase("addlion")) {
			Premium.this.addRank(args, "lion");
        } else if (command.getName().equalsIgnoreCase("removelion")) {
			Premium.this.removeRank(args[0], args[1], false);
        } else if (command.getName().equalsIgnoreCase("expirepremiumranked")) {
			if (args.length == 2) {
				Premium.this.expireRank(args[0], args[1]);
			} else {
				Premium.this.expireRank(args[0], null);
			}
  		} else if (command.getName().equalsIgnoreCase("givecases")) {
			Premium.this.giveCases(args);
		} else if (command.getName().equalsIgnoreCase("removecases")) {
			Premium.this.removeCases(args);
		} else if (command.getName().equalsIgnoreCase("uhcstatreset")) {
			Premium.this.addUHCStatReset(args[0]);
        } else if (command.getName().equalsIgnoreCase("arenapvpstatreset")) {
			Premium.this.addArenaPvPEloReset(args[0]);
        } else if (command.getName().equalsIgnoreCase("coloredname")) {
			getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[0] + " addperm ColorName.changecolor");
  		} else if (command.getName().equalsIgnoreCase("removecoloredname")) {
			getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[0] + " rmperm ColorName.changecolor");
		} else if (command.getName().equalsIgnoreCase("chargeback")) {
			Premium.this.removeRank(args[0], args[1], true);
		} else if (command.getName().equalsIgnoreCase("refund")) {
			Premium.this.removeRank(args[0], args[1], false);
		} else if (command.getName().equalsIgnoreCase("addlives")) {
			Premium.this.addLives(args);
		} else if (command.getName().equalsIgnoreCase("removelives")) {
			Premium.this.removeLives(args);
		}
		return true;

  	}

	public static Connection getConnection() throws SQLException {
		try {
			return hikariDataSource.getConnection();
		} catch (SQLException e) {
			try {
				// Try one more time at max (load of kitpvp servers a lot gets overloaded)
				return hikariDataSource.getConnection();
			} catch (SQLTimeoutException e2) {
				// Can we not grab a connection and has the server just rebooted?
				if (Gberry.plugin.getServerUptime() < 60000) {
					e2.printStackTrace();
					Bukkit.getLogger().severe("UNABLE TO GRAB A DATABASE CONNECTION, STOPPING SERVER");
					Bukkit.getLogger().severe("UNABLE TO GRAB A DATABASE CONNECTION, STOPPING SERVER");
					Bukkit.getLogger().severe("UNABLE TO GRAB A DATABASE CONNECTION, STOPPING SERVER");

					Gberry.plugin.getServer().dispatchCommand(Gberry.plugin.getServer().getConsoleSender(), "stop");
				}

				throw e2;
			}
		}
	}

	// args = [uuid, days, price, transaction_id, username, currency, email, ip, package_id, package_price, date, time]
	public void addRank(String[] args, final String rank) {
		UUID uuid = StringCommon.uuidFromStringWithoutDashes(args[0]);

		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			connection = Gberry.getUnsafeConnection();

			// Update forum information
			String sql = "SELECT * FROM users WHERE uuid = ?";
			ps = connection.prepareStatement(sql);
			ps.setString(1, uuid.toString());
			rs = Gberry.executeQuery(connection, ps);

			int websiteRankId = 10;
			if (rank.equalsIgnoreCase("lion")) {
				websiteRankId = 15;
			} else if (rank.equalsIgnoreCase("donatorplus")) {
				websiteRankId = 12;
			}

			if (rs.next()) {
				int id = rs.getInt("id");
				sql = "UPDATE roles_users SET role_id = ? WHERE user_id = ?";
				ps = connection.prepareStatement(sql);
				ps.setInt(1, websiteRankId);
				ps.setInt(2, id);
				Gberry.executeUpdate(connection, ps);
			}

			// Get the purchase info
			double price = Double.valueOf(args[2]);
			String transactionId = args[3];
			String username = args[4];
			String currency = args[5];

			// NOTE: We have to parse their date time or we have 5k players with extra ranks -.-
			DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yy HH:mm").withZone(DateTimeZone.UTC);
			DateTime timestamp = DateTime.parse(args[10] + " " + args[11], formatter);

			//DateTime timestamp = new DateTime(DateTimeZone.UTC);
			String email = args[6];
			String ip = args[7];
			int packageId = Integer.valueOf(args[8]);
			double packagePrice = Double.valueOf(args[9]);

			final DateTime expiration = args[1].equalsIgnoreCase("365") ? null : timestamp.plusDays(Integer.parseInt(args[1]));

			// Insert the purchase info
			PurchaseUtil.insertPurchase(new Purchase(
					transactionId, uuid, username, price, currency, timestamp, email, ip, packageId, packagePrice, expiration
			));

			// Give them the rank
			final UUID finalUUID = uuid;
			final String groupName = GPermissions.plugin.getUserGroupOffline(uuid.toString());
			if (Premium.this.donatorGroups.contains(groupName) || groupName.equals("default")) {
				if (expiration == null) {
					getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + finalUUID.toString() + " buygroup " + rank);
				} else {
					getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + finalUUID.toString() + " setgroup " + rank);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Something broke");
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}
	}

	public void removeRank(String uuidString, String transactionId, boolean isChargeback) {
		final UUID uuid = StringCommon.uuidFromStringWithoutDashes(uuidString);

		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			connection = Gberry.getUnsafeConnection();

			// Update rank/forum stuff only if it was rank purchase
			Purchase purchase = PurchaseUtil.getPurchase(transactionId);

			if (purchase == null) {
				this.getLogger().severe("Purchase not found for " + uuidString + " with transaction ID " + transactionId);
			}

			String sql = "SELECT * FROM users WHERE uuid = ?;";
			ps = connection.prepareStatement(sql);
			ps.setString(1, uuid.toString());
			rs = ps.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id");
				sql = "UPDATE roles_users SET role_id = 1 WHERE user_id = ?;";
				ps = connection.prepareStatement(sql);
				ps.setInt(1, id);
				ps.executeUpdate();
			}

			getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + uuid.toString() + " setgroup default");

			PurchaseUtil.updatePurchases(transactionId, (isChargeback ? "chargeback" : "refund"));

			if (isChargeback) {
				getServer().dispatchCommand(getServer().getConsoleSender(), "ban " + uuid.toString() + " Chargebacks are not allowed");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Something broke");
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}
	}

	public void expireRank(String uuidString, String transactionId) {
		final UUID uuid = StringCommon.uuidFromStringWithoutDashes(uuidString);

		final List<Purchase> purchases = PurchaseUtil.getPurchases(uuid);
		int currentRankPurchaseId = -1337;
		Purchase highestTempRankPurchase = null;
		// 1 hour offset here to force transactions to expire incase Buycraft run's their shit too fast
		DateTime now = new DateTime(DateTimeZone.UTC).plusHours(1);
		for (int i = 0; i < purchases.size(); i++) {
			Purchase purchase = purchases.get(i);

			if (currentRankPurchaseId == -1337 && PurchaseUtil.isTempRankPurchase(purchase)) {
				currentRankPurchaseId = purchase.getPackageId();
			}

			// Just assume that this is the most recent transaction (terrible decision but only an issue for 30 days)
			if (transactionId == null && PurchaseUtil.isTempRankPurchase(purchase)) {
				break;
			}

			// This is the most recent transaction, ez logic
			if (i == 0 && purchase.getTransactionId().equalsIgnoreCase(transactionId)) {
				break;
			}

			// They have another temporary rank that expires later
			if (PurchaseUtil.isTempRankPurchase(purchase) && purchase.getPackageExpirationDate() != null && purchase.getPackageExpirationDate().isAfter(now)) {
				if (highestTempRankPurchase == null || purchase.getPackageId() > highestTempRankPurchase.getPackageId()) {
					highestTempRankPurchase = purchase;
				}
			}
		}

		// If they aren't even a donator right now bail out (staff/youtube etc)
		final String groupName = GPermissions.plugin.getUserGroupOffline(uuid.toString());
		if (!Premium.this.donatorGroups.contains(groupName)) {
			return;
		}

		// Do they have an unexpired rank?
		if (highestTempRankPurchase != null) {
			// Is their unexpired rank not the same as their current rank?
			if (currentRankPurchaseId != highestTempRankPurchase.getPackageId()) {
				// TODO: BIG FAT TODO: Update forum roles if we ever discriminate between donators on forums
				final Purchase finalHighestRankPurchase = highestTempRankPurchase;
				Premium.this.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + uuid.toString() + " setgroup " + PurchaseUtil.getRankStringFromPackageID(finalHighestRankPurchase.getPackageId()));
			}

			return;
		}

		// TEMP: Not sure if we wanna do this yet
		// Check if they earned a perma rank or had one before
//		final String lifeTimeRank = PurchaseUtil.checkLifetimeRanks(uuid);
//		if (lifeTimeRank != null) {
//			// If they don't have the perm permission give it to them
//			final String permission = "badlion." + lifeTimeRank + "perm";
//			final boolean givePermission = !GPermissions.getInstance().hasUserPermissionOffline(uuid.toString(), permission);
//
//			new BukkitRunnable() {
//				public void run() {
//					getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + uuid.toString() + " setgroup " + lifeTimeRank);
//					if (givePermission) {
//						getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + uuid.toString() + " addperm " + permission);
//					}
//				}
//			}.runTask(Premium.this);
//
//			return;
//		}

		// Temp lifetime rank system
		if (GPermissions.getInstance().hasUserPermissionOffline(uuid.toString(), "badlion.lionperm")) {
			getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + uuid.toString() + " setgroup lion");
			return;
		} else if (GPermissions.getInstance().hasUserPermissionOffline(uuid.toString(), "badlion.donatorplusperm")) {
			getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + uuid.toString() + " setgroup donatorplus");
			return;
		} else if (GPermissions.getInstance().hasUserPermissionOffline(uuid.toString(), "badlion.donatorperm")) {
			getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + uuid.toString() + " setgroup donator");
			return;
		}
		// End temp lifetime rank system

		// If we made it this far they have NOTHING
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			connection = Gberry.getUnsafeConnection();

			// Update forum information
			String sql = "SELECT * FROM users WHERE uuid = ?;";
			ps = connection.prepareStatement(sql);
			ps.setString(1, uuid.toString());
			rs = ps.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id");
				sql = "UPDATE roles_users SET role_id = 1 WHERE user_id = ?;";
				ps = connection.prepareStatement(sql);
				ps.setInt(1, id);
				ps.executeUpdate();
			}

			getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + uuid.toString() + " setgroup default");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Something broke");
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}
	}

    public void addArenaPvPEloReset(String uuidString) {
        String query = "INSERT INTO elo_resets (uuid) VALUES (?);";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        UUID uuid = StringCommon.uuidFromStringWithoutDashes(uuidString);

        try {
            connection = Gberry.getUnsafeConnection();
            ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, uuid.toString());

            Gberry.executeUpdate(connection, ps);
            rs = ps.getGeneratedKeys();

            if (rs.next()) {
                List<String> args = new ArrayList<>(Arrays.asList(new String[] {"ResetEloSync", uuid.toString(), "" + ps.getGeneratedKeys().getInt("reset_id")}));
                Gberry.sendGSyncEvent(args);
            }
        } catch (SQLException e) {
            e.printStackTrace();
			throw new RuntimeException("Something broke");
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }
    }

    public void addUHCStatReset(String uuidString) {
        String query = "INSERT INTO uhc_stat_resets (uuid) VALUES (?);";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        UUID uuid = StringCommon.uuidFromStringWithoutDashes(uuidString);

        try {
            connection = Gberry.getUnsafeConnection();
            ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, uuid.toString());

            Gberry.executeUpdate(connection, ps);
            rs = ps.getGeneratedKeys();

            if (rs.next()) {
                List<String> args = new ArrayList<>(Arrays.asList(new String[] {"UHC", "statreset", uuid.toString(), "" + ps.getGeneratedKeys().getInt("reset_id")}));
                Gberry.sendGSyncEvent(args);
            }
        } catch (SQLException e) {
            e.printStackTrace();
			throw new RuntimeException("Something broke");
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }
    }

	public void giveCases(String[] args) { // /givecases uuid, type, total, rare, super rare, legendary,  transaction_id, price, username, currency, email, ip, package_id, package_price, date, time]
		String transactionID = args[6];
		String uuidString = args[0];
		UUID uuid = StringCommon.uuidFromStringWithoutDashes(uuidString);

		Gberry.ServerType server = Gberry.ServerType.valueOf(args[1]);

		int cases = Integer.valueOf(args[2]);
		int rareCases = Integer.valueOf(args[3]);
		int superRareCases = Integer.valueOf(args[4]);
		int legendaryCases = Integer.valueOf(args[5]);

		UserDataManager.UserData userData = UserDataManager.getUserData(uuid);
		if (userData == null) {
			userData = UserDataManager.getUserDataFromDB(uuid);
		}

		JSONObject casesObject = userData.getCases();

		// Get the purchase info
		double price = Double.valueOf(args[7]);
		String username = args[8];
		String currency = args[9];

		// NOTE: We have to parse their date time or we have 5k players with extra ranks -.-
		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yy HH:mm").withZone(DateTimeZone.UTC);
		DateTime timestamp = DateTime.parse(args[14] + " " + args[15], formatter);

		//DateTime timestamp = new DateTime(DateTimeZone.UTC);
		String email = args[10];
		String ip = args[11];
		int packageId = Integer.valueOf(args[12]);
		double packagePrice = Double.valueOf(args[13]);

		// Insert the purchase info
		PurchaseUtil.insertPurchase(new Purchase(
			transactionID, uuid, username, price, currency, timestamp, email, ip, packageId, packagePrice, null
		));

		List<Map<String, Object>> casesList = (List<Map<String, Object>>) casesObject.get(server.getInternalName() + "_cases");

		if (casesList == null) {
			casesList = new ArrayList<>();
		}

		Map<String, Object> casesMap = new LinkedHashMap<>();
		casesMap.put("transaction_id", transactionID);

		casesMap.put("total_cases", cases);
		casesMap.put("opened_cases", 0);
		casesMap.put("prizes", new LinkedList<String>());

		casesMap.put("rare_cases", rareCases);
		casesMap.put("super_rare_cases", superRareCases);
		casesMap.put("legendary_cases", legendaryCases);

		casesMap.put("rare_items_received", 0);
		casesMap.put("super_rare_items_received", 0);
		casesMap.put("legendary_items_received", 0);
		casesList.add(casesMap);
		casesObject.put(server.getInternalName() + "_cases", casesList);

		PGobject jsonWrapper = new PGobject();
		jsonWrapper.setType("json");

		Gberry.log("CASES", casesObject.toJSONString());

		userData.setCases(casesObject, true);

		List<String> list = new ArrayList<>(
				Arrays.asList(
						new String[]{"Cases", "add", uuid.toString(), server.name(), transactionID, String.valueOf(cases), String.valueOf(rareCases), String.valueOf(superRareCases), String.valueOf(legendaryCases)}
				)
		);
		Gberry.sendGSyncEvent(list);
	}

	@SuppressWarnings("unchecked")
	public void removeCases(String[] args) { // /removecases <uuid> <type> <transaction_id> <chargeback>
		String transactionID = args[2];
		String uuidString = args[0];
		final UUID uuid = StringCommon.uuidFromStringWithoutDashes(uuidString);

		Gberry.ServerType server = Gberry.ServerType.valueOf(args[1]);

		// Purchase tracking
		boolean isChargeback = args.length > 3 && args[3].equalsIgnoreCase("true");
		PurchaseUtil.updatePurchases(transactionID, (isChargeback ? "chargeback" : "refund"));

		if (isChargeback) {
			getServer().dispatchCommand(getServer().getConsoleSender(), "ban " + uuid.toString() + " Chargebacks are not allowed");
		}

		UserDataManager.UserData userData = UserDataManager.getUserData(uuid);
		if (userData == null) {
			userData = UserDataManager.getUserDataFromDB(uuid);
		}

		JSONObject casesObject = userData.getCases();

		List<Map<String, Object>> casesList = (List<Map<String, Object>>) casesObject.get(server.getInternalName() + "_cases");
		if (casesList == null) {
			Gberry.log("CASES", "Cases for " + transactionID + " could not be found on uuid " + uuidString);
			throw new RuntimeException("Something broke");
		}

		Map<String, Object> casesMap = null;
		for (Map<String, Object> map : casesList) {
			if (map.get("transaction_id").equals(transactionID)) {
				casesMap = map;
				break;
			}
		}

		if (casesMap == null) {
			Gberry.log("CASES", "Cases for UUID - " + uuid.toString() + " with Transaction ID - " + transactionID + " for server type - " + server + " was not found! Cannot remove from " + transactionID);
			throw new RuntimeException("Something broke");
		}

		casesMap.put("opened_cases", casesMap.get("total_cases")); // YOLO

		JSONObject cosmetics = userData.getCosmetics();
		List<String> prizes = (List<String>) casesMap.get("prizes");
		for (String prize : prizes) {
		 	cosmetics.remove(prize);

			// Sanity sakes see if they have it armed as a current thing in use
			String[] parts = prize.split("-");
			if (cosmetics.containsKey("particle-active") && cosmetics.get("particle-active") != null && cosmetics.get("particle-active").equals(parts[1])) {
				cosmetics.remove("particle-active");
			}

			if (cosmetics.containsKey("pet-active") && cosmetics.get("pet-active") != null && cosmetics.get("pet-active").equals(parts[1])) {
				cosmetics.remove("pet-active");
			}

			if (cosmetics.containsKey("morph-active") && cosmetics.get("morph-active") != null && cosmetics.get("morph-active").equals(parts[1])) {
				cosmetics.remove("morph-active");
			}

			if (cosmetics.containsKey("arrow_trail-active") && cosmetics.get("arrow_trail-active") != null && cosmetics.get("arrow_trail-active").equals(parts[1])) {
				cosmetics.remove("arrow_trail-active");
			}
		}

		// Sync to cosmetics to remove
		List<String> list = new ArrayList<>();
		list.add("Cosmetics");
		list.add("remove");
		list.add(uuid.toString());
		list.addAll(prizes);
		Gberry.sendGSyncEvent(list);

		userData.setCases(casesObject, true);
		userData.setCosmetics(cosmetics, true);

		list = new ArrayList<>(
				Arrays.asList(
						new String[]{"Cases", "remove", uuid.toString(), server.name(), transactionID}
				)
		);
		list.addAll(prizes);

		Gberry.sendGSyncEvent(list);

		Gberry.log("CASES", "Removed cases for " + transactionID + " " + uuidString);
	}

	public void addLives(String[] args) { // /addlives uuid, num, transaction_id, price, username, currency, email, ip, package_id, package_price, date, time]
		String transactionID = args[2];
		String uuidString = args[0];
		UUID uuid = StringCommon.uuidFromStringWithoutDashes(uuidString);

		int numOfLives = Integer.parseInt(args[1]);

		// Get the purchase info
		double price = Double.valueOf(args[3]);
		String username = args[4];
		String currency = args[5];

		// NOTE: We have to parse their date time or we have 5k players with extra ranks -.-
		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yy HH:mm").withZone(DateTimeZone.UTC);
		DateTime timestamp = DateTime.parse(args[10] + " " + args[11], formatter);

		//DateTime timestamp = new DateTime(DateTimeZone.UTC);
		String email = args[6];
		String ip = args[7];
		int packageId = Integer.valueOf(args[8]);
		double packagePrice = Double.valueOf(args[9]);

		// Insert the purchase info
		PurchaseUtil.insertPurchase(new Purchase(
				transactionID, uuid, username, price, currency, timestamp, email, ip, packageId, packagePrice, null
		));

		Connection connection = null;
		PreparedStatement ps = null;

		String query = "UPDATE usfactions_num_of_lives SET num_of_lives = num_of_lives + ? WHERE uuid = ?;\n";
		query += "INSERT INTO usfactions_num_of_lives (uuid, num_of_lives) SELECT ?, ? WHERE NOT EXISTS " +
						 "(SELECT 1 FROM usfactions_num_of_lives WHERE uuid = ?);";

		try {
			connection = Gberry.getUnsafeConnection();
			ps = connection.prepareStatement(query);

			ps.setInt(1, numOfLives);
			ps.setString(2, uuid.toString());
			ps.setString(3, uuid.toString());
			ps.setInt(4, numOfLives);
			ps.setString(5, uuid.toString());

			Gberry.executeUpdate(connection, ps);

			List<String> gsyncArgs = new ArrayList<>(Arrays.asList(new String[] {"Factions", "addlives", uuid.toString(), "" + numOfLives}));
			Gberry.sendGSyncEvent(gsyncArgs);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Something broke");
		} finally {
			Gberry.closeComponents(ps, connection);
		}
	}

	public void removeLives(String[] args) { // /removelives <uuid> <num> <transaction_id> <chargeback>
		String transactionID = args[2];
		String uuidString = args[0];
		final UUID uuid = StringCommon.uuidFromStringWithoutDashes(uuidString);

		int numOfLives = Integer.parseInt(args[1]);

		// Purchase tracking
		boolean isChargeback = args.length > 3 && args[3].equalsIgnoreCase("true");
		PurchaseUtil.updatePurchases(transactionID, (isChargeback ? "chargeback" : "refund"));

		if (isChargeback) {
			getServer().dispatchCommand(getServer().getConsoleSender(), "ban " + uuid.toString() + " Chargebacks are not allowed");
		}

		Connection connection = null;
		PreparedStatement ps = null;

		String query = "UPDATE usfactions_num_of_lives SET num_of_lives = num_of_lives + ? WHERE uuid = ?";

		try {
			connection = Gberry.getUnsafeConnection();
			ps = connection.prepareStatement(query);

			ps.setInt(1, numOfLives);
			ps.setString(2, uuid.toString());

			Gberry.executeUpdate(connection, ps);

			List<String> gsyncArgs = new ArrayList<>(Arrays.asList(new String[] {"Factions", "rmlives", uuid.toString(), "" + numOfLives}));
			Gberry.sendGSyncEvent(gsyncArgs);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Something broke");
		} finally {
			Gberry.closeComponents(ps, connection);
		}
	}

}
