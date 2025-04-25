package us.zonix.hcfactions.factions.commands.admin;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.factions.commands.FactionCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.factions.Faction;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.util.command.CommandArgs;
import org.bukkit.command.CommandSender;
import us.zonix.hcfactions.factions.commands.FactionCommand;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.util.command.CommandArgs;

public class FactionThawCommand extends FactionCommand {

    @Command(name = "f.thaw", aliases = {"faction.thaw", "factions.thaw"}, permission = Rank.ADMINISTRATOR, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();

        if (command.getArgs().length >= 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < command.getArgs().length; i++) {
                sb.append(command.getArgs()[i]).append(" ");
            }
            String name = sb.toString().trim().replace(" ", "");

            Faction faction = Faction.getByName(name);
            PlayerFaction playerFaction = null;

            if (faction instanceof PlayerFaction) {
                playerFaction = (PlayerFaction) faction;
            }

            if (faction == null || (!(faction instanceof PlayerFaction))) {
                playerFaction = PlayerFaction.getByPlayerName(name);

                if (playerFaction == null) {
                    sender.sendMessage(langConfig.getString("ERROR.NO_FACTIONS_FOUND").replace("%NAME%", name));
                    return;
                }
            }

            playerFaction.setFreezeInformation(null);
            sender.sendMessage(langConfig.getString("ADMIN.THAWED").replaceAll("%FACTION%", playerFaction.getName()));
        } else {
            sender.sendMessage(langConfig.getString("TOO_FEW_ARGS.THAW"));
        }
    }
}
