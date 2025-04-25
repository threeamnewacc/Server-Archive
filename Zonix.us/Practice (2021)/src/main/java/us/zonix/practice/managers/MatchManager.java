package us.zonix.practice.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import me.maiko.dexter.profile.Profile;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import us.zonix.practice.Practice;
import us.zonix.practice.arena.Arena;
import us.zonix.practice.event.match.MatchEndEvent;
import us.zonix.practice.event.match.MatchRestartEvent;
import us.zonix.practice.event.match.MatchStartEvent;
import us.zonix.practice.handler.CustomMovementHandler;
import us.zonix.practice.inventory.InventorySnapshot;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.kit.PlayerKit;
import us.zonix.practice.match.Match;
import us.zonix.practice.match.MatchRequest;
import us.zonix.practice.match.MatchState;
import us.zonix.practice.match.MatchTeam;
import us.zonix.practice.party.Party;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.queue.QueueType;
import us.zonix.practice.runnable.RematchRunnable;
import us.zonix.practice.util.ItemUtil;
import us.zonix.practice.util.PlayerUtil;
import us.zonix.practice.util.TtlHashMap;

public class MatchManager {
    private final Map<UUID, Set<MatchRequest>> matchRequests = new TtlHashMap<>(TimeUnit.SECONDS, 30L);
    private final Map<UUID, UUID> rematchUUIDs = new TtlHashMap<>(TimeUnit.SECONDS, 30L);
    private final Map<UUID, UUID> rematchInventories = new TtlHashMap<>(TimeUnit.SECONDS, 30L);
    private final Map<UUID, UUID> spectators = new ConcurrentHashMap<>();
    private final Map<UUID, Match> matches = new HashMap<>();
    private final Practice plugin = Practice.getInstance();

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
        return (int)this.matches
            .entrySet()
            .stream()
            .filter(match -> match.getValue().getType() == type)
            .filter(match -> match.getValue().getKit().getName().equals(ladder))
            .count();
    }

    public void createMatchRequest(Player requester, Player requested, Arena arena, String kitName, boolean party, boolean bestOfThree) {
        MatchRequest request = new MatchRequest(requester.getUniqueId(), requested.getUniqueId(), arena, kitName, party, bestOfThree);
        this.matchRequests.computeIfAbsent(requested.getUniqueId(), k -> new HashSet<>()).add(request);
    }

    public MatchRequest getMatchRequest(UUID requester, UUID requested) {
        Set<MatchRequest> requests = this.matchRequests.get(requested);
        return requests == null ? null : requests.stream().filter(req -> req.getRequester().equals(requester)).findAny().orElse(null);
    }

    public MatchRequest getMatchRequest(UUID requester, UUID requested, String kitName) {
        Set<MatchRequest> requests = this.matchRequests.get(requested);
        return requests == null
            ? null
            : requests.stream().filter(req -> req.getRequester().equals(requester) && req.getKitName().equals(kitName)).findAny().orElse(null);
    }

    public Match getMatch(PlayerData playerData) {
        return this.matches.get(playerData.getCurrentMatchID());
    }

    public Match getMatch(UUID uuid) {
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(uuid);
        return this.getMatch(playerData);
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

    public void removeFighter(Player player, PlayerData playerData, boolean spectateDeath) {
        Match match = this.matches.get(playerData.getCurrentMatchID());
        Player killer = player.getKiller();
        MatchTeam entityTeam = match.getTeams().get(playerData.getTeamID());
        MatchTeam winningTeam = match.isFFA() ? entityTeam : match.getTeams().get(entityTeam.getTeamID() == 0 ? 1 : 0);
        if (match.getMatchState() != MatchState.ENDING && (!spectateDeath || match.getMatchState() != MatchState.RESTARTING)) {
            String deathMessage = ChatColor.GOLD.toString()
                + ChatColor.BOLD
                + "✪ "
                + ChatColor.RED
                + player.getName()
                + ChatColor.GRAY
                + " has "
                + (match.isBestOfThree() ? "lost this round" : "been eliminated")
                + (
                    killer == null
                        ? "."
                        : (match.isBestOfThree() ? ". Eliminated by " + ChatColor.GREEN + killer.getName() : " by " + ChatColor.GREEN + killer.getName())
                );
            match.broadcast(deathMessage);
            if (match.isRedrover()) {
                if (match.getMatchState() != MatchState.SWITCHING) {
                    match.setMatchState(MatchState.SWITCHING);
                    match.setCountdown(4);
                }
            } else {
                match.addSnapshot(player);
            }

            entityTeam.killPlayer(player.getUniqueId());
            int remaining = entityTeam.getAlivePlayers().size();
            if (remaining != 0) {
                Set<Item> items = new HashSet<>();

                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.getType() != Material.AIR) {
                        items.add(player.getWorld().dropItemNaturally(player.getLocation(), item));
                    }
                }

                for (ItemStack itemx : player.getInventory().getArmorContents()) {
                    if (itemx != null && itemx.getType() != Material.AIR) {
                        items.add(player.getWorld().dropItemNaturally(player.getLocation(), itemx));
                    }
                }

                this.plugin.getMatchManager().addDroppedItems(match, items);
            }

            if (spectateDeath) {
                this.addDeathSpectator(player, playerData, match);
            }

            int remainingWinning = winningTeam.onlinePlayers();
            int remainingLoosing = entityTeam.onlinePlayers();
            if (remainingLoosing == 0) {
                this.plugin.getServer().getPluginManager().callEvent(new MatchEndEvent(match, winningTeam, entityTeam));
            } else if (remainingWinning == 0) {
                this.plugin.getServer().getPluginManager().callEvent(new MatchEndEvent(match, entityTeam, winningTeam));
            } else {
                if (match.isFFA() && remaining == 1 || remaining == 0) {
                    if (!match.isBestOfThree()) {
                        this.plugin.getServer().getPluginManager().callEvent(new MatchEndEvent(match, winningTeam, entityTeam));
                        return;
                    }

                    match.setRounds(match.getRounds() + 1);
                    winningTeam.setMatchWins(winningTeam.getMatchWins() + 1);
                    if (winningTeam.getMatchWins() == 3) {
                        this.plugin.getServer().getPluginManager().callEvent(new MatchEndEvent(match, winningTeam, entityTeam));
                        return;
                    }

                    this.plugin.getServer().getPluginManager().callEvent(new MatchRestartEvent(match, winningTeam, entityTeam));
                }
            }
        }
    }

    public void removeMatch(Match match) {
        this.matches.remove(match.getMatchId());
        CustomMovementHandler.getParkourCheckpoints().remove(match);
    }

    public void giveKits(Player player, Kit kit) {
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        Collection<PlayerKit> playerKits = playerData.getPlayerKits(kit.getName()).values();
        if (kit.isHcteams()) {
            Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
            if (party != null) {
                if (party.getBards().contains(player.getUniqueId())) {
                    this.plugin.getKitManager().getKit("HCBard").applyToPlayer(player);
                } else if (party.getArchers().contains(player.getUniqueId())) {
                    this.plugin.getKitManager().getKit("HCArcher").applyToPlayer(player);
                } else {
                    this.plugin.getKitManager().getKit("NoDebuff").applyToPlayer(player);
                }
            }
        } else {
            if (playerKits.size() == 0) {
                kit.applyToPlayer(player);
            } else {
                player.getInventory().setItem(8, this.plugin.getItemManager().getDefaultBook());
                int slot = -1;

                for (PlayerKit playerKit : playerKits) {
                    player.getInventory()
                        .setItem(
                            ++slot, ItemUtil.createItem(Material.ENCHANTED_BOOK, ChatColor.YELLOW.toString() + ChatColor.BOLD + playerKit.getDisplayName())
                        );
                }

                player.updateInventory();
            }
        }
    }

    private void addDeathSpectator(Player player, PlayerData playerData, Match match) {
        this.spectators.put(player.getUniqueId(), match.getMatchId());
        playerData.setPlayerState(PlayerState.SPECTATING);
        PlayerUtil.clearPlayer(player);
        CraftPlayer playerCp = (CraftPlayer)player;
        EntityPlayer playerEp = playerCp.getHandle();
        playerEp.getDataWatcher().watch(6, 0.0F);
        playerEp.setFakingDeath(true);
        match.addSpectator(player.getUniqueId());
        match.getTeams().forEach(teamx -> teamx.alivePlayers().forEach(member -> member.hidePlayer(player)));
        match.spectatorPlayers().forEach(member -> member.hidePlayer(player));
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        player.setWalkSpeed(0.2F);
        player.setAllowFlight(true);
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
        if (match.isParty() || match.isFFA()) {
            this.plugin
                .getServer()
                .getScheduler()
                .runTaskLater(this.plugin, () -> player.getInventory().setContents(this.plugin.getItemManager().getPartySpecItems()), 1L);
        }

        player.updateInventory();
    }

    public void addRedroverSpectator(Player player, Match match) {
        this.spectators.put(player.getUniqueId(), match.getMatchId());
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().setContents(this.plugin.getItemManager().getPartySpecItems());
        player.updateInventory();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        playerData.setPlayerState(PlayerState.SPECTATING);
    }

    public void addSpectator(Player player, PlayerData playerData, Player target, Match targetMatch) {
        this.spectators.put(player.getUniqueId(), targetMatch.getMatchId());
        if (targetMatch.getMatchState() != MatchState.ENDING && !targetMatch.haveSpectated(player.getUniqueId())) {
            String spectatorName = Profile.getByUuidIfAvailable(player.getUniqueId()).getRank().getGameColor() + player.getName();
            String spectatorMessage = ChatColor.WHITE.toString()
                + ChatColor.BOLD
                + "* "
                + ChatColor.GREEN
                + spectatorName
                + ChatColor.GRAY
                + " is spectating your match.";
            if (!player.hasMetadata("modmode")) {
                targetMatch.broadcast(spectatorMessage);
            }
        }

        targetMatch.addSpectator(player.getUniqueId());
        playerData.setPlayerState(PlayerState.SPECTATING);
        player.teleport(target);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().setContents(this.plugin.getItemManager().getSpecItems());
        player.updateInventory();
        this.plugin.getServer().getOnlinePlayers().forEach(online -> {
            online.hidePlayer(player);
            player.hidePlayer(online);
        });
        targetMatch.getTeams().forEach(team -> team.alivePlayers().forEach(player::showPlayer));
    }

    public void addDroppedItem(Match match, Item item) {
        match.addEntityToRemove(item);
        match.addRunnable(this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            match.removeEntityToRemove(item);
            item.remove();
        }, 100L).getTaskId());
    }

    public void addDroppedItems(Match match, Set<Item> items) {
        for (Item item : items) {
            match.addEntityToRemove(item);
        }

        match.addRunnable(this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            for (Item itemx : items) {
                match.removeEntityToRemove(itemx);
                itemx.remove();
            }
        }, 100L).getTaskId());
    }

    public void removeDeathSpectator(Match match, Player player) {
        match.removeSpectator(player.getUniqueId());
        this.spectators.remove(player.getUniqueId());
    }

    public void removeSpectator(Player player) {
        Match match = this.matches.get(this.spectators.get(player.getUniqueId()));
        match.removeSpectator(player.getUniqueId());
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (match.getTeams().size() > playerData.getTeamID() && playerData.getTeamID() >= 0) {
            MatchTeam entityTeam = match.getTeams().get(playerData.getTeamID());
            if (entityTeam != null) {
                entityTeam.killPlayer(player.getUniqueId());
            }
        }

        if (match.getMatchState() != MatchState.ENDING && !match.haveSpectated(player.getUniqueId())) {
            match.addHaveSpectated(player.getUniqueId());
        }

        this.spectators.remove(player.getUniqueId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
    }

    public void pickPlayer(Match match) {
        Player playerA = this.plugin.getServer().getPlayer(match.getTeams().get(0).getAlivePlayers().get(0));
        PlayerData playerDataA = this.plugin.getPlayerManager().getPlayerData(playerA.getUniqueId());
        if (playerDataA.getPlayerState() != PlayerState.FIGHTING) {
            playerA.teleport(match.getArena().getA().toBukkitLocation());
            PlayerUtil.clearPlayer(playerA);
            if (match.getKit().isCombo()) {
                playerA.setMaximumNoDamageTicks(4);
            }

            this.plugin.getMatchManager().giveKits(playerA, match.getKit());
            playerDataA.setPlayerState(PlayerState.FIGHTING);
        }

        Player playerB = this.plugin.getServer().getPlayer(match.getTeams().get(1).getAlivePlayers().get(0));
        PlayerData playerDataB = this.plugin.getPlayerManager().getPlayerData(playerB.getUniqueId());
        if (playerDataB.getPlayerState() != PlayerState.FIGHTING) {
            playerB.teleport(match.getArena().getB().toBukkitLocation());
            PlayerUtil.clearPlayer(playerB);
            if (match.getKit().isCombo()) {
                playerB.setMaximumNoDamageTicks(4);
            }

            this.plugin.getMatchManager().giveKits(playerB, match.getKit());
            playerDataB.setPlayerState(PlayerState.FIGHTING);
        }

        for (MatchTeam team : match.getTeams()) {
            for (UUID uuid : team.getAlivePlayers()) {
                Player player = this.plugin.getServer().getPlayer(uuid);
                if (player != null && !playerA.equals(player) && !playerB.equals(player)) {
                    playerA.hidePlayer(player);
                    playerB.hidePlayer(player);
                }
            }
        }

        playerA.showPlayer(playerB);
        playerB.showPlayer(playerA);
        match.broadcast(
            ChatColor.YELLOW
                + "Starting duel. "
                + ChatColor.GREEN
                + "("
                + playerA.getName()
                + " vs "
                + playerB.getName()
                + ") in "
                + match.getArena().getName()
        );
    }

    public void saveRematches(Match match) {
        if (!match.isParty() && !match.isFFA()) {
            UUID playerOne = match.getTeams().get(0).getLeader();
            UUID playerTwo = match.getTeams().get(1).getLeader();
            PlayerData dataOne = this.plugin.getPlayerManager().getPlayerData(playerOne);
            PlayerData dataTwo = this.plugin.getPlayerManager().getPlayerData(playerTwo);
            if (dataOne != null) {
                this.rematchUUIDs.put(playerOne, playerTwo);
                InventorySnapshot snapshot = match.getSnapshot(playerTwo);
                if (snapshot != null) {
                    dataOne.setLastSnapshot(snapshot);
                    this.rematchInventories.put(playerOne, snapshot.getSnapshotId());
                }

                if (dataOne.getRematchID() > -1) {
                    this.plugin.getServer().getScheduler().cancelTask(dataOne.getRematchID());
                }

                dataOne.setRematchID(this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new RematchRunnable(playerOne), 600L));
            }

            if (dataTwo != null) {
                this.rematchUUIDs.put(playerTwo, playerOne);
                InventorySnapshot snapshotx = match.getSnapshot(playerOne);
                if (snapshotx != null) {
                    dataTwo.setLastSnapshot(snapshotx);
                    this.rematchInventories.put(playerTwo, snapshotx.getSnapshotId());
                }

                if (dataTwo.getRematchID() > -1) {
                    this.plugin.getServer().getScheduler().cancelTask(dataTwo.getRematchID());
                }

                dataTwo.setRematchID(this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new RematchRunnable(playerTwo), 600L));
            }
        }
    }

    public void removeRematch(UUID uuid) {
        this.rematchUUIDs.remove(uuid);
        this.rematchInventories.remove(uuid);
    }

    public List<UUID> getOpponents(Match match, Player player) {
        for (MatchTeam team : match.getTeams()) {
            if (!team.getPlayers().contains(player.getUniqueId())) {
                return team.getPlayers();
            }
        }

        return null;
    }

    public UUID getRematcher(UUID uuid) {
        return this.rematchUUIDs.get(uuid);
    }

    public UUID getRematcherInventory(UUID uuid) {
        return this.rematchInventories.get(uuid);
    }

    public boolean isRematching(UUID uuid) {
        return this.rematchUUIDs.containsKey(uuid);
    }

    public Map<UUID, Match> getMatches() {
        return this.matches;
    }
}
