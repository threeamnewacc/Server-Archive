package net.badlion.shinyinventory.gui.interfaces.render.text;

import net.badlion.shinyinventory.gui.Interface;
import net.badlion.shinyinventory.gui.buttons.Button;
import net.badlion.shinyinventory.utils.ItemCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ShinyDialga45 on 9/27/2015.
 */
public class BadlionScrollingTextRenderInterface extends ScrollingTextRenderInterface {

    private int task;

    public BadlionScrollingTextRenderInterface(Player player, List<Button> buttons, int size, String title, Interface parent, String text) {
        super(player, buttons, size, title, parent, text);
    }

    @Override
    public HashMap<Character, ItemCreator> getLetterItems() {
        HashMap<Character, ItemCreator> letterItems = new HashMap<>();
        for (Letter letter : Letter.values()) {
            letterItems.put(letter.getLetter(), new ItemCreator(Material.STAINED_CLAY).setData(4));
        }
        return letterItems;
    }

}
