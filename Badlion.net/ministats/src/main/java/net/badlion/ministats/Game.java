package net.badlion.ministats;

import io.nv.bukkit.CleanroomGenerator.CleanroomChunkGenerator;
import net.badlion.gguard.GGuard;
import net.badlion.gguard.ProtectedRegion;
import net.badlion.gguard.UnsafeLocation;
import net.badlion.worldrotator.GWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Game {

	private long startTime = System.currentTimeMillis();
	private long endTime;

    private GWorld gWorld;
	private Location spawnLocation;

    protected ProtectedRegion region;

    private Set<UUID> winners = new HashSet<>();

	//private TeamAutoBalanceTask teamAutoBalanceTask;
	//private GameTimeCheckerTask gameTimeCheckerTask;
	//private DisplayGameStartingBarTask displayGameStartingBarTask;

    public Game() {

    }

	public Game(GWorld gWorld) {
        this.gWorld = gWorld;

		// Load world
		this.loadGWorld();
	}

	private void loadGWorld() {
		// Handle world loading
		WorldCreator wc = new WorldCreator(this.gWorld.getInternalName());
		wc.generator(new CleanroomChunkGenerator("."));
		World world = MiniStats.getInstance().getServer().createWorld(wc);
		world.setAnimalSpawnLimit(0);
		world.setMonsterSpawnLimit(0);
		world.setTime(6000);
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setGameRuleValue("doMobSpawning", "false");
		world.setGameRuleValue("doTileDrops", "false");
		world.setGameRuleValue("doFireTick", "true");
		world.setGameRuleValue("keepInventory", "false");
		world.setGameRuleValue("naturalRegeneration", "true");
		world.getEntities().clear();

		this.gWorld.setBukkitWorld(world);

		// Setup spawn location if config file exists
		if (this.gWorld.getYml() != null) {
			this.spawnLocation = new Location(world, this.gWorld.getYml().getDouble("spawn_location_x"),
					this.gWorld.getYml().getDouble("spawn_location_y"), this.gWorld.getYml().getDouble("spawn_location_z"));
		}

		this.region = new ProtectedRegion(world.getName(), new UnsafeLocation(world.getName(), -10000, 0, -10000),
				new UnsafeLocation(world.getName(), 10000, 256, 10000));
		this.region.setAllowBrokenBlocks(false);
		this.region.setAllowCreatureSpawn(true);
		this.region.setAllowCreeperBlockDamage(false);
		this.region.setAllowFire(true);
		this.region.setAllowFireIgniteByPlayer(true);
		this.region.setAllowIceMelt(false);
		this.region.setAllowPistonUsage(false);
		this.region.setAllowPlacedBlocks(false);
		this.region.setAllowTNTBlockDamage(false);
		this.region.setChangeMobDamageToPlayer(false);
		this.region.setAllowBlockMovement(true);
		this.region.setAllowedBucketPlacements(true);
		this.region.setDamageMultiplier(0.5);
		this.region.setAllowEnderPearls(true);
		this.region.setAllowEndermanMoveBlocks(false);
		this.region.setAllowPlantGrowth(false);
		this.region.setAllowPlantSpread(false);
		this.region.setAllowFireSpread(false);
		this.region.setAllowHangingItems(false);
		this.region.setAllowItemInteraction(true);
		this.region.setAllowChestInteraction(true);
		this.region.setAllowBlockChangesByEntities(false);
		this.region.setAllowLeafDecay(false);
		this.region.setOverrideChestUsage(true);
		this.region.setHealPlayers(false);
		this.region.setFeedPlayers(false);
		this.region.setAllowPotionEffects(true);
		this.region.setAllowPlayerPickupItems(true);
		this.region.setAllowPVP(true);

		GGuard.getInstance().addProtectedRegion(this.region);
	}

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public GWorld getGWorld() {
        return gWorld;
    }

	protected void setGWorld(GWorld gWorld) {
		this.gWorld = gWorld;

		// Load world
		this.loadGWorld();
	}

	public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean containsWinner(UUID uuid) {
        return this.winners.contains(uuid);
    }

    public void addWinner(UUID uuid) {
        this.winners.add(uuid);
    }

    public void clearWinners() {
        this.winners = new HashSet<>();
    }

    public Set<UUID> getWinners() {
        return Collections.unmodifiableSet(this.winners);
    }

}
