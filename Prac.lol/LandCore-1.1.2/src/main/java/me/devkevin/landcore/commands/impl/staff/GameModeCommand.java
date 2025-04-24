package me.devkevin.landcore.commands.impl.staff;

import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GameModeCommand extends PlayerCommand {
    public GameModeCommand() {
        super("gamemode", Rank.SENIOR_MOD);
        setAliases("gm");
        setUsage(CC.RED + "Usage: /gamemode <mode|id>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "1":
            case "creative":
            case "c":
                player.setGameMode(GameMode.CREATIVE);
                player.sendMessage(CC.GREEN + "Your game mode was set to creative.");
                break;
            case "0":
            case "survival":
            case "s":
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(CC.GREEN + "Your game mode was set to survival.");
                break;
            case "2":
            case "adventure":
            case "a":
                player.setGameMode(GameMode.ADVENTURE);
                player.sendMessage(CC.GREEN + "Your game mode was set to adventure.");
                break;
            default:
                player.sendMessage(usageMessage);
                break;
        }
    }
}
