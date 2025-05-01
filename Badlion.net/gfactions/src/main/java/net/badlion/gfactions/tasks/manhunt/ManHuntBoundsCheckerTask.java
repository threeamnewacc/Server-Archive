package net.badlion.gfactions.tasks.manhunt;

import net.badlion.gfactions.GFactions;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class ManHuntBoundsCheckerTask extends BukkitRunnable {

    private GFactions plugin;

    private Map<String, Location> map = new HashMap<>();

    public ManHuntBoundsCheckerTask(GFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (this.plugin.getManHuntPP() == null) { // No more Man Hunt
            this.cancel();
            return;
        }

        if (this.plugin.getManHuntTagged() != null) { // Only check if someone is tagged
            // Check if they try to go outside of the warzone
            ProtectedRegion region = this.plugin.getgGuardPlugin().getProtectedRegion(this.plugin.getManHuntTagged().getLocation(), this.plugin.getgGuardPlugin().getProtectedRegions());
            if (region == null || region.getRegionName().equals("spawn")) {
                Location location = this.map.get(this.plugin.getManHuntTagged().getUniqueId().toString());
                if (location != null) {
                    this.plugin.getManHuntTagged().teleport(location);
                    this.plugin.getManHuntTagged().sendMessage(ChatColor.RED + "Cannot leave warzone when tagged for Man Hunt");
                } else {
                    // Edge case
                    this.plugin.getManHuntTagged().teleport(this.plugin.getManHuntPP().getLocation());
                    this.plugin.getManHuntTagged().sendMessage(ChatColor.RED + "You have to remain in the warzone, you have been teleported to the starting point");
                    this.map.put(this.plugin.getManHuntTagged().getUniqueId().toString(), this.plugin.getManHuntTagged().getLocation());
                }
            } else {
                // Update their location
                this.map.put(this.plugin.getManHuntTagged().getUniqueId().toString(), this.plugin.getManHuntTagged().getLocation());
            }
        }
    }
}
