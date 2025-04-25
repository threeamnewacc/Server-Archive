package us.zonix.practice.runnable;

import java.util.UUID;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;

public class RematchRunnable implements Runnable {
    private final Practice plugin = Practice.getInstance();
    private final UUID playerUUID;

    @Override
    public void run() {
        Player player = this.plugin.getServer().getPlayer(this.playerUUID);
        if (player != null) {
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            if (playerData != null
                && playerData.getPlayerState() == PlayerState.SPAWN
                && this.plugin.getMatchManager().isRematching(player.getUniqueId())
                && this.plugin.getPartyManager().getParty(player.getUniqueId()) == null) {
                player.getInventory().setItem(3, null);
                player.getInventory().setItem(5, null);
                player.updateInventory();
                playerData.setRematchID(-1);
            }

            this.plugin.getMatchManager().removeRematch(this.playerUUID);
        }
    }

    public RematchRunnable(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }
}
