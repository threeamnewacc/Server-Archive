package me.devkevin.landcore.managers;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.inventory.menu.Menu;
import me.devkevin.landcore.inventory.menu.impl.ReportMenu;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class MenuManager {
    private final Map<Class<? extends Menu>, Menu> menus = new HashMap<>();

    public MenuManager(LandCore plugin) {
        registerMenus(
                new ReportMenu(plugin)
        );
    }

    public Menu getMenu(Class<? extends Menu> clazz) {
        return menus.get(clazz);
    }

    public Menu getMatchingMenu(Inventory other) {
        for (Menu menu : menus.values()) {
            if (menu.getInventory().equals(other)) {
                return menu;
            }
        }

        return null;
    }

    public void registerMenus(Menu... menus) {
        for (Menu menu : menus) {
            menu.setup();
            menu.update();
            this.menus.put(menu.getClass(), menu);
        }
    }
}