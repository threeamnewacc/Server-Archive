package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Permission;

public class CmdChat extends FCommand {

	public CmdChat() {
		super();
		this.aliases.add("c");
		this.aliases.add("chat");

		// this.requiredArgs.add("");
		this.optionalArgs.put("mode", "next");

		this.permission = Permission.CHAT.node;
		this.disableOnLock = false;

		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		if (!Conf.factionOnlyChat) {
			msg("<b>The built in chat chat channels are disabled on this server.");
			return;
		}

		String modeString = this.argAsString(0);

		if (modeString != null) {
			modeString = modeString.toLowerCase();
			if (modeString.startsWith("plugin")) {
				this.fme.setChatMode(ChatMode.PUBLIC);
				msg("<instance>Public chat mode.");
			} else if (modeString.startsWith("a")) {
				this.fme.setChatMode(ChatMode.ALLIANCE);
				msg("<instance>Alliance chat mode.");
			} else if (modeString.startsWith("f")) {
				this.fme.setChatMode(ChatMode.FACTION);
				msg("<instance>Faction chat mode.");
			} else {
				msg("<b>Unrecognised chat mode. Plase try 'public', 'faction', or 'ally'.");
			}
		} else {
			ChatMode nextMode = this.fme.getChatMode().getNext();
			this.fme.setChatMode(nextMode);
			msg("<instance>" + nextMode.getName() + " chat mode.");
		}


	}
}
