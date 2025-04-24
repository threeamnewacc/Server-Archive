package me.devkevin.landcore.player.notes.commands;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 23/01/2023 @ 3:02
 * NoteRemoveCommand / me.devkevin.landcore.player.notes.commands / LandCore
 */
public class NoteRemoveCommand extends PlayerCommand {
    private final LandCore plugin;

    public NoteRemoveCommand(LandCore plugin) {
        super("note.remove", Rank.TRIAL_MOD);
        this.plugin = plugin;
        setUsage(CC.RED + "Usage: /note.remove <id>");
    }

    @Override
    public void execute(Player player, String[] args) {
        Player target = plugin.getServer().getPlayer(args[0]);

        CoreProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());

        Number noteID = Integer.parseInt(args[1]);

        if (!targetProfile.removeNote(noteID.intValue())) {
            player.sendMessage(CC.translate("&7That note doesn't exist."));
            return;
        }

        player.sendMessage(CC.RED + "You've deleted Note #" + noteID + " of " + targetProfile.getGrant().getRank().getColor() + targetProfile.getName());
    }
}
