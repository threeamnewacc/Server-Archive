package com.massivecraft.factions.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.massivecraft.factions.type.ChatCallback;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatCallbackManager {

	private final Cache<UUID, ChatCallback> chatCallbacks = CacheBuilder.newBuilder().expireAfterWrite(ChatCallback.TTL, TimeUnit.MILLISECONDS).build();

	public void setChatCallback(Player player, ChatCallback callback) {
		chatCallbacks.put(player.getUniqueId(), callback);
	}

	public ChatCallback takeChatCallback(Player player) {
		ChatCallback callback = chatCallbacks.asMap().remove(player.getUniqueId());
		if (callback == null || callback.expired()) {
			return null;
		}
		return callback;
	}

}
