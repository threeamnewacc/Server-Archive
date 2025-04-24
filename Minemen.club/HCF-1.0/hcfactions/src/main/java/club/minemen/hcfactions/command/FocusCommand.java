package club.minemen.hcfactions.command;

import club.minemen.core.util.cmd.CommandHandler;
import club.minemen.core.util.cmd.annotation.Param;
import club.minemen.core.util.cmd.annotation.commandTypes.Command;
import club.minemen.core.util.finalutil.CC;
import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Role;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * @since 12/31/2017
 */
@RequiredArgsConstructor
public class FocusCommand implements CommandHandler {

	private static final String defaultToString = "!@#$#@!%$#@%$@$#@$#@$#@$";
	private final HCFactions plugin;

	@Command(name = "focus")
	public void focusCommand(Player sender, @Param(name = "target", defaultTo = FocusCommand.defaultToString) String target) {
		FactionPlayer player = FPlayers.getInstance().get(sender);
		if (player.getRole().value < Role.MODERATOR.value) {
			sender.sendFormattedMessage("{0}You must be an {1} in your faction to focus.", CC.RED, Role.MODERATOR.nicename);
			return;
		}

		if (target.equals(FocusCommand.defaultToString)) {
			player.getFaction().setFocusedTarget(null);
			player.getFaction().getFPlayers().forEach(fPlayer -> fPlayer.updateFocus(null));
			player.getFaction().sendMessage(CC.SECONDARY + player.getName() + CC.PRIMARY + " has cleared the focus target.");
		} else {
			Player targetPlayer = this.plugin.getServer().getPlayer(target);
			if (targetPlayer == null) {
				sender.sendFormattedMessage("{0}{1} couldn''t be found!", CC.RED, target);
				return;
			}

			player.getFaction().setFocusedTarget(targetPlayer.getUniqueId());
			player.getFaction().getFPlayers().forEach(fPlayer -> fPlayer.updateFocus(null));
			player.getFaction().getFPlayers().forEach(fPlayer -> fPlayer.updateFocus(targetPlayer));

			player.getFaction().sendMessage(CC.SECONDARY + player.getName() + CC.PRIMARY + " has focused " + CC.SECONDARY + targetPlayer.getName() + CC.PRIMARY + ".");
		}
	}
}