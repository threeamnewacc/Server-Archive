package net.badlion.uhc.listeners.gamemodes;


import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.listeners.MiniStatsListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CutCleanGameMode implements GameMode {

	private Random random = new Random();

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.IRON_INGOT);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "CutClean");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- Ores are pre-smelted");
        lore.add(ChatColor.AQUA + "- Food is pre-cooked");
        lore.add(ChatColor.AQUA + "- Flint/Leather/Feathers drop rates are 100%");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "http://reddit.com/u/KatManKhaos";
    }

    @EventHandler
    public void onEntityDie(EntityDeathEvent event) {
        if (event.getEntity() instanceof Cow) {
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Material.COOKED_BEEF, 3));
            event.getDrops().add(new ItemStack(Material.LEATHER));
        } else if (event.getEntity() instanceof Chicken) {
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Material.COOKED_CHICKEN, 3));
            event.getDrops().add(new ItemStack(Material.FEATHER));
        } else if (event.getEntity() instanceof Pig) {
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Material.GRILLED_PORK, 3));
        } else if (event.getEntity() instanceof Horse) {
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Material.LEATHER));
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onBlockMined(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
	    World world = block.getLocation().getWorld();
	    if (world.getName().equals(BadlionUHC.UHCWORLD_NAME) || world.getEnvironment() == World.Environment.NETHER) {
            // Make sure they have a valid pickaxe
            if (block.getType() == Material.GOLD_ORE && (player.getItemInHand() != null &&
                             (player.getItemInHand().getType() == Material.DIAMOND_PICKAXE || player.getItemInHand().getType() == Material.IRON_PICKAXE))) {
                event.setCancelled(true);

                // Stat Tracking (do before we set to AIR)
                if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STATS.name()).getValue()) {
                    MiniStatsListener.handleBlockBreak(event);
                }

	            block.setType(Material.AIR);
                block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.GOLD_INGOT));
                BadlionUHC.getInstance().createExpOrb(block.getLocation(), 1);
                if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType().getMaxDurability() > 0) {
                    short dur = event.getPlayer().getItemInHand().getDurability();
                    if (++dur >= event.getPlayer().getItemInHand().getType().getMaxDurability()) {
                        player.setItemInHand(null);
                        player.updateInventory();
                    } else {
                        player.getItemInHand().setDurability(dur);
                    }
                }
            } else if (block.getType() == Material.IRON_ORE) {
                event.setCancelled(true);

                // Stat Tracking (do before we set to AIR)
                if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STATS.name()).getValue()) {
                    MiniStatsListener.handleBlockBreak(event);
                }

	            block.setType(Material.AIR);
                block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.IRON_INGOT));
                BadlionUHC.getInstance().createExpOrb(block.getLocation(), 1);
                if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType().getMaxDurability() > 0) {
                    short dur = event.getPlayer().getItemInHand().getDurability();
                    if (++dur >= event.getPlayer().getItemInHand().getType().getMaxDurability()) {
                        player.setItemInHand(null);
                        player.updateInventory();
                    } else {
                        player.getItemInHand().setDurability(dur);
                    }
                }
            } else if (block.getType() == Material.GRAVEL) {
                event.setCancelled(true);

                block.setType(Material.AIR);
                block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.FLINT));
                if (player.getItemInHand() != null && player.getItemInHand().getType().getMaxDurability() > 0) {
                    short dur = player.getItemInHand().getDurability();
                    if (++dur >= player.getItemInHand().getType().getMaxDurability()) {
                        player.setItemInHand(null);
                        player.updateInventory();
                    } else {
                        player.getItemInHand().setDurability(dur);
                    }
                }
            } else if (event.getBlock().getType() == Material.LEAVES && event.getBlock().getData() % 4 == 0) {
	            if (this.random.nextInt(200) == 0) {
		            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
	            }
            } else if (event.getBlock().getType() == Material.LEAVES_2 && event.getBlock().getData() % 4 == 1) {
	            if (this.random.nextInt(200) == 0) {
		            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
	            }
            }
        }
    }

	@EventHandler
	public void onLeafDecay(LeavesDecayEvent event) {
		if (event.getBlock().getType() == Material.LEAVES && event.getBlock().getData() % 4 == 0) {
			if (this.random.nextInt(200) == 0) {
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
			}
		} else if (event.getBlock().getType() == Material.LEAVES_2 && event.getBlock().getData() % 4 == 1) {
			if (this.random.nextInt(200) == 0) {
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
			}
		}
	}

    @Override
    public void unregister() {
        EntityDeathEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
        LeavesDecayEvent.getHandlerList().unregister(this);
    }

}
