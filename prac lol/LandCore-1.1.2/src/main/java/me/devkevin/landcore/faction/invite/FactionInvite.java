package me.devkevin.landcore.faction.invite;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.devkevin.landcore.faction.Faction;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 0:38
 * FactionInvite / me.devkevin.landcore.faction.invite / LandCore
 */
@Getter
@Setter
@RequiredArgsConstructor
public class FactionInvite {
    private final Faction faction;
    private long timestamp = System.currentTimeMillis();
    private boolean cancelled;
}
