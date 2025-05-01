package net.badlion.smellychat;

import org.bukkit.entity.Player;

public interface ChannelHandler {

	/**
	 * Responsible for handling chat communicate in certain channels
	 *
	 * @param player  Player
	 * @param message Message to send
	 * @param channel Channel to send message to
	 */
	void sendMessageToChannel(Player player, String message, Channel channel);

}
