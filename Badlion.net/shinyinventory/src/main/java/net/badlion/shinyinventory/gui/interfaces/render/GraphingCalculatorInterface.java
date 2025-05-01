package net.badlion.shinyinventory.gui.interfaces.render;

import net.badlion.shinyinventory.gui.Interface;
import net.badlion.shinyinventory.gui.buttons.Button;
import net.badlion.shinyinventory.utils.ItemCreator;
import de.congrace.exp4j.ExpressionBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShinyDialga45 on 9/25/2015.
 */
public class GraphingCalculatorInterface extends ChestRenderInterface {

    private String formula;
    private double zoom = 1;

    public GraphingCalculatorInterface(Player player, List<Button> buttons, int size, String title, Interface parent, String formula) {
        super(player, buttons, size, title, parent);
        this.formula = formula;
        updateButtons();
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public double getZoom() {
        return zoom;
    }

    public String getFormula() {
        return this.formula;
    }

    @Override
    public void updateButtons() {
        List<Button> buttons = new ArrayList<>();
        List<Coordinate> coordinates = new ArrayList<>();
        for (int i = -100; i <= 100; i++) {
            double d = ((double)i/(double)10);
            double x = d*getZoom();
            try {
                double y = new ExpressionBuilder(getFormula())
                        .withVariable("x", d)
                        .withVariable("y", 0).build().calculate();
                //double y = Math.round(Math.cos(x));
                coordinates.add(new Coordinate(x, y));
            } catch (Exception e) {

            }
        }
        for (Coordinate coordinate : coordinates) {
            Coordinate newCoordinate = new Coordinate(Math.round(coordinate.getX()), Math.round(coordinate.getY()));
            int renderSlot = getRenderSlot(newCoordinate);
            if (renderSlot >= 0) {
                ItemCreator itemCreator = new ItemCreator(Material.WOOL);
                Button button = new Button(itemCreator.setName(coordinate.getX() + ", " + coordinate.getY()), renderSlot);
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
        Button zi = new Button(new ItemCreator(Material.ARROW)
                .setName(ChatColor.GREEN + "Zoom In"), 44) {
            @Override
            public void function(Player player) {
                setZoom(getZoom() + .1);
                updateButtons();
            }
        };
        buttons.add(zi);
        Button zo = new Button(new ItemCreator(Material.ARROW)
                .setName(ChatColor.GREEN + "Zoom Out"), 53) {
            @Override
            public void function(Player player) {
                setZoom(getZoom() >= 0 ? getZoom() - .1 : getZoom());
                updateButtons();
            }
        };
        buttons.add(zo);
        Button compass = new Button(new ItemCreator(Material.COMPASS)
                .setName(ChatColor.GREEN + "Use these to move!"), 16);
        buttons.add(compass);
        setButtons(buttons);
        updateInventory();
    }
}
