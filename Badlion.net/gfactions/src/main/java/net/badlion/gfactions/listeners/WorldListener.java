package net.badlion.gfactions.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Enderman;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class WorldListener implements Listener {

    private GFactions plugin;
    private ItemStack[] kit = new ItemStack[36];

    public WorldListener(GFactions plugin) {
        this.plugin = plugin;

        kit[0] = new ItemStack(Material.DIAMOND_SWORD);
        kit[0].addEnchantment(Enchantment.DAMAGE_ALL, 3);
        kit[0].addEnchantment(Enchantment.DURABILITY, 3);
        kit[0].addEnchantment(Enchantment.FIRE_ASPECT, 1);

        kit[1] = new ItemStack(Material.BOW);
        kit[1].addEnchantment(Enchantment.ARROW_DAMAGE, 4);
        kit[1].addEnchantment(Enchantment.ARROW_FIRE, 1);
        kit[1].addEnchantment(Enchantment.ARROW_INFINITE, 1);

        kit[2] = new ItemStack(Material.ENDER_PEARL, 16);
        kit[3] = new ItemStack(Material.COOKED_BEEF, 64);

        kit[4] = new ItemStack(Material.POTION, 1, (short) 16421);

        kit[5] = new ItemStack(Material.DIAMOND_HELMET);
        kit[5].addEnchantment(Enchantment.DURABILITY, 3);
        kit[5].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

        kit[6] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        kit[6].addEnchantment(Enchantment.DURABILITY, 3);
        kit[6].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

        kit[7] = new ItemStack(Material.DIAMOND_LEGGINGS);
        kit[7].addEnchantment(Enchantment.DURABILITY, 3);
        kit[7].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

        kit[8] = new ItemStack(Material.DIAMOND_BOOTS);
        kit[8].addEnchantment(Enchantment.DURABILITY, 3);
        kit[8].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        kit[8].addEnchantment(Enchantment.PROTECTION_FALL, 4);

        kit[9] = new ItemStack(Material.ARROW);
        kit[10] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[11] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[12] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[13] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[14] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[15] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[16] = new ItemStack(Material.POTION, 1, (short) 16388);
        kit[17] = new ItemStack(Material.POTION, 1, (short) 16426);

        kit[18] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[19] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[20] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[21] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[22] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[23] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[24] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[25] = new ItemStack(Material.POTION, 1, (short) 16388);
        kit[26] = new ItemStack(Material.POTION, 1, (short) 16426);

        kit[27] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[28] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[29] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[30] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[31] = new ItemStack(Material.POTION, 1, (short) 16421);
        kit[32] = new ItemStack(Material.POTION, 1, (short) 8258);
        kit[33] = new ItemStack(Material.POTION, 1, (short) 8226);
        kit[34] = new ItemStack(Material.POTION, 1, (short) 8226);
        kit[35] = new ItemStack(Material.POTION, 1, (short) 8259);
    }

    @EventHandler
    public void endermenGrief(EntityChangeBlockEvent e) { // Disable enderman griefing
        if (e.getEntity() instanceof Enderman) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void soilTrample(PlayerInteractEvent e) { // Disable soil trampling
        if (e.getAction().equals(Action.PHYSICAL) && e.getPlayer().getLocation().add(0, -1, 0).getBlock().getType().equals(Material.SOIL)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void soilTrample(EntityInteractEvent e) {
        if (e.getEntity().getLocation().add(0, -1, 0).getBlock().getType().equals(Material.SOIL)) { // Disable soil trampling
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void fireSpread(BlockIgniteEvent e) { // Disable fire spread
        if (e.getCause().equals(BlockIgniteEvent.IgniteCause.SPREAD)) {
            e.setCancelled(true);
        }
    }

    /*@EventHandler
    public void lavaFlow(BlockFromToEvent e) { // Disable lava flow
		if (e.getBlock().getWorld().getName().equals("world")) {
			if (e.getBlock().getType().equals(Material.STATIONARY_LAVA) || e.getBlock().getType().equals(Material.LAVA)) {
				e.setCancelled(true);
			}
		}
    }*/

	@EventHandler
	public void onBoatPlace(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getItem() != null && event.getItem().getType() == Material.BOAT) {
				if (event.getClickedBlock() != null) {
					Faction faction = Board.getFactionAt(event.getClickedBlock());
					FPlayer fPlayer = FPlayers.i.get(event.getPlayer());
					Faction myFaction = fPlayer.getFaction();

					if (!myFaction.getId().equals(faction.getId()) && !faction.getId().equals("0")) {
						event.setUseInteractedBlock(Event.Result.DENY);
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.RED + "Cannot use boats except for the wilderness and your faction base.");
					}
				}
			}
		}
	}

	@EventHandler
	public void onBowDamage(EntityDamageByEntityEvent event) {
		// A bit of extra bow damage
		if (event.getDamager() instanceof Arrow) {
			event.setDamage(event.getDamage() + 1);
		}
	}

	/*@EventHandler(priority=EventPriority.FIRST)
	public void onSilkSpawnerThing(EntityExplodeEvent event) {
		List<Block> blocks = event.blockList();
		List<Block> blocksToRemove = new ArrayList<>();
		for (Block block : blocks) {
			if (block.getType() == Material.MOB_SPAWNER) {
				// Find if someone was nearby
				boolean flag = false;
				List<Entity> entities = event.getEntity().getNearbyEntities(30, 30, 30);
				for (Entity entity : entities) {
					if (entity instanceof Player) {
						Player player = (Player) entity;
						if (player.hasPermission("silkspawners.silkdrop.*")) {
							flag = true;
							break;
						}
					}
				}

				// Found no one near by, fk it, screw u silkspawners
				if (!flag) {
					blocksToRemove.add(block);
				}
			}
		}

		for (Block block : blocksToRemove) {
			event.blockList().remove(block);
			block.setType(Material.AIR);
		}
	}*/

    /*@EventHandler(priority=EventPriority.LAST)
    public void onPlayerJoin(PlayerLoginEvent event) {
        if (!event.getPlayer().hasPermission("badlion.donator")) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage("Factions beta is closed to Donators only.");
        }
    }*/

    /*@EventHandler
    public void onPlayerXP(PlayerExpChangeEvent event) {
        if (event.getPlayer().getLevel() >= 50) {
            event.setAmount(0);
        } else {
            if (event.getAmount() < 50) {
                event.setAmount(event.getAmount() * 2);
            }
        }
    }*/

    @EventHandler
    public void onPlayerJoinServer(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            //DeathBanManager.addLives(event.getPlayer().getUniqueId(), 7);

            ItemStack itemStack = new ItemStack(Material.FISHING_ROD);
            itemStack.addEnchantment(Enchantment.LURE, 2);

            event.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS), itemStack);

            //event.getPlayer().getInventory().setContents(this.kit);
        }

        event.getPlayer().sendMessage(ChatColor.RED + "=====================================");
        event.getPlayer().sendMessage(ChatColor.AQUA + "Welcome to Badlion Hardcore Factions!");
        event.getPlayer().sendMessage(ChatColor.GREEN + "07/09/2015 " + ChatColor.GOLD + "Changelog:");
        event.getPlayer().sendMessage(ChatColor.GREEN + "- Archer kit damage buffed to 2.0x from 1.5x");
        event.getPlayer().sendMessage(ChatColor.GREEN + "- New KOTH: KOTH Church");
        event.getPlayer().sendMessage(ChatColor.GREEN + "- Set spawn points when entering End");
        event.getPlayer().sendMessage(ChatColor.GREEN + "- You leave an NPC if you log in combat");
	    event.getPlayer().sendMessage(ChatColor.RED + "=====================================");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Want to keep up to date on the events and community for Badlion Factions? Check out these links!");
        event.getPlayer().sendMessage(ChatColor.BLUE + "http://www.badlion.net/forum/category/27");
        event.getPlayer().sendMessage(ChatColor.GOLD + "http://www.badlion.net/factions/calendar");
        event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "http://www.badlion.net/factions/upcoming-matches");
        event.getPlayer().sendMessage(ChatColor.RED + "=====================================");
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        //event.getPlayer().getInventory().setContents(this.kit);
        ItemStack itemStack = new ItemStack(Material.FISHING_ROD);
        itemStack.addEnchantment(Enchantment.LURE, 2);

	    event.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS), itemStack);
    }

    @EventHandler
    public void onPlayerPlaceLavaBucket(PlayerInteractEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        if (event.getItem() != null && event.getItem().getType() == Material.LAVA_BUCKET) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Faction faction = Board.getFactionAt(event.getClickedBlock());
                if (faction.getId().equals("0") || faction.getId().equals("-1") || faction.getId().equals("-2")) {
                    event.getPlayer().sendMessage(ChatColor.RED + "No lava bucket placement allowed here.");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoinReduceKB(PlayerJoinEvent event) {
        // Smaller # = more KB
        // 0 = default vanilla behavior
        event.getPlayer().setKnockbackReduction(0.1F);
    }


}
