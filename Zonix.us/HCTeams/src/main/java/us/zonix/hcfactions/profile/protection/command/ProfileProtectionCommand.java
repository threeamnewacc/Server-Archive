package us.zonix.hcfactions.profile.protection.command;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.profile.protection.command.subcommand.ProfileProtectionEnableCommand;
import us.zonix.hcfactions.profile.protection.command.subcommand.ProfileProtectionLivesCommand;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.profile.protection.command.subcommand.ProfileProtectionReviveCommand;
import us.zonix.hcfactions.profile.protection.command.subcommand.ProfileProtectionTimeCommand;
import us.zonix.hcfactions.profile.protection.life.command.ProfileProtectionLifeCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.profile.protection.command.subcommand.ProfileProtectionReviveCommand;

import java.util.List;

public class ProfileProtectionCommand extends PluginCommand {

    public static final List<String> HELP_MESSAGE = FactionsPlugin.getInstance().getLanguageConfig().getStringList("PVP_PROTECTION.COMMAND.HELP");

    public ProfileProtectionCommand() {
        new ProfileProtectionEnableCommand();
        new ProfileProtectionLivesCommand();
        new ProfileProtectionReviveCommand();
        new ProfileProtectionTimeCommand();
        new ProfileProtectionLifeCommand();
    }

    @Command(name = "pvp", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        command.getSender().sendMessage(getHelp());
    }

    public static String[] getHelp() {
        return HELP_MESSAGE.toArray(new String[HELP_MESSAGE.size()]);
    }
}
