package us.zonix.hcfactions.factions.events;

import lombok.Getter;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.factions.type.PlayerFaction;

@Getter
public class FactionEnemyFactionEvent extends FactionEvent {

    private PlayerFaction[] factions;

    public FactionEnemyFactionEvent(PlayerFaction[] factions) {
        this.factions = factions;
    }

}
