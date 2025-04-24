package me.devkevin.landcore.gson.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 18/01/2023 @ 15:17
 * EnchantType / land.pvp.core.gson.item / LandCore
 */
@Getter
@RequiredArgsConstructor
public class EnchantType {
    private final String type;
    private final int tier;
}
