package me.devkevin.landcore.faction.profile;

import lombok.Getter;
import lombok.Setter;
import me.devkevin.landcore.faction.Faction;
import me.devkevin.landcore.faction.invite.FactionInvite;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 0:43
 * FactionProfile / me.devkevin.landcore.faction.profile / LandCore
 */
@Getter
@Setter
public class FactionProfile {
    private Faction faction;
    private List<FactionInvite> inviteList = new ArrayList<>();
}
