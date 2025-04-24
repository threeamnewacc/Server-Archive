package me.devkevin.practice.match.duel.menu;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.practice.Practice;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.Clickable;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.ItemUtil;
import me.devkevin.practice.util.inventory.InventoryUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 09/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class DuelMenu {

    private final Practice plugin = Practice.getInstance();

    @Getter private final InventoryUI duelMenu = new InventoryUI(CC.BLUE + "Select a Duel Ladder", true, 2);
    @Getter
    private final InventoryUI bestOfThreeInventory = new InventoryUI(CC.DARK_RED + "Select Match Type", true, 1);

    private final Map<String, InventoryUI> duelMapInventories = new HashMap<>();

    public DuelMenu() {
        menu();
    }

    private void menu() {
        Collection<Kit> kits = this.plugin.getKitManager().getKits();

        for (Kit kit : kits) {
            if (kit.isEnabled()) {
                if (!kit.getName().contains("HCF TeamFight")) {
                    this.duelMenu.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(kit.getIcon().getType(), CC.PINK + kit.getName(), 1, kit.getIcon().getDurability())) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            Player player = (Player)event.getWhoClicked();

                            DuelClick(player, kit);
                        }
                    });

                    this.bestOfThreeInventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(kit.getIcon().getType(), CC.PINK + kit.getName(), 1, kit.getIcon().getDurability())) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            Player player = (Player)event.getWhoClicked();
                            Profile profile = plugin.getProfileManager().getProfileData(player.getUniqueId());


                            DuelClick(player, kit);
                        }
                    });
                }
            }
        }


        for (Kit kit : this.plugin.getKitManager().getKits()) {
            InventoryUI duelInventory = new InventoryUI(CC.GOLD + "Select maps", true, 2);
            InventoryUI botInventory = new InventoryUI(CC.GOLD + "Select Bot Difficulty", true, 1);

            for (Arena arena : this.plugin.getArenaManager().getArenas().values()) {
                if (!arena.isEnabled()) {
                    continue;
                }
                if (kit.getArenaWhiteList().size() > 0 && !kit.getArenaWhiteList().contains(arena.getName())) {
                    continue;
                }


                ItemStack map = new ItemBuilder(Material.valueOf(arena.getIcon())).name(CC.GREEN + arena.getName())
                        .durability(arena.getIconData())
                        .lore(CC.GOLD + "Authors:")
                        .lore(CC.GRAY + "LOL Builder Team")
                        .build();

                ItemStack random = ItemUtil.createItem(Material.PAPER, CC.YELLOW + "Select random map");

                duelInventory.setItem(17, new InventoryUI.AbstractClickableItem(random) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        Player player = (Player)event.getWhoClicked();
                        Profile profile = plugin.getProfileManager().getProfileData(player.getUniqueId());

                        DuelMapClick(player, arena, kit);
                    }
                });

                duelInventory.addItem(new InventoryUI.AbstractClickableItem(map) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        Player player = (Player)event.getWhoClicked();

                        DuelMapClick(player, arena, kit);
                    }
                });
            }

            this.duelMapInventories.put(kit.getName(), duelInventory);
        }
    }

    public void DuelClick(Player player, Kit kit) {
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        Player selected = this.plugin.getServer().getPlayer(profile.getDuelSelecting());
        if (selected == null) {
            player.sendMessage(String.format(PracticeLang.PLAYER_NOT_FOUND, profile.getDuelSelecting()));
            return;
        }

        Profile targetProfile = this.plugin.getProfileManager().getProfileData(selected.getUniqueId());
        if (targetProfile.isBusy()) {
            player.sendMessage(CC.RED + "That player is currently busy.");
            return;
        }

        Party targetParty = this.plugin.getPartyManager().getParty(selected.getUniqueId());
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        boolean partyDuel = party != null;
        if (partyDuel) {
            if (targetParty == null) {
                player.sendMessage(CC.RED + "That player isn't in a party.");
                return;
            }
        }

        CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

        if (coreProfile.hasDonor() && !kit.getName().contains("HCF TeamFight")) {
            player.closeInventory();
            player.openInventory(this.duelMapInventories.get(kit.getName()).getCurrentPage());
            return;
        }

        if (coreProfile.hasDonor()) {
            player.closeInventory();
            player.openInventory(this.duelMapInventories.get(kit.getName()).getCurrentPage());
            return;
        }

        if (this.plugin.getMatchManager().getMatchRequest(player.getUniqueId(), selected.getUniqueId()) != null) {
            player.sendMessage(CC.RED + "You have already sent a duel request to this player, please wait.");
            return;
        }

        Arena arena = this.plugin.getArenaManager().getRandomArena(kit, profile.getLastArenaPlayed());
        if (arena == null) {
            player.sendMessage(CC.RED + "There are no arenas available at this moment.");
            return;
        }

        this.sendDuel(player, selected, kit, partyDuel, party, targetParty, arena);
    }

    public void DuelMapClick(Player player, Arena arena, Kit kit) {
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        Player selected = this.plugin.getServer().getPlayer(profile.getDuelSelecting());
        if (selected == null) {
            player.sendMessage(String.format(PracticeLang.PLAYER_NOT_FOUND, profile.getDuelSelecting()));
            return;
        }

        Profile targetProfile = this.plugin.getProfileManager().getProfileData(selected.getUniqueId());
        if (targetProfile.isBusy()) {
            player.sendMessage(CC.RED + "That player is currently busy.");
            return;
        }

        Party targetParty = this.plugin.getPartyManager().getParty(selected.getUniqueId());
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        boolean partyDuel = party != null;
        if (partyDuel && targetParty == null) {
            player.sendMessage(CC.RED + "That player isn't in a party.");
            return;
        }

        if (this.plugin.getMatchManager().getMatchRequest(player.getUniqueId(), selected.getUniqueId()) != null) {
            player.sendMessage(CC.RED + "You have already sent a duel request to this player, please wait.");
            return;
        }

        this.sendDuel(player, selected, kit, partyDuel, party, targetParty, arena);
    }


    private void sendDuel(Player player, Player selected, Kit kit, boolean partyDuel, Party party, Party targetParty, Arena arena) {
        this.plugin.getMatchManager().createMatchRequest(player, selected, arena, kit.getName(), partyDuel);

        player.closeInventory();

        String requestGetString = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName() + (partyDuel ? (CC.YELLOW + "'s party " + CC.GREEN + "(" + (party.getMembers().size()) + ")") : "") + CC.YELLOW + " has requested a duel with the kit " + CC.GOLD + kit.getName() +
                CC.YELLOW + " on " + CC.GOLD + arena.getName() + CC.YELLOW + ". " + CC.GRAY + "[Click to Accept]";
        String requestSendString = CC.YELLOW + "Sent a duel request to " + CC.GREEN + selected.getName() + (partyDuel ? (CC.YELLOW + "'s party " + CC.GREEN + "(" + (party.getMembers().size()) + ")") : "") + CC.YELLOW + " with the kit " + CC.GREEN + kit.getName() + CC.YELLOW + ".";

        Clickable requestMessage = new Clickable(
                requestGetString,
                CC.GRAY + "Click to accept duel",
                "/accept " + player.getName() + " " + kit.getName());

        if (partyDuel) {
            targetParty.members().forEach(requestMessage::sendToPlayer);
            party.broadcast(requestSendString);
        } else {
            requestMessage.sendToPlayer(selected);
            player.sendMessage(requestSendString);
        }
    }
}
