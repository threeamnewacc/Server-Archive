package us.zonix.hcfactions.profile.fight.killer.type;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import us.zonix.hcfactions.profile.fight.ProfileFightEffect;
import us.zonix.hcfactions.profile.fight.killer.ProfileFightKiller;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileFightPlayerKiller extends ProfileFightKiller {

    @Getter private final UUID uuid;
    @Getter private final ItemStack[] contents, armor;
    @Getter private final double health, hunger;
    @Getter private final int ping;
    @Getter private final List<ProfileFightEffect> effects;

    public ProfileFightPlayerKiller(Player player) {
        this(player.getName(), player.getUniqueId(), ((CraftPlayer)player).getHandle().ping, player.getInventory().getContents(), player.getInventory().getArmorContents(), player.getHealth(), player.getFoodLevel(), new ArrayList<>());

        for (PotionEffect effect : player.getActivePotionEffects()) {
            effects.add(new ProfileFightEffect(effect));
        }
    }

    public ProfileFightPlayerKiller(String name, UUID uuid, int ping, ItemStack[] contents, ItemStack[] armor, double health, double hunger, List<ProfileFightEffect> effects) {
        super(EntityType.PLAYER, name);

        this.ping = ping;
        this.uuid = uuid;
        this.contents = contents;
        this.armor = armor;
        this.health = health;
        this.hunger = hunger;
        this.effects = effects;
    }

}
