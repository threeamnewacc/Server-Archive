package us.zonix.hcfactions.factions.events.player;

import lombok.Getter;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.factions.Faction;
import us.zonix.hcfactions.factions.events.FactionEvent;
import us.zonix.hcfactions.profile.teleport.ProfileTeleportType;

@Getter
public class PlayerCancelFactionTeleportEvent extends FactionEvent {

    private Faction faction;
    private Player player;
    private ProfileTeleportType teleportType;

    public PlayerCancelFactionTeleportEvent(Player player, Faction faction, ProfileTeleportType teleportType) {
        this.player = player;
        this.faction = faction;
        this.teleportType = teleportType;
    }


}
