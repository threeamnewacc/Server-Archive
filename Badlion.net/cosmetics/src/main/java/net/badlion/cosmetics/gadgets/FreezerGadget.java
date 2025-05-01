package net.badlion.cosmetics.gadgets;

import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FreezerGadget extends Gadget {

    public static Map<UUID, Location> frozenPlayerLocations = new HashMap<>();
    public static Map<UUID, Long> frozenPlayerTimes = new HashMap<>();
    public static Map<UUID, Long> creatingFreezerTimes = new HashMap<>();
    private Map<UUID, Long> lastFreezeTimes = new HashMap<>();

    public FreezerGadget() {
        super("freezer", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.ICE, ChatColor.GREEN + "Freezer", ChatColor.GRAY + "Let it go, let it go!"));
    }

    @Override
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.ICE) {
            Player player = event.getPlayer();
            if (lastFreezeTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastFreezeTimes.get(player.getUniqueId()) <= 1000 * 20) {
                player.sendMessage(ChatColor.RED + "Please wait " + (20 - (Math.round(System.currentTimeMillis() - lastFreezeTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
                return;
            }

            if (!player.isOnGround()) {
                player.sendMessage(ChatColor.RED + "You must be on the ground to use this.");
                return;
            }

            lastFreezeTimes.put(player.getUniqueId(), System.currentTimeMillis());
            creatingFreezerTimes.put(player.getUniqueId(), System.currentTimeMillis());
            Location location = new Location(player.getWorld(), player.getLocation().getBlock().getLocation().getX(), player.getLocation().getBlock().getLocation().getY(), player.getLocation().getBlock().getLocation().getZ()).clone().add(0.5D, 0.0D, 0.5D);
            frozenPlayerLocations.put(player.getUniqueId(), location);
        }
    }

    @Override
    public void giveGadget(Player player) {
        player.getInventory().setItem(5, ItemStackUtil.createItem(Material.ICE, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Freezer"));
    }
}
