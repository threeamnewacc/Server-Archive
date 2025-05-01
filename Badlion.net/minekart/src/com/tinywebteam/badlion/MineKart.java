package com.tinywebteam.badlion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.badlion.cmdsigns;
import net.badlion.gberry.Gberry;
import net.badlion.warps.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.tinywebteam.badlion.items.ItemGenerator;
import net.badlion.gberry.listeners.BlockBreakListener;
import net.badlion.gberry.listeners.BlockPlaceListener;
import net.badlion.gberry.listeners.ChunkListener;
import net.badlion.gberry.listeners.EnderCrystalListener;
import net.badlion.gberry.listeners.ExitHorseListener;
import net.badlion.gberry.listeners.HungerListener;
import net.badlion.gberry.listeners.ItemDropListener;
import net.badlion.gberry.listeners.PlayerDamageListener;
import net.badlion.gberry.listeners.PlayerInteractListener;
import net.badlion.gberry.listeners.PlayerItemListener;
import net.badlion.gberry.listeners.PlayerMoveListener;
import net.badlion.gberry.listeners.PlayerPickupItemListener;
import net.badlion.gberry.listeners.PlayerQuitListener;
import net.badlion.gberry.listeners.PlayerSpawnListener;
import net.badlion.gberry.listeners.ProjectileHitListener;
import net.badlion.gberry.listeners.ProjectileLaunchListener;
import com.tinywebteam.badlion.tasks.InviteDelayTask;
import com.tinywebteam.badlion.tasks.ItemBlockTracker;
import com.tinywebteam.badlion.tasks.PlayerTracker;
import com.tinywebteam.badlion.tasks.QueueCheckerTask;
import com.tinywebteam.badlion.tasks.RaceStartDelayTask;
import com.tinywebteam.badlion.tasks.RacerPositionTracker;

public class MineKart extends JavaPlugin {
	
	private Connection connection;
	private Gberry gberry;
	private cmdsigns.CmdSigns cmdSigns;
	
	private ArrayList<Track> tracks;
	private ArrayList<Race> races;
	private HashSet<Block> itemBlockLocations;
	private Map<Player, Racer> playerToRacer;
	private ArrayList<Boolean> availableTracks;
	private Map<Block, ItemBlock> itemBlockMap;
	
	private LinkedList<Player> unrankedPlayersWaitingForMatch;
	private int premiumMembersInQueue;
	private HashSet<String> premiumQueueNames;
	private ArrayList<Player> inMatchMaking;
	private ArrayList<Block> blocksToBeRemoved;
	private Map<Player, BukkitTask> playerToForceStartDelay;
	
	// Race invite logic
	private Map<Player, ArrayList<Player>> playerInviteAcceptedList;
	private Map<Player, Integer> numOfInvitesSent;
	private Map<Player, Player> guestToInviteeMap;
	private Map<Player, BukkitTask> guestToTask;
	private Map<Player, BukkitTask> hostToRaceTask;
	private Map<Player, Player> guestToHostAccepted;
	
	// Used to reset when they fall down
	private ArrayList<Horse> allowToEject;
	
	private WarpSystem warpPlugin;
	
	public MineKart() {
		tracks = new ArrayList<Track>();
		races = new ArrayList<Race>();
		this.itemBlockLocations = new HashSet<Block>();
		this.playerToRacer = new HashMap<Player, Racer>();
		this.availableTracks = new ArrayList<Boolean>();
		this.unrankedPlayersWaitingForMatch = new LinkedList<Player>();
		this.inMatchMaking = new ArrayList<Player>();
		this.premiumQueueNames = new HashSet<String>();
		this.playerInviteAcceptedList = new HashMap<Player, ArrayList<Player>>();
		this.guestToInviteeMap = new HashMap<Player, Player>();
		this.numOfInvitesSent = new HashMap<Player, Integer>();
		this.guestToTask = new HashMap<Player, BukkitTask>();
		this.allowToEject = new ArrayList<Horse>();
		this.hostToRaceTask = new HashMap<Player, BukkitTask>();
		this.blocksToBeRemoved = new ArrayList<Block>();
		this.guestToHostAccepted = new HashMap<Player, Player>();
		this.itemBlockMap = new HashMap<Block, ItemBlock>();
		this.playerToForceStartDelay = new HashMap<Player, BukkitTask>();
	}
	
	public ArrayList<Block> getBlocksToBeRemoved() {
		return blocksToBeRemoved;
	}

	public void setBlocksToBeRemoved(ArrayList<Block> blocksToBeRemoved) {
		this.blocksToBeRemoved = blocksToBeRemoved;
	}

