package me.devkevin.landcore.player.color;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 06/02/2023 @ 4:38
 * SetColorCommand / me.devkevin.landcore.player.color / LandCore
 */
public class SetColorCommand extends PlayerCommand {
    private final LandCore plugin;

    public SetColorCommand(LandCore plugin) {
        super("color");
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        player.openInventory(this.plugin.getColorMenu().getMenu(player).getCurrentPage());
    }
}
