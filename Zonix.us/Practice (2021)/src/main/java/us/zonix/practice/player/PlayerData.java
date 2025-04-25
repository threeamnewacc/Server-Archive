package us.zonix.practice.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import us.zonix.practice.Practice;
import us.zonix.practice.arena.Arena;
import us.zonix.practice.inventory.InventorySnapshot;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.kit.PlayerKit;
import us.zonix.practice.queue.QueueType;
import us.zonix.practice.settings.ProfileOptions;

public class PlayerData {
    public static final int DEFAULT_ELO = 1000;
    private final Map<String, Map<Integer, PlayerKit>> playerKits = new HashMap<>();
    private final Map<String, Integer> rankedLosses = new HashMap<>();
    private final Map<String, Integer> rankedWins = new HashMap<>();
    private final Map<String, Integer> rankedElo = new HashMap<>();
    private final Map<String, Integer> partyElo = new HashMap<>();
    private final UUID uniqueId;
    private PlayerState playerState = PlayerState.LOADING;
    private UUID currentMatchID;
    private InventorySnapshot lastSnapshot;
    private UUID duelSelecting;
    private ProfileOptions options = new ProfileOptions();
    private int eloRange = 50;
    private int pingRange = -1;
    private int teamID = -1;
    private int rematchID = -1;
    private int missedPots;
    private int longestCombo;
    private int combo;
    private int hits;
    private boolean bestOfThreeDuel;
    private Kit bestOfThreeKit;
    private QueueType bestOfThreeQueueType;
    private Arena bestOfThreeArena;
    private boolean leaving = false;
    private int oitcEventWins;
    private int sumoEventWins;
    private int waterDropEventWins;
    private int parkourEventWins;
    private int redroverEventWins;
    private int playedArcher;
    private int playedBard;
    private int playedRanked;
    private int playedUnranked;
    private int unrankedWins;
    private int currentCps;
    private int cps;

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
        return this.rankedElo.computeIfAbsent(kitName, k -> 1000);
    }

    public void setElo(String kitName, int elo) {
        this.rankedElo.put(kitName, elo);
    }

    public int getPartyElo(String kitName) {
        return this.partyElo.computeIfAbsent(kitName, k -> 1000);
    }

    public void incrementPlayedBard() {
        this.playedBard++;
    }

    public void incrementPlayedArcher() {
        this.playedBard++;
    }

    public void incrementRanked() {
        this.playedRanked++;
    }

    public void incrementUnranked() {
        this.playedUnranked++;
    }

    public void incrementUnrankedWins() {
        this.unrankedWins++;
    }

    public void setPartyElo(String kitName, int elo) {
        this.partyElo.put(kitName, elo);
    }

    public void addPlayerKit(int index, PlayerKit playerKit) {
        this.getPlayerKits(playerKit.getName()).put(index, playerKit);
    }

    public Map<Integer, PlayerKit> getPlayerKits(String kitName) {
        return this.playerKits.computeIfAbsent(kitName, k -> new HashMap<>());
    }

    public int getGlobalStats(String type) {
        int i = 0;
        int count = 0;

        for (Kit kit : Practice.getInstance().getKitManager().getKits()) {
            String var6 = type.toUpperCase();
            switch (var6) {
                case "ELO":
                    i += this.getElo(kit.getName());
                    break;
                case "WINS":
                    i += this.getWins(kit.getName());
                    break;
                case "LOSSES":
                    i += this.getLosses(kit.getName());
            }

            count++;
        }

        if (i == 0) {
            i = 0;
        }

        if (count == 0) {
            count = 1;
        }

        return type.toUpperCase().equalsIgnoreCase("ELO") ? Math.round((float)(i / count)) : i;
    }

    public Map<String, Map<Integer, PlayerKit>> getPlayerKits() {
        return this.playerKits;
    }

    public Map<String, Integer> getRankedLosses() {
        return this.rankedLosses;
    }

    public Map<String, Integer> getRankedWins() {
        return this.rankedWins;
    }

    public Map<String, Integer> getRankedElo() {
        return this.rankedElo;
    }

    public Map<String, Integer> getPartyElo() {
        return this.partyElo;
    }

    public int getPlayedArcher() {
        return this.playedArcher;
    }

    public int getPlayedBard() {
        return this.playedBard;
    }

    public int getPlayedRanked() {
        return this.playedRanked;
    }

    public int getPlayedUnranked() {
        return this.playedUnranked;
    }

    public int getUnrankedWins() {
        return this.unrankedWins;
    }

    public int getCurrentCps() {
        return this.currentCps;
    }

    public int getCps() {
        return this.cps;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public void setCurrentMatchID(UUID currentMatchID) {
        this.currentMatchID = currentMatchID;
    }

    public void setLastSnapshot(InventorySnapshot lastSnapshot) {
        this.lastSnapshot = lastSnapshot;
    }

    public void setDuelSelecting(UUID duelSelecting) {
        this.duelSelecting = duelSelecting;
    }

    public void setOptions(ProfileOptions options) {
        this.options = options;
    }

    public void setEloRange(int eloRange) {
        this.eloRange = eloRange;
    }

    public void setPingRange(int pingRange) {
        this.pingRange = pingRange;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    public void setRematchID(int rematchID) {
        this.rematchID = rematchID;
    }

    public void setMissedPots(int missedPots) {
        this.missedPots = missedPots;
    }

    public void setLongestCombo(int longestCombo) {
        this.longestCombo = longestCombo;
    }

    public void setCombo(int combo) {
        this.combo = combo;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public void setOitcEventWins(int oitcEventWins) {
        this.oitcEventWins = oitcEventWins;
    }

    public void setSumoEventWins(int sumoEventWins) {
        this.sumoEventWins = sumoEventWins;
    }

    public void setWaterDropEventWins(int waterDropEventWins) {
        this.waterDropEventWins = waterDropEventWins;
    }

    public void setParkourEventWins(int parkourEventWins) {
        this.parkourEventWins = parkourEventWins;
    }

    public void setRedroverEventWins(int redroverEventWins) {
        this.redroverEventWins = redroverEventWins;
    }

    public void setPlayedArcher(int playedArcher) {
        this.playedArcher = playedArcher;
    }

    public void setPlayedBard(int playedBard) {
        this.playedBard = playedBard;
    }

    public void setPlayedRanked(int playedRanked) {
        this.playedRanked = playedRanked;
    }

    public void setPlayedUnranked(int playedUnranked) {
        this.playedUnranked = playedUnranked;
    }

    public void setUnrankedWins(int unrankedWins) {
        this.unrankedWins = unrankedWins;
    }

    public void setCurrentCps(int currentCps) {
        this.currentCps = currentCps;
    }

    public void setCps(int cps) {
        this.cps = cps;
    }

    public PlayerData(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public PlayerState getPlayerState() {
        return this.playerState;
    }

    public UUID getCurrentMatchID() {
        return this.currentMatchID;
    }

    public InventorySnapshot getLastSnapshot() {
        return this.lastSnapshot;
    }

    public UUID getDuelSelecting() {
        return this.duelSelecting;
    }

    public ProfileOptions getOptions() {
        return this.options;
    }

    public int getEloRange() {
        return this.eloRange;
    }

    public int getPingRange() {
        return this.pingRange;
    }

    public int getTeamID() {
        return this.teamID;
    }

    public int getRematchID() {
        return this.rematchID;
    }

    public int getMissedPots() {
        return this.missedPots;
    }

    public int getLongestCombo() {
        return this.longestCombo;
    }

    public int getCombo() {
        return this.combo;
    }

    public int getHits() {
        return this.hits;
    }

    public boolean isBestOfThreeDuel() {
        return this.bestOfThreeDuel;
    }

    public void setBestOfThreeDuel(boolean bestOfThreeDuel) {
        this.bestOfThreeDuel = bestOfThreeDuel;
    }

    public Kit getBestOfThreeKit() {
        return this.bestOfThreeKit;
    }

    public void setBestOfThreeKit(Kit bestOfThreeKit) {
        this.bestOfThreeKit = bestOfThreeKit;
    }

    public QueueType getBestOfThreeQueueType() {
        return this.bestOfThreeQueueType;
    }

    public void setBestOfThreeQueueType(QueueType bestOfThreeQueueType) {
        this.bestOfThreeQueueType = bestOfThreeQueueType;
    }

    public Arena getBestOfThreeArena() {
        return this.bestOfThreeArena;
    }

    public void setBestOfThreeArena(Arena bestOfThreeArena) {
        this.bestOfThreeArena = bestOfThreeArena;
    }

    public boolean isLeaving() {
        return this.leaving;
    }

    public void setLeaving(boolean leaving) {
        this.leaving = leaving;
    }

    public int getOitcEventWins() {
        return this.oitcEventWins;
    }

    public int getSumoEventWins() {
        return this.sumoEventWins;
    }

    public int getWaterDropEventWins() {
        return this.waterDropEventWins;
    }

    public int getParkourEventWins() {
        return this.parkourEventWins;
    }

    public int getRedroverEventWins() {
        return this.redroverEventWins;
    }
}
