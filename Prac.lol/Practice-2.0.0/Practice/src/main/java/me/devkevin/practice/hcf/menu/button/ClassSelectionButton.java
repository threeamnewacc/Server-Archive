package me.devkevin.practice.hcf.menu.button;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 25/02/2023 @ 1:14
 * ClassSelectionButton / me.devkevin.practice.hcf.menu.button / Practice
 */
public class ClassSelectionButton extends Button {

    private final UUID uuid;

    public ClassSelectionButton(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.SKULL_ITEM).durability(3);
        List<String> lore = new ArrayList<>();
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party.getBards().contains(uuid)) {
            lore.add("&7Diamond");
            lore.add((party.isArcherFull() ? "&c&m" : "&7") + "Archer");
            lore.add("&7\u25CF &eBard");
        } else if (party.getArchers().contains(uuid)) {
            lore.add("&7Diamond");
            lore.add("&7\u25CF &eArcher");
            lore.add((party.isBardFull() ? "&c&m" : "&7") + "Bard");
        } else {
            lore.add("&7\u25CF &eDiamond");
            lore.add((party.isArcherFull() ? "&c&m" : "&7") + "Archer");
            lore.add((party.isBardFull() ? "&c&m" : "&7") + "Bard");
        }

        itemBuilder.name(CC.translate(CC.BLUE + Bukkit.getPlayer(uuid).getName()));
        itemBuilder.owner(Bukkit.getPlayer(uuid).getName());
        itemBuilder.lore(CC.translate(lore));

        return itemBuilder.build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (!party.getBards().contains(uuid) && !party.getArchers().contains(uuid) || !party.getArchers().contains(uuid) && !party.getBards().contains(uuid)) {
            party.addBard(uuid);
        } else if (party.getBards().contains(uuid)) {
            party.addArcher(uuid);
        } else {
            party.removeClass(uuid);
        }

        playNeutral(player);
    }
}
