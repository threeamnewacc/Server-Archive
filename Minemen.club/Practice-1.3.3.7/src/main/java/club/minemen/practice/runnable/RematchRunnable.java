package club.minemen.practice.runnable;

import club.minemen.practice.Practice;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.player.PlayerState;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class RematchRunnable implements Runnable {
	private final Practice plugin = Practice.getInstance();

	private final UUID playerUUID;

	@Override
	public void run() {
		Player player = this.plugin.getServer().getPlayer(this.playerUUID);
		if (player != null) {
			PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
			if (playerData != null) {
				if (playerData.getPlayerState() == PlayerState.SPAWN
						&& this.plugin.getMatchManager().isRematching(player.getUniqueId())
						&& this.plugin.getPartyManager().getParty(player.getUniqueId()) == null) {
					player.getInventory().setItem(3, null);
					player.getInventory().setItem(6, null);
					player.updateInventory();
					playerData.setRematchID(-1);
				}
			}
			this.plugin.getMatchManager().removeRematch(playerUUID);
		}
	}
}
