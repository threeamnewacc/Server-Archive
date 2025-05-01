package net.badlion.ffa;

import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.kits.KitType;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.ffa.commands.SpawnCommand;
import net.badlion.ffa.gamemodes.NoDebuffGamemode;
import net.badlion.ffa.gamemodes.SGGamemode;
import net.badlion.ffa.gamemodes.UHCGamemode;
import net.badlion.ffa.inventories.FFAStatsInventory;
import net.badlion.ffa.listeners.AlivePlayerListener;
import net.badlion.ffa.listeners.GlobalListener;
import net.badlion.ffa.listeners.MPGListener;
import net.badlion.ffa.listeners.MultiKillListener;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.ministats.MiniStats;
import net.badlion.ministats.MiniStatsPlayerCreator;
import net.badlion.ministats.managers.DatabaseManager;
import net.badlion.mpg.MPG;
import net.badlion.mpg.commands.StatsCommand;
import net.badlion.mpg.gamemodes.Gamemode;
import net.badlion.mpg.managers.MPGKitManager;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class FFA extends JavaPlugin {

	public static String FFA_NAME;
	public static String FFA_TYPE;
	public static Gamemode FFA_GAMEMODE;
	public static KitRuleSet FFA_KITRULESET;

	public static String WORLD_NAME;

    private static FFA plugin;

    public FFA() {
	    FFA.plugin = this;

	    MPG.MPG_GAME_NAME = "BadlionFFA";

	    MPG.MPG_PREFIX = ChatColor.GOLD + "" + ChatColor.BOLD + "[" + ChatColor.DARK_AQUA + "FFA" + ChatColor.GOLD + "" + ChatColor.BOLD + "] ";

	    // No deathmatch
	    MPG.HAS_DEATHMATCH = false;

	    // Set matchmaking to false
	    MPG.USES_MATCHMAKING = false;

	    // Configure game
	    MPG.GAME_TYPE = MPG.GameType.FFA.setup(true, true, true, true, false, false);

	    // These aren't used because this is an FFA, but have to set them to some value
	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.PLAYERS_TO_START.name(), 2);
	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.MAX_PLAYERS.name(), Integer.MAX_VALUE);
	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.START_COUNTDOWN_SECONDS.name(), 30);

	    // Set this to true because we kinda use it?
	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.USES_KITS.name(), true);

	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.HEALTH_UNDER_NAME.name(), false);
	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DEAD_ENTITY_ON_DEATH.name(), false);
	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DROP_ITEMS_ON_DEATH.name(), true);
	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.KILL_COMBAT_LOGGER_ON_TIMEOUT.name(), false);

	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.MAX_DISCONNECT_LENGTH.name(), 30);
	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.MAX_NUM_OF_DISCONNECTS.name(), Integer.MAX_VALUE);

	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.SPECTATOR_ON_DEATH.name(), false);
	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.ALLOW_LEAVE_SPECTATOR.name(), true);
	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.DISABLE_SPECTATOR_ON_JOIN.name(), true);
	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.USE_SKULL_SPECTATOR_INVENTORY.name(), false);
	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.SPECTATOR_ON_LOGIN_AFTER_START.name(), false);

	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.KICK_NON_DONATORS_IF_FULL.name(), false);
	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.REBOOT_ON_GAME_END.name(), false);

	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.RESPAWN_TIME.name(), 0);
	    MPG.getInstance().getConfigurator().updateOption(MPG.ConfigFlag.RESPAWN_RESISTANCE_TIME.name(), 0);

	    // Save default config
	    this.saveDefaultConfig();

	    // Load config file manually
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/FFA/config.yml"));

	    // Get FFA type
	    FFA.FFA_TYPE = config.getString("ffa_type").toLowerCase();

	    // Set proper FFA world name
	    FFA.WORLD_NAME = FFA.FFA_TYPE + "ffaworld";

	    // Configure ministats
	    MiniStats.SEASON = 1;
	    MiniStats.TAG = FFA.FFA_TYPE.toUpperCase() + "FFA";
	    MiniStats.TABLE_NAME = FFA.FFA_TYPE + "_ffa_s1_ministats";
    }

    @Override
    public void onEnable() {
	    // Set the stats inventory
	    StatsCommand.getInstance().setStatsInventory(new FFAStatsInventory());

	    // Set ministats player creator to the default creator
	    MiniStats.getInstance().setMiniStatsPlayerCreator(new MiniStatsPlayerCreator.DefaultMiniStatsPlayerCreator());

	    // Register commands
	    this.getCommand("spawn").setExecutor(new SpawnCommand());

	    // Register event listeners
        this.getServer().getPluginManager().registerEvents(new AlivePlayerListener(), this);
	    this.getServer().getPluginManager().registerEvents(new GlobalListener(), this);
	    this.getServer().getPluginManager().registerEvents(new MPGListener(), this);
	    this.getServer().getPluginManager().registerEvents(new MultiKillListener(), this);

	    // Initialize proper gamemode and ruleset
	    switch (FFA.FFA_TYPE) {
		    case "nodebuff":
			    FFA.FFA_NAME = "NoDebuff";
			    FFA.FFA_GAMEMODE = new NoDebuffGamemode();
			    FFA.FFA_KITRULESET = KitRuleSet.noDebuffRuleSet;
			    break;
		    case "sg":
			    FFA.FFA_NAME = "SG";
			    FFA.FFA_GAMEMODE = new SGGamemode();
			    FFA.FFA_KITRULESET = KitRuleSet.sgRuleSet;
			    break;
		    case "uhc":
			    FFA.FFA_NAME = "UHC";
			    FFA.FFA_GAMEMODE = new UHCGamemode();
			    FFA.FFA_KITRULESET = KitRuleSet.uhcRuleSet;
			    break;
		    default:
			    throw new RuntimeException("Unrecognized FFA TYPE in configuration file for FFA: " + FFA.FFA_TYPE);
	    }

	    // Register gamemode listener
	    this.getServer().getPluginManager().registerEvents(FFA.FFA_GAMEMODE, this);
    }

    @Override
    public void onDisable() {
	    Connection connection = null;
	    try {
		    connection = Gberry.getUnsafeConnection();

		    for (Player player : this.getServer().getOnlinePlayers()) {
			    final FFAPlayer ffaPlayer = (FFAPlayer) MPGPlayerManager.getMPGPlayer(player.getUniqueId());

			    // Track time played
			    long totalTimePlayed = (System.currentTimeMillis() - ffaPlayer.getStartTime()) / 1000;
			    ffaPlayer.addTotalTimePlayed(totalTimePlayed);

			    // Force save stats for all players on main thread
			    DatabaseManager.savePlayerData(ffaPlayer, connection);
		    }

	    } catch (SQLException e) {
		    e.printStackTrace();
	    } finally {
		    Gberry.closeComponents(connection);
	    }
    }

	public void prepPlayerForSpawn(Player player) {
		// Heal player
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setSaturation(20);
		player.setExhaustion(0);

		player.setExp(0);
		player.setLevel(0);
		player.setTotalExperience(0);
		player.setFireTicks(0);
		player.setArrowsStuck(0);

		player.setGameMode(GameMode.SURVIVAL);
		player.spigot().setCollidesWithEntities(true);

		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}

		// Clear inventory
		player.getInventory().setArmorContents(null);
		player.getInventory().clear();
		player.setItemOnCursor(null);
		player.getInventory().setHeldItemSlot(0);

		// Give player their kit items
		this.giveFFAKitSelectionItems(player);

		// Is this player an ArenaPvP staff member?
		if (player.hasPermission("badlion.kittrial")) {
			// Give them the spectate item
			player.getInventory().setItem(6, ItemStackUtil.createItem(Material.REDSTONE_TORCH_ON, ChatColor.GREEN + "Spectator Mode"));
		}

		player.updateInventory();
	}

	public void loadKitAutomatically(Player player) {
		// Load their first saved kit or default kit
		KitType kitType = new KitType(player.getUniqueId().toString(), FFA.FFA_KITRULESET.getName());
		Map<KitType, List<Kit>> kitTypeListMap = KitCommon.inventories.get(player.getUniqueId());

		if (kitTypeListMap != null) {
			List<Kit> kits = kitTypeListMap.get(kitType);
			if (kits != null) {
				// Load the first custom kit we can find for the player, if they don't have a custom kit load the default kit
				for (Kit kit : kits) {
					KitCommon.loadKit(player, FFA.FFA_KITRULESET, kit.getId());
					break;
				}
			} else {
				KitCommon.loadDefaultKit(player, FFA.FFA_KITRULESET, true);
			}
		} else {
			KitCommon.loadDefaultKit(player, FFA.FFA_KITRULESET, true);
		}

		player.getInventory().setHeldItemSlot(0);
	}

	private void giveFFAKitSelectionItems(Player player) {
		// SG FFA has no ArenaPvP kit, use MPG's kit system
		if (FFA.FFA_GAMEMODE instanceof SGGamemode) {
			MPGKitManager.loadKit(player, FFA.FFA_GAMEMODE.getDefaultKit(), false);

			// No unbreaking on armor/weapons for SG
			return;
		}

		// Hack the ArenaPvP kits in here so that we don't
		// have to update two kits at once when changing things

		// Create default kit item
		ItemStack defaultKit = ItemStackUtil.createItem(Material.ENCHANTED_BOOK, 1, (short) 0,
				ChatColor.GREEN + "Default " + FFA.FFA_KITRULESET.getName() + " kit.");

		ItemStack[] contents = new ItemStack[36];

		contents[8] = defaultKit;
		Map<KitType, List<Kit>> kitTypeListMap = KitCommon.inventories.get(player.getUniqueId());
		if (kitTypeListMap != null) {
			KitType kitType = new KitType(player.getUniqueId().toString(), FFA.FFA_KITRULESET.getName());
			List<Kit> kits = kitTypeListMap.get(kitType);
			if (kits != null) {
				for (Kit kit : kits) {
					// Create kit item
					ItemStack kitItem = ItemStackUtil.createItem(Material.ENCHANTED_BOOK, 1, (short) 0,
							ChatColor.GOLD + "Load " + FFA.FFA_KITRULESET.getName() + " kit: " + (kit.getId() + 1));

					contents[kit.getId()] = kitItem;
				}
			}
		}

		player.getInventory().setContents(contents);
	}

	public static FFA getInstance() {
		return FFA.plugin;
	}

    public FFAGame getFFAGame() {
        return (FFAGame) MPG.getInstance().getMPGGame();
    }

}
