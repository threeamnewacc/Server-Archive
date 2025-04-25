package us.zonix.hcfactions.deathlookup;

import us.zonix.hcfactions.profile.fight.ProfileFight;
import lombok.Getter;
import lombok.Setter;
import us.zonix.hcfactions.profile.fight.ProfileFight;

public class DeathLookupData {

    @Getter @Setter private ProfileFight fight;
    @Getter @Setter private int page;
    @Getter @Setter private int index;

}
