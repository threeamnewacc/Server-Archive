package us.zonix.practice.settings.item;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.zonix.practice.util.ItemBuilder;
import us.zonix.practice.util.inventory.UtilItem;

public enum ProfileOptionsItem {
    DUEL_REQUESTS(
        UtilItem.createItem(Material.LEASH, 1, (short)0, ChatColor.RED.toString() + ChatColor.BOLD + "Duel Requests"), "Do you want to accept duel requests?"
    ),
    PARTY_INVITES(
        UtilItem.createItem(Material.PAPER, 1, (short)0, ChatColor.RED.toString() + ChatColor.BOLD + "Party Invites"),
        "Do you want to accept party invitations?"
    ),
    TOGGLE_SCOREBOARD(
        UtilItem.createItem(Material.BOOKSHELF, 1, (short)0, ChatColor.RED.toString() + ChatColor.BOLD + "Toggle Scoreboard"), "Toggle your scoreboard"
    ),
    ALLOW_SPECTATORS(
        UtilItem.createItem(Material.COMPASS, 1, (short)0, ChatColor.RED.toString() + ChatColor.BOLD + "Allow Spectators"),
        "Allow players to spectate your matches?"
    ),
    TOGGLE_TIME(
        UtilItem.createItem(Material.SLIME_BALL, 1, (short)0, ChatColor.RED.toString() + ChatColor.BOLD + "Toggle Time"), "Toggle between day, sunset & night"
    ),
    TOGGLE_PING(
        UtilItem.createItem(Material.FLINT_AND_STEEL, 1, (short)0, ChatColor.RED.toString() + ChatColor.BOLD + "Toggle Ping Matchmaking"),
        "Toggle between -1, 25, 50, 75, 100 ping ranges"
    ),
    TOGGLE_VISIBILITY(
        UtilItem.createItem(Material.WATCH, 1, (short)0, ChatColor.RED.toString() + ChatColor.BOLD + "Toggle Visibility"), "Toggle your visibility"
    );

    private ItemStack item;
    private List<String> description;

    private ProfileOptionsItem(ItemStack item, String description) {
        this.item = item;
        this.description = new ArrayList<>();
        this.description.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");
        String parts = "";

        for (int i = 0; i < description.split(" ").length; i++) {
            String part = description.split(" ")[i];
            parts = parts + part + " ";
            if (i == 4 || i + 1 == description.split(" ").length) {
                this.description.add(ChatColor.GRAY + parts.trim());
                parts = "";
            }
        }

        this.description.add(" ");
    }

