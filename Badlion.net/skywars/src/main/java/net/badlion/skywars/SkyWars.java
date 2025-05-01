package net.badlion.skywars;

import net.badlion.gberry.Gberry;
import net.badlion.ministats.MiniStats;
import net.badlion.mpg.MPG;
import net.badlion.skywars.commands.PlayAgainCommand;
import net.badlion.skywars.gamemodes.ClassicGamemode;
import net.badlion.skywars.gamemodes.OverPoweredGamemode;
import net.badlion.skywars.listeners.MPGListener;
import net.badlion.skywars.listeners.MiniStatsListener;
import net.badlion.skywars.listeners.SkyWarsSpectatorListener;
import net.badlion.skywars.listeners.WorldListener;
import net.badlion.skywars.tasks.KeepAliveTask;
import net.badlion.smellymapvotes.SmellyMapVotes;
import net.badlion.smellymapvotes.VoteManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SkyWars extends JavaPlugin {

    private static SkyWars plugin;

    public static SkyWars getInstance() {
        return SkyWars.plugin;
    }

    // TODO Set lobby location
    private Location lobbyLocation;

    // GameModes need to be created somewhere
    public static final ClassicGamemode classicGamemode = new ClassicGamemode();
    public static final OverPoweredGamemode overPoweredGamemode = new OverPoweredGamemode();
    private boolean isFFA = true;
    private String gameModeString = "Classic";

    public SkyWars() {
        SkyWars.plugin = this;

        MPG.MPG_GAME_NAME = "SkyWars";
        MPG.ALLOW_SPECTATING = true;
        MPG.MPG_PREFIX = ChatColor.AQUA + "[" + ChatColor.RED + "SW" + ChatColor.AQUA + "] ";

        MiniStats.TAG = "SkyWars";
        MiniStats.TABLE_NAME = SkyWars.getInstance().getCurrentGame().getGamemode().getName().equals("Classic") ? "swcffa_ministats" : "swopffa_ministats";

        MPG.SERVER_ON_END = "swlobby1"; //+ (new Random().nextInt(5) + 1);
    }

    @Override
    public void onEnable() {
        // Have to do after Gberry is loaded
        if (Gberry.serverName.contains("eu")) {
            MPG.SERVER_ON_END = "eu" + MPG.SERVER_ON_END;
        }

        this.saveDefaultConfig();

//        getConfig().addDefault("MaxPlayers", 12);
        getConfig().addDefault("LobbyLocation", new Location(Bukkit.getWorld("world"), 0, 0, 0, 0, 0));
        getConfig().options().copyDefaults(true);

        this.lobbyLocation = (Location) getConfig().get("LobbyLocation");

        this.getServer().getPluginManager().registerEvents(new MiniStatsListener(), this);
        this.getServer().getPluginManager().registerEvents(new MPGListener(), this);
        this.getServer().getPluginManager().registerEvents(new SkyWarsSpectatorListener(), this);
        this.getServer().getPluginManager().registerEvents(new WorldListener(), this);

        if (!Gberry.serverName.contains("test")) {
            new KeepAliveTask().runTaskTimerAsynchronously(this, 60, 60);
        }

        this.getCommand("playagain").setExecutor(new PlayAgainCommand());

        // TODO: This needs to be moved based on which server
        Gberry.coudhDBDatabase = "sw_beta";
        this.gameModeString = SkyWars.getInstance().getConfig().getString("gamemode", "Classic");

        SmellyMapVotes.getInstance().setServerType(VoteManager.ServerType.SKYWARS);
    }

    @Override
    public void onDisable() {

    }

    public void checkForGamePlayers() {
        new BukkitRunnable() {
            public void run() {
                // TODO
            }
        }.runTaskTimer(SkyWars.getInstance(), 20, 20);
    }

    public SkyGame getCurrentGame() {
        return (SkyGame) MPG.getInstance().getMPGGame();
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public boolean isFFA() {
        return isFFA;
    }

    public String getGamemodeString() {
        return gameModeString;
    }

    public void setGameModeString(String gameModeString) {
        this.gameModeString = gameModeString;
    }
}
