package us.zonix.hcfactions.statracker;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.FactionsPlugin;

public enum StatTrackerType {
    WEAPON,
    ARMOR;

    private static FactionsPlugin main = FactionsPlugin.getInstance();

    public String getHeader() {
        return main.getLanguageConfig().getString("STAT_TRACKER." + (this == WEAPON ? "KILLS" : "DEATHS") + ".HEADER");
    }

    public String getLine() {
        return main.getLanguageConfig().getString("STAT_TRACKER." + (this == WEAPON ? "KILLS" : "DEATHS") + ".LINE");
    }

}
