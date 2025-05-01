package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.CosmeticItem;
import net.badlion.cosmetics.Cosmetics;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Particle extends CosmeticItem {

    protected int speed = 4;

    public Particle(String name, ItemRarity itemRarity, ItemStack itemStack) {
        super(Cosmetics.CosmeticType.PARTICLE, name, itemRarity, itemStack);
    }

    public static Location getParticleLocation(Location location, double yOffset) {
        return location.add(Math.random() - Math.random(), yOffset, Math.random() - Math.random());
    }

    public abstract void spawnParticle(Player player);

    public void spawnParticle(Location location) {

    }

    public int getSpeed() {
        return speed;
    }

}
