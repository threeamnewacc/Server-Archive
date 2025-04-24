package me.devkevin.practice.queue.menu.party;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.PracticeCache;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.tournament.state.TournamentState;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.inventory.InventoryUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import static me.devkevin.practice.util.PlayerUtil.PLACEHOLDER_ITEM;

/**
 * Copyright 23/03/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class PlayJoinMenu {
    private final Practice plugin = Practice.getInstance();

    private final InventoryUI playMenu = new InventoryUI(CC.GRAY + "Play Menu", true, 3);

    public PlayJoinMenu() {
        setupMenu();
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::setupMenu, 20L, 20L);
    }

    private void setupMenu() {
        for (int i = 0; i < 9 * 3; i++) {
            this.playMenu.setItem(i, new InventoryUI.EmptyClickableItem(PLACEHOLDER_ITEM));
        }

        if (this.plugin.getTournamentManager().getTournaments().size() >= 1 && this.plugin.getTournamentManager().getTournament().getTournamentState() == TournamentState.WAITING) {
            // 11 13 15
            // Play Menu

            playMenu.setItem(10, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.IRON_SWORD)
                    .name(CC.DARK_RED + "Queue Unranked")
                    .lore("")
                    .lore(CC.GRAY + "Casual 1v1's matches")
                    .lore(CC.GRAY + "with no loss penalty.")
                    .lore("")
                    .lore(CC.RED + "Players: " + CC.RESET + PracticeCache.getInstance().getUnrankedSoloPlayers())
                    .lore("")
                    .lore(CC.GREEN + "Click to play!")
                    .build()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    Player player = (Player)event.getWhoClicked();

                    player.closeInventory();

                    //player.openInventory(plugin.getQueueJoinMenu().getUnrankedMenu().getCurrentPage());
                }
            });
            playMenu.setItem(12, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.DIAMOND_SWORD)
                    .name(CC.DARK_RED + "Queue Ranked")
                    .lore("")
                    .lore(CC.GRAY + "Casual 1v1's matches with")
                    .lore(CC.GRAY + "a ranked aspect to them. This")
                    .lore(CC.GRAY + "includes ELO.")
                    .lore("")
                    .lore(CC.RED + "Players: " + CC.RESET + PracticeCache.getInstance().getRankedSoloPlayers())
                    .lore("")
                    .lore(CC.GREEN + "Click to play!")
                    .build()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    Player player = (Player)event.getWhoClicked();
                    Profile profile = plugin.getProfileManager().getProfileData(player.getUniqueId());

                    player.closeInventory();

                    if (!player.hasPermission("practice.donors.*") && profile.getUnrankedWins() < 10) {
                        player.sendMessage(ChatColor.RED + "You must win " + (10 - profile.getUnrankedWins()) + " Unranked Matches to join this queue.");
                        return;
                    }

                    //player.openInventory(plugin.getQueueJoinMenu().getRankedMenu().getCurrentPage());
                }
            });

            playMenu.setItem(14, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                    .name(CC.DARK_RED + "TeamFight")
                    .lore("")
                    .lore(CC.GRAY + "Fight with other factions")
                    .lore(CC.GRAY + "capping a koth to win the")
                    .lore(CC.GRAY + "the match.")
                    .lore(CC.GRAY + "switching to " + CC.YELLOW + "Bard" + CC.GRAY + ", " + CC.RED + "Archer" + CC.GRAY + ", " + CC.AQUA + "Diamond " + CC.GRAY + "kit.")
                    .lore("")
                    .lore(CC.YELLOW + CC.BOLD + "NEW")
                    .lore("")
                    .lore(CC.GREEN + "Click to play!")
                    .build()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    Player player = (Player)event.getWhoClicked();


                    player.sendMessage(CC.GREEN + "getFaction::TEST");
                }
            });

            playMenu.setItem(16, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.GOLD_SWORD)
                    .name(CC.DARK_RED + "Tournament")
                    .lore("")
                    .lore(CC.GRAY + "An tournament as been started")
                    .lore("")
                    //.lore(CC.GOLD + "Players: " + CC.RESET + tournament.getPlayers().size() + "/" + tournament.getSize())
                    .lore("")
                    .lore(CC.GOLD + "Click here to join!")
                    .build()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    Player player = (Player)event.getWhoClicked();

                    player.closeInventory();
                    player.performCommand("tournament join");
                }
            });
        } else {
            // Play Menu
            playMenu.setItem(11, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.IRON_SWORD)
                    .name(CC.DARK_RED + "Queue Unranked")
                    .lore("")
                    .lore(CC.GRAY + "Casual 1v1's matches")
                    .lore(CC.GRAY + "with no loss penalty.")
                    .lore("")
                    //.lore(CC.RED + "Players: " + CC.RESET + PracticeCache.getInstance().getUnrankedSoloPlayers())
                    .lore("")
                    .lore(CC.GREEN + "Click to play!")
                    .build()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    Player player = (Player)event.getWhoClicked();

                    player.closeInventory();

                    //player.openInventory(plugin.getQueueJoinMenu().getUnrankedMenu().getCurrentPage());
                }
            });
            playMenu.setItem(13, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.DIAMOND_SWORD)
                    .name(CC.DARK_RED + "Queue Ranked")
                    .lore("")
                    .lore(CC.GRAY + "Casual 1v1's matches with")
                    .lore(CC.GRAY + "a ranked aspect to them. This")
                    .lore(CC.GRAY + "includes ELO.")
                    .lore("")
                    .lore(CC.RED + "Players: " + CC.RESET + PracticeCache.getInstance().getRankedSoloPlayers())
                    .lore("")
                    .lore(CC.GREEN + "Click to play!")
                    .build()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    Player player = (Player)event.getWhoClicked();
                    Profile profile = plugin.getProfileManager().getProfileData(player.getUniqueId());

                    player.closeInventory();

                    if (!player.hasPermission("practice.donors.*") && profile.getUnrankedWins() < 10) {
                        player.sendMessage(ChatColor.RED + "You must win " + (10 - profile.getUnrankedWins()) + " Unranked Matches to join this queue.");
                        return;
                    }

                    //player.openInventory(plugin.getQueueJoinMenu().getRankedMenu().getCurrentPage());
                }
            });
            playMenu.setItem(15, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                    .name(CC.DARK_RED + "TeamFight")
                    .lore("")
                    .lore(CC.GRAY + "Fight with other factions")
                    .lore(CC.GRAY + "capping a koth to win the")
                    .lore(CC.GRAY + "the match.")
                    .lore(CC.GRAY + "switching to " + CC.YELLOW + "Bard" + CC.GRAY + ", " + CC.RED + "Archer" + CC.GRAY + ", " + CC.AQUA + "Diamond " + CC.GRAY + "kit.")
                    .lore("")
                    .lore(CC.YELLOW + CC.BOLD + "NEW")
                    .lore("")
                    .lore(CC.GREEN + "Click to play!")
                    .build()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    Player player = (Player)event.getWhoClicked();


                    player.sendMessage(CC.GREEN + "getFaction::TEST");
                }
            });
        }
    }
}
