package net.kohi.vaultbattle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.badlion.cosmetics.Cosmetics;
import net.kohi.combattracker.CombatTrackerPlugin;
import net.kohi.combattracker.type.UsernamePlayerFormatter;
import net.kohi.combattracker.type.damage.PlayerDamage;
import net.kohi.vaultbattle.command.AdminCommand;
import net.kohi.vaultbattle.command.AntiGriefCommands;
import net.kohi.vaultbattle.gson.LocationTypeAdapter;
import net.kohi.vaultbattle.listener.*;
import net.kohi.vaultbattle.manager.*;
import net.kohi.vaultbattle.menu.SpectatePlayersMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

public class VaultBattlePlugin extends JavaPlugin {

    private static VaultBattlePlugin plugin;

    private static Gson gson;

    private Scoreboard scoreboard;

    private GameManager gameManager;

    private GameMapManager gameMapManager;

    private TeamManager teamManager;

    private PlayerDataManager playerDataManager;

    private AntiGriefManager antiGriefManager;

    private WorldEditPlugin worldEdit;

    private CommandsManager<CommandSender> commands;

    private CombatTrackerPlugin combatTrackerPlugin;

    private int startingPlayers;

    public static VaultBattlePlugin getPlugin() {
        return VaultBattlePlugin.plugin;
    }

    public static Gson getGson() {
        return VaultBattlePlugin.gson;
    }

    @Override
    public void onEnable() {
        plugin = this;

        startingPlayers = getConfig().getInt("startingPlayers", 6);

        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard(); // TODO: unfuck scoreboards in gspigot
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }
        gson = new GsonBuilder()
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .enableComplexMapKeySerialization().setPrettyPrinting()
                .create();
        worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        combatTrackerPlugin = (CombatTrackerPlugin) getServer().getPluginManager().getPlugin("combattracker");
        combatTrackerPlugin.setPlayerNameFormatter(new UsernamePlayerFormatter() {
            @Override
            public String format(Player player) {
                if (getPlayerDataManager().get(player).getTeam() != null) {
                    return getPlayerDataManager().get(player).getTeam().getColor().toChatColor() + player.getName();
                } else {
                    return player.getName();
                }
            }

            @Override
            public String format(PlayerDamage player) {
                if (getPlayerDataManager().get(player.getPlayer()).getTeam() != null) {
                    return getPlayerDataManager().get(player.getPlayer()).getTeam().getColor().toChatColor() + player.getName();
                } else {
                    return player.getName();
                }
            }
        });

        gameManager = new GameManager(this);

        gameMapManager = new GameMapManager(this);
        gameMapManager.load();
        gameMapManager.purgeOldWorlds();

        antiGriefManager = new AntiGriefManager(this);

        teamManager = new TeamManager(this);
        teamManager.load();

        playerDataManager = new PlayerDataManager(this);

        setupCommands();

        getServer().getPluginManager().registerEvents(new JoinLeaveRespawnListener(this), this);
        getServer().getPluginManager().registerEvents(new PingListener(this), this);
        getServer().getPluginManager().registerEvents(new BankListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        getServer().getPluginManager().registerEvents(new SoulboundListener(this), this);
        getServer().getPluginManager().registerEvents(new AntiGriefListener(this), this);

        // Disable all but particle for now
        // TODO: add toggle to cosmetics plugin to prevent adding shit to hotbar
        Cosmetics.getInstance().disallowCosmetics();
        Cosmetics.getInstance().setCosmeticEnabled(Cosmetics.CosmeticType.PARTICLE, true);

        new MCPTask(this).runTaskTimer(this, 100, 100);
    }


    public void setupCommands() {
        commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender sender, String perm) {
                return sender.hasPermission(perm);
            }
        };

        CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, commands);

        commands.setInjector(new SimpleInjector(this));
        cmdRegister.register(AdminCommand.class);
        cmdRegister.register(AntiGriefCommands.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            commands.execute(command.getName(), args, sender, sender);
        } catch (CommandPermissionsException ex) {
            sender.sendMessage(ChatColor.RED + "Permission denied.");
        } catch (MissingNestedCommandException ex) {
            sender.sendMessage(ChatColor.RED + ex.getUsage());
        } catch (CommandUsageException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
            sender.sendMessage(ChatColor.RED + ex.getUsage());
        } catch (CommandException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
        }
        return true;
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }

    public GameMapManager getGameMapManager() {
        return this.gameMapManager;
    }

    public TeamManager getTeamManager() {
        return this.teamManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return this.playerDataManager;
    }

    public AntiGriefManager getAntiGriefManager() {
        return this.antiGriefManager;
    }

    public WorldEditPlugin getWorldEdit() {
        return this.worldEdit;
    }

    public CommandsManager<CommandSender> getCommands() {
        return this.commands;
    }

    public CombatTrackerPlugin getCombatTrackerPlugin() {
        return this.combatTrackerPlugin;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public int getStartingPlayers() {
        return startingPlayers;
    }
}
