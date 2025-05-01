package net.badlion.cosmetics.gadgets;

import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GemShowerGadget extends Gadget {

    public static Map<UUID, Long> playerGemShowerTimes = new HashMap<>();
    public Map<UUID, Long> lastGemShowerTimes = new HashMap<>();

    public GemShowerGadget() {
        super("gem_shower", ItemRarity.RARE, ItemStackUtil.createItem(Material.EMERALD, ChatColor.GREEN + "Gem Shower", ChatColor.GRAY + "GEMS EVERYWHERE!!!"));
    }

    @Override
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.EMERALD) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            if (lastGemShowerTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastGemShowerTimes.get(player.getUniqueId()) <= 1000 * 20) {
                player.sendMessage(ChatColor.RED + "Please wait " + (20 - (Math.round(System.currentTimeMillis() - lastGemShowerTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
                return;
            }
            lastGemShowerTimes.put(player.getUniqueId(), System.currentTimeMillis());
            playerGemShowerTimes.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @Override
    public void giveGadget(Player player) {
        player.getInventory().setItem(5, ItemStackUtil.createItem(Material.EMERALD, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Gem Shower"));
    }
}
