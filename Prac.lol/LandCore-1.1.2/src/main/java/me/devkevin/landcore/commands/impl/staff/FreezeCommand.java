package me.devkevin.landcore.commands.impl.staff;

import com.google.common.collect.Maps;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.event.player.PlayerFreezeEvent;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class FreezeCommand extends BaseCommand {
    private final LandCore plugin;

    public FreezeCommand(LandCore plugin) {
        super("freeze", Rank.SENIOR_MOD);
        this.plugin = plugin;
        setAliases("screenshare", "ss");
        setUsage("/freeze <player>");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
       if (args.length < 1) {
           sender.sendMessage(CC.RED + getUsage());
           return;
       }

       Player target = Bukkit.getPlayer(args[0]);

       if (target != null) {

           CoreProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());

           if (targetProfile.hasStaff()) {
               sender.sendMessage(CC.RED + "If you believe a staff member is cheating, please alert higher staff.");
               return;
           }

           String server = plugin.getServerName();
           String targetName = target.getName();
           String senderName =  sender.getName();

           Map<String, Object> map = Maps.newHashMap();
           map.put("server", server);
           map.put("frozen", targetName);
           map.put("sender", senderName);

           if (targetProfile.freeze(sender)) {
               map.put("isFrozen", true);
               plugin.getRedisMessenger().send("freeze-listener", map);
               return;
           }
           map.put("isFrozen", false);
           plugin.getRedisMessenger().send("freeze-listener", map);
           return;
       }

       PlayerFreezeEvent event = new PlayerFreezeEvent(target);
       this.plugin.getServer().getPluginManager().callEvent(event);

       sender.sendMessage(CC.RED + "That player is offline or does not exist.");
    }
}
