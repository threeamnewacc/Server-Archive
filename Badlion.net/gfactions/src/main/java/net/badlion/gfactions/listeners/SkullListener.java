package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullListener implements Listener {

    private GFactions plugin;

    public SkullListener(GFactions plugin) {
        this.plugin = plugin;
    }

    public ItemStack getNamedSkull(String username) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        meta.setOwner(username);
        skull.setItemMeta(meta);

        return skull;
    }

    /*@EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        event.getDrops().add(this.getNamedSkull(player.getName()));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.SKULL) {

            Skull skull = (Skull) block.getState();
            if (skull.hasOwner()) {
                block.setType(Material.AIR);
                block.getWorld().dropItemNaturally(block.getLocation(), this.getNamedSkull(skull.getOwner()));
                event.setCancelled(true);
            }
        }
    }*/
}
