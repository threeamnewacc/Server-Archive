// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.player;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import club.minemen.practice.Practice;
import java.util.UUID;
import club.minemen.practice.kit.PlayerKit;
import java.util.Map;

public class PlayerData
{
    public static final int DEFAULT_ELO = 1000;
    private final Map<String, Map<Integer, PlayerKit>> playerKits;
    private final Map<String, Integer> rankedLosses;
    private final Map<String, Integer> rankedWins;
    private final Map<String, Integer> rankedElo;
    private final Map<String, Integer> partyElo;
    private final UUID uniqueId;
    private PlayerState playerState;
    private UUID currentMatchID;
    private UUID duelSelecting;
    private boolean acceptingDuels;
    private boolean allowingSpectators;
    private boolean scoreboardEnabled;
    private int cheatFreeMatches;
    private int minemanID;
    private int eloRange;
    private int pingRange;
    private int teamID;
    private int rematchID;
    private int missedPots;
    private int longestCombo;
    private int combo;
    private int hits;
    private int premiumMatchesPlayed;
    private int premiumMatchesExtra;
    private int premiumLosses;
    private int premiumWins;
    private int premiumElo;
    
    public int getPremiumMatches() {
        return Math.max(Practice.getInstance().getPlayerManager().getPremiumMatches(this.uniqueId) + this.premiumMatchesExtra - this.premiumMatchesPlayed, 0);
    }
    
    public int getWins(final String kitName) {
        return this.rankedWins.computeIfAbsent(kitName, k -> 0);
    }
    
    public void setWins(final String kitName, final int wins) {
        this.rankedWins.put(kitName, wins);
    }
    
    public int getLosses(final String kitName) {
        return this.rankedLosses.computeIfAbsent(kitName, k -> 0);
    }
    
    public void setLosses(final String kitName, final int losses) {
        this.rankedLosses.put(kitName, losses);
    }
    
    public int getElo(final String kitName) {
        return this.rankedElo.computeIfAbsent(kitName, k -> 1000);
    }
    
    public void setElo(final String kitName, final int elo) {
        this.rankedElo.put(kitName, elo);
    }
    
    public int getPartyElo(final String kitName) {
        return this.partyElo.computeIfAbsent(kitName, k -> 1000);
    }
    
    public void setPartyElo(final String kitName, final int elo) {
        this.partyElo.put(kitName, elo);
    }
    
    public void addPlayerKit(final int index, final PlayerKit playerKit) {
        this.getPlayerKits(playerKit.getName()).put(index, playerKit);
    }
    
    public Map<Integer, PlayerKit> getPlayerKits(final String kitName) {
        return this.playerKits.computeIfAbsent(kitName, k -> new HashMap());
    }
    
    public void setPlayerState(final PlayerState playerState) {
        this.playerState = playerState;
    }
    
    public void setCurrentMatchID(final UUID currentMatchID) {
        this.currentMatchID = currentMatchID;
    }
    
    public void setDuelSelecting(final UUID duelSelecting) {
        this.duelSelecting = duelSelecting;
    }
    
    public void setAcceptingDuels(final boolean acceptingDuels) {
        this.acceptingDuels = acceptingDuels;
    }
    
    public void setAllowingSpectators(final boolean allowingSpectators) {
        this.allowingSpectators = allowingSpectators;
    }
    
    public void setScoreboardEnabled(final boolean scoreboardEnabled) {
        this.scoreboardEnabled = scoreboardEnabled;
    }
    
    public void setCheatFreeMatches(final int cheatFreeMatches) {
        this.cheatFreeMatches = cheatFreeMatches;
    }
    
    public void setMinemanID(final int minemanID) {
        this.minemanID = minemanID;
    }
    
    public void setEloRange(final int eloRange) {
        this.eloRange = eloRange;
    }
    
    public void setPingRange(final int pingRange) {
        this.pingRange = pingRange;
    }
    
    public void setTeamID(final int teamID) {
        this.teamID = teamID;
    }
    
    public void setRematchID(final int rematchID) {
        this.rematchID = rematchID;
    }
    
    public void setMissedPots(final int missedPots) {
        this.missedPots = missedPots;
    }
    
    public void setLongestCombo(final int longestCombo) {
        this.longestCombo = longestCombo;
    }
    
    public void setCombo(final int combo) {
        this.combo = combo;
    }
    
    public void setHits(final int hits) {
        this.hits = hits;
    }
    
    public void setPremiumMatchesPlayed(final int premiumMatchesPlayed) {
        this.premiumMatchesPlayed = premiumMatchesPlayed;
    }
    
    public void setPremiumMatchesExtra(final int premiumMatchesExtra) {
        this.premiumMatchesExtra = premiumMatchesExtra;
    }
    
    public void setPremiumLosses(final int premiumLosses) {
        this.premiumLosses = premiumLosses;
    }
    
    public void setPremiumWins(final int premiumWins) {
        this.premiumWins = premiumWins;
    }
    
    public void setPremiumElo(final int premiumElo) {
        this.premiumElo = premiumElo;
    }
    
    @ConstructorProperties({ "uniqueId" })
    public PlayerData(final UUID uniqueId) {
        this.playerKits = new HashMap<String, Map<Integer, PlayerKit>>();
        this.rankedLosses = new HashMap<String, Integer>();
        this.rankedWins = new HashMap<String, Integer>();
        this.rankedElo = new HashMap<String, Integer>();
        this.partyElo = new HashMap<String, Integer>();
        this.playerState = PlayerState.LOADING;
        this.acceptingDuels = true;
        this.allowingSpectators = true;
        this.scoreboardEnabled = true;
        this.minemanID = -1;
        this.eloRange = 250;
        this.pingRange = 50;
        this.teamID = -1;
        this.rematchID = -1;
        this.premiumElo = 1000;
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
    
    public UUID getDuelSelecting() {
        return this.duelSelecting;
    }
    
    public boolean isAcceptingDuels() {
        return this.acceptingDuels;
    }
    
    public boolean isAllowingSpectators() {
        return this.allowingSpectators;
    }
    
    public boolean isScoreboardEnabled() {
        return this.scoreboardEnabled;
    }
    
    public int getCheatFreeMatches() {
        return this.cheatFreeMatches;
    }
    
    public int getMinemanID() {
        return this.minemanID;
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
    
    public int getPremiumMatchesPlayed() {
        return this.premiumMatchesPlayed;
    }
    
    public int getPremiumMatchesExtra() {
        return this.premiumMatchesExtra;
    }
    
    public int getPremiumLosses() {
        return this.premiumLosses;
    }
    
    public int getPremiumWins() {
        return this.premiumWins;
    }
    
    public int getPremiumElo() {
        return this.premiumElo;
    }
}
