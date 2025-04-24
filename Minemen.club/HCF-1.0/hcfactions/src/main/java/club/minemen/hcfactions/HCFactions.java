package club.minemen.hcfactions;

import club.minemen.clublibrary.scoreboard.ClubScoreboardHandler;
import club.minemen.core.CorePlugin;
import club.minemen.core.gson.ItemStackTypeAdapterFactory;
import club.minemen.hcfactions.combat.CombatTagManager;
import club.minemen.hcfactions.combat.listeners.CombatTagListener;
import club.minemen.hcfactions.command.FocusCommand;
import club.minemen.hcfactions.command.LivesCommand;
import club.minemen.hcfactions.command.LogoutCommand;
import club.minemen.hcfactions.command.PvPProtectionCommand;
import club.minemen.hcfactions.command.parameter.FPlayerParameter;
import club.minemen.hcfactions.deathban.DeathbanManager;
import club.minemen.hcfactions.handler.PacketHandler;
import club.minemen.hcfactions.listener.ChatListener;
import club.minemen.hcfactions.listener.PlayerPermissionListener;
import club.minemen.hcfactions.scoreboard.HCFactionsScoreGetter;
import club.minemen.spigot.ClubSpigot;
import com.google.common.collect.MapMaker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.CmdAutoHelp;
import com.massivecraft.factions.cmd.DeathbanCmdRoot;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.cmd.chat.SendCoordCommand;
import com.massivecraft.factions.integration.KillsProvider;
import com.massivecraft.factions.listeners.ChatCallbackListener;
import com.massivecraft.factions.listeners.DeathbanListener;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.listeners.FactionsEntityListener;
import com.massivecraft.factions.listeners.FactionsExploitListener;
import com.massivecraft.factions.listeners.FactionsPlayerListener;
import com.massivecraft.factions.listeners.FactionsServerListener;
import com.massivecraft.factions.listeners.FactionsVehicleListener;
import com.massivecraft.factions.listeners.KitSignListener;
import com.massivecraft.factions.listeners.KtagListener;
import com.massivecraft.factions.listeners.LeafListener;
import com.massivecraft.factions.listeners.LoggerListener;
import com.massivecraft.factions.listeners.MenuListener;
import com.massivecraft.factions.listeners.ProtectionListener;
import com.massivecraft.factions.listeners.RebootListener;
import com.massivecraft.factions.manager.ChatCallbackManager;
import com.massivecraft.factions.manager.EconomyManager;
import com.massivecraft.factions.manager.PvpProtectionManager;
import com.massivecraft.factions.manager.SubclaimManager;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.type.HomeTask;
import com.massivecraft.factions.type.LogoutTask;
import com.massivecraft.factions.type.StuckTask;
import com.massivecraft.factions.type.WBScheduleTask;
import com.massivecraft.factions.util.AutoLeaveTask;
import com.massivecraft.factions.util.DtrTask;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.util.LocationTypeAdapter;
import com.massivecraft.factions.util.MapFLocToStringSetTypeAdapter;
import com.massivecraft.factions.util.MyLocationTypeAdapter;
import com.massivecraft.factions.zcore.MPlugin;
import com.massivecraft.factions.zcore.util.MaterialMatcher;
import com.massivecraft.factions.zcore.util.TextUtil;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.scheduler.BukkitTask;

@Getter
public final class HCFactions extends MPlugin {
	
	public static Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(Location.class, new LocationTypeAdapter())
			.registerTypeAdapterFactory(new ItemStackTypeAdapterFactory()).create();
	public static boolean rebooting = false;
	@Getter
	private static HCFactions instance;
	// Listeners
	public final FactionsPlayerListener playerListener;
	public final FactionsEntityListener entityListener;
	public final FactionsExploitListener exploitListener;
	public final FactionsBlockListener blockListener;
	public final FactionsServerListener serverListener;
	public final FactionsVehicleListener vehicleListener;
	public final KtagListener ktagListener;
	public final ProtectionListener protectionListener;
	// Managers
	public final PvpProtectionManager pvpProtectionManager;
	public final SubclaimManager subclaimManager;
	public final ChatCallbackManager chatCallbackManager;
	public final EconomyManager economyManager;
	// Commands
	public FCmdRoot cmdBase;
	public CmdAutoHelp cmdAutoHelp;
	public DeathbanCmdRoot deathbanCmdRoot;
	// stats
	public KillsProvider killsProvider;
	// EOTW Mode
	public boolean endOfTheWorld = false;
	// Tasks
	public BukkitTask worldBorderTask;
	// Persistance related
	@Getter
	@Setter
	private boolean locked = false;
	private Integer AutoLeaveTask = null;
	@Getter
	private Map<Player, HomeTask> homeTasks = new MapMaker().weakKeys().makeMap();
	@Getter
	private Map<Player, StuckTask> stuckTasks = new MapMaker().weakKeys().makeMap();
	@Getter
	private Map<Player, LogoutTask> logoutTasks = new MapMaker().weakKeys().makeMap();
	private boolean endEnabled = false;


