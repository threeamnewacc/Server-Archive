package net.badlion.arenalobby.commands;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.managers.LadderManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class JoinQueueCommand extends GCommandExecutor {

	public JoinQueueCommand() {
		super(2);
	}

	@Override
	public void onGroupCommand(Command command, String label, String[] args) {
		KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(args[0]);

		if(kitRuleSet == null){
			player.sendFormattedMessage("{0}Could not find kit.", ChatColor.RED);
			return;
		}

		String type = args[1];

		ArenaCommon.LadderType ladderType = null;
		for(ArenaCommon.LadderType ladderType1 : ArenaCommon.LadderType.values()){
			if(ladderType1.getTag().equals(type)){
				ladderType = ladderType1;
			}
		}

		if(ladderType == null){
			player.sendFormattedMessage("{0}Could not find queue.", ChatColor.RED);
			return;
		}

		if(ladderType.equals(ArenaCommon.LadderType.RANKED_1V1)
				|| ladderType.equals(ArenaCommon.LadderType.RANKED_2V2)
				|| ladderType.equals(ArenaCommon.LadderType.RANKED_3V3)
				/* TODO: Clan ladder is not done mcp side yet? || ladderType.equals(ArenaCommon.LadderType.RANKED_5V5_CLAN)*/
				|| ladderType.equals(ArenaCommon.LadderType.UNRANKED_1V1)){
			LadderManager.joinLadderQueue(group, player, kitRuleSet.getName(), ladderType);
			return;
		}
		player.sendFormattedMessage("{0}Could not find queue.", ChatColor.RED);
	}

	@Override
	public void usage(CommandSender sender) {
		sender.sendFormattedMessage("{0}Command usage: {1} to join a queue", ChatColor.RED, "/joinqueue <kit> <type>");
	}
}