    public ItemStack getItem(ProfileOptionsItemState state) {
        if (this == DUEL_REQUESTS || this == PARTY_INVITES || this == ALLOW_SPECTATORS || this == TOGGLE_VISIBILITY) {
            List<String> lore = new ArrayList<>(this.description);
            lore.add(
                "  "
                    + (state == ProfileOptionsItemState.ENABLED ? ChatColor.GREEN + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ")
                    + ChatColor.GRAY
                    + this.getOptionDescription(ProfileOptionsItemState.ENABLED)
            );
            lore.add(
                "  "
                    + (state == ProfileOptionsItemState.DISABLED ? ChatColor.RED + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ")
                    + ChatColor.GRAY
                    + this.getOptionDescription(ProfileOptionsItemState.DISABLED)
            );
            lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");
            return new ItemBuilder(this.item).lore(lore).build();
        } else if (this == TOGGLE_TIME) {
            List<String> lore = new ArrayList<>(this.description);
            lore.add(
                "  "
                    + (state == ProfileOptionsItemState.DAY ? ChatColor.YELLOW + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ")
                    + ChatColor.GRAY
                    + this.getOptionDescription(ProfileOptionsItemState.DAY)
            );
            lore.add(
                "  "
                    + (state == ProfileOptionsItemState.SUNSET ? ChatColor.GOLD + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ")
                    + ChatColor.GRAY
                    + this.getOptionDescription(ProfileOptionsItemState.SUNSET)
            );
            lore.add(
                "  "
                    + (state == ProfileOptionsItemState.NIGHT ? ChatColor.BLUE + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ")
                    + ChatColor.GRAY
                    + this.getOptionDescription(ProfileOptionsItemState.NIGHT)
            );
            lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");
            return new ItemBuilder(this.item).lore(lore).build();
        } else if (this == TOGGLE_SCOREBOARD) {
            List<String> lore = new ArrayList<>(this.description);
            lore.add(
                "  "
                    + (state == ProfileOptionsItemState.ENABLED ? ChatColor.GREEN + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ")
                    + ChatColor.GRAY
                    + this.getOptionDescription(ProfileOptionsItemState.ENABLED)
            );
            lore.add(
                "  "
                    + (state == ProfileOptionsItemState.SHOW_PING ? ChatColor.YELLOW + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ")
                    + ChatColor.GRAY
                    + this.getOptionDescription(ProfileOptionsItemState.SHOW_PING)
            );
            lore.add(
                "  "
                    + (state == ProfileOptionsItemState.DISABLED ? ChatColor.RED + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ")
                    + ChatColor.GRAY
                    + this.getOptionDescription(ProfileOptionsItemState.DISABLED)
            );
            lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");
            return new ItemBuilder(this.item).lore(lore).build();
        } else if (this == TOGGLE_PING) {
            List<String> lore = new ArrayList<>(this.description);
            lore.add(
                "  "
                    + (state == ProfileOptionsItemState.NO_RANGE ? ChatColor.RED + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ")
                    + ChatColor.GRAY
                    + this.getOptionDescription(ProfileOptionsItemState.NO_RANGE)
            );
            lore.add(
                "  "
                    + (state == ProfileOptionsItemState.RANGE_25 ? ChatColor.YELLOW + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ")
                    + ChatColor.GRAY
                    + this.getOptionDescription(ProfileOptionsItemState.RANGE_25)
            );
            lore.add(
                "  "
                    + (state == ProfileOptionsItemState.RANGE_50 ? ChatColor.YELLOW + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ")
                    + ChatColor.GRAY
                    + this.getOptionDescription(ProfileOptionsItemState.RANGE_50)
            );
            lore.add(
                "  "
                    + (state == ProfileOptionsItemState.RANGE_75 ? ChatColor.YELLOW + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ")
                    + ChatColor.GRAY
                    + this.getOptionDescription(ProfileOptionsItemState.RANGE_75)
            );
            lore.add(
                "  "
                    + (state == ProfileOptionsItemState.RANGE_100 ? ChatColor.YELLOW + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ")
                    + ChatColor.GRAY
                    + this.getOptionDescription(ProfileOptionsItemState.RANGE_100)
            );
            lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");
            return new ItemBuilder(this.item).lore(lore).build();
        } else {
            return this.getItem(ProfileOptionsItemState.DISABLED);
        }
    }

    public String getOptionDescription(ProfileOptionsItemState state) {
        if (this != DUEL_REQUESTS && this != PARTY_INVITES && this != ALLOW_SPECTATORS && this != TOGGLE_VISIBILITY) {
            if (this == TOGGLE_TIME) {
                if (state == ProfileOptionsItemState.DAY) {
                    return "Day";
                }

                if (state == ProfileOptionsItemState.SUNSET) {
                    return "Sunset";
                }

                if (state == ProfileOptionsItemState.NIGHT) {
                    return "Night";
                }
            } else if (this == TOGGLE_SCOREBOARD) {
                if (state == ProfileOptionsItemState.ENABLED) {
                    return "Enable";
                }

                if (state == ProfileOptionsItemState.SHOW_PING) {
                    return "Show Ping";
                }

                if (state == ProfileOptionsItemState.DISABLED) {
                    return "Disable";
                }
            } else if (this == TOGGLE_PING) {
                if (state == ProfileOptionsItemState.NO_RANGE) {
                    return "No Ping Range";
                }

                if (state == ProfileOptionsItemState.RANGE_25) {
                    return "25ms Range";
                }

                if (state == ProfileOptionsItemState.RANGE_50) {
                    return "50ms Range";
                }

                if (state == ProfileOptionsItemState.RANGE_75) {
                    return "75ms Range";
                }

                if (state == ProfileOptionsItemState.RANGE_100) {
                    return "100ms Range";
                }
            }
        } else {
            if (state == ProfileOptionsItemState.ENABLED) {
                return "Enable";
            }

            if (state == ProfileOptionsItemState.DISABLED) {
                return "Disable";
            }
        }

        return this.getOptionDescription(ProfileOptionsItemState.DISABLED);
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
