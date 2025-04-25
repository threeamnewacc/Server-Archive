package us.zonix.practice.managers;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.maiko.dexter.profile.Profile;
import me.maiko.dexter.rank.Rank;
import me.maiko.dexter.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.zonix.practice.Practice;
import us.zonix.practice.arena.Arena;
import us.zonix.practice.bots.ZonixBot;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.inventory.InventorySnapshot;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.kit.PlayerKit;
import us.zonix.practice.match.Match;
import us.zonix.practice.match.MatchTeam;
import us.zonix.practice.party.Party;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.queue.QueueType;
import us.zonix.practice.util.Clickable;
import us.zonix.practice.util.ItemUtil;
import us.zonix.practice.util.StringUtil;
import us.zonix.practice.util.inventory.InventoryUI;

public class InventoryManager {
    private static final String MORE_PLAYERS = ChatColor.RED + "There must be at least 2 players in your party to do this.";
    private final Practice plugin = Practice.getInstance();
    private final InventoryUI unrankedInventory = new InventoryUI(ChatColor.DARK_RED + "Unranked Queue", true, 2);
    private final InventoryUI rankedInventory = new InventoryUI(ChatColor.DARK_RED + "Ranked Queue", true, 2);
    private final InventoryUI premiumInventory = new InventoryUI(ChatColor.DARK_RED + "Premium Queue", true, 2);
    private final InventoryUI editorInventory = new InventoryUI(ChatColor.DARK_RED + "Kit Editor", true, 2);
    private final InventoryUI coinseventsInventory = new InventoryUI(CC.DARK_RED + "Host Events", true, 2);
    private final InventoryUI eventsInventory = new InventoryUI(ChatColor.DARK_RED + "Host Events", true, 2);
    private final InventoryUI trainInventory = new InventoryUI(ChatColor.DARK_RED + "Training Bot", true, 1);
    private final InventoryUI duelInventory = new InventoryUI(ChatColor.DARK_RED + "Send Request", true, 2);
    private final InventoryUI partySplitInventory = new InventoryUI(ChatColor.DARK_RED + "Split Fights", true, 2);
    private final InventoryUI partyFFAInventory = new InventoryUI(ChatColor.DARK_RED + "Party FFA", true, 2);
    private final InventoryUI bestOfThreeInventory = new InventoryUI(ChatColor.DARK_RED + "Select Match Type", true, 1);
    private final InventoryUI partyEventInventory = new InventoryUI(ChatColor.DARK_RED + "Party Events", true, 1);
    private final InventoryUI partyInventory = new InventoryUI(ChatColor.DARK_RED + "Other Parties", true, 6);
    private final InventoryUI partyMemberInventory = new InventoryUI(ChatColor.DARK_RED + "Edit Party Members", true, 6);
    private final Map<String, InventoryUI> duelMapInventories = new HashMap<>();
    private final Map<String, InventoryUI> trainKitInventories = new HashMap<>();
    private final Map<String, InventoryUI> partySplitMapInventories = new HashMap<>();
    private final Map<String, InventoryUI> partyFFAMapInventories = new HashMap<>();
    private final Map<UUID, InventoryUI> editorInventories = new HashMap<>();
    private final Map<UUID, InventorySnapshot> snapshots = new HashMap<>();
    private int[] KIT_GUI_SLOTS = new int[]{39, 40, 41, 42};

