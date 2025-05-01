package net.badlion.arenalobby.commands;

import net.badlion.gberry.utils.CompressionUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

public class SettingsDbConversionCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, final Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		sender.sendMessage(ChatColor.DARK_RED + "No");
		return false;
		/* TODO: This was just left here for reference in the future
		if (sender.isOp() && sender.getName().equals("git") && args[1].equals("LULCONVERT") && args.length == 6) {
			sender.sendMessage("Starting db settings convert script GOOD LUCK!");
			BukkitUtil.runTaskAsync(new Runnable() {
				@Override
				public void run() {
					// There is around 350k rows so 375 gives us good room at 1000 rows per request
					for (int i = 0; i < 800; i++) {

						Connection connection = null;
						PreparedStatement ps = null;
						ResultSet rs = null;

						try {
							connection = Gberry.getConnection();
							ps = connection.prepareStatement("SELECT * FROM potion_message_options_s14 ORDER BY uuid OFFSET ? LIMIT ?;");

							ps.setInt(1, i * 500);
							ps.setInt(2, 500);

							rs = Gberry.executeQuery(connection, ps);

							Bukkit.getLogger().log(Level.INFO, "DB CONVERT SETTINGS: Offset=" + i * 500);

							if (!rs.next()) {
								Bukkit.getLogger().log(Level.INFO, "DB CONVERT SETTINGS: No results");
								return;
							}

							try {
								Bukkit.getLogger().log(Level.INFO, "DB CONVERT SETTINGS: Sleeping while we wait for userdatas to update");
								Thread.sleep(1000);
								Bukkit.getLogger().log(Level.INFO, "DB CONVERT SETTINGS: Done with sleep");
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							int count = 0;
							while (rs.next()) {
								// Grab all legacy data
								UUID uuid = UUID.fromString(rs.getString("uuid"));
								Map<MessageManager.MessageType, Boolean> oldSettings = deserializeMessageOptions(rs.getBytes("message_options"));

								// Create new settings object
								ArenaSettings arenaSettings = new ArenaSettings();

								boolean saveSettings = false;

								for (Map.Entry<MessageManager.MessageType, Boolean> entry : oldSettings.entrySet()) {
									// If the setting value is not the default then we should set it in the new settings
									if (entry.getKey().getDefault() != entry.getValue()) {
										saveSettings = true;
										switch (entry.getKey()) {
											case DUEL:
												arenaSettings.setAllowDuelRequests(entry.getValue());
												break;
											case PARTY:
												arenaSettings.setAllowPartyRequests(entry.getValue());
												break;
											case DUEL_REQUEST_TYPE:
												arenaSettings.setDuelRequestType(ArenaSettings.DuelRequestType.CHAT);
												break;
											case ENABLE_SCOREBOARD:
												arenaSettings.setSidebarEnabled(entry.getValue());
												break;
											case SHOW_PLAYERS_IN_LOBBY:
												arenaSettings.setShowPlayersInLobby(entry.getValue());
												break;
											case SHOW_COLORED_HELM_SPEC:
												arenaSettings.setShowColoredHelmInSpec(entry.getValue());
												break;
											case SHOW_TITLES:
												arenaSettings.setShowTitles(entry.getValue());
												break;
										}
									}
								}

								// Only load and save if they don't have default settings
								if (saveSettings) {
									// Load UserData from database for this user
									UserDataManager.UserData userData = UserDataManager.getUserDataFromDB(uuid);

									// Save them settings
									ArenaSettingsManager.saveSettings(uuid, arenaSettings, userData);
									count++;
								}
							}
							Bukkit.getLogger().log(Level.INFO, "DB CONVERT SETTINGS: Updating " + count + " players user datas");

						} catch (SQLException e) {
							e.printStackTrace();
						} finally {
							Gberry.closeComponents(rs, ps, connection);
						}
					}
				}
			});

			return true;
		}
		*/
	}

	/*
	private Map<MessageManager.MessageType, Boolean> deserializeMessageOptions(byte[] bytes) {
		Map<MessageManager.MessageType, Boolean> messageTagBooleans = new ConcurrentHashMap<>();

		try {
			String[] messageOptions = CompressionUtil.decompress(bytes).split(",");

			for (String messageOption : messageOptions) {
				if (!messageOption.isEmpty()) {
					try {
						String[] strings = messageOption.split(":");

						messageTagBooleans.put(MessageManager.MessageType.valueOf(strings[0]), Boolean.valueOf(strings[1]));
					} catch (IllegalArgumentException e) {
						// MessageType.valueOf(strings[0]) not found, will automatically remove the record
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return messageTagBooleans;
	}
	*/
}
