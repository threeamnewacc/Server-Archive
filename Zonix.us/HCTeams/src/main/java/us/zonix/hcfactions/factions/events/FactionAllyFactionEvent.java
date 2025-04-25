package us.zonix.hcfactions.factions.events;

import lombok.Getter;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.factions.type.PlayerFaction;

@Getter
public class FactionAllyFactionEvent extends FactionEvent {

    private PlayerFaction[] factions;

    public FactionAllyFactionEvent(PlayerFaction[] factions) {
        this.factions = factions;
    }

}
