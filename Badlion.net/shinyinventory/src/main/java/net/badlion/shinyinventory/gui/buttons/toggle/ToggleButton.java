package net.badlion.shinyinventory.gui.buttons.toggle;

import net.badlion.shinyinventory.gui.buttons.Button;
import net.badlion.shinyinventory.utils.ItemCreator;

/**
 * Created by ShinyDialga45 on 4/10/2015.
 */
public class ToggleButton extends Button {

    private boolean state = false;

    public ToggleButton(ItemCreator itemCreator, boolean state) {
        super(itemCreator);
        setState(state);
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
