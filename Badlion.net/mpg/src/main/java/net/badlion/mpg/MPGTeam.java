package net.badlion.mpg;

import net.badlion.mpg.kits.MPGKit;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.managers.MPGTeamManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MPGTeam {

	// A general Team with no relationship between team members other than friendly fire

	public static final ChatColor[] TEAM_COLORS =
			new ChatColor[] {
			ChatColor.RED, ChatColor.BLUE, ChatColor.GREEN, ChatColor.YELLOW,
			ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_RED,
			ChatColor.DARK_PURPLE, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.AQUA,
			ChatColor.LIGHT_PURPLE, ChatColor.GOLD, ChatColor.WHITE };

	private static int CURRENT_TEAM_NUMBER = 1;

    private Set<UUID> uuids = new LinkedHashSet<>();

	private int clanId;
	private String clanName;

	private int teamNumber = -1;

	private ChatColor color;

	private String teamName;

	private String prefix;
	private int kills = 0;

    private int deaths = 0;

	private Location respawnLocation;

	public static String getTeamNameFromColor(ChatColor color) {
		// Safety check
		if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.TEAM_NUMBERS)) {
			throw new RuntimeException("Tried getting team name from color while TEAM_NUMBERS config flag is enabled");
		}

		String teamName = "";

		String[] s = color.name().split("_");
		for (String s2 : s) {
			teamName += s2.substring(0, 1).toUpperCase() + s2.substring(1).toLowerCase();
		}

		return teamName;
	}

	/**
	 * Creates a team with the given team name.
	 *
	 * Called directly only for single player teams,
	 * team name being the player's username.
	 */
	public MPGTeam(String teamName) {
		this.teamName = teamName;

		// Update respawn location
		this.updateRespawnLocation();

		MPGTeamManager.storeTeam(this);
	}

	/**
	 * Creates a team with the given color.
	 *
	 * Called for real team-based games.
	 */
	public MPGTeam(ChatColor color) {
		// Are we using team numbers?
		if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.TEAM_NUMBERS)) {
			this.teamNumber = MPGTeam.CURRENT_TEAM_NUMBER++;
		}

		this.color = color;

		// Create the prefix and team name
		if (this.teamNumber != -1) {
			this.prefix = this.color + "[Team " + this.teamNumber + "]";

			this.teamName = this.color.toString() + this.teamNumber;
		} else {
			// Are we using color name team prefixes?
			if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.COLOR_NAME_TEAM_PREFIXES)) {
				this.prefix = this.color.toString() + "[";

				String[] split = this.color.name().toLowerCase().split("_");
				for (String s : split) {
					this.prefix += s.substring(0, 1).toUpperCase() + s.substring(1, s.length()) + " ";
				}

				this.prefix = this.prefix.substring(0, this.prefix.length() - 1);

				this.prefix += "]";
			} else {
				this.prefix = this.color.toString() + "[Team]";
			}

			this.teamName = this.color.toString();
		}

		if (MPG.ALLOW_RESPAWNING) {
			// Update respawn location
			this.updateRespawnLocation();
		}

		MPGTeamManager.storeTeam(this);
	}

    public void giveKit(MPGKit kit, boolean verbose) {
        for (UUID uuid : this.uuids) {
	        MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(uuid);

            if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
                kit.load(mpgPlayer.getPlayer(), verbose);
            }
        }
    }

	/**
	 * Send a message to the entire team
	 */
	public void sendMessage(String message) {
		for (UUID uuid: this.uuids) {
			Player player = MPG.getInstance().getServer().getPlayer(uuid);
			if (player != null) {
				player.sendMessage(message);
			}
		}
	}

	/**
	 * Send a message to the alive players on the team
	 */
	public void sendMessageAlivePlayers(String message) {
		for (UUID uuid: this.uuids) {
			MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(uuid);

			if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
				mpgPlayer.getPlayer().sendMessage(message);
			}
		}
	}

	/**
	 * Teleports the team to a location
	 */
	public void teleport(Location location) {
		for (UUID uuid : this.uuids) {
			Player player = MPG.getInstance().getServer().getPlayer(uuid);
			if (player != null) {
				player.teleport(location);
			}
		}
	}

	/**
	 * Teleports alive players on the team to a location
	 */
	public void teleportAlivePlayers(Location location) {
		for (UUID uuid : this.uuids) {
			MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(uuid);

			if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
				mpgPlayer.getPlayer().teleport(location);
			}
		}
	}

	public void updateRespawnLocation() {
		this.respawnLocation = MPG.getInstance().getMPGGame().getWorld().getSpawnLocation(this);
	}

	public UUID getLeader() {
		if (this.uuids.size() == 0) {
			return null;
		}

		return this.uuids.iterator().next();
	}

    public List<UUID> getUUIDs() {
        return Collections.unmodifiableList(new ArrayList<>(this.uuids));
    }

    public boolean add(MPGPlayer mpgPlayer) {
        return this.uuids.add(mpgPlayer.getUniqueId());
    }

    public boolean remove(MPGPlayer mpgPlayer) {
        return this.uuids.remove(mpgPlayer.getUniqueId());
    }

	public int size() {
		return this.uuids.size();
	}

	public boolean isFull() {
		return this.size() >= MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.PLAYERS_PER_TEAM);
	}

    public boolean isOnTeam(MPGPlayer mpgPlayer) {
        return this.uuids.contains(mpgPlayer.getUniqueId());
    }

    public boolean isEmpty() {
        return this.uuids.size() == 1;
    }

    public int getKills() {
        return this.kills;
    }

    public void addKill() {
        this.kills += 1;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void addDeath() {
        this.deaths += 1;
    }

	public int getClanId() {
		return this.clanId;
	}

	public void setClanId(int clanId) {
		this.clanId = clanId;
	}

	public String getClanName() {
		return this.clanName;
	}

	public void setClanName(String clanName) {
		this.clanName = clanName;
	}

	public ChatColor getColor() {
		return this.color;
	}

	public String getTeamName() {
        return this.teamName;
    }

	public int getTeamNumber() {
		return this.teamNumber;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public int getTeamSize() {
		return this.uuids.size();
	}

	public Location getRespawnLocation() {
		return this.respawnLocation;
	}

}
