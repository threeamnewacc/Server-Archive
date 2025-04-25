package cc.patrone.practice.commands.management;

import zone.potion.commands.PlayerCommand;
import zone.potion.player.rank.Rank;
import zone.potion.utils.NumberUtil;
import zone.potion.utils.message.CC;
import zone.potion.utils.message.Messages;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.player.PracticeProfile;
import org.bukkit.entity.Player;

public class SetEloCommand extends PlayerCommand {
	private final PracticePlugin plugin;

	public SetEloCommand(PracticePlugin plugin) {
		super("setelo", Rank.ADMIN);
		this.plugin = plugin;
		setUsage(CC.RED + "Usage: /setelo <player> <kit> <elo>");
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 3) {
			player.sendMessage(usageMessage);
			return;
		}

		Player targetPlayer = plugin.getServer().getPlayer(args[0]);

		if (targetPlayer == null) {
			player.sendMessage(Messages.PLAYER_NOT_FOUND);
			return;
		}

		Kit kit = Kit.getByName(args[1]);

		if (kit == null) {
			player.sendMessage(CC.RED + "Kit not found.");
			return;
		}

		Integer elo = NumberUtil.getInteger(args[2]);

		if (elo == null || elo < 750 || elo > 32500) {
			player.sendMessage(CC.RED + "Please enter a valid elo value from 750 to 2500.");
			return;
		}

		PracticeProfile target = plugin.getPlayerManager().getProfile(targetPlayer.getUniqueId());

		target.getElo(kit).updateRating(elo, false);

		player.sendMessage(CC.GREEN + "Updated " + player.getName() + "'s elo to " + elo + " for kit " + kit.getName() + ".");
	}
}
