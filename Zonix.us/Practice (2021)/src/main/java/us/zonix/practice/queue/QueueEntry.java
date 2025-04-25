package us.zonix.practice.queue;

public class QueueEntry {
    private final QueueType queueType;
    private final String kitName;
    private final boolean bestOfThree;
    private final int elo;
    private final boolean party;

    public QueueType getQueueType() {
        return this.queueType;
    }

    public String getKitName() {
        return this.kitName;
    }

    public boolean isBestOfThree() {
        return this.bestOfThree;
    }

    public int getElo() {
        return this.elo;
    }

    public boolean isParty() {
        return this.party;
    }

    public QueueEntry(QueueType queueType, String kitName, boolean bestOfThree, int elo, boolean party) {
        this.queueType = queueType;
        this.kitName = kitName;
        this.bestOfThree = bestOfThree;
        this.elo = elo;
        this.party = party;
    }
}
