package me.devkevin.landcore.commands.impl.toggle;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

public class ToggleGlobalChat extends PlayerCommand {
    private final LandCore plugin;

    public ToggleGlobalChat(LandCore plugin) {
        super("toggleglobalchat");
        this.plugin = plugin;
        setAliases("togglechat", "tgc");
    }

    @Override
    public void execute(Player player, String[] args) {
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        boolean enabled = !profile.isGlobalChatEnabled();

        profile.setGlobalChatEnabled(enabled);
        player.sendMessage(enabled ? CC.GREEN + "Global chat enabled." : CC.RED + "Global chat disabled.");
    }
}
