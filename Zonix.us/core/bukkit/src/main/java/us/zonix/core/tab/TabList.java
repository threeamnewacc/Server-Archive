package us.zonix.core.tab;

import club.minemen.spigot.ClubSpigot;
import club.minemen.spigot.handler.PacketHandler;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.WorldSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import us.zonix.core.CorePlugin;


public class TabList implements Listener, PacketHandler {

	private JavaPlugin plugin;

	private ITabHandler tabHandler;

	private Field info_action, info_profile_list;

	private Field team_name, team_display, team_prefix, team_suffix, team_players, team_mode, team_color, team_nametag;

	private Field network_channel;

	private Field propertymap;

	private Property head = null;

	/**
	 *
	 * @param plugin
	 * @param handler
	 */
	public TabList(JavaPlugin plugin, ITabHandler handler) {
		this.plugin = plugin;
		this.tabHandler = handler;
		ClubSpigot.INSTANCE.addPacketHandler(this);
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
		try {
			info_action = PacketPlayOutPlayerInfo.class.getDeclaredField("a");
			info_action.setAccessible(true);
			info_profile_list = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
			info_profile_list.setAccessible(true);

			team_name = PacketPlayOutScoreboardTeam.class.getDeclaredField("a");
			team_name.setAccessible(true);
			team_display = PacketPlayOutScoreboardTeam.class.getDeclaredField("b");
			team_display.setAccessible(true);
			team_prefix = PacketPlayOutScoreboardTeam.class.getDeclaredField("c");
			team_prefix.setAccessible(true);
			team_suffix = PacketPlayOutScoreboardTeam.class.getDeclaredField("d");
			team_suffix.setAccessible(true);
			team_players = PacketPlayOutScoreboardTeam.class.getDeclaredField("g");
			team_players.setAccessible(true);
			team_color = PacketPlayOutScoreboardTeam.class.getDeclaredField("f");
			team_color.setAccessible(true);
			team_mode = PacketPlayOutScoreboardTeam.class.getDeclaredField("h");
			team_mode.setAccessible(true);
			team_nametag = PacketPlayOutScoreboardTeam.class.getDeclaredField("e");
			team_nametag.setAccessible(true);

			network_channel = NetworkManager.class.getDeclaredField("channel");
			network_channel.setAccessible(true);

			propertymap = PropertyMap.class.getDeclaredField("properties");
			propertymap.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

	}

	/**
	 * You can set the default head for the players in the tab.
	 * Warning due to mojang rate limit this can only be accessed once every minute.
	 * so basically if you restart the server make sure it was a minute before the last start.
	 * Will be a good idea if you turn this off for testing plugins or if you need to restart often.
	 *
	 * @param head  the uuid of the players head you want
	 * @return      this
	 */
	public TabList setHead(UUID head) {
		try {
			URL url = HttpAuthenticationService
					.constantURL("https://sessionserver.mojang.com/session/minecraft/profile/"
							+ UUIDTypeAdapter.fromUUID(head) + "?unsigned=false");

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			if (connection.getResponseCode() == 429) {
				this.plugin.getLogger().severe("Tab List could not get the players head for the tab please wait 1 minute and restart");
				return this;
			}

			JSONObject obj = (JSONObject) new JSONParser().parse(new BufferedReader(new InputStreamReader(connection.getInputStream())));
			JSONObject props = (JSONObject) ((JSONArray) obj.get("properties")).get(0);
			this.head = new Property("textures", props.get("value").toString(), props.get("signature").toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return this;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onLogin(PlayerJoinEvent event) {

		long start = System.nanoTime();
		setupBoard(((CraftPlayer) event.getPlayer()).getHandle());

		try {
			PacketPlayOutScoreboardTeam a = new PacketPlayOutScoreboardTeam();
			team_mode.set(a, 3);
			team_name.set(a, "zLane");
			team_display.set(a, "zLane");
			team_color.set(a, -1);
			team_players.set(a, Arrays.asList(event.getPlayer().getName()));

			for (Player player : Bukkit.getOnlinePlayers()) {
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(a);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		if (this.tabHandler != null) {
			this.tabHandler.tabCreated(event.getPlayer(), System.nanoTime() - start);
		}
	}

	private void setupBoard(EntityPlayer player) {

		PacketPlayOutPlayerInfo player_packet = new PacketPlayOutPlayerInfo();;
		PacketPlayOutScoreboardTeam t;
		GameProfile profile;

		try {
			List<PacketPlayOutPlayerInfo.PlayerInfoData> players_in_packet = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) info_profile_list.get(player_packet);
			info_action.set(player_packet, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
			for (int r = 0; r < 20; r++) {//slot 0-59 for 1.7 players
				for (int c = 0; c < 3l; c++) {
					String name = playerName(r, c);

					profile = new GameProfile(UUID.randomUUID(), name);
					if (head != null) {
						Multimap<String, Property> prop = (Multimap<String, Property>) propertymap.get(profile.getProperties());
						prop.put("textures", head);
					}

					players_in_packet.add(player_packet.new PlayerInfoData(profile, 0, WorldSettings.EnumGamemode.SURVIVAL, null));
				}
			}
			for (int i = 60; i < 80; i++) {//slot 60-79 for 1.8 players
				String name = playerName(i);
				profile = new GameProfile(UUID.randomUUID(), name);
				if (head != null) {
					Multimap<String, Property> prop = (Multimap<String, Property>) propertymap.get(profile.getProperties());
					prop.put("textures", head);
				}
				players_in_packet.add(player_packet.new PlayerInfoData(profile, 0, WorldSettings.EnumGamemode.SURVIVAL, null));
			}

			player.playerConnection.sendPacket(player_packet);

			t = new PacketPlayOutScoreboardTeam();
			team_name.set(t, "zLane");
			team_display.set(t, "zLane");
			team_mode.set(t, 0);
			team_color.set(t, -1);
			team_nametag.set(t, "always");
			Collection<String> players = new ArrayList<String>();
			for (Player other : Bukkit.getOnlinePlayers()) {
				players.add(other.getName());
			}
			team_players.set(t, players);
			player.playerConnection.sendPacket(t);

			for (int r = 0; r < 20; r++) {
				for (int c = 0; c < 4; c++) {
					String name = playerName(r, c);
					String teamName = "$" + name;
					t = new PacketPlayOutScoreboardTeam();
					team_name.set(t, teamName);
					team_display.set(t, teamName);
					team_mode.set(t, 0);
					team_color.set(t, -1);
					team_nametag.set(t, "always");
					team_players.set(t, Arrays.asList(name));
					player.playerConnection.sendPacket(t);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.plugin.getLogger().warning("Error setting up player board for " + player.getName());
		}
	}

	private String playerName(int r, int c) {
		int slot = r + c * 20;
		return playerName(slot);
	}

	private String playerName(int slot) {
		return ChatColor.BOLD.toString() + ChatColor.GREEN.toString() + ChatColor.UNDERLINE.toString() +
				ChatColor.YELLOW.toString() +
				(slot >= 10 ? ChatColor.COLOR_CHAR + String.valueOf(slot / 10) +
						ChatColor.COLOR_CHAR + String.valueOf(slot % 10)
						: ChatColor.BLACK.toString() +
						ChatColor.COLOR_CHAR + String.valueOf(slot)) + ChatColor.RESET;
	}

	public void updateSlot(Player player, int row, int column, String value) {
		if (row > 19) {
			throw new RuntimeException("Row is above 19 " + row);
		}
		if (column > 4) {
			throw new RuntimeException("Columb is above 4 " + column);
		}
		String prefix = value;
		String suffix = "";
		if (value.length() > 16) {
			prefix = value.substring(0, 16);
			suffix = ChatColor.getLastColors(prefix) + value.substring(16);
			if (suffix.length() > 16) {
				suffix = suffix.substring(0, 16);
			}
		}
		String teamName = "$" + playerName(row, column);
		PacketPlayOutScoreboardTeam t = new PacketPlayOutScoreboardTeam();
		try {
			team_name.set(t, teamName);
			team_display.set(t, teamName);
			team_prefix.set(t, prefix);
			team_suffix.set(t, suffix);
			team_mode.set(t, 2);
			team_nametag.set(t, "always");
			team_color.set(t, -1);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(t);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			this.plugin.getLogger().warning(String.format("Error setting slot %s, %d, %d, %s", player.getName(), row,
					column, value));
		}
	}

	@Override
	public void handleReceivedPacket(PlayerConnection playerConnection, Packet packet) {
		//do nothing we dont care about clients packets
	}

	@Override
	public void handleSentPacket(PlayerConnection playerConnection, Packet packet) {
		if (packet instanceof PacketPlayOutScoreboardTeam) {
			PacketPlayOutScoreboardTeam p = (PacketPlayOutScoreboardTeam) packet;
			try {
				int mode = team_mode.getInt(p);
				String teamname = (String) team_name.get(p);
				/*
				if the packet is a remove player packet and the team isn't zLane
					this happens if a plugin is removing a player from a team and not adding it to another
					we need to check this for 1.8 players consistency as players not in a team will be displayed
					first on the scoreboard in front of all of our slots
				we set the mode of the packet to a add_player packet and change the team name and display name
				to our team "zLane" the only time a player would be removed from zLane is if we actually did it
				so dont check for zLane
					also changing it to a addplayer packet if it was zlane would mean adding a player to a team they
					are already in which would crash 1.7 players
				 */
				if (mode == 4 && !teamname.equals("zLane")) {
					team_mode.set(p, 3);
					team_name.set(p, "zLane");
					team_display.set(p, "zLane");
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}