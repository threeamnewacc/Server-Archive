package net.badlion.survivalgames;

import net.badlion.survivalgames.gamemodes.GameMode;
import net.badlion.survivalgames.gamemodes.ClassicGameMode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Ladder {

    public static enum LadderType {
        FFA, TEAMS
    }

    private static Map<LadderType, Map<String, Ladder>> ladderMap = new HashMap<>();
    private static Map<LadderType, Map<Integer, Ladder>> ladderIdMap = new HashMap<>();

    private int ladderId;
    private String name;
    private GameMode gameMode;

    private Ladder(int id, String ladderName, GameMode gameMode) {
        this.ladderId = id;
        this.name = ladderName;
        this.gameMode = gameMode;
    }

    public static void initialize() {
        for (LadderType ladderType : LadderType.values()) {
            Ladder.ladderMap.put(ladderType, new HashMap<String, Ladder>());
            Ladder.ladderIdMap.put(ladderType, new HashMap<Integer, Ladder>());
        }

        Ladder.registerLadder(1, "Classic", new ClassicGameMode());
        //Ladder.registerLadder(2, "UHC", new UHCGameMode());
        //Ladder.registerLadder(3, "PotPvP", new PotPvPGameMode());
    }

    public static void registerLadder(int id, String ladderName, GameMode gameMode) {
        for (LadderType ladderType : LadderType.values()) {
            Ladder ladder = new Ladder(id, ladderName, gameMode);
            Ladder.ladderIdMap.get(ladderType).put(id, ladder);
            Ladder.ladderMap.get(ladderType).put(ladderName, ladder);
        }
    }

    public static Ladder getLadder(String name, LadderType ladderType) {
        return Ladder.ladderMap.get(ladderType).get(name);
    }

    public static Ladder getLadder(int ladderId, LadderType ladderType) {
        return Ladder.ladderIdMap.get(ladderType).get(ladderId);
    }

    public static Collection<Ladder> getAllLadders() {
        return Ladder.ladderMap.get(LadderType.FFA).values();
    }

    public int getLadderId() {
        return ladderId;
    }

    public String getName() {
        return name;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    // This stuff isn't needed since I made the constructor private, but i'm paranoid
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Ladder)) {
            return false;
        }

        Ladder ladder = (Ladder) obj;

        return this.ladderId == ladder.getLadderId() && this.name.equals(ladder.getName()) && this.gameMode.equals(ladder.getGameMode());
    }

    @Override
    public int hashCode() {
        return (this.ladderId * this.ladderId) + (this.gameMode.hashCode() * this.gameMode.hashCode() * 100);
    }
}
