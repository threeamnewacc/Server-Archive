package us.zonix.hcfactions.claimwall;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Accessors(chain = true)
public class ClaimWall {

    @Getter private final ClaimWallType type;
    @Getter private final Location location;

    public ClaimWall(ClaimWallType type, Location location) {
        this.type = type;
        this.location = location;
    }

    public ClaimWall show(Player player) {
        player.sendBlockChange(location, type.getBlockType(), (byte) type.getBlockData());
        return this;
    }

    public ClaimWall hide(Player player) {
        player.sendBlockChange(location, Material.AIR, (byte) 0);
        return this;
    }

}
