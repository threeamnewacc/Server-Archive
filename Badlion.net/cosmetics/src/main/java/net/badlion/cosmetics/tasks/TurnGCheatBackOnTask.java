package net.badlion.cosmetics.tasks;

import net.badlion.cosmetics.managers.FlightGCheatManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TurnGCheatBackOnTask extends BukkitRunnable {

    private Player player;

    public TurnGCheatBackOnTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        this.player.setBypassGCheat(false);

        FlightGCheatManager.removeFromMapping(this.player);
    }

}
