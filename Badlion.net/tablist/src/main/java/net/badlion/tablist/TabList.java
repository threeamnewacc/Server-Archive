package net.badlion.tablist;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TabList {

	public enum TAB_LIST_VERSION {V1_7, V1_8}

	private final Player player;
	private final TAB_LIST_VERSION version;

	private final int min;
	private final int max;

	private Scoreboard scoreboard;

	private ArrayList<String> sentPackets;
	public final String[][] packetsArray = new String[20][3];
	private final ConcurrentHashMap<Integer, String> packets;

    public static String BLANK_TEXTURE_VALUE = "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=";
    public static String BLANK_TEXTURE_SIG = "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw=";

	/**
	 * Constructor is inclusive and inclusive: [min, max]
	 */
	public TabList(Player player, int min, int max) {
		if (player.getClientVersion().ordinal() <= Player.CLIENT_VERSION.V1_7_6.ordinal()) {
			this.version = TAB_LIST_VERSION.V1_7;
			this.packets = null;
		} else {
			this.version = TAB_LIST_VERSION.V1_8;

			// Set default packets map
			this.packets = new ConcurrentHashMap<>(TabListManager.getInstance().getDefaultPacketsMap());
		}

		this.player = player;
		this.scoreboard = player.getScoreboard();//TabList.getScoreboard(player);
		this.min = min;
		this.max = max;
	}

	/**
	 * Sets a position in the tab list
	 * <p/>
	 * Need to call refreshLegacyTabList()
	 * for legacy players (can be done after
	 * numerous position changes)
	 * <p/>
	 * Throws IllegalArgumentException if arguments are invalid
	 */
	public void setPosition(int id, String val, boolean nonLegacyUpdate) throws IllegalArgumentException {
		if (id < this.min || id > this.max) {
			throw new IllegalArgumentException("Position is too low or high for tab list: " + id);
		}

		if (val == null) {
			throw new IllegalArgumentException("Value for entry cannot be null");
		}

		if (val.length() > 16) {
			throw new IllegalArgumentException("Value is too long: " + val);
		}

		if (this.version == TAB_LIST_VERSION.V1_7) { // Store in 2D array
			int first = id;
			int second = 0;
			if (id > 40) {
				first = id - 40;
				second = 2;
			} else if (id > 20) {
				first = id - 20;
				second = 1;
			}

			this.packetsArray[first - 1][second] = val;
		} else { // Update 1.8 tab list
			if (val.equals(this.packets.get(id))) {
				return;
			}

			if (nonLegacyUpdate) {
				if (this.version == TAB_LIST_VERSION.V1_8) {
					this.update1_8Position(id, val);
				}
			} else {
                // Still set the location, just don't send packet update
                this.packets.put(id, val);
            }
		}
	}

	/**
	 * Updates the tab list if not first time
	 *
	 * @param forceNonLegacy - If true, forces tab list
	 *                       to update if non-legacy tab list.
	 *                       Should only be used if scoreboard
	 *                       for non-legacy user changes.
	 *
	 */
	public void update(boolean forceNonLegacy) {
		if (this.version == TAB_LIST_VERSION.V1_7) {
			this.flush1_7();
			this.send1_7TabList();
		} else if (forceNonLegacy) {
			if (this.version == TAB_LIST_VERSION.V1_8) {
				this.flush1_8();
				this.send1_8TabList();
			}
		}
	}

	/**
	 * Updates the tab list for the first time,
	 * sends all non-legacy packets here
	 */
	public void updateInitial() {
		if (this.version == TAB_LIST_VERSION.V1_7) {
			this.send1_7TabList();
		} else if (this.version == TAB_LIST_VERSION.V1_8) {
			this.send1_8TabList();
		}
	}

	private void update1_8Position(int id, String val) {
		Scoreboard board = this.player.getScoreboard();

		Team team = board.getTeam(TabListManager.getInstance().getTeamNames().get(id - 1)); // Get the team for the ID
		if (team != null) {
			String oldEntry = this.packets.get(id);

			// Add new value
			this.packets.put(id, val);

            try {
				Object gameProfile = TinyProtocolReferences.gameProfileConstructor.invoke(TabListManager.getInstance().getPacketUUIDs().get(id - 1), "!" + oldEntry);

                Object packet = TinyProtocolReferences.tabPacketClass.newInstance();
                TinyProtocolReferences.tabPacketAction.set(packet, 4);
				TinyProtocolReferences.tabPacketGameProfile.set(packet, gameProfile);

                Gberry.protocol.sendPacket(this.player, packet);
            } catch (Exception e) {
                Gberry.log("TabList", "Error sending packet to client");
                e.printStackTrace();
            }

			team.removeEntry(oldEntry);

			try {
				Object gameProfile = TinyProtocolReferences.gameProfileConstructor.invoke(TabListManager.getInstance().getPacketUUIDs().get(id - 1), "!" + val);

				Object packet = TinyProtocolReferences.tabPacketClass.newInstance();
				TinyProtocolReferences.tabPacketAction.set(packet, 0);
				TinyProtocolReferences.tabPacketGameProfile.set(packet, gameProfile);

				Gberry.protocol.sendPacket(this.player, packet);
			} catch (Exception e) {
				Gberry.log("TabList", "Error sending packet to client");
				e.printStackTrace();
			}

            team.addEntry(val);
		} else {
			Gberry.log("TabList", "Team requested is null");
		}
	}

	/**
	 * Refreshes the tab list for legacy players
	 */
	private void send1_7TabList() {
		ArrayList<String> packetStrings = new ArrayList<>();
		ArrayList<String> blankStringsCopy = new ArrayList<>(TabListManager.getInstance().getBlankStrings());
		for (int y = 0; y < 20; y++) {
			for (int x = 0; x < 3; x++) {
				String tabString = this.packetsArray[y][x];

				if (tabString == null || tabString.equals("")) {
					tabString = blankStringsCopy.get(0);
					blankStringsCopy.remove(0);
				}

				if (tabString.length() > 16) {
					Gberry.log("TabList", "Packet string > 16, replacing: " + tabString);

					tabString = blankStringsCopy.get(0);
					blankStringsCopy.remove(0);
				}

				packetStrings.add(tabString);

				try {
					Object packet = TinyProtocolReferences.tabPacketClass.newInstance();
					TinyProtocolReferences.tabPacketName.set(packet, "!" + tabString);
					TinyProtocolReferences.tabPacketAction.set(packet, 0);
					Gberry.protocol.sendPacket(this.player, packet);
				} catch (Exception e) {
					Gberry.log("TabList", "Error sending packet to client");
					e.printStackTrace();
				}
			}
		}

		this.sentPackets = packetStrings;
	}

	/**
	 * Sends the tab list for the first time
	 * for non-legacy players, should only be
	 * called once when the player logs in
	 */
	private void send1_8TabList() {
		this.scoreboard = this.player.getScoreboard();

		ArrayList<String> blankStringsCopy = new ArrayList<>(TabListManager.getInstance().getBlankStrings());
		for (Integer positionID : this.packets.keySet()) {
			String tabString = this.packets.get(positionID);

			if (tabString == null || tabString.equals("")) {
				tabString = blankStringsCopy.get(0);
				blankStringsCopy.remove(0);
			}

			if (tabString.length() > 16) {
				Gberry.log("TabList", "Packet string > 16, replacing: " + tabString);
				continue;
			}

			// Create a tab team on the player's scoreboard for every packet
			Team team = this.scoreboard.getTeam(TabListManager.getInstance().getTeamNames().get(positionID - 1));
			if (team == null) {
				team = this.scoreboard.registerNewTeam(TabListManager.getInstance().getTeamNames().get(positionID - 1)); // registerNewTeam(String name)
			}

			team.addEntry(tabString);

			try {
				Object packet = TinyProtocolReferences.tabPacketClass.newInstance();
				TinyProtocolReferences.tabPacketAction.set(packet, 0);

				Object gameProfile = TinyProtocolReferences.gameProfileConstructor.invoke(TabListManager.getInstance().getPacketUUIDs().get(positionID - 1), "!" + tabString);

				TinyProtocolReferences.tabPacketGameProfile.set(packet, gameProfile);
                TinyProtocolReferences.tabPacketGamemode.set(packet, 4);

				Gberry.protocol.sendPacket(player, packet);
			} catch (Exception e) {
                Gberry.log("TabList", e.toString());
                Gberry.log("TabList", e.getMessage());
				Gberry.log("TabList", "Error sending packet to client");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Flushes the packets for a legacy player
	 */
	private void flush1_7() {
		for (String s : this.sentPackets) {
			try {
				Object packet = TinyProtocolReferences.tabPacketClass.newInstance();
				TinyProtocolReferences.tabPacketName.set(packet, "!" + s);
				TinyProtocolReferences.tabPacketAction.set(packet, 4); // Remove Player
				Gberry.protocol.sendPacket(this.player, packet);
			} catch (Exception e) {
				Gberry.log("TabList", "Error sending packet to client");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Flushes the packets for a non-legacy player
	 */
	private void flush1_8() {
		for (UUID uuid : TabListManager.getInstance().getPacketUUIDs()) {
			try {
				Object gameProfile = TinyProtocolReferences.gameProfileConstructor.invoke(uuid, "");

				Object packet = TinyProtocolReferences.tabPacketClass.newInstance();
				TinyProtocolReferences.tabPacketAction.set(packet, 4); // Remove Player
				TinyProtocolReferences.tabPacketGameProfile.set(packet, gameProfile);

				Gberry.protocol.sendPacket(this.player, packet);
			} catch (Exception e) {
				Gberry.log("TabList", "[TabMain] Error sending packet to client");
			}
		}
	}

	public TAB_LIST_VERSION getVersion() {
		return this.version;
	}

}
