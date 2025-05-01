package net.badlion.kitpvp.tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.tinywebteam.badlion.ItemBlock;
import com.tinywebteam.badlion.MineKart;
import com.tinywebteam.badlion.Race;
import com.tinywebteam.badlion.Racer;
import com.tinywebteam.badlion.Track;
import com.tinywebteam.badlion.items.ItemGenerator;

public class ItemBlockTracker extends BukkitRunnable {
	
	private MineKart plugin;
	private ArrayList<Race> races;
	
	public ItemBlockTracker(MineKart plugin, ArrayList<Race> races) {
		this.plugin = plugin;
		this.races = races;
	}
	
	@Override
	public void run() {
		// Go through each racer and see if they hit a checkpoint or not
		for (Race race : this.races) {
			Track track = race.getTrack();
			for (ItemBlock itemBlock : track.getItemBlocks()) {
				// Continue if no itemblock is spawned here
				if (!itemBlock.isItemAvailable()) {
					continue;
				}
				
				List<Entity> entities = itemBlock.getEnderCrystal().getNearbyEntities(0.15, 1, 0.15);
				if (entities == null || entities.size() == 0)  {
					continue;
				}
				
				// Remove non-players
				ArrayList<Player> players = new ArrayList<Player>();
				for (Entity entity : entities) {
					if (entity instanceof Player) {
						players.add((Player) entity);
					}
				}
				
				if (players.size() == 0) {
					continue;
				}
				
				// Remove any players that arent allowed to pickup an item for 5 seconds
				ArrayList<Player> validPlayers = new ArrayList<Player>();
				Racer racer = null;
				for (Player player : players) {
					racer = this.plugin.getPlayerToRacer().get(player);
					if (racer != null) {
						if (racer.isAllowedToPickupItem()) {
							validPlayers.add(player);
						}
					}
				}
				players = validPlayers;
				
				if (players.size() == 0) {
					continue; // no more valid players left
				}
				
				// Find closest player
				Location location = itemBlock.getEnderCrystal().getLocation();
				double shortestDistance = location.distance(players.get(0).getLocation());
				Player closestPlayer = players.get(0);
				
				for (int i = 1; i < players.size(); ++i) {
					Location playerLocation = players.get(i).getLocation();
					double distance = playerLocation.distance(location);
					
					// Found new closest player
					if (distance < shortestDistance) {
						closestPlayer = players.get(i);
						shortestDistance = distance;
					}
				}
				
				// Give closest player itemblock
				itemBlock.getEnderCrystal().remove();
				itemBlock.setItemAvailable(false);
				this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new SpawnItemBlock(this.plugin, itemBlock/*, enderCrystalLocations*/), 20 * 3); // 3s
				racer = this.plugin.getPlayerToRacer().get(closestPlayer);
				racer.setAllowedToPickupItem(false);
				this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new AllowItemPickupTask(racer), 20 * 5); // 5s
				
				ItemStack [] items = closestPlayer.getInventory().getContents();
				if (items[0] == null) {
					ItemStack item = ItemGenerator.getRandomItem(racer.getRace().getPlayerPositions().indexOf(racer));
					items[0] = item;
					closestPlayer.getInventory().setContents(items);
					closestPlayer.updateInventory();
				}
			}
		}
	}
}