    public InventoryManager() {
        this.setupInventories();
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this::updateInventories, 20L, 20L);
    }

    private void setupInventories() {
        this.bestOfThreeInventory
            .setItem(
                3,
                new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.PAPER, ChatColor.RED + "Single Match")) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        Player player = (Player)event.getWhoClicked();
                        player.closeInventory();
                        PlayerData playerData = InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                        if (playerData != null) {
                            if (playerData.isBestOfThreeDuel()) {
                                InventoryManager.this.handleDuelClick(player, playerData.getBestOfThreeKit(), false);
                                return;
                            }

                            InventoryManager.this.addToQueue(
                                player,
                                InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()),
                                playerData.getBestOfThreeKit(),
                                InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()),
                                playerData.getBestOfThreeQueueType(),
                                false
                            );
                        }
                    }
                }
            );
        this.bestOfThreeInventory
            .setItem(
                5,
                new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.PAPER, ChatColor.RED + "Best of 5 Matches", 5)) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        Player player = (Player)event.getWhoClicked();
                        player.closeInventory();
                        PlayerData playerData = InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                        if (playerData != null) {
                            if (playerData.isBestOfThreeDuel()) {
                                InventoryManager.this.handleDuelClick(player, playerData.getBestOfThreeKit(), true);
                                return;
                            }

                            InventoryManager.this.addToQueue(
                                player,
                                InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()),
                                playerData.getBestOfThreeKit(),
                                InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()),
                                playerData.getBestOfThreeQueueType(),
                                true
                            );
                        }
                    }
                }
            );

        for (final Kit kit : this.plugin.getKitManager().getKits()) {
            if (kit.isEnabled()) {
                this.unrankedInventory
                    .setItem(
                        kit.getPriority(),
                        new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                            @Override
                            public void onClick(InventoryClickEvent event) {
                                Player player = (Player)event.getWhoClicked();
                                if (kit.isHcteams()) {
                                    Party party = InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId());
                                    if (party == null) {
                                        player.sendMessage(ChatColor.RED + "This kit can only be played in a party.");
                                        player.closeInventory();
                                        return;
                                    }

                                    if (party.getArchers().isEmpty() && party.getBards().isEmpty()) {
                                        player.sendMessage(
                                            ChatColor.RED
                                                + "You must specify your party's roles (you must atleast ahve one archer or one bard).\nUse: /party hcteams"
                                        );
                                        player.closeInventory();
                                        player.closeInventory();
                                        return;
                                    }
                                }

                                InventoryManager.this.addToQueue(
                                    player,
                                    InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()),
                                    kit,
                                    InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()),
                                    QueueType.UNRANKED,
                                    false
                                );
                            }
                        }
                    );
                if (kit.isRanked()) {
                    this.rankedInventory
                        .setItem(
                            kit.getPriority(),
                            new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                                @Override
                                public void onClick(InventoryClickEvent event) {
                                    Player player = (Player)event.getWhoClicked();
                                    if (kit.isBestOfThree()) {
                                        InventoryManager.this.handleBestOfThree(player, kit, QueueType.RANKED);
                                    } else {
                                        if (kit.isHcteams()) {
                                            Party party = InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId());
                                            if (party == null) {
                                                player.sendMessage(ChatColor.RED + "This kit can only be played in a party.");
                                                player.closeInventory();
                                                return;
                                            }

                                            if (party.getArchers().isEmpty() || party.getBards().isEmpty()) {
                                                player.sendMessage(ChatColor.RED + "You must specify your party's roles.\nUse: /party hcteams");
                                                player.closeInventory();
                                                player.closeInventory();
                                                return;
                                            }
                                        }

                                        InventoryManager.this.addToQueue(
                                            player,
                                            InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()),
                                            kit,
                                            InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()),
                                            QueueType.RANKED,
                                            false
                                        );
                                    }
                                }
                            }
                        );
                }

                if (kit.isPremium()) {
                    this.premiumInventory
                        .setItem(
                            kit.getPriority(),
                            new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                                @Override
                                public void onClick(InventoryClickEvent event) {
                                    Player player = (Player)event.getWhoClicked();
                                    if (kit.isBestOfThree()) {
                                        InventoryManager.this.handleBestOfThree(player, kit, QueueType.PREMIUM);
                                    } else {
                                        InventoryManager.this.addToQueue(
                                            player,
                                            InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()),
                                            kit,
                                            InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()),
                                            QueueType.PREMIUM,
                                            false
                                        );
                                    }
                                }
                            }
                        );
                }

                this.editorInventory
                    .setItem(
                        kit.getPriority(),
                        new InventoryUI.AbstractClickableItem(
                            ItemUtil.createItem(kit.getIcon().getType(), ChatColor.RED + kit.getName(), 1, kit.getIcon().getDurability())
                        ) {
                            @Override
                            public void onClick(InventoryClickEvent event) {
                                Player player = (Player)event.getWhoClicked();
                                InventoryManager.this.plugin.getEditorManager().addEditor(player, kit);
                                InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()).setPlayerState(PlayerState.EDITING);
                            }
                        }
                    );
                this.duelInventory
                    .setItem(
                        kit.getPriority(),
                        new InventoryUI.AbstractClickableItem(
                            ItemUtil.createItem(kit.getIcon().getType(), ChatColor.RED + kit.getName(), 1, kit.getIcon().getDurability())
                        ) {
                            @Override
                            public void onClick(InventoryClickEvent event) {
                                Player player = (Player)event.getWhoClicked();
                                if (kit.isHcteams()) {
                                    Party party = InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId());
                                    if (party == null) {
                                        player.sendMessage(ChatColor.RED + "This kit can only be played in a party.");
                                        player.closeInventory();
                                        return;
                                    }

                                    if (party.getArchers().isEmpty() || party.getBards().isEmpty()) {
                                        player.sendMessage(ChatColor.RED + "You must specify your party's roles.\nUse: /party hcteams");
                                        player.closeInventory();
                                        player.closeInventory();
                                        return;
                                    }
                                }

                                InventoryManager.this.handleDuelClick((Player)event.getWhoClicked(), kit, false);
                            }
                        }
                    );
                if (!kit.isBuild() && !kit.isParkour() && !kit.isSpleef() && !kit.isSumo()) {
                    this.trainInventory
                        .addItem(
                            new InventoryUI.AbstractClickableItem(
                                ItemUtil.createItem(kit.getIcon().getType(), ChatColor.RED + kit.getName(), 1, kit.getIcon().getDurability())
                            ) {
                                @Override
                                public void onClick(InventoryClickEvent event) {
                                    InventoryManager.this.handleTrainClick((Player)event.getWhoClicked(), kit);
                                }
                            }
                        );
                }

                this.partySplitInventory
                    .setItem(
                        kit.getPriority(),
                        new InventoryUI.AbstractClickableItem(
                            ItemUtil.createItem(kit.getIcon().getType(), ChatColor.RED + kit.getName(), 1, kit.getIcon().getDurability())
                        ) {
                            @Override
                            public void onClick(InventoryClickEvent event) {
                                InventoryManager.this.handlePartySplitClick((Player)event.getWhoClicked(), kit, false);
                            }
                        }
                    );
                this.partyFFAInventory
                    .setItem(
                        kit.getPriority(),
                        new InventoryUI.AbstractClickableItem(
                            ItemUtil.createItem(kit.getIcon().getType(), ChatColor.RED + kit.getName(), 1, kit.getIcon().getDurability())
                        ) {
                            @Override
                            public void onClick(InventoryClickEvent event) {
                                InventoryManager.this.handleFFAClick((Player)event.getWhoClicked(), kit, false);
                            }
                        }
                    );
            }
        }

        this.partyEventInventory
            .setItem(3, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.FIREWORK_CHARGE, ChatColor.RED + "Split Fights")) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    Player player = (Player)event.getWhoClicked();
                    player.closeInventory();
                    player.openInventory(InventoryManager.this.getPartySplitInventory().getCurrentPage());
                }
            });
        this.partyEventInventory.setItem(5, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.SLIME_BALL, ChatColor.RED + "Party FFA")) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                player.closeInventory();
                player.openInventory(InventoryManager.this.getPartyFFAInventory().getCurrentPage());
            }
        });

        for (final PracticeEvent event : this.plugin.getEventManager().getEvents().values()) {
            if (event.isEnabled()) {
                this.eventsInventory
                    .addItem(
                        new InventoryUI.AbstractClickableItem(
                            ItemUtil.reloreItem(
                                event.getItem(), CC.GRAY + CC.ITALIC + "To host the event you must", CC.GRAY + CC.ITALIC + "be a donator of the server."
                            )
                        ) {
                            @Override
                            public void onClick(InventoryClickEvent e) {
                                Player player = (Player)e.getWhoClicked();
                                Profile profile = Profile.getByUuid(player.getUniqueId());
                                Rank rank = profile.getRank();
                                if (event.getName().equalsIgnoreCase("Parkour") && !player.hasPermission("practice.events.parkour")) {
                                    player.sendMessage(
                                        CC.RED + "You cannot host the Parkour Event with " + rank.getGameColor() + rank.getId() + CC.RED + " rank."
                                    );
                                } else if (event.getName().equalsIgnoreCase("Sumo") && !player.hasPermission("practice.events.sumo")) {
                                    player.sendMessage(
                                        CC.RED + "You cannot host the Parkour Event with " + rank.getGameColor() + rank.getId() + CC.RED + " rank."
                                    );
                                } else if (event.getName().equalsIgnoreCase("RedLightGreenLight")
                                    && !player.hasPermission("practice.events.redlightgreenlight")) {
                                    player.sendMessage(
                                        CC.RED + "You cannot host the Parkour Event with " + rank.getGameColor() + rank.getId() + CC.RED + " the rank."
                                    );
                                } else if (event.getName().equalsIgnoreCase("BlockParty") && !player.hasPermission("practice.events.blockparty")) {
                                    player.sendMessage(
                                        CC.RED + "You cannot host the Parkour Event with " + rank.getGameColor() + rank.getId() + CC.RED + " rank."
                                    );
                                } else if (event.getName().equalsIgnoreCase("TNTTag") && !player.hasPermission("practice.events.tnttag")) {
                                    player.sendMessage(
                                        CC.RED + "You cannot host the Parkour Event with " + rank.getGameColor() + rank.getId() + CC.RED + " rank."
                                    );
                                } else {
                                    player.performCommand("host " + event.getName());
                                    player.closeInventory();
                                }
                            }
                        }
                    );
            }
        }

        for (final PracticeEvent event : this.plugin.getEventManager().getEvents().values()) {
            if (event.isEnabled()) {
                this.coinseventsInventory
                    .addItem(
                        new InventoryUI.AbstractClickableItem(
                            ItemUtil.reloreItem(
                                event.getItem(), CC.GRAY + CC.ITALIC + "To host the event you must", CC.GRAY + CC.ITALIC + "be a donator of the server."
                            )
                        ) {
                            @Override
                            public void onClick(InventoryClickEvent e) {
                                Player player = (Player)e.getWhoClicked();
                                player.performCommand("coineventhosting " + event.getName());
                                player.closeInventory();
                            }
                        }
                    );
            }
        }

        for (final Kit kit : this.plugin.getKitManager().getKits()) {
            InventoryUI duelInventory = new InventoryUI("Select Arena", true, 6);
            InventoryUI partySplitInventory = new InventoryUI("Select Arena", true, 6);
            InventoryUI partyFFAInventory = new InventoryUI("Select Arena", true, 6);
            InventoryUI trainingInventory = new InventoryUI("Select Difficulty", true, 1);

            for (final Arena arena : this.plugin.getArenaManager().getArenas().values()) {
                if (arena.isEnabled()
                    && !kit.getExcludedArenas().contains(arena.getName())
                    && (kit.getArenaWhiteList().size() <= 0 || kit.getArenaWhiteList().contains(arena.getName()))) {
                    ItemStack book = ItemUtil.createItem(Material.PAPER, ChatColor.YELLOW + arena.getName());
                    duelInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            Player player = (Player)event.getWhoClicked();
                            PlayerData playerData = InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                            if (kit.isBestOfThree()) {
                                InventoryManager.this.handleBestOfThreeDuel(player, kit, arena);
                            } else {
                                InventoryManager.this.handleDuelMapClick((Player)event.getWhoClicked(), arena, kit, false);
                            }
                        }
                    });
                    partySplitInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            InventoryManager.this.handlePartySplitMapClick((Player)event.getWhoClicked(), arena, kit, false);
                        }
                    });
                    partyFFAInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            InventoryManager.this.handlePartyFFAMapClick((Player)event.getWhoClicked(), arena, kit, false);
                        }
                    });
                }
            }

            int i = 0;

            for (final ZonixBot.BotDifficulty difficulty : ZonixBot.BotDifficulty.values()) {
                trainingInventory.setItem(
                    i,
                    new InventoryUI.AbstractClickableItem(
                        ItemUtil.reloreItem(difficulty.getItem(), ChatColor.GREEN + "Reach: " + ChatColor.WHITE + difficulty.getReach())
                    ) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            InventoryManager.this.handleTrainMapClick((Player)event.getWhoClicked(), difficulty, kit);
                        }
                    }
                );
                i += 2;
            }

            this.duelMapInventories.put(kit.getName(), duelInventory);
            this.partySplitMapInventories.put(kit.getName(), partySplitInventory);
            this.partyFFAMapInventories.put(kit.getName(), partyFFAInventory);
            this.trainKitInventories.put(kit.getName(), trainingInventory);
        }
    }

    private void updateInventories() {
        for (int i = 0; i < 18; i++) {
            InventoryUI.ClickableItem unrankedItem = this.unrankedInventory.getItem(i);
            if (unrankedItem != null) {
                unrankedItem.setItemStack(this.updateQueueLore(unrankedItem.getItemStack(), QueueType.UNRANKED));
                this.unrankedInventory.setItem(i, unrankedItem);
            }

            InventoryUI.ClickableItem rankedItem = this.rankedInventory.getItem(i);
            if (rankedItem != null) {
                rankedItem.setItemStack(this.updateQueueLore(rankedItem.getItemStack(), QueueType.RANKED));
                this.rankedInventory.setItem(i, rankedItem);
            }

            InventoryUI.ClickableItem premiumItem = this.premiumInventory.getItem(i);
            if (premiumItem != null) {
                premiumItem.setItemStack(this.updateQueueLore(premiumItem.getItemStack(), QueueType.PREMIUM));
                this.premiumInventory.setItem(i, premiumItem);
            }
        }
    }

    private ItemStack updateQueueLore(ItemStack itemStack, QueueType queueType) {
        if (itemStack == null) {
            return null;
        } else if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            String ladder = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
            int unrankedQueueSize = this.plugin.getQueueManager().getQueueSize(ladder, QueueType.UNRANKED);
            int unrankedInGameSize = this.plugin.getMatchManager().getFighters(ladder, QueueType.UNRANKED);
            int rankedQueueSize = this.plugin.getQueueManager().getQueueSize(ladder, QueueType.RANKED);
            int rankedInGameSize = this.plugin.getMatchManager().getFighters(ladder, QueueType.RANKED);
            int premiumQueueSize = this.plugin.getQueueManager().getQueueSize(ladder, QueueType.PREMIUM);
            int premiumInGameSize = this.plugin.getMatchManager().getFighters(ladder, QueueType.PREMIUM);
            switch (queueType) {
                case RANKED:
                    return ItemUtil.reloreItem(
                        itemStack,
                        ChatColor.RED + "In Game: " + ChatColor.WHITE + rankedInGameSize,
                        ChatColor.RED + "In Queue: " + ChatColor.WHITE + rankedQueueSize,
                        "",
                        ChatColor.YELLOW + "Click here to play " + ChatColor.RED.toString() + ladder
                    );
                case PREMIUM:
                    return ItemUtil.reloreItem(
                        itemStack,
                        ChatColor.RED + "In Game: " + ChatColor.WHITE + premiumInGameSize,
                        ChatColor.RED + "In Queue: " + ChatColor.WHITE + premiumQueueSize,
                        "",
                        ChatColor.YELLOW + "Click here to play " + ChatColor.RED.toString() + ladder
                    );
                default:
                    return ItemUtil.reloreItem(
                        itemStack,
                        ChatColor.RED + "In Game: " + ChatColor.WHITE + unrankedInGameSize,
                        ChatColor.RED + "In Queue: " + ChatColor.WHITE + unrankedQueueSize,
                        "",
                        ChatColor.YELLOW + "Click here to play " + ChatColor.RED.toString() + ladder
                    );
            }
        } else {
            return null;
        }
    }

    private void addToQueue(Player player, PlayerData playerData, Kit kit, Party party, QueueType queueType, boolean bestOfThree) {
        if (Practice.getInstance().isRegionLock() && (queueType.equals(QueueType.PREMIUM) || queueType.equals(QueueType.RANKED))) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile != null && profile.hasVpnData()) {
                if (Practice.getInstance().getAllowedRegions().stream().noneMatch(profile.getVpnData().getContinentCode()::equalsIgnoreCase)) {
                    player.sendMessage(
                        ChatColor.RED
                            + "Your region does not allow you to join premium/ranked queues on this practice sub-server. Make sure you are on the right proxy and sub-server."
                    );
                    return;
                }
            } else {
                Bukkit.getOnlinePlayers()
                    .parallelStream()
                    .filter(online -> online.hasPermission("core.superadmin"))
                    .forEach(
                        online -> online.sendMessage(
                                ChatColor.RED
                                    + "[!] Couldn't find "
                                    + player.getName()
                                    + "'s region! Make sure the AntiVPN is working correctly, if not please use /regionlock toggle off to disable region-lock."
                            )
                    );
            }
        }

        if (kit != null) {
            if (party == null) {
                this.plugin.getQueueManager().addPlayerToQueue(player, playerData, kit.getName(), queueType, bestOfThree);
            } else if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                this.plugin.getQueueManager().addPartyToQueue(player, party, kit.getName(), queueType, bestOfThree);
            }
        }
    }

    public void addSnapshot(InventorySnapshot snapshot) {
        this.snapshots.put(snapshot.getSnapshotId(), snapshot);
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.removeSnapshot(snapshot.getSnapshotId()), 600L);
    }

    public void removeSnapshot(UUID snapshotId) {
        InventorySnapshot snapshot = this.snapshots.get(snapshotId);
        if (snapshot != null) {
            this.snapshots.remove(snapshotId);
        }
    }

    public InventorySnapshot getSnapshot(UUID snapshotId) {
        return this.snapshots.get(snapshotId);
    }

    public void addParty(final Player player) {
        ItemStack skull = ItemUtil.createItem(Material.SKULL_ITEM, ChatColor.GOLD + player.getName() + " (" + ChatColor.GREEN + "1" + ChatColor.GOLD + ")");
        this.partyInventory.addItem(new InventoryUI.AbstractClickableItem(skull) {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.closeInventory();
                if (inventoryClickEvent.getWhoClicked() instanceof Player) {
                    Player sender = (Player)inventoryClickEvent.getWhoClicked();
                    sender.performCommand("duel " + player.getName());
                }
            }
        });
    }

    public void updateParty(Party party) {
        Player player = this.plugin.getServer().getPlayer(party.getLeader());

        for (int i = 0; i < this.partyInventory.getSize(); i++) {
            InventoryUI.ClickableItem item = this.partyInventory.getItem(i);
            if (item != null) {
                ItemStack stack = item.getItemStack();
                if (stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains(player.getName())) {
                    List<String> lores = new ArrayList<>();
                    party.members().forEach(member -> lores.add(ChatColor.RED + member.getName()));
                    ItemUtil.reloreItem(stack, lores.toArray(new String[0]));
                    ItemUtil.renameItem(stack, ChatColor.GOLD + player.getName() + " (" + ChatColor.GREEN + party.getMembers().size() + ChatColor.GOLD + ")");
                    item.setItemStack(stack);
                    break;
                }
            }
        }
    }

    public void removeParty(Party party) {
        Player player = this.plugin.getServer().getPlayer(party.getLeader());

        for (int i = 0; i < this.partyInventory.getSize(); i++) {
            InventoryUI.ClickableItem item = this.partyInventory.getItem(i);
            if (item != null) {
                ItemStack stack = item.getItemStack();
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
        final InventoryUI inventory = new InventoryUI("Managing Kit Layout", true, 4);

        for (int i = 1; i <= 7; i++) {
            ItemStack save = ItemUtil.createItem(Material.CHEST, ChatColor.YELLOW + "Save Kit " + ChatColor.GREEN + kit.getName() + " #" + i);
            final ItemStack load = ItemUtil.createItem(Material.BOOK, ChatColor.YELLOW + "Load Kit " + ChatColor.GREEN + kit.getName() + " #" + i);
            final ItemStack rename = ItemUtil.createItem(Material.NAME_TAG, ChatColor.YELLOW + "Rename Kit " + ChatColor.GREEN + kit.getName() + " #" + i);
            final ItemStack delete = ItemUtil.createItem(Material.FLINT, ChatColor.YELLOW + "Delete Kit " + ChatColor.GREEN + kit.getName() + " #" + i);
            inventory.setItem(i, new InventoryUI.AbstractClickableItem(save) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    final int kitIndex = event.getSlot();
                    InventoryManager.this.handleSavingKit(player, playerData, kit, kitMap, kitIndex);
                    inventory.setItem(kitIndex + 1, 2, new InventoryUI.AbstractClickableItem(load) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            InventoryManager.this.handleLoadKit(player, kitIndex, kitMap);
                        }
                    });
                    inventory.setItem(kitIndex + 1, 3, new InventoryUI.AbstractClickableItem(rename) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            InventoryManager.this.handleRenamingKit(player, kitIndex, kitMap);
                        }
                    });
                    inventory.setItem(kitIndex + 1, 4, new InventoryUI.AbstractClickableItem(delete) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            InventoryManager.this.handleDeleteKit(player, kitIndex, kitMap, inventory);
                        }
                    });
                }
            });
            if (kitMap != null && kitMap.containsKey(i)) {
                int finalI = i;
                inventory.setItem(finalI + 1, 2, new InventoryUI.AbstractClickableItem(load) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        InventoryManager.this.handleLoadKit(player, finalI, kitMap);
                    }
                });
                inventory.setItem(finalI + 1, 3, new InventoryUI.AbstractClickableItem(rename) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        InventoryManager.this.handleRenamingKit(player, finalI, kitMap);
                    }
                });
                inventory.setItem(finalI + 1, 4, new InventoryUI.AbstractClickableItem(delete) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        InventoryManager.this.handleDeleteKit(player, finalI, kitMap, inventory);
                    }
                });
            }
        }

        this.editorInventories.put(player.getUniqueId(), inventory);
    }

    public void removeEditingKitInventory(UUID uuid) {
        InventoryUI inventoryUI = this.editorInventories.get(uuid);
        if (inventoryUI != null) {
            this.editorInventories.remove(uuid);
        }
    }

    public InventoryUI getEditingKitInventory(UUID uuid) {
        return this.editorInventories.get(uuid);
    }

    private void handleSavingKit(Player player, PlayerData playerData, Kit kit, Map<Integer, PlayerKit> kitMap, int kitIndex) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            kitMap.get(kitIndex).setContents((ItemStack[])player.getInventory().getContents().clone());
            player.sendMessage(ChatColor.GREEN + "Successfully saved kit #" + ChatColor.RED + kitIndex + ChatColor.RED + ".");
        } else {
            PlayerKit playerKit = new PlayerKit(
                kit.getName(), kitIndex, (ItemStack[])player.getInventory().getContents().clone(), kit.getName() + " Kit " + kitIndex
            );
            playerData.addPlayerKit(kitIndex, playerKit);
            player.sendMessage(ChatColor.GREEN + "Successfully saved kit #" + ChatColor.RED + kitIndex + ChatColor.RED + ".");
            player.closeInventory();
        }
    }

    private void handleLoadKit(Player player, int kitIndex, Map<Integer, PlayerKit> kitMap) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            ItemStack[] contents = kitMap.get(kitIndex).getContents();

            for (ItemStack itemStack : contents) {
                if (itemStack != null && itemStack.getAmount() <= 0) {
                    itemStack.setAmount(1);
                }
            }

            player.getInventory().setContents(contents);
            player.updateInventory();
        }
    }

    private void handleRenamingKit(Player player, int kitIndex, Map<Integer, PlayerKit> kitMap) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            this.plugin.getEditorManager().addRenamingKit(player.getUniqueId(), kitMap.get(kitIndex));
            player.closeInventory();
            player.sendMessage(ChatColor.GREEN + "Enter the name you want this kit to be (You can enter chat colors).");
        }
    }

    private void handleDeleteKit(Player player, int kitIndex, Map<Integer, PlayerKit> kitMap, InventoryUI inventory) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            this.plugin.getEditorManager().removeRenamingKit(player.getUniqueId());
            kitMap.remove(kitIndex);
            player.sendMessage(ChatColor.GREEN + "Successfully removed kit " + ChatColor.RED + kitIndex + ChatColor.RED + ".");
            inventory.setItem(kitIndex + 1, 2, null);
            inventory.setItem(kitIndex + 1, 3, null);
            inventory.setItem(kitIndex + 1, 4, null);
        }
    }

    private void handleDuelClick(Player player, Kit kit, boolean bestOfThree) {
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        Player selected = this.plugin.getServer().getPlayer(playerData.getDuelSelecting());
        if (selected == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, playerData.getDuelSelecting()));
        } else {
            PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(selected.getUniqueId());
            if (targetData.getPlayerState() != PlayerState.SPAWN) {
                player.sendMessage(ChatColor.RED + "That player is currently busy.");
            } else {
                Party targetParty = this.plugin.getPartyManager().getParty(selected.getUniqueId());
                Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
                boolean partyDuel = party != null;
                if (partyDuel && targetParty == null) {
                    player.sendMessage(ChatColor.RED + "That player is not in a party.");
                } else if (!kit.isBestOfThree() && player.hasPermission("practice.duel.pick")) {
                    player.closeInventory();
                    player.openInventory(this.duelMapInventories.get(kit.getName()).getCurrentPage());
                } else if (this.plugin.getMatchManager().getMatchRequest(player.getUniqueId(), selected.getUniqueId()) != null) {
                    player.sendMessage(ChatColor.RED + "You have already sent a duel request to this player, please wait.");
                } else {
                    Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
                    if (arena == null) {
                        player.sendMessage(ChatColor.RED + "There are no arenas available at this moment.");
                    } else {
                        this.sendDuel(player, selected, kit, partyDuel, party, targetParty, arena, bestOfThree);
                    }
                }
            }
        }
    }

    private void handlePartySplitClick(Player player, Kit kit, boolean bestOfThree) {
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party != null && kit != null && this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            if (kit.isHcteams()) {
                player.sendMessage(ChatColor.RED + "This kit can only be played with /party duel.");
            } else {
                player.closeInventory();
                if (party.getMembers().size() < 2) {
                    player.sendMessage(MORE_PLAYERS);
                } else {
                    if (!kit.isBestOfThree() && player.hasPermission("practice.duel.pick")) {
                        player.closeInventory();
                        player.openInventory(this.partySplitMapInventories.get(kit.getName()).getCurrentPage());
                        return;
                    }

                    Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
                    if (arena == null) {
                        player.sendMessage(ChatColor.RED + "There are no arenas available at this moment.");
                        return;
                    }

                    this.createPartySplitMatch(party, arena, kit, bestOfThree);
                }
            }
        }
    }

    private void handleFFAClick(Player player, Kit kit, boolean bestOfThree) {
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party != null && kit != null && this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            if (kit.isHcteams()) {
                player.sendMessage(ChatColor.RED + "This kit can only be played with /party duel.");
            } else {
                player.closeInventory();
                if (party.getMembers().size() < 2) {
                    player.sendMessage(MORE_PLAYERS);
                } else {
                    if (!kit.isBestOfThree() && player.hasPermission("practice.duel.pick")) {
                        player.closeInventory();
                        player.openInventory(this.partyFFAMapInventories.get(kit.getName()).getCurrentPage());
                        return;
                    }

                    Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
                    if (arena == null) {
                        player.sendMessage(ChatColor.RED + "There are no arenas available at this moment.");
                        return;
                    }

                    this.createFFAMatch(party, arena, kit, bestOfThree);
                }
            }
        }
    }

    private void handleBestOfThree(Player player, Kit kit, QueueType queueType) {
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (queueType.isBoth()) {
            playerData.setBestOfThreeDuel(true);
            playerData.setBestOfThreeKit(kit);
            playerData.setBestOfThreeQueueType(queueType);
            this.addToQueue(
                player,
                this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()),
                playerData.getBestOfThreeKit(),
                this.plugin.getPartyManager().getParty(player.getUniqueId()),
                playerData.getBestOfThreeQueueType(),
                true
            );
            player.closeInventory();
        } else {
            if (playerData != null) {
                playerData.setBestOfThreeKit(kit);
                playerData.setBestOfThreeQueueType(queueType);
                player.closeInventory();
                player.openInventory(this.bestOfThreeInventory.getCurrentPage());
            }
        }
    }

    private void handleBestOfThreeDuel(Player player, Kit kit) {
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData != null) {
            playerData.setBestOfThreeDuel(true);
            playerData.setBestOfThreeKit(kit);
            player.closeInventory();
            player.openInventory(this.bestOfThreeInventory.getCurrentPage());
        }
    }

    private void handleBestOfThreeDuel(Player player, Kit kit, Arena arena) {
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData != null) {
            playerData.setBestOfThreeDuel(true);
            playerData.setBestOfThreeKit(kit);
            playerData.setBestOfThreeArena(arena);
            player.closeInventory();
            player.openInventory(this.bestOfThreeInventory.getCurrentPage());
        }
    }

    private void handleRedroverClick(Player player, Kit kit, boolean bestOfThree) {
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party != null && kit != null && this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            player.closeInventory();
            if (party.getMembers().size() < 4) {
                player.sendMessage(ChatColor.RED + "There must be at least 4 players in your party to do this.");
            } else {
                if (!kit.isBestOfThree() && player.hasPermission("practice.duel.pick")) {
                    player.closeInventory();
                    return;
                }

                Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
                if (arena == null) {
                    player.sendMessage(ChatColor.RED + "There are no arenas available at this moment.");
                    return;
                }

                this.createRedroverMatch(party, arena, kit, bestOfThree);
            }
        }
    }

    private void handleDuelMapClick(Player player, Arena arena, Kit kit, boolean bestOfThree) {
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        Player selected = this.plugin.getServer().getPlayer(playerData.getDuelSelecting());
        if (selected == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, playerData.getDuelSelecting()));
        } else {
            PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(selected.getUniqueId());
            if (targetData.getPlayerState() != PlayerState.SPAWN) {
                player.sendMessage(ChatColor.RED + "That player is currently busy.");
            } else {
                Party targetParty = this.plugin.getPartyManager().getParty(selected.getUniqueId());
                Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
                boolean partyDuel = party != null;
                if (partyDuel && targetParty == null) {
                    player.sendMessage(ChatColor.RED + "That player is not in a party.");
                } else if (this.plugin.getMatchManager().getMatchRequest(player.getUniqueId(), selected.getUniqueId()) != null) {
                    player.sendMessage(ChatColor.RED + "You have already sent a duel request to this player, please wait.");
                } else {
                    this.sendDuel(player, selected, kit, partyDuel, party, targetParty, arena, bestOfThree);
                }
            }
        }
    }

    private void handleTrainClick(Player player, Kit kit) {
        player.closeInventory();
        player.openInventory(this.trainKitInventories.get(kit.getName()).getCurrentPage());
    }

    private void handleTrainMapClick(Player player, ZonixBot.BotDifficulty difficulty, Kit kit) {
        player.closeInventory();
        this.plugin.getBotManager().createMatch(player, kit, difficulty);
    }

    private void handleRedroverMapClick(Player player, Arena arena, Kit kit, boolean bestOfThree) {
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party != null && this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            player.closeInventory();
            if (party.getMembers().size() < 4) {
                player.sendMessage(MORE_PLAYERS);
            } else {
                this.createRedroverMatch(party, arena, kit, bestOfThree);
            }
        }
    }

    private void handlePartyFFAMapClick(Player player, Arena arena, Kit kit, boolean bestOfThree) {
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party != null && this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            if (kit.isHcteams()) {
                player.sendMessage(ChatColor.RED + "This kit can only be played with /party duel.");
            } else {
                player.closeInventory();
                if (party.getMembers().size() < 2) {
                    player.sendMessage(MORE_PLAYERS);
                } else {
                    this.createFFAMatch(party, arena, kit, bestOfThree);
                }
            }
        }
    }

    private void handlePartySplitMapClick(Player player, Arena arena, Kit kit, boolean bestOfThree) {
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party != null && this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            if (kit.isHcteams()) {
                player.sendMessage(ChatColor.RED + "This kit can only be played with /party duel.");
            } else {
                player.closeInventory();
                if (party.getMembers().size() < 2) {
                    player.sendMessage(MORE_PLAYERS);
                } else {
                    this.createPartySplitMatch(party, arena, kit, bestOfThree);
                }
            }
        }
    }

    private void sendDuel(Player player, Player selected, Kit kit, boolean partyDuel, Party party, Party targetParty, Arena arena, boolean bestOfThree) {
        this.plugin.getMatchManager().createMatchRequest(player, selected, arena, kit.getName(), partyDuel, bestOfThree);
        player.closeInventory();
        String requestGetString = ChatColor.GREEN
            + player.getName()
            + (partyDuel ? ChatColor.YELLOW + "'s party " + ChatColor.GREEN + "(" + party.getMembers().size() + ")" : "")
            + ChatColor.YELLOW
            + " has requested a duel with the kit "
            + ChatColor.GOLD
            + kit.getName()
            + ChatColor.YELLOW
            + " on "
            + ChatColor.GOLD
            + arena.getName()
            + ChatColor.YELLOW
            + ". "
            + ChatColor.GRAY
            + "[Click to Accept]";
        String requestSendString = ChatColor.YELLOW
            + "Sent a duel request to "
            + ChatColor.GREEN
            + selected.getName()
            + (partyDuel ? ChatColor.YELLOW + "'s party " + ChatColor.GREEN + "(" + party.getMembers().size() + ")" : "")
            + ChatColor.YELLOW
            + " with the kit "
            + ChatColor.GREEN
            + kit.getName()
            + ChatColor.YELLOW
            + ".";
        Clickable requestMessage = new Clickable(requestGetString, ChatColor.GRAY + "Click to accept duel", "/accept " + player.getName() + " " + kit.getName());
        if (partyDuel) {
            targetParty.members().forEach(requestMessage::sendToPlayer);
            party.broadcast(requestSendString);
        } else {
            requestMessage.sendToPlayer(selected);
            player.sendMessage(requestSendString);
        }
    }

    private void createPartySplitMatch(Party party, Arena arena, Kit kit, boolean bestOfThree) {
        MatchTeam[] teams = party.split();
        Match match = new Match(arena, kit, QueueType.UNRANKED, bestOfThree, teams);
        Player leaderA = this.plugin.getServer().getPlayer(teams[0].getLeader());
        Player leaderB = this.plugin.getServer().getPlayer(teams[1].getLeader());
        match.broadcast(ChatColor.YELLOW + "Starting a Split Party match with kit " + ChatColor.GREEN + kit.getName() + ".");
        this.plugin.getMatchManager().createMatch(match);
    }

    private void createFFAMatch(Party party, Arena arena, Kit kit, boolean bestOfThree) {
        MatchTeam team = new MatchTeam(party.getLeader(), Lists.newArrayList(party.getMembers()), 0);
        Match match = new Match(arena, kit, QueueType.UNRANKED, bestOfThree, team);
        match.broadcast(ChatColor.YELLOW + "Starting a Party FFA match with kit " + ChatColor.GREEN + kit.getName() + ".");
        this.plugin.getMatchManager().createMatch(match);
    }

    private void createRedroverMatch(Party party, Arena arena, Kit kit, boolean bestOfThree) {
        MatchTeam[] teams = party.split();
        Match match = new Match(arena, kit, QueueType.UNRANKED, true, false, bestOfThree, teams);
        Player leaderA = this.plugin.getServer().getPlayer(teams[0].getLeader());
        Player leaderB = this.plugin.getServer().getPlayer(teams[1].getLeader());
        match.broadcast(ChatColor.YELLOW + "Starting a Redrover match with kit " + ChatColor.GREEN + kit.getName() + ".");
        this.plugin.getMatchManager().createMatch(match);
    }

    public InventoryUI getUnrankedInventory() {
        return this.unrankedInventory;
    }

    public InventoryUI getRankedInventory() {
        return this.rankedInventory;
    }

    public InventoryUI getPremiumInventory() {
        return this.premiumInventory;
    }

    public InventoryUI getEditorInventory() {
        return this.editorInventory;
    }

    public InventoryUI getCoinseventsInventory() {
        return this.coinseventsInventory;
    }

    public InventoryUI getEventsInventory() {
        return this.eventsInventory;
    }

    public InventoryUI getTrainInventory() {
        return this.trainInventory;
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

    public InventoryUI getBestOfThreeInventory() {
        return this.bestOfThreeInventory;
    }

    public InventoryUI getPartyEventInventory() {
        return this.partyEventInventory;
    }

    public InventoryUI getPartyInventory() {
        return this.partyInventory;
    }

    public InventoryUI getPartyMemberInventory() {
        return this.partyMemberInventory;
    }
}
