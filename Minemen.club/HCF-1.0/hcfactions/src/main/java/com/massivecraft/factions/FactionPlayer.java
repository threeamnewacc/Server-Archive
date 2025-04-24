package com.massivecraft.factions;

import club.minemen.clublibrary.util.CC;
import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionPostDisbandEvent;
import com.massivecraft.factions.event.LandClaimEvent;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.NameTagMode;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.type.ChunkWalls;
import com.massivecraft.factions.type.ProtectionTimer;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.factions.zcore.persist.PlayerEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BlockVector;
import static net.md_5.bungee.api.ChatColor.GOLD;

/**
 * Logged in players always have exactly one FactionPlayer instance. Logged out
 * players may or may not have an FactionPlayer instance. They will always have one if
 * they are part of a faction. This is because only players with a faction are
 * saved to disk (in order to not waste disk space).
 * <plugin>
 * The FactionPlayer is linked to a minecraft player using the player name.
 * <plugin>
 * The same instance is always returned for the same player. This means you can
 * use the == operator. No .equals method necessary.
 */
public class FactionPlayer extends PlayerEntity implements RelationParticipator, Comparable<FactionPlayer> {

	private String name;
	// private transient String playerName;
	private transient FLocation lastStoodAt = new FLocation(); // Where did this player stand the last time we checked?
	private String factionId;
	private Role role;
	private long lastLoginTime;
	private long joinedFactionTime = System.currentTimeMillis();
	public final transient ChunkWalls eventZoneWalls = new ChunkWalls(Material.STAINED_GLASS, 10, FactionPlayer::denyEventZoneEntry, Faction::isEvent);
	private transient boolean mapAutoUpdating;
	private transient Faction autoClaimFor;
	private transient boolean physicalMapUpdating;
	private transient boolean physicalWallMapUpdating;
	private transient boolean autoSafeZoneEnabled;
	private transient boolean autoWarZoneEnabled;
	private transient boolean isAdminBypassing = false;
	private transient boolean loginPvpDisabled;
	private transient boolean deleteMe;
	private transient boolean spyingChat = false;

	@Getter
	@Setter
	private transient ChatMode chatMode = ChatMode.PUBLIC;

	@Setter
	@Getter
	private transient long lastPvpTagEndPortalMessage = System.currentTimeMillis();

	// Pvp Protection
	private transient long pvpTagEnd = System.currentTimeMillis() - 1000;
	public final transient ChunkWalls safezoneWalls = new ChunkWalls(Material.STAINED_GLASS, 4, FactionPlayer::denySafeZoneEntry, Faction::isSafeZone);


	// Deathban
	private long pvpProtectionTimeLeft = Conf.pvpProtectionTime * 60 * 1000;
	public final transient ChunkWalls pvpProtWalls = new ChunkWalls(Material.STAINED_GLASS, 14, FactionPlayer::denyPvpProtEntry, Faction::denyPvpProtEntry);
	private transient BukkitTask protectionCountdownTask = null;
	private long deathbanTimeStamp = 0L;
	private long deathbanDuration = 0L;
	private transient long lastDeathbannedJoinTime = 0L;
	private transient Set<BlockVector> physicalMapWalls = null;
	private transient Set<BlockVector> physicalWallMapWalls = null;
	private transient ArrayList<UUID> factionMapList = new ArrayList<>();
	private transient long lastPvpProtWallMessage = System.currentTimeMillis();
	private transient NameTagMode nameTagMode = NameTagMode.RELATION;

	// -------------------------------------------- //
	// Construct
	// -------------------------------------------- //
	// GSON need this noarg constructor.
	public FactionPlayer() {
		this.resetFactionData();
		this.lastLoginTime = System.currentTimeMillis();
		this.mapAutoUpdating = false;
		this.physicalMapUpdating = false;
		this.physicalWallMapUpdating = false;
		this.autoClaimFor = null;
		this.autoSafeZoneEnabled = false;
		this.autoWarZoneEnabled = false;
		this.deleteMe = false;

		if (!Conf.newPlayerStartingFactionID.equals("0") && Factions.getInstance().exists(Conf.newPlayerStartingFactionID)) {
			this.factionId = Conf.newPlayerStartingFactionID;
		}
	}

	public Faction getFaction() {
		if (this.factionId == null) {
			return null;
		}
		return Factions.getInstance().getById(this.factionId);
	}

	public void setFaction(Faction faction) {
		//HCFactions.getInstance().getLogger().info(getName() + " " + factionId + " -> " + faction.getId());
		Faction oldFaction = this.getFaction();
		if (oldFaction != null) {
			oldFaction.removeFPlayer(this);
		}
		faction.addFPlayer(this);
		this.factionId = faction.getId();
		this.joinedFactionTime = System.currentTimeMillis();
		if (isOnline()) {
			updateNearbyPlayersTeams();
		}
	}

	public String getFactionId() {
		return this.factionId;
	}

