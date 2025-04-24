// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.settings;

import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.Iterator;
import org.bukkit.entity.Player;
import club.mineman.core.gui.GuiPage;
import club.mineman.core.gui.GuiFolder;
import club.mineman.core.CorePlugin;
import java.util.Set;

public class SettingsManager
{
    private final Set<SettingsInventoryHandler> handlers;
    private final CorePlugin plugin;
    
    public void addHandler(final SettingsInventoryHandler handler) {
        this.handlers.add(handler);
    }
    
    public void removeHandler(final SettingsInventoryHandler handler) {
        this.handlers.remove(handler);
    }
    
    public void handleInventory(final GuiFolder folder, final GuiPage page, final Player player) {
        for (final SettingsInventoryHandler handler : this.handlers) {
            handler.handleInventory(folder, page, player);
        }
    }
    
    @ConstructorProperties({ "plugin" })
    public SettingsManager(final CorePlugin plugin) {
        this.handlers = new HashSet<SettingsInventoryHandler>();
        this.plugin = plugin;
    }
}
