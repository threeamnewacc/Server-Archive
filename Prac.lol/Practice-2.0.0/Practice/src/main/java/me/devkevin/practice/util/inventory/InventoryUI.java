package me.devkevin.practice.util.inventory;

import lombok.Getter;
import lombok.Setter;
import me.devkevin.practice.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

@Getter
public class InventoryUI {

    @Getter private final List<Inventory2D> inventories = new LinkedList<>();

    private final String title;

    private final int rowOffset;
    private final int rows;

    @Setter private int offset;
    private int page;

    public InventoryUI(String title, int rows) {
        this(title, rows, 0);
    }

    public InventoryUI(String title, boolean bool, int rows) {
        this(title, rows, 0);
    }

    public InventoryUI(String title, int rows, int rowOffset) {
        this.title = title;
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

    public ClickableItem getItem(int slot) {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }

        Inventory2D lastInventory = this.inventories.get(this.inventories.size() - 1);
        return lastInventory.getItem(slot);
    }

    public int getSize() {
        return this.rows * 9;
    }

    private void createNewInventory() {
        Inventory2D inventory = new Inventory2D(this.title, this.rows, this.rowOffset);
        if (this.inventories.size() > 0) {
            inventory.setItem(0, this.rows - 1, new AbstractClickableItem(ItemUtil.createItem(Material.ARROW, ChatColor.AQUA + "Page #" + (this.inventories.size()))) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    InventoryUI.this.page--;
                    try {
                        Inventory2D inventory2D = InventoryUI.this.inventories.get(InventoryUI.this.page);
                        if (inventory2D == null) {
                            InventoryUI.this.page++;
                        } else {
                            event.getWhoClicked().openInventory(InventoryUI.this.getCurrentPage());
                        }
                    } catch (IndexOutOfBoundsException e) {
                        InventoryUI.this.page++;
                    }
                }
            });

            if (inventory.currentY == this.rows - 1 && inventory.currentX == -1) {
                inventory.currentX++;
            }
        }

        this.inventories.add(inventory);
    }

    public void setItem(int x, int y, ClickableItem item) {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }

        Inventory2D lastInventory = this.inventories.get(this.inventories.size() - 1);
        lastInventory.setItem(x - 1, y - 1, item);
    }

    public void setItem(int slot, ClickableItem item) {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }

        Inventory2D lastInventory = this.inventories.get(this.inventories.size() - 1);
        lastInventory.setItem(slot, item);
    }

    public void addItem(ClickableItem item) {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }

        Inventory2D lastInventory = this.inventories.get(this.inventories.size() - 1);

        if (lastInventory.currentY == this.rows - 1 && lastInventory.currentX >= 7 - this.offset) {
            lastInventory.setItem(8, this.rows - 1, new AbstractClickableItem(
                    ItemUtil.createItem(Material.ARROW, ChatColor.RED + "Page #" + (this.inventories.size() + 1))) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    InventoryUI.this.page++;
                    try {
                        Inventory2D inventory2D = InventoryUI.this.inventories.get(InventoryUI.this.page);
                        if (inventory2D == null) {
                            InventoryUI.this.page--;
                        } else {
                            event.getWhoClicked().openInventory(InventoryUI.this.getCurrentPage());
                        }
                    } catch (IndexOutOfBoundsException e) {
                        InventoryUI.this.page--;
                    }
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

        void setItemStack(ItemStack itemStack);

        ItemStack getDefaultItemStack();
    }

    @Getter @Setter
    public static class EmptyClickableItem implements ClickableItem {
        private final ItemStack defaultItemStack;
        private ItemStack itemStack;

        public EmptyClickableItem(ItemStack itemStack) {
            this.itemStack = itemStack;
            this.defaultItemStack = itemStack;
        }

        @Override
        public void onClick(InventoryClickEvent event) {

        }
    }

    @Getter @Setter
    public static abstract class AbstractClickableItem implements ClickableItem {
        private final ItemStack defaultItemStack;
        private ItemStack itemStack;

        public AbstractClickableItem(ItemStack itemStack) {
            this.itemStack = itemStack;
            this.defaultItemStack = itemStack;
        }
    }

    @Getter
    public static class InventoryUIHolder implements InventoryHolder {
        private InventoryUI inventoryUI;
        private String title;
        private int slots;

        private InventoryUIHolder(InventoryUI inventoryUI, String title, int slots) {
            this.inventoryUI = inventoryUI;
            this.title = title;
            this.slots = slots;
        }

        @Override
        public Inventory getInventory() {
            return this.inventoryUI.getCurrentPage();
        }
    }

    @Getter
    public class Inventory2D {
        private final ClickableItem[][] items;
        private final String title;
        private final int rows;

        private Inventory cachedInventory;
        private int currentX = -1;
        private int currentY;

        public Inventory2D(String title, int rows, int rowOffset) {
            this.currentY = rowOffset;
            this.title = title;
            this.rows = rows;
            this.items = new ClickableItem[9][this.rows];
        }

        public void setItem(int x, int y, ClickableItem clickableItem) {
            this.items[x][y] = clickableItem;

            if (this.cachedInventory != null) {
                int slot = (y * 9) + x;

                this.cachedInventory.setItem(slot, clickableItem != null ? clickableItem.getItemStack() : null);
            }
        }

        public void setItem(int slot, ClickableItem clickableItem) {
            int y = Math.abs(slot / 9);
            int x = -(y * 9 - slot);

            this.setItem(x, y, clickableItem);
        }

        public ClickableItem getItem(int slot) {
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

        public Inventory toInventory() {
            if (this.cachedInventory != null) {
                return this.cachedInventory;
            }

            Inventory inventory = Bukkit.getServer().createInventory(new InventoryUIHolder(InventoryUI.this, this.title, this.rows * 9), this.rows * 9, this.title);
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
