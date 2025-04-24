package me.devkevin.landcore.gson.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 18/01/2023 @ 15:18
 * PotionEffectType / land.pvp.core.gson.item / LandCore
 */
@Getter
@RequiredArgsConstructor
public class PotionEffectType {
    private final String type;
    private final int duration;
    private final int amplifier;
}
