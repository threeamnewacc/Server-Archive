package net.badlion.cosmetics.gadgets;

import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FireworkGadget extends Gadget {

    public Map<UUID, Long> lastFireworkTimes = new HashMap<>();

    public FireworkGadget() {
        super("firework", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.FIREWORK, ChatColor.GREEN + "Firework", ChatColor.GRAY + "'Coz baby you're a firework!"));
    }

    @Override
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.FIREWORK) {
            Player player = event.getPlayer();
            if (lastFireworkTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastFireworkTimes.get(player.getUniqueId()) <= 1000 * 5) {
                player.sendMessage(ChatColor.RED + "Please wait " + (5 - (Math.round(System.currentTimeMillis() - lastFireworkTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
                event.setCancelled(true);
                return;
            }
            giveGadget(player);
            lastFireworkTimes.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @Override
    public void giveGadget(Player player) {
        player.getInventory().setItem(5, ItemStackUtil.createItem(Material.FIREWORK, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Firework"));
    }
}
