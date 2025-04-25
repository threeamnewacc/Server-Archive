package us.zonix.hcfactions.factions.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.factions.Faction;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.commands.FactionCommand;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.commands.FactionCommand;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

import java.util.HashSet;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;

public class FactionDisbandAllCommand extends FactionCommand {

    @Command(name = "f.disbandall", permission = Rank.OWNER, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();

        if (command.getArgs().length == 0 && sender instanceof ConsoleCommandSender) {
            for (Faction faction : Faction.getFactions()) {
                if (faction instanceof PlayerFaction) {
                    Set<Claim> claims = new HashSet<>(faction.getClaims());

                    for (Claim claim : claims) {
                        claim.remove();
                    }

                    Bukkit.getScheduler().runTaskAsynchronously(FactionsPlugin.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            main.getFactionsDatabase().getDatabase().getCollection("playerFactions").deleteOne(eq("uuid", faction.getUuid().toString()));
                        }
                    });

                    Faction.getFactions().remove(faction);
                }
            }

            sender.sendMessage(langConfig.getString("ADMIN.DISBAND_ALL"));
        }
    }
}
