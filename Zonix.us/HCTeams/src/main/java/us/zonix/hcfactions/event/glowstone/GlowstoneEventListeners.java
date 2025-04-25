package us.zonix.hcfactions.event.glowstone;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.event.EventManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.event.EventManager;

public class GlowstoneEventListeners implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(BlockBreakEvent event) {

        GlowstoneEvent glowstoneEvent = EventManager.getInstance().getGlowstoneEvent(event.getBlock().getLocation());

        if (glowstoneEvent != null) {

            int there =0;
            int gone = 0;

            for(Block block : glowstoneEvent.getCuboid()){
                if(block.getType().equals(Material.GLOWSTONE)){
                    there++;
                }else{
                    gone++;
                }
            }

            if(event.getBlock().getType().equals(Material.GLOWSTONE)){
                there--;
                gone++;
            }


            if(there == gone){
                Bukkit.broadcastMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("GLOWSTONE.PERCENTAGE_MINED").replace("%GLOWSTONE%", glowstoneEvent.getName()).replace("%PERCENTAGE%", "50%"));
            }

            if(gone > there) {
                int total = there + gone;
                int fifty = total / 2;
                int twenty = fifty / 2;
                if (there == twenty && gone == (fifty+twenty)) {
                    Bukkit.broadcastMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("GLOWSTONE.PERCENTAGE_MINED").replace("%GLOWSTONE%", glowstoneEvent.getName()).replace("%PERCENTAGE%", "25%"));
                }
            }

            if(there > gone) {
                int total = there + gone;
                int fifty = total / 2;
                int twenty = fifty / 2;
                if (gone == twenty && there == (fifty + twenty)) {
                    Bukkit.broadcastMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("GLOWSTONE.PERCENTAGE_MINED").replace("%GLOWSTONE%", glowstoneEvent.getName()).replace("%PERCENTAGE%", "75%"));
                }
            }

            if(there == 0 && event.getBlock().getType() == Material.GLOWSTONE){
                Bukkit.broadcastMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("GLOWSTONE.FULLY_MINED").replace("%GLOWSTONE%", glowstoneEvent.getName()));
            }
        }
    }

}
