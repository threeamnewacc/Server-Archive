package me.enzol.spigot.knockback;

import me.enzol.spigot.knockback.*;

import java.io.*;
import org.bukkit.configuration.file.*;
import java.lang.reflect.*;
import java.util.*;

public class KnockbackProfile
{
    public final String title;
    public List<KnockbackValue> values;
    public KnockbackValue<Boolean> inheritVelocity;
    public KnockbackValue<Double> friction;
    public KnockbackValue<Double> sprintSlowdown;
    public KnockbackValue<Boolean> yawBasedCalculation;
    public KnockbackValue<Double> kbX;
    public KnockbackValue<Double> kbZ;
    public KnockbackValue<Double> kbV;
    public KnockbackValue<Boolean> floatyV;
    public KnockbackValue<Double> vLimit;
    public KnockbackValue<Boolean> kbWtapVMult;
    public KnockbackValue<Double> kbWtapH;
    public KnockbackValue<Double> kbWtapV;
    public KnockbackValue<Integer> maxWtapTicks;
    public KnockbackValue<Double> potM;
    public KnockbackValue<Double> potJ;
    public KnockbackValue<Double> potL;
    public KnockbackValue<Boolean> secondPacketEnabled;
    public KnockbackValue<Double> secondPacketHMult;
    public KnockbackValue<Double> secondPacketVMult;
    public KnockbackValue<Integer> secondPacketInterval;
    public KnockbackValue<Boolean> onPacketFly_beta;
    public KnockbackValue<Double> pearlHOffset;
    public KnockbackValue<Double> pearlVOffset;
    public KnockbackValue<Double> pearlDamage;
    public KnockbackValue<Integer> ticksDown;
    public KnockbackValue<Double> verticalCombo;
    public KnockbackValue<Double> alturaCombo;

    public KnockbackProfile(final String title) {
        this.values = new ArrayList<KnockbackValue>();
        this.inheritVelocity = new KnockbackValue<Boolean>("inherit_velocity", "Inherit Velocity", Boolean.class, false);
        this.friction = new KnockbackValue<Double>("friction", "Friction", Double.class, 1.0D);
        this.sprintSlowdown = new KnockbackValue<Double>("sprint_slowdown", "Sprint Slowdown", Double.class, 1.0D);
        this.yawBasedCalculation = new KnockbackValue<Boolean>("yaw_based_calculation", "Yaw Based Calculation", Boolean.class, true);
        this.kbX = new KnockbackValue<Double>("x", "Horizontal-X", Double.class, 0.36D);
        this.kbZ = new KnockbackValue<Double>("z", "Horizontal-Z", Double.class, 0.42D);
        this.kbV = new KnockbackValue<Double>("vertical", "Vertical", Double.class, 0.42D);
        this.floatyV = new KnockbackValue<Boolean>("floaty_vertical", "Floaty Vertical", Boolean.class, true);
        this.vLimit = new KnockbackValue<Double>("vertical_limit", "Vertical Limit", Double.class, 1.0D);
        this.kbWtapVMult = new KnockbackValue<Boolean>("knockback_wtap_v_mult", "Knockback wTap Vertical Multiplier", Boolean.class, true);
        this.kbWtapH = new KnockbackValue<Double>("knockback_wtap_h", "Knockback wTap Horizontal", Double.class, 1.0D);
        this.kbWtapV = new KnockbackValue<Double>("knockback_wtap_v", "Knockback wTap Vertical", Double.class, 1.2D);
        this.maxWtapTicks = new KnockbackValue<Integer>("max_wtap_ticks", "Max wTap Ticks", Integer.class, 4);
        this.potM = new KnockbackValue<Double>("potm", "Potion M", Double.class, 1.0D);
        this.potJ = new KnockbackValue<Double>("potj", "Potion J", Double.class, 1.2D);
        this.potL = new KnockbackValue<Double>("potl", "Potion L", Double.class, 1.0D);
        this.secondPacketEnabled = new KnockbackValue<Boolean>("combo", "Combo Mode", Boolean.class, true);
        this.secondPacketHMult = new KnockbackValue<Double>("second_packet_h_mult", "Second Packet Horizontal Multiplier", Double.class, 0.5D);
        this.secondPacketVMult = new KnockbackValue<Double>("second_packet_v_mult", "Second Packet Vertical Multiplier", Double.class, 0.5D);
        this.secondPacketInterval = new KnockbackValue<Integer>("second_packet_interval", "Second Packet Interval", Integer.class, 2);
        this.onPacketFly_beta = new KnockbackValue<Boolean>("on_packet_fly_beta", "Packet Fly-Beta", Boolean.class, false);
        this.pearlHOffset = new KnockbackValue<Double>("pearl_h_offset", "Pearl Horizontal Offset", Double.class, 0.6D);
        this.pearlVOffset = new KnockbackValue<Double>("pear_v_offset", "Pearl Vertical Offset", Double.class, 1D);
        this.pearlDamage = new KnockbackValue<Double>("pearl_damage", "Pearl Damage", Double.class, 1.5D);
        this.ticksDown = new KnockbackValue<Integer>("ticks_down", "Ticks Down", Integer.class, 1);
        this.verticalCombo = new KnockbackValue<Double>("vertical_combo", "Vertical Combo", Double.class, 0.6D);
        this.alturaCombo = new KnockbackValue<Double>("altura_combo", "Altura Combo", Double.class, 1D);
        this.title = title;
        this.load();
    }

