package net.badlion.potpvp.commands;

import net.badlion.potpvp.exceptions.NoRatingFoundException;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.RatingManager;
import net.badlion.potpvp.rulesets.KitRuleSet;
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
				this.player.sendMessage(ChatColor.GREEN + "Your " + args[0] + " rating is: " + ChatColor.LIGHT_PURPLE +
												RatingManager.getGroupRating(this.group, Ladder.getLadder(args[0], Ladder.LadderType.OneVsOneRanked)));
			} catch (NoRatingFoundException e) {
				this.player.sendMessage(ChatColor.RED + "Could not find your rating at the moment. Try again in a few seconds.");
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
			for (Ladder ladder : Ladder.getLadderMap(Ladder.LadderType.OneVsOneRanked).values()) {
				sb.append(ladder.getKitRuleSet().getName());
				sb.append(", ");
			}

			this.usageString = sb.toString().substring(0, sb.toString().length() - 2);
		}

		sender.sendMessage(ChatColor.YELLOW + "/rating <kit>");
		sender.sendMessage(this.usageString);
	}

}
