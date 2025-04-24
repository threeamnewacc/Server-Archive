package me.devkevin.practice.hcf.effects;

import lombok.AllArgsConstructor;
import org.bukkit.potion.PotionEffect;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 25/02/2023 @ 0:20
 * EffectData / me.devkevin.practice.hcf.effects / Practice
 */
@AllArgsConstructor
public class EffectData {
    public int energyCost;
    public PotionEffect clickable, heldable;
}
