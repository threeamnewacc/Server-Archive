package us.zonix.hcfactions.profile.protection.command.subcommand;

import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

public class ProfileProtectionEnableCommand extends PluginCommand {

    @Command(name = "pvp.enable", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length != 0 && !sender.isOp()) {
            sender.sendMessage(langFile.getString("PVP_PROTECTION.COMMAND.ENABLE.USAGE"));
            return;
        }

        Profile profile;

        if (args.length == 0) {
            if (sender instanceof Player) {
                profile = Profile.getByPlayer((Player) sender);
            }
            else {
                sender.sendMessage(ChatColor.RED + "You're console dumbass.");
                return;
            }
        } else {
            Player player = Bukkit.getPlayer(StringUtils.join(args));

            if (player != null) {
                profile = Profile.getByPlayer(player);
            }
            else {
                profile = Profile.getByName(StringUtils.join(args));
            }
        }

        if (profile == null) {
            sender.sendMessage(ChatColor.RED + "No player with name '" + StringUtils.join(args) + "' found.");
            return;
        }

        if (profile.getProtection() == null) {
            if (args.length == 0) {
                sender.sendMessage(langFile.getString("PVP_PROTECTION.COMMAND.ENABLE.NONE_SELF"));
            }
            else {
                sender.sendMessage(langFile.getString("PVP_PROTECTION.COMMAND.ENABLE.NONE_OTHER").replace("%PLAYER%", profile.getName()));
            }
        }
        else {
            if (args.length == 0) {
                sender.sendMessage(langFile.getString("PVP_PROTECTION.COMMAND.ENABLE.SELF").replace("%TIME%", profile.getProtection().getTimeLeft()));
            }
            else {
                sender.sendMessage(langFile.getString("PVP_PROTECTION.COMMAND.ENABLE.OTHER").replace("%PLAYER%", profile.getName()).replace("%TIME%", profile.getProtection().getTimeLeft()));
            }

            profile.setProtection(null);

            if (Bukkit.getPlayer(profile.getUuid()) == null) {

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        profile.save();
                    }
                }.runTaskAsynchronously(this.main);

            }
        }

        if (Bukkit.getPlayer(profile.getUuid()) == null) {
            Profile.getProfilesMap().remove(profile.getUuid());
        }
    }

}
