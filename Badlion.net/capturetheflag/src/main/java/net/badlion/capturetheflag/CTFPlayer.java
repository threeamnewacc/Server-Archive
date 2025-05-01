package net.badlion.capturetheflag;

import net.badlion.capturetheflag.manager.FlagManager;
import net.badlion.capturetheflag.manager.SidebarManager;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.kits.MPGKit;
import net.badlion.mpg.managers.MPGKitManager;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class CTFPlayer extends MPGPlayer {

	private CTFTeam carryingFlag = null;

	private int flagsPickedUp;
	private int flagsDelivered;
	private long flagHeldTime;
	private int flagHoldersKilled;

	public CTFPlayer(UUID uuid, String username) {
		super(uuid, username);
	}

	@Override
	public void handlePlayerDeathInternal(LivingEntity livingEntity, Map<String, Object> extraPayload) {
		super.handlePlayerDeathInternal(livingEntity, extraPayload);

		Player killer = livingEntity.getKiller();

		// Was the player carrying the flag?
		if (this.carryingFlag != null) {
			// Gotta set to CREATIVE right away, otherwise this doesnt work
			this.getPlayer().setGameMode(GameMode.CREATIVE);

			// Drop the flag
			FlagManager.dropFlag(this, this.getFlagTeam(), this.getPlayer().getLocation());

			if (killer != null) {
				CTFPlayer killerCTFPlayer = ((CTFPlayer) MPGPlayerManager.getMPGPlayer(killer));
				killerCTFPlayer.incrementFlagHolderKills();
			}

			this.carryingFlag = null;
		}
	}

	@Override
	public void update() {
		final Player player = CTF.getInstance().getServer().getPlayer(this.uuid);

		// Are they offline?
		if (player == null) {
			return;
		}

		// Initialize
		SidebarManager.addSidebar(this);


		/* TODO: Do we want sidebar?
		this.getPlayer().setBossBar();
        */
	}

	@Override
	public Location handlePlayerRespawn(Player player) {
		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);
		MPGKit kit = CTF.getInstance().getCTFGame().getGamemode().getDefaultKit();
		MPGKitManager.loadKit(player, kit, false);
		return mpgPlayer.getTeam().getRespawnLocation();
	}

	@Override
	public CTFTeam getTeam() {
		return (CTFTeam) this.team;
	}

	public boolean isCarryingFlag() {
		return this.carryingFlag != null;
	}

	public CTFTeam getFlagTeam() {
		return this.carryingFlag;
	}

	public void setCarryingFlag(CTFTeam team) {
		this.carryingFlag = team;
	}

	public void incrementFlagsPickedUp() {
		this.flagsPickedUp++;
	}

	public void incrementFlagsDelivered() {
		this.flagsDelivered++;
	}

	public void incrementFlagHolderKills() {
		this.flagHoldersKilled++;
	}

	public void addFlagHeldTime(long amount) {
		this.flagHeldTime += amount;
	}

	public int getFlagsPickedUp() {
		return this.flagsPickedUp;
	}

	public int getFlagsDelivered() {
		return this.flagsDelivered;
	}

	public long getFlagHeldTime() {
		return this.flagHeldTime;
	}

	public int getFlagHoldersKilled() {
		return this.flagHoldersKilled;
	}

}
