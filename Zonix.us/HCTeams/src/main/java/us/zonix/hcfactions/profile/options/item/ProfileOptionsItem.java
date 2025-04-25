package us.zonix.hcfactions.profile.options.item;

import org.apache.commons.lang3.StringEscapeUtils;
import us.zonix.hcfactions.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.zonix.hcfactions.util.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public enum ProfileOptionsItem {

    FOUND_DIAMOND_MESSAGES(new ItemBuilder(Material.DIAMOND).name(ChatColor.BLUE + "Found Diamonds").build(), "Do you want to see found-diamond messages?"),
    DEATH_MESSAGES(new ItemBuilder(Material.SKULL_ITEM).name(ChatColor.BLUE + "Death Messages").build(), "Do you want to see death messages?"),
    PUBLIC_MESSAGES(new ItemBuilder(Material.SIGN).name(ChatColor.BLUE + "Public Messages").build(), "Do you want to receive public messages?");

    private ItemStack item;
    private List<String> description;

    ProfileOptionsItem(ItemStack item, String description) {
        this.item = item;
        this.description = new ArrayList<>();

        String parts = "";
        for (int i = 0; i < description.split(" ").length; i++) {
            String part = description.split(" ")[i];

            parts += part + " ";

            if (i == 4 || (i + 1) == description.split(" ").length) {
                this.description.add(ChatColor.GRAY + parts.trim());
                parts = "";
            }
        }

        this.description.add(" ");
    }

    public ItemStack getItem(ProfileOptionsItemState state) {
        if (this == DEATH_MESSAGES || this == FOUND_DIAMOND_MESSAGES || this == PUBLIC_MESSAGES) {
            List<String> lore = new ArrayList<>(description);

            lore.add("  " + (state == ProfileOptionsItemState.ENABLED ? ChatColor.BLUE + StringEscapeUtils.unescapeHtml4("&#9658;") + " " : "  ") + ChatColor.GRAY + getOptionDescription(ProfileOptionsItemState.ENABLED));
            lore.add("  " + (state == ProfileOptionsItemState.DISABLED ? ChatColor.BLUE + StringEscapeUtils.unescapeHtml4("&#9658;") + " "  : "  ") + ChatColor.GRAY + getOptionDescription(ProfileOptionsItemState.DISABLED));

            return new ItemBuilder(item).lore(lore).build();
        }

        return getItem(ProfileOptionsItemState.DISABLED);
    }

    public String getOptionDescription(ProfileOptionsItemState state) {
        if (this == FOUND_DIAMOND_MESSAGES || this == DEATH_MESSAGES || this == PUBLIC_MESSAGES) {

            if (state == ProfileOptionsItemState.ENABLED) {
                return "Show messages";
            } else if (state == ProfileOptionsItemState.DISABLED) {
                return "Hide messages";
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
