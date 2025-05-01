package net.badlion.skywars;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.FireWorkUtil;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.MPGTeam;
import net.badlion.mpg.gamemodes.Gamemode;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.managers.MPGTeamManager;
import net.badlion.mpg.tasks.PreGameCountdownTask;
import net.badlion.skywars.exceptions.ChestNotFoundException;
import net.badlion.skywars.listeners.SkyWarsGameListener;
import net.badlion.skywars.listeners.SkyWarsPreGameListener;
import net.badlion.skywars.tasks.SkyWarsGameTimeTask;
import net.badlion.skywars.tasks.deathmatch.DeathMatchDamageTask;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SkyGame extends MPGGame {

    private SkyWarsPreGameListener skyWarsPreGameListener;
    private SkyWarsGameListener skyWarsGameListener;

    public static int NUM_OF_SECONDS_TILL_START = 10;

    private Set<Inventory> tier1Chests = new HashSet<>();
    private Set<Inventory> tier2Chests = new HashSet<>();

    private Map<MPGTeam, Integer> spawnsGivenForTeam = new HashMap<>();

    private int deathmatchStartTime = MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.DEATH_MATCH_START_TIME)
            + MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.DEATH_MATCH_TELEPORT_COUNTDOWN_TIME);

    public SkyGame(SkyWorld world) {
        super(world);

        // Turn on tile drops
        this.getGWorld().getBukkitWorld().setGameRuleValue("doTileDrops", "true");

        this.gamemode = Gamemode.getGamemode(SkyWars.getInstance().getGamemodeString());

        // Load Chunks
        for (Location spawn : this.getWorld().getSpawnLocations()) {
            for (int x = spawn.getChunk().getX() - 10; x <= spawn.getChunk().getX() + 10; x++) {
                for (int z = spawn.getChunk().getX() - 10; z <= spawn.getChunk().getX() + 10; z++) {
                    this.getGWorld().getBukkitWorld().loadChunk(x, z);
                }
            }
        }

        // Fix the region stuff for SkyWars
        this.region.setAllowBrokenBlocks(true);
        this.region.setAllowCreatureSpawn(true);
        this.region.setAllowCreeperBlockDamage(true);
        this.region.setAllowFire(true);
        this.region.setAllowFireIgniteByPlayer(true);
        this.region.setAllowIceMelt(false);
        this.region.setAllowPistonUsage(false);
        this.region.setAllowPlacedBlocks(true);
        this.region.setAllowTNTBlockDamage(true);
        this.region.setChangeMobDamageToPlayer(false);
        this.region.setAllowBlockMovement(true);
        this.region.setAllowedBucketPlacements(true);
        this.region.setAllowEnderPearls(true);
        this.region.setAllowEndermanMoveBlocks(false);
        this.region.setAllowPlantGrowth(false);
        this.region.setAllowPlantSpread(false);
        this.region.setAllowHangingItems(false);
        this.region.setAllowItemInteraction(true);
        this.region.setAllowChestInteraction(true);
        this.region.setAllowBlockChangesByEntities(false);
        this.region.setAllowLeafDecay(false);
        this.region.setOverrideChestUsage(true);
        this.region.setHealPlayers(false);
        this.region.setFeedPlayers(false);
        this.region.setAllowPotionEffects(true);

        // TODO: Tell API we are ready to accept people
    }

    // Override to have our extra stuff handled
    public void createGameTasks() {
        new SkyWarsGameTimeTask().runTaskTimer(SkyWars.getInstance(), 20L, 20L);
    }

    public void fillChests() {
        for (Location location : this.getWorld().getTier1ChestLocations()) {
            try {
                if (location.getBlock().getType() != Material.CHEST) {
                    throw new ChestNotFoundException("Invalid chests in config " + this.getGWorld().getInternalName());
                }

                this.addToInventory(this.tier1Chests, location);
            } catch (ChestNotFoundException e) {
                Bukkit.getLogger().info("Found type " + location.getBlock().getType());
                Bukkit.getLogger().info("Unable to find tier 1 chest at " + location.toString());
                //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
                //return;
            }
        }

        for (Location location : this.getWorld().getTier2ChestLocations()) {
            try {
                if (location.getBlock().getType() != Material.ENDER_CHEST && location.getBlock().getType() != Material.CHEST) {
                    throw new ChestNotFoundException("Invalid chests in config " + this.getGWorld().getInternalName());
                }

                location.getBlock().setType(Material.CHEST);
                this.addToInventory(this.tier2Chests, location);
            } catch (ChestNotFoundException e) {
                Bukkit.getLogger().info("Found type " + location.getBlock().getType());
                Bukkit.getLogger().info("Unable to find tier 2 chest at " + location.toString());
                //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
                //return;
            }
        }
    }

    @Override
    public void preGame() {
        // Call after so the API can find out what we are doing
        this.fillChests();

        this.skyWarsPreGameListener = new SkyWarsPreGameListener();
        SkyWars.getInstance().getServer().getPluginManager().registerEvents(this.skyWarsPreGameListener, SkyWars.getInstance());

        // Spawn glass and start the countdown
        this.getWorld().generateGlassAroundSpawns();

        // Testing only
        if (Gberry.serverName.contains("test")) {
            // Teleport to spawns
            Iterator<Location> it = this.getWorld().getSpawnLocations().iterator();
            for (MPGTeam mpgTeam : MPGTeamManager.getAllMPGTeams()) {
                // Error check
                if (!it.hasNext()) {
                    Bukkit.getLogger().info("Invalid number of spawns for " + this.getGWorld().getInternalName());
                    Gberry.broadcastMessageNoBalance("Error loading spawns, restarting server.");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
                }

                mpgTeam.teleport(it.next());

                for (UUID uuid : mpgTeam.getUUIDs()) {
                    MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(uuid);
                    Player pl = mpgPlayer.getPlayer();
                    if (pl != null) {
                        // Set their inventory
                        pl.getInventory().clear();
                        pl.getInventory().addItem(this.skyWarsPreGameListener.getBookItem());
                        pl.updateInventory();
                    }
                }
            }
        }

        final List<MPGTeam> teams = MPGTeamManager.getAllMPGTeams();

        final Iterator it = teams.iterator();

        final Map<UUID, Location> playerLocations = new HashMap<>();

        // Teleport one player per pick
        new BukkitRunnable() {
            private int i = 0;

            @Override
            public void run() {
                if (!it.hasNext()) {
                    this.cancel();
                    return;
                }

                MPGTeam team = (MPGTeam) it.next();
                Location spawnLocation = SkyGame.this.getWorld().getSpawnLocation(this.i);
                for (UUID player : team.getUUIDs()) {
                    playerLocations.put(player, spawnLocation);
                }

                this.i++;
            }
        }.runTaskTimer(SkyWars.getInstance(), 0L, 1L);

        // Fill Chests
        for (Inventory inventory : this.tier1Chests) {
            this.fillChest(inventory, 1);
        }

        for (Inventory inventory : this.tier2Chests) {
            this.fillChest(inventory, 2);
        }

        // Start countdown task
        new PreGameCountdownTask(playerLocations).runTaskTimer(MPG.getInstance(), 0, 20);
    }

    @Override
    public void startGame() {
        // Disable old listener
        this.skyWarsPreGameListener.unregister();

        // Add listener
        this.skyWarsGameListener = new SkyWarsGameListener();
        SkyWars.getInstance().getServer().getPluginManager().registerEvents(this.skyWarsGameListener, SkyWars.getInstance());

        this.getWorld().removeGlass();

        Bukkit.broadcastMessage(MPG.MPG_PREFIX + ChatColor.AQUA + "The game has begun!");
        Gberry.broadcastSound(Sound.NOTE_PLING, 1, 1);

        // TODO: TESTING ONLY
        //this.getWorld().destroyIslands();

        for (Player pl : Bukkit.getOnlinePlayers()) {
            MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(pl.getUniqueId());
            if (mpgPlayer.getState() != MPGPlayer.PlayerState.PLAYER) {
                continue;
            }

            BukkitUtil.closeInventory(pl);

            // Load kit
            if (mpgPlayer.getKit() != null) {
                mpgPlayer.getKit().load(pl);
            } else {
                this.getGamemode().getDefaultKit().load(pl);
            }
        }
    }

    @Override
    public boolean checkForEndGame() {
        int teamsLeft = 0;
        List<MPGTeam> teams = MPGTeamManager.getAllMPGTeams();

        MPGTeam lastTeam = null;
        for (MPGTeam team : teams) {
            if (team.getDeaths() == team.getUUIDs().size()) {
                continue;
            }

            lastTeam = team;
            ++teamsLeft;
        }

        if (lastTeam == null) {
            return false;
        }

        // Keep track of winners
        if (teamsLeft <= 1) {
            for (UUID uuid : lastTeam.getUUIDs()) {
                this.addWinner(uuid);
            }
        }

        return teamsLeft <= 1;
    }

    @Override
    public void endGame() {
        super.endGame();

        // Yay fireworks
        for (Location location : this.getWorld().getSpawnLocations()) {
            FireWorkUtil.shootFirework(location);
        }
    }

    @Override
    public void deathMatch() {
        DeathMatchDamageTask.start(20);
    }

    @Override
    public SkyWorld getWorld() {
        return (SkyWorld) this.world;
    }

    public Set<Inventory> getTier1Chests() {
        return tier1Chests;
    }

    public Set<Inventory> getTier2Chests() {
        return tier2Chests;
    }

    public int getDeathmatchStartTime() {
        return deathmatchStartTime;
    }

}
