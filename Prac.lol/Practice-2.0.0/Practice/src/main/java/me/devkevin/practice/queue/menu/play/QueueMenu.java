package me.devkevin.practice.queue.menu.play;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import me.devkevin.landcore.LandCore;
import me.devkevin.practice.Practice;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.leaderboard.Leaderboard;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.queue.QueueType;
import me.devkevin.practice.util.ItemUtil;
import me.devkevin.practice.util.inventory.InventoryUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import static me.devkevin.practice.util.PlayerUtil.PLACEHOLDER_ITEM;

/**
 * Copyright 12/12/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class QueueMenu {
    private final Practice plugin = Practice.getInstance();

    private final InventoryUI unrankedMenu = new InventoryUI(CC.GRAY + "Play Unranked Queue", 5);
    private final InventoryUI rankedMenu = new InventoryUI(CC.GRAY + "Play Ranked Queue", 5);

    public QueueMenu() {
        this.setupMenu();
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::updateInventories, 20L, 20L);
    }

    private void setupMenu() {
        for (int i = 0; i < 9 * 5; i++) {
            this.unrankedMenu.setItem(i, new InventoryUI.EmptyClickableItem(PLACEHOLDER_ITEM));
            this.rankedMenu.setItem(i, new InventoryUI.EmptyClickableItem(PLACEHOLDER_ITEM));
        }

        Collection<Kit> kits = plugin.getKitManager().getKits();
        for (Kit kit : kits) {
            if (kit.isEnabled()) {
                this.unrankedMenu.setItem(kit.getPriority(), new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        Player player = (Player) event.getWhoClicked();
                        Profile profile = Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId());

                        if (profile == null) {
                            return;
                        }

                        if (profile.isBusy()) {
                            player.sendMessage(CC.RED + "You cannot queue right now.");
                            return;
                        }

                        if (LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).isFrozen()) {
                            player.sendMessage(CC.RED + "You cannot queue while you're frozen.");
                        }

                        addToQueue(
                                player,
                                Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId()),
                                kit,
                                QueueType.UN_RANKED);
                    }
                });
                if (kit.isRanked()) {
                    this.rankedMenu.setItem(kit.getPriority(), new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            Player player = (Player) event.getWhoClicked();
                            Profile profile = Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId());

                            if (profile == null) {
                                return;
                            }

                            if (profile.isBusy()) {
                                player.sendMessage(CC.RED + "You cannot queue right now.");
                                return;
                            }

                            if (LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).isFrozen()) {
                                player.sendMessage(CC.RED + "You cannot queue while you're frozen.");
                            }

                            addToQueue(
                                    player,
                                    Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId()),
                                    kit,
                                    QueueType.RANKED);
                        }
                    });
                }
            }
        }
    }

    private void updateInventories() {
        for (int i = 0; i < 9 * 5; i++) {
            InventoryUI.ClickableItem unrankedItem = this.unrankedMenu.getItem(i);
            if (unrankedItem != null) {
                if (unrankedItem.getItemStack() != PLACEHOLDER_ITEM) {
                    unrankedItem.setItemStack(this.updateQueueLore(unrankedItem.getItemStack(), QueueType.UN_RANKED));
                } else {
                    unrankedItem.setItemStack(PLACEHOLDER_ITEM);
                }
                this.unrankedMenu.setItem(i, unrankedItem);
            }

            InventoryUI.ClickableItem rankedItem = this.rankedMenu.getItem(i);
            if (rankedItem != null) {
                if (rankedItem.getItemStack() != PLACEHOLDER_ITEM) {
                    rankedItem.setItemStack(this.updateQueueLore(rankedItem.getItemStack(), QueueType.RANKED));
                } else {
                    rankedItem.setItemStack(PLACEHOLDER_ITEM);
                }
                this.rankedMenu.setItem(i, rankedItem);
            }
        }
    }

    public void addToQueue(Player player, Profile profile, Kit kit, QueueType queueType) {
        if (kit != null) {
            this.plugin.getQueue().addPlayer(player, profile, kit.getName(), queueType);
        }
    }

    private ItemStack updateQueueLore(ItemStack itemStack, QueueType queueType) {
        if (itemStack == null) {
            return null;
        }

        String ladder;

        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            ladder = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
            if (ladder.contains(" ")) {
                ladder = ladder.split(" ")[1];
            }
            ladder = ladder.replaceAll("[^a-zA-Z]+", "");
        } else {
            return null;
        }

        int unrankedQueueSize = this.plugin.getQueue().getQueueSize(ladder, QueueType.UN_RANKED);
        int unrankedInGameSize = this.plugin.getMatchManager().getFighters(ladder, QueueType.UN_RANKED);

        int rankedQueueSize = this.plugin.getQueue().getQueueSize(ladder, QueueType.RANKED);
        int rankedInGameSize = this.plugin.getMatchManager().getFighters(ladder, QueueType.RANKED);

        ArrayList<String> lore = new ArrayList<>();

        if (!queueType.isRanked()) {
            lore.add(CC.GRAY + "\u25CF " + CC.YELLOW + "In queue: " + CC.GOLD + unrankedQueueSize);
            lore.add(CC.GRAY + "\u25CF " + CC.YELLOW + "In match: " + CC.GOLD + unrankedInGameSize);

            lore.add("");

            int i = 0;
            for (Leaderboard winStreak : plugin.getLeaderboardManager().getKitLeaderboards(plugin.getKitManager().getKit(ladder)).stream().sorted(Comparator.comparingInt(Leaderboard::getWinStreak).reversed()).limit(3).collect(Collectors.toList())) {
                if (winStreak != null) {
                    if (winStreak.getUuid() != null) {
                        lore.add(CC.GRAY + (i + 1) + ". " + CC.YELLOW + CC.I + winStreak.getName() + CC.GRAY + " - " + CC.GOLD + winStreak.getWinStreak());
                        i++;
                    }
                }
            }
        } else {
            lore.add(CC.GRAY + "\u25CF " + CC.YELLOW + "In queue: " + CC.GOLD + rankedQueueSize);
            lore.add(CC.GRAY + "\u25CF " + CC.YELLOW + "In match: " + CC.GOLD + rankedInGameSize);

            lore.add("");

            int i = 0;
            for (Leaderboard leaderboard : plugin.getLeaderboardManager().getSortedKitLeaderboards(plugin.getKitManager().getKit(ladder), "elo").stream().limit(3).collect(Collectors.toList())) {
                if (leaderboard != null) {
                    if (leaderboard.getUuid() != null) {
                        lore.add(CC.GRAY + (i + 1) + ". " + CC.YELLOW + CC.I + leaderboard.getName() + CC.GRAY + " - " + CC.GOLD + leaderboard.getElo());
                        i++;
                    }
                }
            }
        }
        lore.add("");
        lore.add(CC.YELLOW + "Click here to play " + CC.GOLD + ladder + CC.YELLOW + ".");

        switch (queueType) {
            case UN_RANKED:
                return ItemUtil.updateLoreAndAmount(itemStack, Math.min(unrankedInGameSize, 64), lore.toArray(new String[0]));
            case RANKED:
                return ItemUtil.updateLoreAndAmount(itemStack, Math.min(rankedInGameSize, 64), lore.toArray(new String[0]));
        }

        return itemStack;
    }
}
