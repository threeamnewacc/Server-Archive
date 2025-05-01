package net.badlion.cosmetics.gadgets;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MelonBombGadget extends Gadget {

    public Map<UUID, Long> lastMelonTimes = new HashMap<>();

    public MelonBombGadget() {
        super("melon_bomb", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.SPECKLED_MELON, ChatColor.GREEN + "Melon Bomb", ChatColor.GRAY + "Boom, bang, bop,", ChatColor.GRAY + "melon, gone, pop!"));
    }

    public static void shootMelons(final Item melon, final Player pl) {
        new BukkitRunnable() {
            float f = 0.0F;
            int i = 0;
            ArrayList<Item> items = new ArrayList<>();

            public void run() {
                if (i < 150) {
                    Location loc = melon.getLocation().add(Math.sin(f * 0.115D) * 0.05D, 0.1D, Math.cos(f * 0.115D) * 0.05D);

                    ItemStack is = ItemStackUtil.createItem(Material.SPECKLED_MELON, Math.random() + "");
                    Item it = melon.getWorld().dropItem(loc, is);

                    Location temploc = new Location(pl.getWorld(), loc.getX(), melon.getLocation().getY(), loc.getZ());
                    it.setVelocity(temploc.toVector().subtract(melon.getLocation().add(0.0D, -0.05D, 0.0D).toVector()).multiply(5));
                    it.setMetadata("takeable", new FixedMetadataValue(Cosmetics.getInstance(), "takeable"));
                    items.add(it);

                    f += 1.0F;
                    if (f >= 360.0F) f = 0.0F;
                } else if (i == 175) {
                    melon.remove();
                    for (Item item : items) {
                        item.remove();
                    }
                    cancel();
                } else if (i == 150) {
                    for (Item item : items) {
                        item.setVelocity(new Vector(0.0D, 1.0D, 0.0D));
                    }
                }
                i++;
            }
        }.runTaskTimer(Cosmetics.getInstance(), 0L, 2L);
    }

    @Override
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.MELON_BLOCK) {
            final Player player = event.getPlayer();
            if (lastMelonTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastMelonTimes.get(player.getUniqueId()) <= 1000 * 60) {
                player.sendMessage(ChatColor.RED + "Please wait " + (60 - (Math.round(System.currentTimeMillis() - lastMelonTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
                event.setCancelled(true);
                return;
            }
            final Item melon = player.getWorld().dropItem(player.getLocation().add(player.getLocation().getDirection()), ItemStackUtil.createItem(Material.MELON_BLOCK, String.valueOf(Math.random() * 100.0D)));
            melon.setMetadata("takeable", new FixedMetadataValue(Cosmetics.getInstance(), "takeable"));
            melon.setVelocity(player.getLocation().getDirection().multiply(1));

            Bukkit.getScheduler().runTaskLater(Cosmetics.getInstance(), new Runnable() {
                @Override
                public void run() {
                    MelonBombGadget.shootMelons(melon, player);
                }
            }, 20L * 3);
            lastMelonTimes.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @Override
    public void giveGadget(Player player) {
        player.getInventory().setItem(5, ItemStackUtil.createItem(Material.MELON_BLOCK, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Melon Bomb"));
    }
}
