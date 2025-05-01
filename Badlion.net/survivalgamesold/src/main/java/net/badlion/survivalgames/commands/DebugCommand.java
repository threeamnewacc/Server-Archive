package net.badlion.survivalgames.commands;

import net.badlion.survivalgames.SGPlayer;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.managers.SGPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebugCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(p.getUniqueId());
            Bukkit.getLogger().info(p.getName() + " " + sgPlayer.getState().name());
        }

        Bukkit.getLogger().info("=====");

        for (SGPlayer sgPlayer : SGPlayerManager.getPlayersByState(SGPlayer.State.ALIVE)) {
            Bukkit.getLogger().info(sgPlayer.getUuid() + " " + sgPlayer.getState().name());
        }

        for (SGPlayer sgPlayer : SGPlayerManager.getPlayersByState(SGPlayer.State.SPECTATOR)) {
            Bukkit.getLogger().info(sgPlayer.getUuid() + " " + sgPlayer.getState().name());
        }

        Bukkit.getLogger().info(SurvivalGames.getInstance().getState().name());

        return true;
    }

}
