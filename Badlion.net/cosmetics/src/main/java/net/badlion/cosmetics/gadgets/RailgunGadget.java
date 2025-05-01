package net.badlion.cosmetics.gadgets;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class RailgunGadget extends Gadget {

    public Map<UUID, Long> lastRailgunTimes = new HashMap<>();
    private ParticleLibrary particleLibrary = new ParticleLibrary(ParticleLibrary.ParticleType.FIREWORKS_SPARK, 0, 1, 0);

    public RailgunGadget() {
        super("railgun", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.STICK, ChatColor.GREEN + "Railgun", ChatColor.GRAY + "Futuristic gun to shoot your", ChatColor.GRAY + "friends with!"));
    }

    @Override
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.STICK) {
            Player player = event.getPlayer();
            if (this.lastRailgunTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - this.lastRailgunTimes.get(player.getUniqueId()) <= 1000 * 5) {
                player.sendMessage(ChatColor.RED + "Please wait " + (5 - (Math.round(System.currentTimeMillis() - this.lastRailgunTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
                return;
            }

            List<Location> locations = new ArrayList<>();

            for (double d = 0; d < 20; d += 0.4) {
                locations.add(player.getEyeLocation().add(player.getLocation().getDirection().multiply(d)));
            }

            this.particleLibrary.sendToLocation(player, locations);

            this.lastRailgunTimes.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @Override
    public void giveGadget(Player player) {
        player.getInventory().setItem(5, ItemStackUtil.createItem(Material.STICK, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Railgun"));
    }
}
