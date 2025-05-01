package com.tinywebteam.badlion;

import java.util.ArrayList;
import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import com.tinywebteam.badlion.tasks.AttachToHorseTask;
import com.tinywebteam.badlion.tasks.GoTask;
import com.tinywebteam.badlion.tasks.OneTask;
import com.tinywebteam.badlion.tasks.TwoTask;

public class Race {
	
	private int numOfLaps;
	private ArrayList<Racer> racers;
	private Track track;
	private LinkedList<Racer> playerPositions;
	private ArrayList<Racer> racersStillInRace;
	private MineKart plugin;
	private int offset = 0;
	private ArrayList<Block> removeBlocksFromTrack;
	private BukkitTask oneMinuteTask;
	private ArrayList<Item> itemsOnTrack;
	
	public Race(MineKart plugin, ArrayList<Player> players, Track track) {
		this.plugin = plugin;
		this.racers = new ArrayList<Racer>();
		this.track = track;
		this.playerPositions = new LinkedList<Racer>();
		this.removeBlocksFromTrack = new ArrayList<Block>();
		this.itemsOnTrack = new ArrayList<Item>();
		this.racersStillInRace = new ArrayList<Racer>();
		
		// Handle all of the player stuff now
		for (int i = 0; i < players.size(); ++i) {
			// New racer
			Racer racer = new Racer(players.get(i), this.track);
			
			// Add them to racers and intialize 1-8 positions
			this.racers.add(racer);
			this.playerPositions.add(racer);
			this.racersStillInRace.add(racer);
			racer.setRace(this);
			
			// Add to map at top level to detect dc's and garbage.
			this.plugin.getPlayerToRacer().put(players.get(i), racer);
		}
		
		// set gamemode
		for (int i = 0; i < players.size(); ++i) {
			players.get(i).setGameMode(GameMode.SURVIVAL);
		}
		
		// Spawn them horses
		for (int i = 0; i < players.size(); ++i) {
			Horse horse = (Horse) this.plugin.getServer().getWorld("world").spawnEntity(this.track.getSpawnLocations().get(i), EntityType.HORSE);
			players.get(i).teleport(this.track.getSpawnLocations().get(i));
			this.racers.get(i).setHorse(horse);
			HorseInventory inventory = horse.getInventory();
			inventory.setSaddle(new ItemStack(Material.SADDLE, 1));
			horse.setOwner(this.racers.get(i).getPlayer());
			this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new AttachToHorseTask(this.racers.get(i)), 1);
			
			// Nice 3, 2, 1, Go! message
			horse.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 100), true); // wait 3s
			players.get(i).sendMessage(ChatColor.GREEN + "Race starts in");
			players.get(i).sendMessage(ChatColor.GREEN + "3");
			players.get(i).playSound(players.get(i).getLocation(), Sound.ARROW_HIT, 1, 1);
		}
		// Nice 3, 2, 1, Go! message
		this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, new TwoTask(this.plugin, players), 20);
		this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, new OneTask(this.plugin, players), 40);
		this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, new GoTask(this.plugin, players), 60);
	}
	
	public void startCountDown() {
		
	}

	public Track getTrack() {
		return track;
	}

	public void setTrack(Track track) {
		this.track = track;
	}

	public int getNumOfLaps() {
		return numOfLaps;
	}

	public void setNumOfLaps(int numOfLaps) {
		this.numOfLaps = numOfLaps;
	}

	public ArrayList<Racer> getRacers() {
		return racers;
	}

	public void setRacers(ArrayList<Racer> racers) {
		this.racers = racers;
	}

	public LinkedList<Racer> getPlayerPositions() {
		return playerPositions;
	}

	public void setPlayerPositions(LinkedList<Racer> playerPositions) {
		this.playerPositions = playerPositions;
	}

	public MineKart getPlugin() {
		return plugin;
	}

	public void setPlugin(MineKart plugin) {
		this.plugin = plugin;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public ArrayList<Block> getRemoveBlocksFromTrack() {
		return removeBlocksFromTrack;
	}

	public void setRemoveBlocksFromTrack(ArrayList<Block> removeBlocksFromTrack) {
		this.removeBlocksFromTrack = removeBlocksFromTrack;
	}

	public BukkitTask getOneMinuteTask() {
		return oneMinuteTask;
	}

	public void setOneMinuteTask(BukkitTask oneMinuteTask) {
		this.oneMinuteTask = oneMinuteTask;
	}

	public ArrayList<Item> getItemsOnTrack() {
		return itemsOnTrack;
	}

	public void setItemsOnTrack(ArrayList<Item> itemsOnTrack) {
		this.itemsOnTrack = itemsOnTrack;
	}

	public ArrayList<Racer> getRacersStillInRace() {
		return racersStillInRace;
	}

	public void setRacersStillInRace(ArrayList<Racer> racersStillInRace) {
		this.racersStillInRace = racersStillInRace;
	}

}
