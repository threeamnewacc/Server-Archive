package me.devkevin.landcore.player.tags;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 06/02/2023 @ 4:09
 * PrefixCommand / me.devkevin.landcore.player.tags / LandCore
 */
public class PrefixCommand extends PlayerCommand {
    private final LandCore plugin;

    public PrefixCommand(LandCore plugin) {
        super("prefix");
        this.plugin = plugin;
        setAliases("symbols", "tags");
    }

    @Override
    public void execute(Player player, String[] args) {
        player.openInventory(this.plugin.getPrefixMenu().getMenu(player).getCurrentPage());
    }
}
