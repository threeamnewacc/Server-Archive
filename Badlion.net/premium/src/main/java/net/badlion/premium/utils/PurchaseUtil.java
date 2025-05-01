package net.badlion.premium.utils;

import net.badlion.gberry.Gberry;
import net.badlion.premium.Premium;
import net.badlion.premium.Purchase;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PurchaseUtil {

	private static final double LIFETIME_DONATOR_PRICE = 30;
	private static final double LIFETIME_DONATOR_PLUS_PRICE = 50;
	private static final double LIFETIME_LION_PRICE = 75;

	private static final Set<Integer> rankPurchaseIds = new HashSet<>();
	private static final Set<Integer> tempRankPurchaseIds = new HashSet<>();

	static {
		PurchaseUtil.rankPurchaseIds.add(1353833); // Lion Lifetime
		PurchaseUtil.rankPurchaseIds.add(1353838); // Lion Upgrade
		PurchaseUtil.rankPurchaseIds.add(1353849); // Lion 30 days
		PurchaseUtil.rankPurchaseIds.add(1128524); // Donator+ Lifetime
		PurchaseUtil.rankPurchaseIds.add(1129028); // Donator+ Upgrade
		PurchaseUtil.rankPurchaseIds.add(1128519); // Donator+ 30 days
		PurchaseUtil.rankPurchaseIds.add(358452);  // Donator Lifetime
		PurchaseUtil.rankPurchaseIds.add(358450);  // Donator 30 days

		PurchaseUtil.tempRankPurchaseIds.add(1353849); // Lion 30 days
		PurchaseUtil.tempRankPurchaseIds.add(1128519); // Donator+ 30 days
		PurchaseUtil.tempRankPurchaseIds.add(358450);  // Donator 30 days
	}

	public static String getRankStringFromPackageID(int packageId) {
		switch (packageId) {
			case 358450: // Donator 30 days
				return "donator";
			case 1128519: // Donator+ 30 days
				return "donatorplus";
			case 1353849: // Lion 30 days
				return "lion";
			default:
				throw new IllegalArgumentException("Rank string from package id " + packageId + " not found");
		}
	}

	public static void insertPurchase(Purchase purchase) {
		String sql = "INSERT INTO purchases (transaction_id, status, uuid, username, price, currency, ts, email," +
				"ip, package_id, package_price, package_expiration_date)" +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = Premium.getConnection();
			ps = connection.prepareStatement(sql);

			ps.setString(1, purchase.getTransactionId());
			ps.setString(2, "complete");
			ps.setString(3, purchase.getUuid().toString());
			ps.setString(4, purchase.getUsername());
			ps.setDouble(5, purchase.getPrice());
			ps.setString(6, purchase.getCurrency());
			ps.setTimestamp(7, new Timestamp(purchase.getTimestamp().getMillis()));
			ps.setString(8, purchase.getEmail());
			ps.setString(9, purchase.getIp());
			ps.setInt(10, purchase.getPackageId());
			ps.setDouble(11, purchase.getPackagePrice());
			ps.setTimestamp(12, (purchase.getPackageExpirationDate() == null ? null : new Timestamp(purchase.getPackageExpirationDate().getMillis())));

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Something broke");
		} finally {
			Gberry.closeComponents(ps, connection);
		}
	}

	public static void updatePurchases(String transactionId, String status) {
		String sql = "UPDATE purchases SET status = ? WHERE transaction_id = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = Premium.getConnection();
			ps = connection.prepareStatement(sql);

			ps.setString(1, status);
			ps.setString(2, transactionId);

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Something broke");
		} finally {
			Gberry.closeComponents(ps, connection);
		}
	}

	public static String checkLifetimeRanks(UUID uuid) {
		double totalSpent = 0.0D;
		for (Purchase purchase : getPurchases(uuid)) {
			if (isRankPurchase(purchase)) {
				if (purchase.getPrice() > 0) {
					totalSpent += purchase.getPackagePrice();
				}
			}
		}

		if (totalSpent >= LIFETIME_LION_PRICE) {
			return "lion";
		} else if (totalSpent >= LIFETIME_DONATOR_PLUS_PRICE) {
			return "donatorplus";
		} else if (totalSpent >= LIFETIME_DONATOR_PRICE) {
			return "donator";
		}

		return null;
	}

	public static boolean isRankPurchase(Purchase purchase) {
		return PurchaseUtil.rankPurchaseIds.contains(purchase.getPackageId());
	}

	public static boolean isTempRankPurchase(Purchase purchase) {
		return PurchaseUtil.tempRankPurchaseIds.contains(purchase.getPackageId());
	}

	public static List<Purchase> getPurchases(UUID uuid) {
		String sql = "SELECT * FROM purchases WHERE uuid = ? ORDER BY ts DESC;";
		List<Purchase> purchases = new ArrayList<>();

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = Premium.getConnection();
			ps = connection.prepareStatement(sql);
			ps.setString(1, uuid.toString());

			rs = ps.executeQuery();

			while (rs.next()) {
				DateTime dateTime = new DateTime(rs.getTimestamp("ts"), DateTimeZone.UTC);
				Timestamp ts = rs.getTimestamp("package_expiration_date");
				DateTime expirationDate = null;
				if (ts != null) {
					expirationDate = new DateTime(ts, DateTimeZone.UTC);
				}
				purchases.add(new Purchase(rs.getString("transaction_id"), uuid, rs.getString("username"), rs.getDouble("price"),
						rs.getString("currency"), dateTime, rs.getString("email"), rs.getString("ip"), rs.getInt("package_id"),
						rs.getDouble("package_price"), expirationDate));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Something broke");
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return purchases;
	}

	public static Purchase getPurchase(String transactionId) {
		String sql = "SELECT * FROM purchases WHERE transaction_id = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = Premium.getConnection();
			ps = connection.prepareStatement(sql);

			ps.setString(1, transactionId);

			rs = ps.executeQuery();

			if (rs.next()) {
				DateTime dateTime = new DateTime(rs.getTimestamp("ts"), DateTimeZone.UTC);
				Timestamp ts = rs.getTimestamp("package_expiration_date");
				DateTime expirationDate = null;
				if (ts != null) {
					expirationDate = new DateTime(ts, DateTimeZone.UTC);
				}
				return new Purchase(rs.getString("transaction_id"), UUID.fromString(rs.getString("uuid")), rs.getString("username"), rs.getDouble("price"),
										   rs.getString("currency"), dateTime, rs.getString("email"), rs.getString("ip"), rs.getInt("package_id"),
										   rs.getDouble("package_price"), expirationDate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Something broke");
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return null;
	}

	// Cases utils
	public static JSONObject getCases(UUID uuid) {
		String query = "SELECT cases FROM user_data WHERE uuid = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);

			ps.setString(1, uuid.toString());
			rs = Gberry.executeQuery(connection, ps);

			// Get their cases
			if (rs.next()) {
				JSONParser parser = new JSONParser();

				try {
					if (rs.getString("cases") != null) {
						return ((JSONObject) parser.parse(rs.getString("cases")));
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Something broke");
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return null;
	}

}
