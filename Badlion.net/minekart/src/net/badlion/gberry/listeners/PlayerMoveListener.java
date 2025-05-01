package net.badlion.gberry.listeners;

import java.util.HashSet;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.tinywebteam.badlion.ItemBlock;
import com.tinywebteam.badlion.MineKart;
import com.tinywebteam.badlion.Racer;
import com.tinywebteam.badlion.tasks.SpawnItemBlock;
import com.tinywebteam.badlion.tasks.UnlockPlayerSlowTask;

public class PlayerMoveListener implements Listener {
	
	private HashSet<Block> enderCrystalLocations;
	private Map<Block, ItemBlock> locationToItemBlock;
	private MineKart server;
	private double maxSpeed;
	
	public PlayerMoveListener(MineKart server, HashSet<Block> enderCrystalLocations, Map<Block, ItemBlock>locationToItemBlock) {
		this.server = server;
		this.enderCrystalLocations = enderCrystalLocations;
		this.locationToItemBlock = locationToItemBlock;
		//this.maxSpeed = 0.2765293698170313;
		this.maxSpeed = 0.25;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player != null) {
			if (this.server.getPlayerToRacer().containsKey(player)) {
				Racer racer = this.server.getPlayerToRacer().get(player);
				// Acceleration stuff
				double currentSpeed = racer.getHorse().getVelocity().length();
				if (currentSpeed < maxSpeed) {
					// Speed them up a bit
					if (racer.getLastMoveTime() + 250 < System.currentTimeMillis()) {
						racer.setPreviousSpeed(0); // they stopped most likely
					}
					
					// If we don't have a lock, continue and do acceleration stuff
					//if (!racer.isLockSpeedChange()) {
						if (racer.getPreviousSpeed() < 0.08)
							racer.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3, 4));
						else if (racer.getPreviousSpeed() < 0.15) 
							racer.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 3));
						else if (racer.getPreviousSpeed() < 0.22)
							racer.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1, 2));
						else
							racer.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1, 1));
						
						racer.setPreviousSpeed(currentSpeed);
					//}
				}
				
				// Update time they moved
				racer.setLastMoveTime(System.currentTimeMillis());
				
				// Itemblock stuff
				Location location = player.getLocation();
				// Are they at the finish line?
				if (racer.getTrack().getFinishLineBlocks().contains(location.getBlock())) {
					if (racer.getAllowedToIncrementLap()) {
						racer.setCurrentLapNumber(racer.getCurrentLapNumber() + 1); // Increment race lap
						
						// Now gotta prevent them from cheating the system, only 1 time pass GO buddy
						// Doesn't get re-activated until they get to last checkpoint again
						racer.setAllowedToIncrementLap(false);
						
						// THEY CROSSED THE FINISH LINEEEEEEE
						if (racer.getCurrentLapNumber() > racer.getTrack().getNumOfLaps()) {
							racer.remove(true);
							// TODO: something else?
						}
					}
					return;
				}				
				if (location.getY() < 0) {
					// Fell off track?
					if (!this.server.getAllowToEject().contains(racer.getHorse())) {
						this.server.getAllowToEject().add(racer.getHorse());
					}
					// Gotta tp them back up
					racer.getHorse().eject();
					racer.getHorse().teleport(racer.getPreviousCheckPoint().getBlock().getLocation());
					racer.getHorse().setPassenger(racer.getPlayer());
					racer.getPlayer().sendMessage(ChatColor.RED + "You fell out of the world.  Placed back on track.");
					return;
				}
				location.setY(location.getY() - 1);
				Block blockUnder = location.getBlock();
				
				if (racer.getTrack().getSlowedBlocks().contains(blockUnder.getTypeId())) {
					// Slight off track, slow them
					racer.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 4, 4));
				} else if (!racer.getTrack().getAllowedBlocks().contains(blockUnder.getTypeId())) {
					// They are off track, put them back on
					if (!this.server.getAllowToEject().contains(racer.getHorse())) {
						this.server.getAllowToEject().add(racer.getHorse());
					}
					// Gotta tp them back to previous checkpoint
					racer.getHorse().eject();
					racer.getHorse().teleport(racer.getPreviousCheckPoint().getBlock().getLocation());
					racer.getHorse().setPassenger(racer.getPlayer());
					racer.getPlayer().sendMessage(ChatColor.RED + "You went off the track.  Can't move for 3 seconds.");
					racer.setLockSpeedChange(true);
					this.server.getServer().getScheduler().runTaskLater(this.server, new UnlockPlayerSlowTask(this.server, racer), 40);
					racer.getHorse().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 100), true); // high value to slow down very hard
				}
				
				/*if (enderCrystalLocations.contains(event.getTo().getBlock())) {
					ItemBlock itemBlock = this.locationToItemBlock.get(event.getTo().getBlock());
					itemBlock.getEnderCrystal().remove();
					enderCrystalLocations.remove(event.getTo().getBlock());
					this.server.getServer().getScheduler().runTaskLater(this.server, new SpawnItemBlock(this.server, itemBlock, enderCrystalLocations), 20 * 5); // 5s
					
					ItemStack [] items = player.getInventory().getContents();
					if (items[0] == null) {
						ItemStack item = ItemGenerator.getRandomItem(racer.getRace().getPlayerPositions().indexOf(racer));
						items[0] = item;
						player.getInventory().setContents(items);
					}
					
					return;
				}*/
			}
			
		}
	}
}
