package me.devkevin.landcore.commands.impl.toggle;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

public class ToggleMessagesCommand extends PlayerCommand {
    private final LandCore plugin;

    public ToggleMessagesCommand(LandCore plugin) {
        super("togglemessages");
        this.plugin = plugin;
        setAliases("tpm");
    }

    @Override
    public void execute(Player player, String[] args) {
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        boolean messaging = !profile.isMessaging();

        profile.setMessaging(messaging);
        player.sendMessage(messaging ? CC.GREEN + "Messages enabled." : CC.RED + "Messages disabled.");
    }
}
