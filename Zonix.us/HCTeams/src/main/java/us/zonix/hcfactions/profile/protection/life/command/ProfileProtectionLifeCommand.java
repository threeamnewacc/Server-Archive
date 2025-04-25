package us.zonix.hcfactions.profile.protection.life.command;

import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.protection.life.ProfileProtectionLifeType;
import us.zonix.hcfactions.profile.protection.life.command.subcommand.ProfileProtectionLifeAddCommand;
import us.zonix.hcfactions.profile.protection.life.command.subcommand.ProfileProtectionLifeRemoveCommand;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.profile.protection.life.command.subcommand.ProfileProtectionLifeSetCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.protection.life.command.subcommand.ProfileProtectionLifeAddCommand;
import us.zonix.hcfactions.profile.protection.life.command.subcommand.ProfileProtectionLifeRemoveCommand;
import us.zonix.hcfactions.util.command.CommandArgs;

public class ProfileProtectionLifeCommand extends PluginCommand {

    public ProfileProtectionLifeCommand() {
        new ProfileProtectionLifeAddCommand();
        new ProfileProtectionLifeRemoveCommand();
        new ProfileProtectionLifeSetCommand();
    }

    @Command(name = "lives", aliases = {"lifes"}, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();

        Profile profile;
        if (sender instanceof Player) {
            profile = Profile.getByPlayer((Player) sender);
        } else {
            sender.sendMessage(ChatColor.RED + "You're console dumbass.");
            return;
        }

        for (String message : langFile.getStringList("LIVES.COMMAND.VIEW")) {
            message = message.replace("%PLAYER%", profile.getName());

            for (ProfileProtectionLifeType type : ProfileProtectionLifeType.values()) {
                message = message.replace("%" + type.name() + "_LIVES%", profile.getLives().get(type) + "");
            }

            sender.sendMessage(message);
        }

        if (Bukkit.getPlayer(profile.getUuid()) == null) {
            Profile.getProfilesMap().remove(profile.getUuid());
        }
    }
}