	public boolean hasFaction() {
		return !factionId.equals("0");
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Faction getAutoClaimFor() {
		return autoClaimFor;
	}

	public void setAutoClaimFor(Faction faction) {
		this.autoClaimFor = faction;
		if (this.autoClaimFor != null) {
			// TODO: merge these into same autoclaim
			this.autoSafeZoneEnabled = false;
			this.autoWarZoneEnabled = false;
		}
	}

	public boolean isAutoSafeClaimEnabled() {
		return autoSafeZoneEnabled;
	}

	public void setIsAutoSafeClaimEnabled(boolean enabled) {
		this.autoSafeZoneEnabled = enabled;
		if (enabled) {
			this.autoClaimFor = null;
			this.autoWarZoneEnabled = false;
		}
	}

	public boolean isAutoWarClaimEnabled() {
		return autoWarZoneEnabled;
	}

	public void setIsAutoWarClaimEnabled(boolean enabled) {
		this.autoWarZoneEnabled = enabled;
		if (enabled) {
			this.autoClaimFor = null;
			this.autoSafeZoneEnabled = false;
		}
	}

	public boolean isAdminBypassing() {
		return this.isAdminBypassing;
	}

	public void setIsAdminBypassing(boolean val) {
		this.isAdminBypassing = val;
	}

	public boolean isSpyingChat() {
		return spyingChat;
	}

	public void setSpyingChat(boolean chatSpying) {
		this.spyingChat = chatSpying;
	}

	// FIELD: account
	public String getAccountId() {
		return this.getId();
	}

	public void pvpTag(int seconds) {
		boolean add = !isPvpTagged();
		pvpTagEnd = Math.max(System.currentTimeMillis() + seconds * 1000, pvpTagEnd);
		if (add) {
			if (getPlayer() != null) {
				safezoneWalls.update(this, true);
			}
		}
	}

	public void clearPvpTag() {
		pvpTagEnd = System.currentTimeMillis() - 1000;
	}

	public long getPvpTagRemaining() {
		return pvpTagEnd - System.currentTimeMillis();
	}

	public boolean isPvpTagged() {
		return pvpTagEnd - System.currentTimeMillis() > 0;
	}

	public final void resetFactionData() {
		Player player = null;
		if (getId() != null) {
			player = getPlayer();
		}
		//HCFactions.getInstance().getLogger().info(getName() + " " + factionId + " -> 0");
		// clean up any territory ownership in old faction, if there is one
		if (Factions.getInstance().exists(this.getFactionId())) {
			Faction currentFaction = this.getFaction();
			currentFaction.removeFPlayer(this);
			if (currentFaction.isNormal()) {
				currentFaction.clearClaimOwnership(this.getId());
			}
		}

		this.factionId = "0"; // The default neutral faction
		this.role = Role.NORMAL;
		this.autoClaimFor = null;

		if (player != null) {
			updateNearbyPlayersTeams();
		}
	}

	public void setPvpProtection(long time) {
		this.pvpProtectionTimeLeft = time;
	}

	public boolean hasPvpProtection() {
		if (pvpProtectionTimeLeft > 0) {
			return true;
		}
		return false;
	}

	public long getPvpProtectionTime() {
		return pvpProtectionTimeLeft;
	}

	public void removePvpProtection() {
		pvpProtectionTimeLeft = 0;
	}

	public void startPvpProtectionCountdown() {
		if (protectionCountdownTask != null) {
			protectionCountdownTask.cancel();
		}
		protectionCountdownTask = new ProtectionTimer(HCFactions.getInstance(), this).runTaskTimer(HCFactions.getInstance(), 20, 20);
	}

	// -------------------------------------------- //
	// Getters And Setters
	// -------------------------------------------- //
	public long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public boolean isMapAutoUpdating() {
		return mapAutoUpdating;
	}

	public void setMapAutoUpdating(boolean mapAutoUpdating) {
		this.mapAutoUpdating = mapAutoUpdating;
	}

	public boolean isPhysicalMapUpdating() {
		return physicalMapUpdating;
	}

	public void setPhysicalMapUpdating(boolean physicalMapUpdating) {
		this.physicalMapUpdating = physicalMapUpdating;
	}

	public boolean isPhysicalWallMapUpdating() {
		return physicalWallMapUpdating;
	}

	public void setPhysicalWallMapUpdating(boolean physicalWallMapUpdating) {
		this.physicalWallMapUpdating = physicalWallMapUpdating;
	}

	public FLocation getLastStoodAt() {
		return this.lastStoodAt;
	}

	/*
	 * public String getNameAndTag(Faction faction) { return this.getRelationColor(faction)+this.getNameAndTag(); } public String getNameAndTag(FactionPlayer fplayer) { return this.getRelationColor(fplayer)+this.getNameAndTag(); }
	 */
	// TODO: REmovded for refactoring.

	public void setLastStoodAt(FLocation flocation) {
		this.lastStoodAt = flocation;
	}

	public void markForDeletion(boolean delete) {
		deleteMe = delete;
	}

	// ----------------------------------------------//
	// Title, Name, Faction Tag and Chat
	// ----------------------------------------------//
	// Base:
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTag() {
		if (!this.hasFaction()) {
			return "";
		}
		return this.getFaction().getTag();
	}

	// Base concatenations:
	public String getNameAndSomething(String something) {
		String ret = this.role.getPrefix();
		if (something.length() > 0) {
			ret += something + " ";
		}
		ret += this.getName();
		return ret;
	}

	public String getNameAndTag() {
		return this.getNameAndSomething(this.getTag());
	}

	/*
	 * public String getNameAndRelevant(Faction faction) { // Which relation? Relation rel = this.getRelationTo(faction);
	 *
	 * // For member we show title if (rel == Relation.MEMBER) { return rel.getColor() + this.getNameAndTitle(); }
	 *
	 * // For non members we show tag return rel.getColor() + this.getNameAndTag(); } public String getNameAndRelevant(FactionPlayer fplayer) { return getNameAndRelevant(fplayer.getFaction()); }
	 */
	// Chat Tag:
	// These are injected into the format of global chat messages.
	public String getChatTag() {
		if (!this.hasFaction()) {
			return "";
		}

		if (Conf.chatTagIncludeRole) {
			return this.role.getPrefix() + this.getTag();
		}
		return this.getTag();
	}

	// Colored Chat Tag
	public String getChatTag(Faction faction) {
		if (!this.hasFaction()) {
			return "";
		}

		return this.getRelationTo(faction).getColor() + getChatTag();
	}

	public String getChatTag(FactionPlayer fplayer) {
		if (!this.hasFaction()) {
			return "";
		}

		return this.getColorTo(fplayer) + getChatTag();
	}

	// -------------------------------
	// Relation and relation colors
	// -------------------------------
	@Override
	public String describeTo(RelationParticipator that, boolean ucfirst) {
		return RelationUtil.describeThatToMe(this, that, ucfirst);
	}

	@Override
	public String describeTo(RelationParticipator that) {
		return RelationUtil.describeThatToMe(this, that);
	}

	@Override
	public Relation getRelationTo(RelationParticipator rp) {
		return RelationUtil.getRelationTo(this, rp);
	}

	@Override
	public Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful) {
		return RelationUtil.getRelationTo(this, rp, ignorePeaceful);
	}

