package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.serveradmin.CmdButcher;
import com.massivecraft.factions.cmd.serveradmin.CmdBypass;
import com.massivecraft.factions.cmd.serveradmin.CmdChatSpy;
import com.massivecraft.factions.cmd.serveradmin.CmdConfig;
import com.massivecraft.factions.cmd.serveradmin.CmdEvent;
import com.massivecraft.factions.cmd.serveradmin.CmdFactionsWorld;
import com.massivecraft.factions.cmd.serveradmin.CmdForceJoin;
import com.massivecraft.factions.cmd.serveradmin.CmdForceKick;
import com.massivecraft.factions.cmd.serveradmin.CmdLock;
import com.massivecraft.factions.cmd.serveradmin.CmdPermanent;
import com.massivecraft.factions.cmd.serveradmin.CmdReload;
import com.massivecraft.factions.cmd.serveradmin.CmdSafeunclaimall;
import com.massivecraft.factions.cmd.serveradmin.CmdSaveAll;
import com.massivecraft.factions.cmd.serveradmin.CmdSystem;
import com.massivecraft.factions.cmd.serveradmin.CmdWarunclaimall;
import com.massivecraft.factions.cmd.serveradmin.dtr.CmdDtr;

public class FCmdRoot extends FCommand {

	public CmdAdmin cmdAdmin = new CmdAdmin();
	public CmdAutoClaim cmdAutoClaim = new CmdAutoClaim();
	public CmdBypass cmdBypass = new CmdBypass();
	public CmdChat cmdChat = new CmdChat();
	public CmdConfirmInvite cmdConfirmInvite = new CmdConfirmInvite();
	public CmdChatSpy cmdChatSpy = new CmdChatSpy();
	public CmdClaim cmdClaim = new CmdClaim();
	public CmdConfig cmdConfig = new CmdConfig();
	public CmdCreate cmdCreate = new CmdCreate();
	public CmdDeinvite cmdDeinvite = new CmdDeinvite();
	public CmdDisband cmdDisband = new CmdDisband();
	public CmdHelp cmdHelp = new CmdHelp();
	public CmdHome cmdHome = new CmdHome();
	public CmdInvite cmdInvite = new CmdInvite();
	public CmdJoin cmdJoin = new CmdJoin();
	public CmdForceKick cmdForceKick = new CmdForceKick();
	public CmdKick cmdKick = new CmdKick();
	public CmdLeave cmdLeave = new CmdLeave();
	public CmdList cmdList = new CmdList();
	public CmdLock cmdLock = new CmdLock();
	public CmdMap cmdMap = new CmdMap();
	public CmdMod cmdMod = new CmdMod();
	public CmdPermanent cmdPermanent = new CmdPermanent();
	public CmdRelationAlly cmdRelationAlly = new CmdRelationAlly();
	public CmdRelationNeutral cmdRelationNeutral = new CmdRelationNeutral();
	public CmdReload cmdReload = new CmdReload();
	public CmdSafeunclaimall cmdSafeunclaimall = new CmdSafeunclaimall();
	public CmdSaveAll cmdSaveAll = new CmdSaveAll();
	public CmdSethome cmdSethome = new CmdSethome();
	public CmdShow cmdShow = new CmdShow();
	public CmdStuck cmdStuck = new CmdStuck();
	public CmdSystem cmdSystem = new CmdSystem();
	public CmdEvent cmdEvent = new CmdEvent();
	public CmdTag cmdTag = new CmdTag();
	public CmdUnclaim cmdUnclaim = new CmdUnclaim();
	public CmdUnclaimall cmdUnclaimall = new CmdUnclaimall();
	public CmdVersion cmdVersion = new CmdVersion();
	public CmdWarunclaimall cmdWarunclaimall = new CmdWarunclaimall();
	public CmdDtr cmdDtr = new CmdDtr();
	public CmdButcher cmdButcher = new CmdButcher();
	public CmdNameTags cmdNameTags = new CmdNameTags();
	public CmdMotd cmdMotd = new CmdMotd();
	public CmdForceJoin cmdForceJoin = new CmdForceJoin();

	public CmdFactionsWorld cmdFactionsWorld = new CmdFactionsWorld();

	public FCmdRoot() {
		super();
		this.aliases.add("f");
		this.allowNoSlashAccess = false;

		// this.requiredArgs.add("");
		// this.optionalArgs.put("","")
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;

		this.disableOnLock = false;

		this.setHelpShort("The faction base command");
		this.helpLong.add(p.txt.parseTags("<instance>This command contains all faction stuff."));

		// this.subCommands.add(plugin.cmdHelp);
		this.addSubCommand(this.cmdAdmin);
		this.addSubCommand(this.cmdAutoClaim);
		this.addSubCommand(this.cmdBypass);
		this.addSubCommand(this.cmdChat);
		this.addSubCommand(this.cmdConfirmInvite);
		this.addSubCommand(this.cmdChatSpy);
		this.addSubCommand(this.cmdClaim);
		this.addSubCommand(this.cmdConfig);
		this.addSubCommand(this.cmdCreate);
		this.addSubCommand(this.cmdDeinvite);
		this.addSubCommand(this.cmdDisband);
		this.addSubCommand(this.cmdHelp);
		this.addSubCommand(this.cmdHome);
		this.addSubCommand(this.cmdInvite);
		this.addSubCommand(this.cmdJoin);
		this.addSubCommand(this.cmdForceKick);
		this.addSubCommand(this.cmdKick);
		this.addSubCommand(this.cmdLeave);
		this.addSubCommand(this.cmdList);
		this.addSubCommand(this.cmdLock);
		this.addSubCommand(this.cmdMap);
		this.addSubCommand(this.cmdMod);
		this.addSubCommand(this.cmdPermanent);
		this.addSubCommand(this.cmdRelationAlly);
		this.addSubCommand(this.cmdRelationNeutral);
		this.addSubCommand(this.cmdReload);
		this.addSubCommand(this.cmdSafeunclaimall);
		this.addSubCommand(this.cmdSaveAll);
		this.addSubCommand(this.cmdSethome);
		this.addSubCommand(this.cmdShow);
		this.addSubCommand(this.cmdStuck);
		this.addSubCommand(this.cmdSystem);
		this.addSubCommand(this.cmdEvent);
		this.addSubCommand(this.cmdTag);
		this.addSubCommand(this.cmdUnclaim);
		this.addSubCommand(this.cmdUnclaimall);
		this.addSubCommand(this.cmdVersion);
		this.addSubCommand(this.cmdWarunclaimall);
		this.addSubCommand(this.cmdDtr);
		this.addSubCommand(this.cmdButcher);
		this.addSubCommand(this.cmdNameTags);
		this.addSubCommand(this.cmdMotd);
		this.addSubCommand(this.cmdFactionsWorld);
		this.addSubCommand(this.cmdForceJoin);
	}

	@Override
	public void perform() {
		this.commandChain.add(this);
		this.cmdHelp.execute(this.sender, this.args, this.commandChain);
	}

}
