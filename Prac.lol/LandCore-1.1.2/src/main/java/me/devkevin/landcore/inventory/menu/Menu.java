package me.devkevin.landcore.inventory.menu;

import me.devkevin.landcore.inventory.menu.action.Action;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Menu {
    @Getter
    private final Inventory inventory;
    private final Map<Integer, Action> actions = new HashMap<>();
    private final List<Menu> pages = new ArrayList<>();

    protected Menu(int rows, String name) {
        inventory = Bukkit.createInventory(null, 9 * rows, name);
    }

    protected void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    protected void setActionableItem(int slot, ItemStack item, Action action) {
        inventory.setItem(slot, item);
        actions.put(slot, action);
    }

    protected ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    public Action getAction(int slot) {
        return actions.get(slot);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public void addPage(Menu page) {
        pages.add(page);
    }

    public Menu getPage(int index) {
        return pages.get(index);
    }

    protected void clear() {
        actions.clear();
        pages.clear();
        inventory.clear();
    }

    public abstract void setup();

    public abstract void update();
}
