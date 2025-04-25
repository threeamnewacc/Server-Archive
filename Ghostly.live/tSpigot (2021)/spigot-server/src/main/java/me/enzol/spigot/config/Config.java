package me.enzol.spigot.config;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Level;


import me.enzol.spigot.knockback.KnockbackProfile;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {


    private File configFile;
    private YamlConfiguration config;

    private KnockbackProfile currentKb;
    private Set<KnockbackProfile> kbProfiles = new HashSet<>();

    public boolean inheritVelocity;
    public float friction;
    public KnockbackProfile activeProfile;
    private List<KnockbackProfile> loadedProfiles;
    public float sprintSlowdown;
    public boolean yawBasedCalculation;
    public float kbX;
    public float kbZ;
    public float kbV;
    public boolean floatyV;
    public float vLimit;
    public boolean kbWtapVMult;
    public float kbWtapH;
    public float kbWtapV;
    public boolean wtapTicksInterval;
    public int maxWtapTicks;
    public float potM;
    public float potJ;
    public float potL;
    public boolean secondPacketEnabled;
    public float secondPacketHMult;
    public float secondPacketVMult;
    public int secondPacketInterval;
    public boolean onPacketFly_beta;
    public float pearlHOffset;
    public float pearlVOffset;
    public float pearlDamage;
    public float ticksDown;
    public float verticalCombo;
    public double alturaCombo;

    public Config() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("version.properties");

        this.configFile = new File("knockback.yml");
        this.config = new YamlConfiguration();

        try {
            config.load(this.configFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load knockback.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }

        this.config.options().copyDefaults(true);

        this.loadConfig();
    }
    public void create() throws IOException {
        final File f = new File("knockback.yml");
        this.configFile = new File("knockback.yml");
        this.config = new YamlConfiguration();
        if (!f.exists()) {
            final FileConfiguration fc = YamlConfiguration.loadConfiguration(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("configurations/knockback.yml"), Charsets.UTF_8));
            fc.save(f);
            return;
        }
        final FileConfiguration fc = new YamlConfiguration();
        fc.set("active", this.activeProfile.title);
        final ConfigurationSection profiles = fc.createSection("profiles");
        for (final KnockbackProfile profile : this.loadedProfiles) {
            final ConfigurationSection profileData = profiles.createSection(profile.title);
        }
    }
        private void loadConfig() {
        this.inheritVelocity = this.getBoolean("inheritvelocity", true);
        this.friction = this.getFloat("friction", 1.0f);
        this.sprintSlowdown = this.getFloat("sprint-slowdown", 1.0f);
        this.yawBasedCalculation = this.getBoolean("yaw-based-calculation", true);
        this.kbX = this.getFloat("x", 1.0f);
        this.kbZ = this.getFloat("z", 1.0f);
        this.kbV = this.getFloat("vertical", 1.0f);
        this.verticalCombo = this.getFloat("verticalCombo", 1.0f);
        this.alturaCombo = this.getDouble("alturaCombo", 1.0D);
        this.floatyV = this.getBoolean("floaty-vertical", false);
        this.kbWtapVMult = this.getBoolean("vertical-wtap-mult", true);
        this.kbWtapH = this.getFloat("horizontal-wtap", 1.0f);
        this.kbWtapV = this.getFloat("vertical-wtap", 1.0f);
        this.wtapTicksInterval = this.getBoolean("wtap-ticks-interval", true);
        this.maxWtapTicks = this.getInt ("wtap-ticks", 4);
        this.potM = this.getFloat("pots-m", 0.05f);
        this.potJ = this.getFloat("pots-j", 0.5f);
        this.potL = this.getFloat("pots-l", -20.0f);
        this.secondPacketEnabled = this.getBoolean("second-packet-enabled", true);
        this.secondPacketHMult = this.getFloat("second-packet-horizontal-mult", 0.5f);
        this.secondPacketVMult = this.getFloat("second-packet-vertical-mult", 0.5f);
        this.secondPacketInterval = this.getInt("second-packet-delay", 3);
        this.onPacketFly_beta = this.getBoolean("on-fly-in-packet-beta", false);
        this.pearlHOffset = this.getFloat("pearl-horizontal-offset", 0.5f);
        this.pearlVOffset = this.getFloat("pearl-vertical-offset", 0.22f);
        this.pearlDamage = this.getFloat("pearl-damage", 5.0f);

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

    public boolean isInheritVelocity() {
        return inheritVelocity;
    }

    public float isFriction() {
        return friction = 1.0f;
    }

    public float isSprintSlowdown() {
        return sprintSlowdown;
    }

    public boolean isYawBasedCalculation() {
        return yawBasedCalculation = true;
    }

    public float iskbX() {
        return kbX = 1.0f;
    }

    public float iskbZ() {
        return kbZ = 1.0f;
    }

    public float iskbV() {
        return kbV = 1.0f;
    }

    public double isalturaCombo() {
        return alturaCombo = 4.0D;
    }

    public boolean isFloatyV() {
        return floatyV = false;
    }

    public boolean isKbWtapVMult() {
        return kbWtapVMult = true;
    }

    public float iskbWtapH() {
        return kbWtapH = 1.0f;
    }

    public float iskbWtapV() {
        return kbWtapV = 1.0f;
    }

    public boolean iswtapTicksInterval() {
        return wtapTicksInterval = true;
    }

    public int ismaxWtapTicks() {
        return maxWtapTicks = 4;
    }

    public float ispotM() {
        return potM = 0.05f;
    }

    public float ispotJ() {
        return potJ = 0.5f;
    }
    public float ispotL() {
        return potL = -20.0f;
    }
    public boolean issecondPacketEnabled() {
        return secondPacketEnabled = true;
    }
    public float issecondPacketHMult() {
        return secondPacketHMult = 0.5f;
    }
    public float issecondPacketVMult() {
        return secondPacketVMult = 0.5f;
    }
    public int issecondPacketInterval() {
        return secondPacketInterval = 4;
    }
    public boolean isonPacketFly_beta() {
        return onPacketFly_beta = false;
    }
    public float ispearlHOffset() {
        return pearlHOffset = 0.5f;
    }

    public float ispearlVOffset() {
        return pearlVOffset = 0.5f;
    }

    public float ispearlDamage() {
        return pearlDamage = 5.0f;
    }

}
