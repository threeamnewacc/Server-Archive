package us.zonix.hcfactions.profile.protection.command.subcommand;

import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.protection.life.ProfileProtectionLifeType;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProfileProtectionReviveCommand extends PluginCommand {
    @Command(name = "pvp.revive", aliases = {"pvp.revive", "revive"}, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length == 0) {
            sender.sendMessage(langFile.getString("PVP_PROTECTION.COMMAND.REVIVE.USAGE"));
            return;
        }

        Profile profile;
        Player player = Bukkit.getPlayer(StringUtils.join(args));
        if (player != null) {
            profile = Profile.getByPlayer(player);
        } else {
            profile = Profile.getByName(StringUtils.join(args));
        }

        if (profile == null) {
            sender.sendMessage(ChatColor.RED + "No player with name '" + StringUtils.join(args) + "' found.");
            return;
        }

        if (sender instanceof Player && !us.zonix.core.profile.Profile.getByUuid(((Player) sender).getUniqueId()).getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
            Profile senderProfile = Profile.getByPlayer((Player) sender);
            if (senderProfile.getLives().get(ProfileProtectionLifeType.FRIEND) <= 0) {
                sender.sendMessage(langFile.getString("PVP_PROTECTION.COMMAND.REVIVE.NOT_ENOUGH_LIVES").replace("%PLAYER%", profile.getName()));
                return;
            }
        }

        if (profile.getDeathban() == null) {
            sender.sendMessage(langFile.getString("PVP_PROTECTION.COMMAND.REVIVE.NOT_DEATHBANNED").replace("%PLAYER%", profile.getName()));
            return;
        }

        if (sender instanceof Player && !us.zonix.core.profile.Profile.getByUuid(profile.getUuid()).getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
            Profile senderProfile = Profile.getByPlayer((Player) sender);
            senderProfile.getLives().put(ProfileProtectionLifeType.FRIEND, senderProfile.getLives().get(ProfileProtectionLifeType.FRIEND) - 1);
        }

        profile.setDeathban(null);
        sender.sendMessage(langFile.getString("PVP_PROTECTION.COMMAND.REVIVE.REVIVED").replace("%PLAYER%", profile.getName()));

        if (Bukkit.getPlayer(profile.getUuid()) == null) {

            new BukkitRunnable() {

                @Override
                public void run() {
                    profile.save();
                    Profile.getProfilesMap().remove(profile.getUuid());
                }
            }.runTaskAsynchronously(this.main);

        }
    }
}
