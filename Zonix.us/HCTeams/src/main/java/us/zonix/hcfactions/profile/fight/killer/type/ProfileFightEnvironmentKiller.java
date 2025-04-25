package us.zonix.hcfactions.profile.fight.killer.type;

import us.zonix.hcfactions.profile.fight.ProfileFightEnvironment;
import us.zonix.hcfactions.profile.fight.killer.ProfileFightKiller;
import lombok.Getter;
import us.zonix.hcfactions.profile.fight.ProfileFightEnvironment;

public class ProfileFightEnvironmentKiller extends ProfileFightKiller {

    @Getter private final ProfileFightEnvironment type;

    public ProfileFightEnvironmentKiller(ProfileFightEnvironment type) {
        super(null, null);
        this.type = type;
    }
}
