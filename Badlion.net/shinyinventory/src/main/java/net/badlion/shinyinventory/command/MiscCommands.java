package net.badlion.shinyinventory.command;


import net.badlion.shinyinventory.gui.buttons.Button;
import net.badlion.shinyinventory.gui.interfaces.render.BracketDisplayInterface;
import net.badlion.shinyinventory.gui.interfaces.render.ChestRenderInterface;
import net.badlion.shinyinventory.gui.interfaces.render.GraphingCalculatorInterface;
import net.badlion.shinyinventory.gui.interfaces.render.text.GridTextRenderInterface;
import net.badlion.shinyinventory.gui.interfaces.render.text.ScrollingTextRenderInterface;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by ShinyDialga45 on 3/21/15.
 */
public class MiscCommands {

    @Command(aliases = {"scommons", "sco"}, desc = "Debug scommons")
    public static void scommons(final CommandContext args, CommandSender sender) throws Exception {

    }

    @Command(aliases = {"render"}, desc = "Render stuff")
    public static void render(final CommandContext args, CommandSender sender) throws Exception {
        Player player = (Player) sender;
        player.openInventory(new ChestRenderInterface(player, null, 54, "Render", null).getInventory());
    }

    @Command(aliases = {"graph"}, desc = "Graph a math equation")
    public static void graph(final CommandContext args, CommandSender sender) throws Exception {
        String formula = args.getJoinedStrings(0);
        Player player = (Player) sender;
        player.openInventory(new GraphingCalculatorInterface(player, new ArrayList<Button>(), 54, "Graphing Calculator", null, formula).getInventory());
    }

    @Command(aliases = {"scrollingtext"}, desc = "Show some scrolling text")
    public static void scrollingtext(final CommandContext args, CommandSender sender) throws Exception {
        String text = args.getJoinedStrings(0);
        Player player = (Player) sender;
        player.openInventory(new ScrollingTextRenderInterface(player, null, 45, "Scrolling Text", null, text).getInventory());
    }

    @Command(aliases = {"bracket"}, desc = "Show some scrolling text")
    public static void bracket(final CommandContext args, CommandSender sender) throws Exception {
        Player player = (Player) sender;
        player.openInventory(new BracketDisplayInterface(player, null, 54, "Bracket", null).getInventory());
    }

    @Command(aliases = {"gridtext"}, desc = "Show some grid text")
    public static void gridtext(final CommandContext args, CommandSender sender) throws Exception {
        String text = args.getJoinedStrings(0);
        Player player = (Player) sender;
        GridTextRenderInterface gui = new GridTextRenderInterface(player, new ArrayList<Button>(), 54, "Grid Text", null, text);
        gui.updateButtons();
        player.openInventory(gui.getInventory());
    }

}
