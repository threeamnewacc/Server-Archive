// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.managers;

import com.google.common.collect.Lists;
import club.minemen.practice.match.MatchTeam;
import club.minemen.practice.match.Match;
import java.util.function.Consumer;
import club.minemen.core.clickable.Clickable;
import club.minemen.core.mineman.Mineman;
import club.minemen.core.rank.Rank;
import club.minemen.core.CorePlugin;
import club.minemen.core.util.finalutil.StringUtil;
import club.minemen.practice.kit.PlayerKit;
import java.util.List;
import java.util.ArrayList;
import club.minemen.core.listener.UIListener;
import club.minemen.practice.party.Party;
import club.minemen.practice.player.PlayerData;
import org.bukkit.ChatColor;
import java.util.Iterator;
import java.util.Collection;
import club.minemen.practice.arena.Arena;
import club.minemen.core.util.finalutil.ItemUtil;
import org.bukkit.Material;
import club.minemen.practice.player.PlayerState;
import club.minemen.practice.queue.QueueType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import club.minemen.practice.kit.Kit;
import org.bukkit.plugin.Plugin;
import java.util.HashMap;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.inventory.InventorySnapshot;
import java.util.UUID;
import java.util.Map;
import club.minemen.core.inventory.InventoryUI;
import club.minemen.practice.Practice;

public class InventoryManager
{
    private static final String MORE_PLAYERS;
    private final Practice plugin;
    private final InventoryUI unrankedInventory;
    private final InventoryUI rankedInventory;
    private final InventoryUI editorInventory;
    private final InventoryUI duelInventory;
    private final InventoryUI partySplitInventory;
    private final InventoryUI partyFFAInventory;
    private final InventoryUI redroverInventory;
    private final InventoryUI joinPremiumInventory;
    private final InventoryUI partyEventInventory;
    private final InventoryUI partyInventory;
    private final Map<String, InventoryUI> duelMapInventories;
    private final Map<String, InventoryUI> partySplitMapInventories;
    private final Map<String, InventoryUI> partyFFAMapInventories;
    private final Map<String, InventoryUI> redroverMapInventories;
    private final Map<UUID, InventoryUI> editorInventories;
    private final Map<UUID, InventorySnapshot> snapshots;
    
    public InventoryManager() {
        this.plugin = Practice.getInstance();
        this.unrankedInventory = new InventoryUI(CC.PRIMARY + "Select an Unranked Ladder", true, 2);
        this.rankedInventory = new InventoryUI(CC.PRIMARY + "Select a Ranked Ladder", true, 2);
        this.editorInventory = new InventoryUI(CC.PRIMARY + "Select an Editable Ladder", true, 2);
        this.duelInventory = new InventoryUI(CC.PRIMARY + "Select a Duel Ladder", true, 2);
        this.partySplitInventory = new InventoryUI(CC.PRIMARY + "Select a Party Split Kit", true, 2);
        this.partyFFAInventory = new InventoryUI(CC.PRIMARY + "Select a Party FFA Kit", true, 2);
        this.redroverInventory = new InventoryUI(CC.PRIMARY + "Select a Redrover Kit", true, 2);
        this.joinPremiumInventory = new InventoryUI(CC.PRIMARY + "Confirm Joining Premium", true, 1);
        this.partyEventInventory = new InventoryUI(CC.PRIMARY + "Select an Event", true, 2);
        this.partyInventory = new InventoryUI(CC.PRIMARY + "Duel a Party", true, 6);
        this.duelMapInventories = new HashMap<String, InventoryUI>();
        this.partySplitMapInventories = new HashMap<String, InventoryUI>();
        this.partyFFAMapInventories = new HashMap<String, InventoryUI>();
        this.redroverMapInventories = new HashMap<String, InventoryUI>();
        this.editorInventories = new HashMap<UUID, InventoryUI>();
        this.snapshots = new HashMap<UUID, InventorySnapshot>();
        this.setupInventories();
        this.plugin.getServer().getScheduler().runTaskTimer((Plugin)this.plugin, this::updateInventories, 20L, 20L);
    }
    
