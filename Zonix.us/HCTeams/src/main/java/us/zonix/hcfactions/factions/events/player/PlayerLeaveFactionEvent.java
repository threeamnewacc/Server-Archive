package us.zonix.hcfactions.factions.events.player;

import lombok.Getter;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.factions.events.FactionEvent;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.factions.events.FactionEvent;

@Getter
public class PlayerLeaveFactionEvent extends FactionEvent {

    private PlayerFaction faction;
    private Player player;

    public PlayerLeaveFactionEvent(Player player, PlayerFaction faction) {
        this.player = player;
        this.faction = faction;
    }

}
