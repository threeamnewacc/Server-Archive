package com.massivecraft.factions;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.type.SubclaimOwner;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.factions.zcore.persist.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Faction extends Entity implements RelationParticipator {

	private transient final static long ONE_WEEK_MILLIS = 6048000000L;
	// FIELD: relationWish
	private Map<String, Relation> relationWish;
	// FIELD: claimOwnership
	private Map<FLocation, Set<String>> claimOwnership = new ConcurrentHashMap<FLocation, Set<String>>();
	private Map<FLocation, Integer> amountPaidForLand = new ConcurrentHashMap<>();
	// FIELD: fplayers
	// speedy lookup of players in faction
	private transient Set<FactionPlayer> fplayers = new HashSet<FactionPlayer>();
	// FIELD: invites
	// Where string is a lowercase player name
	private Set<String> invites;
	// Land count cached, looping all claimed chunks is slothlike
	private transient int landCount;
	// FIELD: peaceful
	// "peaceful" status can only be set by server admins/moderators/ops, and prevents PvP and land capture to/from the faction
	private boolean peaceful;
	// FIELD: peacefulExplosionsEnabled
	private boolean peacefulExplosionsEnabled;
	// FIELD: permanent
	// "permanent" status can only be set by server admins/moderators/ops, and allows the faction to remain even with 0 members
	private boolean permanent;
	// FIELD: tag
	private String tag;
	private String motd;
	// FIELD: home
	private LazyLocation home;
	// FIELD: lastPlayerLoggedOffTime
	private transient long lastPlayerLoggedOffTime;
	private double dtr = 0.0;
	private long dtrRegenCooldown = 0;
	private boolean system = false;
	private boolean event = false;
	private transient long renameCooldown = 0;
	private long lastAllyTime = System.currentTimeMillis() - ONE_WEEK_MILLIS;
	private UUID uuid = UUID.randomUUID();
	private List<Long> playerLeaveTimes = new ArrayList<>();

	private Map<Location, SubclaimOwner> subclaims = new HashMap<>();

	@Getter
	@Setter
	private transient UUID focusedTarget;

	// -------------------------------------------- //
	// Construct
	// -------------------------------------------- //
	public Faction() {
		this.relationWish = new HashMap<>();
		this.invites = new HashSet<>();
		this.tag = "???";
		this.lastPlayerLoggedOffTime = 0;
		this.peaceful = false;
		this.peacefulExplosionsEnabled = false;
		this.permanent = false;
	}

	public void invite(FactionPlayer fplayer) {
		this.invites.add(fplayer.getName().toLowerCase());
	}

	public void deinvite(FactionPlayer fplayer) {
		this.invites.remove(fplayer.getName().toLowerCase());
	}

	public boolean isInvited(FactionPlayer fplayer) {
		return this.invites.contains(fplayer.getName().toLowerCase());
	}

	public boolean isPeaceful() {
		return this.peaceful;
	}

	public boolean noExplosionsInTerritory() {
		return this.peaceful && !peacefulExplosionsEnabled;
	}

	public boolean isPermanent() {
		return permanent || !this.isNormal();
	}

	public void setPermanent(boolean isPermanent) {
		permanent = isPermanent;
	}

	public String getTag() {
		return this.tag;
	}

	public void setTag(String str) {
		if (Conf.factionTagForceUpperCase) {
			str = str.toUpperCase();
		}
		this.tag = str;
	}

	public String getTag(String prefix) {
		return prefix + this.tag;
	}

	public String getTag(Faction otherFaction) {
		if (otherFaction == null) {
			return getTag();
		}
		return this.getTag(this.getColorTo(otherFaction).toString());
	}

	public String getTag(FactionPlayer otherFplayer) {
		if (otherFplayer == null) {
			return getTag();
		}
		return this.getTag(this.getColorTo(otherFplayer).toString());
	}

	public String getMotd() {
		return motd;
	}

	public boolean setMotd(String string) {
		if (string.length() > 100) {
			return false;
		}
		motd = string;
		return true;
	}

	public String getComparisonTag() {
		return MiscUtil.getComparisonString(this.tag);
	}

	public boolean hasHome() {
		return this.getHome() != null;
	}

	public Location getHome() {
		this.confirmValidHome();
		return (this.home != null) ? this.home.getLocation() : null;
	}

	public void setHome(Location home) {
		this.home = new LazyLocation(home);
	}

	public void confirmValidHome() {
		if (!Conf.homesMustBeInClaimedTerritory || this.home == null || (this.home.getLocation() != null && Board.getFactionAt(new FLocation(this.home.getLocation())) == this)) {
			return;
		}

		msg("<b>Your faction home has been un-set since it is no longer in your territory.");
		this.home = null;
	}

	public double getDtr() {
		checkDtrBounds();
		return dtr;
	}

	public void setDtr(double dtr) {
		this.dtr = dtr;
		checkDtrBounds();
	}

	public void alterDtr(double amount) {
		dtr += amount;
		checkDtrBounds();
	}

	private void checkDtrBounds() {
		double max = getMaxDtr();
		if (dtr > max) {
			dtr = max;
			return;
		}
		double min = getMinDtr();
		if (dtr < min) {
			dtr = min;
		}
	}

	public double getMaxDtr() {
		if (fplayers.size() == 1) {
			return Math.min(2 * Conf.dtrPerPlayer, Conf.dtrMax); //Solo factions getAll 2 dtr
		} else {
			return Math.min(fplayers.size() * Conf.dtrPerPlayer, Conf.dtrMax);
		}
	}

	public double getMinDtr() {
		return -getMaxDtr();
	}

	public long getDtrRegenCooldown() {
		return dtrRegenCooldown;
	}

	public void setDtrRegenCooldown(long dtrRegenCooldown) {
		this.dtrRegenCooldown = dtrRegenCooldown;
	}

	// -------------------------------------------- //
	// Extra Getters And Setters
	// -------------------------------------------- //
	public boolean noPvPInTerritory() {
		return isSafeZone();
	}

	public boolean noMonstersInTerritory() {
		return isSafeZone();
	}

	// -------------------------------
	// Understand the types
	// -------------------------------
	public boolean isNormal() {
		return !(this.isNone() || this.isSafeZone() || this.isWarZone() || this.isSystem());
	}

	public boolean isNone() {
		return this.getId().equals("0");
	}

	public boolean isSafeZone() {
		return this.getId().equals("-1");
	}

	public boolean isWarZone() {
		return this.getId().equals("-2");
	}

	public boolean isPlayerFreeType() {
		return this.isSystem() || this.isSafeZone() || this.isWarZone();
	}

	public boolean isSystem() {
		return system;
	}

	public void setSystem(boolean system) {
		this.system = system;
	}

	public boolean isEvent() {
		return event;
	}

	public void setEvent(boolean event) {
		this.event = event;
	}

	public boolean hasRenameCooldown() {
		return this.renameCooldown != 0 && System.currentTimeMillis() < this.renameCooldown;
	}

	public void addRenameCooldown(int seconds) {
		this.renameCooldown = System.currentTimeMillis() + seconds * 1000;
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

	@Override
	public ChatColor getColorTo(RelationParticipator rp) {
		return RelationUtil.getColorOfThatToMe(this, rp);
	}

	public Relation getRelationWish(Faction otherFaction) {
		if (this.relationWish.containsKey(otherFaction.getId())) {
			return this.relationWish.get(otherFaction.getId());
		}
		return Relation.NEUTRAL;
	}

	public void setRelationWish(Faction otherFaction, Relation relation) {
		Relation oldRelation = getRelationTo(otherFaction);
		if (this.relationWish.containsKey(otherFaction.getId()) && relation.equals(Relation.NEUTRAL)) {
			this.relationWish.remove(otherFaction.getId());
		} else {
			this.relationWish.put(otherFaction.getId(), relation);
		}
		Relation newRelation = getRelationTo(otherFaction);
		if (newRelation != oldRelation) {
			for (FactionPlayer fplayer : getFPlayersWhereOnline(true)) {
				fplayer.updateNearbyPlayersTeams();
			}
		}
	}

	public Set<Faction> getAlliedFactions() {
		Set<Faction> allies = new HashSet<>();
		Iterator<Entry<String, Relation>> iter = this.relationWish.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Relation> entry = iter.next();
			if (entry.getValue() == Relation.ALLY) {
				Faction faction = Factions.getInstance().getById(entry.getKey());
				if (faction == null || !faction.isNormal()) {
					iter.remove();
					continue;
				}
				if (faction.getRelationWish(this) == Relation.ALLY) {
					allies.add(faction);
				}
			}
		}
		return allies;
	}

	public int getLandRounded() {
		return this.landCount;
	}

	public void setLandCount(int count) {
		this.landCount = count;
	}

	public void alterLandCount(int diff) {
		this.landCount += diff;
	}

	public int getLandRoundedInWorld(String worldName) {
		return Board.getFactionCoordCountInWorld(this, worldName);
	}

	public int getMaxLandCount() {
		return Math.min(Conf.baseClaimsAmount + fplayers.size() * Conf.claimsPerPlayer, Conf.claimsLimit);
	}

	public boolean isRaidable() {
		return isNormal() && dtr <= 0;
	}

	// -------------------------------
	// FPlayers
	// -------------------------------
	// maintain the reference list of FPlayers in this faction
	public void refreshFPlayers() {
		fplayers.clear();
		if (this.isPlayerFreeType()) {
			return;
		}

		for (FactionPlayer fplayer : FPlayers.getInstance().getAll()) {
			if (fplayer.getFaction() == this) {
				fplayers.add(fplayer);
			}
		}
	}

	protected boolean addFPlayer(FactionPlayer fplayer) {
		if (this.isPlayerFreeType()) {
			return false;
		}

		return fplayers.add(fplayer);
	}

	protected boolean removeFPlayer(FactionPlayer fplayer) {
		if (this.isPlayerFreeType()) {
			return false;
		}
		//Remove from subclaims
		for (Entry<Location, SubclaimOwner> entry : subclaims.entrySet()) {
			if (entry.getValue().getOwner().equals(fplayer.getUuid())) {
				entry.getValue().setOwner(getFPlayersWhereRole(Role.ADMIN).get(0).getUuid());
			} else if (entry.getValue().getAddedMembers().contains(fplayer.getUuid())) {
				entry.getValue().getAddedMembers().remove(fplayer.getUuid());
			}
		}
		return fplayers.remove(fplayer);
	}

	public Set<FactionPlayer> getFPlayers() {
		// return a shallow copy of the FactionPlayer list, to prevent tampering and concurrency issues
		Set<FactionPlayer> ret = new HashSet<FactionPlayer>(fplayers);
		return ret;
	}

	public Set<FactionPlayer> getFPlayersWhereOnline(boolean online) {
		Set<FactionPlayer> ret = new HashSet<FactionPlayer>();

		for (FactionPlayer fplayer : fplayers) {
			if (fplayer.isOnline() == online) {
				ret.add(fplayer);
			}
		}

		return ret;
	}

	public int getOnlinePlayerCount(boolean online) {
		int count = 0;
		for (FactionPlayer fplayer : fplayers) {
			if (fplayer.isOnline() == online) {
				count++;
			}
		}
		return count;
	}

	public FactionPlayer getFPlayerAdmin() {
		if (!this.isNormal()) {
			return null;
		}

		for (FactionPlayer fplayer : fplayers) {
			if (fplayer.getRole() == Role.ADMIN) {
				return fplayer;
			}
		}
		return null;
	}

	public ArrayList<FactionPlayer> getFPlayersWhereRole(Role role) {
		ArrayList<FactionPlayer> ret = new ArrayList<FactionPlayer>();
		if (!this.isNormal()) {
			return ret;
		}

		for (FactionPlayer fplayer : fplayers) {
			if (fplayer.getRole() == role) {
				ret.add(fplayer);
			}
		}

		return ret;
	}

	public ArrayList<Player> getOnlinePlayers() {
		ArrayList<Player> ret = new ArrayList<Player>();
		if (this.isPlayerFreeType()) {
			return ret;
		}

		for (FactionPlayer fplayer : fplayers) {
			if (fplayer.isOnline()) {
				if (fplayer.getPlayer() != null) {
					ret.add(fplayer.getPlayer());
				}
			}
		}

		return ret;
	}

	// slightly faster check than getOnlinePlayers() if you just want to see if there are any players online
	public boolean hasPlayersOnline() {
		// only real factions can have players online, not safe zone / war zone
		if (this.isPlayerFreeType()) {
			return false;
		}

		for (FactionPlayer fplayer : fplayers) {
			if (fplayer.isOnline()) {
				return true;
			}
		}

		// even if all players are technically logged off, maybe someone was on recently enough to not consider them officially offline yet
		if (Conf.considerFactionsReallyOfflineAfterXMinutes > 0 && System.currentTimeMillis() < lastPlayerLoggedOffTime + (Conf.considerFactionsReallyOfflineAfterXMinutes * 60000)) {
			return true;
		}
		return false;
	}

	public void memberLoggedOff() {
		if (this.isNormal()) {
			lastPlayerLoggedOffTime = System.currentTimeMillis();
		}
	}

	// used when current leader is about to be removed from the faction; promotes new leader, or disbands faction if no other members left
	public void promoteNewLeader() {
		if (!this.isNormal()) {
			return;
		}
		if (this.isPermanent() && Conf.permanentFactionsDisableLeaderPromotion) {
			return;
		}

		FactionPlayer oldLeader = this.getFPlayerAdmin();

		// getAll list of moderators, or list of normal members if there are no moderators
		ArrayList<FactionPlayer> replacements = this.getFPlayersWhereRole(Role.MODERATOR);
		if (replacements == null || replacements.isEmpty()) {
			replacements = this.getFPlayersWhereRole(Role.NORMAL);
		}

		if (replacements == null || replacements.isEmpty()) { // faction admin is the only member; one-man faction
			if (this.isPermanent()) {
				if (oldLeader != null) {
					oldLeader.setRole(Role.NORMAL);
				}
				return;
			}

			// no members left and faction isn't permanent, so disband it
			if (Conf.logFactionDisband) {
				HCFactions.getInstance().log("The faction " + this.getTag() + " (" + this.getId() + ") has been disbanded since it has no members left.");
			}

			this.detach();
		} else { // promote new faction admin
			if (oldLeader != null) {
				oldLeader.setRole(Role.NORMAL);
			}
			replacements.get(0).setRole(Role.ADMIN);
			this.msg("<instance>Faction leader <h>%s<instance> has been removed. %s<instance> has been promoted as the new faction leader.", oldLeader == null ? "" : oldLeader.getName(), replacements.get(0).getName());
			HCFactions.getInstance().log("Faction " + this.getTag() + " (" + this.getId() + ") leader was removed. Replacement leader: " + replacements.get(0).getName());
		}
	}

	// ----------------------------------------------//
	// Messages
	// ----------------------------------------------//
	public void msg(String message, Object... args) {
		message = HCFactions.getInstance().txt.parse(message, args);

		for (FactionPlayer fplayer : this.getFPlayersWhereOnline(true)) {
			fplayer.sendMessage(message);
		}
	}

	public void sendMessage(String message) {
		for (FactionPlayer fplayer : this.getFPlayersWhereOnline(true)) {
			fplayer.sendMessage(message);
		}
	}

	public void sendMessage(List<String> messages) {
		for (FactionPlayer fplayer : this.getFPlayersWhereOnline(true)) {
			fplayer.sendMessage(messages);
		}
	}

	// ----------------------------------------------//
	// Ownership of specific claims
	// ----------------------------------------------//

	// TODO: Remove subclaims on unclaim of land

	public void clearAllClaimOwnership() {
		claimOwnership.clear();
	}

	public void clearClaimOwnership(FLocation loc) {
		claimOwnership.remove(loc);
	}

	public void clearClaimOwnership(String playerName) {
		if (playerName == null || playerName.isEmpty()) {
			return;
		}

		Set<String> ownerData;
		String player = playerName.toLowerCase();

		for (Entry<FLocation, Set<String>> entry : claimOwnership.entrySet()) {
			ownerData = entry.getValue();

			if (ownerData == null) {
				continue;
			}

			Iterator<String> iter = ownerData.iterator();
			while (iter.hasNext()) {
				if (iter.next().equals(player)) {
					iter.remove();
				}
			}

			if (ownerData.isEmpty()) {
				claimOwnership.remove(entry.getKey());
			}
		}
	}


	public String getOwnerListString(FLocation loc) {
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null || ownerData.isEmpty()) {
			return "";
		}

		String ownerList = "";

		Iterator<String> iter = ownerData.iterator();
		while (iter.hasNext()) {
			if (!ownerList.isEmpty()) {
				ownerList += ", ";
			}
			ownerList += iter.next();
		}
		return ownerList;
	}

	public boolean playerHasOwnershipRights(FactionPlayer fplayer, FLocation loc) {
		// in own faction, with sufficient role or permission to bypass ownership?
		if (fplayer.getFaction() == this && (fplayer.getRole().isAtLeast(Conf.ownedAreaModeratorsBypass ? Role.MODERATOR : Role.ADMIN) || Permission.OWNERSHIP_BYPASS.has(fplayer.getPlayer()))) {
			return true;
		}

		// make sure claimOwnership is initialized
		if (claimOwnership.isEmpty()) {
			return true;
		}

		// need to check the ownership list, then
		Set<String> ownerData = claimOwnership.get(loc);

		// if no owner list, owner list is empty, or player is in owner list, they're allowed
		if (ownerData == null || ownerData.isEmpty() || ownerData.contains(fplayer.getName().toLowerCase())) {
			return true;
		}

		return false;
	}

	// ----------------------------------------------//
	// Persistance and entity management
	// ----------------------------------------------//
	@Override
	public void postDetach() {

		// Clean the board
		Board.clean();

		// Clean the fplayers
		FPlayers.getInstance().clean();
	}

	public boolean denyPvpProtEntry() {
		return !(this.isNone() || this.isSafeZone() || this.isWarZone());
	}

	public void setlastAllyTime() {
		lastAllyTime = System.currentTimeMillis();
	}

	public long getAllyCooldownRemaining() {
		long remaining = Conf.allyCooldown * 60000L - (System.currentTimeMillis() - lastAllyTime);
		return remaining > 0 ? remaining : 0;
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public void addLeaveTime() {
		long now = System.currentTimeMillis();
		playerLeaveTimes.add(now);
		playerLeaveTimes.removeIf(t -> now - t > Conf.factionSlotLockTime * 60 * 1000);
	}

	public int getLockedSlots() {
		if (Conf.factionSlotLockTime == 0) {
			return 0;
		}
		long now = System.currentTimeMillis();
		int count = 0;
		for (long t : playerLeaveTimes) {
			if (now - t < Conf.factionSlotLockTime * 60 * 1000) {
				count++;
			}
		}
		return count;
	}

	public Map<Location, SubclaimOwner> getSubclaims() {
		return subclaims;
	}

	public Map<FLocation, Integer> getAmountPaidForLand() {
		return amountPaidForLand;
	}
}