	public Relation getRelationToLocation() {
		return Board.getFactionAt(new FLocation(this)).getRelationTo(this);
	}

	@Override
	public ChatColor getColorTo(RelationParticipator rp) {
		return RelationUtil.getColorOfThatToMe(this, rp);
	}

	// ----------------------------------------------//
	// Health
	// ----------------------------------------------//
	public void heal(int amnt) {
		Player player = this.getPlayer();
		if (player == null) {
			return;
		}
		player.setHealth(player.getHealth() + amnt);
	}

	// ----------------------------------------------//
	// Territory
	// ----------------------------------------------//
	public boolean isInOwnTerritory() {
		return Board.getFactionAt(new FLocation(this)) == this.getFaction();
	}

	public boolean isInOthersTerritory() {
		Faction factionHere = Board.getFactionAt(new FLocation(this));
		return factionHere != null && factionHere.isNormal() && factionHere != this.getFaction();
	}

	public boolean isInAllyTerritory() {
		return Board.getFactionAt(new FLocation(this)).getRelationTo(this).isAlly();
	}

	public boolean isInNeutralTerritory() {
		return Board.getFactionAt(new FLocation(this)).getRelationTo(this).isNeutral();
	}

	public void sendAreaChangeMessage(Faction from, Faction to) {
		String fromString;
		String toString;
		if (to.isNone()) {
			fromString = from.getTag(this);
			toString = to.getTag(this.getFaction());
		} else if (from.isNone()) {
			fromString = from.getTag(this.getFaction());
			toString = to.getTag(this.getFaction());
		} else {
			fromString = from.getTag(this.getFaction());
			toString = to.getTag(this.getFaction());
		}

		ComponentBuilder componentBuilder = new ComponentBuilder(CC.YELLOW + "Entering ");
		componentBuilder.append(toString);

		componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to view faction.").create()))
		                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f show " + ChatColor.stripColor(to.getTag())));

		componentBuilder.append(CC.YELLOW + ", leaving " + ChatColor.RESET, ComponentBuilder.FormatRetention.NONE);

		componentBuilder.append(fromString)
		                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to view faction.").create()))
		                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f show " + ChatColor.stripColor(from.getTag())));
		componentBuilder.append(CC.YELLOW + ".", ComponentBuilder.FormatRetention.NONE);

		getPlayer().spigot().sendMessage(componentBuilder.create());

		if (hasPvpProtection()) {
			if (to.noPvPInTerritory() && !from.noPvPInTerritory()) {
				this.getPlayer().sendFormattedMessage("{0}Your PvP protection has been paused.", ChatColor.YELLOW);
			} else if (from.noPvPInTerritory() && !to.noPvPInTerritory()) {
				this.getPlayer().sendFormattedMessage("{0}Your PvP protection has started.", ChatColor.YELLOW);
			}
		}
	}

	public void sendAreaMessage(Faction fac) {
		this.sendMessage(HCFactions.getInstance().txt.parse("<instance>") + "Entering " + fac.getTag(this));
	}

