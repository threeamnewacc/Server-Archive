package us.zonix.core.server;

import us.zonix.core.CorePlugin;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.zonix.core.util.CC;
import us.zonix.core.util.ItemUtil;
import us.zonix.core.util.inventory.InventoryUI;

public class ServerManager {

    private final CorePlugin plugin = CorePlugin.getInstance();

    @Getter
    private final InventoryUI serverSelector = new InventoryUI(CC.RED + CC.BOLD + "Server Selector", true, 5);

    @Getter
    private final InventoryUI sgSelector = new InventoryUI(CC.RED + CC.BOLD + "Survival Games", true, 3);

    public ServerManager() {
        this.setupServerSelector();
        this.setupSurvivalGamesSelector();
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::updateServerSelector, 20L, 20L);
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::updateSurvivalGamesSelector, 20L, 20L);
    }

    private void setupServerSelector() {
        this.serverSelector.setItem(10, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.POTION, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Practice " + ChatColor.GRAY + "|" + ChatColor.RED.toString() + ChatColor.BOLD + " US", 1, (short) 8197)) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.performCommand("joinqueue " + "practice-us");
                player.closeInventory();
            }
        });

        this.serverSelector.setItem(12, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.POTION, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Practice " + ChatColor.GRAY + "|" + ChatColor.RED.toString() + ChatColor.BOLD + " EU", 1, (short) 8197)) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.performCommand("joinqueue " + "practice-eu");
                player.closeInventory();
            }
        });

        this.serverSelector.setItem(14, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.POTION, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Practice " + ChatColor.GRAY + "|" + ChatColor.RED.toString() + ChatColor.BOLD + " SA", 1, (short) 8197)) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.performCommand("joinqueue " + "practice-sa");
                player.closeInventory();
            }
        });

        this.serverSelector.setItem(16, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.POTION, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Practice " + ChatColor.GRAY + "|" + ChatColor.RED.toString() + ChatColor.BOLD + " AS", 1, (short) 8197)) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.sendMessage(ChatColor.RED + "Please connect using as.zonix.us");
                player.closeInventory();
            }
        });

        this.serverSelector.setItem(29, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.CHEST, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Survival Games")) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.closeInventory();
                player.performCommand("sgmenu");
            }
        });

        this.serverSelector.setItem(31, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.DIAMOND_SWORD, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "HCF")) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.performCommand("joinqueue " + "hcf-us");
                player.closeInventory();
            }
        });

        this.serverSelector.setItem(33, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.NETHER_STAR, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "KitMap")) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.performCommand("joinqueue " + "kitmap-us");
                player.closeInventory();
            }
        });

        int[] slots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 13, 15, 17, 18, 19, 20, 21, 22, 23, 24, 25,
                26, 27, 28, 30, 32, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};

        for (int i : slots) {
            this.serverSelector.setItem(i, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.STAINED_GLASS_PANE, " ", 1, (short) 0)) {
                @Override
                public void onClick(InventoryClickEvent event) {
                }
            });
        }
    }

    private void setupSurvivalGamesSelector() {
        this.sgSelector.setItem(10, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.CHEST, CC.RED + CC.BOLD + "SG-01")) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.performCommand("joinqueue sg-01");
                player.closeInventory();
            }
        });

        this.sgSelector.setItem(12, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.CHEST, CC.RED + CC.BOLD + "SG-02")) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.performCommand("joinqueue sg-02");
                player.closeInventory();
            }
        });

        this.sgSelector.setItem(14, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.CHEST, CC.RED + CC.BOLD + "SG-03")) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.performCommand("joinqueue sg-03");
                player.closeInventory();
            }
        });

        this.sgSelector.setItem(16, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.CHEST, CC.RED + CC.BOLD + "SG-04")) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.performCommand("joinqueue sg-04");
                player.closeInventory();
            }
        });

        int[] slots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 13, 15, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};

        for (int i : slots) {
            this.sgSelector.setItem(i, new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.STAINED_GLASS_PANE, " ", 1, (short) 0)) {
                @Override
                public void onClick(InventoryClickEvent event) {
                }
            });

        }
    }

    private void updateSurvivalGamesSelector() {
        for (int i = 0; i < 24; i++) {
            InventoryUI.ClickableItem item = this.sgSelector.getItem(i);

            if (item == null) {
                continue;
            }

            if (item.getItemStack().getType() == Material.CHEST) {
                String gameId = ChatColor.stripColor(item.getItemStack().getItemMeta().getDisplayName()).split("-")[1];

                item.setItemStack(this.updateQueueLore(item.getItemStack(), "sg-" + gameId.toLowerCase()));

                this.sgSelector.setItem(i, item);
            }
        }
    }

    private void updateServerSelector() {
        for (int i = 0; i < 33; i++) {
            InventoryUI.ClickableItem item = this.serverSelector.getItem(i);

            if (item == null) {
                continue;
            }

            if (item.getItemStack().getType() == Material.POTION) {
                String serverName = ChatColor.stripColor(item.getItemStack().getItemMeta().getDisplayName()).split(" ")[2];

                item.setItemStack(this.updateQueueLore(item.getItemStack(), "practice-" + serverName.toLowerCase()));

                this.serverSelector.setItem(i, item);
            }

            if (item.getItemStack().getType() == Material.DIAMOND_SWORD) {
                item.setItemStack(this.updateQueueLore(item.getItemStack(), "hcf-us"));

                this.serverSelector.setItem(i, item);
            }

            if (item.getItemStack().getType() == Material.ENDER_CHEST) {
                item.setItemStack(this.updateQueueLore(item.getItemStack(), "kitmap-us"));

                this.serverSelector.setItem(i, item);
            }
        }
    }

    private ItemStack updateQueueLore(ItemStack itemStack, String server) {
        if (itemStack == null) {
            return null;
        }

        if (server.equalsIgnoreCase("practice-as")) {
            return ItemUtil.reloreItem(itemStack,
                    CC.DARK_GRAY + CC.STRIKE_THROUGH + "-----------------",
                    CC.RED + CC.BOLD + "* " + CC.RESET + "Players: " + CC.GRAY + "(" + this.plugin.getRedisManager().getAsiaPlayerCount() + "/" + 500 + ")",
                    CC.RED + CC.BOLD + "* " + CC.RESET + "Status: " + CC.GREEN + "Online",
                    CC.DARK_GRAY + CC.STRIKE_THROUGH + "-----------------");
        }

        ServerData serverData = this.plugin.getRedisManager().getServerDataByName(server);

        if (serverData == null) {
            return ItemUtil.reloreItem(itemStack,
                    CC.DARK_GRAY + CC.STRIKE_THROUGH + "-----------------",
                    CC.RED + CC.BOLD + "* " + CC.RESET + "Players: " + CC.GRAY + "(" + 0 + "/" + 0 + ")",
                    CC.RED + CC.BOLD + "* " + CC.RESET + "Status: " + CC.RED + "Offline",
                    CC.DARK_GRAY + CC.STRIKE_THROUGH + "-----------------");
        }

        return ItemUtil.reloreItem(itemStack,
                CC.DARK_GRAY + CC.STRIKE_THROUGH + "-----------------",
                CC.RED + CC.BOLD + "* " + CC.RESET + "Players: " + CC.GRAY + "(" + serverData.getOnlinePlayers() + "/" + serverData.getMaxPlayers() + ")",
                CC.RED + CC.BOLD + "* " + CC.RESET + "Status: " + serverData.getStatus(),
                CC.DARK_GRAY + CC.STRIKE_THROUGH + "-----------------");
    }

}
