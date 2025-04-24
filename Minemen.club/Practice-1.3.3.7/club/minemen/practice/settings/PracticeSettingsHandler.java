// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.settings;

import java.util.Arrays;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.ChatColor;
import club.minemen.core.rank.Rank;
import club.minemen.practice.player.PlayerData;
import club.minemen.core.mineman.Mineman;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import club.minemen.core.util.finalutil.ItemUtil;
import club.minemen.core.util.finalutil.CC;
import org.bukkit.Material;
import club.minemen.core.CorePlugin;
import org.bukkit.entity.Player;
import club.minemen.core.inventory.InventoryUI;
import club.minemen.practice.Practice;
import java.util.List;
import club.minemen.core.settings.SettingsHandler;

public class PracticeSettingsHandler implements SettingsHandler
{
    private static final List<Integer> PING_RANGES;
    private static final List<Integer> ELO_RANGES;
    private final Practice plugin;
    
    public PracticeSettingsHandler() {
        this.plugin = Practice.getInstance();
    }
    
    public void onCreateSettings(final InventoryUI inventoryUI, final Player player) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
        inventoryUI.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.REDSTONE, CC.PRIMARY + "Allowing Spectators: " + (playerData.isAllowingSpectators() ? (CC.GREEN + "Enabled") : (CC.RED + "Disabled")))) {
            public void onClick(final InventoryClickEvent inventoryClickEvent) {
                player.performCommand("tsp");
                player.closeInventory();
            }
        });
        inventoryUI.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.DIAMOND_SWORD, CC.PRIMARY + "Allowing Duels: " + (playerData.isAcceptingDuels() ? (CC.GREEN + "Enabled") : (CC.RED + "Disabled")))) {
            public void onClick(final InventoryClickEvent inventoryClickEvent) {
                player.performCommand("td");
                player.closeInventory();
            }
        });
        inventoryUI.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.MAP, CC.PRIMARY + "Sidebar Visibility: " + (playerData.isScoreboardEnabled() ? (CC.GREEN + "Enabled") : (CC.RED + "Disabled")))) {
            public void onClick(final InventoryClickEvent inventoryClickEvent) {
                player.performCommand("tsb");
                player.closeInventory();
            }
        });
        inventoryUI.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.ENCHANTED_BOOK, CC.PRIMARY + "Matchmaking Settings")) {
            public void onClick(final InventoryClickEvent inventoryClickEvent) {
                if (!mineman.hasRank(Rank.CLUBBER)) {
                    player.closeInventory();
                    player.sendMessage(CC.RED + "Matchmaking Settings are for Clubber rank and higher.");
                    return;
                }
                player.closeInventory();
                PracticeSettingsHandler.this.openMatchmakingSettings(player, playerData, mineman);
            }
        });
    }
    
    private void openMatchmakingSettings(final Player player, final PlayerData playerData, final Mineman mineman) {
        final InventoryUI matchmakingUI = new InventoryUI(CC.PRIMARY + "Matchmaking Settings", false, 1);
        matchmakingUI.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.STICK, CC.PRIMARY + "Ping Range: " + CC.SECONDARY + ((playerData.getPingRange() == -1) ? "Unrestricted" : Integer.valueOf(playerData.getPingRange())))) {
            public void onClick(final InventoryClickEvent event) {
                if (!mineman.hasRank(Rank.PARTYMAN)) {
                    player.sendMessage(CC.RED + "Ping-based Matchmaking Settings are for Partyman rank and higher.");
                    player.closeInventory();
                    return;
                }
                final String[] args = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).split(":");
                final int range = PracticeSettingsHandler.this.handleRangeClick(event.getClick(), PracticeSettingsHandler.PING_RANGES, PracticeSettingsHandler.this.parseOrDefault(args[1], -1));
                playerData.setPingRange(range);
                event.getClickedInventory().setItem(0, ItemUtil.createItem(Material.STICK, CC.PRIMARY + "Ping Range: " + CC.SECONDARY + ((playerData.getPingRange() == -1) ? "Unrestricted" : Integer.valueOf(playerData.getPingRange()))));
            }
        });
        matchmakingUI.addItem((InventoryUI.ClickableItem)new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.BLAZE_ROD, CC.PRIMARY + "ELO Range: " + CC.SECONDARY + ((playerData.getEloRange() == -1) ? "Unrestricted" : Integer.valueOf(playerData.getEloRange())))) {
            public void onClick(final InventoryClickEvent event) {
                final String[] args = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).split(":");
                final int range = PracticeSettingsHandler.this.handleRangeClick(event.getClick(), PracticeSettingsHandler.ELO_RANGES, PracticeSettingsHandler.this.parseOrDefault(args[1], -1));
                playerData.setEloRange(range);
                event.getClickedInventory().setItem(1, ItemUtil.createItem(Material.BLAZE_ROD, CC.PRIMARY + "ELO Range: " + CC.SECONDARY + ((playerData.getEloRange() == -1) ? "Unrestricted" : Integer.valueOf(playerData.getEloRange()))));
            }
        });
        player.openInventory(matchmakingUI.getCurrentPage());
    }
    
    private int handleRangeClick(final ClickType clickType, final List<Integer> ranges, int current) {
        final int min = ranges.get(0);
        final int max = ranges.get(ranges.size() - 1);
        if (clickType == ClickType.LEFT) {
            if (current == max) {
                current = min;
            }
            else {
                current = ranges.get(ranges.indexOf(current) + 1);
            }
        }
        else if (clickType == ClickType.RIGHT) {
            if (current == min) {
                current = max;
            }
            else {
                current = ranges.get(ranges.indexOf(current) - 1);
            }
        }
        return current;
    }
    
    private int parseOrDefault(final String string, final int def) {
        try {
            return Integer.parseInt(string.replace(" ", ""));
        }
        catch (final NumberFormatException e) {
            return def;
        }
    }
    
    static {
        PING_RANGES = Arrays.asList(50, 75, 100, 125, 150, 200, 250, 300, -1);
        ELO_RANGES = Arrays.asList(250, 350, 500, 600, 750, 1000, -1);
    }
}
