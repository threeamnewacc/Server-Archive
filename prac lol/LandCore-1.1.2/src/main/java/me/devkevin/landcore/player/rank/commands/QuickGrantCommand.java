package me.devkevin.landcore.player.rank.commands;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.grant.menus.QuickGrantMenu;
import me.devkevin.landcore.player.rank.Rank;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 20/01/2023 @ 13:22
 * QuickGrantCommand / land.pvp.core.player.rank.commands / LandCore
 */
public class QuickGrantCommand extends PlayerCommand {
    private final LandCore plugin;

    public QuickGrantCommand(LandCore plugin) {
        super("qgrant", Rank.MANAGER);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        new QuickGrantMenu().openMenu(player);
    }
}
