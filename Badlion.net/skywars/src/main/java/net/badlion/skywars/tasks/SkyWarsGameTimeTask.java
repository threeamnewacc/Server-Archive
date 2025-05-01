package net.badlion.skywars.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPG;
import net.badlion.mpg.tasks.GameTimeTask;
import net.badlion.skywars.SkyWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;

public class SkyWarsGameTimeTask extends GameTimeTask {

    public static int CHEST_REFILL_TIME = 60 * 3; // 3 min
    public static int WORLD_DESTRUCTION_TIME = 60 * 5; // 5 min
    public static int DEATHMATCH_TIME = 60 * 7; // 7 min

    public static SkyWarsGameTimeTask getInstance() {
        return (SkyWarsGameTimeTask) SkyWarsGameTimeTask.instance;
    }

    public void run() {
        // Call scoreboard stuff
        super.run();

        if (this.getTotalSeconds() == SkyWarsGameTimeTask.CHEST_REFILL_TIME) {
            Bukkit.broadcastMessage(MPG.MPG_PREFIX + ChatColor.AQUA + "The chests have been refilled!");
            SkyWars.getInstance().getCurrentGame().fillChests();

            // Fill Chests
            for (Inventory inventory : SkyWars.getInstance().getCurrentGame().getTier1Chests()) {
                SkyWars.getInstance().getCurrentGame().fillChest(inventory, 1);
            }

            for (Inventory inventory : SkyWars.getInstance().getCurrentGame().getTier2Chests()) {
                SkyWars.getInstance().getCurrentGame().fillChest(inventory, 2);
            }
        } else if (this.getTotalSeconds() == SkyWarsGameTimeTask.WORLD_DESTRUCTION_TIME) {
            SkyWars.getInstance().getCurrentGame().getWorld().destroyIslands();
        } else if (this.getTotalSeconds() == SkyWarsGameTimeTask.WORLD_DESTRUCTION_TIME - 20) {
            Bukkit.broadcastMessage(MPG.MPG_PREFIX + ChatColor.RED + "Spawn Islands Destruction in 20 Seconds!");
            Gberry.broadcastSound(Sound.WITHER_SPAWN , 1, 1);
        }
    }

    public boolean isPastDestructionTime() {
        return this.getTotalSeconds() > SkyWarsGameTimeTask.WORLD_DESTRUCTION_TIME;
    }

    public boolean isPastChestRefillTime() {
        return this.getTotalSeconds() > SkyWarsGameTimeTask.CHEST_REFILL_TIME;
    }

    public String getTimeTillChestRefill() {
        int seconds = SkyWarsGameTimeTask.CHEST_REFILL_TIME - this.getTotalSeconds();
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return this.niceTime(-1, minutes, seconds);
    }

    public String getDestructionTime() {
        int seconds = SkyWarsGameTimeTask.WORLD_DESTRUCTION_TIME - this.getTotalSeconds();
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return this.niceTime(-1, minutes, seconds);
    }

    public String getDeathmatchTime() {
        int seconds = SkyWarsGameTimeTask.DEATHMATCH_TIME - this.getTotalSeconds();
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return this.niceTime(-1, minutes, seconds);
    }

}
