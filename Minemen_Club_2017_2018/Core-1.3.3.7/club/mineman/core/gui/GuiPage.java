// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class GuiPage
{
    private final Map<Integer, GuiItem> items;
    private final GuiFolder folder;
    
    public GuiPage(final GuiFolder folder) {
        this.folder = folder;
        this.items = new HashMap<Integer, GuiItem>();
    }
    
    public void updatePage() {
        this.folder.getInventory().clear();
        this.items.forEach((slot, item) -> this.folder.getInventory().setItem((int)slot, item.getItemStack()));
        this.folder.getInventory().getViewers().forEach(viewer -> ((Player)viewer).updateInventory());
    }
    
    public void fill() {
        for (int i = 0; i < this.folder.getSize(); ++i) {
            if (this.getItem(i) == null) {
                this.addItem(i, new GuiFiller());
            }
        }
    }
    
    public GuiItem getItem(final int slot) {
        return this.items.get(slot);
    }
    
    public void addItem(final int slot, final GuiItem item) {
        this.items.put(slot, item);
    }
    
    public Map<Integer, GuiItem> getItems() {
        return this.items;
    }
}
