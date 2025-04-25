package me.enzol.spigot.knockback;

import java.util.Map;

public class KnockbackModule {

    public static KnockbackModule INSTANCE;
    public Map<String, KnockbackProfile> profiles;

    public static KnockbackProfile getDfs() {
        return KnockbackModule.INSTANCE.profiles.get("default");
    }
    //Knockback for get by name (For KB Per Ladder on Practice plugins)
    public static KnockbackProfile kbG(final String name) {
        return KnockbackModule.INSTANCE.profiles.getOrDefault(name, getDfs());
    }
}

