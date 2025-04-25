package us.zonix.practice.party.selection;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.zonix.practice.party.selection.archer.ArcherSelectMenu;
import us.zonix.practice.party.selection.bard.BardSelectMenu;
import us.zonix.practice.util.ItemBuilder;
import us.zonix.practice.util.inventory.InventoryUI;

public class ClassSelectionMenu {
    private final InventoryUI inventoryUI = new InventoryUI(ChatColor.GRAY + "Class Selection", true, 3);
    private final InventoryUI.ClickableItem clickableItem = new InventoryUI.ClickableItem() {
        private final ItemStack def = new ItemBuilder(Material.STAINED_GLASS_PANE)
            .name(ChatColor.translateAlternateColorCodes('&', "&c"))
            .durability(15)
            .build();
        private ItemStack itemStack = this.def.clone();

        @Override
        public void onClick(InventoryClickEvent event) {
            event.setCancelled(true);
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
            return this.def;
        }
    };

    public ClassSelectionMenu() {
        this.initializeMenu();
    }

    private void initializeMenu() {
        for (int i = 0; i < this.inventoryUI.getRows() * 9; i++) {
            this.inventoryUI.setItem(i, this.clickableItem);
        }

        this.inventoryUI
            .setItem(
                11,
                new InventoryUI.ClickableItem() {
                    private final ItemStack def = new ItemBuilder(Material.WOOL)
                        .name(ChatColor.GOLD.toString() + ChatColor.BOLD + "Select Bards")
                        .lore(ChatColor.translateAlternateColorCodes('&', "&7Click to open the &6Bard Selection Menu&7."))
                        .durability(4)
                        .build();
                    private ItemStack itemStack = def.clone();

                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (event.getWhoClicked() instanceof Player) {
                            new BardSelectMenu((Player)event.getWhoClicked()).open();
                            ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.CLICK, 1.0F, 1.0F);
                        }

                        event.setCancelled(true);
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
                        return this.def;
                    }
                }
            );
        this.inventoryUI
            .setItem(
                13,
                new InventoryUI.ClickableItem() {
                    private final ItemStack def = new ItemBuilder(Material.NETHER_STAR)
                        .name(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Class Selection")
                        .lore(ChatColor.translateAlternateColorCodes('&', "&7Select your party's &cArchers &7and &6Bards &7using this gui."))
                        .build();
                    private ItemStack itemStack = def.clone();

                    @Override
                    public void onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
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
                        return this.def;
                    }
                }
            );
        this.inventoryUI
            .setItem(
                15,
                new InventoryUI.ClickableItem() {
                    private final ItemStack def = new ItemBuilder(Material.WOOL)
                        .name(ChatColor.RED.toString() + ChatColor.BOLD + "Select Archers")
                        .lore(ChatColor.translateAlternateColorCodes('&', "&7Click to open the &cArcher Selection Menu&7."))
                        .durability(14)
                        .build();
                    private ItemStack itemStack = def.clone();

                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (event.getWhoClicked() instanceof Player) {
                            new ArcherSelectMenu((Player)event.getWhoClicked()).open();
                            ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.CLICK, 1.0F, 1.0F);
                        }

                        event.setCancelled(true);
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
                        return this.def;
                    }
                }
            );
    }

    public void open(Player player) {
        player.openInventory(this.inventoryUI.getCurrentPage());
    }
}
