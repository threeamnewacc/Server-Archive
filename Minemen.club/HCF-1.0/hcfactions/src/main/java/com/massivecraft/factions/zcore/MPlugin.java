package com.massivecraft.factions.zcore;

import club.minemen.hcfactions.HCFactions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.zcore.persist.EM;
import com.massivecraft.factions.zcore.persist.SaveTask;
import com.massivecraft.factions.zcore.util.LibLoader;
import com.massivecraft.factions.zcore.util.PermUtil;
import com.massivecraft.factions.zcore.util.Persist;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

public abstract class MPlugin extends JavaPlugin {

	// Some utils
	public Persist persist;
	public TextUtil txt;
	public LibLoader lib;
	public PermUtil perm;

	// Persist related
	public Gson gson;
	public String refCommand = "";
	// -------------------------------------------- //
	// LANG AND TAGS
	// -------------------------------------------- //
	// These are not supposed to be used directly.
	// They are loaded and used through the TextUtil instance for the plugin.
	public Map<String, String> rawTags = new LinkedHashMap<String, String>();
	protected boolean loadSuccessful = false;
	private Integer saveTask = null;
	private boolean autoSave = true;
	// Listeners
	private MPluginSecretPlayerListener mPluginSecretPlayerListener;
	private MPluginSecretServerListener mPluginSecretServerListener;
	// Our stored base commands
	private List<MCommand<?>> baseCommands = new ArrayList<MCommand<?>>();
	// -------------------------------------------- //
	// ENABLE
	// -------------------------------------------- //
	private long timeEnableStart;

	public boolean getAutoSave() {
		return this.autoSave;
	}

	public void setAutoSave(boolean val) {
		this.autoSave = val;
	}

	public List<MCommand<?>> getBaseCommands() {
		return this.baseCommands;
	}

	public boolean preEnable() {
		log("=== ENABLE START ===");
		timeEnableStart = System.currentTimeMillis();

		// Ensure basefolder exists!
		this.getDataFolder().mkdirs();

		// Create Utility Instances
		this.perm = new PermUtil(this);
		this.persist = new Persist(this);
		this.lib = new LibLoader(this);

		// GSON 2.1 is now embedded in CraftBukkit, used by the auto-updater: https://github.com/Bukkit/CraftBukkit/commit/0ed1d1fdbb1e0bc09a70bc7bfdf40c1de8411665
		// if ( ! lib.require("gson.jar", "http://search.maven.org/remotecontent?filepath=com/google/code/gson/gson/2.1/gson-2.1.jar")) return false;
		this.gson = this.getGsonBuilder().create();

		this.txt = new TextUtil();
		initTXT();

		// attempt to getAll first command defined in plugin.yml as reference command, if any commands are defined in there
		// reference command will be used to prevent "unknown command" console messages
		try {
			Map<String, Map<String, Object>> refCmd = this.getDescription().getCommands();
			if (refCmd != null && !refCmd.isEmpty()) {
				this.refCommand = (String) (refCmd.keySet().toArray()[0]);
			}
		} catch (ClassCastException ex) {
		}

		// Create and register listeners
		this.mPluginSecretPlayerListener = new MPluginSecretPlayerListener(this);
		this.mPluginSecretServerListener = new MPluginSecretServerListener(this);
		getServer().getPluginManager().registerEvents(this.mPluginSecretPlayerListener, this);
		getServer().getPluginManager().registerEvents(this.mPluginSecretServerListener, this);

		// Register recurring tasks
		if (saveTask == null && Conf.saveToFileEveryXMinutes > 0.0) {
			long saveTicks = (long) (20L * 60 * Conf.saveToFileEveryXMinutes); // Approximately every 30 min by default
			saveTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new SaveTask(this), saveTicks, saveTicks);
		}

