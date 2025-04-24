package me.devkevin.practice.profile.hotbar;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import me.devkevin.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright 10/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class HotbarItem {

    private final ItemStack playAgain = new ItemBuilder(Material.PAPER).name(CC.YELLOW + "Play Again" + CC.GRAY + " (Right Click)").unbreakable(true).build();

    private final ItemStack[] spawnItems = new ItemStack[] {
            new ItemBuilder(Material.IRON_SWORD)
                    .name(CC.YELLOW + "Unranked Queue" + CC.GRAY + " (Right Click)")
                    .unbreakable(true)
                    .build(),
            new ItemBuilder(Material.DIAMOND_SWORD)
                    .name(CC.YELLOW + "Ranked Queue" + CC.GRAY + " (Right Click)")
                    .unbreakable(true)
                    .build(),
            new ItemBuilder(Material.BOOK)
                    .name(CC.YELLOW + "Edit Kits" + CC.GRAY + " (Right Click)")
                    .unbreakable(true)
                    .build(),
            null,
            new ItemBuilder(Material.NAME_TAG)
                    .name(CC.YELLOW + "Parties" + CC.GRAY + " (Right Click)")
                    .unbreakable(true)
                    .build(),
            null,
            null,
            new ItemBuilder(Material.SIGN)
                    .name(CC.YELLOW + "Leaderboards" + CC.GRAY + " (Right Click)")
                    .unbreakable(true)
                    .build(),
            new ItemBuilder(Material.CHEST)
                    .durability(3)
                    .name(CC.YELLOW + "Settings" + CC.GRAY + " (Right Click)")
                    .unbreakable(true)
                    .build()
    };

    private final ItemStack[] queueItems = new ItemStack[] {
                new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Leave the Queue" + CC.GRAY + " (Right Click)").unbreakable(true).build(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
    };

    private final ItemStack[] specItems = new ItemStack[] {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Stop Spectating" + CC.GRAY + " (Right Click)").unbreakable(true).build()
    };

    private final ItemStack[] specPartyItems = new ItemStack[] {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Stop Spectating Party" + CC.GRAY + " (Right Click)").unbreakable(true).build()
    };

    private final ItemStack[] partyItems = new ItemStack[] {
            new ItemBuilder(Material.GOLD_AXE).name(CC.AQUA + "Play Menu" + CC.GRAY + " (Right Click)").unbreakable(true).build(),
            new ItemBuilder(Material.GOLD_SWORD).name(CC.DARK_GREEN + "Party Events" + CC.GRAY + " (Right Click)").unbreakable(true).build(),
            null,
            null,
            new ItemBuilder(Material.SLIME_BALL).name(CC.YELLOW + "Fight other parties" + CC.GRAY + " (Right Click)").unbreakable(true).build(),
            null,
            new ItemBuilder(Material.PAPER).name(CC.GOLD + "Information" + CC.GRAY + " (Right Click)").unbreakable(true).build(),
            new ItemBuilder(Material.BOOK).name(CC.DARK_BLUE + "HCF kits" + CC.GRAY + " (Right Click)").unbreakable(true).build(),
            new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Leave Party" + CC.GRAY + " (Right Click)").unbreakable(true).build()
    };

    private final ItemStack DEFAULT_KIT = new ItemBuilder(Material.ENCHANTED_BOOK).name(CC.translate("&eDefault Kit" + CC.GRAY + " (Right Click)")).unbreakable(true).build();

    private final ItemStack[] eventItems = new ItemStack[] {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            new ItemBuilder(Material.NETHER_STAR).name(CC.RED + "Leave Event").unbreakable(true).build()
    };


    private final ItemStack[] lmsItems = new ItemStack[] {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
    };

    private final ItemStack[] staffModeItems = new ItemStack[] {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Leave Staff Mode").unbreakable(true).build()
    };

    private final ItemStack[] editorItems = new ItemStack[] {
            new ItemBuilder(Material.PAPER).name(CC.YELLOW + "Edit kit" + CC.GRAY + " (Right Click)").unbreakable(true).build(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            new ItemBuilder(Material.INK_SACK).durability(1).name(CC.RED + "Back to spawn" + CC.GRAY + " (Right Click)").unbreakable(true).build()
    };

    private final ItemStack[] boxingSword = new ItemStack[] {
            new ItemBuilder(Material.DIAMOND_SWORD)
                    .name(CC.GOLD + CC.BOLD + "Boxing Sword")
                    .enchantment(Enchantment.DAMAGE_ALL, 2)
                    .unbreakable(true)
                    .build(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
    };

    private final ItemStack[] boxingSwordDonor = new ItemStack[] {
            new ItemBuilder(Material.DIAMOND_SWORD)
                    .name(CC.GOLD + CC.BOLD + "Boxing Sword")
                    .enchantment(Enchantment.DAMAGE_ALL, 2)
                    .unbreakable(true)
                    .build(),
            new ItemBuilder(Material.IRON_SWORD)
                    .name(CC.GOLD + CC.BOLD + "Boxing Sword")
                    .enchantment(Enchantment.DAMAGE_ALL, 2)
                    .unbreakable(true)
                    .build(),
            new ItemBuilder(Material.GOLD_SWORD)
                    .name(CC.GOLD + CC.BOLD + "Boxing Sword")
                    .enchantment(Enchantment.DAMAGE_ALL, 2)
                    .unbreakable(true)
                    .build(),
            new ItemBuilder(Material.STONE_SWORD)
                    .name(CC.GOLD + CC.BOLD + "Boxing Sword")
                    .enchantment(Enchantment.DAMAGE_ALL, 2)
                    .unbreakable(true)
                    .build(),
            new ItemBuilder(Material.WOOD_SWORD)
                    .name(CC.GOLD + CC.BOLD + "Boxing Sword")
                    .enchantment(Enchantment.DAMAGE_ALL, 2)
                    .unbreakable(true)
                    .build(),
            null,
            null,
            null,
            null
    };

}
