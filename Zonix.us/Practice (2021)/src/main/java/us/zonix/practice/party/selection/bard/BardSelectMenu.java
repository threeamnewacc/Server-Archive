package us.zonix.practice.party.selection.bard;

import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import us.zonix.practice.Practice;
import us.zonix.practice.party.Party;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.util.ItemBuilder;
import us.zonix.practice.util.inventory.InventoryUI;

public class BardSelectMenu {
    private static final InventoryUI.ClickableItem GO_BACK_CLICKABLE = new InventoryUI.ClickableItem() {
        private final ItemStack def = new ItemBuilder(Material.FIREBALL)
            .name(ChatColor.translateAlternateColorCodes('&', "&cGo Back"))
            .lore(ChatColor.translateAlternateColorCodes('&', "&7Click to go back to the Class Selection Menu."))
            .build();
        private ItemStack itemStack = this.def.clone();

        @Override
        public void onClick(InventoryClickEvent event) {
            ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.CLICK, 1.0F, 1.0F);
            Party.CLASS_SELECTION_MENU.open((Player)event.getWhoClicked());
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
    private static final InventoryUI.ClickableItem YELLOW_GLASS_CLICKABLE = new InventoryUI.ClickableItem() {
        private final ItemStack def = new ItemBuilder(Material.STAINED_GLASS_PANE)
            .name(ChatColor.translateAlternateColorCodes('&', "&c"))
            .durability(4)
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
    private final Player player;
    private final Party party;
    private final InventoryUI inventoryUI;

    public BardSelectMenu(Player player) {
        this.player = player;
        this.party = ((Practice)JavaPlugin.getPlugin(Practice.class)).getPartyManager().getParty(player.getUniqueId());
        this.inventoryUI = new InventoryUI(ChatColor.GRAY + "Bard Selection", (int)Math.ceil((double)((float)this.party.getMembers().size() / 9.0F)) + 1);
    }

    public void open() {
        if (this.player != null && this.party != null) {
            this.refreshItems();
            this.player.closeInventory();
            this.player.openInventory(this.inventoryUI.getCurrentPage());
        }
    }

    private void refreshItems() {
        for (int i = 0; i < 9 * this.inventoryUI.getRows(); i++) {
            this.inventoryUI.setItem(i, YELLOW_GLASS_CLICKABLE);
        }

        for (int i = 0; i < this.party.getMembers().size(); i++) {
            this.inventoryUI.setItem(i, this.getItemStack(Bukkit.getPlayer(this.party.getMembers().get(i))));
        }

        this.inventoryUI.setItem(this.inventoryUI.getRows() * 9 - 1, GO_BACK_CLICKABLE);
    }

    private InventoryUI.ClickableItem getItemStack(final Player player) {
        final AtomicBoolean bard = new AtomicBoolean(this.party.getBards().contains(player.getUniqueId()));
        final PlayerData playerData = ((Practice)JavaPlugin.getPlugin(Practice.class)).getPlayerManager().getPlayerData(player.getUniqueId());
        return new InventoryUI.ClickableItem() {
            private final ItemStack def = new ItemBuilder(bard.get() ? Material.GOLD_HELMET : Material.DIAMOND_HELMET)
                .name(ChatColor.GOLD.toString() + player.getName() + (bard.get() ? ChatColor.GOLD + " (Bard)" : ChatColor.AQUA + " (Diamond)"))
                .lore(
                    ChatColor.translateAlternateColorCodes(
                        '&', "&7Click to set &7" + player.getName() + "'s PvP Class to " + (bard.get() ? "&bDiamond" : "&6Bard") + "&7."
                    )
                )
                .lore(
                    ChatColor.translateAlternateColorCodes(
                        '&',
                        "&6"
                            + player.getName()
                            + " &7has played &6Bard &7"
                            + playerData.getPlayedBard()
                            + " time"
                            + (playerData.getPlayedBard() == 1 ? "" : "s")
                            + "&7."
                    )
                )
                .build();
            private ItemStack itemStack = def.clone();

            @Override
            public void onClick(InventoryClickEvent event) {
                event.setCancelled(true);
                if (bard.get()) {
                    BardSelectMenu.this.party.getBards().remove(player.getUniqueId());
                } else {
                    Player sender = (Player)event.getWhoClicked();
                    if (BardSelectMenu.this.party.getBards().size() >= BardSelectMenu.this.party.getMaxBards()) {
                        sender.sendMessage(
                            ChatColor.RED
                                + String.format(
                                    "Your party has already reached the limit of %s bard" + (BardSelectMenu.this.party.getMaxBards() == 1 ? "" : "s") + ".",
                                    BardSelectMenu.this.party.getMaxBards()
                                )
                        );
                        return;
                    }

                    if (BardSelectMenu.this.party.getBards().contains(player.getUniqueId())) {
                        sender.sendMessage(ChatColor.RED + String.format("%s's role is already bard.", player.getName()));
                        return;
                    }

                    BardSelectMenu.this.party.addBard(player);
                    sender.sendMessage(ChatColor.GREEN + player.getName() + "'s role is now bard.");
                }

                BardSelectMenu.this.refreshItems();
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
    }
}
