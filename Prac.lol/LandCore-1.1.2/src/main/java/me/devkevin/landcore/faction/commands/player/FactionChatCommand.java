package me.devkevin.landcore.faction.commands.player;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.faction.profile.FactionProfile;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 22/03/2023 @ 12:42
 * FactionChatCommand / me.devkevin.landcore.faction.commands.player / LandCore
 */
public class FactionChatCommand extends PlayerCommand {
    private final LandCore plugin;

    public FactionChatCommand(LandCore plugin) {
        super("factionchat");
        this.plugin = plugin;
        setAliases("fc");
    }

    @Override
    public void execute(Player player, String[] args) {
        FactionProfile factionProfile = this.plugin.getFactionManager().getProfile(player);

        boolean inFactionChat = !factionProfile.getFaction().isFactionChat();

        factionProfile.getFaction().setFactionChat(inFactionChat);

        player.sendMessage(inFactionChat ? CC.GREEN + "You are now in faction chat." : CC.RED + "You are no longer in faction chat.");
    }
}
