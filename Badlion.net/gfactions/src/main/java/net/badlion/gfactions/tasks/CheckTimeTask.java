package net.badlion.gfactions.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.bukkitevents.EventStateChangeEvent;
import net.badlion.gfactions.events.koth.KOTH;
import net.badlion.gfactions.events.koth.StartKOTHTask;
import net.badlion.gfactions.listeners.AbuseListener;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.spigotmc.SpigotConfig;

import java.util.ArrayList;
import java.util.List;

public class CheckTimeTask extends BukkitRunnable {

	// TODO: IF WE END UP CONVERTING ALL EVENT SYSTEMS TO USE COMPONENTS, REMEMBER THAT ORDER FOR SOME EVENTS MATTERS, SO INITIALIZE ACCORDINGLY
	private static List<CheckTimeComponent> checkTimeComponents = new ArrayList<>();

	private GFactions plugin;

	private boolean twoBool;
	private boolean tenBool;
	private boolean dragonWarning;
	private boolean market;
	private boolean dungeon;
    private boolean dungeonWarning;
	private boolean lotto;
    private boolean lottery;
    private boolean saveWarning;
    private boolean save;
    private boolean serverJustRestarted;
	private boolean tower;
	private boolean towerWarning;
	private int xTowerLocation;
	private int yTowerLocation;
	private int zTowerLocation;
	private boolean bloodBowlFlag = false;
    private boolean lottoWarningFlag = true;
    private boolean lottoDrawFlag = false;
	
