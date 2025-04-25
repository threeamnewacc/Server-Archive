package us.zonix.hcfactions.elevator;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;

public class Elevator {

    @Getter private final Minecart minecart;

    public Elevator(Minecart minecart) {
        this.minecart = minecart;
    }

    public Location getLocation(boolean up) {
        Location current = minecart.getLocation();

        if (up) {
            while (!current.getBlock().isEmpty()) {

                if (current.getBlockY() >= current.getWorld().getMaxHeight()) {
                    return null;
                }

                current = current.add(0, 1, 0);
            }
        } else {
            while (!current.getBlock().isEmpty()) {

                if (current.getBlockY() <= 0) {
                    return null;
                }

                current = current.add(0, -1, 0);
            }
        }

        if (up) {
            return current.add(0, 1, 0);
        } else {
            return current.add(0, -1, 0);
        }
    }

    public static Elevator getByEntity(Entity entity) {
        if (entity instanceof Minecart) {
            if (entity.getLocation().getBlock().getType() == Material.FENCE_GATE) {
                if (!entity.getLocation().clone().add(0, 1, 0).getBlock().isEmpty() || (!entity.getLocation().clone().add(0, -1, 0).getBlock().isEmpty()) && entity.getLocation().clone().add(0, -1, 0).getBlock().getType() == Material.SOUL_SAND) {
                    return new Elevator((Minecart) entity);
                }
            }
        }
        return null;
    }
}
