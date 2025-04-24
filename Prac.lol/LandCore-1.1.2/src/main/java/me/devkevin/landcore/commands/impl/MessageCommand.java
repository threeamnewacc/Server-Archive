package me.devkevin.landcore.commands.impl;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.event.player.PlayerMessageEvent;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.utils.StringUtil;
import me.devkevin.landcore.utils.message.CC;
import me.devkevin.landcore.utils.message.Messages;
import org.bukkit.entity.Player;

public class MessageCommand extends PlayerCommand {
    private final LandCore plugin;

    public MessageCommand(LandCore plugin) {
        super("message");
        this.plugin = plugin;
        setAliases("msg", "m", "whisper", "w", "tell", "t");
        setUsage(CC.RED + "Usage: /message <player> <message>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(usageMessage);
            return;
        }

        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (profile.getActiveMute() != null) {
            if (profile.getActiveMute().isActive()) {
                player.sendMessage(CC.RED + "You're muted for another " + profile.getActiveBan().getTimeRemaining() + ".");
            } else if (profile.getActiveMute().isPermanent()) {
                player.sendMessage(CC.RED + "You're permanently muted.");
            }
            return;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Messages.PLAYER_NOT_FOUND);
            return;
        }

        CoreProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());

        if (targetProfile.hasPlayerIgnored(player.getUniqueId())) {
            player.sendMessage(CC.RED + "That player is ignoring you!");
            return;
        }

        plugin.getServer().getPluginManager().callEvent(new PlayerMessageEvent(player, target, StringUtil.buildString(args, 1)));
    }
}
