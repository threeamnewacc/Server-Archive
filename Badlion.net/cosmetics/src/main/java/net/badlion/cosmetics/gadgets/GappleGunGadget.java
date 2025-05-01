package net.badlion.cosmetics.gadgets;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.particles.Particle;
import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.*;

public class GappleGunGadget extends Gadget {

    public Map<UUID, Long> lastGappleGunTimes = new HashMap<>();
    private ParticleLibrary spell = new ParticleLibrary(ParticleLibrary.ParticleType.SPELL_MOB, 0, 1, 0);
    private ParticleLibrary flame = new ParticleLibrary(ParticleLibrary.ParticleType.FLAME, 0, 1, 0);

    public GappleGunGadget() {
        super("gapple_gun", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.GOLDEN_APPLE, (byte) 1, ChatColor.GREEN + "GApple Gun", ChatColor.GRAY + "Chug, chug, chug!"));
    }

    @Override
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.IRON_BARDING) {
            final Player player = event.getPlayer();
            if (this.lastGappleGunTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - this.lastGappleGunTimes.get(player.getUniqueId()) <= 1000 * 2) {
                player.sendMessage(ChatColor.RED + "Please wait " + (2 - (Math.round(System.currentTimeMillis() - this.lastGappleGunTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
                event.setCancelled(true);
                return;
            }
            this.lastGappleGunTimes.put(player.getUniqueId(), System.currentTimeMillis());
            final List<Item> items = new ArrayList<>();
            List<Location> locations = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                locations.add(Particle.getParticleLocation(player.getEyeLocation().add(player.getLocation().getDirection().multiply(0.2D)), 1.0D));
                double x = -0.5F + (float) (Math.random() * 1.0D);
                double z = -0.5F + (float) (Math.random() * 1.0D);

                Item gapple = player.getWorld().dropItem(player.getEyeLocation().add(player.getLocation().getDirection()), ItemStackUtil.createItem(Material.GOLDEN_APPLE, String.valueOf(Math.random() * 100.0D)));
                gapple.setMetadata("takeable", new FixedMetadataValue(Cosmetics.getInstance(), "takeable"));
                gapple.setVelocity(player.getLocation().getDirection().multiply(2).add(new Vector(x, 0.0D, z)));

                items.add(gapple);
            }
            this.flame.sendToLocation(player, locations);
            Bukkit.getScheduler().runTaskLater(Cosmetics.getInstance(), new Runnable() {
                @Override
                public void run() {
                    List<Location> locations = new ArrayList<>();
                    for (Item item : items) {
                        for (int i = 0; i < 5; i++) {
                            locations.add(Particle.getParticleLocation(item.getLocation(), 0.0D));
                        }
                        item.remove();
                    }
                    GappleGunGadget.this.spell.sendToLocation(player, locations);
                }
            }, 20L);
        }
    }

    @Override
    public void giveGadget(Player player) {
        player.getInventory().setItem(5, ItemStackUtil.createItem(Material.IRON_BARDING, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "GApple Gun"));
    }
}
