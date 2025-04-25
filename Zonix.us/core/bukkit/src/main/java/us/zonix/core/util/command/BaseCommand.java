package us.zonix.core.util.command;

import us.zonix.core.CorePlugin;
import us.zonix.core.util.file.ConfigFile;

public abstract class BaseCommand {

    public CorePlugin main = CorePlugin.getInstance();
    public ConfigFile configFile = main.getConfigFile();

    public BaseCommand() {
        main.getCommandFramework().registerCommands(this);
    }

    public abstract void onCommand(CommandArgs command);

}
