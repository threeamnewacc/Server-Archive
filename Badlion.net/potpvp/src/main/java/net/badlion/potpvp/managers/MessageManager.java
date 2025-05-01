package net.badlion.potpvp.managers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.AsyncPlayerJoinEvent;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.CompressionUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.MessageEvent;
import net.badlion.potpvp.ffaworlds.FFAWorld;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.ChatMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MessageManager extends BukkitUtil.Listener {

	private static Map<UUID, MessageOptions> playerMessageOptions = new ConcurrentHashMap<>();

	public enum MessageType {
		DUEL(ItemStackUtil.createItem(Material.YELLOW_FLOWER, "Duel"), true), // Used separately from message options inventory
		PARTY(ItemStackUtil.createItem(Material.YELLOW_FLOWER, "Party"), true), // Used separately from message options inventory
		RANKED_MESSAGES(ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "Ranked Messages",
				ChatColor.YELLOW + "Show all ranked messages"), false),
		EVENT_MESSAGES(ItemStackUtil.createItem(Material.NETHER_STAR, ChatColor.GREEN + "Event Messages",
				ChatColor.YELLOW + "Show event messages"), false),
		ELO_AT_MATCH_START(ItemStackUtil.createItem(Material.PAINTING, ChatColor.GREEN + "Elo at Match Start",
				ChatColor.YELLOW + "Show opponent's elo", ChatColor.YELLOW + "at match start"), true),
		FFA_MESSAGES(ItemStackUtil.createItem(Material.GOLD_AXE, ChatColor.GREEN + "FFA Messages",
				ChatColor.YELLOW + "Show all FFA kill/death", ChatColor.YELLOW + "messages"), true),
		TDM_MESSAGES(ItemStackUtil.createItem(Material.BOW, ChatColor.GREEN + "TDM Messages",
				ChatColor.YELLOW + "Show all TDM kill/death", ChatColor.YELLOW + "messages"), true);
		/*BOLD_OWN_MESSAGES(ItemStackUtil.createItem(Material.FIREWORK_CHARGE, ChatColor.GREEN + "Bold Own Messages",
				ChatColor.YELLOW + "Bold your own", ChatColor.YELLOW + "kill/death messages"), true);*/

		private ItemStack item;

		private final boolean enabled;

		MessageType(ItemStack item, boolean enabled) {
			this.item = item;

			this.enabled = enabled;
		}

		public ItemStack getItem() {
			return item;
		}

		public boolean getDefault() {
			return this.enabled;
		}

	}

	@EventHandler
	public void onMessageEvent(MessageEvent event) {
		if (event.getRecipients() != null) {
			// Handle which messages actually get sent to the client
			for (Player player : event.getRecipients()) {
				// Force send the message to this player?
				if (event.getForce() != null && event.getForce().contains(player)) {
					// Fetch MessageOptions object and send message request
					if (event.getBaseComponents() != null) {
						MessageManager.getMessageOptions(player).sendMessageRequest(true, event.getMessageType(), event.getBaseComponents());
					} else {
						MessageManager.getMessageOptions(player).sendMessageRequest(true, event.getMessageType(), event.getChatMessage());
					}
				} else {
					if (event.getBaseComponents() != null) {
						MessageManager.getMessageOptions(player).sendMessageRequest(false, event.getMessageType(), event.getBaseComponents());
					} else {
						MessageManager.getMessageOptions(player).sendMessageRequest(false, event.getMessageType(), event.getChatMessage());
					}
				}
			}
		} else {
			// Handle which messages actually get sent to the client
			for (Player player : PotPvP.getInstance().getServer().getOnlinePlayers()) {
				// Force send the message to this player?
				if (event.getForce() != null && event.getForce().contains(player)) {
					// Fetch MessageOptions object and send message request
					if (event.getBaseComponents() != null) {
						MessageManager.getMessageOptions(player).sendMessageRequest(true, event.getMessageType(), event.getBaseComponents());
					} else {
						MessageManager.getMessageOptions(player).sendMessageRequest(true, event.getMessageType(), event.getChatMessage());
					}
				} else {
					if (event.getBaseComponents() != null) {
						MessageManager.getMessageOptions(player).sendMessageRequest(false, event.getMessageType(), event.getBaseComponents());
					} else {
						MessageManager.getMessageOptions(player).sendMessageRequest(false, event.getMessageType(), event.getChatMessage());
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// Load MessageOptions object
		MessageManager.getMessageOptions(event.getPlayer());
	}

	@EventHandler
	public void onPlayerAsyncJoin(final AsyncPlayerJoinEvent event) {
		MessageOptions messageOptions = MessageManager.playerMessageOptions.get(event.getUuid());
		if (messageOptions != null) {
			messageOptions.fetchMessageOptions(event.getConnection());
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		// Remove MessageOptions object
        try {
		MessageManager.getMessageOptions(event.getPlayer()).remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public static MessageOptions getMessageOptions(Player player) {
		MessageOptions messageOptions = MessageManager.playerMessageOptions.get(player.getUniqueId());

		if (messageOptions != null)
			return messageOptions;

		return MessageManager.playerMessageOptions.put(player.getUniqueId(), new MessageOptions(player));
	}

	public static class MessageOptions {

		private Player player;

		private boolean loaded = false;

		private Map<MessageType, List<ChatMessage>> messageQueue = new ConcurrentHashMap<>();
		private Map<MessageType, Boolean> messageTagBooleans = new ConcurrentHashMap<>();

		public MessageOptions(Player player) {
			this.player = player;

			// Load default values for now in case database takes too long to load
			for (MessageType messageType : MessageType.values()) {
				MessageOptions.this.messageTagBooleans.put(messageType, messageType.getDefault());
			}

			MessageManager.playerMessageOptions.put(player.getUniqueId(), this);
		}

		public void sendMessageRequest(boolean force, MessageType messageType, BaseComponent[] baseComponents) {
			// FFA World check
			/*if (messageType.equals(MessageType.FFA_MESSAGES)) {
				boolean inFFAWorld = false;
				for (FFAWorld ffaWorld : FFAWorld.getFfaWorlds().values()) {
					if (ffaWorld.getTeamsToPlayers().contains(this.player)) {
						inFFAWorld = true;
						break;
					}
				}

				if (!inFFAWorld) return;
			}*/

			// Have the player's message options loaded?
			if (this.messageTagBooleans.isEmpty()) {
				// Fuck the queue for fancy messages
				return;
			}

			// Do they have these types of messages enabled?
			if (force || this.messageTagBooleans.get(messageType)) {
				this.player.spigot().sendMessage(baseComponents);
			}
		}

		public void sendMessageRequest(boolean force, MessageType messageType, ChatMessage chatMessage) {
			// FFA World check
			if (messageType.equals(MessageType.FFA_MESSAGES)) {
				boolean inFFAWorld = false;
				for (FFAWorld ffaWorld : FFAWorld.getFfaWorlds().values()) {
					if (ffaWorld.getPlayers().contains(this.player)) {
						inFFAWorld = true;
						break;
					}
				}

				if (!inFFAWorld) return;
			}

			// Have the player's message options loaded?
			if (!this.loaded) {
				// Force is ignored here, but very small edge case that probably will never happen

				// Add to queue
				List<ChatMessage> queue = this.messageQueue.get(messageType);
				if (queue == null) {
					queue = new ArrayList<>();
					this.messageQueue.put(messageType, queue);
				}

				queue.add(chatMessage);
				return;
			}

			// Do they have these types of messages enabled?
			if (force || this.messageTagBooleans.get(messageType)) {
				chatMessage.sendTo(this.player);
			}
		}

		public void remove() {
			MessageManager.playerMessageOptions.remove(this.player.getUniqueId());
		}

		public boolean toggleMessageOption(MessageType messageType) {
			Boolean bool = this.messageTagBooleans.get(messageType);

			// Does message type not exist for this player?
			if (bool == null) {
				// Add it
				bool = messageType.getDefault();
				this.messageTagBooleans.put(messageType, messageType.getDefault());
			} else {
				this.messageTagBooleans.put(messageType, !bool);
			}

			BukkitUtil.runTaskAsync(new Runnable() {
				@Override
				public void run() {
					String query = "UPDATE potion_message_options SET message_options = ? WHERE uuid = ?;";

					Connection connection = null;
					PreparedStatement ps = null;

					try {
						connection = Gberry.getConnection();
						ps = connection.prepareStatement(query);

						ps.setBytes(1, MessageOptions.this.serializeMessageOptions());
						ps.setString(2, MessageOptions.this.player.getUniqueId().toString());

						Gberry.executeUpdate(connection, ps);

					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						Gberry.closeComponents(ps, connection);
					}
				}
			});

			return !bool;
		}

		private void handleQueuedMessageRequests() {
			for (MessageType messageType : this.messageQueue.keySet()) {
				// Do they have these types of messages enabled?
				if (this.messageTagBooleans.get(messageType)) {
					for (ChatMessage chatMessage : this.messageQueue.get(messageType)) {
						chatMessage.sendTo(this.player);
					}
				}
			}

			this.messageQueue.clear();
		}

		/**
		 * Should be called ASYNC
		 */
		private void fetchMessageOptions(Connection connection) {
			String query = "SELECT * FROM potion_message_options WHERE uuid = ?;";

			PreparedStatement ps = null;
			PreparedStatement ps2 = null;
			ResultSet rs = null;

			try {
				ps = connection.prepareStatement(query);
				ps.setString(1, MessageOptions.this.player.getUniqueId().toString());
				rs = Gberry.executeQuery(connection, ps);

				if (rs.next()) {
					MessageOptions.this.deserializeMessageOptions(rs.getBytes("message_options"));
				} else {
					// Insert default records into database
					query = "INSERT INTO potion_message_options (uuid, message_options) VALUES (?, ?);";

					try {
						ps2 = connection.prepareStatement(query);
						ps2.setString(1, MessageOptions.this.player.getUniqueId().toString());
						ps2.setBytes(2, MessageOptions.this.serializeMessageOptions());

						Gberry.executeUpdate(connection, ps2);
					} catch (SQLException e) {
						// Ignore exception, this is normal
						//e.printStackTrace();
					} finally {
						if (ps2 != null) {
							try {
								ps2.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					}
				}

				MessageOptions.this.loaded = true;

				MessageOptions.this.handleQueuedMessageRequests();

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				Gberry.closeComponents(rs, ps);
			}
		}

		private byte[] serializeMessageOptions() {
			StringBuilder sb = new StringBuilder();

			for (MessageType messageType : this.messageTagBooleans.keySet()) {
				sb.append(messageType.name());
				sb.append(":");
				sb.append(this.messageTagBooleans.get(messageType));
				sb.append(",");
			}

			try {
				return CompressionUtil.compress(sb.toString());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		private void deserializeMessageOptions(byte[] bytes) {
			try {
				String[] messageOptions = CompressionUtil.decompress(bytes).split(",");

				for (String messageOption : messageOptions) {
					if (!messageOption.isEmpty()) {
						try {
							String[] strings = messageOption.split(":");

							this.messageTagBooleans.put(MessageType.valueOf(strings[0]), Boolean.valueOf(strings[1]));
						} catch (IllegalArgumentException e) {
							// MessageType.valueOf(strings[0]) not found, will automatically remove the record
						}
					}
				}

				// Check if they have all the message tags
				for (MessageType messageType : MessageType.values()) {
					if (this.messageTagBooleans.get(messageType) == null) {
						this.toggleMessageOption(messageType);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public boolean isLoaded() {
			return loaded;
		}

		public boolean getMessageTagBoolean(MessageType messageType) {
			return this.messageTagBooleans.get(messageType);
		}

	}

}
