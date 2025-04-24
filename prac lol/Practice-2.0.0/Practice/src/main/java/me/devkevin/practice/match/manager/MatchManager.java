package me.devkevin.practice.match.manager;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.hcf.kit.HCFKit;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.kit.PlayerKit;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchRequest;
import me.devkevin.practice.match.MatchState;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.match.event.impl.MatchEndEvent;
import me.devkevin.practice.match.event.impl.MatchStartEvent;
import me.devkevin.practice.match.menu.MatchDetailsMenu;
import me.devkevin.practice.match.task.RematchRunnable;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.queue.QueueType;
import me.devkevin.practice.util.Clickable;
import me.devkevin.practice.util.ItemUtil;
import me.devkevin.practice.util.PlayerUtil;
import me.devkevin.practice.util.TtlHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 28/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class MatchManager {

    private final Practice plugin = Practice.getInstance();

    private final Map<UUID, Set<MatchRequest>> matchRequests = new TtlHashMap<>(TimeUnit.SECONDS, 30);
    private final Map<UUID, UUID> rematchUUIDs = new TtlHashMap<>(TimeUnit.SECONDS, 30);
    private final Map<UUID, UUID> rematchInventories = new TtlHashMap<>(TimeUnit.SECONDS, 30);
    private final Map<UUID, UUID> spectators = new ConcurrentHashMap<>();

    private final Map<UUID, Match> matches = new HashMap<>();

    private final Map<UUID, Kit> playAgainKit = new TtlHashMap<>(TimeUnit.SECONDS, 30);

    public int getFighters() {
        int i = 0;
        for (Match match : this.matches.values()) {
            for (MatchTeam matchTeam : match.getTeams()) {
                i += matchTeam.getAlivePlayers().size();
            }
        }

        return i;
    }

    public int getFighters(String ladder, QueueType type) {
        int i = 0;
        for (Match match : this.matches.values()) {
            if (match.getKit().getName().equalsIgnoreCase(ladder)) {
                if (match.getType() == type) {
                    for (MatchTeam matchTeam : match.getTeams()) {
                        i += matchTeam.getAlivePlayers().size();
                    }
                }
            }
        }

        return i;
    }

    public int getFighters(QueueType type) {
        int i = 0;
        for (Match match : this.matches.values()) {
            if (match.getType() == type) {
                for (MatchTeam team : match.getTeams()) {
                    i += team.getAlivePlayers().size();
                }
            }
        }

        return i;
    }

    public Map<UUID, Match> getMatches() {
        return matches;
    }

    public void createMatchRequest(Player requester, Player requested, Arena arena, String kitName, boolean party) {
        MatchRequest request = new MatchRequest(requester.getUniqueId(), requested.getUniqueId(), arena, kitName, party);
        this.matchRequests.computeIfAbsent(requested.getUniqueId(), k -> new HashSet<>()).add(request);
    }

    public MatchRequest getMatchRequest(UUID requester, UUID requested) {
        Set<MatchRequest> requests = this.matchRequests.get(requested);
        if (requests == null) {
            return null;
        }

        return requests.stream().filter(req -> req.getRequester().equals(requester)).findAny().orElse(null);
    }

    public MatchRequest getMatchRequest(UUID requester, UUID requested, String kitName) {
        Set<MatchRequest> requests = this.matchRequests.get(requested);
        if (requests == null) {
            return null;
        }

        return requests.stream().filter(req -> req.getRequester().equals(requester) && req.getKitName().equals(kitName)).findAny().orElse(null);
    }

    public Match getMatch(Profile profile) {
        return this.matches.get(profile.getCurrentMatchID());
    }

    public Match getMatch(UUID uuid) {
        Profile profile = this.plugin.getProfileManager().getProfileData(uuid);
        return this.getMatch(profile);
    }

    public void removeMatch(Match match) {
        this.matches.remove(match.getMatchId());
    }

    public Match getMatchFromUUID(UUID uuid) {
        return this.matches.get(uuid);
    }

    public Match getSpectatingMatch(UUID uuid) {
        return this.matches.get(this.spectators.get(uuid));
    }

    public void removeMatchRequests(UUID uuid) {
        this.matchRequests.remove(uuid);
    }

    public void createMatch(Match match) {
        this.matches.put(match.getMatchId(), match);
        this.plugin.getServer().getPluginManager().callEvent(new MatchStartEvent(match));
    }

    public void removeSpectator(Player player) {
        Match match = this.matches.get(this.spectators.get(player.getUniqueId()));

        match.removeSpectator(player.getUniqueId());

        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        if (match.getTeams().size() > profile.getTeamID() && profile.getTeamID() >= 0) {
            MatchTeam entityTeam = match.getTeams().get(profile.getTeamID());
            //Kill the player if they are in a redrover.
            if (entityTeam != null) {
                entityTeam.killPlayer(player.getUniqueId());
            }
        }

        if (match.getMatchState() != MatchState.ENDING) {
            CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

            if (coreProfile != null) {
                if (!coreProfile.hasStaff()) {
                    if (!match.haveSpectated(player.getUniqueId())) {
                        match.broadcastMessage(coreProfile.getGrant().getRank().getColor() + player.getName() + CC.RED + " is no longer spectating.");
                        match.addHaveSpectated(player.getUniqueId());
                    }
                }
            }
        }

        this.spectators.remove(player.getUniqueId());
        this.plugin.getProfileManager().sendToSpawn(player);
    }

    public void giveKits(Player player, Kit kit) {
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
        Collection<PlayerKit> playerKits = profile.getPlayerKits(kit.getName()).values();

        if (kit.getName().equalsIgnoreCase("HCF")) {
            UUID uniqueId = player.getUniqueId();
            Party party = this.plugin.getPartyManager().getParty(uniqueId);
            HCFKit clazz = new HCFKit();
            if (party.getBards().contains(uniqueId)) {
                clazz.giveBardKit(player);
            } else if (party.getArchers().contains(uniqueId)) {
                clazz.giveArcherKit(player);
            } else {
                this.plugin.getKitManager().getKit("HCF").applyToPlayer(player);
            }

            return;
        }

        if (kit.isBoxing()) {

            CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

            if (coreProfile.hasDonor()) {
                player.getInventory().setContents(this.plugin.getHotbarItem().getBoxingSwordDonor());
            } else {
                player.getInventory().setContents(this.plugin.getHotbarItem().getBoxingSword());
            }
            return;
        }

        if (playerKits.size() == 0) {
            kit.applyToPlayer(player);
        } else {
            player.getInventory().setItem(8, this.plugin.getHotbarItem().getDEFAULT_KIT());
            int slot = -1;
            for (PlayerKit playerKit : playerKits) {
                player.getInventory().setItem(++slot,
                        ItemUtil.createItem(Material.ENCHANTED_BOOK, ChatColor.YELLOW.toString() + ChatColor.BOLD + playerKit.getDisplayName()));
            }
            player.updateInventory();
        }
    }

    public void removeFighter(Player player, Profile profile, boolean spectateDeath) {
        Match match = this.matches.get(profile.getCurrentMatchID());

        Player killer = player.getKiller();

        if (player.isOnline() && killer != null) {
            killer.hidePlayer(player);
        }

        //PlayerUtil.playDeathAnimation(player);

        MatchTeam entityTeam = match.getTeams().get(profile.getTeamID());
        MatchTeam winningTeam = match.isFFA() ? entityTeam : match.getTeams().get(entityTeam.getTeamID() == 0 ? 1 : 0);
        if (match.getMatchState() == MatchState.ENDING) {
            return;
        }

        if (killer != null) {
            match.broadcastMessage(CC.GOLD + player.getName() + CC.YELLOW + " was slain by " + CC.GOLD + killer.getName() + CC.YELLOW + "!");
        } else {
            match.broadcastMessage(CC.GOLD + player.getName() + CC.YELLOW + " has disconnected.");
        }

        match.broadcastMessage("");

        match.addSnapshot(player);
        entityTeam.killPlayer(player.getUniqueId());

        int remaining = entityTeam.getAlivePlayers().size();
        if (remaining != 0) {
            Set<Item> items = new HashSet<>();
            for (ItemStack inventory : player.getInventory().getContents()) {
                if (inventory != null && inventory.getType() != Material.AIR) {
                    items.add(player.getWorld().dropItemNaturally(player.getLocation(), inventory));
                }
            }
            for (ItemStack armor : player.getInventory().getArmorContents()) {
                if (armor != null && armor.getType() != Material.AIR) {
                    items.add(player.getWorld().dropItemNaturally(player.getLocation(), armor));
                }
            }

            plugin.getMatchManager().addDroppedItems(match, items);
        }

        if (spectateDeath) {
            this.addDeathSpectator(player, profile, match);
        }

        if (match.isFFA() && remaining == 1 || match.isFFA() && remaining == 0 || remaining == 0) {
            this.plugin.getServer().getPluginManager().callEvent(new MatchEndEvent(match, winningTeam, entityTeam));
        }
    }

    private void addDeathSpectator(Player player, Profile profile, Match match) {
        this.spectators.put(player.getUniqueId(), match.getMatchId());

        profile.setState(ProfileState.SPECTATING);

        PlayerUtil.reset(player);

        //playerEp.getDataWatcher().watch(6, 0.0F); // Spawn a player in a STANDING position. This packet is broken as fuck, dont use

        match.addSpectator(player.getUniqueId());
        match.addRunnable(this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            match.getTeams().forEach(team -> team.alivePlayers().forEach(member -> member.hidePlayer(player)));

            match.spectatorPlayers().forEach(member -> member.hidePlayer(player));

            player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
            player.setWalkSpeed(0.2F);
            player.setFlySpeed(0.4F);
            player.setAllowFlight(true);
        }, 20L));

        if (match.isRedrover()) {
            for (MatchTeam team : match.getTeams()) {
                for (UUID alivePlayerUUID : team.getAlivePlayers()) {
                    Player alivePlayer = this.plugin.getServer().getPlayer(alivePlayerUUID);

                    if (alivePlayer != null) {
                        player.showPlayer(alivePlayer);
                    }
                }
            }
        }

        player.setWalkSpeed(0.0F);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000, -5));

        if (match.isParty() || match.isFFA()) {
            player.getInventory().clear();
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () ->
                    player.getInventory().setContents(this.plugin.getHotbarItem().getSpecPartyItems()), 1L);
        }

        player.updateInventory();
    }

    public void addDroppedItem(Match match, Item item) {
        match.addEntityToRemove(item);
        match.addRunnable(plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            match.removeEntityToRemove(item);
            item.remove();
        }, 100L).getTaskId());
    }

    public void addDroppedItems(Match match, Set<Item> items) {
        for (Item item : items) {
            match.addEntityToRemove(item);
        }

        match.addRunnable(plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (Item item : items) {
                match.removeEntityToRemove(item);
                item.remove();
            }
        }, 100L).getTaskId());
    }

    public void addRedroverSpectator(Player player, Match match) {
        this.spectators.put(player.getUniqueId(), match.getMatchId());

        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().setContents(this.plugin.getHotbarItem().getSpecPartyItems());
        player.updateInventory();

        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        profile.setState(ProfileState.SPECTATING);
    }

    public void addSpectator(Player player, Profile profile, Player target, Match targetMatch) {
        this.spectators.put(player.getUniqueId(), targetMatch.getMatchId());

        if (targetMatch.getMatchState() != MatchState.ENDING) {

            CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

            if (!coreProfile.hasStaff()) {
                if (!targetMatch.haveSpectated(player.getUniqueId())) {

                    String spectatorMessage = coreProfile.getGrant().getRank().getColor() + player.getName() + ChatColor.YELLOW + " is spectating your match.";

                    targetMatch.broadcastMessage(spectatorMessage);
                }
            }
        }

        targetMatch.addSpectator(player.getUniqueId());

        profile.setState(ProfileState.SPECTATING);

        player.teleport(target);
        player.setAllowFlight(true);
        player.setFlying(true);

        player.getInventory().clear();
        player.getInventory().setContents(this.plugin.getHotbarItem().getSpecItems());
        player.updateInventory();

        plugin.getServer().getOnlinePlayers().forEach(online -> {
            PlayerUtil.hideOrShowPlayer(player, online, true);
            PlayerUtil.hideOrShowPlayer(online, player, true);
        });

        targetMatch.getTeams().forEach(team -> team.alivePlayers().forEach(player::showPlayer));
    }

    public void saveRematches(Match match) {
        if (match.isParty() || match.isFFA()) {
            return;
        }
        UUID playerOne = match.getTeams().get(0).getLeader();
        UUID playerTwo = match.getTeams().get(1).getLeader();

        Profile dataOne = this.plugin.getProfileManager().getProfileData(playerOne);
        Profile dataTwo = this.plugin.getProfileManager().getProfileData(playerTwo);

        if (dataOne != null) {
            this.rematchUUIDs.put(playerOne, playerTwo);
            MatchDetailsMenu snapshot = match.getSnapshot(playerTwo);
            if (snapshot != null) {
                dataOne.setLastSnapshot(snapshot);
                this.rematchInventories.put(playerOne, snapshot.getSnapshotId());
            }
            if (dataOne.getRematchID() > -1) {
                this.plugin.getServer().getScheduler().cancelTask(dataOne.getRematchID());
            }
            dataOne.setRematchID(
                    this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new RematchRunnable(playerOne), 20L * 30L));
        }
        if (dataTwo != null) {
            this.rematchUUIDs.put(playerTwo, playerOne);
            MatchDetailsMenu snapshot = match.getSnapshot(playerOne);
            if (snapshot != null) {
                dataTwo.setLastSnapshot(snapshot);
                this.rematchInventories.put(playerTwo, snapshot.getSnapshotId());
            }
            if (dataTwo.getRematchID() > -1) {
                this.plugin.getServer().getScheduler().cancelTask(dataTwo.getRematchID());
            }
            dataTwo.setRematchID(
                    this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new RematchRunnable(playerTwo), 20L * 30L));
        }
    }

    public void removeRematch(UUID uuid) {
        this.rematchUUIDs.remove(uuid);
        this.rematchInventories.remove(uuid);
    }

    public UUID getRematcher(UUID uuid) {
        return this.rematchUUIDs.get(uuid);
    }

    public UUID getRematcherInventory(UUID uuid) {
        return this.rematchInventories.get(uuid);
    }

    public boolean  isRematching(UUID uuid) {
        return this.rematchUUIDs.containsKey(uuid);
    }

    public void processRequeue(Player player, Match match) {
        if (match.isParty() || match.isFFA() || match.isPartyMatch() || plugin.getTournamentManager().isInTournament(player.getUniqueId()) || plugin.getPartyManager().getParty(player.getUniqueId()) != null) {
            return;
        }

        Profile profile = plugin.getProfileManager().getProfileData(player.getUniqueId());
        this.playAgainKit.put(profile.getUuid(), match.getKit());

        Clickable clickable = new Clickable(CC.YELLOW + CC.BOLD + "want to play again? " + CC.GREEN + "(Click here)",
                CC.GRAY + "Click to play again",
                "/playagain");

        clickable.sendToPlayer(player);

        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            if (player.isOnline()) {
                player.getInventory().setItem(3, plugin.getHotbarItem().getPlayAgain());
            }
        }, 10L);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (profile.getState() == ProfileState.SPAWN && hasPlayAgainRequest(player.getUniqueId()) && plugin.getPartyManager().getParty(player.getUniqueId()) == null) {
                player.getInventory().setItem(3, new ItemStack(Material.AIR));
                player.updateInventory();
            }
        }, 20L * 30L);
    }

    public void removePlayAgainRequest(UUID uuid) {
        this.playAgainKit.remove(uuid);
    }

    public Kit getPlayAgainRequestKit(UUID uuid) {
        return this.playAgainKit.getOrDefault(uuid, null);
    }

    public boolean hasPlayAgainRequest(UUID uuid) {
        return this.playAgainKit.containsKey(uuid);
    }
}