	// MMC SHIT
	private CombatTagManager combatTagManager;
	private DeathbanManager deathbanManager;

	public HCFactions() {
		instance = this;
		this.playerListener = new FactionsPlayerListener(this);
		this.entityListener = new FactionsEntityListener();
		this.exploitListener = new FactionsExploitListener();
		this.blockListener = new FactionsBlockListener();
		this.serverListener = new FactionsServerListener();
		this.vehicleListener = new FactionsVehicleListener();
		this.ktagListener = new KtagListener();
		this.protectionListener = new ProtectionListener();
		this.pvpProtectionManager = new PvpProtectionManager();
		this.subclaimManager = new SubclaimManager();
		this.chatCallbackManager = new ChatCallbackManager();
		this.economyManager = new EconomyManager();
	}

	@Override
	public void onEnable() {
		if (!preEnable()) {
			return;
		}
		this.registerManagers();
		this.loadSuccessful = false;

		// Load Conf from disk
		Conf.load();
		FPlayers.getInstance().loadFromDisc();
		Factions.getInstance().loadFromDisc();
		Board.load();

		for (String string : Conf.denyCraftingOfMaterials) {
			MaterialMatcher matcher = null;
			try {
				matcher = MaterialMatcher.parse(string);
			} catch (Exception ex) {
				getLogger().warning("Except while parsing \"" + string + "\"");
				getLogger().warning(ex.getMessage());
			}
			int found = 0;
			Iterator<Recipe> it = getServer().recipeIterator();
			while (it.hasNext()) {
				Recipe recipe = it.next();
				if (matcher.matches(recipe.getResult())) {
					found++;
					it.remove();
				}
			}
			if (found == 0) {
				getLogger().info("Couldn't find any recipes matching " + matcher);
			}
		}

		// Add Base Commands
		this.cmdBase = new FCmdRoot();
		this.cmdAutoHelp = new CmdAutoHelp();
		this.getBaseCommands().add(cmdBase);
		this.deathbanCmdRoot = new DeathbanCmdRoot();
		this.getBaseCommands().add(deathbanCmdRoot);

		this.getCommand("sc").setExecutor(new SendCoordCommand());

		// start up task which runs the autoLeaveAfterDaysOfInactivity routine
		this.startAutoLeaveTask(false);
		this.worldBorderTask = new WBScheduleTask().runTaskTimer(this, 0, 40);


		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new DtrTask(), 20 * 10, 20 * 10);

		// Register Event Handlers
		this.getServer().getPluginManager().registerEvents(this.playerListener, this);
		this.getServer().getPluginManager().registerEvents(this.entityListener, this);
		this.getServer().getPluginManager().registerEvents(this.exploitListener, this);
		this.getServer().getPluginManager().registerEvents(this.blockListener, this);
		this.getServer().getPluginManager().registerEvents(this.serverListener, this);
		this.getServer().getPluginManager().registerEvents(this.vehicleListener, this);
		this.getServer().getPluginManager().registerEvents(this.ktagListener, this);
		this.getServer().getPluginManager().registerEvents(new ProtectionListener(), this);
		this.getServer().getPluginManager().registerEvents(new DeathbanListener(), this);
		this.getServer().getPluginManager().registerEvents(new MenuListener(), this);
		this.getServer().getPluginManager().registerEvents(new KitSignListener(), this);
		this.getServer().getPluginManager().registerEvents(new LoggerListener(), this);
		this.getServer().getPluginManager().registerEvents(new ChatCallbackListener(), this);
		this.getServer().getPluginManager().registerEvents(new LeafListener(), this);
		this.getServer().getPluginManager().registerEvents(new RebootListener(), this);

		// since some other plugins execute commands directly through this command interface, provide it
		this.getCommand(this.refCommand).setExecutor(this);
		this.registerBookUnenchantRecipe();
		this.postEnable();
		this.loadSuccessful = true;


		// Swap all biomes with other biomes
		// TODO: MMC FIX
		/*Bukkit.getServer().setBiomeBase(Biome.OCEAN, Biome.FOREST, 0);
		Bukkit.getServer().setBiomeBase(Biome.BEACH, Biome.RIVER, 0);
		Bukkit.getServer().setBiomeBase(Biome.JUNGLE, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.JUNGLE_HILLS, Biome.TAIGA, 0);
		Bukkit.getServer().setBiomeBase(Biome.JUNGLE_EDGE, Biome.DESERT, 0);
		Bukkit.getServer().setBiomeBase(Biome.DEEP_OCEAN, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.SAVANNA_PLATEAU, Biome.FOREST, 0);*/

