package me.devkevin.landcore.player.rank.commands;

import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.player.rank.menu.RankListMenu;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/01/2023 @ 1:43
 * RankListCommand / me.devkevin.landcore.player.rank.commands / LandCore
 */
public class RankListCommand extends PlayerCommand {
    private CoreProfile coreProfile;

    public RankListCommand() {
        super("ranklist", Rank.MANAGER);
        setAliases("ranks");
    }

    @Override
    public void execute(Player player, String[] args) {
        new RankListMenu(coreProfile).openMenu(player);
    }
}
