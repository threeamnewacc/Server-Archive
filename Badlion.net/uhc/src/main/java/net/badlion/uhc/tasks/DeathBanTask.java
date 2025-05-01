package net.badlion.uhc.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathBanTask extends BukkitRunnable {

    private Player player;

    public DeathBanTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        this.player.kickPlayer("You have died!");
    }

}
