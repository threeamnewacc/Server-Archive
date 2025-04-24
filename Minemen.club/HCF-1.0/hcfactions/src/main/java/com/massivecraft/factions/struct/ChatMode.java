package com.massivecraft.factions.struct;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ChatMode {
	FACTION("Faction"),
	ALLIANCE("Alliance"),
	PUBLIC("Public");

	private final String name;

	public ChatMode getNext() {
		if (this == PUBLIC) {
			return ALLIANCE;
		}
		if (this == ALLIANCE) {
			return FACTION;
		}
		return PUBLIC;
	}
}
