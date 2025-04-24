package me.devkevin.landcore.commands.impl;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 24/03/2023 @ 20:43
 * StoreCommand / me.devkevin.landcore.commands.impl / LandCore
 */
public class StoreCommand extends PlayerCommand {
    private final LandCore plugin;

    public StoreCommand(LandCore plugin) {
        super("store");
        this.plugin = plugin;
        setAliases("pstore");
    }

    @Override
    public void execute(Player player, String[] args) {
        player.openInventory(this.plugin.getStorePCoinMenu().menu(player).getCurrentPage());
    }
}
