package us.zonix.hcfactions.profile.ore;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.util.ItemBuilder;
import us.zonix.hcfactions.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.hcfactions.util.player.PlayerUtility;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.ItemBuilder;
import us.zonix.hcfactions.util.player.PlayerUtility;

import java.util.*;

public class ProfileOreListeners implements Listener {

    private static final HashSet<BlockFace> faces = new HashSet<>(Arrays.asList(BlockFace.values()));
    private static FactionsPlugin main = FactionsPlugin.getInstance();
    private List<Block> blocks = new ArrayList<>();
    private List<Block> placed = new ArrayList<>();

    public ProfileOreListeners() {
        faces.remove(BlockFace.SELF);
        new BukkitRunnable() {
            @Override
            public void run() {
                blocks.clear(); //clear blocks every 10 minutes
                placed.clear();
            }
        }.runTaskTimerAsynchronously(main, 20 * 600, 20 * 600);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getBlock().getType().name().contains("_ORE")) {
            placed.add(event.getBlock());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.DIAMOND_ORE && !blocks.contains(event.getBlock()) && !placed.contains(event.getBlock())) {
            int count = countDiamonds(event.getBlock());
            if (count > 0) {
                String message = main.getLanguageConfig().getString("ORES.FOUND_DIAMONDS" + (count == 1 ? "_SINGLE" : "_MULTI")).replace("%COUNT%", count + "").replace("%PLAYER%", event.getPlayer().getName());
                for (Player player : PlayerUtility.getOnlinePlayers() ) {
                    Profile profile = Profile.getByPlayer(player);
                    if (profile.getOptions().isViewFoundDiamondMessages()) {
                        player.sendMessage(message);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreakEventUpdate(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.QUARTZ_ORE && event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            ItemStack itemStack = event.getPlayer().getItemInHand();
            if (itemStack != null && !itemStack.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
                int toDrop = new Random().nextInt(3) + 1;

                if (itemStack.getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
                    for (int i = 0; i < itemStack.getEnchantments().get(Enchantment.LOOT_BONUS_BLOCKS); i++) {
                        toDrop += new Random().nextInt(1) + 1;
                    }
                }

                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemBuilder(Material.GLOWSTONE_DUST).amount(toDrop).build());
            }
        }

        if (block.getType().name().contains("_ORE") && block.getType() != Material.GLOWING_REDSTONE_ORE && !placed.contains(block)) {
            Profile profile = Profile.getByPlayer(event.getPlayer());
            ProfileOreType type = ProfileOreType.valueOf(block.getType().name().replace("_ORE", ""));
            profile.getOres().put(type, profile.getOres().get(type) + 1);
        }
    }

    private int countDiamonds(Block start) {
        int count = 0;
        if (start.getType() == Material.DIAMOND_ORE && !blocks.contains(start) && !placed.contains(start)) {
            blocks.add(start);
            count++;
        }

        for (BlockFace face : faces) {
            Block block = start.getRelative(face);
            if (block.getType() == Material.DIAMOND_ORE && !blocks.contains(block) && !placed.contains(block)) {
                count += countDiamonds(block);
            }
        }

        return count;
    }

}
