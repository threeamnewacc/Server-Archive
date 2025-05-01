package net.badlion.potpvp.bukkitevents;

import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.MessageManager;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private MessageManager.MessageType messageType;
	private String message;
	private BaseComponent[] baseComponents;
	private ChatMessage chatMessage;
	private List<Player> recipients;
	private List<Player> force;


	public MessageEvent(MessageManager.MessageType messageType, String message) {
		this.messageType = messageType;
		this.message = message;
		this.chatMessage = PotPvP.getInstance().getServer().createChatMessage(message, false);
	}

	public MessageEvent(MessageManager.MessageType messageType, String message, Player... force) {
		this.messageType = messageType;
		this.message = message;
		this.chatMessage = PotPvP.getInstance().getServer().createChatMessage(message, false);

		List<Player> list = new ArrayList<>();
		Collections.addAll(list, force);
		this.force = list;
	}

	public MessageEvent(MessageManager.MessageType messageType, String message, List<Player> force) {
		this.messageType = messageType;
		this.message = message;
		this.chatMessage = PotPvP.getInstance().getServer().createChatMessage(message, false);

		this.force = force;
	}

	public MessageEvent(MessageManager.MessageType messageType, String message, List<Player> recipients, Player... force) {
		this.messageType = messageType;
		this.message = message;
		this.chatMessage = PotPvP.getInstance().getServer().createChatMessage(message, false);

		this.recipients = recipients;

		List<Player> list = new ArrayList<>();
		Collections.addAll(list, force);
		this.force = list;
	}

	public MessageEvent(MessageManager.MessageType messageType, String message, List<Player> recipients, List<Player>... force) {
		this.messageType = messageType;
		this.message = message;
		this.chatMessage = PotPvP.getInstance().getServer().createChatMessage(message, false);

		this.recipients = recipients;

		List<Player> list = null;
		for (List<Player> list2 : force) {
			if (list == null) {
				list = new ArrayList<>(list2);
				continue;
			}

			for (Player player : list2) {
				list.add(player);
			}
		}
		this.force = list;
	}

	public MessageEvent(MessageManager.MessageType messageType, BaseComponent[] baseComponents) {
		this.messageType = messageType;
		this.baseComponents = baseComponents;
	}

	public MessageEvent(MessageManager.MessageType messageType, BaseComponent[] baseComponents, Player... force) {
		this.messageType = messageType;
		this.baseComponents = baseComponents;

		List<Player> list = new ArrayList<>();
		Collections.addAll(list, force);
		this.force = list;
	}

	public MessageEvent(MessageManager.MessageType messageType, BaseComponent[] baseComponents, List<Player> force) {
		this.messageType = messageType;
		this.baseComponents = baseComponents;

		this.force = force;
	}

	public MessageEvent(MessageManager.MessageType messageType, BaseComponent[] baseComponents, List<Player> recipients, Player... force) {
		this.messageType = messageType;
		this.baseComponents = baseComponents;

		this.recipients = recipients;

		List<Player> list = new ArrayList<>();
		Collections.addAll(list, force);
		this.force = list;
	}

	public MessageEvent(MessageManager.MessageType messageType, BaseComponent[] baseComponents, List<Player> recipients, List<Player>... force) {
		this.messageType = messageType;
		this.baseComponents = baseComponents;

		this.recipients = recipients;

		List<Player> list = null;
		for (List<Player> list2 : force) {
			if (list == null) {
				list = new ArrayList<>(list2);
				continue;
			}

			for (Player player : list2) {
				list.add(player);
			}
		}
		this.force = list;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public MessageManager.MessageType getMessageType() {
		return messageType;
	}

	public String getMessage() {
		return message;
	}

	public BaseComponent[] getBaseComponents() {
		return baseComponents;
	}

	public ChatMessage getChatMessage() {
		return chatMessage;
	}

	public List<Player> getRecipients() {
		return recipients;
	}

	public List<Player> getForce() {
		return force;
	}

}
