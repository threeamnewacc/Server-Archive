package net.badlion.gfactions.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.FakeEntitySpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class MobSpawnerListener implements Listener {

	private GFactions plugin;
    private Random random;

	public MobSpawnerListener(GFactions plugin) {
		this.plugin = plugin;
        this.random = new Random();
	}

	@EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
	public void onMobSpawnerPlaced(final BlockPlaceEvent event) {
		final ItemStack item = event.getItemInHand();
		if (item == null) {
			return;
		}

		// 1 tick later
		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
			@Override
			public void run() {
				if (item.getType().equals(Material.MOB_SPAWNER))
				{
					String sType;
					if (item.getItemMeta().getLore() == null) {
						sType = "Pig Spawner";
					} else {
						sType = item.getItemMeta().getLore().get(0);
					}

					if (item.getItemMeta() != null && item.getItemMeta().getDisplayName() == null) {
						Block setBlock = event.getBlock();
						setBlock.setType(Material.MOB_SPAWNER);
						final CreatureSpawner s = (CreatureSpawner)setBlock.getState();
						s.setSpawnedType(EntityType.valueOf(sType.split(" ")[0].toUpperCase()));
					}
				}
			}
		}, 1);

	}

    @EventHandler
    public void onFakeSpawn(FakeEntitySpawnEvent event) {
        if (event.getMobType().equals("Skeleton")) {
            // EXP
            this.handleXPInsert(5, event.getPlayers());

            // Items
            this.handleItemInsert(2, Material.BONE, event.getPlayers());
            this.handleItemInsert(2, Material.ARROW, event.getPlayers());
        } else if (event.getMobType().equals("Spider") || event.getMobType().equals("CaveSpider")) {
            // EXP
            this.handleXPInsert(5, event.getPlayers());

            // Items
            this.handleItemInsert(1, Material.STRING, event.getPlayers());
            this.handleItemInsert(1, Material.SPIDER_EYE, event.getPlayers());
        } else if (event.getMobType().equals("Zombie")) {
            // EXP
            this.handleXPInsert(5, event.getPlayers());

            // Items
            this.handleItemInsert(2, Material.ROTTEN_FLESH, event.getPlayers());
	        this.handleItemInsert(2, Material.POTATO_ITEM, event.getPlayers(), 1);
	        this.handleItemInsert(2, Material.CARROT_ITEM, event.getPlayers(), 1);
        } else if (event.getMobType().equals("Blaze")) {
            // EXP
            this.handleXPInsert(10, event.getPlayers());

            // Items
            this.handleItemInsert(1, Material.BLAZE_ROD, event.getPlayers());
        } else if (event.getMobType().equals("Creeper")) {
            // EXP
            this.handleXPInsert(5, event.getPlayers());

            // Items
            this.handleItemInsert(2, Material.SULPHUR, event.getPlayers());
        }
    }

	public boolean handleItemInsert(int maxDrop, Material material, List<Player> players, int chance) {
		if (Gberry.generateRandomInt(1, 100) <= chance) {
			this.handleItemInsert(maxDrop, material, players);
			return true;
		}

		return false;
	}

    public void handleItemInsert(int maxDrop, Material material, List<Player> players) {
        if (players.size() == 0) {
            return;
        }

        int amt = this.random.nextInt(maxDrop + 1);
        if (amt == 0) {
            return;
        }

        players.get(0).getInventory().addItem(new ItemStack(material, amt));

        // Play sound
        for (Player player : players) {
            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1F, 1F);
        }
    }

    public void handleXPInsert(int flatXP, List<Player> players) {
        if (players.size() == 0) {
            return;
        }

        for (Player player : players) {
            if (player.getLevel() < 50) {
                player.giveExp(flatXP / players.size() + 1);

                // Play sound
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
            }
        }
    }

}
