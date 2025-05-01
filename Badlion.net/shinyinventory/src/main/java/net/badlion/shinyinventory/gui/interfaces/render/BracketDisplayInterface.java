package net.badlion.shinyinventory.gui.interfaces.render;

import net.badlion.shinyinventory.gui.Interface;
import net.badlion.shinyinventory.gui.buttons.Button;
import net.badlion.shinyinventory.utils.ItemCreator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShinyDialga45 on 9/25/2015.
 */
public class BracketDisplayInterface extends ChestRenderInterface {

    public BracketDisplayInterface(Player player, List<Button> buttons, int size, String title, Interface parent) {
        super(player, buttons, size, title, parent);
        updateButtons();
    }

    @Override
    public void updateButtons() {
        List<Button> buttons = new ArrayList<>();
        List<Coordinate> coordinates = new ArrayList<>();
        /*int length = 0;
        int line = 0;
        for (int i = 3; i >= 0; i--) {
            length = length + ((i+1)/4);
            for (int x = 1; x <= i*i; x++) {
                coordinates.add(new Coordinate(length + x, line));
            }
            line++;
        }*/
        coordinates.add(new Coordinate(0, 0));
        coordinates.add(new Coordinate(2, 0));
        coordinates.add(new Coordinate(4, 0));
        coordinates.add(new Coordinate(6, 0));
        coordinates.add(new Coordinate(8, 0));
        coordinates.add(new Coordinate(10, 0));
        coordinates.add(new Coordinate(12, 0));
        coordinates.add(new Coordinate(14, 0));
        coordinates.add(new Coordinate(1, 2));
        coordinates.add(new Coordinate(5, 2));
        coordinates.add(new Coordinate(9, 2));
        coordinates.add(new Coordinate(13, 2));
        coordinates.add(new Coordinate(3, 4));
        coordinates.add(new Coordinate(11, 4));
        coordinates.add(new Coordinate(7, 6));

        for (Coordinate coordinate : coordinates) {
            Coordinate newCoordinate = new Coordinate(Math.round(coordinate.getX()), Math.round(coordinate.getY()));
            int renderSlot = getRenderSlot(newCoordinate);
            if (renderSlot >= 0) {
                ItemCreator itemCreator = new ItemCreator(Material.SKULL_ITEM)
                        .setSkullOwner("ShinyDialga")
                        .setData(3)
                        .setName(ChatColor.GOLD + "ShinyDialga");
                Button button = new Button(itemCreator, renderSlot);
                buttons.add(button);
            }
        }
        Button up = new Button(new ItemCreator(Material.ARROW)
                .setName(ChatColor.GREEN + "Move Up"), 7) {
            @Override
            public void function(Player player) {
                setOrigin(new Coordinate(getOrigin().getX(), getOrigin().getY() + 1));
                updateButtons();
            }
        };
        buttons.add(up);
        Button down = new Button(new ItemCreator(Material.ARROW)
                .setName(ChatColor.GREEN + "Move Down"), 25) {
            @Override
            public void function(Player player) {
                setOrigin(new Coordinate(getOrigin().getX(), getOrigin().getY() - 1));
                updateButtons();
            }
        };
        buttons.add(down);
        Button left = new Button(new ItemCreator(Material.ARROW)
                .setName(ChatColor.GREEN + "Move Left"), 15) {
            @Override
            public void function(Player player) {
                setOrigin(new Coordinate(getOrigin().getX() - 1, getOrigin().getY()));
                updateButtons();
            }
        };
        buttons.add(left);
        Button right = new Button(new ItemCreator(Material.ARROW)
                .setName(ChatColor.GREEN + "Move Right"), 17) {
            @Override
            public void function(Player player) {
                setOrigin(new Coordinate(getOrigin().getX() + 1, getOrigin().getY()));
                updateButtons();
            }
        };
        buttons.add(right);
        Button compass = new Button(new ItemCreator(Material.COMPASS)
                .setName(ChatColor.GREEN + "Use these to move!"), 16);
        buttons.add(compass);
        setButtons(buttons);
        updateInventory();
    }
}
