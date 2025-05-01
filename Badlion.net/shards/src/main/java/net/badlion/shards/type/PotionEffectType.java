package net.badlion.shards.type;

public class PotionEffectType {
    private final String type;
    private final int duration;
    private final int amplifier;

    public PotionEffectType(String type, int duration, int amplifier){
        this.type = type;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    public int getDuration() {
        return duration;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public String getType() {
        return type;
    }
}
