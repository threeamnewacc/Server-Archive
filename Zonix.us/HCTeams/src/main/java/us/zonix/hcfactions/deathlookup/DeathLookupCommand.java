package us.zonix.hcfactions.deathlookup;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.command.CommandArgs;

import static com.mongodb.client.model.Filters.eq;

/*
    This is in beta, not going to be making it configurable for quite some time.
 */
public class DeathLookupCommand extends PluginCommand {

    @Command(name = "deathlookup", aliases = {"lookupdeath", "dl"}, permission = Rank.SENIOR_MODERATOR)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /" + command.getLabel() + " <target>");
            return;
        }

        Profile toLookupProfile;
        Player toLookup = Bukkit.getPlayer(StringUtils.join(args));
        if (toLookup != null) {
            toLookupProfile = Profile.getByPlayer(toLookup);
        } else {
            toLookupProfile = Profile.getByName(StringUtils.join(args));
        }

        if (toLookupProfile == null) {
            player.sendMessage(ChatColor.RED + "No player with name '" + StringUtils.join(args) + "' found.");
            return;
        }

        profile.setDeathLookup(new DeathLookup(toLookupProfile));
        player.openInventory(profile.getDeathLookup().getDeathInventory(1));
    }
}
