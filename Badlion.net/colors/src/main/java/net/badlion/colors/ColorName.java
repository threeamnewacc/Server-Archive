package net.badlion.colors;

import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ColorName extends JavaPlugin {

	private static ColorName plugin;
	private String serverName;

	@Override
	public void onEnable() {
		Gberry.enableAsyncLoginEvent = true;

		ColorName.plugin = this;
		this.getCommand("color").setExecutor(new ColorCommand());

		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

		this.saveDefaultConfig();

		this.serverName = this.getConfig().getString("colors.serverName");

		String tableName = this.serverName + "_colors";
		if (tableName.equals("default_colors")) {
			Bukkit.getLogger().severe("Default Name Specified, disabling Colors!!!");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		
	}

	@Override
	public void onDisable() {

	}

	public void commitColorChange(final Player player, final String color) {
		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {

			public void run() {
				Connection connection = null;
				PreparedStatement ps = null;

				try {
					//String sql = "INSERT INTO "+ ColorName.serverName + "_colors (uuid, color) VALUES(?, ?) " +
					//					 "ON DUPLICATE KEY UPDATE color=?;";
					String query = "UPDATE " + ColorName.this.serverName + "_colors SET color = ? WHERE uuid = ?;\n";
					query += "INSERT INTO " + ColorName.this.serverName + "_colors (uuid, color) SELECT ?, ? WHERE NOT EXISTS " +
							"(SELECT 1 FROM " + ColorName.this.serverName + "_colors WHERE uuid = ?);";
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					ps.setString(1, color);
					ps.setString(2, player.getUniqueId().toString());
					ps.setString(3, player.getUniqueId().toString());
					ps.setString(4, color);
					ps.setString(5, player.getUniqueId().toString());

					Gberry.executeUpdate(connection, ps);

					// Update the player's chat prefix in MCP
					Gberry.sendChatPlayerPrefixChange(player.getUniqueId());
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(ps, connection);
				}
			}
		});
	}

	public static ColorName getInstance() {
		return ColorName.plugin;
	}

	public String getServerName() {
		return this.serverName;
	}

}