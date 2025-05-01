package net.badlion.skywarslobby;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.skywarslobby.commands.LeaveCommand;
import net.badlion.skywarslobby.inventories.GameQueueInventory;
import net.badlion.skywarslobby.inventories.KitSelectionInventory;
import net.badlion.skywarslobby.listeners.LobbyListener;
import net.badlion.skywarslobby.managers.KitCreationManager;
import net.badlion.skywarslobby.managers.SkyWarsKitManager;
import net.badlion.skywarslobby.tasks.CheckQueueStatusTask;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.smellyparticles.ParticleInventory;
import net.badlion.smellypets.PetInventory;
import net.badlion.smellypets.SmellyPets;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class SkyWarsLobby extends JavaPlugin {

    private static SkyWarsLobby plugin;

    private Location spawnLocation;

	private Set<Player> hidingPlayers = new HashSet<>();

    public SkyWarsLobby() {
        SkyWarsLobby.plugin = this;
    }

    @Override
    public void onEnable() {
	    Gberry.enableAsyncLoginEvent = true;
        Gberry.enableAsyncQuitEvent = true;

        this.spawnLocation = new Location(this.getServer().getWorld("world"), 0.5, 152, 0.5, 180, 0);

        this.getServer().getPluginManager().registerEvents(new LobbyListener(), this);

        this.getCommand("leave").setExecutor(new LeaveCommand());

        new CheckQueueStatusTask().runTaskTimerAsynchronously(this, 20, 20);

        BukkitUtil.initialize(this);
        SmellyInventory.initialize(this, false);
        GameQueueInventory.initialize();
	    KitCreationManager.initialize();
	    SkyWarsKitManager.initialize();
	    KitSelectionInventory.initialize();

	    // Disable pets for now
	    SmellyPets.getInstance().disallowPets();
    }

    @Override
    public void onDisable() {
        CheckQueueStatusTask.flushPlayers();
    }

	public static SkyWarsLobby getInstance() {
		return SkyWarsLobby.plugin;
	}

	public void teleportToSpawnAndGiveItems(Player player) {
		player.teleport(this.spawnLocation);

		player.getInventory().clear();
		player.getInventory().setArmorContents(null);

		player.getInventory().setItem(0, ItemStackUtil.createItem(Material.COMPASS, ChatColor.AQUA + "Join a Game"));
		player.getInventory().setItem(2, ItemStackUtil.createItem(Material.BOOK, ChatColor.AQUA + "Kit Creation"));
		player.getInventory().setItem(4, ItemStackUtil.createItem(Material.REDSTONE_COMPARATOR, ChatColor.AQUA + "Hide/Show Players"));

		// Hotbar items for pets/particles
		player.getInventory().setItem(7, ParticleInventory.getOpenParticleInventoryItem());
		player.getInventory().setItem(8, PetInventory.getOpenPetInventoryItem());

		player.updateInventory();
	}

	public boolean isHidingPlayers(Player player) {
		return this.hidingPlayers.contains(player);
	}

	public void toggleHidingPlayers(Player player) {
		this.toggleHidingPlayers(player, true);
	}

	public void toggleHidingPlayers(Player player, boolean verbose) {
		if (this.hidingPlayers.contains(player)) {
			for (Player pl : SkyWarsLobby.getInstance().getServer().getOnlinePlayers()) {
				player.showPlayer(pl);
			}

			this.hidingPlayers.remove(player);

			if (verbose) {
				player.sendMessage(ChatColor.YELLOW + "Now showing players");
			}
		} else {
			for (Player pl : SkyWarsLobby.getInstance().getServer().getOnlinePlayers()) {
				player.hidePlayer(pl);
			}

			this.hidingPlayers.add(player);

			if (verbose) {
				player.sendMessage(ChatColor.YELLOW + "Now hiding players");
			}
		}
	}

	public Set<Player> getHidingPlayers() {
		return hidingPlayers;
	}

	public Location getSpawnLocation() {
        return spawnLocation;
    }

}