	// -------------------------------
	// Actions
	// -------------------------------
	public void leave() {
		Faction myFaction = this.getFaction();

		if (myFaction == null) {
			resetFactionData();
			return;
		}

		boolean perm = myFaction.isPermanent();

		if (!perm && this.getRole() == Role.ADMIN && myFaction.getFPlayers().size() > 1) {
			this.getPlayer().sendFormattedMessage("{0}You must assign the leader role to someone else in the faction.", CC.RED);
			return;
		}

		if (isPvpTagged()) {
			this.getPlayer().sendFormattedMessage("{0}You cannot leave the faction while pvp tagged.", CC.RED);
			return;
		}

		if (myFaction.isRaidable()) {
			this.getPlayer().sendFormattedMessage("{0}You cannot leave the faction while it''s raidable.", CC.RED);
			return;
		}

		FPlayerLeaveEvent leaveEvent = new FPlayerLeaveEvent(this, myFaction, FPlayerLeaveEvent.PlayerLeaveReason.LEAVE);
		Bukkit.getServer().getPluginManager().callEvent(leaveEvent);
		if (leaveEvent.isCancelled()) {
			return;
		}

		if (myFaction.isNormal()) {
			for (FactionPlayer fplayer : myFaction.getFPlayersWhereOnline(true)) {
				fplayer.getPlayer().sendFormattedMessage("{1}{2}{0} left {1}{3}{0}.", CC.PRIMARY, CC.SECONDARY, this.describeTo(fplayer, true), myFaction.describeTo(fplayer));
			}

			if (Conf.logFactionLeave) {
				HCFactions.getInstance().log(this.getName() + " left the faction: " + myFaction.getTag());
			}
		}

		this.resetFactionData();
		myFaction.addLeaveTime();

		if (myFaction.isNormal() && !perm && myFaction.getFPlayers().isEmpty()) {
			// Remove this faction
			for (FactionPlayer fplayer : FPlayers.getInstance().getOnline()) {
				fplayer.msg("<instance>%s<instance> was disbanded.", myFaction.describeTo(fplayer, true));
			}

			FactionPostDisbandEvent postDisbandEvent = new FactionPostDisbandEvent(this.getPlayer(), myFaction);
			Bukkit.getServer().getPluginManager().callEvent(postDisbandEvent);


			myFaction.detach();


			if (Conf.logFactionDisband) {
				HCFactions.getInstance().log("The faction " + myFaction.getTag() + " (" + myFaction.getId() + ") was disbanded due to the last player (" + this.getName() + ") leaving.");
			}
		}
	}

	public boolean canClaimForFaction(Faction forFaction) {
		if (forFaction.isNone()) {
			return false;
		}

		if (this.isAdminBypassing() || (forFaction == this.getFaction() && this.getRole().isAtLeast(Role.MODERATOR)) || (forFaction.isSafeZone() && Permission.MANAGE_SAFE_ZONE.has(getPlayer())) || (forFaction.isWarZone() && Permission.MANAGE_WAR_ZONE.has(getPlayer()))) {
			return true;
		}

		return false;
	}

	public boolean canClaimForFactionAtLocation(Faction forFaction, Location location, boolean notifyFailure) {
		String error = null;
		FLocation flocation = new FLocation(location);
		Faction myFaction = getFaction();
		Faction currentFaction = Board.getFactionAt(flocation);
		int ownedLand = forFaction.getLandRounded();

		if (this.isAdminBypassing()) {
			return true;
		} else if (Conf.worldsNoClaiming.contains(flocation.getWorldName())) {
			error = HCFactions.getInstance().txt.parse("<b>Sorry, this world has land claiming disabled.");
		} else if (forFaction.isSafeZone() && Permission.MANAGE_SAFE_ZONE.has(getPlayer())) {
			return true;
		} else if (forFaction.isWarZone() && Permission.MANAGE_WAR_ZONE.has(getPlayer())) {
			return true;
		} else if (myFaction != forFaction) {
			error = HCFactions.getInstance().txt.parse("<b>You can't claim land for <h>%s<b>.", forFaction.describeTo(this));
		} else if (forFaction == currentFaction) {
			error = HCFactions.getInstance().txt.parse("%s<instance> already own this land.", forFaction.describeTo(this, true));
		} else if (this.getRole().value < Role.MODERATOR.value) {
			error = HCFactions.getInstance().txt.parse("<b>You must be <h>%s<b> to claim land.", Role.MODERATOR.toString());
		} else if (forFaction.getFPlayers().size() < Conf.claimsRequireMinFactionMembers) {
			error = HCFactions.getInstance().txt.parse("Factions must have at least <h>%s<b> members to claim land.", Conf.claimsRequireMinFactionMembers);
		} else if (currentFaction.isSafeZone()) {
			error = HCFactions.getInstance().txt.parse("<b>You can not claim a Safe Zone.");
		} else if (currentFaction.isWarZone()) {
			error = HCFactions.getInstance().txt.parse("<b>You can not claim a War Zone.");
		} else if (currentFaction.isSystem()) {
			error = HCFactions.getInstance().txt.parse("<b>You can not claim over a system faction.");
		} else if (ownedLand >= forFaction.getMaxLandCount() && forFaction.isNormal()) {
			error = HCFactions.getInstance().txt.parse("<b>Limit reached. You can't claim more land!");
		} else if (currentFaction.getRelationTo(forFaction) == Relation.ALLY) {
			error = HCFactions.getInstance().txt.parse("<b>You can't claim the land of your allies.");
		} else if (Conf.claimsMustBeConnected && !this.isAdminBypassing() && myFaction.getLandRoundedInWorld(flocation.getWorldName()) > 0 && !Board.isConnectedLocation(flocation, myFaction) && (!Conf.claimsCanBeUnconnectedIfOwnedByOtherFaction || !currentFaction.isNormal())) {
			if (Conf.claimsCanBeUnconnectedIfOwnedByOtherFaction) {
				error = HCFactions.getInstance().txt.parse("<b>You can only claim additional land which is connected to your first claim or controlled by another faction!");
			} else {
				error = HCFactions.getInstance().txt.parse("<b>You can only claim additional land which is connected to your first claim!");
			}
		} else if (currentFaction.isNormal()) {
			if (!Conf.allowOverclaiming) {
				error = HCFactions.getInstance().txt.parse("<b>Claiming over other factions' land is not allowed however their land is unprotected if their power is zero or less");
			} else if (myFaction.isPeaceful()) {
				error = HCFactions.getInstance().txt.parse("%s<instance> owns this land. Your faction is peaceful, so you cannot claim land from other factions.", currentFaction.getTag(this));
			} else if (currentFaction.isPeaceful()) {
				error = HCFactions.getInstance().txt.parse("%s<instance> owns this land, and is a peaceful faction. You cannot claim land from them.", currentFaction.getTag(this));
			} else if (!currentFaction.isRaidable()) {
				// TODO more messages WARN current faction most importantly
				error = HCFactions.getInstance().txt.parse("%s<instance> owns this land and is strong enough to keep it.", currentFaction.getTag(this));
			} else if (!Board.isBorderLocation(flocation)) {
				error = HCFactions.getInstance().txt.parse("<b>You must start claiming land at the border of the territory.");
			}
		}

		if (error == null && Conf.landBorder > 0) {
			int x = location.getBlockX() >> 4;
			int z = location.getBlockZ() >> 4;
			for (int dx = -Conf.landBorder; dx <= Conf.landBorder; dx++) {
				for (int dz = -Conf.landBorder; dz <= Conf.landBorder; dz++) {
					Faction f = Board.getFactionAt(new FLocation(location.getWorld().getName(), x + dx, z + dz));
					if (!f.isNone() && f != forFaction) {
						error = HCFactions.getInstance().txt.parse("<b>You must claim at least %d chunks away from other factions.", Conf.landBorder);
					}
				}
			}
		}

		if (notifyFailure && error != null) {
			msg(error);
		}
		return error == null;
	}

