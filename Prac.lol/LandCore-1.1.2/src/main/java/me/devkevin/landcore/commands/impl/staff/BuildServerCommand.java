package me.devkevin.landcore.commands.impl.staff;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.LandCoreAPI;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.rank.Rank;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 07/02/2023 @ 17:46
 * BuilServerCommand / me.devkevin.landcore.commands.impl / LandCore
 */
public class BuildServerCommand extends PlayerCommand {
    private final LandCore plugin;

    public BuildServerCommand(LandCore plugin) {
        super("buildserver", Rank.BUILDER);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        LandCoreAPI.sendToServer(player, "build");
    }
}
