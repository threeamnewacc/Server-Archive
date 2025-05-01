package net.badlion.shinyinventory.gui;

import org.bukkit.inventory.Inventory;

/**
 * Created by ShinyDialga45 on 7/19/2015.
 */
public class SimpleInterfaceHolder implements InterfaceHolder {

    private Inventory inventory;
    private Interface gui;

    public SimpleInterfaceHolder(Inventory inventory, Interface gui) {
        this.inventory = inventory;
        this.gui = gui;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public Interface getInterface() {
        return this.gui;
    }

}
