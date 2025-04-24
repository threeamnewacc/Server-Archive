package me.devkevin.landcore.store;

import lombok.Getter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.utils.inventory.InventoryUI;
import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

import static me.devkevin.landcore.LandCoreAPI.PLACEHOLDER_ITEM;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 24/03/2023 @ 20:02
 * StorePCoinMenu / me.devkevin.landcore.store / LandCore
 */
@Getter
public class StorePCoinMenu {
    private final LandCore plugin = LandCore.getInstance();

    private final InventoryUI menu = new InventoryUI(CC.GRAY + "PCoins Store Menu", 5);

    public InventoryUI menu(Player player) {
        CoreProfile coreProfile = this.plugin.getProfileManager().getProfile(player.getUniqueId());

        int[] slots = {9, 10, 11, 13, 14, 15, 16, 17, 19, 26, 27, 28, 35, 37, 38, 39, 40, 41, 42, 43, 44};
        Arrays.stream(slots).forEach(slot -> this.menu.setItem(slot, new InventoryUI.EmptyClickableItem(PLACEHOLDER_ITEM)));

        this.menu.setItem(12, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
                .durability(5)
                .name(CC.YELLOW + "You're viewing this category!")
                .hideFlags()
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                player.closeInventory();
            }
        });

        this.menu.setItem(36, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.BED)
                .name(CC.RED + "Close Store")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                player.closeInventory();
            }
        });

        this.menu.setItem(3, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.EMERALD)
                .name(CC.GOLD + "Permanent Ranks")
                .lore("")
                .lore(CC.GRAY + "Improve your experience with ranks")
                .lore(CC.GRAY + "which offers you extra features!")
                .lore("")
                .lore(CC.RED + "Note: " + CC.GRAY + "Each ranks contains all")
                .lore(CC.GRAY + "features from previous ones.")
                .lore("")
                .lore(CC.YELLOW + "You're viewing this category!")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                player.openInventory(menu(player).getCurrentPage());
            }
        });

        this.menu.setItem(4, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.DIAMOND)
                .name(CC.GOLD + "Temporary Ranks")
                .lore("")
                .lore(CC.GRAY + "Improve your experience with ranks")
                .lore(CC.GRAY + "which offers you extra features!")
                .lore("")
                .lore(CC.RED + "Note: " + CC.GRAY + "Each ranks contains all")
                .lore(CC.GRAY + "features from previous ones.")
                .lore("")
                .lore(CC.GREEN + "Purchased days will accumulate at any")
                .lore(CC.GREEN + "time you decide to purchase this again!")
                .lore("")
                .lore(CC.YELLOW + "Click to view this category!")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                player.closeInventory();
                //TODO:
            }
        });

        this.menu.setItem(5, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.IRON_INGOT)
                .name(CC.GOLD + "Upgrade Ranks")
                .lore("")
                .lore(CC.GRAY + "Improve your experience with ranks")
                .lore(CC.GRAY + "which offers you extra features!")
                .lore("")
                .lore(CC.RED + "Note: " + CC.GRAY + "Each ranks contains all")
                .lore(CC.GRAY + "features from previous ones.")
                .lore("")
                .lore(CC.YELLOW + "Click to view this category!")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                player.closeInventory();
                //TODO:
            }
        });

        this.menu.setItem(0, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.SKULL_ITEM).durability(3).owner(player.getName())
                .name(CC.GREEN + "Buying for: " + coreProfile.getGrant().getRank().getColor() + player.getName())
                .lore(CC.GRAY + "You're buying for yourself!")
                .lore("")
                .lore(CC.RED + "Note: " + CC.GRAY + "If you want buying for another")
                .lore(CC.GRAY + "player, both must be connected to same server.")
                .lore("")
                .lore(CC.YELLOW + "Click to know more about buy for another player.")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                player.closeInventory();

                player.sendMessage(CC.GRAY + CC.B + "To buy for another player you must transfer PCoins to his account using");
                player.sendMessage(CC.YELLOW + CC.U + "/ppay (player) (amount)");
            }
        });

        this.menu.setItem(18, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.GOLD_INGOT)
                .name(CC.GOLD + "PCoins: " + CC.R + coreProfile.getP_coin() + CC.COIN)
                .lore("")
                .lore(CC.GRAY + "PCoin are used to acquire server")
                .lore(CC.GRAY + "ranks, chat tags, chat color themes,")
                .lore(CC.GRAY + "buy features in the entire network")
                .lore(CC.GRAY + "and access to exclusive gamemodes")
                .lore("")
                .lore(CC.YELLOW + CC.U + "You can purchase PCoins at store.prac.lol")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                player.closeInventory();
                player.sendMessage(CC.GRAY + "● " + CC.YELLOW + "Store: " + CC.GOLD + "https://store.prac.lol".replace("https://", ""));
            }
        });




        return menu;
    }
}
