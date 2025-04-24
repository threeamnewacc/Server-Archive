package com.massivecraft.factions.struct;

import club.minemen.hcfactions.HCFactions;
import org.bukkit.command.CommandSender;

public enum Permission {

	MANAGE_SAFE_ZONE("managesafezone"),
	MANAGE_WAR_ZONE("managewarzone"),
	OWNERSHIP_BYPASS("ownershipbypass"),
	ADMIN("admin"),
	ADMIN_ANY("admin.any"),
	AUTOCLAIM("autoclaim"),
	BYPASS("bypass"),
	CHAT("chat"),
	CHATSPY("chatspy"),
	CLAIM("claim"),
	CLAIM_RADIUS("claim.radius"),
	CONFIG("config"),
	CREATE("create"),
	DEINVITE("deinvite"),
	DISBAND("disband"),
	DISBAND_ANY("disband.any"),
	HELP("help"),
	HOME("home"),
	INVITE("invite"),
	JOIN("join"),
	JOIN_ANY("join.any"),
	JOIN_OTHERS("join.others"),
	KICK("kick"),
	KICK_ANY("kick.any"),
	LEAVE("leave"),
	LIST("list"),
	LOCK("lock"),
	MAP("map"),
	MOD("mod"),
	MOD_ANY("mod.any"),
	SET_PERMANENT("setpermanent"),
	RELATION("relation"),
	RELOAD("reload"),
	REVIVE("revive"),
	SAVE("save"),
	SETHOME("sethome"),
	SETHOME_ANY("sethome.any"),
	SHOW("show"),
	STUCK("stuck"),
	TAG("tag"),
	UNCLAIM("unclaim"),
	UNCLAIM_ALL("unclaimall"),
	VERSION("version"),
	SET_DTR("setdtr"),
	BUTCHER("butcher"),
	STAFF("staff"),
	MOTD("motd");

	public final String node;

	Permission(final String node) {
		this.node = "factions." + node;
	}

	public boolean has(CommandSender sender, boolean informSenderIfNot) {
		return HCFactions.getInstance().perm.has(sender, this.node, informSenderIfNot);
	}

	public boolean has(CommandSender sender) {
		return has(sender, false);
	}
}
