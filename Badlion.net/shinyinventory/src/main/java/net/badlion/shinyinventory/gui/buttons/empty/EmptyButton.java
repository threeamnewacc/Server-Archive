package net.badlion.shinyinventory.gui.buttons.empty;

import net.badlion.shinyinventory.gui.buttons.Button;
import net.badlion.shinyinventory.utils.ItemCreator;
import org.bukkit.Material;

/**
 * Created by ShinyDialga45 on 4/10/2015.
 */
public class EmptyButton extends Button {

    public EmptyButton(int slot) {
        super(new ItemCreator(Material.AIR), slot);
    }

}
