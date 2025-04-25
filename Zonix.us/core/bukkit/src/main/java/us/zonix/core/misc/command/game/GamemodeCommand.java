package us.zonix.core.misc.command.game;

import org.bukkit.*;
import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

import java.util.Locale;

public class GamemodeCommand extends BaseCommand {

    @Command(name = "gamemode", aliases = {"gm", "gmc", "gma", "gms"}, rank = Rank.MANAGER, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1 && !command.getLabel().equalsIgnoreCase("gmc") && !command.getLabel().equalsIgnoreCase("gma") && !command.getLabel().equalsIgnoreCase("gms")) {
            player.sendMessage(ChatColor.RED + "Usage: /gamemode <mode> <player>");
            return;
        }

        GameMode mode;

        if (command.getLabel().equalsIgnoreCase("gmc")) {
            mode = GameMode.CREATIVE;
        }
        else if (command.getLabel().equalsIgnoreCase("gma")) {
            mode = GameMode.ADVENTURE;
        }
        else if (command.getLabel().equalsIgnoreCase("gms")) {
            mode = GameMode.SURVIVAL;
        }
        else {
            mode = this.getGameModeByName(args[0]);
        }

        Player target;

        if (args.length > 1) {
            if (player.hasPermission("player.gamemode.others")) {
                target = Bukkit.getPlayer(args[1]);
            }
            else {
                target = null;
            }
        }
        else {
            target = player;
        }

        if (target == null) {
            player.sendMessage(ChatColor.RED + "That player is not online.");
            return;
        }

        if (target.getGameMode() == mode) {
            player.sendMessage(ChatColor.RED + "Gamemode of " + target.getName() + " is already " + mode.name() + '.');
            return;
        }

        target.setGameMode(mode);
    }

    private GameMode getGameModeByName(String id) {
        id = id.toLowerCase(Locale.ENGLISH);

        if (id.equalsIgnoreCase("gmc") || id.contains("creat") || id.equalsIgnoreCase("1") || id.equalsIgnoreCase("c")) {
            return GameMode.CREATIVE;
        }

        if (id.equalsIgnoreCase("gms") || id.contains("survi") || id.equalsIgnoreCase("0") || id.equalsIgnoreCase("s")) {
            return GameMode.SURVIVAL;
        }

        if (id.equalsIgnoreCase("gma") || id.contains("advent") || id.equalsIgnoreCase("2") || id.equalsIgnoreCase("a")) {
            return GameMode.ADVENTURE;
        }

        if (id.equalsIgnoreCase("gmt") || id.contains("toggle") || id.contains("cycle") || id.equalsIgnoreCase("t")) {
            return null;
        }

        return null;
    }

}
