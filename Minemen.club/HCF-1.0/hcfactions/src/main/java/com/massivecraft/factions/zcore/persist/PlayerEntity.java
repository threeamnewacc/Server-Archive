package com.massivecraft.factions.zcore.persist;

import club.minemen.hcfactions.HCFactions;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlayerEntity extends Entity {

//	private transient Player player;
	private transient UUID uniqueId;

	public Player getPlayer() {
		return HCFactions.getInstance().getServer().getPlayer(this.uniqueId);
	}

	public void setPlayer(Player player) {
		if(player != null) {
			this.uniqueId = player.getUniqueId();
		}
	}

	public boolean isOnline() {
		return this.getPlayer() != null;
	}

	// make sure target player should be able to detect that this player is online
	public boolean isOnlineAndVisibleTo(Player player) {
		Player target = this.getPlayer();
		return target != null && player.canSee(target);
	}

	public boolean isOffline() {
		return !isOnline();
	}

	// -------------------------------------------- //
	// Message Sending Helpers
	// -------------------------------------------- //
	public void sendMessage(String msg) {
		Player player = this.getPlayer();
		if (player == null) {
			return;
		}
		player.sendMessage(msg);
	}

	public void sendMessage(List<String> msgs) {
		for (String msg : msgs) {
			this.sendMessage(msg);
		}
	}

	public UUID getUuid() {
		return UUID.fromString(this.id);
	}

}
