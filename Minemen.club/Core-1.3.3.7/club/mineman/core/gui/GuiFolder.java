// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import club.mineman.core.CorePlugin;
import org.bukkit.inventory.Inventory;

public class GuiFolder
{
    private final Inventory inventory;
    private final String name;
    private final int size;
    private GuiPage currentPage;
    
    public GuiFolder(final String name, final int size) {
        this.name = name;
        this.size = size;
        this.inventory = CorePlugin.getInstance().getServer().createInventory((InventoryHolder)null, size, name);
        CorePlugin.getInstance().getFolders().add(this);
    }
    
    public void openGui(final Player player) {
        player.closeInventory();
        player.openInventory(this.inventory);
    }
    
    public void setCurrentPage(final GuiPage currentPage) {
        (this.currentPage = currentPage).updatePage();
    }
    
    public Inventory getInventory() {
        return this.inventory;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public GuiPage getCurrentPage() {
        return this.currentPage;
    }
}
