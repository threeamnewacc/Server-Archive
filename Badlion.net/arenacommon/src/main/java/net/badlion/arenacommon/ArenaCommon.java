package net.badlion.arenacommon;

public class ArenaCommon {

	public enum LadderType {

		UNRANKED_1V1(false, "1v1 Unranked", "1u"),
		RANKED_1V1(true, "1v1 Ranked", "1r"),
		RANKED_2V2(true, "2v2 Ranked", "2r"),
		RANKED_3V3(true, "3v3 Ranked", "3r"),
		DUEL(false, "Duel", "duel"),
		TOURNAMENT(false, "Tournament", "t"),
		PARTY_FFA(false, "Team FFA", "ffa"),
		PARTY_TEAM(false, "Team Fight", "team"),
		PARTY_RED_ROVER_DUEL(false, "Party Red Rover Duel", "rrd"),
		PARTY_RED_ROVER_BATTLE(false, "Team Red Rover Battle", "rrb"),
		RANKED_5V5_CLAN(true, "5v5 Clan Ranked", "5c");

		private final boolean ranked;
		private final String niceName;
		private final String tag;

		LadderType(boolean ranked, String niceName, String tag) {
			this.ranked = ranked;
			this.niceName = niceName;
			this.tag = tag;
		}

		public boolean isRanked() {
			return ranked;
		}

		public String getNiceName() {
			return this.niceName;
		}

		public String getTag() {
			return tag;
		}
	}

	public enum EventType {

		FFA("FFA", "ffa"),
		UHCMEETUP("UHC Meetup", "ffa");

		private final String niceName;
		private final String tag;

		EventType(String niceName, String tag) {
			this.niceName = niceName;
			this.tag = tag;
		}

		public String getNiceName() {
			return this.niceName;
		}

		public String getTag() {
			return tag;
		}
	}

	public enum ArenaType {
		// TODO: GAPPLE IS NEVER USED?
		// NOTE: RED_ROVER IS UNUSED, ID/SLOT CAN BE USED FOR FUTURE ARENA TYPE
		PEARL, NON_PEARL, BUILD_UHC, HORSE, SOUP, ARCHER, LMS, WAR, SLAUGHTER, UHC_MEETUP,
		INFECTION, KOTH, RED_ROVER, PARTY_FFA, SKYWARS, TDM, SPLEEF, SPLEEF_FFA, BUILD_UHC_FFA, GAPPLE
	}

}
