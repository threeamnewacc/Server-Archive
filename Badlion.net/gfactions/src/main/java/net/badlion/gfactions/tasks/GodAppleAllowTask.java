package net.badlion.gfactions.tasks;

import net.badlion.gfactions.GFactions;
import org.bukkit.scheduler.BukkitRunnable;

public class GodAppleAllowTask extends BukkitRunnable {

    private GFactions plugin;

    private String uuid;

    public GodAppleAllowTask(GFactions plugin, String uuid) {
        this.plugin = plugin;

        this.uuid = uuid;
    }

    @Override
    public void run() {
        if (this.plugin.getGodAppleBlacklist().containsKey(uuid)) {  //
            this.plugin.getGodAppleBlacklist().remove(uuid);
        }
    }

}
