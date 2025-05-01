package net.badlion.cosmetics.gadgets;

import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PeeGadget extends Gadget {

    public static Map<UUID, Long> peeingPlayers = new HashMap<>();
    private Map<UUID, Long> lastPeeTimes = new HashMap<>();

    public PeeGadget() {
        super("pee", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.GOLD_BLOCK, ChatColor.GREEN + "Pee", ChatColor.GRAY + "Busting? This gadget can", ChatColor.GRAY + "help you with that!"));
    }

    @Override
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.GOLD_BLOCK) {
            Player player = event.getPlayer();
            if (lastPeeTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastPeeTimes.get(player.getUniqueId()) <= 1000 * 10) {
                player.sendMessage(ChatColor.RED + "Please wait " + (10 - (Math.round(System.currentTimeMillis() - lastPeeTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
                return;
            }
            lastPeeTimes.put(player.getUniqueId(), System.currentTimeMillis());
            peeingPlayers.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @Override
    public void giveGadget(Player player) {
        player.getInventory().setItem(5, ItemStackUtil.createItem(Material.GOLD_BLOCK, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Pee"));
    }
}
