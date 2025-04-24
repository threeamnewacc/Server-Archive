package me.devkevin.practice.options.item;

import lombok.AllArgsConstructor;
import club.inverted.chatcolor.CC;
import me.devkevin.practice.util.ItemBuilder;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 11/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@AllArgsConstructor
public enum ProfileOptionsItem {

    DUEL_REQUESTS(new ItemBuilder(Material.DIAMOND_SWORD).name(CC.DARK_RED + "Duel Requests").lore(CC.GRAY + "Do you want to accept duel requests?").build()),
    PARTY_INVITES(new ItemBuilder(Material.SKULL_ITEM).name(CC.DARK_RED + "Party Invites").lore(CC.GRAY + "Do you want to accept party invitations?").durability(3).build()),
    TOGGLE_SCOREBOARD(new ItemBuilder(Material.PAINTING).name(CC.DARK_RED + "Toggle Scoreboard").lore("Toggle your scoreboard").build()),
    ALLOW_SPECTATORS(new ItemBuilder(Material.DIAMOND_HELMET).name(CC.DARK_RED + "Allow Spectators").lore(CC.GRAY + "Allow players to spectate your matches?").build()),
    TOGGLE_TIME(new ItemBuilder(Material.SLIME_BALL).name(CC.DARK_RED + "Toggle Time").lore(CC.GRAY + "Toggle between day, sunset & night").build()),
    TOGGLE_PING(new ItemBuilder(Material.FLINT_AND_STEEL).name(CC.DARK_RED + "Toggle Ping Matchmaking").lore(CC.GRAY + "Toggle between -1, 25, 50, 75, 100 ping ranges").build()),
    TOGGLE_VISIBILITY(new ItemBuilder(Material.WATCH).name(CC.DARK_RED + "Toggle Visibility").lore(CC.GRAY + "Toggle your visibility").build());

    private final ItemStack item;