		loadSuccessful = true;
		return true;
	}

	public void postEnable() {
		log("=== ENABLE DONE (Took " + (System.currentTimeMillis() - timeEnableStart) + "ms) ===");
	}

	public void onDisable() {
		if (saveTask != null) {
			this.getServer().getScheduler().cancelTask(saveTask);
			saveTask = null;
		}
		// only save data if plugin actually loaded successfully
		if (loadSuccessful) {
			EM.saveAllToDisc();
		}
		log("Disabled");
	}

	public void suicide() {
		log("Now I suicide!");
		this.getServer().getPluginManager().disablePlugin(this);
	}

	// -------------------------------------------- //
	// Some inits...
	// You are supposed to override these in the plugin if you aren't satisfied with the defaults
	// The goal is that you always will be satisfied though.
	// -------------------------------------------- //
	public GsonBuilder getGsonBuilder() {
		return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
	}

	public void addRawTags() {
		this.rawTags.put("l", "<green>"); // logo
		this.rawTags.put("a", "<gold>"); // art
		this.rawTags.put("n", "<silver>"); // notice
		this.rawTags.put("instance", "<yellow>"); // info
		this.rawTags.put("g", "<lime>"); // good
		this.rawTags.put("b", "<rose>"); // bad
		this.rawTags.put("h", "<pink>"); // highligh
		this.rawTags.put("c", "<aqua>"); // command
		this.rawTags.put("plugin", "<teal>"); // parameter
	}

	public void initTXT() {
		this.addRawTags();

		Type type = new TypeToken<Map<String, String>>() {
		}.getType();

		Map<String, String> tagsFromFile = this.persist.load(type, "tags");
		if (tagsFromFile != null) {
			this.rawTags.putAll(tagsFromFile);
		}
		this.persist.save(this.rawTags, "tags");

		for (Entry<String, String> rawTag : this.rawTags.entrySet()) {
			this.txt.tags.put(rawTag.getKey(), TextUtil.parseColor(rawTag.getValue()));
		}
	}

	// -------------------------------------------- //
	// COMMAND HANDLING
	// -------------------------------------------- //
	// can be overridden by P method, to provide option
	public boolean logPlayerCommands() {
		return true;
	}

	public boolean handleCommand(CommandSender sender, String commandString, boolean testOnly) {
		return handleCommand(sender, commandString, testOnly, false);
	}

	public boolean handleCommand(final CommandSender sender, String commandString, boolean testOnly, boolean async) {
		boolean noSlash = true;
		if (commandString.startsWith("/")) {
			noSlash = false;
			commandString = commandString.substring(1);
		}

		String lower = commandString.toLowerCase();

		for (final MCommand<?> command : this.getBaseCommands()) {
			if (noSlash && !command.allowNoSlashAccess) {
				continue;
			}

			for (String alias : command.aliases) {
				// disallow double-space after alias, so specific commands can be prevented (preventing "f home" won't prevent "f  home")
				if (lower.startsWith(alias + "  ")) {
					return false;
				}

				if (lower.startsWith(alias + " ") || lower.equals(alias)) {
					final List<String> args = new ArrayList<String>(Arrays.asList(commandString.split("\\s+")));
					args.remove(0);

					if (testOnly) {
						return true;
					}

					if (async) {
						this.getServer().getScheduler().runTaskAsynchronously(this, () -> command.execute(sender, args));
					} else {
						command.execute(sender, args);
					}

					return true;
				}
			}
		}
		return false;
	}

	public boolean handleCommand(CommandSender sender, String commandString) {
		return this.handleCommand(sender, commandString, false);
	}

	// -------------------------------------------- //
	// HOOKS
	// -------------------------------------------- //
	public void preAutoSave() {
		this.log(Level.INFO, "Saving Faction data (auto)...");
	}

	public void postAutoSave() {
		this.log(Level.INFO, "Saved Faction data!");
	}

	// -------------------------------------------- //
	// LOGGING
	// -------------------------------------------- //
	public void log(Object msg) {
		log(Level.INFO, msg);
	}

	public void log(String str, Object... args) {
		log(Level.INFO, this.txt.parse(str, args));
	}

	public void log(Level level, String str, Object... args) {
		log(level, this.txt.parse(str, args));
	}

	public void log(Level level, Object msg) {
		HCFactions.getInstance().getLogger().log(level, "" + msg);
	}
}
