package net.badlion.gfactions;

import java.lang.reflect.Field;

public class DeathBanConfig extends Config {

    private int deathBanTimeInMinutes;
    private int maxDeathBanLengthInMinutes;
    private int extraMinutesPerDeath;
    private int specialDeathBanInMinutes;
    private boolean specialDeathBanEnabled;
    private int heartShardPieces;

    private double donatorTimeOffPercentage;
    private double donatorPlusTimeOffPercentage;
    private double lionTimeOffPercentage;

    public DeathBanConfig(String fileName) {
        super(fileName);

        // Load config
        this.load();
    }

    public void load() {
        this.deathBanTimeInMinutes = this.config.getInt("deathban-time-in-mins");
        this.maxDeathBanLengthInMinutes = this.config.getInt("deathban-max-time-in-mins");
        this.extraMinutesPerDeath = this.config.getInt("extra-min-per-death");
        this.specialDeathBanInMinutes = this.config.getInt("special-death-ban-length-in-min");
        this.specialDeathBanEnabled = this.config.getBoolean("special-death-ban-enabled");
        this.heartShardPieces = this.config.getInt("heart-shard-pieces");

        this.donatorTimeOffPercentage = (double) this.config.getInt("donator-time-off-deathban-percentage") / 100;
        this.donatorPlusTimeOffPercentage = (double) this.config.getInt("donator-plus-time-off-deathban-percentage") / 100;
        this.lionTimeOffPercentage = (double) this.config.getInt("lion-time-off-deathban-percentage") / 100;

        if (this.donatorPlusTimeOffPercentage > this.lionTimeOffPercentage || this.donatorTimeOffPercentage > this.lionTimeOffPercentage) {
            throw new RuntimeException("XXX Donator cannot have larger time off deathban than Lion");
        }

        // Validations
        if (this.donatorTimeOffPercentage > this.donatorPlusTimeOffPercentage) {
            throw new RuntimeException("XXX Donator cannot have larger time off deathban than Donator+");
        }

        if (this.lionTimeOffPercentage > 100 || this.donatorTimeOffPercentage > 100 || this.donatorPlusTimeOffPercentage > 100) {
            throw new RuntimeException("XXX Invalid % off for donator or donator+ or lion");
        }

        if (this.deathBanTimeInMinutes > this.maxDeathBanLengthInMinutes) {
            throw new RuntimeException("XXX Base death ban time cannot be longer than maximum");
        }

        if (this.deathBanTimeInMinutes + this.extraMinutesPerDeath > this.maxDeathBanLengthInMinutes) {
            throw new RuntimeException("XXX Base death ban time + extra time per death cannot be higher than maximum");
        }

        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getType() == Integer.class) {
                try {
                    int val = (Integer) field.get(this);

                    if (val < 0) {
                        throw new RuntimeException("XXX " + field.getName() + " cannot be negative");
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Something broke when loading deathban.yml");
                }
            }
        }
    }

    public int getDeathBanTimeInMinutes() {
        return deathBanTimeInMinutes;
    }

    public int getMaxDeathBanLengthInMinutes() {
        return maxDeathBanLengthInMinutes;
    }

    public int getExtraMinutesPerDeath() {
        return extraMinutesPerDeath;
    }

    public int getSpecialDeathBanInMinutes() {
        return specialDeathBanInMinutes;
    }

    public boolean isSpecialDeathBanEnabled() {
        return specialDeathBanEnabled;
    }

    public int getHeartShardPieces() {
        return heartShardPieces;
    }

    public double getDonatorTimeOffPercentage() {
        return donatorTimeOffPercentage;
    }

    public double getDonatorPlusTimeOffPercentage() {
        return donatorPlusTimeOffPercentage;
    }

    public double getLionTimeOffPercentage() {
        return lionTimeOffPercentage;
    }
}
