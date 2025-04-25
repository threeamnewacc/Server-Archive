package us.zonix.practice.queue;

public enum QueueType {
    UNRANKED("Unranked"),
    RANKED("Ranked"),
    PREMIUM("Premium");

    private final String name;

    public boolean isRanked() {
        return this != UNRANKED;
    }

    public boolean isPremium() {
        return this == PREMIUM;
    }

    public boolean isBoth() {
        return this == PREMIUM || this == RANKED;
    }

    public String getName() {
        return this.name;
    }

    private QueueType(String name) {
        this.name = name;
    }
}