	@Override
	public void onEnable() {
		// Load up DB First
		this.gberry = (Gberry) this.getServer().getPluginManager().getPlugin("Gberry");
		this.connection = this.gberry.getConnection(); 
		
		cmdSigns = (cmdsigns.CmdSigns)this.getServer().getPluginManager().getPlugin("CmdSigns");
		warpPlugin = (WarpSystem)this.getServer().getPluginManager().getPlugin("WarpSystem");
		
		for (int i = -25; i < 15; ++i) {
			for (int j = -50; j < 30; ++j) {
				this.getServer().getWorld("world").getChunkAt(i, j);
			}
		}
		
		// Get all ender crystal locations and remove
		List<Entity> entitiesInWorld = this.getServer().getWorld("world").getEntities();
		for (Entity entity : entitiesInWorld) {
			if (entity instanceof EnderCrystal) {
				entity.remove();
			}
		}
		
		// Load DB stuff
		this.premiumMembersInQueue = 0;
		this.getRaceTracks();
		this.loadPremiumMembers();
		
		ItemGenerator.intialize();
		
		this.getServer().getScheduler().runTaskTimer(this, new PlayerTracker(this, this.races), 0, 5); // 0.25s
		this.getServer().getScheduler().runTaskTimer(this, new RacerPositionTracker(this, this.races), 0, 5); // 0.25s
		this.getServer().getScheduler().runTaskTimer(this, new ItemBlockTracker(this, this.races), 0, 1); // 0.05s
		
		this.getServer().getPluginManager().registerEvents(new PlayerDamageListener(this), this);
		this.getServer().getPluginManager().registerEvents(new EnderCrystalListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(this, this.itemBlockLocations, this.itemBlockMap), this);
		this.getServer().getPluginManager().registerEvents(new PlayerItemListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
		this.getServer().getPluginManager().registerEvents(new ExitHorseListener(this), this);
		//this.getServer().getPluginManager().registerEvents(new HorseJumpListener(this), this);
		this.getServer().getPluginManager().registerEvents(new ProjectileHitListener(this), this);
		this.getServer().getPluginManager().registerEvents(new ProjectileLaunchListener(this), this);
		this.getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
		this.getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
		this.getServer().getPluginManager().registerEvents(new HungerListener(this), this);
		this.getServer().getPluginManager().registerEvents(new ItemDropListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerPickupItemListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerSpawnListener(this), this);
		this.getServer().getPluginManager().registerEvents(new ChunkListener(), this);
	}
	
	@Override
	public void onDisable() {
		// GET OFF YOUR FKN HORSES
		Player [] players = this.getServer().getOnlinePlayers();
		for (int i = 0; i < players.length; ++i) {
			Horse horse = (Horse) players[i].getVehicle();
			if (horse != null) {
				horse.eject();
				horse.remove();
			}
			players[i].kickPlayer("Server rebooting.");
		}
		for (Race race : this.races) {
			for (Racer racer : race.getRacers()) {
				racer.remove();
			}
		}
		
	}
	
	public void tpPlayerToSpawn(Player player) {
		player.getInventory().clear();
		player.teleport(new Location(player.getWorld(), 0.5, 71, 0.5)); // HARDCODED
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args){
		if (sender instanceof Player) {
			final Player player = (Player) sender;
			if (command.getName().equalsIgnoreCase("race")) {  // 1 player
				if (playerToRacer.containsKey(player)) {
					player.sendMessage(ChatColor.RED + "Already in a match!");
					return true;
				}
				
				if (!unrankedPlayersWaitingForMatch.contains(player) && !inMatchMaking.contains(player)) {
					if (this.premiumQueueNames.contains(player.getName())) {
						unrankedPlayersWaitingForMatch.add(this.premiumMembersInQueue++, player); // add them  in front of non-premiums
					} else {
						unrankedPlayersWaitingForMatch.add(player); // add to end
					}
					
					player.sendMessage("Added to unranked matchmaking. 1 minute maximum wait.");
					inMatchMaking.add(player);
					
					// Force start match in 2 min
					this.playerToForceStartDelay.put(player, this.getServer().getScheduler().runTaskLater(this, new QueueCheckerTask(this, player), 20 * 60));
					
					if (unrankedPlayersWaitingForMatch.size() >= 8) {
						Boolean foundTrack = false;
						ArrayList<Integer> randomTracksToChooseFrom = new ArrayList<Integer>();
						for (int i = 0; i < this.availableTracks.size(); ++i) {
							if (this.availableTracks.get(i) == true) {
								foundTrack = true;
								randomTracksToChooseFrom.add(i);
							}
						}
						
						if (!foundTrack) {
							return false; // wait a bit longer
						}
						
						ArrayList<Player> players = new ArrayList<Player>();
						int initialSize = unrankedPlayersWaitingForMatch.size();
						for (int i = 0; i < initialSize; ++i) {
							Player p = this.getUnrankedPlayersWaitingForMatch().remove();
							players.add(p);
							
							// Remove from force task stuff
							BukkitTask task = this.getPlayerToForceStartDelay().get(p);
							task.cancel();
							this.getPlayerToForceStartDelay().remove(p);
							
							if (this.premiumQueueNames.contains(players.get(i).getName())) {
								--this.premiumMembersInQueue;
							}
						}

						try {
							createMatch(players, 0, randomTracksToChooseFrom);
							
							//player1.sendMessage(ChatColor.BLUE + "Now in unranked match with " + player2.getName());
							//player2.sendMessage(ChatColor.BLUE + "Now in unranked match with " + player1.getName());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Bukkit.getLogger().severe("Out of tracks.");
							for (int i = 0; i < players.size(); ++i) {
								removePlayerFromMatchmaking(players.get(i));
								players.get(i).sendMessage(ChatColor.RED + "No tracks available.  Try again later.");
							}
							e.printStackTrace();
						}
					}
				} else {
					player.sendMessage(ChatColor.RED + "Matchmaking is finding you an opponent.");
				}
			} else if (command.getName().equalsIgnoreCase("invite")) {
				// Example usage: /invite <playerName>
				if (this.premiumQueueNames.contains(player.getName())) {
					if (args.length != 1) {
						player.sendMessage(ChatColor.RED + "The correct syntax is /invite <player name> for a race invite.");
						return false;
					} else if (this.playerToRacer.containsKey(player)) {
						player.sendMessage(ChatColor.RED + "You are already in a race.  Wait until you are done racing to create a new race.");
						return false;
					} else if (this.guestToInviteeMap.containsKey(player)) {
						player.sendMessage(ChatColor.RED + "You already have a pending invite.  /accept or /deny before trying to make your own race.");
						return false;
					} else if (this.numOfInvitesSent.containsKey(player) && this.numOfInvitesSent.get(player) >= 7) {
						player.sendMessage(ChatColor.RED + "You have already invited 7 players.");
						return false;
					}
					Player guest = this.getServer().getPlayer(args[0]);
					if (guest != null) {
						if (this.guestToInviteeMap.containsKey(guest)) {
							player.sendMessage(ChatColor.BLUE + "This player already has a pending invite.");
							return false;
						} else if (this.playerToRacer.containsKey(guest)) {
							player.sendMessage(ChatColor.BLUE + "This player is already in a match.  Wait until they are finished.");
							return false;
						} else if (this.playerInviteAcceptedList.containsKey(player) && this.playerInviteAcceptedList.get(player).contains(guest)) {
							player.sendMessage(ChatColor.BLUE + "This player is already in your race list and has accepted the invite.");
							return false;
						}
						
						// Force start task
						if (!this.hostToRaceTask.containsKey(player)) {
							BukkitTask startRace = new RaceStartDelayTask(this, player).runTaskLater(this, 20 * 120); // 120s
							this.hostToRaceTask.put(player, startRace);
						}
						
						// Send invite
						BukkitTask invite = new InviteDelayTask(this, guest).runTaskLater(this, 20 * 60); // 60s
						this.guestToTask.put(guest, invite);
						
						// Keep track
						if (this.numOfInvitesSent.containsKey(player)) {
							this.numOfInvitesSent.put(player, this.numOfInvitesSent.get(player) + 1);
						} else {
							this.numOfInvitesSent.put(player, 1);
						}
						
						// Add self to accepted player list
						if (!this.playerInviteAcceptedList.containsKey(player)) {
							ArrayList<Player> invitedGuests = new ArrayList<Player>();
							invitedGuests.add(player); // gotta add ourselves
							this.playerInviteAcceptedList.put(player, invitedGuests);
						}
						this.guestToInviteeMap.put(guest, player);
						
						// No queueing up now
						if (!this.inMatchMaking.contains(player)) {
							this.inMatchMaking.add(player);
						}
						this.inMatchMaking.add(guest);
						
						// Done
						player.sendMessage(ChatColor.GREEN + "Invited " + guest.getName() + " to a race.");
						guest.sendMessage(ChatColor.BLUE + "Race request from " + player.getName() + ".  Type /accept or /deny within 60 seconds to respond.");
					} else {
						player.sendMessage(ChatColor.RED + "No such player exists.");
					}
				} else {
					player.sendMessage(ChatColor.BLUE + "This is a donator feature only.  Go to www.badlion.net to become a donator.");
				}
			} else if (command.getName().equalsIgnoreCase("start")) {
				// Example usage: /start
				if (this.premiumQueueNames.contains(player.getName())) {
					ArrayList<Player> players = this.playerInviteAcceptedList.get(player);
					if (players != null && players.size() > 1) {
						try {
							boolean foundTrack = false;
							ArrayList<Integer> randomTracksToChooseFrom = new ArrayList<Integer>();
							for (int i = 0; i < this.availableTracks.size(); ++i) {
								if (this.availableTracks.get(i) == true) {
									foundTrack = true;
									randomTracksToChooseFrom.add(i);
								}
							}
							if (!foundTrack) {
								player.sendMessage(ChatColor.RED + "No tracks available at the moment.  Try again in a few seconds.");
								return false; // wait a bit longer
							}
							
							Bukkit.getLogger().info("players size " + players.size());
							
							// Remove from map for dc check
							for (Player p : players) {
								this.guestToHostAccepted.remove(p);
							}
							this.hostToRaceTask.remove(player); // they started alrdy
							this.playerInviteAcceptedList.remove(player);
							createMatch(players, 0, randomTracksToChooseFrom);
							
							//player1.sendMessage(ChatColor.BLUE + "Now in unranked match with " + player2.getName());
							//player2.sendMessage(ChatColor.BLUE + "Now in unranked match with " + player1.getName());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Bukkit.getLogger().severe("Out of tracks.");
							for (int i = 0; i < players.size(); ++i) {
								removePlayerFromMatchmaking(players.get(i));
								players.get(i).sendMessage(ChatColor.RED + "No tracks available.  Try again later.");
							}
							e.printStackTrace();
						}
					} else {
						player.sendMessage(ChatColor.RED + "You need to use /invite to invite some players first.  Or wait for players to accept your invite.");
					}
				} else {
					player.sendMessage(ChatColor.BLUE + "This is a donator feature only.  Go to www.badlion.net to become a donator.");
				}
			} else if (command.getName().equalsIgnoreCase("accept")) {
				// Example usage: /accept 
				// TODO: Multi-invite system aka /accept Gberry /accept Archy
				if (this.guestToInviteeMap.containsKey(player)) {
					Player host = this.guestToInviteeMap.get(player);
					this.guestToInviteeMap.remove(player);
					this.guestToTask.get(player).cancel();
					this.guestToTask.remove(player);
					if (this.playerInviteAcceptedList.get(host) != null) {
						this.playerInviteAcceptedList.get(host).add(player);
						this.guestToHostAccepted.put(player, host);
						host.sendMessage(ChatColor.GREEN + player.getName() + " has accepted your invite.  /start if ready or wait for more players.");
						player.sendMessage(ChatColor.GREEN + "You have accepted the race invite.  Waiting for host to start the race.");
					} else {
						player.sendMessage(ChatColor.RED + "It looks like this race started already or no longer exists.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You have no pending invites.");
				}
			} else if (command.getName().equalsIgnoreCase("deny")) {
				// Example usage: /deny 
				// TODO: Multi-invite system
				if (this.guestToInviteeMap.containsKey(player)) {
					Player host = this.guestToInviteeMap.get(player);
					this.guestToInviteeMap.remove(player);
					this.guestToTask.get(player).cancel();
					this.guestToTask.remove(player);
					host.sendMessage(ChatColor.GREEN + player.getName() + " has denied your invite.");
					player.sendMessage(ChatColor.GREEN + "You have denied the race invite.");
				} else {
					player.sendMessage(ChatColor.RED + "You have no pending invites.");
				}
			} else if (command.getName().equalsIgnoreCase("addtrack")) {
				// Example usage: /addtrack <trackName> <numOfLaps> <slowBlocks> <allowBlocks>
				this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
					@Override
					public void run() {
						try {
							String sql = "INSERT INTO race_tracks (race_track_name, num_of_laps, slowblocks, allowblocks) VALUES(?, ?, ?, ?) " +
									"ON DUPLICATE KEY UPDATE num_of_laps=?, slowblocks=?, allowblocks=?;";
							PreparedStatement ps = connection.prepareStatement(sql);
							ps.setString(1, args[0]);
							ps.setInt(2, Integer.parseInt(args[1]));
							ps.setString(3, args[2]);
							ps.setString(4, args[3]);
							ps.setInt(5, Integer.parseInt(args[1]));
							ps.setString(6, args[2]);
							ps.setString(7, args[3]);
							ps.executeUpdate();
							player.sendMessage(ChatColor.GREEN + "Added/Updated track " + args[0]);
						} catch (SQLException ex) {
							ex.printStackTrace();
							org.bukkit.Bukkit.getLogger().severe(ex.getMessage());
							player.sendMessage(ChatColor.RED + "Something broke.  Contact an admin if it continues to not work.");
						}
					}
				});
			} else if (command.getName().equalsIgnoreCase("addcheckpoint")) {
				// Example usage: /addcheckpoint <trackName> <checkpointName> <nextCheckpointName> <checkpointNumber>
				this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
					@Override
					public void run() {
						try {
							String sql = "INSERT INTO race_track_checkpoints (race_track_name, race_track_checkpoint_name, race_track_next_checkpoint_name, race_track_point_id, x, y, z)" +
										"VALUES(?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE race_track_checkpoint_name=?, race_track_next_checkpoint_name=?, race_track_point_id=?, x=?, y=?, z=?;";
							PreparedStatement ps = connection.prepareStatement(sql);
							ps.setString(1, args[0]);
							ps.setString(2, args[1]);
							ps.setString(3, args[2]);
							ps.setInt(4, Integer.parseInt(args[3]));
							ps.setInt(5, player.getLocation().getBlockX());
							ps.setInt(6, player.getLocation().getBlockY());
							ps.setInt(7, player.getLocation().getBlockZ());
							ps.setString(8, args[1]);
							ps.setString(9, args[2]);
							ps.setInt(10, Integer.parseInt(args[3]));
							ps.setInt(11, player.getLocation().getBlockX());
							ps.setInt(12, player.getLocation().getBlockY());
							ps.setInt(13, player.getLocation().getBlockZ());
							ps.executeUpdate();
							player.sendMessage(ChatColor.GREEN + "Added/Updated Checkpoint " + args[1]);
						} catch (SQLException ex) {
							ex.printStackTrace();
							org.bukkit.Bukkit.getLogger().severe(ex.getMessage());
							player.sendMessage(ChatColor.RED + "Something broke.  Contact an admin if it continues to not work.");
						}
					}
				});
			} else if (command.getName().equalsIgnoreCase("additemblock")) {
				// Example usage: /additemblock <trackName> <itemBlockName>
				Block block = player.getLocation().getBlock();
				Location location = new Location(Bukkit.getWorld("world"), block.getX() + 0.5, block.getY() - 0.7, block.getZ() + 0.5);
				Bukkit.getWorld("world").spawn(location, EnderCrystal.class);
				this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
					@Override
					public void run() {
						try {
							String sql = "INSERT INTO race_track_itemblock_locations (race_track_name, x, y, z)" +
										"VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE x=?, y=?, z=?;";
							PreparedStatement ps = connection.prepareStatement(sql);
							ps.setString(1, args[0]);
							ps.setFloat(2, (float) (player.getLocation().getBlockX() + 0.5));
							ps.setFloat(3, (float) (player.getLocation().getBlockY() - 0.7));
							ps.setFloat(4, (float) (player.getLocation().getBlockZ() + 0.5));
							ps.setFloat(5, (float) (player.getLocation().getBlockX() + 0.5));
							ps.setFloat(6, (float) (player.getLocation().getBlockY() - 0.7));
							ps.setFloat(7, (float) (player.getLocation().getBlockZ() + 0.5));
							ps.executeUpdate();
							player.sendMessage(ChatColor.GREEN + "Added/Updated itemblock " + args[0]);
						} catch (SQLException ex) {
							ex.printStackTrace();
							org.bukkit.Bukkit.getLogger().severe(ex.getMessage());
							player.sendMessage(ChatColor.RED + "Something broke.  Contact an admin if it continues to not work.");
						}
					}
				});
			} else if (command.getName().equalsIgnoreCase("addfinishlineblock")) {
				// Example usage: /addfinishlineblock <trackName>
				this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
					@Override
					public void run() {
						try {
							String sql = "INSERT INTO race_track_finish_line_blocks (race_track_name, x, y, z)" +
										"VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE x=?, y=?, z=?;";
							PreparedStatement ps = connection.prepareStatement(sql);
							ps.setString(1, args[0]);
							ps.setInt(2, player.getLocation().getBlockX());
							ps.setInt(3, player.getLocation().getBlockY());
							ps.setInt(4, player.getLocation().getBlockZ());
							ps.setInt(5, player.getLocation().getBlockX());
							ps.setInt(6, player.getLocation().getBlockY());
							ps.setInt(7, player.getLocation().getBlockZ());
							ps.executeUpdate();
							player.sendMessage(ChatColor.GREEN + "Added/Updated finishblock " + args[0]);
						} catch (SQLException ex) {
							ex.printStackTrace();
							org.bukkit.Bukkit.getLogger().severe(ex.getMessage());
							player.sendMessage(ChatColor.RED + "Something broke.  Contact an admin if it continues to not work.");
						}
					}
				});
			} else if (command.getName().equalsIgnoreCase("reloaddev")) {
				this.getRaceTracks();
			} else if (command.getName().equalsIgnoreCase("removeendercrystal")) {
				for (Entity entity : Bukkit.getWorld("world").getEntities()) {
					if (entity instanceof EnderCrystal) {
						entity.remove();
					}
				}
			}
		}
		return true;
	}
	
	public boolean createMatch(ArrayList<Player> players, int rankedOrUnranked, ArrayList<Integer> randomTracksToChooseFrom) throws Exception {
		return createMatch(players, rankedOrUnranked, "", 0, randomTracksToChooseFrom); // pass through method
	}
	
	public boolean createMatch(ArrayList<Player> players, int rankedOrUnranked, String rankedType, int ladderId, ArrayList<Integer> randomTracksToChooseFrom) throws Exception {
		// Find an open arena
		Race race = null;

		// We found an arena, FIND YOUR OWN FUCKING TRACK
		int j = (int)(randomTracksToChooseFrom.size() * Math.random());
		int trackNumber = randomTracksToChooseFrom.get(j);
		this.availableTracks.set(trackNumber, false);
		Track track = this.tracks.get(trackNumber);
		track.setIndex(trackNumber);
		
		// Create a match
		race = new Race(this, players, track);
		this.races.add(race);
		
		if (ladderId == 0) {
			// Unranked
			race.startCountDown();
		} else {
			// Ranked
			/*String hash = this.cmdSigns.getRandomString().nextString();
			while (this.cmdSigns.getValidHashes().contains(hash)) {
				hash = this.cmdSigns.getRandomString().nextString();
			}
			this.cmdSigns.getValidHashes().add(hash);
			this.getServer().getPluginCommand("load").execute(player1, "load", new String [] {hash, player1.getName(), rankedType});
			
			hash = this.cmdSigns.getRandomString().nextString();
			while (this.cmdSigns.getValidHashes().contains(hash)) {
				hash = this.cmdSigns.getRandomString().nextString();
			}
			this.cmdSigns.getValidHashes().add(hash);
			this.getServer().getPluginCommand("load").execute(player2, "load", new String [] {hash, player2.getName(), rankedType});
			
			// Remove pot effects
			for (PotionEffect effect : player1.getActivePotionEffects())
				player1.removePotionEffect(effect.getType());
			
			for (PotionEffect effect : player2.getActivePotionEffects())
				player2.removePotionEffect(effect.getType());
			
			player1.setGameMode(GameMode.SURVIVAL);
			player2.setGameMode(GameMode.SURVIVAL);
			player1.teleport(arena.getWarp1());
			player2.teleport(arena.getWarp2());	*/
		}
		
		return true;
	}
	
	public void loadPremiumMembers() {
		try {
			String query = "SELECT * FROM user_roles WHERE role_id = 1;";
			PreparedStatement ps = this.connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				this.premiumQueueNames.add(rs.getString("username"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getRaceTracks() {
		// Remove any tracks, we might be reloading
		this.tracks.clear();
		// Get tracks first
		try {
			String query = "SELECT * FROM race_tracks;";
			PreparedStatement ps = this.connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				// Get track
				HashSet<Integer> slowBlocks = new HashSet<Integer>();
				for (String block : rs.getString("slowblocks").split(",")) {
					if (!block.equals("")) {
						slowBlocks.add(Integer.parseInt(block));
					}
				}
				HashSet<Integer> allowBlocks = new HashSet<Integer>();
				// HARDCODED some values
				allowBlocks.add(0); // air
				allowBlocks.add(51); // fire
				allowBlocks.add(200); // endercrystal
				allowBlocks.add(30); // cobweb
				allowBlocks.add(36); // hidden block
				allowBlocks.add(46); // tnt
				for (String block : rs.getString("allowblocks").split(",")) {
					if (!block.equals("")) {
						allowBlocks.add(Integer.parseInt(block));
					}
				}
				
				Track track = new Track(rs.getString("race_track_name"), rs.getInt("num_of_laps"), allowBlocks, slowBlocks);
				tracks.add(track);
				
				// Get Checkpoints
				String query2 = "SELECT * FROM race_track_checkpoints WHERE race_track_name = ? ORDER BY race_track_point_id ASC;";
				ps = this.connection.prepareStatement(query2);
				ps.setString(1, rs.getString("race_track_name"));
				ResultSet rs2 = ps.executeQuery();
				
				// Organize data for later use
				ArrayList<CheckPoint> checkPointList = new ArrayList<CheckPoint>();
				Map<String, CheckPoint> checkPointMap = new HashMap<String, CheckPoint>();
				while (rs2.next()) {
					// Create new checkpoint for this track
					CheckPoint checkPoint = new CheckPoint(rs2.getString("race_track_checkpoint_name"), rs2.getString("race_track_next_checkpoint_name"), 
							this.getServer().getWorld("world").getBlockAt(rs2.getInt("x"), rs2.getInt("y"), rs2.getInt("z")));
					checkPointMap.put(rs2.getString("race_track_checkpoint_name"), checkPoint);
					checkPointList.add(checkPoint);
				}
				
				// Add checkpoints to track
				track.setCheckPoints(checkPointList);
				for (CheckPoint checkPoint : checkPointList) {
					track.getCheckPointToNextCheckPoint().put(checkPoint, checkPointMap.get(checkPoint.getNextPointName()));
				}
				
				// Get item blocks
				String query3 = "SELECT * FROM race_track_itemblock_locations WHERE race_track_name = ?;";
				ps = this.connection.prepareStatement(query3);
				ps.setString(1, rs.getString("race_track_name"));
				ResultSet rs3 = ps.executeQuery();
				
				while (rs3.next()) {

					Location location2 = this.getServer().getWorld("world").getBlockAt(rs3.getInt("x"), rs3.getInt("y"), rs3.getInt("z")).getLocation();
					if (location2.getX() < 0)
						location2.setX(location2.getX() - 0.5);
					else
						location2.setX(location2.getX() + 0.5);
					location2.setY(location2.getY() + 0.3);
					if (location2.getZ() < 0)
						location2.setZ(location2.getZ() - 0.5);
					else
						location2.setZ(location2.getZ() + 0.5);
					Location location = location2.clone();
					location.setY(location.getY() + 1);
					//Location location3 = location.clone(); // block above
					//location3.setY(location3.getY() + 1); // block above
					//this.itemBlockLocations.add(location.getBlock());
					//this.itemBlockLocations.add(location3.getBlock()); // block above
					EnderCrystal enderCrystal = Bukkit.getWorld("world").spawn(location2, EnderCrystal.class);
					ItemBlock itemBlock = new ItemBlock(location2, enderCrystal, location.getBlock());
					track.getItemBlocks().add(itemBlock);
					//this.itemBlockMap.put(location.getBlock(), itemBlock);
				}
				
				// Get finish line blocks
				String query4 = "SELECT * FROM race_track_finish_line_blocks WHERE race_track_name = ?;";
				ps = this.connection.prepareStatement(query4);
				ps.setString(1, rs.getString("race_track_name"));
				ResultSet rs4 = ps.executeQuery();
				
				HashSet<Block> finishLineBlocks = new HashSet<Block>();
				
				while (rs4.next()) {
					finishLineBlocks.add(this.getServer().getWorld("world").getBlockAt(rs4.getInt("x"), rs4.getInt("y"), rs4.getInt("z")));
				}
				
				track.setFinishLineBlocks(finishLineBlocks);
				
				// Get spawn warps
				// HAX, eventually fix warp system to be nicer
				Map<String, Location> spawns = this.warpPlugin.getAllWarpsLike(track.getTrackName());
				ArrayList<Location> spawnLocations = new ArrayList<Location>();
				for (int i = 0; i < spawns.size(); ++i) {
					spawnLocations.add(spawns.get(track.getTrackName() + String.valueOf(i+1)));
				}
				track.setSpawnLocations(spawnLocations);
				
				this.availableTracks.add(true);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			org.bukkit.Bukkit.getLogger().severe(ex.getMessage());
		}
		
		// TODO: Get itemblock locations
	}
	
	public void removePlayerFromMatchmaking(Player player) {
		// Removes them from in matchmaking
		if (this.inMatchMaking.contains(player)){
			this.inMatchMaking.remove(player);
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public ArrayList<Track> getTracks() {
		return tracks;
	}

	public void setTracks(ArrayList<Track> tracks) {
		this.tracks = tracks;
	}

	public ArrayList<Race> getRaces() {
		return races;
	}

	public void setRaces(ArrayList<Race> races) {
		this.races = races;
	}

	public HashSet<Block> getItemBlockLocations() {
		return itemBlockLocations;
	}

	public void setItemBlockLocations(HashSet<Block> itemBlockLocations) {
		this.itemBlockLocations = itemBlockLocations;
	}

	public Map<Player, Racer> getPlayerToRacer() {
		return playerToRacer;
	}

	public void setPlayerToRacer(Map<Player, Racer> playerToRacer) {
		this.playerToRacer = playerToRacer;
	}

	public ArrayList<Boolean> getAvailableTracks() {
		return availableTracks;
	}

	public void setAvailableTracks(ArrayList<Boolean> availableTracks) {
		this.availableTracks = availableTracks;
	}

	public ArrayList<Player> getInMatchMaking() {
		return inMatchMaking;
	}

	public void setInMatchMaking(ArrayList<Player> inMatchMaking) {
		this.inMatchMaking = inMatchMaking;
	}

	public LinkedList<Player> getUnrankedPlayersWaitingForMatch() {
		return unrankedPlayersWaitingForMatch;
	}

	public void setUnrankedPlayersWaitingForMatch(
			LinkedList<Player> unrankedPlayersWaitingForMatch) {
		this.unrankedPlayersWaitingForMatch = unrankedPlayersWaitingForMatch;
	}

	public int getPremiumMembersInQueue() {
		return premiumMembersInQueue;
	}

	public void setPremiumMembersInQueue(int premiumMembersInQueue) {
		this.premiumMembersInQueue = premiumMembersInQueue;
	}

	public HashSet<String> getPremiumQueueNames() {
		return premiumQueueNames;
	}

	public void setPremiumQueueNames(HashSet<String> premiumQueueNames) {
		this.premiumQueueNames = premiumQueueNames;
	}

	public Map<Player, ArrayList<Player>> getPlayerInviteAcceptedList() {
		return playerInviteAcceptedList;
	}

	public void setPlayerInviteAcceptedList(
			Map<Player, ArrayList<Player>> playerInviteAcceptedList) {
		this.playerInviteAcceptedList = playerInviteAcceptedList;
	}

	public Map<Player, Player> getGuestToInviteeMap() {
		return guestToInviteeMap;
	}

	public void setGuestToInviteeMap(Map<Player, Player> guestToInviteeMap) {
		this.guestToInviteeMap = guestToInviteeMap;
	}

	public Map<Player, Integer> getNumOfInvitesSent() {
		return numOfInvitesSent;
	}

	public void setNumOfInvitesSent(Map<Player, Integer> numOfInvitesSent) {
		this.numOfInvitesSent = numOfInvitesSent;
	}

	public Map<Player, BukkitTask> getGuestToTask() {
		return guestToTask;
	}

	public void setGuestToTask(Map<Player, BukkitTask> guestToTask) {
		this.guestToTask = guestToTask;
	}

	public Map<Player, BukkitTask> getHostToRaceTask() {
		return hostToRaceTask;
	}

	public void setHostToRaceTask(Map<Player, BukkitTask> hostToRaceTask) {
		this.hostToRaceTask = hostToRaceTask;
	}

	public ArrayList<Horse> getAllowToEject() {
		return allowToEject;
	}

	public void setAllowToEject(ArrayList<Horse> allowToEject) {
		this.allowToEject = allowToEject;
	}

	public Map<Player, Player> getGuestToHostAccepted() {
		return guestToHostAccepted;
	}

	public void setGuestToHostAccepted(Map<Player, Player> guestToHostAccepted) {
		this.guestToHostAccepted = guestToHostAccepted;
	}

	public Map<Block, ItemBlock> getItemBlockMap() {
		return itemBlockMap;
	}

	public void setItemBlockMap(Map<Block, ItemBlock> itemBlockMap) {
		this.itemBlockMap = itemBlockMap;
	}

	public Map<Player, BukkitTask> getPlayerToForceStartDelay() {
		return playerToForceStartDelay;
	}

	public void setPlayerToForceStartDelay(
			Map<Player, BukkitTask> playerToForceStartDelay) {
		this.playerToForceStartDelay = playerToForceStartDelay;
	}

}
