package us.zonix.hcfactions.factions.claims;

import us.zonix.hcfactions.factions.Faction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.Profile;

@Getter
@Setter
public class ClaimProfile {

    private Player player;
    private Profile profile;
    private Faction faction;
    private boolean resetClicked;
    private ClaimPillar[] pillars;

    public ClaimProfile(Player player, Faction faction) {
        this.player = player;
        this.faction = faction;
        this.pillars = new ClaimPillar[2];
        this.profile = Profile.getByPlayer(player);
        this.profile.setClaimProfile(this);
    }

    public void removePillars() {
        for (ClaimPillar claimPillar : pillars) {
            if (claimPillar != null) {
                claimPillar.remove();
            }
        }

        this.pillars = new ClaimPillar[2];
    }

}
