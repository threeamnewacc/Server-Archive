package us.zonix.core.punishment;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum PunishmentType {

	BAN("permanently banned", "unbanned", "\n&cYour account has been suspended." + "\n\n" + "To appeal, visit http://www.zonix.us."),
	TEMPBAN("temporarily banned", "unbanned", "\n&cYour account has been temporarily suspended." + "\n\n" + "To appeal, visit http://www.zonix.us."),
	MUTE("temporarily muted", "unmuted", "&cYou are currently muted for %DURATION%."),
	BLACKLIST("blacklisted", "unblacklisted", "\n&cYour account has been blacklisted." + "\n\n" + "This punishment cannot be appealed.");

	@Getter String context;
	@Getter String undoContext;
	@Getter String message;

	PunishmentType(String context, String undoContext, String message) {
		this.context = context;
		this.undoContext = undoContext;
		this.message = ChatColor.translateAlternateColorCodes('&', message);
	}

}