		// Weird sub-biomes
		// TODO: MMC FIX
		/*Bukkit.getServer().setBiomeBase(Biome.JUNGLE, Biome.PLAINS, 128);
		Bukkit.getServer().setBiomeBase(Biome.JUNGLE_EDGE, Biome.DESERT, 128);
		Bukkit.getServer().setBiomeBase(Biome.SAVANNA, Biome.SAVANNA, 128);
		Bukkit.getServer().setBiomeBase(Biome.SAVANNA_PLATEAU, Biome.RIVER, 128);*/

		/* Create new factions map world
		Bukkit.createWorld(new WorldCreator("factions_world"));

		Bukkit.getLogger().info("Generating new factions world...");
		*/

		this.registerListeners();
		this.registerCommands();
		ClubSpigot.INSTANCE.addPacketHandler(new PacketHandler(this));

		this.getServer().getOnlinePlayers().forEach(player -> {
			FactionPlayer factionPlayer = FPlayers.getInstance().get(player);
			if (factionPlayer == null) {
				return;
			}

			FPlayers.getInstance().login(player);
		});

		ClubScoreboardHandler.setScoreboardConfiguration(HCFactionsScoreGetter.getConfiguration());
		ClubScoreboardHandler.setRequiredLinesToDisplay(3);
		ClubScoreboardHandler.init();
	}

	private void registerManagers() {
		this.deathbanManager = new DeathbanManager(this);
		this.deathbanManager.loadData();
		this.combatTagManager = new CombatTagManager(this);
	}

	private void registerListeners() {
		Arrays.asList(
				new ChatListener(this), new PlayerPermissionListener(this), new CombatTagListener()
		).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
	}

	private void registerCommands() {
		CorePlugin.getInstance().getCommandManager().registerParameter(FactionPlayer.class, new FPlayerParameter());
		CorePlugin.getInstance().getCommandManager().registerAllClasses(
				Arrays.asList(
						new PvPProtectionCommand(this), new PvPProtectionCommand(this), new LivesCommand(this),
						new LogoutCommand(this), new FocusCommand(this)
				));
	}

	public void registerBookUnenchantRecipe() {
		ShapelessRecipe plainBook = new ShapelessRecipe(new ItemStack(Material.BOOK, 1));
		plainBook.addIngredient(Material.ENCHANTED_BOOK);
		plainBook.addIngredient(Material.ENCHANTED_BOOK);
		getServer().addRecipe(plainBook);
	}

	@Override
	public GsonBuilder getGsonBuilder() {
		Type mapFLocToStringSetType = new TypeToken<Map<FLocation, Set<String>>>() {
		}.getType();

		return new GsonBuilder()
				.setPrettyPrinting()
				.enableComplexMapKeySerialization()
				.disableHtmlEscaping()
				.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(Location.class, new LocationTypeAdapter())
				.registerTypeAdapter(LazyLocation.class, new MyLocationTypeAdapter())
				.registerTypeAdapter(mapFLocToStringSetType, new MapFLocToStringSetTypeAdapter());
	}

	@Override
	public void onDisable() {
		this.deathbanManager.saveData();

		// only save data if plugin actually completely loaded successfully
		if (this.loadSuccessful) {
			Board.save();
			Conf.save();
		}

		getLogger().info("Cleaning up temp placed liquids");
		Iterator iter = FactionsPlayerListener.liquidsToReset.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			this.getServer().getScheduler().cancelTask((int) entry.getValue());
			iter.remove();
		}

		if (AutoLeaveTask != null) {
			this.getServer().getScheduler().cancelTask(AutoLeaveTask);
			AutoLeaveTask = null;
		}
		this.worldBorderTask.cancel();
		super.onDisable();
	}

	public void startAutoLeaveTask(boolean restartIfRunning) {
		if (AutoLeaveTask != null) {
			if (!restartIfRunning) {
				return;
			}
			this.getServer().getScheduler().cancelTask(AutoLeaveTask);
		}

		if (Conf.autoLeaveRoutineRunsEveryXMinutes > 0.0) {
			long ticks = (long) (20 * 60 * Conf.autoLeaveRoutineRunsEveryXMinutes);
			AutoLeaveTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoLeaveTask(), ticks, ticks);
		}
	}

	@Override
	public void postAutoSave() {
		Board.save();
		Conf.save();
	}

	@Override
	public boolean logPlayerCommands() {
		return Conf.logPlayerCommands;
	}

	@Override
	public boolean handleCommand(CommandSender sender, String commandString, boolean testOnly) {
		if (sender instanceof Player && FactionsPlayerListener.preventCommand(commandString, (Player) sender)) {
			return true;
		}

		return super.handleCommand(sender, commandString, testOnly);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		return handleCommand(sender, "/" + label + " " + TextUtil.implode(Arrays.asList(split), " "), false);
	}

	// -------------------------------------------- //
	// Functions for other plugins to hook into
	// -------------------------------------------- //
	// This value will be updated whenever new hooks are added
	public int hookSupportVersion() {
		return 3;
	}

	// Simply put, should this chat event be left for Factions to handle? For now, that means players with Faction Chat
	// enabled or use of the Factions f command without a slash; combination of isPlayerFactionChatting() and isFactionsCommand()
	public boolean shouldLetFactionsHandleThisChat(AsyncPlayerChatEvent event) {
		if (event == null) {
			return false;
		}
		return (isPlayerFactionChatting(event.getPlayer()) || isFactionsCommand(event.getMessage()));
	}

	// Does player have Faction Chat enabled? If so, chat plugins should preferably not do channels,
	// local chat, or anything else which targets individual recipients, so Faction Chat can be done
	public boolean isPlayerFactionChatting(Player player) {
		if (player == null) {
			return false;
		}
		FactionPlayer me = FPlayers.getInstance().get(player);

		if (me == null) {
			return false;
		}

		return me.getChatMode() != ChatMode.PUBLIC;
	}

	// Is this chat message actually a Factions command, and thus should be left alone by other plugins?
	// TODO: GET THIS BACK AND WORKING
	public boolean isFactionsCommand(String check) {
		if (check == null || check.isEmpty()) {
			return false;
		}
		return this.handleCommand(null, check, true);
	}

	// Get a player's faction tag (faction name), mainly for usage by chat plugins for local/channel chat
	public String getPlayerFactionTag(Player player) {
		return getPlayerFactionTagRelation(player, null);
	}

	// Same as above, but with relation (enemy/neutral/ally) coloring potentially added to the tag
	public String getPlayerFactionTagRelation(Player speaker, Player listener) {
		String tag = "~";

		if (speaker == null) {
			return tag;
		}

		FactionPlayer me = FPlayers.getInstance().get(speaker);
		if (me == null) {
			return tag;
		}

		// if listener isn't set, or config option is disabled, give back uncolored tag
		if (listener == null || !Conf.chatTagRelationColored) {
			tag = me.getChatTag().trim();
		} else {
			FactionPlayer you = FPlayers.getInstance().get(listener);
			if (you == null) {
				tag = me.getChatTag().trim();
			} else // everything checks out, give the colored tag
			{
				tag = me.getChatTag(you).trim();
			}
		}
		if (tag.isEmpty()) {
			tag = "~";
		}

		return tag;
	}

	// Get a list of all faction tags (names)
	public Set<String> getFactionTags() {
		Set<String> tags = new HashSet<String>();
		for (Faction faction : Factions.getInstance().getAll()) {
			tags.add(faction.getTag());
		}
		return tags;
	}

	// Get a list of all players in the specified faction
	public Set<String> getPlayersInFaction(String factionTag) {
		Set<String> players = new HashSet<String>();
		Faction faction = Factions.getInstance().getByTag(factionTag);
		if (faction != null) {
			for (FactionPlayer fplayer : faction.getFPlayers()) {
				players.add(fplayer.getName());
			}
		}
		return players;
	}

	// Get a list of all online players in the specified faction
	public Set<String> getOnlinePlayersInFaction(String factionTag) {
		Set<String> players = new HashSet<String>();
		Faction faction = Factions.getInstance().getByTag(factionTag);
		if (faction != null) {
			for (FactionPlayer fplayer : faction.getFPlayersWhereOnline(true)) {
				players.add(fplayer.getName());
			}
		}
		return players;
	}

	// check if player is allowed to build/destroy in a particular location
	public boolean isPlayerAllowedToBuildHere(Player player, Location location) {
		return FactionsBlockListener.playerCanBuildDestroyBlock(player, location, "", true);
	}

	// check if player is allowed to interact with the specified block (doors/chests/whatever)
	public boolean isPlayerAllowedToInteractWith(Player player, Block block) {
		return FactionsPlayerListener.canPlayerUseBlock(player, block, true, Action.RIGHT_CLICK_BLOCK);
	}

	// check if player is allowed to use a specified item (flint&steel, buckets, etc) in a particular location
	public boolean isPlayerAllowedToUseThisHere(Player player, Location location, Material material) {
		return FactionsPlayerListener.playerCanUseItemHere(player, location, material, true);
	}

	public ChatCallbackManager getChatCallbackManager() {
		return chatCallbackManager;
	}

	public EconomyManager getEconomyManager() {
		return economyManager;
	}


	public boolean isEndEnabled() {
		return endEnabled;
	}

	public void setEndEnabled(boolean endEnabled) {
		this.endEnabled = endEnabled;
	}
}
