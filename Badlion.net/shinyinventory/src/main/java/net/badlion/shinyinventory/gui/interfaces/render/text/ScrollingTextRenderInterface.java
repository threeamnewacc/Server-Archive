package net.badlion.shinyinventory.gui.interfaces.render.text;

import net.badlion.shinyinventory.ShinyInventory;
import net.badlion.shinyinventory.gui.Interface;
import net.badlion.shinyinventory.gui.buttons.Button;
import net.badlion.shinyinventory.gui.interfaces.render.Coordinate;
import net.badlion.shinyinventory.utils.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ShinyDialga45 on 9/27/2015.
 */
public class ScrollingTextRenderInterface extends GridTextRenderInterface {

    private int task;

    public ScrollingTextRenderInterface(Player player, List<Button> buttons, int size, String title, Interface parent, String text) {
        super(player, buttons, size, title, parent, text);
        setOrigin(new Coordinate(-6, 0));
        render();
    }

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }

    public void render() {
        final int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(ShinyInventory.getInstance(), new Runnable() {
            @Override
            public void run() {
                setOrigin(new Coordinate(getOrigin().getX() + 1, getOrigin().getY()));
                updateButtons();
                checkTask();
            }
        }, 4L, 2L);
        setTask(task);
    }

    public void checkTask() {
        if (getOrigin().getX() > Letter.getLength(getText()) - 1) {
            Bukkit.getScheduler().cancelTask(getTask());
            if (getPlayer().getOpenInventory().getTopInventory().equals(getInventory())) {
                //getPlayer().openInventory(getNextInventory().getInventory());
            }
        }
    }

    public Interface getNextInventory() {
        return null;
    }

    @Override
    public void updateButtons() {
        List<Button> buttons = new ArrayList<>();
        List<Letter> letters = new ArrayList<>();
        for (char c : getText().toCharArray()) {
            Letter letter = Letter.getLetter(c);
            letters.add(letter);
        }
        double totalX = 0;
        for (Letter letter : letters) {
            for (final Coordinate coordinate : letter.getCoordinates().keySet()) {
                Coordinate newCoordinate = new Coordinate(coordinate.getX(), coordinate.getY());
                newCoordinate.setX(coordinate.getX() + totalX);
                int renderSlot = getRenderSlot(newCoordinate);
                if (renderSlot >= 0) {
                    ItemCreator itemCreator = letter.getCoordinates().get(coordinate) != null ? letter.getCoordinates().get(coordinate) : getLetterItems().get(letter.getLetter());
                    itemCreator.setName(newCoordinate.getX() + ", " + newCoordinate.getY());
                    Button button = new Button(itemCreator, renderSlot);
                    buttons.add(button);
                }
            }
            totalX = totalX + letter.getLength() + 1;
        }
        setButtons(buttons);
        updateInventory();
    }

    public HashMap<Character, ItemCreator> getLetterItems() {
        HashMap<Character, ItemCreator> letterItems = new HashMap<>();
        for (Letter letter : Letter.values()) {
            letterItems.put(letter.getLetter(), new ItemCreator(Material.WOOL));
        }
        return letterItems;
    }

}
