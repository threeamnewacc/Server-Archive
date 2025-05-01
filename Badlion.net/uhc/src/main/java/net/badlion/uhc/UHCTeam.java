package net.badlion.uhc;

import com.google.common.base.Joiner;
import net.badlion.combattag.CombatTagPlugin;
import net.badlion.combattag.LoggerNPC;
import net.badlion.common.libraries.EnumCommon;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.managers.UHCTeamManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UHCTeam {

    private static int globalTeamNumber = 0;

	public enum GameType {
		SOLO,
		TEAM
	}

	private ArrayList<UUID> uuids = new ArrayList<>();

    private String prefix;
    private int teamNumber;
	private ChatColor chatColor;

    private int kills = 0;

	/**
	 * Creates a team with the given team leader
	 *
	 * @param uuid - Team leader
	 */
	public UHCTeam(UUID uuid) {
        this.uuids.add(uuid);

        UHCTeamManager.addUHCTeam(this);

		// lol no
		if (UHCTeam.globalTeamNumber == 69) {
			UHCTeam.globalTeamNumber++;
		}

		if (uuid.equals(UUID.fromString("e3020512-5ba9-4104-82dc-f1c4dcba553c"))) { // For Gberry ;3
			this.teamNumber = 0;

			// No one else get's to be team 0 ^.^
			if (UHCTeam.globalTeamNumber == 0) {
				UHCTeam.globalTeamNumber++;
			}
		} else {
			this.teamNumber = UHCTeam.globalTeamNumber++;
		}

		this.chatColor = BadlionUHC.getInstance().getRandomTeamChatColor();
        this.prefix = this.chatColor + "[Team " + this.teamNumber + "]";
	}

	public void damage(double amount) {
		this.damage(amount, null);
	}

	public void damage(double amount, UUID toIgnore) {
		for (UUID uuid : this.uuids) {
			if (toIgnore != null && uuid.equals(toIgnore)) {
				continue;
			}

			UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(uuid);
			if (uhcPlayer.isAliveAndPlaying()) {
				Player pl = BadlionUHC.getInstance().getServer().getPlayer(uuid);
				if (pl != null) {
					if (pl.getHealth() - amount < 0) {
						pl.setHealth(0);
					} else {
						pl.setHealth(pl.getHealth() - amount);
					}

					pl.playSound(pl.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "HURT_FLESH", "ENTITY_PLAYER_HURT"), 1f, 1f);
				} else {
					// Get combat tag
					LoggerNPC loggerNPC = CombatTagPlugin.getInstance().getLogger(uuid);
					if (loggerNPC != null) {
						if (loggerNPC.getEntity().getHealth() - amount < 0) {
							loggerNPC.getEntity().setHealth(0);
						} else {
							loggerNPC.getEntity().setHealth(loggerNPC.getEntity().getHealth() - amount);
						}
					}
				}
			}
		}
	}

	public void heal(double amount) {
		this.heal(amount, null);
	}

	public void heal(double amount, UUID toIgnore) {
		for (UUID uuid : this.uuids) {
			if (toIgnore != null && uuid.equals(toIgnore)) {
				continue;
			}

			UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(uuid);
			if (uhcPlayer.isAliveAndPlaying()) {
				Player pl = BadlionUHC.getInstance().getServer().getPlayer(uuid);
				if (pl != null) {
					if (pl.getHealth() + amount > 20) {
						pl.setHealth(20);
					} else {
						pl.setHealth(pl.getHealth() + amount);
					}
				} else {
					// Get combat tag
					LoggerNPC loggerNPC = CombatTagPlugin.getInstance().getLogger(uuid);
					if (loggerNPC != null) {
						if (loggerNPC.getEntity().getHealth() + amount > 20) {
							loggerNPC.getEntity().setHealth(20);
						} else {
							loggerNPC.getEntity().setHealth(loggerNPC.getEntity().getHealth() + amount);
						}
					}
				}
			}
		}
	}

	public void addAbsorption() {
	 	this.addAbsorption(null);
	}

	public void addAbsorption(UUID toIgnore) {
		for (UUID uuid : this.uuids) {
			if (toIgnore != null && uuid.equals(toIgnore)) {
				continue;
			}

			UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(uuid);
			if (uhcPlayer.isAliveAndPlaying()) {
				Player pl = BadlionUHC.getInstance().getServer().getPlayer(uuid);
				if (pl != null) {
					pl.removePotionEffect(PotionEffectType.ABSORPTION);

					pl.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 60 * 2, 0), true);
				} else {
					// Get combat tag
					LoggerNPC loggerNPC = CombatTagPlugin.getInstance().getLogger(uuid);
					if (loggerNPC != null) {
						loggerNPC.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 60 * 2, 0), true);
					}
				}
			}
		}
	}

	public int getSize() {
		return this.uuids.size();
	}

	public UUID getLeader() {
		return this.uuids.get(0);
	}

	public ArrayList<UUID> getUuids() {
		return uuids;
	}

	public void addPlayer(UUID uuid) {
		this.uuids.add(uuid);
	}

	public void removePlayer(UUID uuid) {
		this.uuids.remove(uuid);
	}

    public String getPrefix() {
        return prefix;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public void addKill() {
        this.kills += 1;
    }

    public int getKills() {
        return this.kills;
    }

	public ChatColor getChatColor() {
		return chatColor;
	}

	public void setChatColor(ChatColor chatColor) {
		this.chatColor = chatColor;
	}

	@Override
	public String toString() {
		List<String> names = new ArrayList<>();
		for (UUID uuid : this.uuids) {
			names.add(BadlionUHC.getInstance().getUsername(uuid));
		}

		return "Team #" + this.teamNumber + " " +Joiner.on(", ").join(names);
	}
}
