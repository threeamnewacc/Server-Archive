package net.badlion.smellychat;

import com.google.common.collect.ImmutableList;
import net.badlion.banmanager.BanManager;
import net.badlion.gberry.Gberry;
import net.badlion.gpermissions.GPermissions;
import net.badlion.smellychat.commands.AdminChatCommand;
import net.badlion.smellychat.commands.AliasCommand;
import net.badlion.smellychat.commands.ChannelCommand;
import net.badlion.smellychat.commands.GlobalMuteCommand;
import net.badlion.smellychat.commands.IgnoreListCommand;
import net.badlion.smellychat.commands.MarkCommand;
import net.badlion.smellychat.commands.ModChatCommand;
import net.badlion.smellychat.commands.ReportCommand;
import net.badlion.smellychat.commands.ToggleGlobalCommand;
import net.badlion.smellychat.listeners.BungeeCordListener;
import net.badlion.smellychat.listeners.CustomEventListener;
import net.badlion.smellychat.listeners.PlayerListener;
import net.badlion.smellychat.managers.ChannelManager;
import net.badlion.smellychat.managers.ChatSettingsManager;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SmellyChat extends JavaPlugin {

	public static boolean GLOBAL_MUTE = false;

	private static SmellyChat plugin;

	private UUID serverUUID;

	private BanManager banManager;
	private GPermissions gPermissions;
	private ChannelHandler channelHandler;

	// Staff members
	private String reportMessagePermission;
	private Set<Player> admins = new HashSet<>();
	private Set<Player> mods = new HashSet<>();

	// Logging
	private Queue<SmellyChatRecord> smellyChatRecords = new ConcurrentLinkedQueue<>();

	public static SmellyChat getInstance() {
		return SmellyChat.plugin;
	}

	public void onEnable() {
		SmellyChat.plugin = this;

		Gberry.enableAsyncLoginEvent = true;

		this.serverUUID = UUID.randomUUID();

		this.banManager = (BanManager) this.getServer().getPluginManager().getPlugin("BanManager");
		this.gPermissions = (GPermissions) this.getServer().getPluginManager().getPlugin("GPermissions");

		// Initialize smellyinventory
		SmellyInventory.initialize(this, false);

		// Figure out which report permission we need to use
		switch (Gberry.serverType) {
			case ARENAPVP:
				this.reportMessagePermission = "badlion.kittrial";
				break;
			case FACTIONS:
				this.reportMessagePermission = "badlion.fmod";
				break;
			case FFA:
				this.reportMessagePermission = "badlion.kittrial";
				break;
			case LOBBY:
				this.reportMessagePermission = "badlion.staff";
				break;
			case MINIUHC:
				this.reportMessagePermission = "badlion.uhctrial";
				break;
			case SG:
				this.reportMessagePermission = "badlion.sgtrial";
				break;
			case SKYWARS:
				this.reportMessagePermission = "badlion.sgtrial";
				break;
			case TOURNAMENT:
				this.reportMessagePermission = "badlion.kittrial";
				break;
			case UHC:
				this.reportMessagePermission = "badlion.uhctrial";
				break;
			case UHCMEETUP:
				this.reportMessagePermission = "badlion.kittrial";
				break;
			case UNKNOWN:
				this.reportMessagePermission = "badlion.staff";
				break;
		}

		// Load channel handler (this gets overwritten later if we're using a custom one)
		this.channelHandler = new RegularChannelHandler();

		// Load commands
		this.getCommand("ad").setExecutor(new AdminChatCommand());
		this.getCommand("mc").setExecutor(new ModChatCommand());
		this.getCommand("ch").setExecutor(new ChannelCommand());
		this.getCommand("gc").setExecutor(new AliasCommand());
		this.getCommand("tgc").setExecutor(new ToggleGlobalCommand());
		this.getCommand("globalmute").setExecutor(new GlobalMuteCommand());
		MarkCommand markCommand = new MarkCommand();
		this.getCommand("mark").setExecutor(markCommand);
		this.getCommand("unmark").setExecutor(markCommand);
		//this.getCommand("friendslist").setExecutor(new FriendsListCommand());
		this.getCommand("ignorelist").setExecutor(new IgnoreListCommand());
		this.getCommand("report").setExecutor(new ReportCommand());

		// Load listeners
		this.getServer().getPluginManager().registerEvents(new AdminChatCommand(), this);
		this.getServer().getPluginManager().registerEvents(new ChatSettingsManager(), this);
		this.getServer().getPluginManager().registerEvents(new ModChatCommand(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		this.getServer().getPluginManager().registerEvents(new CustomEventListener(), this);

		// Register BungeeCord
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeCordListener());

		// Initialize managers
		ChannelManager.initialize();
		ChatSettingsManager.initialize();

		// Insert chat logs every 5 seconds
		new BukkitRunnable() {
			@Override
			public void run() {
				SmellyChat.this.insertSmellyChatRecords();
			}

		}.runTaskTimerAsynchronously(this, 20 * 5, 20 * 5);

		// Check if global mute is already on
		String globalMuteString = Gberry.getGlobalSetting("global_mute");
		if (globalMuteString != null) {
			SmellyChat.GLOBAL_MUTE = Boolean.valueOf(globalMuteString);
			CustomEventListener.globalMuteFromDB = SmellyChat.GLOBAL_MUTE;
		}
	}

	public void onDisable() {
	}

	public void logMessage(Channel channel, Player player, String message) {
		this.logMessage(channel.getIdentifier(), player, message);
	}

	public void logMessage(final String channelIdentifier, final Player player, final String message) {
		this.smellyChatRecords.add(new SmellyChatRecord(channelIdentifier, player.getUniqueId().toString(), player.getDisguisedName(), null, null, message));
	}

	public void logMessage(final Player sender, final Player receiver, final String message) {
		this.smellyChatRecords.add(new SmellyChatRecord(null, sender.getUniqueId().toString(), sender.getDisguisedName(),
				receiver.getUniqueId().toString(), receiver.getDisguisedName(), message));
	}

	public void networkBroadcast(String globalMessage, String type) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("Forward");
			out.writeUTF("ONLINE");
			out.writeUTF("SmellyChat");

			// MORE FUCKIN STREAMS
			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);

			// Report or mod chat?
			if (type.equals("Report")) {
				msgout.writeUTF("Report" + this.reportMessagePermission);
			} else if (type.equals("ModChat")) {
				msgout.writeUTF("ModChat");
			}

			msgout.writeUTF(globalMessage);

			out.writeShort(msgbytes.toByteArray().length);
			out.write(msgbytes.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		if (this.getServer().getOnlinePlayers().size() > 0) {
			ImmutableList.copyOf(this.getServer().getOnlinePlayers()).get(0).sendPluginMessage(this, "BungeeCord", b.toByteArray());
		}
	}

	private void insertSmellyChatRecords() {
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO smelly_chat_logs (log_time, server_name, channel, sender_uuid, sender_username, receiver_uuid, receiver_username, message) VALUES ");
		List<SmellyChatRecord> records = new ArrayList<>();

		Iterator<SmellyChatRecord> iterator = this.smellyChatRecords.iterator();
		while (iterator.hasNext()) {
			records.add(iterator.next());
			iterator.remove();
		}

		if (records.size() == 0) {
			return;
		}

		for (int i = 0; i < records.size(); i++) {
			builder.append("(?, ?, ?, ?, ?, ?, ?, ?), ");
		}

		String sql = builder.substring(0, builder.length() - 2) + ";";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(sql);

			int i = 1;

			for (SmellyChatRecord record : records) {
				ps.setTimestamp(i++, new Timestamp(record.getTimeStamp()));
				ps.setString(i++, Gberry.serverName.toUpperCase());
				ps.setString(i++, record.getChannelIdentifier());
				ps.setString(i++, record.getSenderUUID());
				ps.setString(i++, record.getSenderName());
				ps.setString(i++, record.getReceiverUUID());
				ps.setString(i++, record.getReceiverName());
				ps.setString(i++, record.getMessage());
			}

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(ps, connection);
		}
	}

	public UUID getServerUUID() {
		return serverUUID;
	}

	public String getReportMessagePermission() {
		return reportMessagePermission;
	}

	public Set<Player> getAdmins() {
		return admins;
	}

	public Set<Player> getMods() {
		return mods;
	}

	public BanManager getBanManager() {
		return banManager;
	}

	public GPermissions getGPermissions() {
		return gPermissions;
	}

	public ChannelHandler getChannelHandler() {
		return channelHandler;
	}

	public void setChannelHandler(ChannelHandler channelHandler) {
		this.channelHandler = channelHandler;
	}

	public class SmellyChatRecord {

		private long timeStamp;

		private String channelIdentifier;

		private String senderUUID;
		private String senderName;

		private String receiverUUID;
		private String receiverName;

		private String message;

		public SmellyChatRecord(String channelIdentifier, String senderUUID, String senderName, String receiverUUID, String receiverName,
		                        String message) {
			this.timeStamp = System.currentTimeMillis();

			this.channelIdentifier = channelIdentifier;

			this.senderUUID = senderUUID;
			this.senderName = senderName;

			this.receiverUUID = receiverUUID;
			this.receiverName = receiverName;

			this.message = message;
		}

		public long getTimeStamp() {
			return timeStamp;
		}

		public String getChannelIdentifier() {
			return channelIdentifier;
		}

		public String getSenderUUID() {
			return senderUUID;
		}

		public String getSenderName() {
			return senderName;
		}

		public String getReceiverUUID() {
			return receiverUUID;
		}

		public String getReceiverName() {
			return receiverName;
		}

		public String getMessage() {
			return message;
		}

	}

}
