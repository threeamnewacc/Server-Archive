// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GuiFiller implements GuiItem
{
    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7);
    }
}
