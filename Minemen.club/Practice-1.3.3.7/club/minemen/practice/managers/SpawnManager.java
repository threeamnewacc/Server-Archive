// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.managers;

import org.bukkit.configuration.file.FileConfiguration;
import club.minemen.core.util.CustomLocation;
import club.minemen.practice.Practice;

public class SpawnManager
{
    private final Practice plugin;
    private CustomLocation spawnLocation;
    private CustomLocation spawnMin;
    private CustomLocation spawnMax;
    private CustomLocation editorLocation;
    private CustomLocation editorMin;
    private CustomLocation editorMax;
    
    public SpawnManager() {
        this.plugin = Practice.getInstance();
        this.loadConfig();
    }
    
    private void loadConfig() {
        final FileConfiguration config = this.plugin.getMainConfig().getConfig();
        if (config.contains("spawnLocation")) {
            this.spawnLocation = CustomLocation.stringToLocation(config.getString("spawnLocation"));
            this.spawnMin = CustomLocation.stringToLocation(config.getString("spawnMin"));
            this.spawnMax = CustomLocation.stringToLocation(config.getString("spawnMax"));
            this.editorLocation = CustomLocation.stringToLocation(config.getString("editorLocation"));
            this.editorMin = CustomLocation.stringToLocation(config.getString("editorMin"));
            this.editorMax = CustomLocation.stringToLocation(config.getString("editorMax"));
        }
    }
    
    public void saveConfig() {
        final FileConfiguration config = this.plugin.getMainConfig().getConfig();
        config.set("spawnLocation", (Object)CustomLocation.locationToString(this.spawnLocation));
        config.set("spawnMin", (Object)CustomLocation.locationToString(this.spawnMin));
        config.set("spawnMax", (Object)CustomLocation.locationToString(this.spawnMax));
        config.set("editorLocation", (Object)CustomLocation.locationToString(this.editorLocation));
        config.set("editorMin", (Object)CustomLocation.locationToString(this.editorMin));
        config.set("editorMax", (Object)CustomLocation.locationToString(this.editorMax));
        this.plugin.getMainConfig().save();
    }
    
    public Practice getPlugin() {
        return this.plugin;
    }
    
    public CustomLocation getSpawnLocation() {
        return this.spawnLocation;
    }
    
    public CustomLocation getSpawnMin() {
        return this.spawnMin;
    }
    
    public CustomLocation getSpawnMax() {
        return this.spawnMax;
    }
    
    public CustomLocation getEditorLocation() {
        return this.editorLocation;
    }
    
    public CustomLocation getEditorMin() {
        return this.editorMin;
    }
    
    public CustomLocation getEditorMax() {
        return this.editorMax;
    }
    
    public void setSpawnLocation(final CustomLocation spawnLocation) {
        this.spawnLocation = spawnLocation;
    }
    
    public void setSpawnMin(final CustomLocation spawnMin) {
        this.spawnMin = spawnMin;
    }
    
    public void setSpawnMax(final CustomLocation spawnMax) {
        this.spawnMax = spawnMax;
    }
    
    public void setEditorLocation(final CustomLocation editorLocation) {
        this.editorLocation = editorLocation;
    }
    
    public void setEditorMin(final CustomLocation editorMin) {
        this.editorMin = editorMin;
    }
    
    public void setEditorMax(final CustomLocation editorMax) {
        this.editorMax = editorMax;
    }
}
