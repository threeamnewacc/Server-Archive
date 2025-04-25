package us.zonix.hcfactions.profile.achievement;

import lombok.Getter;

public enum ProfileAchievementType {
    JOINED_SERVER("Join HCF", "sign-in"),
    DIE("Die once", "times");

    @Getter private final String name;
    @Getter private final String icon;

    ProfileAchievementType(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }
}
