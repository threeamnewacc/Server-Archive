package net.badlion.cosmetics.gadgets;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WaterBombGadget extends Gadget {

    public Map<UUID, Long> lastWaterBombTimes = new HashMap<>();

    public WaterBombGadget() {
        super("water_bomb", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.POTION, ChatColor.GREEN + "Water Bomb", ChatColor.GRAY + "Splash your friends, enemies", ChatColor.GRAY + "or parents!"));
    }

    public static void explode(Item item) {
        for (int i = 0; i < 16; i++) {
            double x = -0.5F + (float) (Math.random() * 1.0D);
            double y = 1.0D;
            double z = -0.5F + (float) (Math.random() * 1.0D);
            FallingBlock fallingBlock = item.getWorld().spawnFallingBlock(item.getLocation(), Material.STATIONARY_WATER, (byte) 0);
            fallingBlock.setMetadata("fallfix", new FixedMetadataValue(Cosmetics.getInstance(), "fallfix"));
            fallingBlock.setDropItem(false);
            fallingBlock.setVelocity(new Vector(x, y, z));
        }
        item.remove();
    }

    @Override
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.POTION) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            player.updateInventory();
            if (lastWaterBombTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastWaterBombTimes.get(player.getUniqueId()) <= 1000 * 15) {
                player.sendMessage(ChatColor.RED + "Please wait " + (15 - (Math.round(System.currentTimeMillis() - lastWaterBombTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
                return;
            }
            final Item waterBomb = player.getWorld().dropItem(player.getLocation().add(player.getLocation().getDirection()), ItemStackUtil.createItem(Material.POTION, String.valueOf(Math.random())));
            waterBomb.setMetadata("takeable", new FixedMetadataValue(Cosmetics.getInstance(), "takeable"));
            waterBomb.setVelocity(player.getLocation().getDirection().multiply(1));

            Bukkit.getScheduler().runTaskLater(Cosmetics.getInstance(), new Runnable() {
                @Override
                public void run() {
                    WaterBombGadget.explode(waterBomb);
                }
            }, 20L * 3);
            lastWaterBombTimes.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @Override
    public void giveGadget(Player player) {
        player.getInventory().setItem(5, ItemStackUtil.createItem(Material.POTION, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Water Bomb"));
    }
}
