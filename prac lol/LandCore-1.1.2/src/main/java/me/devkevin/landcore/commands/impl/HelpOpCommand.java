package me.devkevin.landcore.commands.impl;

import com.google.common.collect.Maps;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.utils.StringUtil;
import me.devkevin.landcore.utils.message.CC;
import me.devkevin.landcore.utils.timer.Timer;
import org.bukkit.entity.Player;

import java.util.Map;

public class HelpOpCommand extends PlayerCommand {
    private final LandCore plugin;

    public HelpOpCommand(LandCore plugin) {
        super("helpop");
        this.plugin = plugin;
        setUsage(CC.RED + "/helpop <help message>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        Timer cooldownTimer = profile.getReportCooldownTimer();

        if (cooldownTimer.isActive()) {
            player.sendMessage(CC.RED + "You can't request assistance for another " + cooldownTimer.formattedExpiration() + ".");
            return;
        }

        String request = StringUtil.buildString(args, 0);

        Map<String, Object> requestMap = Maps.newHashMap();
        requestMap.put("server", plugin.getServerName());
        requestMap.put("player", player.getName());
        requestMap.put("request", request);

        plugin.getRedisMessenger().send("help-op", requestMap);

        player.sendMessage(CC.GREEN + "Request sent: " + CC.R + request);
    }
}
