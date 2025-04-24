package me.devkevin.practice.kit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 10/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Kit {
    private final Practice plugin = Practice.getInstance();

    private final String name;

    private ItemStack[] contents = new ItemStack[36];
    private ItemStack[] armor = new ItemStack[4];
    private ItemStack[] kitEditContents = new ItemStack[36];
    private ItemStack icon;

    private List<String> arenaWhiteList = new ArrayList<>();

    private boolean enabled;
    private boolean ranked;
    private boolean sumo;
    private boolean build;
    private boolean hcf;
    private boolean parkour;
    private boolean spleef;
    private boolean bedWars;
    private boolean combo;
    private boolean boxing;

    public void whitelistArena(String arena) {
        if (!this.arenaWhiteList.remove(arena)) {
            this.arenaWhiteList.add(arena);
        }
    }

    public void applyToPlayer(Player player) {
        player.getInventory().setContents(contents);
        player.getInventory().setArmorContents(armor);
        player.updateInventory();
        player.sendMessage(CC.YELLOW + "You equipped default kit.");
    }

    /*public void applyKit(Player player, Profile profile) {
        ItemStack[] playerContents = this.contents;
        ItemStack[] playerArmor = this.armor;
        if (profile.getCurrentKitContents() != null) {
            playerContents = profile.getCurrentKitContents();
        }

        Color color = profile.getTeamID() == 0 ? Color.RED : Color.BLUE;
        int data = color == Color.RED ? 14 : 11;

        int i = 0;
        ItemStack[] finalContents = this.getColoredItems(playerContents, data, i);
        player.getInventory().setContents(finalContents);
        profile.setCurrentKitContents(finalContents);

        ItemStack[] finalArmor = this.getColoredArmor(playerArmor, color, i);
        player.getInventory().setArmorContents(finalArmor);

        player.updateInventory();
    }

    public ItemStack[] getColoredItems(ItemStack[] items, int color, int i) {
        ItemStack[] finalItems = new ItemStack[36];

        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                if (item.getType() == Material.STAINED_CLAY) {
                    finalItems[i] = new ItemBuilder(item).durability(color).build();
                } else if (item.getType() == Material.WOOL) {
                    finalItems[i] = new ItemBuilder(item).durability(color).build();
                } else {
                    finalItems[i] = item;
                }
            }

            i++;
        }

        return finalItems;
    }

    public ItemStack[] getColoredArmor(ItemStack[] armor, Color color, int i) {
        ItemStack[] finalArmor = new ItemStack[4];

        for (ItemStack item : armor) {
            if (item != null && item.getType() != Material.AIR) {
                if (item.getType().name().startsWith("LEATHER_")) {
                    finalArmor[i] = new ItemBuilder(item).color(color).build();
                } else {
                    finalArmor[i] = item;
                }
            }

            i++;
        }

        return finalArmor;
    }*/

    public int getEditorSlots() {

        if (Kit.this.getName().equalsIgnoreCase("Nodebuff")) return 10;
        if (Kit.this.getName().equalsIgnoreCase("Debuff")) return 11;
        if (Kit.this.getName().equalsIgnoreCase("Boxing")) return 12;
        if (Kit.this.getName().equalsIgnoreCase("Vanilla")) return 13;
        if (Kit.this.getName().equalsIgnoreCase("Axe")) return 14;
        if (Kit.this.getName().equalsIgnoreCase("Combo")) return 15;
        if (Kit.this.getName().equalsIgnoreCase("Gapple")) return 16;
        if (Kit.this.getName().equalsIgnoreCase("BuildUHC")) return 19;
        if (Kit.this.getName().equalsIgnoreCase("Soup")) return 20;
        if (Kit.this.getName().equalsIgnoreCase("Sumo")) return 21;
        if (Kit.this.getName().equalsIgnoreCase("Archer")) return 22;
        if (Kit.this.getName().equalsIgnoreCase("Classic")) return 23;
        if (Kit.this.getName().equalsIgnoreCase("invaded")) return 24;
        if (Kit.this.getName().equalsIgnoreCase("Strategy")) return 25;
        if (Kit.this.getName().equalsIgnoreCase("HCF")) return 28;
        if (Kit.this.getName().equalsIgnoreCase("BedFight")) return 29;

        return 10;
    }

    public int getPriority() {
        //TODO: implement more ladders in the future

        if (Kit.this.getName().equalsIgnoreCase("Nodebuff")) return 10;
        if (Kit.this.getName().equalsIgnoreCase("Debuff")) return 11;
        if (Kit.this.getName().equalsIgnoreCase("Boxing")) return 12;
        if (Kit.this.getName().equalsIgnoreCase("Vanilla")) return 13;
        if (Kit.this.getName().equalsIgnoreCase("Axe")) return 14;
        if (Kit.this.getName().equalsIgnoreCase("Combo")) return 15;
        if (Kit.this.getName().equalsIgnoreCase("Gapple")) return 16;
        if (Kit.this.getName().equalsIgnoreCase("BuildUHC")) return 19;
        if (Kit.this.getName().equalsIgnoreCase("Soup")) return 20;
        if (Kit.this.getName().equalsIgnoreCase("Sumo")) return 21;
        if (Kit.this.getName().equalsIgnoreCase("Archer")) return 22;
        if (Kit.this.getName().equalsIgnoreCase("Classic")) return 23;
        if (Kit.this.getName().equalsIgnoreCase("invaded")) return 24;
        if (Kit.this.getName().equalsIgnoreCase("Strategy")) return 25;
        if (Kit.this.getName().equalsIgnoreCase("HCF")) return 28;
        if (Kit.this.getName().equalsIgnoreCase("BedFight")) return 29;

        return 10;
    }
}