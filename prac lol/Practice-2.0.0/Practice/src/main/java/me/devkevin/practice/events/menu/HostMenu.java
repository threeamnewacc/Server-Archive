package me.devkevin.practice.events.menu;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.practice.Practice;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.inventory.InventoryUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

import static me.devkevin.practice.util.PlayerUtil.PLACEHOLDER_ITEM;

/**
 * Copyright 23/08/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class HostMenu {
    private final Practice plugin = Practice.getInstance();

    private final InventoryUI hostMenu = new InventoryUI(CC.DARK_RED + "Host an event", true, 5);

    public HostMenu() {
        this.hostMenu();
    }

    private void hostMenu() {
        for (int i = 0; i < 9 * 5; i++) {
            this.hostMenu.setItem(i, new InventoryUI.EmptyClickableItem(PLACEHOLDER_ITEM));
        }

        this.hostMenu.setItem(0, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.BED)
                .name(CC.RED + "Back to General")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                player.openInventory(plugin.getGeneralSettingMenu().getGeneralMenu().getCurrentPage());
            }
        });

        this.hostMenu.setItem(11, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.LEASH).name(CC.DARK_RED + "Sumo")
                        .lore(Arrays.asList(
                                "",
                                CC.GRAY + "Knock your opponent off",
                                CC.GRAY + "the platform until you",
                                CC.GRAY + "are the last player alive",
                                "",
                                CC.RED + "Needed rank: " + CC.GOLD + "Gold",
                                "",
                                CC.GREEN + "Click here to host..."
                        ))
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                if (!player.hasPermission("practice.donors.gold")) {
                    player.sendMessage("");
                    player.sendMessage(CC.RED + "You cannot host this event with " + profile.getGrant().getRank().getColor() + profile.getGrant().getRank().getName() + CC.RED + " rank.");
                    player.sendMessage(CC.GRAY + "Purchase the Gold rank at https://store.inverted.club/ to host events on your own.");
                    player.sendMessage("");
                    return;
                }

                player.performCommand("hostevent sumo");
            }
        });

        this.hostMenu.setItem(13, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.TNT).name(CC.DARK_RED + "TnTTag")
                        .lore(Arrays.asList(
                                "",
                                CC.GRAY + "Knock your opponent with",
                                CC.GRAY + "the TNT before explode until",
                                CC.GRAY + "you are the last player alive",
                                "",
                                CC.RED + "Needed rank: " + CC.DARK_GREEN + "Emerald",
                                "",
                                CC.GREEN + "Click here to host...",
                                ""
                        ))
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                if (!player.hasPermission("practice.donors.emerald")) {
                    player.sendMessage("");
                    player.sendMessage(CC.RED + "You cannot host this event with " + profile.getGrant().getRank().getColor() + profile.getGrant().getRank().getName() + CC.RED + " rank.");
                    player.sendMessage(CC.GRAY + "Purchase the Emerald rank at https://store.inverted.club/ to host events on your own.");
                    player.sendMessage("");
                    return;
                }

                //player.performCommand("hostevent TnTTag");
            }
        });

        this.hostMenu.setItem(15, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.REDSTONE_BLOCK).name(CC.RED + CC.BOLD + "???")
                        .lore(Arrays.asList(
                                "",
                                CC.GRAY + "Under development",
                                ""
                        ))
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                player.sendMessage(CC.RED + "Under development");
                /*Profile profile = Profile.getByUuid(player.getUniqueId());

                if (!profile.getRank().hasRank(Rank.GOLD)) {
                    player.sendMessage("");
                    player.sendMessage(CC.RED + "You cannot host this event with " + profile.getRank().getColor() + profile.getRank().getName() + CC.RED + " rank.");
                    player.sendMessage(CC.GRAY + "Purchase the Gold rank at store.udrop.club to host events on your own.");
                    player.sendMessage("");
                    return;
                }

                player.performCommand("host sumo");*/
            }
        });

        this.hostMenu.setItem(29, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.REDSTONE_BLOCK).name(CC.RED + CC.BOLD + "???")
                        .lore(Arrays.asList(
                                "",
                                CC.GRAY + "Under development",
                                ""
                        ))
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                player.sendMessage(CC.RED + "Under development");
                /*Profile profile = Profile.getByUuid(player.getUniqueId());

                if (!profile.getRank().hasRank(Rank.GOLD)) {
                    player.sendMessage("");
                    player.sendMessage(CC.RED + "You cannot host this event with " + profile.getRank().getColor() + profile.getRank().getName() + CC.RED + " rank.");
                    player.sendMessage(CC.GRAY + "Purchase the Gold rank at store.udrop.club to host events on your own.");
                    player.sendMessage("");
                    return;
                }

                player.performCommand("host sumo");*/
            }
        });

        this.hostMenu.setItem(31, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.REDSTONE_BLOCK).name(CC.RED + CC.BOLD + "???")
                        .lore(Arrays.asList(
                                "",
                                CC.GRAY + "Under development",
                                ""
                        ))
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                player.sendMessage(CC.RED + "Under development");
                /*Profile profile = Profile.getByUuid(player.getUniqueId());

                if (!profile.getRank().hasRank(Rank.GOLD)) {
                    player.sendMessage("");
                    player.sendMessage(CC.RED + "You cannot host this event with " + profile.getRank().getColor() + profile.getRank().getName() + CC.RED + " rank.");
                    player.sendMessage(CC.GRAY + "Purchase the Gold rank at store.udrop.club to host events on your own.");
                    player.sendMessage("");
                    return;
                }

                player.performCommand("host sumo");*/
            }
        });

        this.hostMenu.setItem(33, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.REDSTONE_BLOCK).name(CC.RED + CC.BOLD + "???")
                        .lore(Arrays.asList(
                                "",
                                CC.GRAY + "Under development",
                                ""
                        ))
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                player.sendMessage(CC.RED + "Under development");
                /*Profile profile = Profile.getByUuid(player.getUniqueId());

                if (!profile.getRank().hasRank(Rank.GOLD)) {
                    player.sendMessage("");
                    player.sendMessage(CC.RED + "You cannot host this event with " + profile.getRank().getColor() + profile.getRank().getName() + CC.RED + " rank.");
                    player.sendMessage(CC.GRAY + "Purchase the Gold rank at store.udrop.club to host events on your own.");
                    player.sendMessage("");
                    return;
                }

                player.performCommand("host sumo");*/
            }
        });
    }
}
