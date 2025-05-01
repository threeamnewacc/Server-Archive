package net.badlion.arenalobby.commands;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.exceptions.NoRatingFoundException;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.arenalobby.managers.LadderManager;
import net.badlion.arenalobby.managers.RatingManager;
import net.badlion.gberry.utils.RatingUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class RatingCommand extends GCommandExecutor {

	private String usageString = "";

	public RatingCommand() {
		super(1); // 1 args required
	}

	@Override
	public void onGroupCommand(Command command, String label, String[] args) {
		KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(args[0]);
		if (kitRuleSet != null) {
			try {
				this.player.sendFormattedMessage("{0}Your {1} rating is: {2}", ChatColor.GREEN, args[0], ChatColor.LIGHT_PURPLE +
						RatingUtil.Rank.getRankByElo(RatingManager.getGroupRating(this.group, LadderManager.getLadder(args[0], ArenaCommon.LadderType.RANKED_1V1))).getName());
			} catch (NoRatingFoundException e) {
				this.player.sendFormattedMessage("{0}Could not find your rating at the moment. Try again in a few seconds.", ChatColor.RED);
			}
		} else {
			this.usage(this.player);
		}
	}

	@Override
	public void usage(CommandSender sender) {
		// Do this here to make sure that ladders are initialized
		if (this.usageString.length() == 0) {
			StringBuilder sb = new StringBuilder(ChatColor.YELLOW + "Available Kits (case-sensitive): ");
			for (Ladder ladder : LadderManager.getLadderMap(ArenaCommon.LadderType.RANKED_1V1).values()) {
				sb.append(ladder.getKitRuleSet().getName());
				sb.append(", ");
			}

			this.usageString = sb.toString().substring(0, sb.toString().length() - 2);
		}

		sender.sendMessage(ChatColor.YELLOW + "/rating <kit>");
		sender.sendMessage(this.usageString);
	}

}
