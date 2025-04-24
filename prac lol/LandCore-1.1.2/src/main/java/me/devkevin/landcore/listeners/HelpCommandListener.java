package me.devkevin.landcore.listeners;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.utils.message.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 17/01/2023 @ 12:49
 * HelpCommandListener / land.pvp.core.listeners / LandCore
 */
@RequiredArgsConstructor
public class HelpCommandListener implements Listener {
    private final LandCore plugin;

    // Commands we don't want players to be able to run(and or, display the help message for).
    private static final String[] DISALLOWED_COMMANDS = {
            "?",
            "help",
            "version",
            "ver",
            "bukkit"
    };

    @EventHandler
    public void onHelpCommand(PlayerCommandPreprocessEvent event) {
        CoreProfile coreProfile = plugin.getProfileManager().getProfile(event.getPlayer().getUniqueId());

        boolean staff = coreProfile.hasStaff();

        for (String command : DISALLOWED_COMMANDS) {
            if (event.getMessage().startsWith("/" + command) && !staff) {
                event.getPlayer().sendMessage(CC.RED + "If you need help, require assistance by using /helpop.");
            }
        }
    }
}