	public boolean attemptClaim(Faction forFaction, Location location, boolean notifyFailure) {
		// notifyFailure is false if called by auto-claim; no need to notify on every failure for it
		// return value is false on failure, true on success

		FLocation flocation = new FLocation(location);
		Faction currentFaction = Board.getFactionAt(flocation);

		int ownedLand = forFaction.getLandRounded();

		int costToClaim = Conf.firstClaimCost + (Conf.increasePerClaimCost * ownedLand);

		if (!this.canClaimForFactionAtLocation(forFaction, location, notifyFailure)) {
			return false;
		}

		LandClaimEvent claimEvent = new LandClaimEvent(flocation, forFaction, this);
		Bukkit.getServer().getPluginManager().callEvent(claimEvent);
		if (claimEvent.isCancelled()) {
			return false;
		}

		// Take balance
		if (!this.isAdminBypassing()) {
			if (!HCFactions.getInstance().getEconomyManager().handleBuyLand(this, forFaction, flocation.getCoordString(), costToClaim)) {
				return false;
			}
		}

		// announce success
		Set<FactionPlayer> informTheseFactionPlayers = new HashSet<FactionPlayer>();
		informTheseFactionPlayers.add(this);
		informTheseFactionPlayers.addAll(forFaction.getFPlayersWhereOnline(true));
		for (FactionPlayer fp : informTheseFactionPlayers) {
			fp.getPlayer().sendFormattedMessage("{1}{2}{0} claimed land for {1}{3}{0} from {1}{4}{0}.", CC.PRIMARY, CC.SECONDARY, this.describeTo(fp, true), forFaction.describeTo(fp), currentFaction.describeTo(fp), costToClaim);
		}
		if (this.hasPvpProtection()) {
			removePvpProtection();
			this.getPlayer().sendFormattedMessage("{0}You''ve lost your PvP protection from claiming land.", CC.RED);
		}
		Board.setFactionAt(forFaction, flocation, costToClaim);

		if (Conf.logLandClaims) {
			HCFactions.getInstance().log(this.getName() + " claimed land at (" + flocation.getCoordString() + ") for the faction: " + forFaction.getTag());
		}

		return true;
	}

	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	@Override
	public boolean shouldBeSaved() {
		return !this.deleteMe;
	}

	public void msg(String str, Object... args) {
		this.sendMessage(HCFactions.getInstance().txt.parse(str, args));
	}

	public boolean updateFocus(Player other) {
		if (this.isOffline()) {
			return false;
		}

		Scoreboard scoreboard = this.getPlayer().getScoreboard();

		String teamName = "§§focus";
		Team focusTeam = scoreboard.getTeam(teamName);
		if (focusTeam == null) {
			focusTeam = scoreboard.registerNewTeam(teamName);
			focusTeam.setPrefix(Conf.colorFocus.toString());
		}

		if (other == null) {
			for (String entry : focusTeam.getEntries()) {
				focusTeam.removeEntry(entry);
				Player targetPlayer = HCFactions.getInstance().getServer().getPlayerExact(entry);
				if (targetPlayer != null) {
					this.updatePlayerTeam(targetPlayer);
				}
			}
			return true;
		} else if (this.getFaction() != null && this.getFaction().getFocusedTarget() != null && this.getFaction().getFocusedTarget().equals(other.getUniqueId())) {
			focusTeam.addPlayer(other);
			return true;
		}

		return false;
	}

