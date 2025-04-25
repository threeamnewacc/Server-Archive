package me.enzol.spigot;

import com.google.common.base.Throwables;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;


import me.enzol.spigot.knockback.KnockbackProfile;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class TrainingSpigotConfig {

    private static final String HEADER = "This is the main configuration file for SpigotX.\n"
            + "Modify with caution, and make sure you know what you are doing.\n";

    private File configFile;
    private YamlConfiguration config;

    private KnockbackProfile currentKb;
    private Set<KnockbackProfile> kbProfiles = new HashSet<>();

    private String knockbackDefaultProfile;
    private boolean PingCommand;
    private boolean hidePlayersFromTab;
    private boolean firePlayerMoveEvent;
    private boolean fireLeftClickAir;
    private boolean fireLeftClickBlock;
    private boolean entityActivation;
    private boolean invalidArmAnimationKick;
    private boolean mobAIEnabled;
    private boolean baseVersionEnabled;
    private boolean doChunkUnload;
    private boolean blockOperations;
    private boolean disableJoinMessage;
    private boolean disableLeaveMessage;
    private boolean pearlThroughString;
    private boolean pearlThroughGate;
    private boolean pearlThroughWeb;
    private boolean pearlThroughVine;

    public TrainingSpigotConfig() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("version.properties");

        this.configFile = new File("settings.yml");
        this.config = new YamlConfiguration();

        try {
            config.load(this.configFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load settings.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }

        this.config.options().copyDefaults(true);

        this.loadConfig();
    }

    private void loadConfig() {
        this.PingCommand = this.getBoolean("ping-command", false);
        this.knockbackDefaultProfile = this.getString("knockback.default-profile", "default");
        this.hidePlayersFromTab = this.getBoolean("hide-players-from-tab", true);
        this.firePlayerMoveEvent = this.getBoolean("fire-player-move-event", false);
        this.fireLeftClickAir = this.getBoolean("fire-left-click-air", false);
        this.fireLeftClickBlock = this.getBoolean("fire-left-click-block", false);
        this.entityActivation = this.getBoolean("entity-activation", false);
        this.invalidArmAnimationKick = this.getBoolean("invalid-arm-animation-kick", false);
        this.mobAIEnabled = this.getBoolean("mob-ai", true);
        this.baseVersionEnabled = this.getBoolean("1-8-enabled", false);
        this.doChunkUnload = this.getBoolean("do-chunk-unload", true);
        this.blockOperations = this.getBoolean("block-operations", false);
        this.disableJoinMessage = this.getBoolean("disable-join-message", true);
        this.disableLeaveMessage = this.getBoolean("disable-leave-message", true);
        this.pearlThroughGate = getBoolean("pearlThrough.fencegate", true);
        this.pearlThroughString = getBoolean("pearlThrough.string", true);
        this.pearlThroughWeb = getBoolean("pearlThrough.cobweb", true);
        this.pearlThroughVine = getBoolean("pearlThrough.vine", true);

        try {
            this.config.save(this.configFile);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + this.configFile, ex);
        }
    }

    public void save() {
        try {
            this.config.save(this.configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set(String path, Object val) {
        this.config.set(path, val);

        try {
            this.config.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getKeys(String path) {
        if (!this.config.isConfigurationSection(path)) {
            this.config.createSection(path);
            return new HashSet<>();
        }

        return this.config.getConfigurationSection(path).getKeys(false);
    }

    public boolean getBoolean(String path, boolean def) {
        this.config.addDefault(path, def);
        return this.config.getBoolean(path, this.config.getBoolean(path));
    }

    public double getDouble(String path, double def) {
        this.config.addDefault(path, def);
        return this.config.getDouble(path, this.config.getDouble(path));
    }

    public float getFloat(String path, float def) {
        return (float) this.getDouble(path, (double) def);
    }

    public int getInt(String path, int def) {
        this.config.addDefault(path, def);
        return config.getInt(path, this.config.getInt(path));
    }

    public <T> List getList(String path, T def) {
        this.config.addDefault(path, def);
        return this.config.getList(path, this.config.getList(path));
    }

    public String getString(String path, String def) {
        this.config.addDefault(path, def);
        return this.config.getString(path, this.config.getString(path));
    }

    public boolean isPingCommand() {
        return this.PingCommand;
    }

    public boolean isBlockOperations() {
        return blockOperations;
    }

    public boolean isFireLeftClickAir() {
        return fireLeftClickAir;
    }

    public boolean isBaseVersionEnabled() {
        return baseVersionEnabled;
    }

    public boolean isFireLeftClickBlock() {
        return fireLeftClickBlock;
    }

    public boolean isEntityActivation() {
        return entityActivation;
    }

    public boolean isFirePlayerMoveEvent() {
        return firePlayerMoveEvent;
    }

    public boolean isHidePlayersFromTab() {
        return hidePlayersFromTab;
    }

    public boolean isDisableJoinMessage() {
        return disableJoinMessage;
    }

    public boolean isDisableLeaveMessage() {
        return disableLeaveMessage;
    }

    public boolean isDoChunkUnload() {
        return doChunkUnload;
    }

    public boolean isInvalidArmAnimationKick() {
        return invalidArmAnimationKick;
    }

    public boolean isMobAIEnabled() {
        return mobAIEnabled;
    }

    public boolean isPearlThroughGate() {
        return pearlThroughGate;
    }

    public boolean isPearlThroughString() {
        return pearlThroughString;
    }

    public boolean isPearlThroughVine() {
        return pearlThroughVine;
    }

    public boolean isPearlThroughWeb() {
        return pearlThroughWeb;
    }

    public String getKnockbackDefaultProfile() {
        return TrainingSpigot.INSTANCE.getConfig().config.getString("knockback.default-profile");
    }


}
