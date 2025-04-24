package com.massivecraft.factions.cmd;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.MCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class FCommand extends MCommand<HCFactions> {

	public boolean disableOnLock;

	public FactionPlayer fme;
	public Faction myFaction;
	public boolean senderMustBeMember;
	public boolean senderMustBeModerator;
	public boolean senderMustBeAdmin;

	public boolean isMoneyCommand;

	public FCommand() {
		super(HCFactions.getInstance());

		// Due to safety reasons it defaults to disable on lock.
		disableOnLock = true;

		// The money commands must be disabled if money should not be used.
		isMoneyCommand = false;

		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void execute(CommandSender sender, List<String> args, List<MCommand<?>> commandChain) {
		if (sender instanceof Player) {
			this.fme = FPlayers.getInstance().get((Player) sender);
			this.myFaction = this.fme.getFaction();
		} else {
			this.fme = null;
			this.myFaction = null;
		}
		super.execute(sender, args, commandChain);
	}

	@Override
	public boolean isEnabled() {
		if (p.isLocked() && this.disableOnLock) {
			msg("<b>Factions was locked by an admin. Please try again later.");
			return false;
		}

		return true;
	}

	@Override
	public boolean validSenderType(CommandSender sender, boolean informSenderIfNot) {
		boolean superValid = super.validSenderType(sender, informSenderIfNot);
		if (!superValid) {
			return false;
		}

		if (!(this.senderMustBeMember || this.senderMustBeModerator || this.senderMustBeAdmin)) {
			return true;
		}

		if (!(sender instanceof Player)) {
			return false;
		}

		FactionPlayer fplayer = FPlayers.getInstance().get((Player) sender);

		if (!fplayer.hasFaction()) {
			sender.sendMessage(p.txt.parse("<b>You are not member of any faction."));
			return false;
		}

		if (this.senderMustBeModerator && !fplayer.getRole().isAtLeast(Role.MODERATOR)) {
			sender.sendMessage(p.txt.parse("<b>Only faction officers can %s.", this.getHelpShort()));
			return false;
		}

		if (this.senderMustBeAdmin && !fplayer.getRole().isAtLeast(Role.ADMIN)) {
			sender.sendMessage(p.txt.parse("<b>Only faction leaders can %s.", this.getHelpShort()));
			return false;
		}

		return true;
	}

	// -------------------------------------------- //
	// Assertions
	// -------------------------------------------- //
	public boolean assertHasFaction() {
		if (me == null) {
			return true;
		}

		if (!fme.hasFaction()) {
			sendMessage("You are not member of any faction.");
			return false;
		}
		return true;
	}

	public boolean assertMinRole(Role role) {
		if (me == null) {
			return true;
		}

		if (fme.getRole().value < role.value) {
			msg("<b>You <h>must be " + role + "<b> to " + this.getHelpShort() + ".");
			return false;
		}
		return true;
	}

	// -------------------------------------------- //
	// Argument Readers
	// -------------------------------------------- //
	// FPLAYER ======================
	public FactionPlayer strAsFPlayer(String name, FactionPlayer def, boolean msg) {
		FactionPlayer ret = def;

		if (name != null) {
			FactionPlayer fplayer = FPlayers.getInstance().getByName(name);
			if (fplayer != null) {
				ret = fplayer;
			}
		}

		if (msg && ret == null) {
			this.msg("<b>No player \"<plugin>%s<b>\" could be found.", name);
		}

		return ret;
	}

	public FactionPlayer argAsFPlayer(int idx, FactionPlayer def, boolean msg) {
		return this.strAsFPlayer(this.argAsString(idx), def, msg);
	}

	public FactionPlayer argAsFPlayer(int idx, FactionPlayer def) {
		return this.argAsFPlayer(idx, def, true);
	}

	public FactionPlayer argAsFPlayer(int idx) {
		return this.argAsFPlayer(idx, null);
	}

	// FACTION ======================
	public Faction strAsFaction(String name, Faction def, boolean msg) {
		Faction ret = def;

		if (name != null) {
			Faction faction = null;

			// First we try an exact match
			if (faction == null) {
				faction = Factions.getInstance().getByTag(name);
			}

			// Next we match player names
			if (faction == null) {
				FactionPlayer fplayer = FPlayers.getInstance().getByName(name);
				if (fplayer != null) {
					faction = fplayer.getFaction();
				}
			}

			if (faction != null) {
				ret = faction;
			}
		}

		if (msg && ret == null) {
			this.msg("<b>The faction or player \"<plugin>%s<b>\" could not be found.", name);
		}

		return ret;
	}

	public Faction argAsFaction(int idx, Faction def, boolean msg) {
		return this.strAsFaction(this.argAsString(idx), def, msg);
	}

	public Faction argAsFaction(int idx, Faction def) {
		return this.argAsFaction(idx, def, true);
	}

	public Faction argAsFaction(int idx) {
		return this.argAsFaction(idx, null);
	}

	// -------------------------------------------- //
	// Commonly used logic
	// -------------------------------------------- //
	public boolean canIAdministerYou(FactionPlayer i, FactionPlayer you) {
		if (!i.getFaction().equals(you.getFaction())) {
			i.sendMessage(p.txt.parse("%s <b>is not in the same faction as you.", you.describeTo(i, true)));
			return false;
		}

		if (i.getRole().value > you.getRole().value || i.getRole().equals(Role.ADMIN)) {
			return true;
		}

		if (you.getRole().equals(Role.ADMIN)) {
			i.sendMessage(p.txt.parse("<b>Only the faction leader can do that."));
		} else if (i.getRole().equals(Role.MODERATOR)) {
			if (i == you) {
				return true; // Moderators can control themselves
			} else {
				i.sendMessage(p.txt.parse("<b>Officers can't control each other..."));
			}
		} else {
			i.sendMessage(p.txt.parse("<b>You must be a faction officer to do that."));
		}

		return false;
	}
}