	public void updatePlayerTeam(Player other) {
		if (!isOnline()) {
			return;
		}
		Scoreboard scoreboard = getPlayer().getScoreboard();
		FactionPlayer fOther = FPlayers.getInstance().get(other);

		if (this.updateFocus(other)) {
			return;
		}


		Relation rel = getRelationTo(fOther);
		String teamName = "§§" + rel.nicename;
		Team currentTeam = scoreboard.getPlayerTeam(other);
		// player already set in correct team?
		if (currentTeam != null && currentTeam.getName().equals(teamName)) {
			return;
		}
		Team team = scoreboard.getTeam(teamName);
		// create team if needed
		if (team == null) {
			team = scoreboard.registerNewTeam(teamName);
			team.setPrefix(rel.getColor().toString());
			if (rel.equals(Relation.MEMBER)) {
				team.setCanSeeFriendlyInvisibles(true);
			} else {
				team.setCanSeeFriendlyInvisibles(false);
				//team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
			}
		}
		// remove from old team if needed
		if (currentTeam != null) {
			currentTeam.removePlayer(other);
		}
		team.addPlayer(other);
	}

	public void updateNearbyPlayersTeams() {
		Player player = getPlayer();
		Location loc = player.getLocation();
		Location otherLoc = new Location(null, 0, 0, 0); // re-used location object for every other player
		for (Player other : Bukkit.getOnlinePlayers()) {
			if (other.getWorld().equals(player.getWorld())) {
				// is within 100 units?
				if (other.getLocation(otherLoc).distanceSquared(loc) < 100 * 100) {
					updatePlayerTeam(other);
					FPlayers.getInstance().get(other).updatePlayerTeam(player);
				}
			}
		}
	}

	private Set<BlockVector> getPhysicalMapWalls() {
		if (physicalMapWalls == null) {
			physicalMapWalls = new HashSet<>();
		}
		return physicalMapWalls;
	}

	private Set<BlockVector> getPhysicalWallMapWalls() {
		if (physicalWallMapWalls == null) {
			physicalWallMapWalls = new HashSet<>();
		}
		return physicalWallMapWalls;
	}

