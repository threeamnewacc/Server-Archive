package net.badlion.cosmetics.gadgets;

import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderPearlGadget extends Gadget {

    public Map<UUID, Long> lastEnderPearlTimes = new HashMap<>();

    public EnderPearlGadget() {
        super("ender_pearl", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.ENDER_PEARL, ChatColor.GREEN + "Ender Pearl", ChatColor.GRAY + "World's first FREE teleportation device!"));
    }

    @Override
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.ENDER_PEARL) {
            Player player = event.getPlayer();
            if (lastEnderPearlTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastEnderPearlTimes.get(player.getUniqueId()) <= 1000 * 5) {
                player.sendMessage(ChatColor.RED + "Please wait " + (5 - (Math.round(System.currentTimeMillis() - lastEnderPearlTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
                event.setCancelled(true);
                player.updateInventory();
                return;
            }
            lastEnderPearlTimes.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @Override
    public void giveGadget(Player player) {
        player.getInventory().setItem(5, ItemStackUtil.createItem(Material.ENDER_PEARL, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Ender Pearl"));
    }
}
