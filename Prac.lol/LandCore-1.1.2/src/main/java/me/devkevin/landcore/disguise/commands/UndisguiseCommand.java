package me.devkevin.landcore.disguise.commands;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.managers.PlayerManager;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.haoshoku.nick.NickPlugin;
import xyz.haoshoku.nick.api.NickAPI;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 10/02/2023 @ 0:23
 * UndisguiseCommand / me.devkevin.landcore.disguise / LandCore
 */
public class UndisguiseCommand extends BaseCommand {
    private final LandCore plugin;

    public UndisguiseCommand(LandCore plugin) {
        super("undisguise", Rank.TRIAL_MOD); // temp rank for test
        this.plugin = plugin;
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        CoreProfile coreProfile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (NickAPI.isNicked(player)) {
            NickAPI.resetNick(player);
            NickAPI.resetSkin(player);
            NickAPI.resetGameProfileName(player);
            NickAPI.resetUniqueId(player);
            NickAPI.refreshPlayer(player);
            NickAPI.refreshPlayerSync(player);

            coreProfile.setDisguiseRank(null); // when player un disguise we reset the rank

            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());

            player.sendMessage(CC.SECONDARY + "You've been " + CC.RED + "undisguised" + CC.SECONDARY + " and reset to your default skin.");
        } else {
            player.sendMessage(CC.RED + "You aren't currently disguised!");
        }
    }
}
