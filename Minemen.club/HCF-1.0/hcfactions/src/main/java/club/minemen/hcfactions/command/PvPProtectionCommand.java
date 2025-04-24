package club.minemen.hcfactions.command;

import club.minemen.core.rank.Rank;
import club.minemen.core.util.cmd.CommandHandler;
import club.minemen.core.util.cmd.annotation.Param;
import club.minemen.core.util.cmd.annotation.commandTypes.BaseCommand;
import club.minemen.core.util.cmd.annotation.commandTypes.SubCommand;
import club.minemen.core.util.finalutil.CC;
import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @since 12/2/2017
 */
@RequiredArgsConstructor
public class PvPProtectionCommand implements CommandHandler {

	private final HCFactions plugin;

	@BaseCommand(name = "pvp")
	public void pvpCommand() {
		throw new IllegalArgumentException("");
	}

	@SubCommand(baseCommand = "pvp", name = {"time", "check"}, description = "Check how much time you have remaining")
	public void checkTime(Player player) {
		FactionPlayer factionPlayer = FPlayers.getInstance().getByName(player.getName());
		if (factionPlayer == null) {
			return;
		}

		if (factionPlayer.hasPvpProtection()) {
			player.sendFormattedMessage("{0}You have {1}{2}{0} of pvp protection left.", CC.PRIMARY, CC.SECONDARY, HCFactions.getInstance().pvpProtectionManager.getProtectionTimeString(
					factionPlayer.getPlayer()));
		} else {
			player.sendFormattedMessage("{0}You currently do not have pvp protection.", CC.RED);
		}
	}

	@SubCommand(baseCommand = "pvp", name = {"enable"}, description = "Turns off your pvp protection")
	public void enable(Player player) {
		FactionPlayer factionPlayer = FPlayers.getInstance().getByName(player.getName());
		if (factionPlayer == null) {
			return;
		}

		if (!factionPlayer.hasPvpProtection()) {
			player.sendFormattedMessage("{0}You are already off pvp protection.", CC.RED);
			return;
		}

		factionPlayer.removePvpProtection();
		player.sendFormattedMessage("{0}You no longer have pvp protection.", CC.B_RED);
	}

	@SubCommand(baseCommand = "pvp", name = {"give"}, description = "Give a player pvp protection", rank = Rank.PLAT_ADMIN)
	public void give(CommandSender commandSender, @Param(name = "target") FactionPlayer target) {
		if (target.isOffline()) {
			commandSender.sendFormattedMessage("{0}{1} is offline.", CC.RED, target.getName());
			return;
		}

		if (target.hasPvpProtection()) {
			commandSender.sendFormattedMessage("{0}{1} already has pvp protection.", CC.RED, target.getName());
			return;
		}

		target.setPvpProtection(Conf.pvpProtectionTime * 60 * 1000);
		target.startPvpProtectionCountdown();
		target.getPlayer().sendFormattedMessage("{0}You''ve been given pvp protection.", CC.PRIMARY);
		commandSender.sendFormattedMessage("{0}You gave {1}{2}{0} pvp protection.", CC.PRIMARY, CC.SECONDARY, target.getName());
	}
}
