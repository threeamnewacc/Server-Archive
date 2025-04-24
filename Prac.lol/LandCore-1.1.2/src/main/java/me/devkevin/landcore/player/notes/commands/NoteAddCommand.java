package me.devkevin.landcore.player.notes.commands;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.notes.Note;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 23/01/2023 @ 2:57
 * NoteAddCommand / me.devkevin.landcore.player.notes.commands / LandCore
 */
public class NoteAddCommand extends PlayerCommand {
    private final LandCore plugin;

    public NoteAddCommand(LandCore plugin) {
        super("note.add", Rank.TRIAL_MOD);
        this.plugin = plugin;
        setUsage(CC.RED + "Usage: /note.add <player> <note>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(usageMessage);
            return;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        CoreProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());

        int id = targetProfile.getNotes().size() + 1;
        targetProfile.getNotes().add(new Note(id, args[1], player));
        player.sendMessage(CC.GREEN + "You has been added a Note to " + targetProfile.getGrant().getRank().getColor() + targetProfile.getName());
    }
}
