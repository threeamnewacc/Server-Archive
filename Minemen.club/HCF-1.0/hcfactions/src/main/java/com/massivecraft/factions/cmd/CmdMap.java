package com.massivecraft.factions.cmd;

import club.minemen.core.util.finalutil.CC;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;

public class CmdMap extends FCommand {

	public CmdMap() {
		super();
		this.aliases.add("map");

		// this.requiredArgs.add("");
		this.optionalArgs.put("mode", "chat/surface");

		this.permission = Permission.MAP.node;
		this.disableOnLock = false;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		if (this.argIsSet(0)) {
			if (this.argAsString(0).equalsIgnoreCase("chat")) {
				this.fme.setMapAutoUpdating(!this.fme.isMapAutoUpdating());
				if (this.fme.isMapAutoUpdating()) {
					this.showMap();
				}
				this.fme.getPlayer().sendFormattedMessage("{0}Chat map has been {1}{0}.", CC.PRIMARY, this.fme.isMapAutoUpdating() ? CC.GREEN + "enabled" : CC.RED + "disabled");
			} else if (this.argAsString(0).equalsIgnoreCase("surface")) {
				this.fme.setPhysicalMapUpdating(!this.fme.isPhysicalMapUpdating());
				this.fme.setPhysicalWallMapUpdating(false);
				this.fme.getPlayer().sendFormattedMessage("{0}Surface map has been {1}{0}.", CC.PRIMARY, this.fme.isPhysicalMapUpdating() ? CC.GREEN + "enabled" : CC.RED + "disabled");
			} /*else if (this.argAsString(0).equalsIgnoreCase("wall")) {
				if (fme.isPhysicalWallMapUpdating()) {
					fme.setPhysicalWallMapUpdating(false);
					msg("<instance>Physical WALL Map auto update <red>DISABLED.");
				} else {
					fme.setPhysicalWallMapUpdating(true);
					fme.setPhysicalMapUpdating(false);
					msg("<instance>Physical WALL Map auto update <green>ENABLED.");
				}
			}*/
		} else {
			this.showMap();
		}
	}

	private void showMap() {
		sendMessage(Board.getMap(myFaction, new FLocation(fme), fme.getPlayer().getLocation().getYaw()));
	}

}
