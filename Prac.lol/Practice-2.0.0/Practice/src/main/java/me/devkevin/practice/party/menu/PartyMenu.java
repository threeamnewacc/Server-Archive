package me.devkevin.practice.party.menu;

import club.inverted.chatcolor.CC;
import com.google.common.collect.Lists;
import lombok.Getter;
import me.devkevin.landcore.LandCore;
import me.devkevin.practice.Practice;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.queue.QueueType;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.ItemUtil;
import me.devkevin.practice.util.inventory.InventoryUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

import static me.devkevin.practice.util.PlayerUtil.PLACEHOLDER_ITEM;

/**
 * Copyright 28/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class PartyMenu {

    private final Practice plugin = Practice.getInstance();

    private final InventoryUI partyMenu = new InventoryUI(CC.DARK_RED + "Duel another parties", true, 6);
    private final InventoryUI partyEvent = new InventoryUI(CC.DARK_RED + "Host Party Events", true, 4);

    private final InventoryUI partySplitMenu = new InventoryUI(CC.DARK_RED + "Select a Party Split Kit", true,1);
    private final InventoryUI partyFFAMenu = new InventoryUI(CC.DARK_RED + "Select a Party FFA Kit", true,1);
    private final InventoryUI partyRedroverMenu = new InventoryUI(CC.DARK_RED + "Select a Redrover Kit", true,1);
    private final InventoryUI partyKothMenu = new InventoryUI(CC.DARK_RED + "Play Koth Mode", true, 1);
    private final InventoryUI partyHCFMenu = new InventoryUI(CC.DARK_RED + "Play HCF Mdde", true, 1);

    private final Map<String, InventoryUI> partySplitMapInventories = new HashMap<>();
    private final Map<String, InventoryUI> partyFFAMapInventories = new HashMap<>();
    private final Map<String, InventoryUI> redroverMapInventories = new HashMap<>();

    public PartyMenu() {
        this.PartyMenu();
    }

    private void PartyMenu() {
        for (int i = 0; i < 9 * 4; i++) {
            this.partyEvent.setItem(i, new InventoryUI.EmptyClickableItem(PLACEHOLDER_ITEM));
        }


        Collection<Kit> kits = this.plugin.getKitManager().getKits();

        for (Kit kit : kits) {
            if (kit.isEnabled()) {
                this.partyFFAMenu.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        Player player = (Player) event.getWhoClicked();
                        handleFFAClick(player, kit);
                    }
                });
                this.partySplitMenu.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        Player player = (Player) event.getWhoClicked();
                        handleSplitClick(player, kit);
                    }
                });
                if (!kit.getName().equalsIgnoreCase("HCF")) {
                    this.partyRedroverMenu.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            Player player = (Player) event.getWhoClicked();
                            handleRedroverClick(player, kit);
                        }
                    });
                }
            }
        }

        this.partyKothMenu.setItem(0, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.FENCE_GATE).name(CC.DARK_RED + "Koth").build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                player.sendMessage(CC.RED + "Under development.");
            }
        });
        this.partyHCFMenu.setItem(0, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.POTION).data(373).name(CC.DARK_RED + "HCF").build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                player.sendMessage(CC.RED + "Under development.");
            }
        });


        // party events
        this.partyEvent.setItem(11, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.LEASH).name(CC.DARK_RED + "Party Split")
                        .lore(CC.GRAY + "Slip your party into")
                        .lore(CC.GRAY + "two teams and fight.")
                        .lore("")
                        .lore(CC.DARK_RED + "Click to host")
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                player.openInventory(partySplitMenu.getCurrentPage());
            }
        });
        this.partyEvent.setItem(13, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.BLAZE_ROD).name(CC.DARK_RED + "Party FFA")
                        .lore(CC.GRAY + "Everyone in the party")
                        .lore(CC.GRAY + "fights everybody else.")
                        .lore("")
                        .lore(CC.DARK_RED + "Click to host")
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                player.openInventory(partyFFAMenu.getCurrentPage());
            }
        });
        this.partyEvent.setItem(15, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.REDSTONE).name(CC.DARK_RED + "Red Rover")
                        .lore(CC.GRAY + "Fight in a series of 1v1s")
                        .lore(CC.GRAY + "until there is a winner")
                        .lore("")
                        .lore(CC.DARK_RED + "Click to host")
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                player.openInventory(partyRedroverMenu.getCurrentPage());
            }
        });
        this.partyEvent.setItem(21, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.FENCE_GATE).name(CC.DARK_RED + "Party Koth")
                        .lore(CC.GRAY + "Slip your party into")
                        .lore(CC.GRAY + "two teams and fight")
                        .lore(CC.GRAY + "capping a koth to win.")
                        .lore("")
                        .lore(CC.DARK_RED + "Click to host")
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                player.openInventory(partyKothMenu.getCurrentPage());
            }
        });
        this.partyEvent.setItem(23, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.DIAMOND_CHESTPLATE).name(CC.DARK_RED + "Party HCF")
                        .lore(CC.GRAY + "Slip your party into")
                        .lore(CC.GRAY + "two teams choosing")
                        .lore(CC.GRAY + "Bard, Archer, Diamond")
                        .lore(CC.GRAY + "kit and fight.")
                        .lore("")
                        .lore(CC.DARK_RED + "Click to host")
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                player.openInventory(partyHCFMenu.getCurrentPage());
            }
        });

        for (Kit kit : this.plugin.getKitManager().getKits()) {
            InventoryUI partySplitInventory = new InventoryUI(CC.BLUE + "Select  Split Arena", true, 3);
            InventoryUI partyFFAInventory = new InventoryUI(CC.BLUE + "Select FFA Arena", true, 3);
            InventoryUI redroverInventory = new InventoryUI(CC.BLUE + "Select Redrover Arena", true, 3);

            for (Arena arena : this.plugin.getArenaManager().getArenas().values()) {
                if (!arena.isEnabled()) {
                    continue;
                }

                if (kit.getArenaWhiteList().size() > 0 && !kit.getArenaWhiteList().contains(arena.getName())) {
                    continue;
                }

                ItemStack dirt = ItemUtil.createItem(Material.DIRT, CC.YELLOW + arena.getName());

                partySplitInventory.addItem(new InventoryUI.AbstractClickableItem(dirt) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        PartyMenu.this.handlePartySplitMapClick((Player) event.getWhoClicked(), arena, kit);
                    }
                });
                partyFFAInventory.addItem(new InventoryUI.AbstractClickableItem(dirt) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        PartyMenu.this.handlePartyFFAMapClick((Player) event.getWhoClicked(), arena, kit);
                    }
                });
                redroverInventory.addItem(new InventoryUI.AbstractClickableItem(dirt) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        PartyMenu.this.handleRedroverMapClick((Player) event.getWhoClicked(), arena, kit);
                    }
                });
            }
            this.partySplitMapInventories.put(kit.getName(), partySplitInventory);
            this.partyFFAMapInventories.put(kit.getName(), partyFFAInventory);
            this.redroverMapInventories.put(kit.getName(), redroverInventory);
        }
    }

    public void addParty(Player player) {
        ItemStack skull = new ItemBuilder(Material.SKULL_ITEM)
                .name(LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName() + CC.GRAY + " (" + "1" + ")")
                .durability(3)
                .owner(player.getName())
                .lore(PracticeLang.line)
                .lore(CC.GRAY + "Members: ")
                .lore(CC.RED + "No members online")
                .lore("")
                .lore(CC.GRAY + "Click here to duel " + player.getName() + "'s party.")
                .lore(PracticeLang.line)
                .build();
        this.partyMenu.addItem(new InventoryUI.AbstractClickableItem(skull) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player sender = (Player)event.getWhoClicked();
                player.closeInventory();
                sender.performCommand("duel " + player.getName());
            }
        });
    }

    public void updateParty(Party party) {
        Player player = this.plugin.getServer().getPlayer(party.getLeader());

        for (int i = 0; i < this.partyMenu.getSize(); i++) {
            InventoryUI.ClickableItem item = this.partyMenu.getItem(i);

            if (item != null) {
                ItemStack stack = item.getItemStack();

                if (stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains(player.getName())) {
                    List<String> strings = new ArrayList<>();

                    strings.add(PracticeLang.line);
                    strings.add(CC.GRAY + "Members: ");

                    party.members().forEach(member -> strings.add(CC.GRAY + " - " + LandCore.getInstance().getProfileManager().getProfile(member.getUniqueId()).getGrant().getRank().getColor() + member.getName()));

                    strings.add("");
                    strings.add(CC.GRAY + "Click here to duel " + player.getName() + "'s party.");
                    strings.add(PracticeLang.line);

                    ItemUtil.reloreItem(stack, strings.toArray(new String[0]));
                    ItemUtil.renameItem(stack, LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName() + CC.GRAY + " (" + CC.GRAY + party.getMembers().size() + CC.GRAY + ")");

                    item.setItemStack(stack);
                    break;
                }
            }
        }
    }

    public void removeParty(Party party) {
        Player player = this.plugin.getServer().getPlayer(party.getLeader());

        for (int i = 0; i < this.partyMenu.getSize(); i++) {
            InventoryUI.ClickableItem item = this.partyMenu.getItem(i);

            if (item != null) {
                ItemStack stack = item.getItemStack();

                if (stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains(player.getName())) {
                    this.partyMenu.removeItem(i);
                    break;
                }
            }
        }
    }

    private void handleFFAClick(Player player, Kit kit) {
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

        if (party == null || kit == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }

        player.closeInventory();

        if (party.getMembers().size() < 2) {
            player.sendMessage(CC.RED + "You need more than 2 players to start an event.");
        } else {
            if (player.hasPermission("practice.donors.*")) {
                player.closeInventory();
                player.openInventory(this.partyFFAMapInventories.get(kit.getName()).getCurrentPage());
                return;
            }

            Profile profile = this.plugin.getProfileManager().getProfileData(party.getLeader());
            Arena arena = this.plugin.getArenaManager().getRandomArena(kit, profile.getLastArenaPlayed());
            if (arena == null) {
                player.sendMessage(CC.RED + "No available arenas found.");
                return;
            }

            if (kit.getName().equals("Parkour")) {
                player.sendMessage(CC.RED + "Sorry but you can't use this kit in FFA mode!");
                return;
            }

            if (kit.getName().equals("BedWars")) {
                player.sendMessage(CC.RED + "Sorry but you can't use this kit in FFA mode!");
                return;
            }

            this.createFFAMatch(party, arena, kit);
        }
    }

    private void handleSplitClick(Player player, Kit kit) {
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

        if (party == null || kit == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }

        player.closeInventory();

        if (party.getMembers().size() < 2) {
            player.sendMessage(CC.RED + "You need more than 2 players to start an event.");
        } else {
            if (player.hasPermission("practice.donors.*")) {
                player.closeInventory();
                player.openInventory(this.partySplitMapInventories.get(kit.getName()).getCurrentPage());
                return;
            }

            Profile profile = this.plugin.getProfileManager().getProfileData(party.getLeader());
            Arena arena = this.plugin.getArenaManager().getRandomArena(kit, profile.getLastArenaPlayed());
            if (arena == null) {
                player.sendMessage(CC.RED + "No available arenas found.");
                return;
            }

            if (kit.getName().equals("BedWars")) {
                player.sendMessage(CC.RED + "Sorry but you can't use this kit in FFA mode!");
            }
        }
    }

    private void handleRedroverClick(Player player, Kit kit) {
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

        if (party == null || kit == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }

        player.closeInventory();

        if (party.getMembers().size() < 4) {
            player.sendMessage(CC.RED + "You need more than 4 players to start an event.");
        } else {
            if (player.hasPermission("practice.donors.*")) {
                player.closeInventory();
                player.openInventory(this.redroverMapInventories.get(kit.getName()).getCurrentPage());
                return;
            }

            Profile profile = this.plugin.getProfileManager().getProfileData(party.getLeader());
            Arena arena = this.plugin.getArenaManager().getRandomArena(kit, profile.getLastArenaPlayed());
            if (arena == null) {
                player.sendMessage(CC.RED + "No available arenas found.");
                return;
            }

            this.createRedroverMatch(party, arena, kit);
        }
    }

    private void handlePartyFFAMapClick(Player player, Arena arena, Kit kit) {
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

        if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }

        player.closeInventory();

        if (party.getMembers().size() < 2) {
            player.sendMessage(CC.RED + "You need more than 2 players to start an event.");
        } else {
            this.createFFAMatch(party, arena, kit);
        }
    }

    private void handlePartySplitMapClick(Player player, Arena arena, Kit kit) {
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

        if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }

        player.closeInventory();

        if (party.getMembers().size() < 2) {
            player.sendMessage(CC.RED + "You need more than 2 players to start an event.");
        } else {
            this.createSplitMatch(party, arena, kit);
        }
    }

    private void handleRedroverMapClick(Player player, Arena arena, Kit kit) {
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

        if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }

        player.closeInventory();

        if (party.getMembers().size() < 2) {
            player.sendMessage(CC.RED + "You need more than 2 players to start an event.");
        } else {
            this.createRedroverMatch(party, arena, kit);
        }
    }

    private void createFFAMatch(Party party, Arena arena, Kit kit) {
        MatchTeam team = new MatchTeam(party.getLeader(), Lists.newArrayList(party.getMembers()), 0);
        Match match = new Match(arena, kit, QueueType.UN_RANKED, team);

        match.broadcastMessage(
                CC.YELLOW + "Starting a party FFA match with kit " +
                        CC.BLUE + kit.getName() +
                        CC.YELLOW + " and arena " +
                        CC.BLUE + arena.getName() +
                        CC.YELLOW + ".");

        this.plugin.getMatchManager().createMatch(match);
    }

    private void createSplitMatch(Party party, Arena arena, Kit kit) {
        MatchTeam[] teams = party.splits();
        Match match = new Match(arena, kit, QueueType.UN_RANKED, teams);
        Player leaderA = this.plugin.getServer().getPlayer(teams[0].getLeader());
        Player leaderB = this.plugin.getServer().getPlayer(teams[1].getLeader());

        match.broadcastMessage(
                CC.YELLOW + "Starting a party Split match with kit " +
                        CC.BLUE + kit.getName() +
                        CC.YELLOW + " and arena " +
                        CC.BLUE + arena.getName() +
                        CC.YELLOW + " between " +
                        CC.BLUE + leaderA.getName() +
                        CC.YELLOW + "'s team and " +
                        CC.BLUE + leaderB.getName() +
                        CC.YELLOW + "'s team.");

        this.plugin.getMatchManager().createMatch(match);
    }

    private void createRedroverMatch(Party party, Arena arena, Kit kit) {
        MatchTeam[] teams = party.splits();
        Match match = new Match(arena, kit, QueueType.UN_RANKED, teams);
        Player leaderA = this.plugin.getServer().getPlayer(teams[0].getLeader());
        Player leaderB = this.plugin.getServer().getPlayer(teams[1].getLeader());

        match.broadcastMessage(
                CC.YELLOW + "Starting a party Redrover match with kit " +
                        CC.BLUE + kit.getName() +
                        CC.YELLOW + " and arena " +
                        CC.BLUE + arena.getName() +
                        CC.YELLOW + " between " +
                        CC.BLUE + leaderA.getName() +
                        CC.YELLOW + "'s team and " +
                        CC.BLUE + leaderB.getName() +
                        CC.YELLOW + "'s team.");

        this.plugin.getMatchManager().createMatch(match);
    }

    public Inventory getMenu(int page) {
        int max = (int) Math.ceil(plugin.getPartyManager().getParties().values().size() / 18.0);

        Inventory inventory = Bukkit.createInventory(null, 27, CC.translate("&4Parties"));
        inventory.setItem(0, new ItemBuilder(Material.ARROW).name(CC.GRAY + "Previous Page").build());
        inventory.setItem(8, new ItemBuilder(Material.ARROW).name(CC.GRAY + "Next Page").build());

        int minIndex = (int) ((double) (page - 1) * 18);
        int maxIndex = (int) ((double) (page) * 18);

        List<Party> toLoop = new ArrayList<>(plugin.getPartyManager().getParties().values());
        Collections.reverse(toLoop);

        toLoop.forEach(party -> {
            int number = toLoop.indexOf(party);

            if(number >= minIndex && number < maxIndex) {
                number -= (int) ((double) (18) * (page - 1)) - 9;

                List<String> lore = new ArrayList<>();
                int[] count = {0};

                List<Player> members = new ArrayList<>(party.getMembers().stream().map(Bukkit::getPlayer).collect(Collectors.toList()));
                members.remove(Bukkit.getPlayer(party.getLeader()));

                lore.add("");
                lore.add(CC.GRAY + "Members:");
                if(members.isEmpty()) {
                    lore.add(CC.RED + "No members online");
                } else {
                    members.forEach(member -> {
                        if(count[0] != 10) {
                            lore.add(" &7- " + LandCore.getInstance().getProfileManager().getProfile(member.getUniqueId()).getGrant().getRank().getColor() + member.getDisplayName());
                            count[0]++;
                        }
                    });
                }
                lore.add("");
                lore.add(CC.GREEN + "Click to duel");

                        inventory.setItem(number, new ItemBuilder(Material.SKULL_ITEM)
                                .durability(3)
                                .owner(Bukkit.getPlayer(party.getLeader()).getName())
                                .name(LandCore.getInstance().getProfileManager().getProfile(party.getLeader()).getGrant().getRank().getColor() + Bukkit.getPlayer(party.getLeader()).getName() + CC.GRAY + "'s Party" )
                                .lore(lore).build());
            }
        });
        return inventory;
    }
}