    public ItemStack getItem(ProfileOptionsItemState state) {
        if (this == DUEL_REQUESTS || this == PARTY_INVITES || this == ALLOW_SPECTATORS || this == TOGGLE_VISIBILITY) {
            List<String> lore = new ArrayList<>();

            lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");
            lore.add(CC.GRAY + "Do you want to accept duel requests?");
            lore.add("");
            lore.add("  " + (state == ProfileOptionsItemState.ENABLED ? ChatColor.GREEN + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ") + ChatColor.GRAY + getOptionDescription(ProfileOptionsItemState.ENABLED));
            lore.add("  " + (state == ProfileOptionsItemState.DISABLED ? ChatColor.RED + StringEscapeUtils.unescapeHtml4("&#9658;") + " "  : "  ") + ChatColor.GRAY + getOptionDescription(ProfileOptionsItemState.DISABLED));
            lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");

            return new ItemBuilder(item).lore(lore).build();
        }

        else if(this == TOGGLE_TIME) {
            List<String> lore = new ArrayList<>();

            lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");
            lore.add(CC.GRAY + "Toggle between day, sunset & night");
            lore.add("");
            lore.add("  " + (state == ProfileOptionsItemState.DAY ? ChatColor.YELLOW + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ") + ChatColor.GRAY + getOptionDescription(ProfileOptionsItemState.DAY));
            lore.add("  " + (state == ProfileOptionsItemState.SUNSET ? ChatColor.GOLD + StringEscapeUtils.unescapeHtml4("&#9658;") + " "  : "  ") + ChatColor.GRAY + getOptionDescription(ProfileOptionsItemState.SUNSET));
            lore.add("  " + (state == ProfileOptionsItemState.NIGHT ? ChatColor.BLUE + StringEscapeUtils.unescapeHtml4("&#9658;") + " "  : "  ") + ChatColor.GRAY + getOptionDescription(ProfileOptionsItemState.NIGHT));
            lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");

            return new ItemBuilder(item).lore(lore).build();
        }

        else if(this == TOGGLE_SCOREBOARD) {
            List<String> lore = new ArrayList<>();

            lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");
            lore.add(CC.GRAY + "Toggle your scoreboard");
            lore.add("");
            lore.add("  " + (state == ProfileOptionsItemState.ENABLED ? ChatColor.GREEN + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ") + ChatColor.GRAY + getOptionDescription(ProfileOptionsItemState.ENABLED));
            lore.add("  " + (state == ProfileOptionsItemState.DISABLED ? ChatColor.RED + StringEscapeUtils.unescapeHtml4("&#9658;") + " "  : "  ") + ChatColor.GRAY + getOptionDescription(ProfileOptionsItemState.DISABLED));
            lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");

            return new ItemBuilder(item).lore(lore).build();
        }

        else if(this == TOGGLE_PING) {
            List<String> lore = new ArrayList<>();

            lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");
            lore.add("  " + (state == ProfileOptionsItemState.NO_RANGE ? ChatColor.RED + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ") + ChatColor.GRAY + getOptionDescription(ProfileOptionsItemState.NO_RANGE));
            lore.add("  " + (state == ProfileOptionsItemState.RANGE_25 ? ChatColor.YELLOW + StringEscapeUtils.unescapeHtml4("&#9658;") + " "  : "  ") + ChatColor.GRAY + getOptionDescription(ProfileOptionsItemState.RANGE_25));
            lore.add("  " + (state == ProfileOptionsItemState.RANGE_50 ? ChatColor.YELLOW + StringEscapeUtils.unescapeHtml4("&#9658;") + " "  : "  ") + ChatColor.GRAY + getOptionDescription(ProfileOptionsItemState.RANGE_50));
            lore.add("  " + (state == ProfileOptionsItemState.RANGE_75 ? ChatColor.YELLOW + StringEscapeUtils.unescapeHtml4("&#9658;") + " "  : "  ") + ChatColor.GRAY + getOptionDescription(ProfileOptionsItemState.RANGE_75));
            lore.add("  " + (state == ProfileOptionsItemState.RANGE_100 ? ChatColor.YELLOW + StringEscapeUtils.unescapeHtml4("&#9658;") + " "  : "  ") + ChatColor.GRAY + getOptionDescription(ProfileOptionsItemState.RANGE_100));
            lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");

            return new ItemBuilder(item).lore(lore).build();
        }

        return getItem(ProfileOptionsItemState.DISABLED);
    }

    public String getOptionDescription(ProfileOptionsItemState state) {
        if (this == DUEL_REQUESTS || this == PARTY_INVITES || this == ALLOW_SPECTATORS || this == TOGGLE_VISIBILITY) {

            if (state == ProfileOptionsItemState.ENABLED) {
                return "Enable";
            } else if (state == ProfileOptionsItemState.DISABLED) {
                return "Disable";
            }
        }

        else if(this == TOGGLE_TIME) {
            if (state == ProfileOptionsItemState.DAY) {
                return "Day";
            } else if (state == ProfileOptionsItemState.SUNSET) {
                return "Sunset";
            } else if (state == ProfileOptionsItemState.NIGHT) {
                return "Night";
            }
        }

        else if(this == TOGGLE_SCOREBOARD) {
            if (state == ProfileOptionsItemState.ENABLED) {
                return "Enable";
            } else if (state == ProfileOptionsItemState.SHOW_PING) {
                return "Show Ping";
            } else if (state == ProfileOptionsItemState.DISABLED) {
                return "Disable";
            }
        }

        else if(this == TOGGLE_PING) {
            if (state == ProfileOptionsItemState.NO_RANGE) {
                return "No Ping Range";
            } else if (state == ProfileOptionsItemState.RANGE_25) {
                return "25ms Range";
            } else if (state == ProfileOptionsItemState.RANGE_50) {
                return "50ms Range";
            } else if (state == ProfileOptionsItemState.RANGE_75) {
                return "75ms Range";
            } else if (state == ProfileOptionsItemState.RANGE_100) {
                return "100ms Range";
            }
        }


        return getOptionDescription(ProfileOptionsItemState.DISABLED);
    }

    public static ProfileOptionsItem fromItem(ItemStack itemStack) {
        for (ProfileOptionsItem item : values()) {
            for (ProfileOptionsItemState state : ProfileOptionsItemState.values()) {
                if (item.getItem(state).isSimilar(itemStack)) {
                    return item;
                }
            }
        }
        return null;
    }
}
