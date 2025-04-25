package cc.patrone.practice.manager.impl;

import cc.patrone.practice.manager.Manager;
import cc.patrone.practice.manager.ManagerHandler;
import cc.patrone.practice.util.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
public class SettingsManager extends Manager {

    private Location spawn;
    private Location editor;

    public SettingsManager(ManagerHandler managerHandler) {
        super(managerHandler);
        fetchLocations();
    }

    // if you're reading this, are you stupid or are you dumb?

    private void fetchLocations() {
        if (this.managerHandler.getPlugin().getConfig().get("spawn") != null) spawn = LocationUtil.getLocationFromString(this.managerHandler.getPlugin().getConfig().getString("spawn"));
        if (this.managerHandler.getPlugin().getConfig().get("editor") != null) editor = LocationUtil.getLocationFromString(this.managerHandler.getPlugin().getConfig().getString("editor"));
    }

    public void save() {
        if (spawn != null) this.managerHandler.getPlugin().getConfig().set("spawn", LocationUtil.getStringFromLocation(spawn));
        if (editor != null) this.managerHandler.getPlugin().getConfig().set("editor", LocationUtil.getStringFromLocation(editor));
        this.managerHandler.getPlugin().saveConfig();
    }

}
