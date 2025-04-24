package me.devkevin.landcore.commands.impl.staff;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

public class MuteChatCommand extends PlayerCommand {
    private final LandCore plugin;

    public MuteChatCommand(LandCore plugin) {
        super("mutechat", Rank.TRIAL_MOD);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        boolean globalChatMuted = !plugin.getServerSettings().isGlobalChatMuted();

        plugin.getServerSettings().setGlobalChatMuted(globalChatMuted);
        plugin.getServer().broadcastMessage(globalChatMuted ? CC.RED + "Global chat has been muted by " + player.getName() + "."
                : CC.GREEN + "Global chat has been enabled by " + plugin.getProfileManager().getProfile(player.getUniqueId()).getRank().getColor() + player.getName() + ".");
    }
}
