package net.badlion.tdm;

import net.badlion.gberry.Gberry;
import net.badlion.ministats.MiniStats;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGTeam;
import net.badlion.mpg.commands.VoteCommand;
import net.badlion.mpg.managers.MPGKitManager;
import net.badlion.tdm.kits.TDMKit;
import net.badlion.tdm.tasks.KeepAliveTask;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class TDM extends JavaPlugin {

	private static TDM plugin;

	public TDM() {
		TDM.plugin = this;

		MiniStats.TAG = "TDM";
		MiniStats.TABLE_NAME = "tdm_ministats";

		Gberry.coudhDBDatabase = "tdm_beta";

		MPG.MPG_GAME_NAME = "TDM";
		MPG.MPG_PREFIX = ChatColor.AQUA + "[" + ChatColor.RED + "TDM" + ChatColor.AQUA + "] ";

		// TODO: FIGURE OUT HOW WE WANT TO DO SPECTATING
		MPG.GAME_TYPE = MPG.GameType.PARTY.setup(true, true, true, true, true);

		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.PLAYERS_TO_START.name(), 1);
		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.MAX_PLAYERS.name(), 32);
		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.GAME_TIME_LIMIT.name(), 60); // TODO: CHANGE TO 600
		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.SCORE_LIMIT.name(), 3); // TODO: CHANGE TO 100
		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.USES_KITS.name(), true);

		// TODO: v - SERVER LIMIT SHOULD BE INFINITE BUT ACTUAL TEAM LIMIT SHOULD BE IN ACCOUNT, NEED TO TALK TO GBERRY
		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.KICK_NON_DONATORS_IF_FULL.name(), false);
		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.REBOOT_ON_GAME_END.name(), false);
		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.SEND_TO_SERVER_ON_END.name(), false);

		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.RESPAWN_TIME.name(), 3);
		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.RESPAWN_RESISTANCE_TIME.name(), 6);

		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.SPECTATOR_ON_DEATH.name(), false);
		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.USE_SKULL_SPECTATOR_INVENTORY.name(), false);

		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.VOTING_TIME.name(), 30);
		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.NUM_OF_VOTE_CHOICES.name(), 3);
		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.CAN_VOTE_FOR_LAST_WINNER.name(), false);
		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.VOTE_TYPE.name(), VoteCommand.VoteType.KIT);
		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.VOTE_SELECTION_METHOD.name(), VoteCommand.VoteSelectionMethod.MAJORITY_VOTE);

		MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.NUM_OF_TEAMS.name(), 2);

		// TODO: ADD MORE KITS
		MPG.VOTE_OBJECTS.add(TDMKit.getKit());
	}

	@Override
	public void onEnable() {
		// Setup our TDM teams
		for (int i = 0; i < MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.NUM_OF_TEAMS); i++) {
			ChatColor color = MPGTeam.TEAM_COLORS[i];
			new TDMTeam(color);
		}

		if (!Gberry.serverName.contains("test")) {
			new KeepAliveTask().runTaskTimerAsynchronously(this, 60, 60);
		}
	}

	@Override
	public void onDisable() {

	}

	public static TDM getInstance() {
		return plugin;
	}

	public TDMGame getCurrentGame() {
		return (TDMGame) MPG.getInstance().getMPGGame();
	}

}
