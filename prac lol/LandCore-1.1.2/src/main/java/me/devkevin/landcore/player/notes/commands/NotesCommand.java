package me.devkevin.landcore.player.notes.commands;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.notes.menu.NotesMenu;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 23/01/2023 @ 3:00
 * NotesCommand / me.devkevin.landcore.player.notes.commands / LandCore
 */
public class NotesCommand extends PlayerCommand {
    private final LandCore plugin;

    public NotesCommand(LandCore plugin) {
        super("notes", Rank.TRIAL_MOD);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Player target = plugin.getServer().getPlayer(args[0]);

        CoreProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());

        if (targetProfile.getNotes().isEmpty()) {
            player.sendMessage(targetProfile.getGrant().getRank().getColor() + targetProfile.getName() + CC.RED + " has no notes.");
            return;
        }

        new NotesMenu(targetProfile).openMenu(player);
    }
}
