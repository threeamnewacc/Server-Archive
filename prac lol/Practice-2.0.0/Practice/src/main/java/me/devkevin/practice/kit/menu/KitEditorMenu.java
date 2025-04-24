package me.devkevin.practice.kit.menu;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.practice.Practice;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.kit.PlayerKit;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.ItemUtil;
import me.devkevin.practice.util.inventory.InventoryUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Copyright 16/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class KitEditorMenu {

    private final Practice plugin = Practice.getInstance();

    private final InventoryUI kitEditor = new InventoryUI(CC.GRAY + "Kit Editor Menu", true, 4);
    private final Map<UUID, InventoryUI> editorInventories = new HashMap<>();

    public KitEditorMenu() {
        this.KitEditorMenu();
    }

    public void KitEditorMenu() {
        Collection<Kit> kits = this.plugin.getKitManager().getKits();

        for (Kit kit : kits) {
            if (kit.isEnabled()) {
                this.kitEditor.setItem(kit.getEditorSlots(), new InventoryUI.AbstractClickableItem(new ItemBuilder(kit.getIcon()).name(CC.GOLD + kit.getName()).build()) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        Player player = (Player) event.getWhoClicked();

                        if (kit.getName().equals("Sumo") || kit.getName().equals("Boxing")) {
                            player.closeInventory();
                            player.sendMessage(CC.RED + "This kit is not editable.");
                        } else {
                            KitEditorMenu.this.plugin.getEditorManager().addEditor(player, kit);
                            KitEditorMenu.this.plugin.getProfileManager().getProfileData(player.getUniqueId())
                                    .setState(ProfileState.EDITING);
                        }
                    }
                });
            }
        }
    }

    public void addEditingKitInventory(Player player, Kit kit) {
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        Map<Integer, PlayerKit> kitMap = profile.getPlayerKits(kit.getName());

        InventoryUI inventory = new InventoryUI("Managing Kit Layout", true, 4);

        int maxKits = profile.getMaxCustomKits();

        for (int i = 1; i <= 4; i++) {

            while (inventory.getItem(i) != null) {
                ++i;
            }

            CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

            if (coreProfile.hasDonor()) {
                ItemStack save = ItemUtil
                        .createItem(Material.CHEST, CC.YELLOW + "Save Kit " + CC.GREEN + kit.getName() + " #" + i);
                ItemStack load = ItemUtil
                        .createItem(Material.BOOK, CC.YELLOW + "Load Kit " + CC.GREEN + kit.getName() + " #" + i);
                ItemStack rename = ItemUtil.createItem(Material.NAME_TAG,
                        CC.YELLOW + "Rename Kit " + CC.GREEN + kit.getName() + " #" + i);
                ItemStack delete = ItemUtil
                        .createItem(Material.FLINT, CC.YELLOW + "Delete Kit " + CC.GREEN + kit.getName() + " #" + i);

                inventory.setItem(i, new InventoryUI.AbstractClickableItem(save) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        int kitIndex = event.getSlot();
                        KitEditorMenu.this.handleSavingKit(player, profile, kit, kitMap, kitIndex);
                        inventory.setItem(kitIndex + 1, 2, new InventoryUI.AbstractClickableItem(load) {
                            @Override
                            public void onClick(InventoryClickEvent event) {
                                KitEditorMenu.this.handleLoadKit(player, kitIndex, kitMap);
                            }
                        });
                        inventory.setItem(kitIndex + 1, 3, new InventoryUI.AbstractClickableItem(rename) {
                            @Override
                            public void onClick(InventoryClickEvent event) {
                                KitEditorMenu.this.handleRenamingKit(player, kitIndex, kitMap);
                            }
                        });
                        inventory.setItem(kitIndex + 1, 4, new InventoryUI.AbstractClickableItem(delete) {
                            @Override
                            public void onClick(InventoryClickEvent event) {
                                KitEditorMenu.this.handleDeleteKit(player, kitIndex, kitMap, inventory);
                            }
                        });
                    }
                });

                final int kitIndex = i;

                if (kitMap != null && kitMap.containsKey(kitIndex)) {
                    inventory.setItem(kitIndex + 1, 2, new InventoryUI.AbstractClickableItem(load) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            KitEditorMenu.this.handleLoadKit(player, kitIndex, kitMap);
                        }
                    });
                    inventory.setItem(kitIndex + 1, 3, new InventoryUI.AbstractClickableItem(rename) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            KitEditorMenu.this.handleRenamingKit(player, kitIndex, kitMap);
                        }
                    });
                    inventory.setItem(kitIndex + 1, 4, new InventoryUI.AbstractClickableItem(delete) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            KitEditorMenu.this.handleDeleteKit(player, kitIndex, kitMap, inventory);
                        }
                    });
                }
            } else {

                ItemStack save = ItemUtil
                        .createItem(Material.CHEST, CC.YELLOW + "Save Kit " + CC.GREEN + kit.getName() + " #" + i);
                ItemStack load = ItemUtil
                        .createItem(Material.BOOK, CC.YELLOW + "Load Kit " + CC.GREEN + kit.getName() + " #" + i);
                ItemStack rename = ItemUtil.createItem(Material.NAME_TAG,
                        CC.YELLOW + "Rename Kit " + CC.GREEN + kit.getName() + " #" + i);
                ItemStack delete = ItemUtil
                        .createItem(Material.FLINT, CC.YELLOW + "Delete Kit " + CC.GREEN + kit.getName() + " #" + i);


                if (i == 2 || i == 3 || i == 4) {
                    ItemBuilder builder = new ItemBuilder(Material.REDSTONE_BLOCK);
                    builder.name(CC.RED + "Unavailable Kit Slot");
                    builder.lore(Arrays.asList(CC.GRAY + "Your rank does not allow you", CC.GRAY + "to create more than " + maxKits + " kit" + (maxKits > 1 ? "s" : "") + ".", "", CC.GRAY + "Purchase a higher rank at:", CC.PINK + "shop." + LandCore.getInstance().getNetworkName() + "."));
                    inventory.setItem(i, new InventoryUI.EmptyClickableItem(builder.build()));
                    continue;
                }

                inventory.setItem(i, new InventoryUI.AbstractClickableItem(save) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        int kitIndex = event.getSlot();
                        KitEditorMenu.this.handleSavingKit(player, profile, kit, kitMap, kitIndex);
                        inventory.setItem(kitIndex + 1, 2, new InventoryUI.AbstractClickableItem(load) {
                            @Override
                            public void onClick(InventoryClickEvent event) {
                                KitEditorMenu.this.handleLoadKit(player, kitIndex, kitMap);
                            }
                        });
                        inventory.setItem(kitIndex + 1, 3, new InventoryUI.AbstractClickableItem(rename) {
                            @Override
                            public void onClick(InventoryClickEvent event) {
                                KitEditorMenu.this.handleRenamingKit(player, kitIndex, kitMap);
                            }
                        });
                        inventory.setItem(kitIndex + 1, 4, new InventoryUI.AbstractClickableItem(delete) {
                            @Override
                            public void onClick(InventoryClickEvent event) {
                                KitEditorMenu.this.handleDeleteKit(player, kitIndex, kitMap, inventory);
                            }
                        });
                    }
                });

                final int kitIndex = i;

                if (kitMap != null && kitMap.containsKey(kitIndex)) {
                    inventory.setItem(kitIndex + 1, 2, new InventoryUI.AbstractClickableItem(load) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            KitEditorMenu.this.handleLoadKit(player, kitIndex, kitMap);
                        }
                    });
                    inventory.setItem(kitIndex + 1, 3, new InventoryUI.AbstractClickableItem(rename) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            KitEditorMenu.this.handleRenamingKit(player, kitIndex, kitMap);
                        }
                    });
                    inventory.setItem(kitIndex + 1, 4, new InventoryUI.AbstractClickableItem(delete) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            KitEditorMenu.this.handleDeleteKit(player, kitIndex, kitMap, inventory);
                        }
                    });
                }
            }
        }
        this.editorInventories.put(player.getUniqueId(), inventory);
    }

    public void removeEditingKitInventory(UUID uuid) {
        InventoryUI inventoryUI = this.editorInventories.get(uuid);
        if (inventoryUI != null) {
            this.editorInventories.remove(uuid);
        }
    }

    public InventoryUI getEditingKitInventory(UUID uuid) {
        return this.editorInventories.get(uuid);
    }

    private void handleSavingKit(Player player, Profile profile, Kit kit, Map<Integer, PlayerKit> kitMap, int kitIndex) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            kitMap.get(kitIndex).setContents(player.getInventory().getContents().clone());
            player.sendMessage(
                    CC.YELLOW + "Successfully saved kit " + CC.PINK + kitIndex + CC.YELLOW + ".");
            return;
        }

        PlayerKit playerKit = new PlayerKit(kit.getName(), kitIndex, player.getInventory().getContents().clone(),
                kit.getName() + " Kit " + kitIndex);
        profile.addPlayerKit(kitIndex, playerKit);

        player.sendMessage(CC.YELLOW + "Successfully saved kit " + CC.PINK + kitIndex + CC.YELLOW + ".");
    }

    private void handleLoadKit(Player player, int kitIndex, Map<Integer, PlayerKit> kitMap) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            ItemStack[] contents = kitMap.get(kitIndex).getContents();
            for (ItemStack itemStack : contents) {
                if (itemStack != null) {
                    if (itemStack.getAmount() <= 0) {
                        itemStack.setAmount(1);
                    }
                }
            }
            player.getInventory().setContents(contents);
            player.updateInventory();
        }
    }

    private void handleRenamingKit(Player player, int kitIndex, Map<Integer, PlayerKit> kitMap) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            this.plugin.getEditorManager().addRenamingKit(player.getUniqueId(), kitMap.get(kitIndex));

            player.closeInventory();
            player.sendMessage(CC.YELLOW + "Enter a name for this kit (chat colors are also applicable).");
        }
    }

    private void handleDeleteKit(Player player, int kitIndex, Map<Integer, PlayerKit> kitMap, InventoryUI inventory) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            this.plugin.getEditorManager().removeRenamingKit(player.getUniqueId());

            kitMap.remove(kitIndex);

            player.sendMessage(
                    CC.YELLOW + "Successfully removed kit " + CC.PINK + kitIndex + CC.YELLOW + ".");

            if (LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).hasDonor()) {
                inventory.setItem(kitIndex + 1, 2, null);
                inventory.setItem(kitIndex + 1, 3, null);
                inventory.setItem(kitIndex + 1, 4, null);
                /*inventory.setItem(kitIndex + 1, 5, null);
                inventory.setItem(kitIndex + 1, 6, null);
                inventory.setItem(kitIndex + 1, 7, null);*/
            } else {
                inventory.setItem(kitIndex + 1, 2, null);
            }
        }
    }
}
