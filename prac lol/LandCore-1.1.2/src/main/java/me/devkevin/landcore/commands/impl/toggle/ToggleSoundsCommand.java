package me.devkevin.landcore.commands.impl.toggle;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

public class ToggleSoundsCommand extends PlayerCommand {
    private final LandCore plugin;

    public ToggleSoundsCommand(LandCore plugin) {
        super("togglesounds");
        this.plugin = plugin;
        setAliases("sounds", "ts");
    }

    @Override
    public void execute(Player player, String[] args) {
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        boolean playingSounds = !profile.isPlayingSounds();

        profile.setPlayingSounds(playingSounds);
        player.sendMessage(playingSounds ? CC.GREEN + "Sounds enabled." : CC.RED + "Sounds disabled.");
    }
}