    public void load() {
        try {
            this.values.clear();
            Field[] fields;
            for (int length = (fields = this.getClass().getFields()).length, i = 0; i < length; ++i) {
                final Field f = fields[i];
                if (f.getType() == KnockbackValue.class) {
                    this.values.add((KnockbackValue)f.get(this));
                }
            }
            final File file = new File("Knockback" + File.separator + this.title + ".yml");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            final YamlConfiguration config = new YamlConfiguration();
            config.load(file);
            config.options().header("Help / Caption:\n\nMETHOD IN BETA! I RECOMMEND FALSE.\n  1point1kb: <true/false>\n\nLimits Vertical Knockback\n  limit_vertical: <true/false>\n  limit_vertical_value: <Value double>\n\nVertical and Horizontal Knockback Values\n  horizontal: <Value double>\n  vertical: <Value double>\n\n'inheritance_horizontal' to enter the values ('horizontal' values entered above) from Horizontal Knockback to the X and Z coordinates.\n'inheritance_vertical' to enter the values ('vertical' values entered above) from Vertical Knockback to the Y coordinates.\n  inheritance_horizontal: <true/false>\n  inheritance_vertical: <true/false>\n\nVertical and Horizontal Knockback Values while with Force Effect\n  inheritance_strength_horizontal: <<Value double>>\n  inheritance_strength_vertical: <Value double>\n\nVertical and Horizontal Knockback values while on the ground\n  ground_horizontal: <Value double>\n  ground_vertical: <Value double>\n\nVertical and Horizontal Knockback values while running\n  sprint_horizontal: <Value double>\n  sprint_vertical: <Value double>\n\nVertical and Horizontal Knockback values in Bows\n  bow_horizontal: <Value double>\n  bow_vertical: <Value double>\n\nVertical and Horizontal Knockback values in Rod\n  rod_horizontal: <Value double>\n  rod_vertical: <Value double>\n\nCombo Settings\n  combo: <true/false>\n  combo_ticks: Value integer  combo_velocity: <Value double>\n  combo_height: <Value double>\n\nStop the sprint when it takes damage\n  stop_sprint: <true/false>\n\nSlows down the attacker (AutoWtap)\n  attacker_slowdown: <Value double>\n\nPotions Settings\n  potion_fall: <Value double>\n  potion_multiplier: <Value double>\n  potion_offset: <Value double>\n");
            config.options().copyDefaults(true);
            for (final KnockbackValue value : this.values) {
                final Object val = config.get(value.id);
                if (val == null) {
                    config.set(value.id, value.value);
                }
                else {
                    value.value = val;
                }
            }
            config.save(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            final File file = new File("Knockback" + File.separator + this.title + ".yml");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            final YamlConfiguration config = new YamlConfiguration();
            config.options().header("Help / Caption:\n\nMETHOD IN BETA! I RECOMMEND FALSE.\n  1point1kb: <true/false>\n\nLimits Vertical Knockback\n  limit_vertical: <true/false>\n  limit_vertical_value: <Value double>\n\nVertical and Horizontal Knockback Values\n  horizontal: <Value double>\n  vertical: <Value double>\n\n'inheritance_horizontal' to enter the values ('horizontal' values entered above) from Horizontal Knockback to the X and Z coordinates.\n'inheritance_vertical' to enter the values ('vertical' values entered above) from Vertical Knockback to the Y coordinates.\n  inheritance_horizontal: <true/false>\n  inheritance_vertical: <true/false>\n\nVertical and Horizontal Knockback Values while with Force Effect\n  inheritance_strength_horizontal: <<Value double>>\n  inheritance_strength_vertical: <Value double>\n\nVertical and Horizontal Knockback values while on the ground\n  ground_horizontal: <Value double>\n  ground_vertical: <Value double>\n\nVertical and Horizontal Knockback values while running\n  sprint_horizontal: <Value double>\n  sprint_vertical: <Value double>\n\nVertical and Horizontal Knockback values in Bows\n  bow_horizontal: <Value double>\n  bow_vertical: <Value double>\n\nVertical and Horizontal Knockback values in Rod\n  rod_horizontal: <Value double>\n  rod_vertical: <Value double>\n\nCombo Settings\n  combo: <true/false>\n  combo_ticks: Value integer  combo_velocity: <Value double>\n  combo_height: <Value double>\n\nStop the sprint when it takes damage\n  stop_sprint: <true/false>\n\nSlows down the attacker (AutoWtap)\n  attacker_slowdown: <Value double>\n\nPotions Settings\n  potion_fall: <Value double>\n  potion_multiplier: <Value double>\n  potion_offset: <Value double>\n");
            config.options().copyDefaults(true);
            for (final KnockbackValue value : this.values) {
                config.set(value.id, value.value);
            }
            config.save(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//OLD CLASS
/*public class KnockbackProfile
{
    public final String title;
    public boolean inheritVelocity;
    public Double friction;
    public Double sprintSlowdown;
    public boolean yawBasedCalculation;
    public Double kbX;
    public Double kbZ;
    public Double kbV;
    public boolean DoubleyV;
    public Double vLimit;
    public boolean kbWtapVMult;
    public Double kbWtapH;
    public Double kbWtapV;
    public boolean wtapTicksInterval;
    public int maxWtapTicks;
    public Double potM;
    public Double potJ;
    public Double potL;
    public boolean secondPacketEnabled;
    public Double secondPacketHMult;
    public Double secondPacketVMult;
    public int secondPacketInterval;
    public boolean onPacketFly_beta;
    public Double pearlHOffset;
    public Double pearlVOffset;
    public Double pearlDamage;
    public Double ticksDown;
    public Double verticalCombo;
    public double alturaCombo;

    public KnockbackProfile(final String title) {
        this.inheritVelocity = true;
        this.friction = 1.0f;
        this.sprintSlowdown = 1.0f;
        this.yawBasedCalculation = true;
        this.kbX = 1.0f;
        this.kbZ = 1.0f;
        this.kbV = 1.0f;
        this.verticalCombo = 0.1f;
        this.alturaCombo = 4.0D;
        this.DoubleyV = false;
        this.kbWtapVMult = true;
        this.kbWtapH = 1.0f;
        this.kbWtapV = 1.0f;
        this.wtapTicksInterval = true;
        this.maxWtapTicks = 4;
        this.potM = 0.05f;
        this.potJ = 0.5f;
        this.potL = -20.0f;
        this.secondPacketEnabled = true;
        this.secondPacketHMult = 0.5f;
        this.secondPacketVMult = 0.5f;
        this.secondPacketInterval = 4;
        this.onPacketFly_beta = false;
        this.pearlHOffset = 0.5f;
        this.pearlVOffset = 0.22f;
        this.pearlDamage = 5.0f;
        this.title = title.toLowerCase();
    }
    
    public KnockbackProfile(final String title, final KnockbackProfile preset) {
        this(title);
        if (preset != null) {
            this.inheritVelocity = preset.inheritVelocity;
            this.friction = preset.friction;
            this.sprintSlowdown = preset.sprintSlowdown;
            this.yawBasedCalculation = preset.yawBasedCalculation;
            this.kbX = preset.kbX;
            this.kbZ = preset.kbZ;
            this.kbV = preset.kbV;
            this.verticalCombo = preset.verticalCombo;
            this.alturaCombo = preset.alturaCombo;
            this.DoubleyV = preset.DoubleyV;
            this.vLimit = preset.vLimit;
            this.kbWtapVMult = preset.kbWtapVMult;
            this.kbWtapH = preset.kbWtapH;
            this.kbWtapV = preset.kbWtapV;
            this.wtapTicksInterval = preset.wtapTicksInterval;
            this.maxWtapTicks = preset.maxWtapTicks;
            this.potM = preset.potM;
            this.potJ = preset.potJ;
            this.potL = preset.potL;
            this.secondPacketEnabled = preset.secondPacketEnabled;
            this.secondPacketHMult = preset.secondPacketHMult;
            this.secondPacketInterval = preset.secondPacketInterval;
            this.onPacketFly_beta = preset.onPacketFly_beta;
            this.pearlHOffset = preset.pearlHOffset;
            this.pearlVOffset = preset.pearlVOffset;
            this.pearlDamage = preset.pearlDamage;
        }
    }
    
    public KnockbackProfile(final String title, final ConfigurationSection data) {
        this(title);
        this.load(data);
    }
    
    public void save(final ConfigurationSection data) {
        data.set("inherit-velocity", this.inheritVelocity);
        data.set("friction", this.friction);
        data.set("sprint-slowdown", this.sprintSlowdown);
        data.set("yaw-based-calculation", this.yawBasedCalculation);
        data.set("x", this.kbZ);
        data.set("vertical", this.kbV);
        data.set("z", this.kbX);
        data.set("verticalCombo", this.verticalCombo);
        data.set("alturaCombo", this.alturaCombo);
        data.set("Doubley-vertical", this.DoubleyV);
        data.set("vertical-limit", this.vLimit);
        data.set("horizontal-wtap", this.kbWtapH);
        data.set("vertical-wtap", this.kbWtapV);
        data.set("vertical-wtap-mult", this.kbWtapVMult);
        data.set("wtap-ticks-interval", this.wtapTicksInterval);
        data.set("wtap-ticks", this.maxWtapTicks);
        data.set("pots-m", this.potM);
        data.set("pots-j", this.potJ);
        data.set("pots-l", this.potL);
        data.set("second-packet-enabled", this.secondPacketEnabled);
        data.set("second-packet-horizontal-mult", this.secondPacketHMult);
        data.set("second-packet-vertical-mult", this.secondPacketVMult);
        data.set("second-packet-delay", this.secondPacketInterval);
        data.set("on-fly-in-packet-beta", this.onPacketFly_beta);
        data.set("pearl-horizontal-offset", this.pearlHOffset);
        data.set("pearl-vertical-offset", this.pearlVOffset);
        data.set("pearl-damage", this.pearlDamage);
    }
    
    public void load(final ConfigurationSection data) {
        this.inheritVelocity = data.getBoolean("inherit-velocity", true);
        this.friction = (Double)data.getDouble("friction", 1.0);
        this.sprintSlowdown = (Double)data.getDouble("sprint-slowdown", 1.0);
        this.yawBasedCalculation = data.getBoolean("yaw-based-calculation", true);
        this.kbX = (Double)data.getDouble("x", 1.0);
        this.kbV = (Double)data.getDouble("vertical", 1.0);
        this.kbZ = (Double)data.getDouble("z", 1.0);
        this.verticalCombo = (Double)data.getDouble("verticalCombo", 0.1);
        this.alturaCombo = data.getDouble("alturaCombo", 4.0);
        this.DoubleyV = data.getBoolean("Doubley-vertical", false);
        this.vLimit = (Double)data.getDouble("vertical-limit", 0.0);
        this.kbWtapH = (Double)data.getDouble("horizontal-wtap", 1.0);
        this.kbWtapV = (Double)data.getDouble("vertical-wtap", 1.0);
        this.kbWtapVMult = data.getBoolean("vertical-wtap-mult", true);
        this.maxWtapTicks = data.getInt("wtap-ticks", 4);
        this.wtapTicksInterval = data.getBoolean("wtap-ticks-interval", false);
        this.potM = (Double)data.getDouble("pots-m", 0.05000000074505806);
        this.potJ = (Double)data.getDouble("pots-j", 0.5);
        this.potL = (Double)data.getDouble("pots-l", -20.0);
        this.secondPacketEnabled = data.getBoolean("second-packet-enabled", true);
        this.secondPacketHMult = (Double)data.getDouble("second-packet-horizontal-mult", 0.5);
        this.secondPacketVMult = (Double)data.getDouble("second-packet-vertical-mult", 0.5);
        this.secondPacketInterval = data.getInt("second-packet-delay", 4);
        this.onPacketFly_beta = data.getBoolean("on-fly-in-packet-beta", false);
        this.pearlHOffset = (Double)data.getDouble("pearl-horizontal-offset", 0.5);
        this.pearlVOffset = (Double)data.getDouble("pearl-vertical-offset", 0.2199999988079071);
        this.pearlDamage = (Double)data.getDouble("pearl-damage", 5.0);
    }
    
    public void apply() {
        for (final EntityPlayer eh : MinecraftServer.getServer().getPlayerList().players) {
            if (eh.getPrivateKnockback() != null) {
                continue;
            }
        }
    }
}
*/
