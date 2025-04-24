package com.massivecraft.factions.type;

import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.struct.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SubclaimOwner {

	UUID factionId;
	UUID owner;

	List<UUID> addedMembers = new ArrayList<>();

	public boolean canEdit(FactionPlayer player) {
		if (player.getUuid().equals(owner)) {
			return true;
		}
		if (player.getFaction() != null && player.getFaction().getUUID().equals(factionId)) {
			if (player.getRole().isAtLeast(Role.MODERATOR)) {
				return true;
			}
		}
		return false;
	}

}
