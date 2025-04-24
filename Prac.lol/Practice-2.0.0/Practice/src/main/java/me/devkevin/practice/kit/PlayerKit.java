package me.devkevin.practice.kit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright 10/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PlayerKit {

    private String name;
    private final int index;

    private ItemStack[] contents;
    private String displayName;

    public void applyToPlayer(Player player) {
        for (ItemStack itemStack : this.contents) {
            if (itemStack != null) {
                if (itemStack.getAmount() <= 0) {
                    itemStack.setAmount(1);
                }
            }
        }
        player.getInventory().setContents(this.contents);
        player.getInventory().setArmorContents(Practice.getInstance().getKitManager().getKit(this.name).getArmor());
        player.updateInventory();
        player.sendMessage(CC.YELLOW + "You equipped your " + CC.GOLD + this.displayName + CC.YELLOW + " Kit");
    }
}
