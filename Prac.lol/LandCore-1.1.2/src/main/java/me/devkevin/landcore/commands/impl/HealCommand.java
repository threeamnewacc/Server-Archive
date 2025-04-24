package me.devkevin.landcore.commands.impl;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 07/02/2023 @ 18:08
 * HealCommand / me.devkevin.landcore.commands.impl / LandCore
 */
public class HealCommand extends PlayerCommand {
    private final LandCore plugin;

    public HealCommand(LandCore plugin) {
        super("heal", Rank.ADMIN);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        player.sendMessage(CC.GOLD + CC.B + "You now have fully healed.");
        player.setHealth(player.getMaxHealth());
    }
}
