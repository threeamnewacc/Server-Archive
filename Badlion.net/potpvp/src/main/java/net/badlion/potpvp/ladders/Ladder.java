package net.badlion.potpvp.ladders;

import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.matchmaking.MatchMakingService;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.statemachine.State;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.util.*;

public abstract class Ladder {

    public enum LadderType {OneVsOneUnranked, OneVsOneRanked, TwoVsTwoRanked, ThreeVsThreeRanked, FiveVsFiveRanked, Duel, FFA}

    protected int ladderId;
    protected List<KitRuleSet> kitRuleSets = new ArrayList<>();
    protected MatchMakingService matchMakingService;
    protected LadderType ladderType;
    protected boolean isRanked;
    protected boolean countsTowardsLimit;

    public static int globalRankedLadders = 0;

    private static Map<String, Ladder> ladder1v1RankedMap = new LinkedHashMap<>();
    private static Map<String, Ladder> ladder2v2RankedMap = new LinkedHashMap<>();
    private static Map<String, Ladder> ladder3v3RankedMap = new LinkedHashMap<>();
    private static Map<String, Ladder> ladder5v5RankedMap = new LinkedHashMap<>();

    private static Map<String, Ladder> ladder1v1UnrankedMap = new LinkedHashMap<>();

    private static Map<Integer, String> ladderIdToLadderNameMap = new HashMap<>();

    public Ladder(int ladderId, KitRuleSet kitRuleSet, MatchMakingService matchMakingService, LadderType ladderType, boolean isRanked,
                  boolean countsTowardsLimit) {
        // Check both conditions for special events
        if (ladderType == LadderType.OneVsOneRanked && isRanked && countsTowardsLimit) {
            // Only some ladders count towards global ranking depending on the version
            if (Bukkit.getSpigotJarVersion() != Server.SERVER_VERSION.V1_9 || kitRuleSet.is1_9Compatible()) {
                ++globalRankedLadders;
            }
        }

        this.ladderId = ladderId;
        this.kitRuleSets.add(kitRuleSet);
        this.matchMakingService = matchMakingService;
        this.matchMakingService.setLadder(this);
        this.ladderType = ladderType;
        this.isRanked = isRanked;
        this.countsTowardsLimit = countsTowardsLimit;
    }

    public Ladder(int ladderId, List<KitRuleSet> kitRuleSets, MatchMakingService matchMakingService, LadderType ladderType, boolean isRanked,
                  boolean countsTowardsLimit) {
        this.ladderId = ladderId;
        this.kitRuleSets = kitRuleSets;
        this.matchMakingService = matchMakingService;
        this.matchMakingService.setLadder(this);
        this.ladderType = ladderType;
        this.isRanked = isRanked;
        this.countsTowardsLimit = countsTowardsLimit;
    }

    public boolean hasLimit() {
        return this.countsTowardsLimit;
    }

	public static Map<String, Ladder> getLadderMap(LadderType ladderType) {
        if (ladderType == LadderType.OneVsOneUnranked) {
            return ladder1v1UnrankedMap;
        } else if (ladderType == LadderType.OneVsOneRanked) {
            return ladder1v1RankedMap;
        } else if (ladderType == LadderType.TwoVsTwoRanked) {
            return ladder2v2RankedMap;
        } else if (ladderType == LadderType.ThreeVsThreeRanked) {
            return ladder3v3RankedMap;
        } else if (ladderType == LadderType.FiveVsFiveRanked) {
            return ladder5v5RankedMap;
        }

        return null;
    }

    public static void registerLadder(String key, Ladder ladder) {
        Ladder.getLadderMap(ladder.getLadderType()).put(key, ladder);
        Ladder.ladderIdToLadderNameMap.put(ladder.getLadderId(), key);

        // Ladder population initialization
        for (KitRuleSet kitRuleSet : ladder.getAllKitRuleSets()) {
            kitRuleSet.getLadderPopulations().put(ladder.getLadderType(), 0);
        }
    }

    public static Ladder getLadder(String key, LadderType ladderType) {
        return Ladder.getLadderMap(ladderType).get(key);
    }

    public static Ladder getLadder(int key, LadderType ladderType) {
        String ladderName = Ladder.ladderIdToLadderNameMap.get(key);
        return Ladder.getLadder(ladderName, ladderType);
    }

    protected State<Group> getMatchState() {
        if (ladderType == LadderType.OneVsOneUnranked) {
            return GroupStateMachine.regularMatchState;
        } else {
            return GroupStateMachine.rankedMatchState;
        }
    }

    public void addGroup(Group group, int rating) {
        this.matchMakingService.addGroup(group, rating);
    }

    public abstract Game createGame();

    public abstract boolean addPlayersToGame(Game game);

    public KitRuleSet getKitRuleSet() {
        return kitRuleSets.get(0);
    }

    public List<KitRuleSet> getAllKitRuleSets() {
        return Collections.unmodifiableList(this.kitRuleSets);
    }

	public MatchMakingService getMatchMakingService() {
		return matchMakingService;
	}

	public LadderType getLadderType() {
        return ladderType;
    }

    public int getLadderId() {
        return ladderId;
    }

    public boolean isRanked() {
        return isRanked;
    }

    @Override
    public String toString() {
        return this.kitRuleSets.get(0).getName() + " " + this.getLadderType().name();
    }
}
