package me.devkevin.practice.tournament.host;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.ItemUtil;
import me.devkevin.practice.util.inventory.InventoryUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright 20/03/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class TournamentHostMenu {
    private final Practice plugin = Practice.getInstance();

    private final InventoryUI inventoryUI = new InventoryUI(CC.DARK_GRAY + "Start a tournament", 4);

    public TournamentHostMenu() {
        new BukkitRunnable() {
            @Override
            public void run() {
                setItems();
            }
        }.runTaskLater(plugin, 30L);
    }

    private void setItems() {
        this.inventoryUI.setItem(0, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.BED)
                .name(CC.RED + "Back to General")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

                player.openInventory(plugin.getGeneralSettingMenu().getGeneralMenu().getCurrentPage());
            }
        });

        plugin.getKitManager().getKits().forEach(kit -> {

            if (kit.isEnabled()) {
                inventoryUI.setItem(kit.getPriority(), new InventoryUI.ClickableItem() {
                    private final ItemStack def = ItemUtil.renameItem(ItemUtil.reloreItem(kit.getIcon().getData().toItemStack(),
                            CC.GRAY + "Click to host tournament"), kit.getName());
                    private ItemStack itemStack = def.clone();

                    @Override
                    public void onClick(InventoryClickEvent event) {
                        InventoryUI inventoryUI = new InventoryUI(CC.GRAY + "Select team size", 1);

                        for (int i = 1; i < 5; i++) {
                            int finalI = i;
                            inventoryUI.addItem(new InventoryUI.ClickableItem() {
                                private final ItemStack def = ItemUtil.renameItem(ItemUtil.reloreItem(
                                        new ItemStack(Material.NAME_TAG),
                                        CC.GREEN + "Teamsize: " + CC.GRAY + finalI),
                                        CC.GRAY + kit.getName());
                                private ItemStack itemStack = def.clone();

                                @Override
                                public void onClick(InventoryClickEvent event) {
                                    if (!plugin.getTournamentManager().getTournaments().isEmpty()) {
                                        event.getWhoClicked().sendMessage(CC.RED + "There already is an ongoing tournament.");
                                        event.getWhoClicked().closeInventory();
                                        return;
                                    }
                                    plugin.getTournamentManager().createTournament(event.getWhoClicked(), finalI, 150, kit.getName());
                                }

                                @Override
                                public ItemStack getItemStack() {
                                    return itemStack;
                                }

                                @Override
                                public void setItemStack(ItemStack itemStack) {
                                    this.itemStack = itemStack;
                                }

                                @Override
                                public ItemStack getDefaultItemStack() {
                                    return def;
                                }
                            });
                        }

                        event.getWhoClicked().openInventory(inventoryUI.getCurrentPage());
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return this.itemStack;
                    }

                    @Override
                    public void setItemStack(ItemStack itemStack) {
                        this.itemStack = itemStack;
                    }

                    @Override
                    public ItemStack getDefaultItemStack() {
                        return def;
                    }
                });
            }
        });
    }
}
