package me.enzol.spigot.knockback;

import me.enzol.spigot.command.KnockbackCommand;
import net.minecraft.server.MinecraftServer;

import java.util.*;
import java.io.*;

public class KnockbackModule
{
    public static KnockbackModule INSTANCE;
    public KnockbackProfile activeProfile;
    public Map<String, KnockbackProfile> profiles;

    static {
        KnockbackModule.INSTANCE = new KnockbackModule();
    }

    public static KnockbackModule get() {
        return KnockbackModule.INSTANCE;
    }

    public KnockbackProfile getActiveProfile() {
        return this.activeProfile;
    }

    public KnockbackModule() {
        this.profiles = new HashMap<String, KnockbackProfile>();
        MinecraftServer.getServer().server.getCommandMap().register("tspigot", new KnockbackCommand());
        final File knockback = new File("Knockback");
        if (!knockback.exists()) {
            knockback.mkdir();
        }
        final File[] files = knockback.listFiles();
        if (files != null) {
            File[] array;
            for (int length = (array = files).length, i = 0; i < length; ++i) {
                final File file = array[i];
                this.profiles.put(file.getName().replace(".yml", ""), new KnockbackProfile(file.getName().replace(".yml", "")));
            }
        }
        if (!this.profiles.containsKey("default")) {
            this.profiles.put("default", new KnockbackProfile("default"));
        }
    }
    public static KnockbackProfile getDfs() {
        return KnockbackModule.INSTANCE.profiles.get("default");
    }
    //Knockback for get by name (For KB Per Ladder on Practice plugins)
    public static KnockbackProfile kbG(final String name) {
        return KnockbackModule.INSTANCE.profiles.getOrDefault(name, getDfs());
    }
}