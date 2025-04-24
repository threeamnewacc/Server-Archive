package me.devkevin.practice.profile.listener;

import club.inverted.chatcolor.CC;
import com.bizarrealex.aether.Aether;
import com.bizarrealex.aether.event.BoardCreateEvent;
import com.bizarrealex.aether.scoreboard.Board;
import lombok.Getter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.events.oitc.OITCEvent;
import me.devkevin.practice.events.oitc.OITCPlayer;
import me.devkevin.practice.hcf.menu.ClassSelectionMenu;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.kit.PlayerKit;
import me.devkevin.practice.leaderboard.menu.LeaderboardsMenu;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchState;
import me.devkevin.practice.match.matches.OngoingMatchesMenu;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.util.Animation;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright 10/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class ProfileListener implements Listener {
    private final List<String> blockedCommands = new ArrayList<>();

    @Getter
    private final Practice plugin = Practice.getInstance();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        me.devkevin.practice.profile.Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
        if (profile == null) return;

        CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

        Board board = Board.getByPlayer(player);
        if (board != null) Board.getBoards().remove(board);

        switch (profile.getState()) {
            case FIGHTING:
                profile.setLeaving(true);
                this.plugin.getMatchManager().removeFighter(player, profile, false);
                break;
            case SPECTATING:
                if (this.plugin.getEventManager().getSpectators().contains(player.getUniqueId())) {
                    this.plugin.getEventManager().removeSpectator(player);
                } else {
                    this.plugin.getMatchManager().removeSpectator(player);
                }
                break;
            case QUEUE:
                if (party == null) {
                    this.plugin.getQueue().removePlayerFromQueue(player);
                } else if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                    this.plugin.getQueue().removePartyFromQueue(party);
                }
                break;
            case EVENT:
                PracticeEvent<?> practiceEvent = this.plugin.getEventManager().getEventPlaying(player);
                if (practiceEvent != null) {
                    practiceEvent.leave(player);
                }
                break;
        }
        profile.getCachedPlayer().clear();
        this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
        this.plugin.getPartyManager().leaveParty(player);
        this.plugin.getPartyManager().removePartyInvites(player.getUniqueId());
        this.plugin.getTournamentManager().leaveTournament(player);

        try {
            this.plugin.getProfileManager().removePlayerData(player.getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        me.devkevin.practice.profile.Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
        if (profile == null) return;

        CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

        Board board = Board.getByPlayer(player);
        if (board != null) Board.getBoards().remove(board);

        switch (profile.getState()) {
            case FIGHTING:
                profile.setLeaving(true);
                this.plugin.getMatchManager().removeFighter(player, profile, false);
                break;
            case SPECTATING:

                if(this.plugin.getEventManager().getSpectators().contains(player.getUniqueId())) {
                    this.plugin.getEventManager().removeSpectator(player);
                } else {
                    this.plugin.getMatchManager().removeSpectator(player);
                }

                break;
            case QUEUE:
                if (party == null) {
                    this.plugin.getQueue().removePlayerFromQueue(player);
                } else if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                    this.plugin.getQueue().removePartyFromQueue(party);
                }
                break;
            case EVENT:
                PracticeEvent<?> practiceEvent = this.plugin.getEventManager().getEventPlaying(player);
                if (practiceEvent != null) { // A redundant check, but just in case
                    practiceEvent.leave(player);
                }
                break;
        }

        profile.getCachedPlayer().clear();
        this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
        this.plugin.getPartyManager().leaveParty(player);
        this.plugin.getPartyManager().removePartyInvites(player.getUniqueId());
        this.plugin.getTournamentManager().leaveTournament(player);

        try {
            this.plugin.getProfileManager().removePlayerData(player.getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onProfileFirstConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.plugin.getProfileManager().createPlayerData(player);

        if (Board.getByPlayer(player) == null) {
            Aether board = this.plugin.getBoard();
            Bukkit.getPluginManager().callEvent(new BoardCreateEvent(new Board(player, board, board.getOptions()), player));
        }

        try {
            this.plugin.getProfileManager().sendToSpawn(player);
        } catch (Exception e) {
            player.sendMessage(CC.RED + "Error: spawn location not set.");
        }

        Animation animation = new Animation("footer", player.getUniqueId(), 30L);

        animation.getLines().add(CC.GRAY + CC.I + "prac.lol");
        animation.getLines().add(CC.GRAY + CC.I + "store.prac.lol");
        animation.getLines().add(CC.GRAY + CC.I + "prac.lol/discord");
        animation.getLines().add(CC.GRAY + CC.I + "prac.lol/twitter");
    }

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        me.devkevin.practice.profile.Profile profile = null;

        try {
            profile = new me.devkevin.practice.profile.Profile(event.getUniqueId());

            if (profile.getName() == null) {
                profile.setName(event.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerInteractSoup(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!player.isDead() && player.getItemInHand().getType() == Material.MUSHROOM_SOUP && player.getHealth() < 19.0) {
            final double newHealth = Math.min(player.getHealth() + 7.0, 20.0);
            player.setHealth(newHealth);
            player.getItemInHand().setType(Material.BOWL);
            player.updateInventory();
        }
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        Material type = item.getType();
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileData(player.getUniqueId());
        ProfileState state = profile.getState();
        if (state == ProfileState.SPAWN || state == ProfileState.EDITING || state == ProfileState.SPECTATING) {
            return;
        }

        if (type != Material.GOLDEN_APPLE) {
            return;
        }

        if (state != ProfileState.FIGHTING) {
            return;
        }

        if (profile.isFighting()) {
            if (!item.hasItemMeta()) {
                return;
            }
            if (!item.getItemMeta().hasDisplayName()) {
                return;
            }
            if (!item.getItemMeta().getDisplayName().contains("Golden Head")) {
                return;
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
            player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
        }
    }

    @EventHandler
    public void onRegenerate(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) {
            return;
        }

        Player player = (Player) event.getEntity();
        Profile profile = plugin.getProfileManager().getProfileData(player.getUniqueId());
        switch (profile.getState()) {
            case FIGHTING:
                Match match = plugin.getMatchManager().getMatch(profile);
                if (match.getKit().isBoxing() || match.getKit().isBuild() || match.getKit().isSumo()) {
                    event.setCancelled(true);
                }

                EntityRegainHealthEvent.RegainReason reason = event.getRegainReason();
                double amount = event.getAmount();

                if (reason == EntityRegainHealthEvent.RegainReason.MAGIC && amount > 2.0) {
                    profile.setWastedHP(8.0 - amount);
                }
                break;
            case EVENT:
                // TODO:
                break;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Profile profileData = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        if ((event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)) {
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE) {
            if (profileData.getState() != ProfileState.SPECTATING) {
                return;
            }
        }
        if (profileData.getState() == ProfileState.SPECTATING) {
            event.setCancelled(true);
        }

        if (event.getAction().name().endsWith("_BLOCK")) {
            if (event.getClickedBlock().getType().name().contains("SIGN") && event.getClickedBlock().getState() instanceof Sign) {
                Sign sign = (Sign) event.getClickedBlock().getState();
                if (ChatColor.stripColor(sign.getLine(1)).equals("[Soup]")) {
                    event.setCancelled(true);

                    Inventory inventory = this.plugin.getServer().createInventory(null, 54,
                            ChatColor.DARK_GRAY + "Soup Refill");

                    for (int i = 0; i < 54; i++) {
                        inventory.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
                    }

                    event.getPlayer().openInventory(inventory);
                }
            }
            if (event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                event.setCancelled(true);
            }

            if (event.getClickedBlock().getType().name().contains("DOOR")) {
                event.setCancelled(true);
            }

            if (event.getClickedBlock().getType().name().contains("FENCE")) {
                event.setCancelled(true);
            }
        }

        if (event.getAction().name().startsWith("RIGHT_")) {
            ItemStack item = event.getItem();
            Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
            if (player.getItemInHand() == null || !event.hasItem()) return;

            switch (profileData.getState()) {
                case LOADING:
                    player.sendMessage(
                            CC.RED + "You must wait until your player data has loaded before you can use items.");
                    break;
                case SPAWN:
                    if (item == null) {
                        return;
                    }
                    event.setCancelled(true);
                    switch (item.getType()) {
                        case DIAMOND_SWORD:
                            if (party != null) { // in the case the plugin bugged
                                player.sendMessage(CC.RED + "You can't join to the Ranked Queue while in a party.");
                                return;
                            }

                            if (this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
                                player.sendMessage(CC.RED + "You can't do that while you're in a tournament.");
                                return;
                            }

                            CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                            if (profileData.getMatchesPlayed() >= 10) {
                                player.openInventory(plugin.getQueueMenu().getRankedMenu().getCurrentPage());
                            } else if (coreProfile.hasDonor()) {
                                player.openInventory(plugin.getQueueMenu().getRankedMenu().getCurrentPage());
                            } else {
                                player.sendMessage(ChatColor.RED + "You need to play " + (10  - profileData.getMatchesPlayed()) + " unranked matches before playing ranked!");
                            }

                            break;
                        case IRON_SWORD:
                            if (party != null) { // in the case the plugin bugged
                                player.sendMessage(CC.RED + "You can't join to the Unranked Queue while in a party.");
                                return;
                            }

                            if (this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
                                player.sendMessage(CC.RED + "You can't do that while you're in a tournament.");
                                return;
                            }

                            player.openInventory(this.plugin.getQueueMenu().getUnrankedMenu().getCurrentPage());

                            //player.openInventory(this.plugin.getQueueJoinMenu().getUnrankedMenu().getCurrentPage());
                            break;
                        case GOLD_SWORD:
                            //player.openInventory(this.plugin.getQueueJoinMenu().getBotSelectKitMenu().getCurrentPage());
                            break;
                        case WOOD_SWORD:
                            player.sendMessage(CC.RED + "Soon ");
                            break;
                        case NAME_TAG:
                            this.plugin.getPartyManager().createParty(player);
                            break;
                        case CHEST:
                            player.openInventory(this.plugin.getGeneralSettingMenu().getGeneralMenu().getCurrentPage());
                            break;
                        case PAPER:
                            if (item.isSimilar(plugin.getHotbarItem().getPlayAgain())) {
                                if (plugin.getMatchManager().hasPlayAgainRequest(player.getUniqueId())) {
                                    player.performCommand("playagain");
                                }
                            } else {
                                if (party == null) {
                                    player.performCommand("inventory " + profileData.getLastSnapshot().getSnapshotId().toString());
                                    return;
                                }

                                if (!this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                                    player.sendMessage(ChatColor.RED + "You are not the leader of this party.");
                                    return;
                                }
                                player.openInventory(this.plugin.getPartyMenu().getPartyMenu().getCurrentPage());
                            }
                            break;
                        case BOOK:
                            player.openInventory(this.plugin.getKitEditorMenu().getKitEditor().getCurrentPage());
                            break;
                        case INK_SACK:

                            if (item.getData().getData() == 1) {
                                UUID rematching = this.plugin.getMatchManager().getRematcher(player.getUniqueId());
                                Player rematcher = this.plugin.getServer().getPlayer(rematching);

                                if (rematcher == null) {
                                    player.sendMessage(ChatColor.RED + "That player is not online.");
                                    return;
                                }

                                if (this.plugin.getMatchManager()
                                        .getMatchRequest(rematcher.getUniqueId(), player.getUniqueId()) != null) {
                                    this.plugin.getServer().dispatchCommand(player, "accept " + rematcher.getName());
                                } else {
                                    this.plugin.getServer().dispatchCommand(player, "duel " + rematcher.getName());
                                }
                            }
                            break;
                        /*case EYE_OF_ENDER:
                            player.openInventory(this.plugin.getHostMenu().getHostMenu().getCurrentPage());
                            break;*/
                        case COMPASS:
                            new OngoingMatchesMenu().openMenu(player);
                            break;
                        case SIGN:
                            new LeaderboardsMenu().openMenu(player);
                            break;
                        case TRIPWIRE_HOOK:
                            if (event.getAction() ==  Action.RIGHT_CLICK_AIR ||
                                    event.getAction() ==  Action.LEFT_CLICK_AIR ||
                                    event.getAction() ==  Action.RIGHT_CLICK_BLOCK ||
                                    event.getAction() ==  Action.LEFT_CLICK_BLOCK) {
                                player.sendMessage(CC.translate("&aYour current cps: &c" + profileData.getCurrentCps()));
                            }
                            break;
                    }
                case QUEUE:
                    if (item == null) {
                        return;
                    }

                    if (item.getType() == Material.INK_SACK) {
                        if (party == null) {
                            this.plugin.getQueue().removePlayerFromQueue(player);
                        } else {
                            this.plugin.getQueue().removePartyFromQueue(party);
                        }
                        break;
                    }
                    break;
                case PARTY:
                    if (item == null) {
                        return;
                    }

                    switch (item.getType()) {
                        case PAPER:
                            player.performCommand("party info");
                            break;
                        case INK_SACK:
                            this.plugin.getPartyManager().leaveParty(player);
                            break;
                        case EMERALD:
                            UUID rematching = this.plugin.getMatchManager().getRematcher(player.getUniqueId());
                            Player rematcher = this.plugin.getServer().getPlayer(rematching);

                            if (rematcher == null) {
                                player.sendMessage(CC.RED + "That player is no longer online.");
                                return;
                            }

                            if (this.plugin.getMatchManager()
                                    .getMatchRequest(rematcher.getUniqueId(), player.getUniqueId()) != null) {
                                this.plugin.getServer().dispatchCommand(player, "accept " + rematcher.getName());
                            } else {
                                this.plugin.getServer().dispatchCommand(player, "duel " + rematcher.getName());
                            }
                            break;
                        case GOLD_AXE:
                            player.openInventory(this.plugin.getPartyQueueJoinMenu().getPartyQueueMenu().getCurrentPage());
                            break;
                        case SLIME_BALL:
                            if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                                player.sendMessage(CC.RED + "You're not the leader of your party.");
                                return;
                            }
                            player.openInventory(this.plugin.getPartyMenu().getPartyMenu().getCurrentPage());
                            break;
                        case GOLD_SWORD:
                            if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                                player.sendMessage(CC.RED + "You're not the leader of your party.");
                                return;
                            }
                            player.openInventory(this.getPlugin().getPartyMenu().getPartyEvent().getCurrentPage());
                            break;
                        case NETHER_STAR:
                            player.performCommand("party list");
                            break;
                        case BOOK:
                            if (party != null) {
                                if (!party.getLeader().equals(player.getUniqueId())) {
                                    player.sendMessage(CC.RED + "You are not the leader of the party!");
                                    return;
                                }

                                new ClassSelectionMenu().openMenu(player);
                            }
                            break;
                    }
                    break;
                case SPECTATING:
                    if (item == null) {
                        return;
                    }

                    if (item.isSimilar(plugin.getHotbarItem().getPlayAgain())) {
                        if (plugin.getMatchManager().hasPlayAgainRequest(player.getUniqueId())) {
                            player.performCommand("playagain");
                        }
                    }

                    if (item.getType() == Material.INK_SACK) {
                        if (this.plugin.getEventManager().getSpectators().contains(player.getUniqueId())) {
                            this.plugin.getEventManager().removeSpectator(player);
                        } else if (party == null) {
                            this.plugin.getMatchManager().removeSpectator(player);
                        } else {
                            this.plugin.getPartyManager().leaveParty(player);
                        }
                    }
                    break;
                case EDITING:
                    if(event.getClickedBlock() == null) {
                        return;
                    }
                    switch (event.getClickedBlock().getType()) {
                        case WALL_SIGN:
                        case SIGN:
                        case SIGN_POST:
                            this.plugin.getEditorManager().removeEditor(player.getUniqueId());
                            Bukkit.getScheduler().runTaskLater(plugin, () ->
                                    this.plugin.getProfileManager().sendToSpawn(player), 1L);
                            break;
                        case CHEST:
                            Kit kit = this.plugin.getKitManager()
                                    .getKit(this.plugin.getEditorManager().getEditingKit(player.getUniqueId()));

                            if (kit.isCombo()) {
                                player.closeInventory();
                                event.setCancelled(true);
                                player.sendMessage(CC.RED + "You are not allowed to do this while editing a GApple kit.");
                            }

                            else if (kit.isBuild()) {
                                player.closeInventory();
                                event.setCancelled(true);
                                player.sendMessage(CC.RED + "You are not allowed to do this while editing a Build kit.");
                            }

                            else if (kit.isBoxing()) {
                                player.closeInventory();
                                event.setCancelled(true);
                                player.sendMessage(CC.RED + "You are not allowed to do this while editing a Boxing kit.");
                            }

                            //Check if the edit kit contents are empty before opening the inventory.
                            else if (kit.getKitEditContents()[0] != null) {
                                Inventory editorInventory = this.plugin.getServer().createInventory(null, 36);

                                editorInventory.setContents(kit.getKitEditContents());
                                player.openInventory(editorInventory);
                            }
                            event.setCancelled(true);
                            break;
                        case ANVIL:
                            player.openInventory(
                                    this.plugin.getKitEditorMenu().getEditingKitInventory(player.getUniqueId()).getCurrentPage());
                            event.setCancelled(true);
                            break;
                        case ENDER_PEARL:
                            event.setCancelled(true);
                            break;
                    }
                case STAFF:
                    if (item == null) {
                        return;
                    }

                    switch (item.getType()) {
                        case INK_SACK:
                            // reset player from staff mode
                            this.plugin.getProfileManager().sendToSpawn(player);
                            break;
                    }
                    break;
                case FIGHTING:
                    if (item == null) {
                        return;
                    }
                    Match match = this.plugin.getMatchManager().getMatch(profileData);

                    if (item.isSimilar(plugin.getHotbarItem().getPlayAgain())) {
                        if (plugin.getMatchManager().hasPlayAgainRequest(player.getUniqueId())) {
                            player.performCommand("playagain");
                        }
                    }

                    switch (item.getType()) {
                        case MUSHROOM_SOUP:
                            if(player.getHealth() <= 19.0D && !player.isDead()) {
                                if(player.getHealth() < 20.0D || player.getFoodLevel() < 20) {
                                    player.getItemInHand().setType(Material.BOWL);
                                }
                                player.setHealth(player.getHealth() + 7.0D > 20.0D ? 20.0D : player.getHealth() +
                                        7.0D);
                                player.setFoodLevel(player.getFoodLevel() + 2 > 20 ? 20 : player.getFoodLevel() + 2);
                                player.setSaturation(12.8F);
                                player.updateInventory();
                            }
                            break;
                        case ENCHANTED_BOOK:
                            Kit kit = match.getKit();
                            PlayerInventory inventory = player.getInventory();

                            int kitIndex = inventory.getHeldItemSlot();
                            if(kitIndex == 8) {
                                kit.applyToPlayer(player);
                            } else {
                                Map<Integer, PlayerKit> kits = profileData.getPlayerKits(kit.getName());

                                PlayerKit playerKit = kits.get(kitIndex + 1);

                                if(playerKit != null) {
                                    playerKit.applyToPlayer(player);
                                }
                            }
                            break;
                        case POTION:
                            if (match.getMatchState() == MatchState.STARTING) {
                                if (!Potion.fromItemStack(item).isSplash()) {
                                    break;
                                }
                                event.setCancelled(true);
                                player.sendMessage(ChatColor.RED + "You can't throw pots in your current state!");
                                player.updateInventory();
                            }
                            break;
                        case ENDER_PEARL:
                            if (match.getMatchState() == MatchState.STARTING) {
                                event.setCancelled(true);
                                player.sendMessage(ChatColor.RED + "You can't throw enderpearls in your current state!");
                                player.updateInventory();
                            }
                            break;
                    }
                    break;
                case EVENT:
                    if (item == null) {
                        return;
                    }
                    if (item.getType() == Material.NETHER_STAR) {
                        PracticeEvent eventPlaying = this.plugin.getEventManager().getEventPlaying(player);

                        if (eventPlaying != null) {
                            eventPlaying.leave(player);
                        } else {
                            this.plugin.getProfileManager().sendToSpawn(player);
                        }
                    }
                    break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        me.devkevin.practice.profile.Profile profile = plugin.getProfileManager().getProfileData(player.getUniqueId());

        if (profile == null) {
            return;
        }

        if (profile.isInSpawn()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        me.devkevin.practice.profile.Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        if (profile == null) {
            return;
        }

        switch (profile.getState()) {
            case EVENT:
                PracticeEvent currentEvent = this.plugin.getEventManager().getEventPlaying(player);

                if(currentEvent != null) {
                    if(currentEvent instanceof OITCEvent) {
                        OITCEvent oitcEvent = (OITCEvent) currentEvent;
                        OITCPlayer oitcKiller = oitcEvent.getPlayer(player.getKiller());
                        OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);
                        oitcPlayer.setLastKiller(oitcKiller);
                        //PlayerUtil.re(event);
                        break;
                    }

                    if (currentEvent.onDeath() != null) {
                        currentEvent.onDeath().accept(player);
                    }
                }
                break;
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        profile.getPotions().clear();
        profile.getPackets().clear();

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (player.isDead()) {
                ((CraftPlayer) player).getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
            }
        });

        switch (profile.getState()) {
            case FIGHTING: {
                Match match = plugin.getMatchManager().getMatch(player.getUniqueId());

                if (match.isPartyMatch()) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        if (player.isDead()) {
                            ((CraftPlayer) player).getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
                        }
                    });
                }

                this.plugin.getMatchManager().removeFighter(player, profile, true);
            }
            case EVENT:
                PracticeEvent<?> currentEvent = this.plugin.getEventManager().getEventPlaying(player);

                if (currentEvent != null) {
                    if (currentEvent instanceof OITCEvent) {
                        OITCEvent oitcEvent = (OITCEvent) currentEvent;
                        OITCPlayer oitcKiller = oitcEvent.getPlayer(player.getKiller());
                        OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);
                        oitcPlayer.setLastKiller(oitcKiller);
                        //PlayerUtil.respawnPlayer(event);
                        break;
                    }

                    if (currentEvent.onDeath() != null) {
                        currentEvent.onDeath().accept(player);
                    }
                }
                break;
        }

        event.setDroppedExp(0);
        event.setDeathMessage(null);
        event.getDrops().clear();
    }

    /*@EventHandler
    public void onHungerDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.STARVATION) {
            event.setCancelled(true);
        }
    }*/

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        me.devkevin.practice.profile.Profile practicePlayerData = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
        if (practicePlayerData.getState() == ProfileState.FIGHTING) {

            Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

            if (match.getKit().isBoxing() || match.getKit().isSumo() || this.plugin.getEventManager().getEventPlaying(player) != null) {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        Item itemDrop = event.getItemDrop();
        Material drop = itemDrop.getItemStack().getType();

        boolean noDrop = drop.name().contains("_SWORD") || drop.name().contains("_AXE") || drop.name().contains("_SPADE") || drop.name().contains("_PICKAXE") || drop == Material.BOW || drop == Material.ENCHANTED_BOOK || drop == Material.MUSHROOM_SOUP;

        if (profile.getState() == ProfileState.FIGHTING) {
            if (drop == Material.ENCHANTED_BOOK) {
                event.setCancelled(true);
            } else if (noDrop) {
                event.setCancelled(true);
                player.sendMessage(CC.RED + "You can't drop weapons in 1v1s.");
            } else {
                Match match = this.plugin.getMatchManager().getMatch(event.getPlayer().getUniqueId());
                this.plugin.getMatchManager().addDroppedItem(match, event.getItemDrop());
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        if (profile.getState() == ProfileState.FIGHTING) {
            Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

            if (match.getEntitiesToRemove().contains(event.getItem())) {
                match.removeEntityToRemove(event.getItem());
            } else {
                event.setCancelled(true);
            }
        } else if (profile.isSpectating()) {
            // don't let player pick items while are in spec mode
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack stack = event.getItem();
        if (stack == null || stack.getType() != Material.POTION) return;

        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> {
        }, 1L);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player) event.getEntity().getShooter();
            me.devkevin.practice.profile.Profile shooterData = this.plugin.getProfileManager().getProfileData(shooter.getUniqueId());

            if (shooterData.getState() == ProfileState.FIGHTING) {
                Match match = this.plugin.getMatchManager().getMatch(shooter.getUniqueId());
                match.addEntityToRemove(event.getEntity());
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player) event.getEntity().getShooter();
            me.devkevin.practice.profile.Profile shooterData = this.plugin.getProfileManager().getProfileData(shooter.getUniqueId());

            if (shooterData != null) {
                if (shooterData.getState() == ProfileState.FIGHTING) {
                    Match match = this.plugin.getMatchManager().getMatch(shooter.getUniqueId());

                    match.removeEntityToRemove(event.getEntity());

                    if (event.getEntityType() == EntityType.ARROW) {
                        event.getEntity().remove();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        if (player.getLastDamageCause() != null && player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            if (((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager() instanceof FishHook) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        String chatMessage = event.getMessage();
        Profile profile = plugin.getProfileManager().getProfileData(player.getUniqueId());

        if (party != null) {
            if (chatMessage.startsWith("!") || chatMessage.startsWith("@")) {
                event.setCancelled(true);

                String message = ChatColor.GOLD + "(Party) " + ChatColor.WHITE + player.getName() + ChatColor.GRAY + ": " + chatMessage.replaceFirst("!", "").replaceFirst("@", "");

                party.broadcast(message);
            }
        } else {
            PlayerKit kitRenaming = this.plugin.getEditorManager().getRenamingKit(player.getUniqueId());

            if(kitRenaming != null) {
                kitRenaming.setDisplayName(ChatColor.translateAlternateColorCodes('&', chatMessage));
                event.setCancelled(true);
                event.getPlayer().sendMessage(CC.GREEN + "Successfully renamed kit " + CC.PINK + "#" + kitRenaming.getIndex() + CC.GREEN + "'s name to " + CC.PINK + kitRenaming.getDisplayName() + CC.GREEN + ".");
                this.plugin.getEditorManager().removeRenamingKit(event.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        me.devkevin.practice.profile.Profile profileData = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
        Material drop = event.getItem().getType();

        if (profileData.getState() == ProfileState.EDITING) {
            if (drop.getId() == 373) {
                event.setCancelled(true);
                player.updateInventory();
                player.sendMessage(CC.RED + "You cant do this while editing kit.");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractOnKitEditor(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        me.devkevin.practice.profile.Profile profileData = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        if (event.getAction().name().startsWith("RIGHT_")) {
            ItemStack item = event.getItem();

            if (profileData.getState() == ProfileState.EDITING) {
                if (item == null) {
                    return;
                }

                switch (item.getType()) {
                    case BOW:
                    case GOLDEN_APPLE:
                    case FISHING_ROD:
                    case ENDER_PEARL:
                        event.setCancelled(true);
                        player.sendMessage(CC.RED + "You cant do this while editing kit.");
                        break;
                    case POTION:
                        // don't let players throw pots while editing kit
                        Potion potion = Potion.fromItemStack(event.getItem());

                        if (potion.isSplash()) {
                            event.setCancelled(true);
                            player.updateInventory();
                            player.sendMessage(CC.RED + "You cant do this while editing kit.");
                        }
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onFlight(PlayerToggleFlightEvent event) {
        if (event.getPlayer().hasPotionEffect(PotionEffectType.WEAKNESS)) {
            event.setCancelled(true);
        }
    }
}
