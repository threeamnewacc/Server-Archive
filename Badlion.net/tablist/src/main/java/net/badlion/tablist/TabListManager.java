package net.badlion.tablist;

import com.google.common.collect.ImmutableList;
import net.badlion.disguise.events.PlayerDisguiseEvent;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.gberry.utils.tinyprotocol.GameProfileHook;
import net.badlion.gberry.utils.tinyprotocol.GameProfileScheduler;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import net.badlion.gspigot.ProtocolOutHook;
import net.badlion.gspigot.ProtocolScheduler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TabListManager implements Listener {

	private static TabListManager instance;

	private JavaPlugin plugin;

	private BaseComponent[] header;
	private BaseComponent[] footer;

	private ArrayList<UUID> packetUUIDs = new ArrayList<>();
	private LinkedList<String> teamNames = new LinkedList<>();

	private ArrayList<String> blankStrings = new ArrayList<>();

	protected ConcurrentHashMap<Player, TabList> tabLists = new ConcurrentHashMap<>();

	private final ConcurrentHashMap<Integer, String> defaultPacketsMap = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<UUID, ConcurrentHashMap<String, Boolean>> playerTabPackets = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, Boolean>> teamFlags = new ConcurrentHashMap<>();
	// ^ This is a memory leak...might need to clean up later

	public TabListManager(JavaPlugin plugin) {
		Gberry.enableProtocol = true;
		TabListManager.instance = this;
		this.plugin = plugin;

		// Register ourselves
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

		// Create Gberry logging tag
        //Gberry.loggingTags.add("TabList");
        //Gberry.loggingTags.add("TabList2");
        //Gberry.loggingTags.add("TabList3");

		// Initialize all of our lists
		this.fillBlankStrings();
		this.generateTeamNames();
		this.generateRandomUUIDs();
		this.fillDefaultPacketsMap();

		// 1.8 stuff
		this.header = new ComponentBuilder("Welcome to the Badlion Network!").color(ChatColor.GOLD).create();
		this.footer = new ComponentBuilder("NA East Proxy: na.badlion.net                 Website: www.badlion.net\n" +
				"NA West Proxy: naw.badlion.net      Twitter: twitter.com/BadlionNetwork\n" +
				"EU Proxy: eu.badlion.net         Teamspeak: ts.badlion.net\n" +
				"AU Proxy: au.badlion.net               Come listen to music in our TS!").color(ChatColor.GOLD).create();

		ProtocolScheduler.addHook(new ProtocolOutHook() {
			@Override
			public Object handlePacket(Player receiver, Object packet) {
				if (TinyProtocolReferences.tabPacketClass.isInstance(packet)) {
					if (receiver.getClientVersion().ordinal() <= Player.CLIENT_VERSION.V1_7_6.ordinal()) {
						String name = TinyProtocolReferences.tabPacketName.get(packet);
						if (name == null) {
							return null;
						}
						if (name.startsWith("!")) {
							TinyProtocolReferences.tabPacketName.set(packet, name.substring(1));
						} else if (!name.startsWith("@")) { // Probably unneeded since /anon tab packets irrelevant for 1.7 but w/e
							return null;
						}
					} else {
						Object gameProfile = TinyProtocolReferences.tabPacketGameProfile.get(packet);
						int action = TinyProtocolReferences.tabPacketAction.get(packet);

						if (gameProfile != null) {
							String name = TinyProtocolReferences.gameProfileName.get(gameProfile);
							UUID uuid = TinyProtocolReferences.gameProfileUUID.get(gameProfile);

							if (name.startsWith("!")) {
								try {
									// Make new game profile
									Object fakeGameProfile = TinyProtocolReferences.gameProfileConstructor.invoke(uuid, name.substring(1));

									Object property = TinyProtocolReferences.propertyConstructor.invoke("textures", TabList.BLANK_TEXTURE_VALUE, TabList.BLANK_TEXTURE_SIG);

									Object properties = TinyProtocolReferences.gameProfilePropertyMap.get(fakeGameProfile);
									TinyProtocolReferences.propertyMapPut.invoke(properties, "textures", property);

									// Do any hooks (such as textures)                                           `
									for (GameProfileHook hook : GameProfileScheduler.getHooks()) {
										fakeGameProfile = hook.handleGameProfile(fakeGameProfile);
									}

									TinyProtocolReferences.tabPacketGameProfile.set(packet, fakeGameProfile);
								} catch (Exception e) {
									e.printStackTrace();
									Bukkit.getLogger().info("Something went wrong when creating a gameProfileClass");
									return null;
								}
							} else if (action == 0) { // Adding a player
								// 12/7/15 - Return out early (fixes scoreboard color problem on potpvp)
								ConcurrentHashMap<UUID, Boolean> flags = TabListManager.teamFlags.get(receiver.getUniqueId());
								if (flags == null) {
									flags = new ConcurrentHashMap<>();
									TabListManager.teamFlags.put(receiver.getUniqueId(), flags);
								}

								Boolean found = flags.get(uuid);
								if (found == null) {
									flags.put(uuid, true);
								} else {
									return packet; // Return out early
								}

								// Send a team default packet
								try {
									Object teamPacket = TinyProtocolReferences.scoreboardTeamPacket.newInstance();
									Collection names = TinyProtocolReferences.teamScoreboardPacketList.get(teamPacket);
									names.add(name);
									TinyProtocolReferences.teamScoreboardPacketAction.set(teamPacket, 3);
									TinyProtocolReferences.teamScoreboardPacketRegisteredName.set(teamPacket, ScoreboardUtil.DEFAULT_TEAM_NAME);

									Gberry.protocol.sendPacket(receiver, teamPacket);
								} catch (Exception e) {
									Gberry.log("TabList", e.toString());
									Gberry.log("TabList", e.getMessage());
									Gberry.log("TabList", "Error sending packet to client");
									e.printStackTrace();
								}
							}
						}
					}
				}

				return packet;
			}

			@Override
			public ProtocolPriority getPriority() {
				return ProtocolPriority.LAST;
			}
		});
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		ScoreboardUtil.resetScoreboardWithoutDefaultPlayers(player);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		TabListManager.teamFlags.remove(event.getPlayer().getUniqueId()); // Clean out some of the memory
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerDisguiseEvent(PlayerDisguiseEvent event) {
		for (Player player : this.plugin.getServer().getOnlinePlayers()) {
			ScoreboardUtil.addEntryToDefaultTeam(player.getScoreboard(), event.getDisguiseName());
		}
	}

	public TabList getTabList(Player player) {
		return this.tabLists.get(player);
	}

	public TabList removeTabList(Player player) {
		return this.tabLists.remove(player);
	}

    public void setAllTabListPositions(final Map<Integer, String> tabChanges) {
        this.setAllTabListPositions(tabChanges, false);
    }

	/**
	 * Sets a position in the tab list for
	 * all players and automatically updates
	 */
	public void setAllTabListPositions(final Map<Integer, String> tabChanges, final boolean flush) {
		final ImmutableList<Player> players = ImmutableList.copyOf(this.tabLists.keySet());
		int size = players.size();
		int diff = (int) Math.ceil((double) players.size() / 40D);
		for (int i = 0, j = 0; i < size; i += diff) {
			if (i >= size) return;
			final int start = i;
			final int end = i + diff;
			this.plugin.getServer().getScheduler().runTaskLater(TabListManager.this.plugin, new Runnable() {
				@Override
				public void run() {
                    try {
                        for (int i = start; i < end; ++i) {
                            if (i >= players.size()) return;

                            Player player = players.get(i);

                            if (player.isOnline()) {
                                TabList tabList = TabListManager.this.tabLists.get(player);

                                for (Integer id : tabChanges.keySet()) {
                                    // They logged off
                                    if (tabList == null) {
                                        break;
                                    }

                                    String val = tabChanges.get(id);
                                    tabList.setPosition(id, val, !flush);
                                }

                                if (tabList != null && flush) {
                                    tabList.update(true);
                                } else if (tabList != null && tabList.getVersion() == TabList.TAB_LIST_VERSION.V1_7) {
                                    tabList.update(false);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
				}
			}, ++j);
		}
	}

	private void fillBlankStrings() {
		int characterCounter = 0;

		while (characterCounter < 15) {
			StringBuilder sb = new StringBuilder();
			characterCounter++;
			for (int x = 0; x <= characterCounter; x++) {
				sb.append(" ");
			}
			this.blankStrings.add(sb.toString());
		}
		characterCounter = 0;

		while (characterCounter < 15) {
			StringBuilder sb = new StringBuilder();
			characterCounter++;
			for (int x = 0; x <= characterCounter; x++) {
				sb.append((char) 0x26f7);
			}
			this.blankStrings.add(sb.toString());
		}
		characterCounter = 0;

		while (characterCounter < 15) {
			StringBuilder sb = new StringBuilder();
			characterCounter++;
			for (int x = 0; x <= characterCounter; x++) {
				sb.append((char) 0x26f8);
			}
			this.blankStrings.add(sb.toString());
		}
		characterCounter = 0;

		while (characterCounter < 15) {
			characterCounter++;
			StringBuilder sb = new StringBuilder();
			for (int x = 0; x <= characterCounter; x++) {
				sb.append((char) 0x26c7);
			}
			this.blankStrings.add(sb.toString());
		}
		characterCounter = 0;

		while (characterCounter < 15) {
			characterCounter++;
			StringBuilder sb = new StringBuilder();
			for (int x = 0; x <= characterCounter; x++) {
				sb.append((char) 0x26c9);
			}
			this.blankStrings.add(sb.toString());
		}
		characterCounter = 0;

		while (characterCounter < 5) {
			characterCounter++;
			StringBuilder sb = new StringBuilder();
			for (int x = 0; x <= characterCounter; x++) {
				sb.append((char) 0x26cc);
			}
			this.blankStrings.add(sb.toString());
		}
	}

	private void generateTeamNames() {
		int counter = 0;
		StringBuilder sb = new StringBuilder("");
		while (counter < 80) {
			//System.out.println(counter + ": " + sb.toString());

            if (counter < 10) {
			    this.teamNames.add("\\u00010" + counter);
            } else {
                this.teamNames.add("\\u0001" + counter);
            }

			sb.setLength(0);
			counter++;
		}
	}

	private void generateRandomUUIDs() {
		for (int x = 0; x < 80; x++) {
			String uuid = "00000000-0000-0000-0000-0000000000";
			if (x < 10) {
				uuid += "0";
			}

			this.packetUUIDs.add(UUID.fromString(uuid + x));
		}
	}

	private void fillDefaultPacketsMap() {
		for (int i = 1; i < 81; i++) {
			this.defaultPacketsMap.put(i, "");
		}
	}

	public static TabListManager getInstance() {
		return TabListManager.instance;
	}

	public JavaPlugin getPlugin() {
		return this.plugin;
	}

	public BaseComponent[] getHeader() {
		return header;
	}

	public BaseComponent[] getFooter() {
		return footer;
	}

	ArrayList<UUID> getPacketUUIDs() {
		return packetUUIDs;
	}

	LinkedList<String> getTeamNames() {
		return teamNames;
	}

	ArrayList<String> getBlankStrings() {
		return blankStrings;
	}

	ConcurrentHashMap<Integer, String> getDefaultPacketsMap() {
		return defaultPacketsMap;
	}

}
