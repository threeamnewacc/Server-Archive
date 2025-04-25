package me.enzol.spigot.util;

import net.minecraft.server.*;
import org.bukkit.potion.*;

public class CraftPotionUtil
{
    private CraftPotionUtil() {
    }

    public static PotionEffect toBukkit(final MobEffect effect) {
        return new PotionEffect(PotionEffectType.getById(effect.getEffectId()), effect.getDuration(), effect.getAmplifier(), effect.isAmbient());
    }

    public static MobEffect toNMS(final PotionEffect effect) {
        return new MobEffect(effect.getType().getId(), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles());
    }

    public static MobEffect cloneWithDuration(final MobEffect effect, final int duration) {
        return new MobEffect(effect.getEffectId(), duration, effect.getAmplifier(), effect.isAmbient(), effect.isShowParticles());
    }

    public static void extendDuration(final MobEffect effect, final int duration) {
        effect.a(cloneWithDuration(effect, duration));
    }
}
