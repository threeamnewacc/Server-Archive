package us.zonix.hcfactions.profile.achievement;

import lombok.Getter;

public class ProfileAchievement {

    @Getter private final long createdAt;
    @Getter private final ProfileAchievementType type;

    public ProfileAchievement(long createdAt, ProfileAchievementType type) {
        this.createdAt = createdAt;
        this.type = type;
    }

    public ProfileAchievement(ProfileAchievementType type) {
        this(System.currentTimeMillis(), type);
    }
}
