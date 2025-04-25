package us.zonix.hcfactions.profile.fight;

import lombok.Getter;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ProfileFightEffect {

    @Getter private final PotionEffectType type;
    @Getter private final int level, duration;

    public ProfileFightEffect(PotionEffect effect) {
        this.type = effect.getType();
        this.level = effect.getAmplifier() + 1;
        this.duration = (effect.getDuration() / 20) * 1000; //convert to seconds then to ms
    }
}
