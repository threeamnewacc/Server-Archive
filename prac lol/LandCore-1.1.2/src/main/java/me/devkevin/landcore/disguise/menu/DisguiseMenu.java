package me.devkevin.landcore.disguise.menu;

import lombok.Getter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.nametag.impl.InternalNametag;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.inventory.InventoryUI;
import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.Arrays;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 09/02/2023 @ 19:00
 * DisguiseMenu / me.devkevin.landcore.disguise / LandCore
 */
@Getter
public class DisguiseMenu {
    private final InventoryUI rankMenu = new InventoryUI(CC.GRAY + "Pick a Rank -> (Disguise)", 1);

    public DisguiseMenu() {
        setup();
    }

    private void setup() {
        this.rankMenu.setItem(0, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.BLUE)
                        .name(CC.BLUE + "✦" + CC.GREEN + "Basic")
                        .lore(Arrays.asList(
                                "",
                                CC.GRAY + CC.B + "In disguise mode",
                                CC.GRAY + CC.B + "you can only choose",
                                CC.GRAY + CC.B + "donor ranks or member.",
                                ""
                        ))
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                coreProfile.setDisguiseRank(Rank.BASIC);
                player.sendMessage(CC.GREEN + "You has choose " + CC.BLUE + "✦" + CC.GREEN + "Basic" + CC.GREEN + " rank.");

                player.closeInventory();

                Bukkit.getScheduler().runTask(LandCore.getInstance(), () -> {
                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        player1.hidePlayer(player);
                        player1.showPlayer(player);

                        LandCore.getInstance().getDisguiseManager().updateTablist();

                        InternalNametag.reloadPlayer(player);
                        InternalNametag.reloadOthersFor(player);
                    }
                });
            }
        });

        this.rankMenu.setItem(1, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.YELLOW)
                        .name(CC.YELLOW + "✯" + CC.GOLD + "Gold")
                        .lore(Arrays.asList(
                                "",
                                CC.GRAY + CC.B + "In disguise mode",
                                CC.GRAY + CC.B + "you can only choose",
                                CC.GRAY + CC.B + "donor ranks or member.",
                                ""
                        ))
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                coreProfile.setDisguiseRank(Rank.GOLD);
                player.sendMessage(CC.GREEN + "You has choose " + CC.YELLOW + "✯" + CC.GOLD + "Gold" + CC.GREEN + " rank.");

                player.closeInventory();

                Bukkit.getScheduler().runTask(LandCore.getInstance(), () -> {
                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        player1.hidePlayer(player);
                        player1.showPlayer(player);

                        LandCore.getInstance().getDisguiseManager().updateTablist();

                        InternalNametag.reloadPlayer(player);
                        InternalNametag.reloadOthersFor(player);
                    }
                });
            }
        });

        this.rankMenu.setItem(2, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.LIME)
                        .name(CC.GREEN + "✵" + CC.DARK_GREEN + "Emerald")
                        .lore(Arrays.asList(
                                "",
                                CC.GRAY + CC.B + "In disguise mode",
                                CC.GRAY + CC.B + "you can only choose",
                                CC.GRAY + CC.B + "donor ranks or member.",
                                ""
                        ))
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                coreProfile.setDisguiseRank(Rank.EMERALD);
                player.sendMessage(CC.GREEN + "You has choose " + CC.GREEN + "✵" + CC.DARK_GREEN + "Emerald" + CC.GREEN + " rank.");

                player.closeInventory();

                Bukkit.getScheduler().runTask(LandCore.getInstance(), () -> {
                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        player1.hidePlayer(player);
                        player1.showPlayer(player);

                        LandCore.getInstance().getDisguiseManager().updateTablist();

                        InternalNametag.reloadPlayer(player);
                        InternalNametag.reloadOthersFor(player);
                    }
                });
            }
        });

        this.rankMenu.setItem(3, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.AQUA)
                        .name(CC.AQUA + "❇" + CC.D_AQUA + "Diamond")
                        .lore(Arrays.asList(
                                "",
                                CC.GRAY + CC.B + "In disguise mode",
                                CC.GRAY + CC.B + "you can only choose",
                                CC.GRAY + CC.B + "donor ranks or member.",
                                ""
                        ))
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                coreProfile.setDisguiseRank(Rank.DIAMOND);
                player.sendMessage(CC.GREEN + "You has choose " + CC.AQUA + "❇" + CC.D_AQUA + "Diamond" + CC.GREEN + " rank.");

                player.closeInventory();

                Bukkit.getScheduler().runTask(LandCore.getInstance(), () -> {
                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        player1.hidePlayer(player);
                        player1.showPlayer(player);

                        LandCore.getInstance().getDisguiseManager().updateTablist();

                        InternalNametag.reloadPlayer(player);
                        InternalNametag.reloadOthersFor(player);
                    }
                });
            }
        });

        this.rankMenu.setItem(4, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.ORANGE)
                        .name(CC.GOLD + "❊" + CC.YELLOW + "LOL")
                        .lore(Arrays.asList(
                                "",
                                CC.GRAY + CC.B + "In disguise mode",
                                CC.GRAY + CC.B + "you can only choose",
                                CC.GRAY + CC.B + "donor ranks or member.",
                                ""
                        ))
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                coreProfile.setDisguiseRank(Rank.LOL);
                player.sendMessage(CC.GREEN + "You has choose " + CC.GOLD + "❊" + CC.YELLOW + "LOL" + CC.GREEN + " rank.");

                player.closeInventory();

                Bukkit.getScheduler().runTask(LandCore.getInstance(), () -> {
                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        player1.hidePlayer(player);
                        player1.showPlayer(player);

                        LandCore.getInstance().getDisguiseManager().updateTablist();

                        InternalNametag.reloadPlayer(player);
                        InternalNametag.reloadOthersFor(player);
                    }
                });
            }
        });

        this.rankMenu.setItem(8, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.GREEN)
                        .name(CC.GREEN + "Member")
                        .lore(Arrays.asList(
                                "",
                                CC.GRAY + CC.B + "In disguise mode",
                                CC.GRAY + CC.B + "you can only choose",
                                CC.GRAY + CC.B + "donor ranks or member.",
                                ""
                        ))
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                coreProfile.setDisguiseRank(Rank.MEMBER);
                player.sendMessage(CC.GREEN + "You has choose " + CC.GREEN + "Member" + CC.GREEN + " rank.");

                player.closeInventory();

                Bukkit.getScheduler().runTask(LandCore.getInstance(), () -> {
                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        player1.hidePlayer(player);
                        player1.showPlayer(player);

                        LandCore.getInstance().getDisguiseManager().updateTablist();

                        InternalNametag.reloadPlayer(player);
                        InternalNametag.reloadOthersFor(player);

                        NickAPI.refreshPlayerSync(player);
                    }
                });
            }
        });
    }
}
