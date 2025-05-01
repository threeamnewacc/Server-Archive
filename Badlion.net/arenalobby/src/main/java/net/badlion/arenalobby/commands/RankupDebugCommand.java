package net.badlion.arenalobby.commands;

import net.badlion.arenalobby.helpers.RankUpHelper;
import net.badlion.gberry.utils.RatingUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankupDebugCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if(commandSender instanceof Player){
			Player player = (Player) commandSender;
			if(player.isOp()){
				RankUpHelper.handleRankedUp(player, RatingUtil.Rank.EMERALD_II);
			}
		}

		return false;
	}
}
