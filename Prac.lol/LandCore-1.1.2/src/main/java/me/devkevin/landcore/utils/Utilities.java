package me.devkevin.landcore.utils;

import org.bukkit.Bukkit;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 19/01/2023 @ 16:56
 * Utilities / land.pvp.core.utils / LandCore
 */
public class Utilities {

    public static String SERVER_VERSION_PACKAGE;

    static {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();

        SERVER_VERSION_PACKAGE = packageName.substring(packageName.lastIndexOf('.') + 1);
    }
}
