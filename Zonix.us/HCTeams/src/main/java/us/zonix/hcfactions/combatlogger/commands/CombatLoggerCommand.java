package us.zonix.hcfactions.combatlogger.commands;

import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.profile.cooldown.ProfileCooldown;
import us.zonix.hcfactions.profile.cooldown.ProfileCooldownType;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.cooldown.ProfileCooldownType;
import us.zonix.hcfactions.util.command.CommandArgs;

public class CombatLoggerCommand extends PluginCommand {
    @Command(name = "logout", aliases = "combatlog")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.getCooldownByType(ProfileCooldownType.LOGOUT) != null) {
            player.sendMessage(langFile.getString("COMBAT_LOGGER.LOGOUT_ALREADY"));
            return;
        }

        player.sendMessage(langFile.getString("COMBAT_LOGGER.LOGOUT").replace("%TIME%", main.getMainConfig().getInt("COMBAT_LOGGER.LOGOUT_TIME") + ""));
        profile.getCooldowns().add(new ProfileCooldown(ProfileCooldownType.LOGOUT, main.getMainConfig().getInt("COMBAT_LOGGER.LOGOUT_TIME")));
        profile.setLogoutLocation(player.getLocation());
    }
}
