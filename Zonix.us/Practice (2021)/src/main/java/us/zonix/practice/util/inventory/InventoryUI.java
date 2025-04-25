package us.zonix.practice.util.inventory;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import us.zonix.practice.util.ItemUtil;

public class InventoryUI {
    private final List<InventoryUI.Inventory2D> inventories = new LinkedList<>();
    private final String title;
    private final int rowOffset;
    private final int rows;
    private int offset;
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

    public InventoryUI.Inventory2D getCurrentUI() {
        return this.inventories.get(this.page);
    }

    public Inventory getCurrentPage() {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }

        return this.inventories.get(this.page).toInventory();
    }

    public InventoryUI.ClickableItem getItem(int slot) {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }

        InventoryUI.Inventory2D lastInventory = this.inventories.get(this.inventories.size() - 1);
        return lastInventory.getItem(slot);
    }

    public int getSize() {
        return this.rows * 9;
    }

    private void createNewInventory() {
        InventoryUI.Inventory2D inventory = new InventoryUI.Inventory2D(this.title, this.rows, this.rowOffset);
        if (this.inventories.size() > 0) {
            inventory.setItem(
                0,
                this.rows - 1,
                new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.ARROW, ChatColor.RED + "Page #" + this.inventories.size())) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        InventoryUI.this.page--;

                        try {
                            InventoryUI.Inventory2D inventory2D = InventoryUI.this.inventories.get(InventoryUI.this.page);
                            if (inventory2D == null) {
                                InventoryUI.this.page++;
                            } else {
                                event.getWhoClicked().openInventory(InventoryUI.this.getCurrentPage());
                            }
                        } catch (IndexOutOfBoundsException var3) {
                            InventoryUI.this.page++;
                        }
                    }
                }
            );
            if (inventory.currentY == this.rows - 1 && inventory.currentX == -1) {
                inventory.currentX++;
            }
        }

        this.inventories.add(inventory);
    }

    public void setItem(int x, int y, InventoryUI.ClickableItem item) {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }

        InventoryUI.Inventory2D lastInventory = this.inventories.get(this.inventories.size() - 1);
        lastInventory.setItem(x - 1, y - 1, item);
    }

    public void setItem(int slot, InventoryUI.ClickableItem item) {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }

        InventoryUI.Inventory2D lastInventory = this.inventories.get(this.inventories.size() - 1);
        lastInventory.setItem(slot, item);
    }

    public void addItem(InventoryUI.ClickableItem item) {
        if (this.inventories.size() == 0) {
            this.createNewInventory();
        }

        InventoryUI.Inventory2D lastInventory = this.inventories.get(this.inventories.size() - 1);
        if (lastInventory.currentY == this.rows - 1 && lastInventory.currentX >= 7 - this.offset) {
            lastInventory.setItem(
                8,
                this.rows - 1,
                new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.ARROW, ChatColor.RED + "Page #" + (this.inventories.size() + 1))) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        InventoryUI.this.page++;

                        try {
                            InventoryUI.Inventory2D inventory2D = InventoryUI.this.inventories.get(InventoryUI.this.page);
                            if (inventory2D == null) {
                                InventoryUI.this.page--;
                            } else {
                                event.getWhoClicked().openInventory(InventoryUI.this.getCurrentPage());
                            }
                        } catch (IndexOutOfBoundsException var3) {
                            InventoryUI.this.page--;
                        }
                    }
                }
            );
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
        InventoryUI.Inventory2D inventory2D = this.inventories.get(this.page);
        this.setItem(slot, null);

        for (int i = slot + 1; i < this.getSize(); i++) {
            InventoryUI.ClickableItem item = this.getItem(i);
            this.setItem(i - 1, item);
            this.setItem(i, null);
        }

        if (inventory2D.currentX >= 0) {
            inventory2D.currentX--;
        } else if (inventory2D.currentY > 0) {
            inventory2D.currentY--;
            inventory2D.currentX = 7;
        }
    }

    public String getTitle() {
        return this.title;
    }

    public int getRowOffset() {
        return this.rowOffset;
    }

    public int getRows() {
        return this.rows;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getPage() {
        return this.page;
    }

    public List<InventoryUI.Inventory2D> getInventories() {
        return this.inventories;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public abstract static class AbstractClickableItem implements InventoryUI.ClickableItem {
        private final ItemStack defaultItemStack;
        private ItemStack itemStack;

        public AbstractClickableItem(ItemStack itemStack) {
            this.itemStack = itemStack;
            this.defaultItemStack = itemStack;
        }

        @Override
        public ItemStack getDefaultItemStack() {
            return this.defaultItemStack;
        }

        @Override
        public ItemStack getItemStack() {
            return this.itemStack;
        }

        @Override
        public void setItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
        }
    }

    public interface ClickableItem {
        void onClick(InventoryClickEvent var1);

        ItemStack getItemStack();

        void setItemStack(ItemStack var1);

        ItemStack getDefaultItemStack();
    }

    public static class EmptyClickableItem implements InventoryUI.ClickableItem {
        private final ItemStack defaultItemStack;
        private ItemStack itemStack;

        public EmptyClickableItem(ItemStack itemStack) {
            this.itemStack = itemStack;
            this.defaultItemStack = itemStack;
        }

        @Override
        public void onClick(InventoryClickEvent event) {
        }

        @Override
        public ItemStack getDefaultItemStack() {
            return this.defaultItemStack;
        }

        @Override
        public ItemStack getItemStack() {
            return this.itemStack;
        }

        @Override
        public void setItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
        }
    }

    public class Inventory2D {
        private final InventoryUI.ClickableItem[][] items;
        private final String title;
        private final int rows;
        private Inventory cachedInventory;
        private int currentX = -1;
        private int currentY;

        public Inventory2D(String title, int rows, int rowOffset) {
            this.currentY = rowOffset;
            this.title = title;
            this.rows = rows;
            this.items = new InventoryUI.ClickableItem[9][this.rows];
        }

        public void setItem(int x, int y, InventoryUI.ClickableItem clickableItem) {
            this.items[x][y] = clickableItem;
            if (this.cachedInventory != null) {
                int slot = y * 9 + x;
                this.cachedInventory.setItem(slot, clickableItem != null ? clickableItem.getItemStack() : null);
            }
        }

        public void setItem(int slot, InventoryUI.ClickableItem clickableItem) {
            int y = Math.abs(slot / 9);
            int x = -(y * 9 - slot);
            this.setItem(x, y, clickableItem);
        }

        public InventoryUI.ClickableItem getItem(int slot) {
            int y = Math.abs(slot / 9);
            int x = -(y * 9 - slot);
            if (this.items.length <= x) {
                return null;
            } else {
                InventoryUI.ClickableItem[] items = this.items[x];
                return items.length <= y ? null : items[y];
            }
        }

        public Inventory toInventory() {
            if (this.cachedInventory != null) {
                return this.cachedInventory;
            } else {
                Inventory inventory = Bukkit.getServer()
                    .createInventory(new InventoryUI.InventoryUIHolder(InventoryUI.this, this.title, this.rows * 9), this.rows * 9, this.title);

                for (int y = 0; y < this.rows; y++) {
                    for (int x = 0; x < 9; x++) {
                        int slot = y * 9 + x;
                        InventoryUI.ClickableItem item = this.items[x][y];
                        if (item != null) {
                            inventory.setItem(slot, item.getItemStack());
                        }
                    }
                }

                this.cachedInventory = inventory;
                return inventory;
            }
        }

        public InventoryUI.ClickableItem[][] getItems() {
            return this.items;
        }

        public String getTitle() {
            return this.title;
        }

        public int getRows() {
            return this.rows;
        }

        public Inventory getCachedInventory() {
            return this.cachedInventory;
        }

        public int getCurrentX() {
            return this.currentX;
        }

        public int getCurrentY() {
            return this.currentY;
        }
    }

    public static class InventoryUIHolder implements InventoryHolder {
        private InventoryUI inventoryUI;
        private String title;
        private int slots;

        private InventoryUIHolder(InventoryUI inventoryUI, String title, int slots) {
            this.inventoryUI = inventoryUI;
            this.title = title;
            this.slots = slots;
        }

        public Inventory getInventory() {
            return this.inventoryUI.getCurrentPage();
        }

        public InventoryUI getInventoryUI() {
            return this.inventoryUI;
        }

        public String getTitle() {
            return this.title;
        }

        public int getSlots() {
            return this.slots;
        }
    }
}
