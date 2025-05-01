package net.badlion.disguise.managers;

import net.badlion.disguise.Disguise;
import net.badlion.disguise.DisguisedPlayer;
import net.badlion.disguise.events.PlayerDisguiseEvent;
import net.badlion.disguise.events.PlayerUndisguiseEvent;
import net.badlion.gberry.GMap;
import net.badlion.gberry.GMapManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.Pair;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DisguiseManager implements GMap<DisguisedPlayer> {

	private static final int COOLDOWN_TIME = 5000;

	private static Map<UUID, Pair> savedPlayerDisguises = new HashMap<>();

	private static final Map<UUID, Long> lastDisguiseTime = new HashMap<>();

    private static final Map<UUID, DisguisedPlayer> disguiseMap = new ConcurrentHashMap<>();
	private static final Map<String, UUID> randomNameMapping = new ConcurrentHashMap<>();

	private static final List<SkinTexture> randomSkinTextures = new ArrayList<>();

	private static final Random rand = new Random();

    public static void initialize() {
	    // GMap stuff
	    GMapManager.getInstance().register(new DisguiseManager());

	    // Cache all skins
	    BukkitUtil.runTaskAsync(new Runnable() {
		    @Override
		    public void run() {
			    String query = "SELECT * FROM disguise_skins;";

			    Connection connection = null;
			    PreparedStatement ps = null;
			    ResultSet rs = null;

			    try {
				    connection = Gberry.getConnection();
				    ps = connection.prepareStatement(query);

				    rs = Gberry.executeQuery(connection, ps);

				    while (rs.next()) {
					    DisguiseManager.randomSkinTextures.add(
							    new DisguiseManager.SkinTexture(rs.getString("texture"), rs.getString("signature")));
				    }
			    } catch (SQLException e) {
				    Disguise.getInstance().getLogger().info("Failed to load skins");
				    e.printStackTrace();
			    } finally {
				    Gberry.closeComponents(rs, ps, connection);
			    }
		    }
	    });
    }

	public static boolean hasCooldown(UUID uuid) {
		Long time = DisguiseManager.lastDisguiseTime.get(uuid);

		if (time == null || System.currentTimeMillis() >= time + DisguiseManager.COOLDOWN_TIME) {
			DisguiseManager.lastDisguiseTime.put(uuid, System.currentTimeMillis());
			return false;
		}

		return true;
	}

	/**
	 * NOTE: Must be called ASYNC!
	 */
    private static String getRandomName() {
	    // TODO: RACE CONDITION, NEED TO LOCK TABLE

	    String query = "SELECT * FROM disguise_names WHERE in_use = 'false' ORDER BY RANDOM() LIMIT 1;";
	    String safetyQuery = "SELECT * FROM disguise_names WHERE in_use = 'false' and disguise_name = ?;";
	    String query2 = "UPDATE disguise_names SET in_use = 'true' where disguise_name = ?;";

	    Connection connection = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    try {
		    connection = Gberry.getConnection();
		    ps = connection.prepareStatement(query);

		    rs = Gberry.executeQuery(connection, ps);

		    rs.next();

		    String randomName = rs.getString("disguise_name");

		    // Don't leak anything!!!
		    Gberry.closeComponents(rs, ps);

		    // Avoid race conditions until we fix this thing...
		    ps = connection.prepareStatement(safetyQuery);

		    ps.setString(1, randomName);

		    rs = Gberry.executeQuery(connection, ps);

		    // A different thread got this name before we did, BTFO
		    if (!rs.next()) return null;

		    // Don't leak anything!!!
		    Gberry.closeComponents(ps);

		    ps = connection.prepareStatement(query2);

		    ps.setString(1, randomName);

		    Gberry.executeUpdate(connection, ps);

		    return randomName;

	    } catch (SQLException e) {
		    e.printStackTrace();
	    } finally {
		    Gberry.closeComponents(rs, ps, connection);
	    }

	    return null;
    }

	private static void setRandomNameNotInUse(final String disguisedName) {
		// Set this disguised name to be not in use
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				String query = "UPDATE disguise_names SET in_use = 'false' where disguise_name = ?;";

				Connection connection = null;
				PreparedStatement ps = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setString(1, disguisedName);

					Gberry.executeUpdate(connection, ps);

				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(ps, connection);
				}
			}
		});
	}

	private static void logDisguise(final UUID uuid, final String disguisedName) {
		// Log this disguise
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				String query = "INSERT INTO disguise_history (uuid, disguise_name, disguise_time) VALUES (?, ?, ?);";

				Connection connection = null;
				PreparedStatement ps = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setString(1, uuid.toString());
					ps.setString(2, disguisedName);
					ps.setTimestamp(3, new Timestamp(DateTime.now().toDateTime(DateTimeZone.UTC).getMillis()));

					Gberry.executeUpdate(connection, ps);

				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(ps, connection);
				}
			}
		});
	}

	private static void logUndisguise(final UUID uuid, final String disguisedName) {
		// Log this disguise
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				String query = "UPDATE disguise_history SET undisguise_time = ? WHERE uuid = ? AND disguise_name = ?;";

				Connection connection = null;
				PreparedStatement ps = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setTimestamp(1, new Timestamp(DateTime.now().toDateTime(DateTimeZone.UTC).getMillis()));
					ps.setString(2, uuid.toString());
					ps.setString(3, disguisedName);

					Gberry.executeUpdate(connection, ps);

				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(ps, connection);
				}
			}
		});
	}

    public static void storeDisguisePlayer(DisguisedPlayer disguisedPlayer) {
        DisguiseManager.disguiseMap.put(disguisedPlayer.getUUID(), disguisedPlayer);

	    // This needs to be stored as lowercase so we can grab it easily
        DisguiseManager.randomNameMapping.put(disguisedPlayer.getDisguisedName().toLowerCase(), disguisedPlayer.getUUID());
    }

	public static UUID getDisguisedUUID(String name) {
		return DisguiseManager.randomNameMapping.get(name.toLowerCase());
	}

    public static DisguisedPlayer getDisguisePlayer(UUID uuid) {
        return DisguiseManager.disguiseMap.get(uuid);
    }

    public static DisguisedPlayer removeDisguisePlayer(UUID uuid) {
        final DisguisedPlayer disguisedPlayer = DisguiseManager.disguiseMap.remove(uuid);
        if (disguisedPlayer != null) {
            DisguiseManager.randomNameMapping.remove(disguisedPlayer.getDisguisedName().toLowerCase());

	        // Set this disguised name to be not in use
	        DisguiseManager.setRandomNameNotInUse(disguisedPlayer.getDisguisedName());
        }

		return disguisedPlayer;
    }

	public static void savePlayerDisguise(Player player) {
		JSONObject disguiseSettings = UserDataManager.getUserData(player).getDisguiseSettings();
		String skinTexture = (String) disguiseSettings.get("skin_texture");
		String skinSignature = (String) disguiseSettings.get("skin_signature");

		DisguiseManager.savedPlayerDisguises.put(player.getUniqueId(), new Pair<>(player.getDisguisedName(), new SkinTexture(skinTexture, skinSignature)));
	}

	/**
	 * Disguises an offline player with their saved disguise information
	 */
	public static void disguisePlayerWithSavedDisguise(final UUID uuid) {
		Pair savedDisguise = DisguiseManager.savedPlayerDisguises.remove(uuid);

		// Safety check
		if (savedDisguise == null) {
			throw new RuntimeException("Attempted to disguise player with saved disguise where no saved disguise was found for " + uuid);
		}

		final String disguiseName = (String) savedDisguise.getA();
		final SkinTexture skinTexture = (SkinTexture) savedDisguise.getB();

		DisguiseManager.storeDisguisePlayer(new DisguisedPlayer(uuid, "N/A", disguiseName));

		// Store information in UserData
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				final UserDataManager.UserData userData = UserDataManager.getUserDataFromDB(uuid);
				BukkitUtil.runTask(new Runnable() {
					@Override
					public void run() {
						JSONObject disguiseSettings = userData.getDisguiseSettings();

						disguiseSettings.put("is_disguised", true);
						disguiseSettings.put("disguise_name", disguiseName);
						disguiseSettings.put("skin_texture", skinTexture.getValue());
						disguiseSettings.put("skin_signature", skinTexture.getSignature());

						userData.setDisguiseSettings(disguiseSettings, true);
					}
				});
			}
		});

		// Log disguised name
		DisguiseManager.logDisguise(uuid, disguiseName);
	}

	public static void disguisePlayerWithSavedDisguise(final Player player) {
		Pair savedDisguise = DisguiseManager.savedPlayerDisguises.remove(player.getUniqueId());

		// Safety check
		if (savedDisguise == null) {
			throw new RuntimeException("Attempted to disguise player with saved disguise where no saved disguise was found for " + player.getUniqueId());
		}

		String disguiseName = (String) savedDisguise.getA();
		SkinTexture skinTexture = (SkinTexture) savedDisguise.getB();

		// Call the disguise event
		PlayerDisguiseEvent event = new PlayerDisguiseEvent(player, disguiseName, false);
		Disguise.getInstance().getServer().getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			// Event handles the error message to the player

			// Set this disguised name to be not in use
			DisguiseManager.setRandomNameNotInUse(disguiseName);

			return;
		}

		player.disguise(disguiseName, skinTexture.getValue(), skinTexture.getSignature());

		// Update /list
		Gberry.plugin.getListCommandHandler().removePlayerFromList(player);
		Gberry.plugin.getListCommandHandler().addPlayerToList(player);

		DisguiseManager.storeDisguisePlayer(new DisguisedPlayer(player.getUniqueId(), player.getName(), disguiseName));

		// Store information in UserData
		UserDataManager.UserData userData = UserDataManager.getUserData(player);
		JSONObject disguiseSettings = userData.getDisguiseSettings();

		disguiseSettings.put("is_disguised", true);
		disguiseSettings.put("disguise_name", disguiseName);
		disguiseSettings.put("skin_texture", skinTexture.getValue());
		disguiseSettings.put("skin_signature", skinTexture.getSignature());

		userData.setDisguiseSettings(disguiseSettings, true);

		// Log disguised name
		DisguiseManager.logDisguise(player.getUniqueId(), disguiseName);
	}

	public static void disguisePlayer(final Player player, final boolean fromCommand, final boolean redisguise) {
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				String randomName;
				do {
					randomName = DisguiseManager.getRandomName();
				} while (randomName == null);

				final String finalRandomName = randomName;
				BukkitUtil.runTask(new Runnable() {
					@Override
					public void run() {

						// Call the disguise event
						PlayerDisguiseEvent event = new PlayerDisguiseEvent(player, finalRandomName, fromCommand);
						Disguise.getInstance().getServer().getPluginManager().callEvent(event);

						if (event.isCancelled()) {

							// Event handles the error message to the player

							// Set this disguised name to be not in use
							DisguiseManager.setRandomNameNotInUse(finalRandomName);

							return;
						} else if (fromCommand) {
							if (redisguise) {
								player.sendMessage(ChatColor.GREEN + "You have redisguised! Your disguise name is " + finalRandomName + ".");
							} else {
								player.sendMessage(ChatColor.GREEN + "You have enabled disguise! Your disguise name is " + finalRandomName + ".");
							}
						}

						SkinTexture skinTexture = DisguiseManager.getRandomSkinTexture();

						player.disguise(finalRandomName, skinTexture.getValue(), skinTexture.getSignature());

						// Update /list
						Gberry.plugin.getListCommandHandler().removePlayerFromList(player);
						Gberry.plugin.getListCommandHandler().addPlayerToList(player);

						DisguiseManager.storeDisguisePlayer(new DisguisedPlayer(player.getUniqueId(), player.getName(), finalRandomName));

						// Store information in UserData
						UserDataManager.UserData userData = UserDataManager.getUserData(player);
						JSONObject disguiseSettings = userData.getDisguiseSettings();

						disguiseSettings.put("is_disguised", true);
						disguiseSettings.put("disguise_name", finalRandomName);
						disguiseSettings.put("skin_texture", skinTexture.getValue());
						disguiseSettings.put("skin_signature", skinTexture.getSignature());

						userData.setDisguiseSettings(disguiseSettings, true);

						// Log disguised name
						DisguiseManager.logDisguise(player.getUniqueId(), finalRandomName);
					}
				});
			}
		});
	}

	/**
	 * Undisguises an offline player
	 */
	public static void undisguisePlayer(final UUID uuid) {
		// Remove player's disguise info
		DisguisedPlayer disguisedPlayer = DisguiseManager.removeDisguisePlayer(uuid);

		// Store information in UserData
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				final UserDataManager.UserData userData = UserDataManager.getUserDataFromDB(uuid);
				BukkitUtil.runTask(new Runnable() {
					@Override
					public void run() {
						JSONObject disguiseSettings = userData.getDisguiseSettings();

						disguiseSettings.put("is_disguised", false);
						disguiseSettings.remove("disguise_name");
						disguiseSettings.remove("skin_texture");
						disguiseSettings.remove("skin_signature");

						userData.setDisguiseSettings(disguiseSettings, true);
					}
				});
			}
		});

		// Log undisguise
		DisguiseManager.logUndisguise(uuid, disguisedPlayer.getDisguisedName());
	}

	public static boolean undisguisePlayer(Player player, boolean fromCommand) {
		// Call the undisguise event
		PlayerUndisguiseEvent event = new PlayerUndisguiseEvent(player, fromCommand);
		Disguise.getInstance().getServer().getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			// Event handles the error message to the player
			return false;
		}

		// Remove player's disguise info
		DisguisedPlayer disguisedPlayer = DisguiseManager.removeDisguisePlayer(player.getUniqueId());

		player.undisguise();

		// Update /list
		Gberry.plugin.getListCommandHandler().addPlayerToList(player);

		// Store information in UserData
		UserDataManager.UserData userData = UserDataManager.getUserData(player);
		JSONObject disguiseSettings = userData.getDisguiseSettings();

		disguiseSettings.put("is_disguised", false);
		disguiseSettings.remove("disguise_name");
		disguiseSettings.remove("skin_texture");
		disguiseSettings.remove("skin_signature");

		userData.setDisguiseSettings(disguiseSettings, true);

		// Log undisguise
		DisguiseManager.logUndisguise(player.getUniqueId(), disguisedPlayer.getDisguisedName());

		return true;
	}

	public static class SkinTexture {

		private String value;
		private String signature;

		public SkinTexture(String value, String signature) {
			this.value = value;
			this.signature = signature;
		}

		public String getValue() {
			return this.value;
		}

		public String getSignature() {
			return this.signature;
		}
	}

	public static SkinTexture getRandomSkinTexture() {
		// No skins loaded
		if (DisguiseManager.randomSkinTextures.size() == 0) {
			return new SkinTexture("", "");
		}

		return DisguiseManager.randomSkinTextures.get(DisguiseManager.rand.nextInt(DisguiseManager.randomSkinTextures.size()));
	}

	@Override
    public String getName() {
        return "disguise_manager";
    }

    @Override
    public Map<UUID, DisguisedPlayer> getMap() {
        return DisguiseManager.disguiseMap;
    }

}
