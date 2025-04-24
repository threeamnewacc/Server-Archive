package me.devkevin.practice.queue.menu.party;

import lombok.Getter;
import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.inventory.InventoryUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collection;

/**
 * Copyright 29/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class PartyQueueJoinMenu {

    private Practice plugin = Practice.getInstance();

    // party main menu for 2v2, 3v3, 4v4, 5v5
    private final InventoryUI partyQueueMenu = new InventoryUI(CC.GOLD + "Select a party queue type", true, 1);


    // party menu 2v2
    private final InventoryUI party2v2Menu = new InventoryUI(CC.GOLD + "Select Type", true, 1);
    private final InventoryUI party2v2MenuUn_ranked = new InventoryUI(CC.GOLD + "Select ladder", true, 1);
    private final InventoryUI party2v2MenuRanked = new InventoryUI(CC.GOLD + "Select ladder", true, 1);

    // party menu 3v3
    private final InventoryUI party3v3Menu = new InventoryUI(CC.GOLD + "Select Type", true, 1);
    private final InventoryUI party3v3MenuUn_ranked = new InventoryUI(CC.GOLD + "Select ladder", true, 1);
    private final InventoryUI party3v3MenuRanked = new InventoryUI(CC.GOLD + "Select ladder", true, 1);

    // party menu 4v4
    private final InventoryUI party4v4Menu = new InventoryUI(CC.GOLD + "Select Type", true, 1);
    private final InventoryUI party4v4MenuUn_ranked = new InventoryUI(CC.GOLD + "Select ladder", true, 1);
    private final InventoryUI party4v4MenuRanked = new InventoryUI(CC.GOLD + "Select ladder", true, 1);

    // party menu 5v5
    private final InventoryUI party5v5Menu = new InventoryUI(CC.GOLD + "Select Type", true, 1);
    private final InventoryUI party5v5MenuUn_ranked = new InventoryUI(CC.GOLD + "Select ladder", true, 1);
    private final InventoryUI party5v5MenuRanked = new InventoryUI(CC.GOLD + "Select ladder", true, 1);

    public PartyQueueJoinMenu() {
        this.QueueMenu();
    }

    private void QueueMenu() {
        Collection<Kit> kits = Practice.getInstance().getKitManager().getKits();

        // 2v2 start
        this.partyQueueMenu.setItem(0, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.DIAMOND_SWORD)
                .name(CC.GOLD + "2v2 Queue")
                .amount(2)
                .lore("")
                .lore(CC.YELLOW + "Click here to join 2v2 queue.")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                player.openInventory(PartyQueueJoinMenu.this.party2v2Menu.getCurrentPage());
            }
        });
        this.party2v2Menu.setItem(3, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.EMERALD)
                .name(CC.GOLD + "2v2 UnRanked")
                .lore("")
                .lore(CC.YELLOW + "Click here to select unranked type.")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                player.openInventory(PartyQueueJoinMenu.this.party2v2MenuUn_ranked.getCurrentPage());
            }
        });
        this.party2v2Menu.setItem(5, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.DIAMOND)
                .name(CC.GOLD + "2v2 Ranked")
                .lore("")
                .lore(CC.YELLOW + "Click here to select ranked type.")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                player.openInventory(PartyQueueJoinMenu.this.party2v2MenuRanked.getCurrentPage());
            }
        });

        for (Kit kit : kits) {
            if (kit.isEnabled()) {
                if (!kit.getName().contains("HCF TeamFight")) {
                    this.party2v2MenuUn_ranked.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                                                           @Override
                                                           public void onClick(InventoryClickEvent event) {
                                                               Player player = (Player) event.getWhoClicked();
                                                               Profile profile = Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId());

                                                               if (profile == null) {
                                                                   return;
                                                               }

                                                               if (profile.isEditing()) {
                                                                   player.sendMessage(CC.RED + "You cannot queue right now.");
                                                                   return;
                                                               }

                                                               if (kit.getName().contains("HCF TeamFight")) {
                                                                   Party party = plugin.getPartyManager().getParty(player.getUniqueId());

                                                                   if(party == null) {
                                                                       player.sendMessage(ChatColor.RED + "This kit can only be played in a party.");
                                                                       player.closeInventory();
                                                                       return;
                                                                   }

                                                                   if (party.getArchers().isEmpty() && party.getBards().isEmpty()) {
                                                                       player.sendMessage(ChatColor.RED + "You must specify your party's roles (you must atleast ahve one archer or one bard)" +
                                                                               ".\nUse: /party hcteams");
                                                                       player.closeInventory();
                                                                       player.closeInventory();
                                                                       return;
                                                                   }
                                                               }

                                                               /*addToQueue2V2(
                                                                       player,
                                                                       Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId()),
                                                                       kit,
                                                                       QueueType.UN_RANKED_2v2
                                                               );*/
                                                           }
                                                       }

                    );
                }
            }
            if (kit.isRanked()) {
                this.party2v2MenuRanked.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        Player player = (Player) event.getWhoClicked();
                        Profile profile = Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId());

                        if (profile == null) {
                            return;
                        }

                        if (profile.isEditing()) {
                            player.sendMessage(CC.RED + "You cannot queue right now.");
                            return;
                        }

                        if (kit.getName().contains("HCF TeamFight")) {

                            Party party = plugin.getPartyManager().getParty(player.getUniqueId());

                            if(party == null) {
                                player.sendMessage(ChatColor.RED + "This kit can only be played in a party.");
                                player.closeInventory();
                                return;
                            }

                            if(party.getArchers().isEmpty() || party.getBards().isEmpty()) {
                                player.sendMessage(ChatColor.RED + "You must specify your party's roles.\nUse: /party hcteams");
                                player.closeInventory();
                                player.closeInventory();
                                return;
                            }
                        }

                        /*addToQueue2V2(
                                player,
                                Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId()),
                                kit,
                                QueueType.RANKED_2v2
                        );*/
                    }
                });
            }
        }
        // 2v2 end
    }
}
