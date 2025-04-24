package club.minemen.hcfactions.command;

import club.minemen.core.rank.Rank;
import club.minemen.core.util.cmd.CommandHandler;
import club.minemen.core.util.cmd.annotation.Param;
import club.minemen.core.util.cmd.annotation.commandTypes.BaseCommand;
import club.minemen.core.util.cmd.annotation.commandTypes.SubCommand;
import club.minemen.core.util.finalutil.CC;
import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.FactionPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @since 12/3/2017
 */
@RequiredArgsConstructor
public class LivesCommand implements CommandHandler {

	private final HCFactions plugin;

	@BaseCommand(name = "lives")
	public void livesCommand(CommandSender commandSender) {
		throw new IllegalArgumentException("Provided no arguments");
	}

	@SubCommand(baseCommand = "lives", name = "check", description = "Check how many lives you have")
	public void checkLivesCommand(CommandSender commandSender, @Param(name = "target", defaultTo = "self") FactionPlayer player) {
		int lives = this.plugin.getDeathbanManager().getLives(player.getUuid());
		commandSender.sendFormattedMessage("{1}{2}{0} has {1}{3}{0} live{4}{0}.", CC.PRIMARY, CC.SECONDARY, player.getName(), lives, lives == 1 ? "" : "s");
	}

	@SubCommand(baseCommand = "lives", name = "revive", description = "Revive a player with one of your lives")
	public void reviveCommand(Player player, @Param(name = "target") FactionPlayer targetPlayer) {
		if (player.getUniqueId() == targetPlayer.getUuid()) {
			player.sendFormattedMessage("{0}You''re not able to revive yourself!", CC.RED);
			return;
		}

		if (this.plugin.getDeathbanManager().getLives(player.getUniqueId()) <= 0) {
			player.sendFormattedMessage("{0}You require at least 1 life to revive someone!", CC.RED);
			return;
		}

		if (!this.plugin.getDeathbanManager().isDeathBanned(targetPlayer.getUuid())) {
			player.sendFormattedMessage("{0}{1} is not deathbanned!", CC.RED, targetPlayer.getName());
			return;
		}

		this.plugin.getDeathbanManager().removeLives(player.getUniqueId(), 1);
		HCFactions.getInstance().getDeathbanManager().unDeathbanPlayer(targetPlayer.getUuid());
		player.sendFormattedMessage("{0}You''ve revived {1}{2}{0}.", CC.PRIMARY, CC.SECONDARY, targetPlayer.getName());
		this.plugin.getLogger().info(player.getName() + " has used a life to revive " + targetPlayer.getName());
	}

	@SubCommand(baseCommand = "lives", name = {"gift", "send"}, description = "Send lives to another player")
	public void sendLivesCommand(Player player, @Param(name = "target player") FactionPlayer targetPlayer, int lives) {
		if (player.getUniqueId() == targetPlayer.getUuid()) {
			player.sendFormattedMessage("{0}You cannot send yourself lives!", CC.RED);
			return;
		}

		if (lives <= 0) {
			player.sendFormattedMessage("{0}Invalid amount of lives...", CC.RED);
			return;
		}

		if (this.plugin.getDeathbanManager().getLives(player.getUniqueId()) < lives) {
			player.sendFormattedMessage("{0}You don''t have {1} live{2}.", CC.RED, lives, lives == 1 ? "" : "s");
			return;
		}


		this.plugin.getDeathbanManager().addLives(targetPlayer.getUuid(), lives);
		this.plugin.getDeathbanManager().removeLives(player.getUniqueId(), lives);
		this.plugin.getLogger().info(player.getName() + " has given/gifted/sent " + targetPlayer.getName() + " " + lives + " lives");
		player.sendFormattedMessage("{0}You have sent {2}{0} {1}{3} live{4}{0}.", CC.PRIMARY, CC.SECONDARY, targetPlayer.getName(), lives, lives == 1 ? "" : "s");

		Player otherPlayer = targetPlayer.getPlayer();
		if (otherPlayer != null) {
			otherPlayer.sendFormattedMessage("{0}{2}{0} has sent you {1}{3} live{4}{0}.", CC.PRIMARY, CC.SECONDARY, player.getDisplayName(), lives, lives == 1 ? "" : "s");
		}
	}

	@SubCommand(baseCommand = "lives", name = "add", description = "Add lives to a player", rank = Rank.PLAT_ADMIN)
	public void addLivesCommand(CommandSender commandSender, @Param(name = "target", defaultTo = "self") FactionPlayer player, int lives) {
		this.plugin.getDeathbanManager().addLives(player.getUuid(), lives);
		commandSender.sendFormattedMessage("{0}You have given {2} {1}{3} live{4}{0}.", CC.PRIMARY, CC.SECONDARY, player.getName(), lives, lives == 1 ? "" : "s");
		this.plugin.getLogger().info(commandSender.getName() + " has given " + player.getName() + " " + lives + " lives");
	}
}
