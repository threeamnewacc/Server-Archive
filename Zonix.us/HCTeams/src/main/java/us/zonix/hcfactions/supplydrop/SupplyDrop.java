package us.zonix.hcfactions.supplydrop;

import us.zonix.hcfactions.FactionsPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.hcfactions.FactionsPlugin;

public class SupplyDrop {

    private static FactionsPlugin main = FactionsPlugin.getInstance();
    private static final int PREPARATION_TIME = 2;

    @Getter @Setter private SupplyDropState state;
    @Getter private final Location location;

    public SupplyDrop(Location location) {
        this.location = location;
        this.state = SupplyDropState.PREPARING;

        new BukkitRunnable() {
            @Override
            public void run() {
                location.getWorld().spawnFallingBlock(new Location(location.getWorld(), location.getX(), location.getWorld().getMaxHeight(), location.getZ()), Material.CHEST, (byte) 0);
            }
        }.runTaskLater(main, PREPARATION_TIME);
    }
}
