package net.badlion.cosmetics;

import net.badlion.cosmetics.utils.ParticleLibrary;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EntityTrail extends CosmeticItem {

    private Map<UUID, List<Location>> previousLocations = new HashMap<>();
    private Map<UUID, List<Entity>> playerTrails = new HashMap<>();
    protected ParticleLibrary.ParticleType particleType;
    protected ParticleLibrary particleLibrary;
    protected int speed = 1;
    protected int amount = 1;

    public EntityTrail(String name, Cosmetics.CosmeticType cosmeticType, CosmeticItem.ItemRarity rarity, ItemStack itemStack) {
        super(cosmeticType, name, rarity, itemStack);
        this.particleLibrary = new ParticleLibrary(this.particleType, 0, this.amount, 0);
    }

    private List<Entity> getTrailEntities(Player player) {
        if (player == null) {
            return null;
        }

        List<Entity> trailEntities = this.playerTrails.get(player.getUniqueId());
        if (trailEntities == null) {
            trailEntities = new ArrayList<>();
            this.playerTrails.put(player.getUniqueId(), trailEntities);
        }

        return trailEntities;
    }

    public void spawnTrail(Player player) {
        if (this.particleLibrary.getType() == null) {
            this.particleLibrary = new ParticleLibrary(this.particleType, 0, this.amount, 0);
        }

        List<Location> previousLocations = this.previousLocations.get(player.getUniqueId());

        List<Entity> toRemove = new ArrayList<>();
        List<Location> locations = new ArrayList<>();
        for (Entity entity : this.getTrailEntities(player)) {
            if (previousLocations != null && previousLocations.contains(entity.getLocation())) {
                toRemove.add(entity);
                continue;
            }
            locations.add(entity.getLocation());
        }

        this.particleLibrary.sendToLocation(player, locations);
        this.previousLocations.put(player.getUniqueId(), locations);

        for (Entity entity : toRemove) {
            this.getTrailEntities(player).remove(entity);
        }
    }

    public void addTrail(Player player, Entity entity) {
        getTrailEntities(player).add(entity);
    }

    public void removeEntity(Player player, Entity entity) {
        getTrailEntities(player).remove(entity);
    }

    public int getSpeed() {
        return speed;
    }

    public ParticleLibrary.ParticleType getParticleType() {
        return particleType;
    }

    public int getAmount() {
        return amount;
    }

}
