package us.zonix.practice.match;

import java.util.UUID;
import us.zonix.practice.arena.Arena;

public class MatchRequest {
    private final UUID requester;
    private final UUID requested;
    private final Arena arena;
    private final String kitName;
    private final boolean party;
    private final boolean bestOfThree;

    public UUID getRequester() {
        return this.requester;
    }

    public UUID getRequested() {
        return this.requested;
    }

    public Arena getArena() {
        return this.arena;
    }

    public String getKitName() {
        return this.kitName;
    }

    public boolean isParty() {
        return this.party;
    }

    public boolean isBestOfThree() {
        return this.bestOfThree;
    }

    public MatchRequest(UUID requester, UUID requested, Arena arena, String kitName, boolean party, boolean bestOfThree) {
        this.requester = requester;
        this.requested = requested;
        this.arena = arena;
        this.kitName = kitName;
        this.party = party;
        this.bestOfThree = bestOfThree;
    }
}
