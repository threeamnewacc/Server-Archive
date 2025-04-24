package me.devkevin.practice.options;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import club.inverted.chatcolor.CC;
import me.devkevin.practice.options.item.ProfileOptionsItem;
import me.devkevin.practice.options.item.ProfileOptionsItemState;
import me.devkevin.practice.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

/**
 * Copyright 11/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Accessors(chain = true)
public class ProfileOptions {

    @Getter @Setter private boolean duelRequests = true;
    @Getter @Setter private boolean partyInvites = true;
    @Getter @Setter private boolean spectators = true;
    @Getter @Setter private boolean scoreboard = true;
    @Getter @Setter private ProfileOptionsItemState time = ProfileOptionsItemState.DAY;
    @Getter @Setter private ProfileOptionsItemState pingBased = ProfileOptionsItemState.NO_RANGE;
    @Getter @Setter private boolean visibility = true;

    public Inventory getInventory() {
        Inventory toReturn = Bukkit.createInventory(null, 54, CC.D_GRAY + "Settings");

        int[] slots = new int[] {1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};

        toReturn.setItem(0, new ItemBuilder(Material.BED).name(CC.RED + "Back to General").build());

        toReturn.setItem(4,
                new ItemBuilder(Material.NETHER_STAR).name(CC.DARK_RED + "Settings")
                        .lore("")
                        .lore(CC.GRAY + "Change your settings as you want.")
                        .lore("")
                        .build());

        toReturn.setItem(20, ProfileOptionsItem.DUEL_REQUESTS.getItem(duelRequests ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
        toReturn.setItem(22, ProfileOptionsItem.PARTY_INVITES.getItem(partyInvites ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
        toReturn.setItem(24, ProfileOptionsItem.ALLOW_SPECTATORS.getItem(spectators ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
        toReturn.setItem(38, ProfileOptionsItem.TOGGLE_SCOREBOARD.getItem(scoreboard ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
        toReturn.setItem(40, ProfileOptionsItem.TOGGLE_TIME.getItem(time));
        toReturn.setItem(42, ProfileOptionsItem.TOGGLE_PING.getItem(pingBased));


        toReturn.setItem(12, ProfileOptionsItem.TOGGLE_VISIBILITY.getItem(visibility ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
        toReturn.setItem(14, new ItemBuilder(Material.BOOKSHELF).name(CC.DARK_RED + "General Settings").build());

        for (int i : slots) {
            toReturn.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(15).build());
        }
        return toReturn;
    }
}
