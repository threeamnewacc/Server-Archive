package net.badlion.shinyinventory.gui.interfaces;

import net.badlion.shinyinventory.gui.Interface;
import org.bukkit.inventory.Inventory;

public class InterfaceInventory {

    private Interface gui;
    private Inventory inventory;

    public InterfaceInventory(Interface gui, Inventory inventory) {
        //InterfaceManager.registerInventory(this);
        this.gui = gui;
        this.inventory = inventory;
    }

    public Interface getInterface() {
        return this.gui;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

}
