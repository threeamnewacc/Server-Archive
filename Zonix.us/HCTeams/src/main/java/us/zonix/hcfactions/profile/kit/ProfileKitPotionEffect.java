package us.zonix.hcfactions.profile.kit;

import lombok.Getter;
import org.bukkit.potion.PotionEffectType;

public class ProfileKitPotionEffect {

    @Getter private final PotionEffectType type;
    @Getter private final int level;

    public ProfileKitPotionEffect(PotionEffectType type, int level) {
        this.type = type;
        this.level = level;
    }
}
