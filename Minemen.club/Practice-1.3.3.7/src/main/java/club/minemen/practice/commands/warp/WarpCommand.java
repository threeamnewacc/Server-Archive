package club.minemen.practice.commands.warp;

import club.minemen.core.command.BaseCommand;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.player.PlayerState;
import java.util.Collections;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand extends BaseCommand {

	private final Practice plugin = Practice.getInstance();

	public WarpCommand() {
		super("spawn");

		this.setPlayerOnly(true);
		this.setAliases(Collections.singletonList("ffa"));
	}

	@Override
	public boolean onExecute(CommandSender commandSender, String label, String[] args) {
		Player player = (Player) commandSender;

		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		if (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.FFA) {
			player.sendMessage(CC.RED + "You can't do this in your current state.");
			return true;
		}

		switch (label.toLowerCase()) {
			case "spawn":
				this.plugin.getPlayerManager().sendToSpawnAndReset(player);
				break;
			case "ffa":
				//this.plugin.getFfaManager().addPlayer(player);
				break;
		}
		return true;
	}

}
