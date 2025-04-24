package me.devkevin.landcore.gson.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 18/01/2023 @ 15:17
 * ItemType / land.pvp.core.gson.item / LandCore
 */
@Getter
@RequiredArgsConstructor
public class ItemType {

    private final String type;
    private final short durability;
    private final int amount;
    private final List<EnchantType> enchants;
    private final ItemMetaType meta;
}
