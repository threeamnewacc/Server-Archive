package us.zonix.hcfactions.profile.kit;

import lombok.Getter;

import java.text.DecimalFormat;

public class ProfileKitEnergy {

    private static final DecimalFormat SECONDS_FORMATTER = new DecimalFormat("#0.0");

    @Getter private long time;

    public ProfileKitEnergy() {
        this.time = System.currentTimeMillis() + 100000;
    }

    public void setEnergy(double amount) {
        time = (long) ((System.currentTimeMillis() + 100000) - (amount * 1000));
    }

    public String getFormattedString() {
        if (getEnergy() >= 100.0) {
            return "100.0";
        }
        return SECONDS_FORMATTER.format((((System.currentTimeMillis() + 100000) - time) / 1000.0f));
    }

    public String getFormattedDifference(int amount) {
        return SECONDS_FORMATTER.format(amount - getEnergy());
    }

    public double getEnergy() {
        if ((((System.currentTimeMillis() + 100000) - time) / 1000.0f) >= 100.0) {
            return 100.0;
        }
        return (((System.currentTimeMillis() + 100000) - time) / 1000.0f);
    }

}
