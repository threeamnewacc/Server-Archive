package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;

import java.util.ArrayList;
import java.util.List;

public class CmdHelp extends FCommand {

	private List<ArrayList<String>> helpPages;

	public CmdHelp() {
		super();
		this.aliases.add("help");
		this.aliases.add("h");
		this.aliases.add("?");

		//this.requiredArgs.add("");
		this.optionalArgs.put("page", "1");

		this.permission = Permission.HELP.node;
		this.disableOnLock = false;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		if (helpPages == null) updateHelp();

		int page = this.argAsInt(0, 1);

		sendMessage(p.txt.titleize("Factions Help (" + page + "/" + helpPages.size() + ")"));

		page -= 1;

		if (page < 0 || page >= helpPages.size()) {
			msg("<b>This page does not exist");
			return;
		}
		sendMessage(helpPages.get(page));
	}

	public void updateHelp() {
		helpPages = new ArrayList<>();
		ArrayList<String> pageLines;

		pageLines = new ArrayList<>();
		//pageLines.add( plugin.cmdBase.cmdHelp.getUseageTemplate(true) );
		pageLines.add(p.cmdBase.cmdList.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdShow.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdJoin.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdLeave.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdHome.getUseageTemplate(true));
		helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(p.cmdBase.cmdCreate.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdTag.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdInvite.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdDeinvite.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdSethome.getUseageTemplate(true));
		helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(p.cmdBase.cmdClaim.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdAutoClaim.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdUnclaim.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdUnclaimall.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdKick.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdMod.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdAdmin.getUseageTemplate(true));
		helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(p.cmdBase.cmdMap.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdDisband.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdRelationAlly.getUseageTemplate(true));
		pageLines.add(p.cmdBase.cmdRelationNeutral.getUseageTemplate(true));
		helpPages.add(pageLines);
	}
}
