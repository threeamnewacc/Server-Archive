package us.zonix.hcfactions.profile.fight;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import us.zonix.hcfactions.profile.fight.killer.ProfileFightKiller;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
    Shitty constructors :)
 */
public class ProfileFight {

    @Getter private final UUID uuid;
    @Getter private final ProfileFightKiller killer;
    @Getter private final long occurredAt;
    @Getter private final ItemStack[] contents, armor;
    @Getter private final double hunger;
    @Getter private final List<ProfileFightEffect> effects;
    @Getter private final Location location;
    @Getter private final int ping;

    public ProfileFight(Player player, ProfileFightKiller killer) {

        this(UUID.randomUUID(), ((CraftPlayer)player).getHandle().ping, System.currentTimeMillis(), player.getInventory().getContents(), player.getInventory().getArmorContents(), player.getFoodLevel(), new ArrayList<>(), killer, player.getLocation());

        for (PotionEffect effect : player.getActivePotionEffects()) {
            effects.add(new ProfileFightEffect(effect));
        }
    }

    public ProfileFight(UUID uuid, int ping, long occurredAt, ItemStack[] contents, ItemStack[] armor, double hunger, List<ProfileFightEffect> effects, ProfileFightKiller killer, Location location) {
        this.uuid = uuid;

        this.occurredAt = occurredAt;
        this.contents = contents;
        this.armor = armor;
        this.hunger = hunger;
        this.killer = killer;
        this.effects = effects;
        this.location = location;
        this.ping = ping;
    }

    public boolean hasArmor() {
        for (ItemStack itemStack : armor) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                return true;
            }
        }
        return false;
    }

    public int getInventorySize() {
        int toReturn = 0;

        for (ItemStack itemStack : contents) {
            if (itemStack != null) {
                toReturn++;
            }
        }

        return toReturn;
    }



}
