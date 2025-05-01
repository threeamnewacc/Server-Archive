package net.badlion.cosmetics.gadgets;

import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderButtGadget extends Gadget {

    public Map<UUID, Long> lastEnderButtTimes = new HashMap<>();

    public EnderButtGadget() {
        super("ender_butt", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.EYE_OF_ENDER, ChatColor.GREEN + "Ender Butt", ChatColor.GRAY + "Ride an Ender Butt into the sunset!"));
    }

    @Override
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.ENDER_PEARL) {
            Player player = event.getPlayer();
            if (lastEnderButtTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastEnderButtTimes.get(player.getUniqueId()) <= 1000 * 5) {
                player.sendMessage(ChatColor.RED + "Please wait " + (5 - (Math.round(System.currentTimeMillis() - lastEnderButtTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
                event.setCancelled(true);
                giveGadget(player);
                return;
            }
            lastEnderButtTimes.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @Override
    public void giveGadget(Player player) {
        player.getInventory().setItem(5, ItemStackUtil.createItem(Material.ENDER_PEARL, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Ender Butt"));
    }
}
