package me.devkevin.landcore.commands.impl;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 07/02/2023 @ 18:07
 * FeedCommand / me.devkevin.landcore.commands.impl / LandCore
 */
public class FeedCommand extends PlayerCommand {
    private final LandCore plugin;

    public FeedCommand(LandCore plugin) {
        super("feed", Rank.ADMIN);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        player.sendMessage(CC.GOLD + CC.B + "You now have full hunger");
        player.setFoodLevel(20);
    }
}
