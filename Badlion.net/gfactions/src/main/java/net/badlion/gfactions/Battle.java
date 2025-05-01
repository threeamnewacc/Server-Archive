package net.badlion.gfactions;

import java.util.HashMap;
import java.util.Map;

public class Battle {

	private Map<String, FightParticipant> participants = new HashMap<>();
	public final static int VERSION = 1;

	public Battle() {

	}

	public Map<String, FightParticipant> getParticipants() {
		return participants;
	}

	public void setParticipants(Map<String, FightParticipant> participants) {
		this.participants = participants;
	}
}
