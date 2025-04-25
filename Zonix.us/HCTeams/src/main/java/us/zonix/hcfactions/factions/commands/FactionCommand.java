package us.zonix.hcfactions.factions.commands;

import us.zonix.hcfactions.files.ConfigFile;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.files.ConfigFile;

public class FactionCommand {

    public FactionsPlugin main = FactionsPlugin.getInstance();
    public ConfigFile langConfig = main.getLanguageConfig();
    public ConfigFile mainConfig = main.getMainConfig();

    public FactionCommand() {
        main.getFramework().registerCommands(this);
    }

}
