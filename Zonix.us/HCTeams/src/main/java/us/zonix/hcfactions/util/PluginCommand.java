package us.zonix.hcfactions.util;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.files.ConfigFile;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.files.ConfigFile;
import us.zonix.hcfactions.util.command.CommandArgs;

public abstract class PluginCommand {

    public FactionsPlugin main = FactionsPlugin.getInstance();
    public ConfigFile configFile = main.getMainConfig();
    public ConfigFile langFile = main.getLanguageConfig();
    public ConfigFile scoreboardFile = main.getScoreboardConfig();

    public PluginCommand() {
        main.getFramework().registerCommands(this);
    }

    public abstract void onCommand(CommandArgs command);

}
