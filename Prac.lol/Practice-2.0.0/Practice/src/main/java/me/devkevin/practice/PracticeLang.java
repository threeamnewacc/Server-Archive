package me.devkevin.practice;

import club.inverted.chatcolor.CC;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.ChatColor;

/**
 * Copyright 10/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class PracticeLang {

    public static final String line = CC.GRAY + CC.STRIKETHROUGH + StringUtils.repeat("-", 30);

    // kit related
    public static final String NO_KIT_EXIST = CC.RED + "That kit doesn't exist.";
    public static final String NO_ARENA_EXIST = CC.RED + "An arena with that name does not exist.";
    public static final String ALREADY_KIT_EXIST = CC.RED + "That kit is already exist.";
    public static final String KIT_CREATED = CC.YELLOW + " kit has been created.";
    public static final String KIT_DELETED = CC.RED + " kit has been deleted.";
    public static final String KIT_ENABLE = CC.GREEN + " kit has been enabled.";
    public static final String KIT_DISABLE = CC.RED + " kit has been disabled.";
    public static final String SUMO_KIT_ENABLE = CC.GREEN + " sumo kit has been enabled.";
    public static final String SUMO_KIT_DISABLE = CC.RED + " sumo kit has been disabled.";
    public static final String BUILD_KIT_ENABLE = CC.GREEN + " build mode has been enabled.";
    public static final String BUILD_KIT_DISABLE = CC.RED + " build mode has been disabled.";
    public static final String RANKED_KIT_ENABLE = CC.GREEN + "Ranked mode has been enabled for ";
    public static final String RANKED_KIT_DISABLE = CC.RED + "Ranked mode has been disabled for ";
    public static final String ICON_KIT = CC.YELLOW + "You has been set the icon of ";
    public static final String NO_ICON_KIT = CC.YELLOW + "You must be holding an item to set the kit icon";
    public static final String KIT_SETINV = CC.YELLOW + "You has been update the inv of ";
    public static final String KIT_GETINV = CC.YELLOW + "You has been retrieved kit contents of ";
    public static final String NO_DROP_SLOT_1 = CC.RED + "You can't drop that while you're holding it in slot 1.";
    public static final String INV_NOT_FOUND = CC.RED + "Cannot find the request inventory. Maybe it expired?";
    public static final String NO_PERMISSION = CC.RED + "You don't have permission to execute that command.";
    public static final String PLAYER_NOT_FOUND = ChatColor.RED + "%s not found.";
    public static final String ERROR_STATE = ChatColor.RED + "You can't execute that command in your current state.";
    public static final String VERTICAL_BAR = StringEscapeUtils.unescapeJava("\u2503");
    public static final String HEART = StringEscapeUtils.unescapeJava("\u2764");

    /**
     * `&7&l» ` - Arrow used on the left side of item display names
     * Named left arrow due to its usage on the left side of items, despite the fact
     * the arrow is actually pointing to the right.
     * @see me.devkevin.practice.profile.hotbar.HotbarItem usage
     */
    public static final String LEFT_ARROW = ChatColor.BLUE.toString() + "» ";

    /**
     * ` &7&l«` - Arrow used on the right side of item display names
     * Named right arrow due to its usage on the right side of items, despite the fact
     * the arrow is actually pointing to the left.
     * @see me.devkevin.practice.profile.hotbar.HotbarItem usage
     */
    public static final String RIGHT_ARROW = ChatColor.BLUE.toString() + " «";


    public static final String[] blockedCommands = new String[] {
            "/gamemode creative",
            "/gamemode c",
            "/gamemode 1",
            "/gmc",
            "/kill",
            "/disguise",
            "/undisguise"
    };
}
