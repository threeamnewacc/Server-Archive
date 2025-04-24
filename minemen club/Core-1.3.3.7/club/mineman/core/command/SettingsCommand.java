// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import java.beans.ConstructorProperties;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import club.mineman.core.gui.GuiClickable;
import java.util.List;
import club.mineman.core.mineman.Mineman;
import club.mineman.core.gui.GuiItem;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import club.mineman.core.util.finalutil.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import club.mineman.core.gui.GuiPage;
import club.mineman.core.gui.GuiFolder;
import club.mineman.core.util.finalutil.CC;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import club.mineman.core.CorePlugin;
import org.bukkit.command.Command;

public class SettingsCommand extends Command
{
    private final CorePlugin plugin;
    
    public SettingsCommand(final CorePlugin plugin) {
        super("settings");
        this.plugin = plugin;
    }
    
    public boolean execute(final CommandSender commandSender, final String s, final String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        final Player player = (Player)commandSender;
        final Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        final GuiFolder folder = new GuiFolder(CC.GREEN + "Settings", 9);
        final GuiPage page = new GuiPage(folder);
        final List<ItemStack> items = new ArrayList<ItemStack>(Arrays.asList(ItemUtil.createItem(Material.BOOK_AND_QUILL, CC.GOLD + "Private Messages: " + (mineman.isCanSeeMessages() ? (CC.GREEN + "Enabled") : (CC.RED + "Disabled"))), ItemUtil.createItem(Material.PAPER, CC.GOLD + "Global Chat: " + (mineman.isChatEnabled() ? (CC.GREEN + "Enabled") : (CC.RED + "Disabled"))), ItemUtil.createItem(Material.EYE_OF_ENDER, CC.GOLD + "Vanish: " + (mineman.isVanishMode() ? (CC.GREEN + "Enabled") : (CC.RED + "Disabled")))));
        for (int i = 1; i <= 5; i += 2) {
            page.addItem(i, new SettingsClickable(items, this.plugin, player));
        }
        this.plugin.getSettingsManager().handleInventory(folder, page, player);
        page.fill();
        folder.setCurrentPage(page);
        folder.openGui(player);
        return true;
    }
    
    private class SettingsClickable implements GuiClickable
    {
        private final List<ItemStack> items;
        private final CorePlugin plugin;
        private final Player player;
        
        @Override
        public void onClick(final InventoryClickEvent event) {
            final ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) {
                return;
            }
            this.player.closeInventory();
            final Mineman mineman = this.plugin.getPlayerManager().getPlayer(this.player.getUniqueId());
            final String s;
            final String setting = s = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).split(":")[0];
            switch (s) {
                case "Private Messages": {
                    mineman.setCanSeeMessages(!mineman.isCanSeeMessages());
                    this.player.sendMessage(CC.PINK + setting + " " + CC.D_PURPLE + "have been " + CC.PINK + (mineman.isCanSeeMessages() ? "enabled" : "disabled"));
                    break;
                }
                case "Global Chat": {
                    mineman.setChatEnabled(!mineman.isChatEnabled());
                    this.player.sendMessage(CC.PINK + setting + " " + CC.D_PURPLE + "has been " + CC.PINK + (mineman.isChatEnabled() ? "enabled" : "disabled"));
                    break;
                }
                case "Vanish": {
                    this.player.performCommand("vanish");
                    break;
                }
            }
        }
        
        @Override
        public ItemStack getItemStack() {
            return this.items.remove(0);
        }
        
        @ConstructorProperties({ "items", "plugin", "player" })
        public SettingsClickable(final List<ItemStack> items, final CorePlugin plugin, final Player player) {
            this.items = items;
            this.plugin = plugin;
            this.player = player;
        }
    }
}
