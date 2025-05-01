package net.badlion.shinyinventory.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Created by ShinyDialga45 on 7/19/2015.
 */
public interface InterfaceHolder extends InventoryHolder {

    Inventory getInventory();

    Interface getInterface();

}
