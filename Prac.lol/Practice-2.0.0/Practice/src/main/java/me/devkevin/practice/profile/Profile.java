package me.devkevin.practice.profile;

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.kit.PlayerKit;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.menu.MatchDetailsMenu;
import me.devkevin.practice.options.ProfileOptions;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.util.Cooldown;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright 10/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
@Setter
@RequiredArgsConstructor
public class Profile {

    public static final int DEFAULT_ELO = 1000;
    public static Map<UUID, Profile> players = Maps.newHashMap();

    /*
     * The maps don't need getters as they are never accessed directly.
     */
    private final Map<String, Map<Integer, PlayerKit>> playerKits = new HashMap<>();
    private final Map<String, PlayerKit[]> kits = new ConcurrentHashMap<>();
    private final Map<String, Integer> rankedLosses = new HashMap<>();
    private final Map<String, Integer> rankedWins = new HashMap<>();
    private final Map<String, Integer> rankedElo = new HashMap<>();
    private final Map<String, Integer> partyElo = new HashMap<>();
    private final Map<String, Integer> currentWinstreak = new HashMap<>();
    private final Map<String, Integer> highestWinStreak = new HashMap<>();

    private Map<Player, Player> cachedPlayer = new ConcurrentHashMap<>();

    @Getter private MatchDetailsMenu lastSnapshot;

    @SerializedName("uuid")
    private final UUID uuid;
    public String name;
    private ProfileState state = ProfileState.LOADING;
    private ProfileOptions options = new ProfileOptions();

    private List<Location> potions = new ArrayList<>();
    private List<Packet> packets = new ArrayList<>();

    private UUID currentMatchID;
    private UUID duelSelecting;

    private int teamID = -1;
    private int rematchID = -1;
    private int eloRange = 250;
    private int pingRange = 50;

    public int unrankedWins;
    public int unrankedLosses;

    private int experience;

    private int oitcKills;
    private int oitcDeaths;
    private int oitcWins;
    private int oitcLosses;

    private int sumoWins;
    private int sumoLosses;

    private int tntrunEventWins;
    private int tntrunEventLosses;

    private boolean disconnected = false;
    private boolean npcsLoaded = true;
    private boolean selectClasses = true;
    private int elo, potionsThrown, potionsMissed, hits, combo, longestCombo;
    private long lastDamagedMillis = 0;
    private double wastedHP;

    private int wTapHits;
    private int criticalHits;
    private int blockedHits;

    private long lastRespawnTime;

    private boolean respawning;
    private boolean hasRespawned;

    private int currentCps;
    private int cps;

    private int playedArcher, playedBard;

    private boolean canRate = false;

    private int matchesPlayed;
    private int globalElo = Profile.DEFAULT_ELO;
    private int uDropElo = Profile.DEFAULT_ELO;

    private Cooldown playerCommandCooldown = new Cooldown(0);

    private UUID followingId;
    private boolean following;

    private boolean dataLoaded;

    private int globalWinStreak;
    private int globalHighestWinStreak;

    private boolean leaving = false;

    private Arena lastArenaPlayed;

    private Match match;

    public int getMaxCustomKits() {
        return 4;
    }

    public void addPlayerKit(int index, PlayerKit playerKit) {
        this.getPlayerKits(playerKit.getName()).put(index, playerKit);
    }

    public Map<Integer, PlayerKit> getPlayerKits(String kitName) {
        return this.playerKits.computeIfAbsent(kitName, k -> new HashMap<>());
    }

    public double getPotionAccuracy() {
        if (potionsMissed == 0) {
            return 100.0;
        } else if (potionsThrown == potionsMissed) {
            return 50.0;
        }
        return Math.round(100.0D - (((double) potionsMissed / (double) potionsThrown) * 100.0D));
    }

    public void incrementPotionsThrown() {
        this.potionsThrown++;
    }

    public void incrementPotionsMissed() {
        this.potionsMissed++;
    }

    public void incrementPlayedBard() {
        this.playedBard++;
    }

    public void incrementPlayedArcher() {
        this.playedBard++;
    }

    public void incrementCriticalHits() {
        criticalHits++;
    }

    public void incrementBlockedHits() {
        blockedHits++;
    }


    public int getWins(String kitName) {
        return this.rankedWins.computeIfAbsent(kitName, k -> 0);
    }

    public void setWins(String kitName, int wins) {
        this.rankedWins.put(kitName, wins);
    }

    public int getLosses(String kitName) {
        return this.rankedLosses.computeIfAbsent(kitName, k -> 0);
    }

    public void setLosses(String kitName, int losses) {
        this.rankedLosses.put(kitName, losses);
    }

    public int getElo(String kitName) {
        return this.rankedElo.computeIfAbsent(kitName, k -> Profile.DEFAULT_ELO);
    }

    public void setElo(String kitName, int elo) {
        this.rankedElo.put(kitName, elo);
    }

    public int getPartyElo(String kitName) {
        return this.partyElo.computeIfAbsent(kitName, k -> Profile.DEFAULT_ELO);
    }

    public void setPartyElo(String kitName, int elo) {
        this.partyElo.put(kitName, elo);
    }

    public boolean isInSpawn() { return state == ProfileState.SPAWN; }
    public boolean isInQueue() { return state == ProfileState.QUEUE; }
    public boolean isFighting() { return state == ProfileState.FIGHTING; }
    public boolean isInParty() { return state == ProfileState.PARTY; }
    public boolean isSpectating() { return state == ProfileState.SPECTATING; }
    public boolean isEditing() { return state == ProfileState.EDITING; }
    public boolean isInStaffMode() { return state == ProfileState.STAFF; }
    public boolean isInEvent() { return state == ProfileState.EVENT; }
    public boolean isInSoloUnranked() { return state == ProfileState.SoloUnranked; }
    public boolean isInSoloRanked() { return state == ProfileState.RankedSolo; }
    public boolean isBusy() { return this.isInQueue() || this.isFighting() || this.isEditing(); }
    public boolean isBotMatch() { return state == ProfileState.BOT_FIGHTING; }

    public void setCurrentWinstreak(String kitName, int streak) {
        this.currentWinstreak.put(kitName, streak);
    }

    public int getCurrentWinstreak(String kitName) {
        return this.currentWinstreak.computeIfAbsent(kitName, k -> 0);
    }

    public void setHighestWinStreak(String kitName, int streak) {
        this.highestWinStreak.put(kitName, streak);
    }

    public int getHighestWinStreak(String kitName) {
        return this.highestWinStreak.computeIfAbsent(kitName, k -> 0);
    }

    public void setHits(int hits) {
        if (System.currentTimeMillis() - lastDamagedMillis < 250) {
            return;
        }

        this.hits = hits;
        this.lastDamagedMillis = System.currentTimeMillis();
    }

    public int getGlobalWins() {
        final AtomicInteger integer = new AtomicInteger();
        final List<Kit> kits = new ArrayList<>(Practice.getInstance().getKitManager().getKits());

        kits.forEach(kit -> {
            integer.addAndGet(getWins(kit.getName()));
        });

        return integer.get();
    }

    public int getGlobalLosses() {
        final AtomicInteger integer = new AtomicInteger();
        final List<Kit> kits = new ArrayList<>(Practice.getInstance().getKitManager().getKits());

        kits.forEach(kit -> {
            integer.addAndGet(this.getLosses(kit.getName()));
        });

        return integer.get();
    }
}
