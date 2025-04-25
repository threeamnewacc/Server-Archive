package cc.patrone.practice.commands;

import zone.potion.commands.PlayerCommand;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.leaderboard.Leaderboard;
import cc.patrone.practice.leaderboard.RankedRating;
import cc.patrone.practice.utils.StringUtil;
import org.bukkit.entity.Player;

public class LeaderboardCommand extends PlayerCommand {
	private static final int PAGE_SIZE = 10;
	private final PracticePlugin plugin;

	public LeaderboardCommand(PracticePlugin plugin) {
		super("leaderboard");
		this.plugin = plugin;
		setAliases("leaderboards", "lb", "topelo", "elo", "topstats");
		setUsage(CC.RED + "Usage: /leaderboard <kit> [page]");
	}

	private static int validIntegerOf(int max, String arg) {
		try {
			int i = Integer.parseInt(arg);

			if (i < 1) {
				return 1;
			} else if (i > max) {
				return max;
			} else {
				return i;
			}
		} catch (NumberFormatException ex) {
			return 1;
		}
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return;
		}

		Kit kit = Kit.getByName(args[0]);

		if (kit == null) {
			player.sendMessage(CC.RED + "That's not a kit!");
			return;
		}

		if (!kit.isRanked()) {
			player.sendMessage(CC.RED + "That kit isn't ranked!");
			return;
		}

		Leaderboard leaderboard = plugin.getLeaderboardManager().getLeaderboardByKit(kit);

		if (leaderboard == null || leaderboard.getRatings().isEmpty()) {
			player.sendMessage(CC.RED + "The leaderboards are empty... Please wait until they have been updated.");
			return;
		}

		int size = leaderboard.getRatings().size();
		int maxPages = size % PAGE_SIZE == 0 ? size / PAGE_SIZE : size / PAGE_SIZE + 1;
		int pageIndex = args.length < 2 ? 1 : validIntegerOf(maxPages, args[1]);
		int index = (pageIndex - 1) * PAGE_SIZE;
		int count = index + 1;

		for (RankedRating rating : leaderboard.getRatings(index)) {
			if (count > index + (PAGE_SIZE + 1)) {
				break;
			}

			player.sendMessage(CC.PRIMARY + StringUtil.formatNumberWithCommas(count) + ". " + CC.PRIMARY
					+ rating.getDisplayName() + CC.GRAY + ": " + CC.SECONDARY + StringUtil.formatNumberWithCommas(rating.getRating()) + " ELO");

			count++;
		}
	}
}
