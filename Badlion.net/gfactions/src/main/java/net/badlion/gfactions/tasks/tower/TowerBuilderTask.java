package net.badlion.gfactions.tasks.tower;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.events.Tower;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class TowerBuilderTask extends BukkitRunnable {
	
	private GFactions plugin;
	private int noTower;
	private World world;
    private Chest chest;
	
	public TowerBuilderTask(GFactions plugin) {
		this.plugin = plugin;
		this.world = this.plugin.getServer().getWorld("world");
	}
	
	@Override
	public void run() {
		// Spawn tower
		Block block = this.plugin.getServer().getWorld("world").getBlockAt(this.plugin.getCheckTimeTask().getxTowerLocation(), this.plugin.getCheckTimeTask().getyTowerLocation(), this.plugin.getCheckTimeTask().getzTowerLocation());
		Tower tower = new Tower(this.plugin, 1, block.getLocation());
		this.plugin.setTower(tower);
		block = this.plugin.getServer().getWorld("world").getBlockAt(block.getX() + 9, block.getY() + 22, block.getZ() + 6);
		this.chest = (Chest)block.getState();
		tower.setChest(this.chest);

		// Pick 6 random items
        ArrayList<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            int r = this.plugin.generateRandomInt(1, 100);
            if (1 <= r && r < 10) {
                items.addAll(this.plugin.getItemGenerator().generateRandomSuperRareItem(1));
            } else if (10 <= r && r < 60) {
                items.addAll(this.plugin.getItemGenerator().generateRandomRareItem(1));
            } else if (60 <= r && r <= 100) {
                items.addAll(this.plugin.getItemGenerator().generateRandomCommonItem(1));
            }
        }

		for (ItemStack item : items) {
			this.chest.getBlockInventory().addItem(item);
		}

		// Set Tower Location
		this.plugin.getTower().setChestLocation(block.getLocation());
	}

}
