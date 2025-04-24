package me.devkevin.landcore.utils.inventory;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.utils.item.ItemMaker;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;

/**
 * Copyright 25/04/2021 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class InventoryMaker {

    private List<Inventory2D> inventories = new LinkedList<>();

    private String title;

    private int rowOffset;
    private int rows;

    private int offset;
    private int page;

    public InventoryMaker(String title, int rows) {
        this(title, rows, 0);
    }

    private InventoryMaker(String title, int rows, int rowOffset) {
        this.title = CC.translate(title);
        this.rows = rows;
        this.rowOffset = rowOffset;
    }

    public Inventory2D getCurrentUI() {
        return this.inventories.get(page);
    }

    public Inventory getCurrentPage() {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }

        return this.inventories.get(page).toInventory();
    }

    private ClickableItem getItem(int slot) {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }

        Inventory2D lastInventory = this.inventories.get(this.inventories.size() - 1);
        return lastInventory.getItem(slot);
    }

    private int getSize() {
        return this.rows * 9;
    }

    private void createNewInventory() {
        Inventory2D inventory = new Inventory2D(this.title, this.rows, this.rowOffset);

        if (this.inventories.size() > 0) {
            inventory.setItem(0, this.rows - 1, new ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    InventoryMaker.this.page--;
                    try {
                        Inventory2D inventory2D = InventoryMaker.this.inventories.get(InventoryMaker.this.page);
                        if (inventory2D != null) {
                            Player player = (Player) event.getWhoClicked();
                            player.playSound(player.getLocation(), Sound.DIG_GRASS, 1, 1);
                            player.openInventory(InventoryMaker.this.getCurrentPage());
                        }
                    } catch (IndexOutOfBoundsException ignored) {

                    }
                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(Material.CARPET).setDisplayName("&6Previous Page").create();
                }
            });

            if (inventory.currentY == this.rows - 1 && inventory.currentX == -1) {
                inventory.currentX++;
            }
        }

        this.inventories.add(inventory);
    }

    private void setItem(int x, int y, ClickableItem item) {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }

        Inventory2D lastInventory = this.inventories.get(this.inventories.size() - 1);
        lastInventory.setItem(x - 1, y - 1, item);
    }

    public void setItem(int slot, ClickableItem item) {
        setItem(slot, item, null, false);
    }

    public void setItem(int slot, ClickableItem item, Player player, boolean update) {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }

        Inventory2D lastInventory = this.inventories.get(this.inventories.size() - 1);
        lastInventory.setItem(slot, item, player, update);
    }

    public void addItem(ClickableItem item) {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }

        Inventory2D lastInventory = this.inventories.get(this.inventories.size() - 1);

        if (lastInventory.currentY == this.rows - 1 && lastInventory.currentX >= 7 - this.offset) {
            lastInventory.setItem(8, this.rows - 1, new ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    InventoryMaker.this.page++;
                    try {
                        Inventory2D inventory2D = InventoryMaker.this.inventories.get(InventoryMaker.this.page);
                        if (inventory2D == null) {
                            InventoryMaker.this.page--;
                        } else {
                            Player player = (Player) event.getWhoClicked();
                            player.playSound(player.getLocation(), Sound.DIG_GRASS, 1, 1);
                            player.openInventory(InventoryMaker.this.getCurrentPage());
                        }
                    } catch (IndexOutOfBoundsException e) {
                        InventoryMaker.this.page--;
                    }
                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(Material.CARPET).setDisplayName("&6Next Page").create();
                }
            });
            this.createNewInventory();
            this.addItem(item);
        } else {
            lastInventory.setItem(++lastInventory.currentX + this.offset, lastInventory.currentY, item);
        }

        if (lastInventory.currentX >= 8 - this.offset) {
            lastInventory.currentX = this.offset - 1;
            lastInventory.currentY++;
        }
    }

    public void removeItem(int slot) {
        Inventory2D inventory2D = this.inventories.get(this.page);
        this.setItem(slot, null);
        for (int i = slot + 1; i < this.getSize(); i++) {
            ClickableItem item = this.getItem(i);

            this.setItem(i - 1, item);
            this.setItem(i, null);
        }
        if (inventory2D.currentX >= 0) {
            inventory2D.currentX--;
        } else {
            if (inventory2D.currentY > 0) {
                inventory2D.currentY--;
                inventory2D.currentX = 7;
            }
        }
    }

    public interface ClickableItem {

        void onClick(InventoryClickEvent event);

        ItemStack getItemStack();

    }

    public static class InventoryMakerHolder implements InventoryHolder {

        private InventoryMaker InventoryMaker;

        private InventoryMakerHolder(InventoryMaker InventoryMaker) {
            this.InventoryMaker = InventoryMaker;
        }

        @Override
        public Inventory getInventory() {
            return this.InventoryMaker.getCurrentPage();
        }

        public InventoryMaker getInventoryMaker() {
            return InventoryMaker;
        }
    }

    public class Inventory2D {

        private ClickableItem[][] items;
        private String title;
        private int rows, currentX = -1, currentY;

        private Inventory cachedInventory;

        Inventory2D(String title, int rows, int rowOffset) {
            this.currentY = rowOffset;
            this.title = title;
            this.rows = rows;
            this.items = new ClickableItem[9][this.rows];
        }

        void setItem(int x, int y, ClickableItem clickableItem) {
            this.items[x][y] = clickableItem;

            if (this.cachedInventory != null) {
                int slot = (y * 9) + x;
                if (clickableItem != null && clickableItem.getItemStack() != null) {
                    cachedInventory.setItem(slot, clickableItem.getItemStack());
                }
            }
        }

        public void setItem(int slot, ClickableItem clickableItem) {
            setItem(slot, clickableItem, null, false);
        }

        public void setItem(int slot, ClickableItem clickableItem, Player player, boolean update) {
            int y = Math.abs(slot / 9);
            int x = -(y * 9 - slot);

            if (update && player != null) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (!player.isOnline() || !player.getOpenInventory().getTitle().equalsIgnoreCase(title)) {
                            cancel();
                            return;
                        }
                        setItem(x, y, clickableItem);
                    }
                }.runTaskTimer(LandCore.getInstance(), 2L, 5L);
            } else {
                setItem(x, y, clickableItem);
            }
        }

        ClickableItem getItem(int slot) {
            int y = Math.abs(slot / 9);
            int x = -(y * 9 - slot);
            if (this.items.length <= x) {
                return null;
            }
            ClickableItem[] items = this.items[x];
            if (items.length <= y) {
                return null;
            }
            return items[y];
        }

        Inventory toInventory() {
            if (this.cachedInventory != null) {
                return this.cachedInventory;
            }

            Inventory inventory = Bukkit.getServer().createInventory(new InventoryMakerHolder(InventoryMaker.this), this.rows * 9, this.title);
            for (int y = 0; y < this.rows; y++) {
                for (int x = 0; x < 9; x++) {
                    int slot = y * 9 + x;
                    ClickableItem item = this.items[x][y];
                    if (item != null) {
                        inventory.setItem(slot, item.getItemStack());
                    }
                }
            }
            this.cachedInventory = inventory;
            return inventory;
        }

    }

}