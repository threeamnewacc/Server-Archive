package net.badlion.shinyinventory.gui.interfaces;

import net.badlion.shinyinventory.gui.Interface;
import net.badlion.shinyinventory.gui.SimpleInterfaceHolder;
import net.badlion.shinyinventory.gui.buttons.Button;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * Created by ShinyDialga on 9/20/2015.
 */
public class HopperInterface extends Interface {

    private int size;
    private Interface parent;
    private Inventory inventory;
    private String title;

    public HopperInterface(Player player, List<Button> buttons, String title, Interface parent) {
        super(player, buttons);
        setTitle(title);
        setParent(parent);
        this.inventory = Bukkit.createInventory(new SimpleInterfaceHolder(inventory, this), InventoryType.HOPPER, getTitle());
        /*//this.inventory = player.getInventory();
        //inventory = Bukkit.createInventory(new SimpleInterfaceHolder(inventory, this), InventoryType.valueOf(args), getTitle());
        //setInventory(new InterfaceInventory(this, inventory));
        updateButtons();
        updateInventory();*/
    }

    public void setTitle(String title) {
        int titleSize = 32;
        this.title = title.length() > titleSize ? title.substring(0, titleSize - 1) : title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setParent(Interface parent) {
        this.parent = parent;
    }

    public Interface getParent() {
        return this.parent;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

}
