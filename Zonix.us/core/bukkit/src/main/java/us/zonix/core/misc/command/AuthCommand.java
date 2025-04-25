package us.zonix.core.misc.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.CorePlugin;
import us.zonix.core.api.request.PlayerRequest;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.auth.TimeBasedOneTimePasswordUtil;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class AuthCommand extends BaseCommand {

    @Command(name = "auth", aliases = "2fa", rank = Rank.TRIAL_MOD, description = "Authenticate", requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if(args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /auth <token>");
        } else if(args.length == 1) {

            Profile profile = Profile.getByUuid(player.getUniqueId());

            if(profile == null) {
                return;
            }

            if(profile.isAuthenticated()) {
                player.sendMessage(ChatColor.RED + "You are already authenticated.");
                return;
            }

            if(profile.getTwoFactorAuthentication() == null) {
                player.sendMessage(ChatColor.RED + "Error occurred! Contact @DevEmilio in Telegram.");
                return;
            }

            String token = profile.getTwoFactorAuthentication();

            try {

                String actualToken = TimeBasedOneTimePasswordUtil.generateCurrentNumberString(token);

                if (!actualToken.trim().equalsIgnoreCase(args[0].trim())) {
                    player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "(✖) Invalid authentication token.");
                    return;
                }

                profile.setAuthenticated(true);
                profile.setIp(player.getAddress().getAddress().getHostAddress());
                CorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), profile::save);
                PlayerRequest.UpdateAuthenticationRequest request = new PlayerRequest.UpdateAuthenticationRequest(player.getUniqueId(), profile.getTwoFactorAuthentication(), profile.isAuthenticated());
                CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(request);

                player.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "(✔) Authentication has been successful.");
            } catch (Exception ex) {
                player.sendMessage(ChatColor.RED + "Error occurred! Contact @DevEmilio in Telegram.");
            }

        } else {
            player.sendMessage(ChatColor.RED + "Usage: /auth <token>");
        }
    }
}