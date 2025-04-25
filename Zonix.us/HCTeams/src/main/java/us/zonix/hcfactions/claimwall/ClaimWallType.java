package us.zonix.hcfactions.claimwall;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.type.SystemFaction;
import org.bukkit.Material;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.type.SystemFaction;

public enum ClaimWallType {

    PVP_PROTECTION,
    SPAWN_TAG;

    private static FactionsPlugin main = FactionsPlugin.getInstance();

    public Material getBlockType() {
        return Material.valueOf(main.getMainConfig().getString("CLAIM_WALL." + name() + ".BLOCK"));
    }

    public int getBlockData() {
        return main.getMainConfig().getInt("CLAIM_WALL." + name() + ".DATA");
    }

    public int getSize() {
        return main.getMainConfig().getInt("CLAIM_WALL." + name() + ".SIZE");
    }

    public int getRange() {
        return main.getMainConfig().getInt("CLAIM_WALL." + name() + ".RANGE");
    }

    public boolean isValid(Claim claim) {
        if (claim == null) {
            return true;
        }

        if (this == ClaimWallType.PVP_PROTECTION && claim.getFaction() instanceof SystemFaction) {
            return false;
        }

        boolean isSystem = claim.getFaction() instanceof SystemFaction;
        boolean isDeathban = isSystem && ((SystemFaction) claim.getFaction()).isDeathban();

        if (this == ClaimWallType.SPAWN_TAG && !isSystem) {
            return false;
        }

        if (this == ClaimWallType.SPAWN_TAG && isDeathban) {
            return false;
        }

        return true;
    }


}
