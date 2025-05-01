package net.badlion.gfactions.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.PlayerRunnable;
import net.badlion.gfactions.GFactions;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LimitEXPTask extends BukkitRunnable {

    @Override
    public void run() {
        Gberry.distributeTask(GFactions.plugin, new PlayerRunnable() {
            @Override
            public void run(Player player) {
                if (player.getLevel() >= 50 && player.getExp() > 0) {
                    player.setLevel(50);
                    player.setExp(0);
                }
            }
        });
    }

}