	public CheckTimeTask(GFactions plugin) {
		this.plugin = plugin;
		this.twoBool = false;
		this.tenBool = false;
		this.market = false;
		this.dungeon = false;
        this.dungeonWarning = false;
		this.lotto = false;
        this.lottery = false;
        this.dragonWarning = false;
        this.saveWarning = false;
        this.save = false;
        this.serverJustRestarted = true;

        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {

            @Override
            public void run() {
                serverJustRestarted = false;
            }

        }, 20 * 60 * 5);
	}
	
	@Override
	public void run() {
		DateTime date = new DateTime();
		// Warning for restart
		/*EOTW if (!this.twoBool && date.isAfter(this.plugin.getDateToRestart().minusMinutes(2))) {
			this.twoBool = true;
			Gberry.broadcastMessage(ChatColor.RED + "Server rebooting in two minutes.");
		} else if (!this.tenBool && date.isAfter(this.plugin.getDateToRestart().minusSeconds(10))) {
			this.tenBool = true;
			Gberry.broadcastMessage(ChatColor.RED + "Server rebooting in 10 seconds.");
		}

        // Restart stuff, do not restart while BloodBowl is running
		if (!this.plugin.getBloodBowlManager().isRunning()) {
			if (date.isAfter(this.plugin.getDateToRestart())) {
				this.restartServer();
                return; // Because we don't want events to run, right?
			}
		} else {
			// One time if we are running bloodbowl let the server restart 5 minutes later
			if (!this.bloodBowlFlag) {
				this.plugin.setDateToRestart(this.plugin.getDateToRestart().plusMinutes(5));
				this.bloodBowlFlag = true;
			}
		}*/

		// Component stuff
		for (CheckTimeComponent checkTimeComponent : CheckTimeTask.checkTimeComponents) {
			checkTimeComponent.run(this);
		}
		
		// Market Property (once an hour)
		/*if (!this.market && date.getMinuteOfDay() % 60 == 5) {
			this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new MarketPropertyTask(this.plugin));
			this.market = true;
		} else if (this.market && date.getMinuteOfDay() % 60 == 6) {
			this.market = false;
		}
		
		// Lotto Property (once an hour)
		if (!this.lotto && date.getHourOfDay() % 4 == 0) {
			this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new TicketHQTask(this.plugin));
			this.lotto = true;
		} else if (this.market && date.getHourOfDay() % 4 == 1) {
			this.lotto = false;
		}

        // Dungeon Warning
        if (!this.dungeonWarning && date.getMinuteOfDay() % 48 == 38) {
            this.plugin.getDungeonManager().generateRandomPortal();
            this.dungeonWarning = true;
        } else if (this.dungeonWarning && date.getMinuteOfDay() % 48 == 39) {
            this.dungeonWarning = false;
        }
		
		// Dungeon
		if (!this.dungeon && date.getMinuteOfDay() % 48 == 0) {
			this.plugin.getDungeonManager().createNewDungeon();
			this.dungeon = true;
		} else if (this.dungeon && date.getMinuteOfDay() % 48 == 1) {
			this.dungeon = false;
		}*/

        // Lottery
        /*if (this.plugin.getLottoDrawingTask() == null) {
            DateTime now = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0);
            for (DateTime dt : this.plugin.getLotteryTimes()) {
				// They datetimes are almost never going to be equal, check day of week and minute of day
				if (now.getDayOfWeek() == dt.getDayOfWeek() && now.getMinuteOfDay() == dt.getMinuteOfDay() && this.lottoDrawFlag) {
                    this.plugin.setLottoDrawingTask(new LottoDrawingTask(this.plugin));
                    this.plugin.getLottoDrawingTask().runTask(this.plugin);
                    this.lottoDrawFlag = false;
                    break;
                } else if (now.getDayOfWeek() == dt.getDayOfWeek() && now.getMinuteOfDay() == dt.getMinuteOfDay() - 15 && this.lottoWarningFlag) {
					Gberry.broadcastMessage(ChatColor.DARK_AQUA + "[" + ChatColor.LIGHT_PURPLE + "LOTTO" + ChatColor.DARK_AQUA + "] "
							+ ChatColor.GOLD + "Lottery drawing in 15 minutes! Use \"/lotto\" to buy tickets!");
                    this.lottoWarningFlag = false;
					break;
				} else if (now.getDayOfWeek() == dt.getDayOfWeek() && now.getMinuteOfDay() == dt.getMinuteOfDay() - 14) {
                    this.lottoWarningFlag = true;
                } else if (now.getDayOfWeek() == dt.getDayOfWeek() && now.getMinuteOfDay() == dt.getMinuteOfDay() - 10 && this.lottoWarningFlag) {
					Gberry.broadcastMessage(ChatColor.DARK_AQUA + "[" + ChatColor.LIGHT_PURPLE + "LOTTO" + ChatColor.DARK_AQUA + "] "
							+ ChatColor.GOLD + "Lottery drawing in 10 minutes! Use \"/lotto\" to buy tickets!");
                    this.lottoWarningFlag = false;
					break;
				} else if (now.getDayOfWeek() == dt.getDayOfWeek() && now.getMinuteOfDay() == dt.getMinuteOfDay() - 9) {
                    this.lottoWarningFlag = true;
                } else if (now.getDayOfWeek() == dt.getDayOfWeek() && now.getMinuteOfDay() == dt.getMinuteOfDay() - 5 && this.lottoWarningFlag) {
					Gberry.broadcastMessage(ChatColor.DARK_AQUA + "[" + ChatColor.LIGHT_PURPLE + "LOTTO" + ChatColor.DARK_AQUA + "] "
							+ ChatColor.GOLD + "Lottery drawing in 5 minutes! Use \"/lotto\" to buy tickets!");
                    this.lottoWarningFlag = false;
                    this.lottoDrawFlag = true;
					break;
				}
            }
        }*/

        // KOTH (10 Minute Warning), do not allow KOTH to run when BloodBowl is going on
		if (!this.plugin.getBloodBowlManager().isRunning()) {
			if (this.plugin.getKoth() == null) {
				DateTime now = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0);
				for (DateTime dt : this.plugin.getKothTimes()) {
					// They datetimes are almost never going to be equal, check day of week and minute of day (10 min warning)
					if (now.getDayOfWeek() == dt.getDayOfWeek() && now.getMinuteOfDay() == dt.getMinuteOfDay() - 10) {
						List<String> koths = (List<String>) this.plugin.getConfig().getList("gfactions.koth.koth_names");
						String kothName = koths.get(this.plugin.generateRandomInt(0, koths.size() - 1));

						int x = this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".capzone.x_min");
						int y = this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".capzone.y_min");
						int z = this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".capzone.z_min");

						this.plugin.setKoth(new KOTH(this.plugin, 1800, kothName)); // Length: 1800 seconds (30 minutes)

						Gberry.broadcastMessage(ChatColor.GOLD + "New KOTH starting in 10 minutes around "  + ChatColor.GREEN + x + ", " + y + ", " + z + ChatColor.GOLD + "!!!");
						new StartKOTHTask(this.plugin, this.plugin.getKoth()).runTaskLater(this.plugin, 10 * 60 * 20);

						// Call TabList event
						EventStateChangeEvent event = new EventStateChangeEvent("KOTH", true);
						this.plugin.getServer().getPluginManager().callEvent(event);

						break;
					}
				}
			}
		}

        // ManHunt
        if (this.plugin.getManHuntPP() == null) {
            DateTime now = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0);
            for (DateTime dt : this.plugin.getManhuntTimes()) {
                // They datetimes are almost never going to be equal, check day of week and minute of day
                if (now.getDayOfWeek() == dt.getDayOfWeek() && now.getMinuteOfDay() == dt.getMinuteOfDay() - 10) {
                    this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "manhunt start");
                    break;
                }
            }
        }

        /*if (this.plugin.getSuperMine() == null) {
            DateTime now = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0);
            for (DateTime dt : this.plugin.getManhuntTimes()) {
                // They datetimes are almost never going to be equal, check day of week and minute of day
                if (now.getDayOfWeek() == dt.getDayOfWeek() && now.getMinuteOfDay() == dt.getMinuteOfDay() - 10) {
                    new SuperMine();
                    break;
                }
            }
        }*/

        // Parkour
        if (this.plugin.getParkourChest() == null) {
            DateTime now = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0);
            for (DateTime dt : this.plugin.getParkourTimes()) {
                // They datetimes are almost never going to be equal, check day of week and minute of day
                if (now.getDayOfWeek() == dt.getDayOfWeek() && now.getMinuteOfDay() == dt.getMinuteOfDay() - 10) {
                    this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "parkour");
                    break;
                }
            }
        }

		// SAVE THE WORLDS
		if (!this.serverJustRestarted && !this.saveWarning && date.getMinuteOfDay() % 15 == 0 && date.getSecondOfMinute() % 60 == 20) {
			this.saveWarning = true;
			Gberry.broadcastMessage(ChatColor.RED + "[" + ChatColor.DARK_GRAY + "DISK" + ChatColor.RED + "]" + ChatColor.WHITE + "Saving worlds in 10 seconds. Expect some lag.");
		} else if (this.saveWarning && date.getMinuteOfDay() % 60 == 0) {
			this.saveWarning = false;
		}

		if (!this.serverJustRestarted && !this.save && date.getMinuteOfDay() % 15 == 0 && date.getSecondOfMinute() % 60 == 30) {
            SpigotConfig.enableAntiCheat = false;
			this.save = true;
			this.plugin.getServer().getWorld("world").save();
			this.plugin.getServer().getWorld("world_nether").save();

            Bukkit.getScheduler().runTaskLater(GFactions.plugin, new Runnable() {
	            @Override
	            public void run() {
		            SpigotConfig.enableAntiCheat = true;
	            }
            }, 200);
		} else if (this.save && date.getMinuteOfDay() % 60 == 0) {
			this.save = false;
		}

        // Dragon Event
        /*if (this.dragonWarning && this.plugin.getDragonEvent() == null && date.getHourOfDay() % 3 == 0 && date.getMinuteOfHour() == 15) { // ITS DA TURD HR OF LE DAY
	        this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "dragonevent");
        } else if (!this.dragonWarning && date.getHourOfDay() % 3 == 0 && date.getMinuteOfHour() == 5) {
            Gberry.broadcastMessage(ChatColor.GOLD + "The Ender Dragon will be spawning in ten minutes in the End!");

            this.dragonWarning = true;
        }*/

		// Tower - WARNING: MUST BE LAST THING TO RUN IN THIS RUN() FUNCTION
		/*if (!this.towerWarning && date.getMinuteOfDay() % 60 == 25) {
			while (!this.generateTowerLocation()) {} // Keep doing it until we get a good tower generation, FUCK THE TPS

			Gberry.broadcastMessage(ChatColor.GREEN + "New Insanity Tower Spawning at " + this.getxTowerLocation() + ", " + this.getyTowerLocation() + ", " + this.getzTowerLocation() + " in 5 minutes.");
			EventStateChangeEvent event = new EventStateChangeEvent("Tower", false);
			this.plugin.getServer().getPluginManager().callEvent(event);
		} else if (!this.tower && date.getMinuteOfDay() % 60 == 30) {
			// Hack time yo
			if (this.getxTowerLocation() == 0 && this.getyTowerLocation() == 0 && this.getzTowerLocation() == 0) {
				while (!this.generateTowerLocation()) {} // Keep doing it until we get a good tower generation, FUCK THE TPS
			}

			// Start the tower event
			this.towerWarning = false;
			new TowerBuilderTask(this.plugin).runTask(this.plugin);
			this.tower = true;
		} else if (this.tower && date.getMinuteOfDay() % 60 == 50) {
			// Too long, despawn
			this.tower = false;
			Tower tower = this.plugin.getTower();
			if (tower != null) {
				tower.despawn();
				EventStateChangeEvent event = new EventStateChangeEvent("Tower", false);
				this.plugin.getServer().getPluginManager().callEvent(event);
			}
		}*/ /*else if (this.tower) {
			Tower tower = this.plugin.getTower();
			if (tower != null) {
				for (ItemStack stack : tower.getChest().getBlockInventory().getContents()) {
					if (stack != null && !stack.getType().equals(Material.AIR)) {
						// WARNING: MUST BE LAST THING TO RUN IN THIS RUN() FUNCTION
						return; // nope
					}
				}

				// Despawn
				this.tower = false;
				this.plugin.getTower().despawn();
				EventStateChangeEvent event = new EventStateChangeEvent("Tower", false);
				this.plugin.getServer().getPluginManager().callEvent(event);
			}
		}*/

	}

	private boolean generateTowerLocation() {
		this.towerWarning = true;
		World world = this.plugin.getServer().getWorld("world");
		int x = this.plugin.generateRandomInt(this.plugin.getWarZoneMinX(), this.plugin.getWarZoneMaxX());
		int z = this.plugin.generateRandomInt(this.plugin.getWarZoneMinZ(), this.plugin.getWarZoneMaxZ());

		/*  (+ z)
		 *  ^       ^>
		 *  ^     ^>
		 *  ^   ^>
		 *  ^ ^>
		 *  P > > > > > (- x)
		 *
		 * P = Location we generate
		 * Arrows represent the way the schematic is pasted
		 */

		// Check borders of 16x16 area
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				// We don't care about the middle
				if (i != 0 && i != 15 && j != 0 && j != 15)  {
					continue;
				}

				int x2 = x - i;
				int z2 = z + j;
				int y2 = world.getHighestBlockYAt(x2, z2) - 1;


				ProtectedRegion region = this.plugin.getgGuardPlugin().getProtectedRegion(world.getBlockAt(x2, y2, z2).getLocation(),
						this.plugin.getgGuardPlugin().getProtectedRegions());

				// Return false if region isn't warzone
				if (region != null && !region.getRegionName().equalsIgnoreCase("warzone")) {
					return false;
				}
			}
		}

		// Update X/Y/Z
		this.xTowerLocation = x;
		this.yTowerLocation = this.plugin.getServer().getWorld("world").getHighestBlockYAt(x, z) - 1;
		this.zTowerLocation = z;

		return true;
	}

	private void restartServer() {
        AbuseListener.ignoreCombatLogging = true;

		// Lazy hackish way for now to update PVPProtTime
		/*for (final Player player : this.plugin.getServer().getOnlinePlayers())
			if (this.plugin.getMapNameToJoinTime().containsKey(player.getUniqueId().toString()) &&
					this.plugin.getMapNameToPvPTimeRemaining().containsKey(player.getUniqueId().toString())) {
				final int timeRemaining = this.plugin.getMapNameToPvPTimeRemaining().get(player.getUniqueId().toString());
				final long timeJoined = this.plugin.getMapNameToJoinTime().get(player.getUniqueId().toString());
				final long currentTime = System.currentTimeMillis();

				// Ok they are still protected...update their time in the DB
				if ((timeJoined + timeRemaining) > currentTime) {
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

						@Override
						public void run() {
							// Update DB
							plugin.updateProtection(player, (timeRemaining - (currentTime - timeJoined)));
						}
					});
				} else {
					// Their PVP protection is over, time to remove from the system
					this.plugin.getMapNameToPvPTimeRemaining().remove(player.getUniqueId().toString());
					this.plugin.getMapNameToJoinTime().remove(player.getUniqueId().toString());

					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

						@Override
						public void run() {
							// Purge from DB
							plugin.removeProtection(player);
						}
					});
				}
			}*/
		
		// Throw everyone onto the lobby server
//		ByteArrayOutputStream b = new ByteArrayOutputStream();
//		DataOutputStream out = new DataOutputStream(b);
//		try {
//			out.writeUTF("Connect");
//			out.writeUTF("lobby");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		for (Player player : this.plugin.getServer().getOnlinePlayers()) {
//			player.sendPluginMessage(this.plugin, "BungeeCord", b.toByteArray());
//		}
//
//		// Wait for MySQL to clean up a few seconds...
//		try {
//		    Thread.sleep(5000);
//		} catch(InterruptedException ex) {
//		    Thread.currentThread().interrupt();
//		}
		
		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "stop");
	}

	public boolean isTower() {
		return tower;
	}

	public void setTower(boolean tower) {
		this.tower = tower;
	}

	public boolean isTowerWarning() {
		return towerWarning;
	}

	public void setTowerWarning(boolean towerWarning) {
		this.towerWarning = towerWarning;
	}

	public int getxTowerLocation() {
		return xTowerLocation;
	}

	public void setxTowerLocation(int xTowerLocation) {
		this.xTowerLocation = xTowerLocation;
	}

	public int getyTowerLocation() {
		return yTowerLocation;
	}

	public void setyTowerLocation(int yTowerLocation) {
		this.yTowerLocation = yTowerLocation;
	}

	public int getzTowerLocation() {
		return zTowerLocation;
	}

	public void setzTowerLocation(int zTowerLocation) {
		this.zTowerLocation = zTowerLocation;
	}

	public static void addCheckTimeComponent(CheckTimeComponent component) {
		CheckTimeTask.checkTimeComponents.add(component);
	}

	public interface CheckTimeComponent {

		void run(CheckTimeTask task);

	}

}