	public void updateWalls() {
		Player player = getPlayer();

		if (isPhysicalMapUpdating()) {
			//Color carpet boders around faction land
			Set<BlockVector> physicalMapWalls = getPhysicalMapWalls();
			int dist = 16;
			List<Material> materials = new ArrayList<>();
			List<Byte> datas = new ArrayList<>();
			List<String> names = new ArrayList<>();

			materials.add(Material.YELLOW_FLOWER);
			datas.add((byte) 0);
			names.add(ChatColor.YELLOW + "Dandelion");
			for (int i = 0; i <= 8; i++) {
				materials.add(Material.RED_ROSE);
				datas.add((byte) i);
			}
			names.addAll(Arrays.asList(ChatColor.RED + "Poppy", ChatColor.BLUE + "Blue Orchid", ChatColor.DARK_PURPLE + "Allium", ChatColor.WHITE + "Azure Bluet",
					ChatColor.RED + "Red Tulip", ChatColor.GOLD + "Orange Tulip", ChatColor.WHITE + "White Tulip", ChatColor.LIGHT_PURPLE + "Pink Tulip", ChatColor.WHITE + "Oxeye Daisy"));
			for (int i = 0; i <= 5; i++) {
				materials.add(Material.SAPLING);
				datas.add((byte) i);
			}
			names.addAll(Arrays.asList(ChatColor.GREEN + "Oak Sapling", ChatColor.DARK_GREEN + "Spruce Sapling", ChatColor.GREEN + "Birch Sapling", ChatColor.DARK_GREEN + "Jungle Sapling",
					ChatColor.GRAY + "Acacia Sapling", ChatColor.DARK_GRAY + "Dark Oak Sapling"));
			materials.add(Material.DEAD_BUSH);
			datas.add((byte) 0);
			names.add(ChatColor.GOLD + "Dead Bush");
			materials.add(Material.BROWN_MUSHROOM);
			datas.add((byte) 0);
			names.add(ChatColor.GOLD + "Brown Mushroom");

			materials.add(Material.RED_MUSHROOM);
			datas.add((byte) 0);
			names.add(ChatColor.RED + "Red Mushroom");

			materials.add(Material.TORCH);
			datas.add((byte) 0);
			names.add(ChatColor.YELLOW + "Torch");

			materials.add(Material.REDSTONE_TORCH_ON);
			datas.add((byte) 0);
			names.add(ChatColor.RED + "Redstone Torch");

			Block origin = player.getLocation().getBlock();
			String worldName = player.getWorld().getName();
			for (int x = origin.getX() - dist; x <= origin.getX() + dist; x++) {
				for (int z = origin.getZ() - dist; z <= origin.getZ() + dist; z++) {
					int chunkX = x >> 4;
					int chunkZ = z >> 4;
					if (Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ)).denyPvpProtEntry()) {
						int data = 0;
						if (!factionMapList.contains(Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ)).getUUID())) {
							factionMapList.add(Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ)).getUUID());
							data = factionMapList.indexOf(Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ)).getUUID());
							if (data >= materials.size()) {
								while (data >= materials.size()) {
									data -= materials.size();
								}
							}
							msg("<gold>Faction land nearby <red>" + Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ)).getTag() + " <white>Border Item = " + names.get(data));
						} else {
							data = factionMapList.indexOf(Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ)).getUUID());
							if (data >= materials.size()) {
								while (data >= materials.size()) {
									data -= materials.size();
								}
							}
						}
						if (((x & 15) == 0 && Board.getFactionAt(new FLocation(worldName, chunkX - 1, chunkZ)) != Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ)))
						    || ((x & 15) == 15 && Board.getFactionAt(new FLocation(worldName, chunkX + 1, chunkZ)) != Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ)))
						    || ((z & 15) == 0 && Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ - 1)) != Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ)))
						    || ((z & 15) == 15 && Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ + 1)) != Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ)))) {
							int y = origin.getWorld().getHighestBlockYAt(new Location(origin.getWorld(), x, 0, z));

							BlockVector bv = new BlockVector(x, y, z);
							if (!player.getWorld().getBlockAt(x, y, z).getType().isSolid()) {
								physicalMapWalls.add(bv);
								player.sendBlockChange(bv.toLocation(player.getWorld()), materials.get(data), datas.get(data));
							}

						}
					}
				}
			}

			// dirty: remove wall blocks further than 10 chunks away
			Iterator<BlockVector> iter = physicalMapWalls.iterator();
			while (iter.hasNext()) {
				BlockVector bv = iter.next();
				if (Math.abs(player.getLocation().getX() - bv.getX()) > 160
				    || Math.abs(player.getLocation().getZ() - bv.getZ()) > 160) {
					iter.remove();
				}
			}
		} else {
			resetPhysicalMapWalls();
			factionMapList.clear();
		}
		if (isPhysicalWallMapUpdating()) {
			//show wall of flowers
			Set<BlockVector> physicalWallMapWalls = getPhysicalWallMapWalls();
			int dist = 6;
			Block origin = player.getLocation().getBlock();
			String worldName = player.getWorld().getName();
			for (int x = origin.getX() - dist; x <= origin.getX() + dist; x++) {
				for (int z = origin.getZ() - dist; z <= origin.getZ() + dist; z++) {
					int chunkX = x >> 4;
					int chunkZ = z >> 4;
					if (getFaction().equals(Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ)))) {
						if (((x & 15) == 0 && Board.getFactionAt(new FLocation(worldName, chunkX - 1, chunkZ)) != Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ)))
						    || ((x & 15) == 15 && Board.getFactionAt(new FLocation(worldName, chunkX + 1, chunkZ)) != Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ)))
						    || ((z & 15) == 0 && Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ - 1)) != Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ)))
						    || ((z & 15) == 15 && Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ + 1)) != Board.getFactionAt(new FLocation(worldName, chunkX, chunkZ)))) {
							for (int y = origin.getY() - dist; y <= origin.getY() + dist; y++) {
								BlockVector bv = new BlockVector(x, y, z);
								if (!player.getWorld().getBlockAt(x, y, z).getType().isSolid()) {
									physicalWallMapWalls.add(bv);
									if (!(player.getWorld().getBlockAt(x, y, z).getType().equals(Material.STATIONARY_WATER) || player.getWorld().getBlockAt(x, y, z).getType().equals(Material.WATER))) {
										player.sendBlockChange(bv.toLocation(player.getWorld()), Material.REDSTONE_TORCH_ON, (byte) 0);
									}
								}
							}
						}
					}
				}
			}

			// dirty: remove wall blocks further than 10 chunks away
			Iterator<BlockVector> iter = physicalWallMapWalls.iterator();
			while (iter.hasNext()) {
				BlockVector bv = iter.next();
				if (Math.abs(player.getLocation().getX() - bv.getX()) > 160
				    || Math.abs(player.getLocation().getZ() - bv.getZ()) > 160) {
					iter.remove();
				}
			}
		} else {
			resetPhysicalWallMapWalls();
		}
	}

	public void resetPhysicalMapWalls() {
		Player player = getPlayer();

		if (physicalMapWalls == null || isPhysicalMapUpdating()) {
			return;
		}

		for (BlockVector vec : physicalMapWalls) {
			Location loc = vec.toLocation(player.getWorld());
			Block block = loc.getBlock();
			player.sendBlockChange(loc, block.getType(), block.getData());
		}
		physicalMapWalls = null;
	}

	public void resetPhysicalWallMapWalls() {
		Player player = getPlayer();

		if (physicalWallMapWalls == null || isPhysicalWallMapUpdating()) {
			return;
		}

		for (BlockVector vec : physicalWallMapWalls) {
			Location loc = vec.toLocation(player.getWorld());
			Block block = loc.getBlock();
			player.sendBlockChange(loc, block.getType(), block.getData());
		}
		physicalWallMapWalls = null;
	}

	@Override
	public int compareTo(FactionPlayer other) {
		return other.role.value - this.role.value;
	}

	// sends a message about pvp protection walls, with a 10 seconds cooldown
	public void sendPvpProtWallsMessage() {
		long now = System.currentTimeMillis();
		if (now - lastPvpProtWallMessage > 10000) {
			msg("<b>That glass wall prevents you from entering faction land while you have PvP protection. Use <a>/pvp enable<b> to enable PvP.");
			lastPvpProtWallMessage = now;
		}
	}

	public void setNameTagMode(NameTagMode nameTagMode) {
		this.nameTagMode = nameTagMode;
		if (isOnline()) {
			updateNearbyPlayersTeams();
		}
	}

	public void sendInfoMessage() {
		msg(HCFactions.getInstance().txt.titleize(this.getFaction().getTag(this)));
		if (!this.getFaction().isNormal()) {
			msg("<a>This faction cannot have players in it!");
			if (this.getFaction().hasHome()) {
				Location home = this.getFaction().getHome();
				msg("<a>Location: <instance>%d, %d, %d", home.getBlockX(), home.getBlockY(), home.getBlockZ());
			}
			return;
		}

		if (this.getFaction().getMotd() != null) {
			if (this.getFaction().getMotd().length() > 0) {
				msg("<a>MOTD: " + ChatColor.GRAY + this.getFaction().getMotd());
			}
		}
		double dtr = this.getFaction().getDtr();
		if (dtr > 0) {
			msg("<a>DTR: <instance>%.2f / %.2f (%d %s)", dtr, this.getFaction().getMaxDtr(), (int) Math.ceil(dtr), dtr > 1 ? "deaths" : "death");
		} else {
			msg("<a>DTR: <instance>%.2f / %.2f <b>(RAIDABLE)", dtr, this.getFaction().getMaxDtr());
		}

		long dtrFreeze = this.getFaction().getDtrRegenCooldown() - System.currentTimeMillis();
		if (dtrFreeze > 0) {
			msg("<a>DTR freeze: <instance>%s", DurationFormatUtils.formatDurationWords(dtrFreeze, true, true));
		}

		msg("<a>Land: <instance>%d / %d", this.getFaction().getLandRounded(), this.getFaction().getMaxLandCount());

		if (this.getFaction().hasHome()) {
			Location home = this.getFaction().getHome();
			msg("<a>Faction home: <instance>%d, %d, %d", home.getBlockX(), home.getBlockY(), home.getBlockZ());
		} else {
			msg("<a>Faction home: <instance>Not set");
		}

		if (this.getFaction().isPermanent()) {
			msg("<a>This faction is permanent, remaining even with no members.");
		}

		String listpart;

		// List relation
		Set<Faction> allies = this.getFaction().getAlliedFactions();
		if (!allies.isEmpty()) {
			ComponentBuilder msgBuilder = new ComponentBuilder("Allies (" + allies.size() + "/" + Conf.allyLimit + "): ").color(GOLD);
			boolean first = true;
			for (Faction ally : allies) {
				if (!first) {
					msgBuilder.append(ChatColor.YELLOW + ", ");
				}
				first = false;
				listpart = ally.getTag(this) + " (" + ally.getOnlinePlayerCount(true) + ")";
				msgBuilder.append("Click to f show " + ally.getTag());
				msgBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to f show " + ally.getTag()).create()));
				msgBuilder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f show " + ally.getTag()));
			}
			this.getPlayer().spigot().sendMessage(msgBuilder.create());
		}

		List<FactionPlayer> online = new ArrayList<>(this.getFaction().getFPlayersWhereOnline(true));
		List<FactionPlayer> offline = new ArrayList<>(this.getFaction().getFPlayersWhereOnline(false));

		if (Conf.factionMemberLimit > 0) {
			msg("<a>Members: <instance>%d / %d", this.getFaction().getFPlayers().size(), Conf.factionMemberLimit - this.getFaction().getLockedSlots());
		} else {
			msg("<a>Members: <instance>%d", this.getFaction().getFPlayers().size());
		}

		if (!online.isEmpty()) {
			Collections.sort(online);
			this.getPlayer().sendMessage(ChatColor.GOLD + "Members online(" + online.size() + "): " + memberList(online));
		}
		if (!offline.isEmpty()) {
			Collections.sort(offline);
			this.getPlayer().sendMessage(ChatColor.GOLD + "Members offline(" + offline.size() + "): " + memberList(offline));
		}
	}

	private String memberList(Collection<FactionPlayer> fplayers) {
		StringJoiner online = new StringJoiner(ChatColor.YELLOW + ", ");
		StringJoiner offline = new StringJoiner(ChatColor.YELLOW + ", ");
		for (FactionPlayer factionPlayer : fplayers) {
			if (HCFactions.getInstance().killsProvider != null) {
				online.add(
						factionPlayer.getColorTo(this) + factionPlayer.getNameAndSomething("") + ChatColor.GRAY + "[" + factionPlayer
								.getColorTo(this) + HCFactions.getInstance().killsProvider.getKills(
								factionPlayer.getUuid()) + ChatColor.GRAY + "]");
			} else {
				online.add(factionPlayer.getColorTo(this) + factionPlayer.getNameAndSomething(""));
			}
		}
		return online.merge(offline).toString();
	}

	public boolean isStaff() {
		return this.isOnline() && Permission.STAFF.has(this.getPlayer());
	}

	public long getJoinedFactionTime() {
		return this.joinedFactionTime;
	}

	public boolean denyEventZoneEntry() {
		if (this.factionId.equals("0")) {
			return true;
		}
		if (Conf.joinFactionEventCooldown <= 0) {
			return false;
		}
		if (this.isStaff()) {
			return false;
		}
		long timeInFaction = System.currentTimeMillis() - this.joinedFactionTime;
		return timeInFaction < Conf.joinFactionEventCooldown * 60 * 1000; // config in minutes
	}

	public boolean denySafeZoneEntry() {
		return this.isPvpTagged() && !this.isStaff();
	}

	public boolean denyPvpProtEntry() {
		return hasPvpProtection() && !this.isStaff();
	}
}