    private void setupInventories() {
        final Collection<Kit> kits = this.plugin.getKitManager().getKits();
        for (final Kit kit : kits) {
            if (kit.isEnabled()) {
                this.unrankedInventory.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                    public void onClick(final InventoryClickEvent event) {
                        final Player player = (Player)event.getWhoClicked();
                        InventoryManager.this.addToQueue(player, InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()), kit, InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()), QueueType.UNRANKED);
                    }
                });
                if (kit.isRanked()) {
                    this.rankedInventory.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                        public void onClick(final InventoryClickEvent event) {
                            final Player player = (Player)event.getWhoClicked();
                            InventoryManager.this.addToQueue(player, InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()), kit, InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()), QueueType.RANKED);
                        }
                    });
                }
                this.editorInventory.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                    public void onClick(final InventoryClickEvent event) {
                        final Player player = (Player)event.getWhoClicked();
                        InventoryManager.this.plugin.getEditorManager().addEditor(player, kit);
                        InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()).setPlayerState(PlayerState.EDITING);
                    }
                });
                this.duelInventory.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleDuelClick((Player)event.getWhoClicked(), kit);
                    }
                });
                this.partySplitInventory.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handlePartySplitClick((Player)event.getWhoClicked(), kit);
                    }
                });
                this.partyFFAInventory.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleFFAClick((Player)event.getWhoClicked(), kit);
                    }
                });
                this.redroverInventory.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleRedroverClick((Player)event.getWhoClicked(), kit);
                    }
                });
            }
        }
        this.partyEventInventory.setItem(2, (InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.LEASH, CC.PRIMARY + "Party Split")) {
            public void onClick(final InventoryClickEvent event) {
                final Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                player.openInventory(InventoryManager.this.getPartySplitInventory().getCurrentPage());
            }
        });
        this.partyEventInventory.setItem(4, (InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.BLAZE_ROD, CC.PRIMARY + "Party FFA")) {
            public void onClick(final InventoryClickEvent event) {
                final Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                player.openInventory(InventoryManager.this.getPartyFFAInventory().getCurrentPage());
            }
        });
        this.partyEventInventory.setItem(6, (InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.REDSTONE, CC.PRIMARY + "Redrover")) {
            public void onClick(final InventoryClickEvent event) {
                final Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                player.openInventory(InventoryManager.this.getRedroverInventory().getCurrentPage());
            }
        });
        for (int i = 0; i < 9; ++i) {
            this.joinPremiumInventory.setItem(i, (InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.DIAMOND, CC.PRIMARY + "Confirm Joining Premium")) {
                public void onClick(final InventoryClickEvent event) {
                    final Player player = (Player)event.getWhoClicked();
                    final ItemStack item = event.getCurrentItem();
                    if (item != null && item.getType() == Material.DIAMOND) {
                        InventoryManager.this.plugin.getQueueManager().addPlayerToQueue(player, InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()), "NoDebuff", QueueType.PREMIUM);
                    }
                }
            });
        }
        for (final Kit kit : this.plugin.getKitManager().getKits()) {
            final InventoryUI duelInventory = new InventoryUI(CC.PRIMARY + "Select a Duel Map", true, 3);
            final InventoryUI partySplitInventory = new InventoryUI(CC.PRIMARY + "Select a Party Split Map", true, 3);
            final InventoryUI partyFFAInventory = new InventoryUI(CC.PRIMARY + "Select a Party FFA Map", true, 3);
            final InventoryUI redroverInventory = new InventoryUI(CC.PRIMARY + "Select a Redrover Map", true, 3);
            for (final Arena arena : this.plugin.getArenaManager().getArenas().values()) {
                if (!arena.isEnabled()) {
                    continue;
                }
                if (kit.getExcludedArenas().contains(arena.getName())) {
                    continue;
                }
                if (kit.getArenaWhiteList().size() > 0 && !kit.getArenaWhiteList().contains(arena.getName())) {
                    continue;
                }
                final ItemStack book = ItemUtil.createItem(Material.BOOK, CC.GREEN + arena.getName());
                duelInventory.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(book) {
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleDuelMapClick((Player)event.getWhoClicked(), arena, kit);
                    }
                });
                partySplitInventory.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(book) {
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handlePartySplitMapClick((Player)event.getWhoClicked(), arena, kit);
                    }
                });
                partyFFAInventory.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(book) {
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handlePartyFFAMapClick((Player)event.getWhoClicked(), arena, kit);
                    }
                });
                redroverInventory.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(book) {
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleRedroverMapClick((Player)event.getWhoClicked(), arena, kit);
                    }
                });
            }
            this.duelMapInventories.put(kit.getName(), duelInventory);
            this.partySplitMapInventories.put(kit.getName(), partySplitInventory);
            this.partyFFAMapInventories.put(kit.getName(), partyFFAInventory);
            this.redroverMapInventories.put(kit.getName(), redroverInventory);
        }
    }
    
    private void updateInventories() {
        for (int i = 0; i < 18; ++i) {
            final InventoryUI.ClickableItem unrankedItem = this.unrankedInventory.getItem(i);
            if (unrankedItem != null) {
                unrankedItem.setItemStack(this.updateQueueLore(unrankedItem.getItemStack(), QueueType.UNRANKED));
                this.unrankedInventory.setItem(i, unrankedItem);
            }
            final InventoryUI.ClickableItem rankedItem = this.rankedInventory.getItem(i);
            if (rankedItem != null) {
                rankedItem.setItemStack(this.updateQueueLore(rankedItem.getItemStack(), QueueType.RANKED));
                this.rankedInventory.setItem(i, rankedItem);
            }
        }
    }
    
    private ItemStack updateQueueLore(final ItemStack itemStack, final QueueType type) {
        if (itemStack == null) {
            return null;
        }
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            final String ladder = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
            final int queueSize = this.plugin.getQueueManager().getQueueSize(ladder, type);
            final int inGameSize = this.plugin.getMatchManager().getFighters(ladder, type);
            return ItemUtil.reloreItem(itemStack, new String[] { CC.PRIMARY + "Playing: " + CC.SECONDARY + inGameSize, CC.PRIMARY + "Queued: " + CC.SECONDARY + queueSize });
        }
        return null;
    }
    
    private void addToQueue(final Player player, final PlayerData playerData, final Kit kit, final Party party, final QueueType queueType) {
        if (kit != null) {
            if (party == null) {
                this.plugin.getQueueManager().addPlayerToQueue(player, playerData, kit.getName(), queueType);
            }
            else if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                this.plugin.getQueueManager().addPartyToQueue(player, party, kit.getName(), queueType);
            }
        }
    }
    
    public void addSnapshot(final InventorySnapshot snapshot) {
        this.snapshots.put(snapshot.getSnapshotId(), snapshot);
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, () -> this.removeSnapshot(snapshot.getSnapshotId()), 600L);
    }
    
    public void removeSnapshot(final UUID snapshotId) {
        final InventorySnapshot snapshot = this.snapshots.get(snapshotId);
        if (snapshot != null) {
            UIListener.INVENTORIES.remove(snapshot.getInventoryUI());
            this.snapshots.remove(snapshotId);
        }
    }
    
    public InventorySnapshot getSnapshot(final UUID snapshotId) {
        return this.snapshots.get(snapshotId);
    }
    
    public void addParty(final Player player) {
        final ItemStack skull = ItemUtil.createItem(Material.SKULL_ITEM, CC.PRIMARY + player.getName() + " (" + CC.SECONDARY + "1" + CC.PRIMARY + ")");
        this.partyInventory.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(skull) {
            public void onClick(final InventoryClickEvent inventoryClickEvent) {
                player.closeInventory();
                player.performCommand("duel " + player.getName());
            }
        });
    }
    
    public void updateParty(final Party party) {
        final Player player = this.plugin.getServer().getPlayer(party.getLeader());
        for (int i = 0; i < this.partyInventory.getSize(); ++i) {
            final InventoryUI.ClickableItem item = this.partyInventory.getItem(i);
            if (item != null) {
                final ItemStack stack = item.getItemStack();
                if (stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains(player.getName())) {
                    final List<String> lores = new ArrayList<String>();
                    party.members().forEach(member -> lores.add(CC.PRIMARY + member.getName()));
                    ItemUtil.reloreItem(stack, (String[])lores.toArray(new String[0]));
                    ItemUtil.renameItem(stack, CC.PRIMARY + player.getName() + " (" + CC.SECONDARY + party.getMembers().size() + CC.PRIMARY + ")");
                    item.setItemStack(stack);
                    break;
                }
            }
        }
    }
    
    public void removeParty(final Party party) {
        final Player player = this.plugin.getServer().getPlayer(party.getLeader());
        for (int i = 0; i < this.partyInventory.getSize(); ++i) {
            final InventoryUI.ClickableItem item = this.partyInventory.getItem(i);
            if (item != null) {
                final ItemStack stack = item.getItemStack();
                if (stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains(player.getName())) {
                    this.partyInventory.removeItem(i);
                    break;
                }
            }
        }
    }
    
    public void addEditingKitInventory(final Player player, final Kit kit) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        final Map<Integer, PlayerKit> kitMap = playerData.getPlayerKits(kit.getName());
        final InventoryUI inventory = new InventoryUI(CC.PRIMARY + "Editing Kit Layout", true, 4);
        for (int i = 1; i <= 7; ++i) {
            final ItemStack save = ItemUtil.createItem(Material.CHEST, CC.PRIMARY + "Save kit " + CC.SECONDARY + kit.getName() + " #" + i);
            final ItemStack load = ItemUtil.createItem(Material.BOOK, CC.PRIMARY + "Load kit " + CC.SECONDARY + kit.getName() + " #" + i);
            final ItemStack rename = ItemUtil.createItem(Material.NAME_TAG, CC.PRIMARY + "Rename kit " + CC.SECONDARY + kit.getName() + " #" + i);
            final ItemStack delete = ItemUtil.createItem(Material.FLINT, CC.PRIMARY + "Delete kit " + CC.SECONDARY + kit.getName() + " #" + i);
            inventory.setItem(i, (InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(save) {
                public void onClick(final InventoryClickEvent event) {
                    final int kitIndex = event.getSlot();
                    InventoryManager.this.handleSavingKit(player, playerData, kit, kitMap, kitIndex);
                    inventory.setItem(kitIndex + 1, 2, (InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(load) {
                        public void onClick(final InventoryClickEvent event) {
                            InventoryManager.this.handleLoadKit(player, kitIndex, kitMap);
                        }
                    });
                    inventory.setItem(kitIndex + 1, 3, (InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(rename) {
                        public void onClick(final InventoryClickEvent event) {
                            InventoryManager.this.handleRenamingKit(player, kitIndex, kitMap);
                        }
                    });
                    inventory.setItem(kitIndex + 1, 4, (InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(delete) {
                        public void onClick(final InventoryClickEvent event) {
                            InventoryManager.this.handleDeleteKit(player, kitIndex, kitMap, inventory);
                        }
                    });
                }
            });
            final int kitIndex = i;
            if (kitMap != null && kitMap.containsKey(kitIndex)) {
                inventory.setItem(kitIndex + 1, 2, (InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(load) {
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleLoadKit(player, kitIndex, kitMap);
                    }
                });
                inventory.setItem(kitIndex + 1, 3, (InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(rename) {
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleRenamingKit(player, kitIndex, kitMap);
                    }
                });
                inventory.setItem(kitIndex + 1, 4, (InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(delete) {
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleDeleteKit(player, kitIndex, kitMap, inventory);
                    }
                });
            }
        }
        this.editorInventories.put(player.getUniqueId(), inventory);
    }
    
    public void removeEditingKitInventory(final UUID uuid) {
        final InventoryUI inventoryUI = this.editorInventories.get(uuid);
        if (inventoryUI != null) {
            UIListener.INVENTORIES.remove(inventoryUI);
            this.editorInventories.remove(uuid);
        }
    }
    
    public InventoryUI getEditingKitInventory(final UUID uuid) {
        return this.editorInventories.get(uuid);
    }
    
    private void handleSavingKit(final Player player, final PlayerData playerData, final Kit kit, final Map<Integer, PlayerKit> kitMap, final int kitIndex) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            kitMap.get(kitIndex).setContents(player.getInventory().getContents().clone());
            player.sendMessage(CC.PRIMARY + "Successfully saved kit " + CC.SECONDARY + kitIndex + CC.PRIMARY + ".");
            return;
        }
        final PlayerKit playerKit = new PlayerKit(kit.getName(), kitIndex, player.getInventory().getContents().clone(), kit.getName() + " Kit " + kitIndex);
        playerData.addPlayerKit(kitIndex, playerKit);
        player.sendMessage(CC.PRIMARY + "Successfully saved kit " + CC.SECONDARY + kitIndex + CC.PRIMARY + ".");
    }
    
    private void handleLoadKit(final Player player, final int kitIndex, final Map<Integer, PlayerKit> kitMap) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            final ItemStack[] contents2;
            final ItemStack[] contents = contents2 = kitMap.get(kitIndex).getContents();
            for (final ItemStack itemStack : contents2) {
                if (itemStack != null && itemStack.getAmount() <= 0) {
                    itemStack.setAmount(1);
                }
            }
            player.getInventory().setContents(contents);
            player.updateInventory();
        }
    }
    
    private void handleRenamingKit(final Player player, final int kitIndex, final Map<Integer, PlayerKit> kitMap) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            this.plugin.getEditorManager().addRenamingKit(player.getUniqueId(), kitMap.get(kitIndex));
            player.closeInventory();
            player.sendMessage(CC.PRIMARY + "Enter a name for this kit (chat colors are also applicable).");
        }
    }
    
    private void handleDeleteKit(final Player player, final int kitIndex, final Map<Integer, PlayerKit> kitMap, final InventoryUI inventory) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            this.plugin.getEditorManager().removeRenamingKit(player.getUniqueId());
            kitMap.remove(kitIndex);
            player.sendMessage(CC.PRIMARY + "Successfully removed kit " + CC.SECONDARY + kitIndex + CC.PRIMARY + ".");
            inventory.setItem(kitIndex + 1, 2, (InventoryUI.ClickableItem)null);
            inventory.setItem(kitIndex + 1, 3, (InventoryUI.ClickableItem)null);
            inventory.setItem(kitIndex + 1, 4, (InventoryUI.ClickableItem)null);
        }
    }
    
    private void handleDuelClick(final Player player, final Kit kit) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        final Player selected = this.plugin.getServer().getPlayer(playerData.getDuelSelecting());
        if (selected == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, playerData.getDuelSelecting()));
            return;
        }
        final PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(selected.getUniqueId());
        if (targetData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(CC.RED + "Player is not in spawn.");
            return;
        }
        final Party targetParty = this.plugin.getPartyManager().getParty(selected.getUniqueId());
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        final boolean partyDuel = party != null;
        if (partyDuel && targetParty == null) {
            player.sendMessage(CC.RED + "That player is not in a party.");
            return;
        }
        final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
        if (mineman != null && mineman.getRank().hasRank(Rank.CLUBBER)) {
            player.closeInventory();
            player.openInventory(this.duelMapInventories.get(kit.getName()).getCurrentPage());
            return;
        }
        if (this.plugin.getMatchManager().getMatchRequest(player.getUniqueId(), selected.getUniqueId()) != null) {
            player.sendMessage(CC.RED + "You already sent a match request to that player. Please wait until it expires.");
            return;
        }
        final Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
        if (arena == null) {
            player.sendMessage(CC.RED + "No available arenas found.");
            return;
        }
        this.sendDuel(player, selected, kit, partyDuel, party, targetParty, arena);
    }
    
    private void handlePartySplitClick(final Player player, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || kit == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 2) {
            player.sendMessage(InventoryManager.MORE_PLAYERS);
        }
        else {
            final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
            if (mineman != null && mineman.getRank().hasRank(Rank.CLUBBER)) {
                player.closeInventory();
                player.openInventory(this.partySplitMapInventories.get(kit.getName()).getCurrentPage());
                return;
            }
            final Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
            if (arena == null) {
                player.sendMessage(CC.RED + "No available arenas found.");
                return;
            }
            this.createPartySplitMatch(party, arena, kit);
        }
    }
    
    private void handleFFAClick(final Player player, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || kit == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 2) {
            player.sendMessage(InventoryManager.MORE_PLAYERS);
        }
        else {
            final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
            if (mineman != null && mineman.getRank().hasRank(Rank.CLUBBER)) {
                player.closeInventory();
                player.openInventory(this.partyFFAMapInventories.get(kit.getName()).getCurrentPage());
                return;
            }
            final Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
            if (arena == null) {
                player.sendMessage(CC.RED + "No available arenas found.");
                return;
            }
            this.createFFAMatch(party, arena, kit);
        }
    }
    
    private void handleRedroverClick(final Player player, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || kit == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 4) {
            player.sendMessage(CC.RED + "You need more 4 or more players in your party to start an event.");
        }
        else {
            final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
            if (mineman != null && mineman.getRank().hasRank(Rank.CLUBBER)) {
                player.closeInventory();
                player.openInventory(this.redroverMapInventories.get(kit.getName()).getCurrentPage());
                return;
            }
            final Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
            if (arena == null) {
                player.sendMessage(CC.RED + "No available arenas found.");
                return;
            }
            this.createRedroverMatch(party, arena, kit);
        }
    }
    
    private void handleDuelMapClick(final Player player, final Arena arena, final Kit kit) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        final Player selected = this.plugin.getServer().getPlayer(playerData.getDuelSelecting());
        if (selected == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, playerData.getDuelSelecting()));
            return;
        }
        final PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(selected.getUniqueId());
        if (targetData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(CC.RED + "Player is not in spawn.");
            return;
        }
        final Party targetParty = this.plugin.getPartyManager().getParty(selected.getUniqueId());
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        final boolean partyDuel = party != null;
        if (partyDuel && targetParty == null) {
            player.sendMessage(CC.RED + "That player is not in a party.");
            return;
        }
        if (this.plugin.getMatchManager().getMatchRequest(player.getUniqueId(), selected.getUniqueId()) != null) {
            player.sendMessage(CC.RED + "You already sent a match request to that player. Please wait until it expires.");
            return;
        }
        this.sendDuel(player, selected, kit, partyDuel, party, targetParty, arena);
    }
    
    private void handleRedroverMapClick(final Player player, final Arena arena, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 4) {
            player.sendMessage(InventoryManager.MORE_PLAYERS);
        }
        else {
            this.createRedroverMatch(party, arena, kit);
        }
    }
    
    private void handlePartyFFAMapClick(final Player player, final Arena arena, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 2) {
            player.sendMessage(InventoryManager.MORE_PLAYERS);
        }
        else {
            this.createFFAMatch(party, arena, kit);
        }
    }
    
    private void handlePartySplitMapClick(final Player player, final Arena arena, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 2) {
            player.sendMessage(InventoryManager.MORE_PLAYERS);
        }
        else {
            this.createPartySplitMatch(party, arena, kit);
        }
    }
    
    private void sendDuel(final Player player, final Player selected, final Kit kit, final boolean partyDuel, final Party party, final Party targetParty, final Arena arena) {
        this.plugin.getMatchManager().createMatchRequest(player, selected, arena, kit.getName(), partyDuel);
        player.closeInventory();
        final Clickable requestMessage = new Clickable(CC.SECONDARY + player.getName() + CC.PRIMARY + " has sent you a " + (partyDuel ? "party" : "") + "duel request" + ((kit.getName() != null) ? (" with kit " + CC.SECONDARY + kit.getName() + CC.PRIMARY) : "") + ((arena == null) ? "" : (" on arena " + arena.getName())) + ". " + CC.GREEN + "[Accept]", CC.GREEN + "Click to accept", "/accept " + player.getName() + " " + kit.getName());
        if (partyDuel) {
            targetParty.members().forEach(requestMessage::sendToPlayer);
            party.broadcast(CC.PRIMARY + "Sent a party duel request to " + CC.SECONDARY + selected.getName() + CC.PRIMARY + "'s party with kit " + CC.SECONDARY + kit.getName() + CC.PRIMARY + ((arena == null) ? "" : (CC.PRIMARY + " on arena " + arena.getName())) + ".");
        }
        else {
            requestMessage.sendToPlayer(selected);
            player.sendMessage(CC.PRIMARY + "Sent a duel request to " + CC.SECONDARY + selected.getName() + CC.PRIMARY + " with kit " + CC.SECONDARY + kit.getName() + CC.PRIMARY + ((arena == null) ? "" : (CC.PRIMARY + " on arena " + arena.getName())) + ".");
        }
    }
    
    private void createPartySplitMatch(final Party party, final Arena arena, final Kit kit) {
        final MatchTeam[] teams = party.split();
        final Match match = new Match(arena, kit, QueueType.UNRANKED, teams);
        final Player leaderA = this.plugin.getServer().getPlayer(teams[0].getLeader());
        final Player leaderB = this.plugin.getServer().getPlayer(teams[1].getLeader());
        match.broadcast(CC.PRIMARY + "Starting a party split match with kit " + CC.SECONDARY + kit.getName() + CC.PRIMARY + " and arena " + CC.SECONDARY + arena.getName() + CC.PRIMARY + " between " + CC.SECONDARY + leaderA.getName() + CC.PRIMARY + "'s team and " + CC.SECONDARY + leaderB.getName() + CC.PRIMARY + "'s team.");
        this.plugin.getMatchManager().createMatch(match);
    }
    
    private void createFFAMatch(final Party party, final Arena arena, final Kit kit) {
        final MatchTeam team = new MatchTeam(party.getLeader(), Lists.newArrayList((Iterable)party.getMembers()), null, 0);
        final Match match = new Match(arena, kit, QueueType.UNRANKED, new MatchTeam[] { team });
        match.broadcast(CC.PRIMARY + "Starting a party FFA match with kit " + CC.SECONDARY + kit.getName() + CC.PRIMARY + " and arena " + CC.SECONDARY + arena.getName() + CC.PRIMARY + ".");
        this.plugin.getMatchManager().createMatch(match);
    }
    
    private void createRedroverMatch(final Party party, final Arena arena, final Kit kit) {
        final MatchTeam[] teams = party.split();
        final Match match = new Match(arena, kit, QueueType.UNRANKED, true, teams);
        final Player leaderA = this.plugin.getServer().getPlayer(teams[0].getLeader());
        final Player leaderB = this.plugin.getServer().getPlayer(teams[1].getLeader());
        match.broadcast(CC.PRIMARY + "Starting a redrover match with kit " + CC.SECONDARY + kit.getName() + CC.PRIMARY + " and arena " + CC.SECONDARY + arena.getName() + CC.PRIMARY + " between " + CC.SECONDARY + leaderA.getName() + CC.PRIMARY + "'s team and " + CC.SECONDARY + leaderB.getName() + CC.PRIMARY + "'s team.");
        this.plugin.getMatchManager().createMatch(match);
    }
    
    public InventoryUI getUnrankedInventory() {
        return this.unrankedInventory;
    }
    
    public InventoryUI getRankedInventory() {
        return this.rankedInventory;
    }
    
    public InventoryUI getEditorInventory() {
        return this.editorInventory;
    }
    
    public InventoryUI getDuelInventory() {
        return this.duelInventory;
    }
    
    public InventoryUI getPartySplitInventory() {
        return this.partySplitInventory;
    }
    
    public InventoryUI getPartyFFAInventory() {
        return this.partyFFAInventory;
    }
    
    public InventoryUI getRedroverInventory() {
        return this.redroverInventory;
    }
    
    public InventoryUI getJoinPremiumInventory() {
        return this.joinPremiumInventory;
    }
    
    public InventoryUI getPartyEventInventory() {
        return this.partyEventInventory;
    }
    
    public InventoryUI getPartyInventory() {
        return this.partyInventory;
    }
    
    static {
        MORE_PLAYERS = CC.RED + "You need more 2 or more players in your party to start an event.";
    }
}
